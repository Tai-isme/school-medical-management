import React, { useEffect, useState } from "react";
import {
  Button,
  Card,
  Row,
  Col,
  Tag,
  Modal,
  Descriptions,
  Form,
  Input,
  DatePicker,
  message,
  Tabs,
  Pagination,
} from "antd";
import {
  PlusOutlined,
  SearchOutlined,
  UploadOutlined,
  DownloadOutlined,
} from "@ant-design/icons";
import { Select } from "antd"; // Thêm dòng này
import axios from "axios";
import dayjs from "dayjs";
import Swal from "sweetalert2";
import { Table } from "antd"; // Thêm import này

const VaccineProgramList = () => {
  const [programs, setPrograms] = useState([]);
  const [detailVisible, setDetailVisible] = useState(false);
  const [createVisible, setCreateVisible] = useState(false);
  const [loading, setLoading] = useState(false);
  const [program, setProgram] = useState(null);
  const [searchTerm, setSearchTerm] = useState(""); // Tìm kiếm theo tên
  const [filterDate, setFilterDate] = useState(null); // Thêm state lọc ngày
  const [editMode, setEditMode] = useState(false);
  const [filterStatus, setFilterStatus] = useState(""); // Thêm state này
  const [resultVisible, setResultVisible] = useState(false);
  const [resultData, setResultData] = useState([]);
  const [resultLoading, setResultLoading] = useState(false);
  const [createResultVisible, setCreateResultVisible] = useState(false);
  const [createResultLoading, setCreateResultLoading] = useState(false);
  const [resultForm] = Form.useForm();
  const [activeTab, setActiveTab] = useState("program");
  const [currentPage, setCurrentPage] = useState(1);
  const [showResultPage, setShowResultPage] = useState(false);
  const [resultPageData, setResultPageData] = useState([]);
  const [resultPageLoading, setResultPageLoading] = useState(false);
  const [resultTablePage, setResultTablePage] = useState(1);
  const [resultTablePageSize, setResultTablePageSize] = useState(8); // Số dòng mỗi trang
  const pageSize = 3; // Số chương trình mỗi trang
  const userRole = localStorage.getItem("role"); // Lấy role từ localStorage

  // Thêm state để lưu danh sách vaccine
  const [vaccineList, setVaccineList] = useState([]);
  const [addVaccineVisible, setAddVaccineVisible] = useState(false);
  const [addVaccineLoading, setAddVaccineLoading] = useState(false);
  const [addVaccineForm] = Form.useForm();

  // Thêm state để lưu kết quả của nurse
  const [nurseResults, setNurseResults] = useState([]);
  const [nurseResultsLoading, setNurseResultsLoading] = useState(false);
  const [searchTermResult, setSearchTermResult] = useState(""); // Thêm state tìm kiếm kết quả
  const [selectedResult, setSelectedResult] = useState(null);
  const [selectedResultLoading, setSelectedResultLoading] = useState(false);
  const [selectedVaccineResult, setSelectedVaccineResult] = useState(null);
  const [selectedVaccineResultId, setSelectedVaccineResultId] = useState(null);
  const [selectedVaccineResultLoading, setSelectedVaccineResultLoading] =
    useState(false);
  const [sampleResultData, setSampleResultData] = useState(null); // Thêm state ở đầu component
  const [editableRows, setEditableRows] = useState([]); // Thêm state cho hàng có thể chỉnh sửa
  const [studentStats, setStudentStats] = useState({}); // { [programId]: { total, confirmed } }

  const [importVaccineVisible, setImportVaccineVisible] = useState(false);
  const [importLoading, setImportLoading] = useState(false);
  const [importFile, setImportFile] = useState(null);
  useEffect(() => {
    fetchProgram();
    // Lấy danh sách vaccine khi load trang
    fetchVaccineList();
    // Lấy kết quả của nurse khi load trang
    fetchNurseResults();
  }, []);

  const fetchProgram = async () => {
    const token = localStorage.getItem("token");
    try {
      const res = await axios.get(
        "http://localhost:8080/api/admin/vaccine-program",
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setPrograms(res.data);
    } catch (error) {
      setPrograms([]);
    }
  };

  const fetchVaccineList = async () => {
    const token = localStorage.getItem("token");
    try {
      const res = await axios.get(
        "http://localhost:8080/api/admin/get=all-VaccineName",
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setVaccineList(res.data);
    } catch {
      setVaccineList([]);
    }
  };

  const fetchNurseResults = async () => {
    setNurseResultsLoading(true);
    const token = localStorage.getItem("token");
    try {
      const res = await axios.get(
        "http://localhost:8080/api/nurse/vaccine-result",
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setNurseResults(res.data);
    } catch {
      setNurseResults([]);
    } finally {
      setNurseResultsLoading(false);
    }
  };

  const fetchStudentStats = async (programId) => {
    const token = localStorage.getItem("token");
    try {
      const res = await axios.get(
        `http://localhost:8080/api/nurse/vaccine-forms/program/${programId}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      const total = res.data.length;
      const confirmed = res.data.filter((item) => item.commit === true).length;
      setStudentStats((prev) => ({
        ...prev,
        [programId]: { total, confirmed },
      }));
    } catch {
      setStudentStats((prev) => ({
        ...prev,
        [programId]: { total: 0, confirmed: 0 },
      }));
    }
  };

  // Lọc danh sách theo tên chương trình và ngày tiêm
  const filteredPrograms = programs.filter((program) => {
    const matchName =
      typeof program.vaccineName === "object"
        ? program.vaccineName?.vaccineName
            ?.toLowerCase()
            .includes(searchTerm.toLowerCase())
        : typeof program.vaccineName === "string"
        ? program.vaccineName.toLowerCase().includes(searchTerm.toLowerCase())
        : false;
    const matchDate = filterDate
      ? dayjs(program.vaccineDate).isSame(filterDate, "day")
      : true;
    const matchStatus = filterStatus ? program.status === filterStatus : true;
    return matchName && matchDate && matchStatus;
  });

  // Gọi khi mount hoặc khi danh sách chương trình thay đổi
  useEffect(() => {
    filteredPrograms.forEach((program) => {
      if (!studentStats[program.vaccineId]) {
        fetchStudentStats(program.vaccineId);
      }
    });
    // eslint-disable-next-line
  }, [filteredPrograms]);

  const handleCreate = async (values) => {
    setLoading(true);
    const token = localStorage.getItem("token");
    try {
      await axios.post(
        "http://localhost:8080/api/admin/vaccine-program",
        {
          vaccineNameId: values.vaccineNameId,
          description: values.description,
          vaccineDate: values.vaccineDate.format("YYYY-MM-DD"),
          note: values.note,
        },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      message.success("Tạo chương trình tiêm chủng thành công!");
      setCreateVisible(false);
      fetchProgram();
    } catch (error) {
      message.error("Tạo chương trình tiêm chủng thất bại!");
    } finally {
      setLoading(false);
    }
  };

  const handleUpdate = async (values) => {
    setLoading(true);
    const token = localStorage.getItem("token");
    try {
      await axios.put(
        `http://localhost:8080/api/admin/vaccine-program/${program.vaccineId}`,
        {
          vaccineNameId: values.vaccineNameId,
          manufacture: values.manufacture,
          description: values.description,
          vaccineDate: values.vaccineDate.format("YYYY-MM-DD"),
          note: values.note,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      await Swal.fire({
        icon: "success",
        title: "Cập nhật chương trình thành công!",
        showConfirmButton: false,
        timer: 1500,
      });
      setCreateVisible(false);
      setEditMode(false);
      fetchProgram();
    } catch (error) {
      Swal.fire({
        icon: "error",
        title: "Cập nhật chương trình thất bại!",
        confirmButtonText: "OK",
      });
    } finally {
      setLoading(false);
    }
  };
  // filepath: f:\Ky_5_FPT\SWP\Frontend\school-medical-management\Frontend\my-

  const handleDelete = async (programId) => {
    const result = await Swal.fire({
      title: "Bạn có chắc muốn xóa chương trình này?",
      text: "Hành động này không thể hoàn tác!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      cancelButtonColor: "#3085d6",
      confirmButtonText: "Xóa",
      cancelButtonText: "Hủy",
    });

    if (result.isConfirmed) {
      const token = localStorage.getItem("token");
      try {
        await axios.delete(
          `http://localhost:8080/api/admin/vaccine-program/${programId}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        await Swal.fire(
          "Đã xóa!",
          "Chương trình đã được xóa thành công.",
          "success"
        );
        fetchProgram();
      } catch {
        Swal.fire("Lỗi", "Xóa thất bại!", "error");
      }
    }
  };

  const handleViewResult = async (programId) => {
    setActiveTab("result");
    setSelectedVaccineResultLoading(true);
    setSelectedVaccineResultId(programId);
    setSampleResultData(null); // Thêm dòng này để tắt chế độ chỉnh sửa
    try {
      const token = localStorage.getItem("token");
      const res = await axios.get(
        `http://localhost:8080/api/nurse/vaccine-result/program/${programId}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setSelectedVaccineResult(res.data); // API trả về mảng
    } catch (err) {
      setSelectedVaccineResult([]);
    } finally {
      setSelectedVaccineResultLoading(false);
    }
  };

  const handleUpdateStatus = async (vaccineId, status) => {
    const confirm = await Swal.fire({
      title: "Bạn có chắc muốn chuyển trạng thái?",
      text: "Thao tác này sẽ thay đổi trạng thái của chương trình tiêm chủng.",
      icon: "question",
      showCancelButton: true,
      confirmButtonText: "Chuyển trạng thái",
      cancelButtonText: "Hủy",
    });
    if (!confirm.isConfirmed) return;

    const token = localStorage.getItem("token");
    try {
      await axios.patch(
        `http://localhost:8080/api/admin/vaccine-program/${vaccineId}?status=${status}`,
        {},
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      message.success("Cập nhật trạng thái thành công!");
      fetchProgram();
    } catch (error) {
      message.error("Cập nhật trạng thái thất bại!");
    }
  };
  // ...existing code...

  const handleCreateResult = async (values) => {
    setCreateResultLoading(true);
    const token = localStorage.getItem("token");
    try {
      await axios.post(
        "http://localhost:8080/api/nurse/vaccine-result",
        {
          statusHealth: values.statusHealth,
          resultNote: values.resultNote,
          reaction: values.reaction,
          createdAt: new Date().toISOString(),
          vaccineFormId: program.vaccineId,
          // Thêm các trường khác nếu cần
        },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      message.success(
        `Tạo kết quả cho vaccine ID ${program.vaccineId} thành công!`
      );
      setCreateResultVisible(false);
      resultForm.resetFields();
    } catch (error) {
      message.error("Tạo kết quả thất bại!");
    } finally {
      setCreateResultLoading(false);
    }
  };

  const handleCreateProgramResult = async (program) => {
    const confirm = await Swal.fire({
      title: "Bạn có chắc muốn tạo kết quả?",
      text: "Sau khi tạo, bạn sẽ nhập kết quả tiêm chủng cho học sinh.",
      icon: "question",
      showCancelButton: true,
      confirmButtonText: "Tạo kết quả",
      cancelButtonText: "Hủy",
    });
    if (!confirm.isConfirmed) return;

    const token = localStorage.getItem("token");
    try {
      const res = await axios.post(
        `http://localhost:8080/api/nurse/create-vaccineResults-byProgram/${program.vaccineId}`,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setSampleResultData(res.data);
      setEditableRows(res.data.map((item) => ({ ...item })));
      setActiveTab("result");
      message.success("Tạo kết quả thành công!");
    } catch (error) {
      message.error("Tạo kết quả thất bại!");
    }
  };
  // ...existing code...

  const handleEditCell = (value, record, field) => {
    setEditableRows((prev) =>
      prev.map((row) =>
        row.vaccineResultId === record.vaccineResultId
          ? { ...row, [field]: value }
          : row
      )
    );
  };

  // Hàm lưu từng dòng
  const handleSaveRow = async (record) => {
    const token = localStorage.getItem("token");
    try {
      await axios.put(
        `http://localhost:8080/api/nurse/vaccine-result/${record.vaccineResultId}`,
        {
          statusHealth: record.statusHealth,
          resultNote: record.resultNote,
          reaction: record.reaction,
          vaccineFormId: record.vaccineFormDTO.id,
        },
        { headers: { Authorization: `Bearer ${token}` } }
      );

      setSampleResultData((prev) =>
        prev.map((row) =>
          row.vaccineResultId === record.vaccineResultId ? { ...record } : row
        )
      );

      Swal.fire({
        icon: "success",
        title: "Thành công",
        text: "Đã lưu kết quả!",
        timer: 2000,
        showConfirmButton: false,
      });
    } catch (error) {
      Swal.fire({
        icon: "error",
        title: "Lỗi",
        text: "Lưu kết quả thất bại!",
        confirmButtonText: "OK",
      });
    }
  };

  // Phân trang
  const pagedPrograms = filteredPrograms.slice(
    (currentPage - 1) * pageSize,
    currentPage * pageSize
  );

  // Reset trang về 1 khi filter/search thay đổi
  useEffect(() => {
    setCurrentPage(1);
  }, [searchTerm, filterDate, filterStatus]);

  // Thêm hàm lấy màu theo trạng thái
  const getStatusColor = (status) => {
    switch (status) {
      case "NOT_STARTED":
        return "default";
      case "ON_GOING":
        return "blue";
      case "COMPLETED":
        return "green";
      default:
        return "default";
    }
  };

  // Hàm ánh xạ trạng thái sang tiếng Việt
  const getStatusText = (status) => {
    switch (status) {
      case "NOT_STARTED":
        return "Chưa bắt đầu";
      case "ON_GOING":
        return "Đang diễn ra";
      case "COMPLETED":
        return "Đã hoàn thành";
      default:
        return status;
    }
  };

  const handleShowResultPage = async () => {
    setResultPageLoading(true);
    setShowResultPage(true);
    await fetchNurseResults();
    setResultPageLoading(false);
  };

  const pagedNurseResults = nurseResults.slice(
    (resultTablePage - 1) * resultTablePageSize,
    resultTablePage * resultTablePageSize
  );

  // Thêm biến lọc kết quả nurse theo tên chương trình:
  const filteredNurseResults = nurseResults.filter((item) => {
    const studentName = item?.studentDTO?.fullName || "";
    return studentName.toLowerCase().includes(searchTermResult.toLowerCase());
  });

  const handleNotifyVaccine = async (formId) => {
    const token = localStorage.getItem("token");
    try {
      await axios.post(
        "http://localhost:8080/api/notify-vaccine",
        { formIds: [formId] },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      message.success("Gửi thông báo thành công!");
    } catch (error) {
      message.error("Gửi thông báo thất bại!");
    }
  };

  const handleEditResult = async (programId) => {
    setSelectedVaccineResultLoading(true);
    setActiveTab("result");
    try {
      const token = localStorage.getItem("token");
      const res = await axios.get(
        `http://localhost:8080/api/nurse/vaccine-result/program/${programId}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setSampleResultData(res.data); // bật chế độ editable
      setEditableRows(res.data.map((item) => ({ ...item })));
    } catch (error) {
      setSampleResultData([]);
      setEditableRows([]);
      message.error("Không lấy được dữ liệu kết quả!");
    } finally {
      setSelectedVaccineResultLoading(false);
    }
  };

  const handleSendNotification = async (programId) => {
    const confirm = await Swal.fire({
      title: "Bạn có chắc muốn gửi thông báo?",
      text: "Sau khi gửi, phụ huynh sẽ nhận được thông báo về chương trình tiêm chủng này.",
      icon: "question",
      showCancelButton: true,
      confirmButtonText: "Gửi thông báo",
      cancelButtonText: "Hủy",
    });
    if (!confirm.isConfirmed) return;

    const token = localStorage.getItem("token");
    try {
      await axios.post(
        `http://localhost:8080/api/nurse/create-vaccine-form/${programId}`,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );
      Swal.fire({
        icon: "success",
        title: "Thành công",
        text: "Gửi thông báo thành công!",
        confirmButtonText: "OK",
      });
    } catch (error) {
      Swal.fire({
        icon: "error",
        title: "Thất bại",
        text: "Gửi thông báo thất bại!",
        confirmButtonText: "OK",
      });
    }
  };

  return (
    <div
      style={{
        marginLeft: 220,
        padding: 24,
        width: "100%",
        maxWidth: "100%",
        transition: "margin 0.2s",
        minWidth: 0, // Thêm dòng này
      }}
    >
      <Tabs
        activeKey={activeTab}
        onChange={setActiveTab}
        items={[
          {
            key: "program",
            label: "Chương trình tiêm chủng",
            children: (
              <>
                <div
                  style={{
                    display: "flex",
                    alignItems: "center",
                    marginBottom: 15,
                    justifyContent: "space-between",
                    width: "100%",
                    flexWrap: "wrap",
                    gap: 12,
                  }}
                >
                  <h2
                    style={{
                      margin: 0,
                      flex: 1,
                      fontWeight: 700,
                      whiteSpace: "nowrap",
                    }}
                  >
                    <span style={{ color: "#52c41a", marginRight: 8 }}>🛡️</span>
                    Quản Lý Chương Trình Tiêm Chủng
                  </h2>
                  <div
                    style={{
                      display: "flex",
                      alignItems: "center",
                      gap: 1,
                      flexWrap: "wrap",
                      marginLeft: "auto",
                    }}
                  >
                    {/* Nút Lấy biểu mẫu căn trái */}
                    {userRole === "ADMIN" && (
                      <Button
                        type="default"
                        icon={<DownloadOutlined />}
                        style={{
                          border: "1.5px solid #21ba45",
                          color: "#21ba45",
                          background: "#fff",
                          fontWeight: 600,
                          marginRight: 12,
                        }}
                        onClick={() => {
                          const link = document.createElement("a");
                          link.href = "/vaccine_name_import.xlsx";
                          link.setAttribute("download", "vaccine_name_import.xlsx");
                          document.body.appendChild(link);
                          link.click();
                          document.body.removeChild(link);
                        }}
                      >
                        Lấy biểu mẫu
                      </Button>
                    )}

                    {/* Nhóm filter và các nút khác căn phải */}
                    <div
                      style={{
                        display: "flex",
                        gap: 12,
                        flexWrap: "wrap",
                        marginLeft: "auto",
                      }}
                    >
                      <Input
                        placeholder="Tìm kiếm tên chương trình..."
                        prefix={<SearchOutlined />}
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        allowClear
                        style={{ width: 220, background: "#fff" }}
                      />
                      <DatePicker
                        placeholder="Lọc theo ngày tiêm"
                        value={filterDate}
                        onChange={setFilterDate}
                        allowClear
                        style={{ width: 170 }}
                        format="YYYY-MM-DD"
                      />
                      <Select
                        placeholder="Lọc theo trạng thái"
                        value={filterStatus}
                        onChange={setFilterStatus}
                        allowClear
                        style={{ width: 170 }}
                        options={[
                          { value: "", label: "Tất cả trạng thái" },
                          { value: "NOT_STARTED", label: "Chưa bắt đầu" },
                          { value: "ON_GOING", label: "Đang diễn ra" },
                          { value: "COMPLETED", label: "Đã hoàn thành" },
                        ]}
                      />
                      {userRole === "ADMIN" && (
                        <>
                          <Button
                            type="primary"
                            icon={<PlusOutlined />}
                            style={{ background: "#21ba45", border: "none" }}
                            onClick={() => setCreateVisible(true)}
                          >
                            Lên lịch tiêm chủng
                          </Button>
                          <Button
                            type="default"
                            style={{
                              border: "1px solid #21ba45",
                              color: "#21ba45",
                            }}
                            onClick={() => setAddVaccineVisible(true)}
                          >
                            Thêm mới vaccine
                          </Button>
                          <Button
                            type="default"
                            icon={<UploadOutlined />}
                            style={{
                              border: "1.5px solid #1890ff",
                              color: "#fff",
                              background:
                                "linear-gradient(90deg, #1890ff 60% 100%)",
                              fontWeight: 600,
                              boxShadow: "0 2px 8px rgba(33,186,69,0.08)",
                              transition: "all 0.2s",
                            }}
                            onClick={() => setImportVaccineVisible(true)}
                          >
                            Import tên vaccine
                          </Button>
                        </>
                      )}
                    </div>
                  </div>
                </div>
                {/* Danh sách chương trình hoặc thông báo rỗng */}
                <div style={{ minHeight: 350 }}>
                  {filteredPrograms.length === 0 ? (
                    <div
                      style={{
                        textAlign: "center",
                        color: "#888",
                        marginTop: 48,
                        fontSize: 18,
                      }}
                    >
                      Không có chương trình tiêm chủng nào.
                    </div>
                  ) : (
                    pagedPrograms.map((program) => (
                      <Card
                        key={program.vaccineId}
                        style={{
                          background: "#f6fcf7",
                          borderRadius: 10,
                          border: "1px solid #e6f4ea",
                          width: "100%", // Sửa lại từ "calc(100vw - 260px)"
                          minWidth: 0,   // Sửa lại từ 1200
                          margin: "0 auto",
                          transition: "width 0.2s",
                          marginBottom: 16,
                        }}
                        bodyStyle={{ padding: 24 }}
                      >
                        <div
                          style={{
                            display: "flex",
                            justifyContent: "space-between",
                            alignItems: "flex-start",
                          }}
                        >
                          <div>
                            <div
                              style={{
                                fontWeight: 700,
                                fontSize: 18,
                                marginBottom: 4,
                              }}
                            >
                              {program.vaccineName?.vaccineName ||
                                program.vaccineName}
                            </div>
                            <div style={{ color: "#555", marginBottom: 2 }}>
                              Mô tả: {program.description}
                            </div>
                            <div style={{ color: "#555", marginBottom: 8 }}>
                              Ngày tiêm: {program.vaccineDate}
                            </div>
                          </div>
                          {/* Nếu là ADMIN thì cho phép chỉnh trạng thái, nếu là NURSE thì chỉ hiển thị Tag */}
                          {userRole === "ADMIN" ? (
                            <Select
                              value={program.status}
                              style={{
                                width: 160,
                                marginTop: 4,
                                fontWeight: 600,
                                color:
                                  program.status === "ON_GOING"
                                    ? "#1890ff"
                                    : program.status === "COMPLETED"
                                    ? "#21ba45"
                                    : "#595959",
                              }}
                              onChange={(status) =>
                                handleUpdateStatus(program.vaccineId, status)
                              }
                              options={[
                                {
                                  value: "NOT_STARTED",
                                  label: (
                                    <span style={{ color: "#595959" }}>
                                      Chưa bắt đầu
                                    </span>
                                  ),
                                },
                                {
                                  value: "ON_GOING",
                                  label: (
                                    <span style={{ color: "#1890ff" }}>
                                      Đang diễn ra
                                    </span>
                                  ),
                                },
                                {
                                  value: "COMPLETED",
                                  label: (
                                    <span style={{ color: "#21ba45" }}>
                                      Đã hoàn thành
                                    </span>
                                  ),
                                },
                              ]}
                              dropdownStyle={{ minWidth: 160 }}
                            />
                          ) : (
                            <Tag
                              color={getStatusColor(program.status)}
                              style={{ fontSize: 14, marginTop: 4 }}
                            >
                              {getStatusText(program.status)}
                            </Tag>
                          )}
                        </div>
                        <Row gutter={32} style={{ margin: "24px 0" }}>
                          <Col span={12}>
                            <div
                              style={{
                                background: "#fff",
                                borderRadius: 8,
                                padding: 16,
                                textAlign: "center",
                              }}
                            >
                              <div
                                style={{
                                  color: "#1890ff",
                                  fontWeight: 700,
                                  fontSize: 32,
                                }}
                              >
                                {studentStats[program.vaccineId]?.total ??
                                  "..."}
                              </div>
                              <div style={{ color: "#888", fontWeight: 500 }}>
                                Tổng học sinh
                              </div>
                            </div>
                          </Col>
                          <Col span={12}>
                            <div
                              style={{
                                background: "#fff",
                                borderRadius: 8,
                                padding: 16,
                                textAlign: "center",
                              }}
                            >
                              <div
                                style={{
                                  color: "#21ba45",
                                  fontWeight: 700,
                                  fontSize: 32,
                                }}
                              >
                                {studentStats[program.vaccineId]?.confirmed ??
                                  "..."}
                              </div>
                              <div style={{ color: "#888", fontWeight: 500 }}>
                                Đã xác nhận
                              </div>
                            </div>
                          </Col>
                        </Row>
                        <div
                          style={{
                            display: "flex",
                            justifyContent: "space-between",
                            alignItems: "center",
                          }}
                        >
                          <div>
                            <Button
                              onClick={() => {
                                setDetailVisible(true);
                                setProgram(program);
                              }}
                            >
                              Xem chi tiết
                            </Button>
                            <Button
                              style={{
                                background: "#21ba45",
                                border: "none",
                                marginLeft: 8,
                              }}
                              onClick={() =>
                                handleViewResult(program.vaccineId)
                              }
                              disabled={program.status !== "COMPLETED"}
                            >
                              Xem kết quả
                            </Button>
                            <Button
                              type="primary"
                              style={{
                                marginLeft: 8,
                                background: "#1890ff",
                                border: "none",
                              }}
                              onClick={() => handleCreateProgramResult(program)}
                              disabled={
                                userRole === "ADMIN" ||
                                !(
                                  userRole === "NURSE" &&
                                  program.status === "ON_GOING" &&
                                  studentStats[program.vaccineId]?.confirmed > 0
                                )
                              } // Chỉ cho NURSE bấm khi đang ON_GOING và đã xác nhận > 0
                            >
                              Tạo kết quả
                            </Button>
                            <Button
                              type="default"
                              style={{
                                marginLeft: 8,
                                background: "#ff9800",
                                color: "#fff",
                                border: "none",
                              }}
                              onClick={() =>
                                handleEditResult(program.vaccineId)
                              }
                              disabled={program.status !== "COMPLETED"}
                            >
                              Chỉnh sửa kết quả
                            </Button>
                            <Button
                              type="default"
                              style={{
                                marginLeft: 8,
                                background: "#00bcd4",
                                color: "#fff",
                                border: "none",
                              }}
                              onClick={() =>
                                handleSendNotification(program.vaccineId)
                              }
                              disabled={
                                userRole === "ADMIN" ||
                                program.status === "NOT_STARTED" ||
                                program.status === "COMPLETED" ||
                                program.sended === 1 || // Nếu đã gửi thông báo thì disable
                                (program.status === "ON_GOING" &&
                                  Array.isArray(
                                    studentStats[program.vaccineId]?.forms
                                  ) &&
                                  studentStats[program.vaccineId]?.forms.every(
                                    (f) => f.status === "DRAFT"
                                  ) === false)
                              }
                            >
                              Gửi thông báo
                            </Button>
                            {/* Đã xóa nút Tạo kết quả */}
                          </div>
                          {/* Ẩn nút Sửa, Xóa nếu là NURSE */}
                          {userRole === "ADMIN" && (
                            <div
                              style={{
                                display: "flex",
                                gap: 8,
                                marginLeft: "auto",
                              }}
                            >
                              {program.status === "NOT_STARTED" && (
                                <Button
                                  type="default"
                                  onClick={() => {
                                    setProgram(program);
                                    setEditMode(true);
                                    setCreateVisible(true);
                                  }}
                                >
                                  Sửa
                                </Button>
                              )}
                              {userRole === "ADMIN" &&
                                program.status === "NOT_STARTED" && (
                                  <Button
                                    danger
                                    type="primary"
                                    onClick={() =>
                                      handleDelete(program.vaccineId)
                                    }
                                  >
                                    Xóa
                                  </Button>
                                )}
                            </div>
                          )}
                        </div>
                      </Card>
                    ))
                  )}
                </div>
                <div style={{ marginTop: 24, textAlign: "center" }}>
                  <Pagination
                    current={currentPage}
                    pageSize={pageSize}
                    total={filteredPrograms.length}
                    onChange={setCurrentPage}
                    showSizeChanger={false}
                  />
                </div>
                <Modal
                  title="Chi tiết chương trình tiêm chủng"
                  open={detailVisible}
                  onCancel={() => setDetailVisible(false)}
                  footer={[
                    <Button key="close" onClick={() => setDetailVisible(false)}>
                      Đóng
                    </Button>,
                  ]}
                >
                  {program && (
                    <Descriptions column={1} bordered size="small">
                      <Descriptions.Item label="Vaccine ID">
                        {program.vaccineId}
                      </Descriptions.Item>
                      <Descriptions.Item label="Tên vaccine">
                        {typeof program.vaccineName === "object"
                          ? program.vaccineName.vaccineName
                          : program.vaccineName}
                      </Descriptions.Item>
                      <Descriptions.Item label="Mô tả">
                        {program.description}
                      </Descriptions.Item>
                      <Descriptions.Item label="Ngày tiêm">
                        {program.vaccineDate}
                      </Descriptions.Item>
                      <Descriptions.Item label="Trạng thái">
                        <Tag
                          color={getStatusColor(program.status)}
                          style={{ fontSize: 14 }}
                        >
                          {getStatusText(program.status)}
                        </Tag>
                      </Descriptions.Item>
                      <Descriptions.Item label="Ghi chú">
                        {program.note}
                      </Descriptions.Item>
                    </Descriptions>
                  )}
                </Modal>
                <Modal
                  title={
                    editMode
                      ? "Sửa chương trình tiêm chủng"
                      : "Lên lịch tiêm chủng"
                  }
                  open={createVisible}
                  onCancel={() => {
                    setCreateVisible(false);
                    setEditMode(false);
                    setProgram(null);
                  }}
                  footer={null}
                  destroyOnClose
                >
                  <Form
                    layout="vertical"
                    onFinish={editMode ? handleUpdate : handleCreate}
                    initialValues={
                      editMode && program
                        ? {
                            vaccineName: program.vaccineName,
                            manufacture: program.manufacture,
                            description: program.description,
                            vaccineDate: dayjs(program.vaccineDate),
                            note: program.note,
                          }
                        : {}
                    }
                  >
                    <Form.Item
                      label="Tên vaccine"
                      name="vaccineNameId"
                      rules={[{ required: true, message: "Chọn vaccine" }]}
                    >
                      <Select
                        showSearch
                        placeholder="Chọn vaccine"
                        optionFilterProp="children"
                        filterOption={(input, option) =>
                          option.children
                            .toLowerCase()
                            .includes(input.toLowerCase())
                        }
                      >
                        {vaccineList.map((v) => (
                          <Select.Option key={v.id} value={v.id}>
                            {v.vaccineName}
                          </Select.Option>
                        ))}
                      </Select>
                    </Form.Item>
                    <Form.Item label="Mô tả" name="description">
                      <Input.TextArea rows={2} />
                    </Form.Item>
                    <Form.Item
                      label="Ngày tiêm"
                      name="vaccineDate"
                      rules={[
                        { required: true, message: "Chọn ngày tiêm" },
                        {
                          validator: (_, value) => {
                            if (!value) return Promise.resolve();
                            if (value.isBefore(dayjs(), "day")) {
                              return Promise.reject(
                                "Chỉ được chọn ngày trong tương lai!"
                              );
                            }
                            return Promise.resolve();
                          },
                        },
                      ]}
                    >
                      <DatePicker
                        style={{ width: "100%" }}
                        format="YYYY-MM-DD"
                        disabledDate={(current) =>
                          current && current < dayjs().startOf("day")
                        }
                      />
                    </Form.Item>
                    <Form.Item label="Ghi chú" name="note">
                      <Input.TextArea rows={2} />
                    </Form.Item>
                    <Form.Item>
                      <Button
                        type="primary"
                        htmlType="submit"
                        loading={loading}
                        style={{ width: "100%" }}
                      >
                        {editMode ? "Cập nhật" : "Tạo chương trình"}
                      </Button>
                    </Form.Item>
                  </Form>
                </Modal>
                <Modal
                  title="Kết quả tiêm chủng"
                  open={resultVisible}
                  onCancel={() => setResultVisible(false)}
                  footer={[
                    <Button key="close" onClick={() => setResultVisible(false)}>
                      Đóng
                    </Button>,
                  ]}
                >
                  {resultLoading ? (
                    <div>Đang tải...</div>
                  ) : resultData.length === 0 ? (
                    <div>Không có dữ liệu kết quả cho chương trình này.</div>
                  ) : (
                    <Descriptions column={1} bordered size="small">
                      {resultData.map((item, idx) => (
                        <React.Fragment key={idx}>
                          <Descriptions.Item label="Trạng thái sức khỏe">
                            {item.statusHealth}
                          </Descriptions.Item>
                          <Descriptions.Item label="Số lượng">
                            {item.count}
                          </Descriptions.Item>
                        </React.Fragment>
                      ))}
                    </Descriptions>
                  )}
                </Modal>
                <Modal
                  title="Tạo kết quả tiêm chủng"
                  open={createResultVisible}
                  onCancel={() => {
                    setCreateResultVisible(false);
                    resultForm.resetFields();
                  }}
                  footer={null}
                  destroyOnClose
                >
                  <Form
                    form={resultForm}
                    layout="vertical"
                    onFinish={handleCreateResult}
                  >
                    <Form.Item
                      label="Tình trạng sức khỏe"
                      name="statusHealth"
                      rules={[
                        { required: true, message: "Nhập tình trạng sức khỏe" },
                      ]}
                    >
                      <Input />
                    </Form.Item>
                    <Form.Item
                      label="Phản ứng"
                      name="reaction"
                      rules={[{ required: true, message: "Nhập phản ứng" }]}
                    >
                      <Input />
                    </Form.Item>
                    <Form.Item label="Ghi chú kết quả" name="resultNote">
                      <Input.TextArea rows={2} />
                    </Form.Item>
                    <Form.Item>
                      <Button
                        type="primary"
                        htmlType="submit"
                        loading={createResultLoading}
                        style={{ width: "100%" }}
                      >
                        Tạo kết quả
                      </Button>
                    </Form.Item>
                  </Form>
                </Modal>
                <Modal
                  title="Thêm mới vaccine"
                  open={addVaccineVisible}
                  onCancel={() => {
                    setAddVaccineVisible(false);
                    addVaccineForm.resetFields();
                  }}
                  footer={null}
                  destroyOnClose
                >
                  <Form
                    form={addVaccineForm}
                    layout="vertical"
                    onFinish={async (values) => {
                      setAddVaccineLoading(true);
                      const token = localStorage.getItem("token");
                      try {
                        await axios.post(
                          "http://localhost:8080/api/admin/create-VaccineName",
                          {
                            vaccineName: values.vaccineName,
                            manufacture: values.manufacture,
                            url: values.url, // Thêm dòng này
                            note: values.note,
                          },
                          { headers: { Authorization: `Bearer ${token}` } }
                        );
                        Swal.fire({
                          icon: "success",
                          title: "Thêm vaccine mới thành công!",
                          showConfirmButton: false,
                          timer: 1500,
                        });
                        setAddVaccineVisible(false);
                        addVaccineForm.resetFields();
                        fetchVaccineList();
                      } catch {
                        Swal.fire({
                          icon: "error",
                          title: "Thêm vaccine mới thất bại!",
                        });
                      } finally {
                        setAddVaccineLoading(false);
                      }
                    }}
                  >
                    <Form.Item
                      label="Tên vaccine"
                      name="vaccineName"
                      rules={[{ required: true, message: "Nhập tên vaccine" }]}
                    >
                      <Input />
                    </Form.Item>
                    <Form.Item label="Nhà sản xuất" name="manufacture">
                      <Input />
                    </Form.Item>
                    <Form.Item
                      label="URL thông tin vaccine"
                      name="url"
                      rules={[
                        {
                          required: true,
                          message: "Nhập URL thông tin vaccine",
                        },
                        { type: "url", message: "URL không hợp lệ!" },
                      ]}
                    >
                      <Input />
                    </Form.Item>
                    <Form.Item label="Ghi chú" name="note">
                      <Input.TextArea rows={2} />
                    </Form.Item>
                    <Form.Item>
                      <Button
                        type="primary"
                        htmlType="submit"
                        loading={addVaccineLoading}
                        style={{ width: "100%" }}
                      >
                        Thêm mới
                      </Button>
                    </Form.Item>
                  </Form>
                </Modal>
                <Modal
                  title="Import tên vaccine từ Excel"
                  open={importVaccineVisible}
                  onCancel={() => setImportVaccineVisible(false)}
                  footer={null}
                  destroyOnClose
                >
                  <Form
                    layout="vertical"
                    onFinish={async () => {
                      if (!importFile) {
                        message.error("Vui lòng chọn file Excel!");
                        return;
                      }
                      const formData = new FormData();
                      formData.append("file", importFile);
                      setImportLoading(true);
                      try {
                        const token = localStorage.getItem("token");
                        await axios.post(
                          "http://localhost:8080/api/admin/vaccine-name/import-excel",
                          formData,
                          {
                            headers: {
                              Authorization: `Bearer ${token}`,
                              "Content-Type": "multipart/form-data",
                            },
                          }
                        );
                        // Thông báo thành công bằng Swal.fire
                        Swal.fire({
                          icon: "success",
                          title: "Import thành công!",
                          showConfirmButton: false,
                          timer: 1500,
                        });
                        setImportVaccineVisible(false);
                        fetchVaccineList();
                        setImportFile(null);
                      } catch {
                        message.error("Import thất bại!");
                      } finally {
                        setImportLoading(false);
                      }
                    }}
                  >
                    <Form.Item
                      label="Chọn file Excel"
                      name="file"
                      rules={[
                        {
                          required: true,
                          message: "Vui lòng chọn file Excel!",
                        },
                      ]}
                    >
                      <Input
                        type="file"
                        accept=".xlsx,.xls"
                        onChange={(e) => setImportFile(e.target.files[0])}
                      />
                    </Form.Item>
                    <Form.Item>
                      <Button
                        type="primary"
                        htmlType="submit"
                        loading={importLoading}
                        style={{ width: "100%" }}
                      >
                        Import
                      </Button>
                    </Form.Item>
                  </Form>
                </Modal>
              </>
            ),
          },
          {
            key: "result",
            label: "Kết quả chương trình",
            children: (
              <div style={{ marginTop: 24 }}>
                <div
                  style={{
                    background: "#fff",
                    borderRadius: 10,
                    padding: 24,
                    width: "100%", // Sửa dòng này
                    maxWidth: "100%", // Sửa dòng này
                    margin: "0 auto",
                    boxShadow: "0 2px 8px rgba(0,0,0,0.05)",
                    overflowX: "auto",
                  }}
                >
                  {/* Thêm ô tìm kiếm */}
                  <div
                    style={{
                      marginBottom: 16,
                      display: "flex",
                      justifyContent: "flex-end",
                    }}
                  >
                    <Input
                      placeholder="Tìm kiếm tên học sinh..."
                      prefix={<SearchOutlined />}
                      value={searchTermResult}
                      onChange={(e) => setSearchTermResult(e.target.value)}
                      allowClear
                      style={{ width: 260 }}
                    />
                  </div>
                  <Table
                    columns={[
                      {
                        title: "ID kết quả",
                        dataIndex: "vaccineResultId",
                        key: "vaccineResultId",
                      },
                      {
                        title: "Học sinh",
                        dataIndex: ["studentDTO", "fullName"],
                        key: "studentName",
                        render: (_, record) => record.studentDTO?.fullName,
                      },
                      {
                        title: "Giới tính",
                        dataIndex: ["studentDTO", "gender"],
                        key: "gender",
                        render: (_, record) => record.studentDTO?.gender,
                      },
                      {
                        title: "Tình trạng sức khỏe",
                        dataIndex: "statusHealth",
                        key: "statusHealth",
                        render: (text, record) =>
                          sampleResultData ? (
                            <Input
                              value={
                                editableRows.find(
                                  (r) =>
                                    r.vaccineResultId === record.vaccineResultId
                                )?.statusHealth
                              }
                              onChange={(e) =>
                                handleEditCell(
                                  e.target.value,
                                  record,
                                  "statusHealth"
                                )
                              }
                              style={{ minWidth: 100 }}
                            />
                          ) : (
                            text
                          ),
                      },
                      {
                        title: "Phản ứng",
                        dataIndex: "reaction",
                        key: "reaction",
                        render: (text, record) =>
                          sampleResultData ? (
                            <Input
                              value={
                                editableRows.find(
                                  (r) =>
                                    r.vaccineResultId === record.vaccineResultId
                                )?.reaction
                              }
                              onChange={(e) =>
                                handleEditCell(
                                  e.target.value,
                                  record,
                                  "reaction"
                                )
                              }
                              style={{ minWidth: 100 }}
                            />
                          ) : (
                            text
                          ),
                      },
                      {
                        title: "Ghi chú kết quả",
                        dataIndex: "resultNote",
                        key: "resultNote",
                        render: (text, record) =>
                          sampleResultData ? (
                            <Input
                              value={
                                editableRows.find(
                                  (r) =>
                                    r.vaccineResultId === record.vaccineResultId
                                )?.resultNote
                              }
                              onChange={(e) =>
                                handleEditCell(
                                  e.target.value,
                                  record,
                                  "resultNote"
                                )
                              }
                              style={{ minWidth: 120 }}
                            />
                          ) : (
                            text
                          ),
                      },
                      {
                        title: "Ngày tạo",
                        dataIndex: "createdAt",
                        key: "createdAt",
                        render: (text) => dayjs(text).format("YYYY-MM-DD"),
                      },
                      {
                        title: "Tên vaccine",
                        key: "vaccineName",
                        render: (_, record) =>
                          record.vaccineFormDTO?.vaccineProgram?.vaccineName
                            ?.vaccineName,
                      },
                      // Bỏ cột "Ngày tiêm"
                      sampleResultData && {
                        title: "Thao tác",
                        key: "action",
                        render: (_, record) => (
                          <Button
                            type="primary"
                            onClick={() =>
                              handleSaveRow(
                                editableRows.find(
                                  (r) =>
                                    r.vaccineResultId === record.vaccineResultId
                                )
                              )
                            }
                          >
                            Lưu
                          </Button>
                        ),
                      },
                    ].filter(Boolean)}
                    dataSource={
                      sampleResultData
                        ? editableRows.filter((item) =>
                            (item?.studentDTO?.fullName || "")
                              .toLowerCase()
                              .includes(searchTermResult.toLowerCase())
                          )
                        : selectedVaccineResultId
                        ? (selectedVaccineResult || []).filter((item) =>
                            (item?.studentDTO?.fullName || "")
                              .toLowerCase()
                              .includes(searchTermResult.toLowerCase())
                          )
                        : filteredNurseResults
                    }
                    loading={
                      selectedVaccineResultLoading || nurseResultsLoading
                    }
                    rowKey="vaccineResultId"
                    bordered
                    style={{
                      paddingLeft: 2,
                      width: "100%",
                      minWidth: 1600, // Sửa lại từ 1250
                      borderRadius: 12,
                      overflow: "auto", // Đảm bảo Table cuộn khi thiếu không gian
                      background: "#fff",
                      boxShadow: "0 2px 8px rgba(33,186,69,0.08)",
                    }}
                    scroll={{ x: true }} // Cho phép cuộn ngang tự động
                    pagination={{
                      current: resultTablePage,
                      pageSize: resultTablePageSize,
                      total: sampleResultData
                        ? editableRows.filter((item) =>
                            (item?.studentDTO?.fullName || "")
                              .toLowerCase()
                              .includes(searchTermResult.toLowerCase())
                          ).length
                        : selectedVaccineResultId
                        ? (selectedVaccineResult || []).filter((item) =>
                            (item?.studentDTO?.fullName || "")
                              .toLowerCase()
                              .includes(searchTermResult.toLowerCase())
                          ).length
                        : filteredNurseResults.length,
                      onChange: setResultTablePage,
                      showSizeChanger: false,
                    }}
                  />
                </div>
              </div>
            ),
          },
        ]}
      />
    </div>
  );
};

export default VaccineProgramList;
