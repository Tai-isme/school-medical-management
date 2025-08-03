import React, { useEffect, useState, useMemo } from "react";
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
import VaccineProgramModal from "./VaccineProgramModal"; // Import component mới tạo
import VaccineProgramResultTab from "./VaccineProgramResultTab";
import AddVaccineModal from "./AddVaccineModal";
import VaccineImportModal from './VaccineImportModal';
import TemplateDownloadButton from './TemplateDownloadButton';


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

  const [importVaccineVisible, setImportVaccineVisible] = useState(false);
  const [importLoading, setImportLoading] = useState(false);
  const [importFile, setImportFile] = useState(null);
  const [vaccineData, setVaccineData] = useState([]);

  const [notifyModalVisible, setNotifyModalVisible] = useState(false);
  const [notifyProgramId, setNotifyProgramId] = useState(null);
  const [notifyDeadline, setNotifyDeadline] = useState(null);
  const [notifyLoading, setNotifyLoading] = useState(false);

  useEffect(() => {
    fetchProgram();
    // Lấy danh sách vaccine khi load trang
    fetchVaccineList();
    // Lấy kết quả của nurse khi load trang
    fetchNurseResults();
    // fetchVaccineData();
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
      // Chuẩn hóa dữ liệu để tương thích với render
      const programs = res.data
        .map((item) => ({
          vaccineProgramId: item.vaccineProgramId,
          vaccineProgramName: item.vaccineProgramName,
          description: item.description,
          vaccineId: item.vaccineId,
          unit: item.unit,
          startDate: item.startDate,
          dateSendForm: item.dateSendForm,
          status: item.status,
          location: item.location,
          nurseId: item.nurseId,
          adminId: item.adminId,
          nurse: item.nurseDTO,
          admin: item.adminDTO,
          vaccineName: item.vaccineNameDTO?.vaccineName || "",
          vaccineNameId: item.vaccineNameDTO?.id,
          manufacture: item.vaccineNameDTO?.manufacture || "",
          totalUnit: item.vaccineNameDTO?.totalUnit || "",
          vaccineUnitDTOs: item.vaccineNameDTO?.vaccineUnitDTOs || [],
          participateClassDTOs:
            item.participateClassDTOs?.map((cls) => ({
              classId: cls.classDTO?.classId,
              className: cls.classDTO?.className,
              teacherName: cls.classDTO?.teacherName,
              quantity: cls.classDTO?.quantity,
            })) || [],
          vaccineFormDTOs: item.vaccineFormDTOs || [],
          note: item.vaccineFormDTOs?.[0]?.note || "",
        }))
        // Sắp xếp giảm dần theo vaccineProgramId
        .sort((a, b) => b.vaccineProgramId - a.vaccineProgramId);

      setPrograms(programs);
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

  const handleCreate = async (values) => {
    setLoading(true);
    const token = localStorage.getItem("token");
    try {
      await axios.post(
        "http://localhost:8080/api/admin/vaccine-program",
        {
          ...values,
          startDate: values.startDate
            ? values.startDate.format("YYYY-MM-DD")
            : undefined,
          dateSendForm: values.sendFormDate
            ? values.sendFormDate.format("YYYY-MM-DD")
            : undefined,
          classIds: values.classIds, // Đảm bảo gửi đúng tên trường
        },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      // Sau khi tạo chương trình thành công
      message.success("Tạo chương trình tiêm chủng thành công!");
      setCreateVisible(false);
      setEditMode(false);
      setProgram(null);
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
        `http://localhost:8080/api/admin/vaccine-program/${program.vaccineProgramId}`,
        {
          vaccineProgramName: values.vaccineProgramName,
          vaccineNameId: values.vaccineNameId,
          unit: values.unit,
          description: values.description,
          startDate: values.startDate.format("YYYY-MM-DD"),
          dateSendForm: values.sendFormDate.format("YYYY-MM-DD"),
          location: values.location,
          nurseId: values.nurseId,
          classIds: values.classIds, // Đổi từ values.classes sang values.classIds
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      // Sau khi cập nhật chương trình thành công
      await Swal.fire({
        icon: "success",
        title: "Cập nhật chương trình thành công!",
        showConfirmButton: false,
        timer: 1500,
      });
      setCreateVisible(false);
      setEditMode(false);
      setProgram(null);
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
        // Sau khi xóa chương trình thành công
        await Swal.fire(
          "Đã xóa!",
          "Chương trình đã được xóa thành công.",
          "success"
        );
        setProgram(null);
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
    try {
      const token = localStorage.getItem("token");
      const res = await axios.get(
        `http://localhost:8080/api/nurse/vaccine-result/program/${programId}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      // Khi nhận response từ API (res.data là mảng như bạn gửi ở trên)
      const mappedData = res.data.map((item) => ({
        vaccineResultId: item.vaccineResultDTO?.vaccineResultId || null,
        vaccineFormId: item.id,
        reaction: item.vaccineResultDTO?.reaction || "",
        actionsTaken: item.vaccineResultDTO?.actionsTaken || "",
        resultNote: item.vaccineResultDTO?.resultNote || "",
        isInjected:
          typeof item.vaccineResultDTO?.isInjected === "boolean"
            ? item.vaccineResultDTO.isInjected
            : false,
        createdAt: item.vaccineResultDTO?.createdAt || "",
        studentDTO: item.studentDTO || null,
        // ...bạn có thể thêm các trường khác nếu cần
      }));

      setSampleResultData(mappedData);
      setEditableRows(mappedData.map((item) => ({ ...item })));
    } catch (err) {
      setSampleResultData([]);
      setEditableRows([]);
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
      const res = await axios.get(
        `http://localhost:8080/api/nurse/vaccine-forms-commit/program/${program.vaccineProgramId}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
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
        return "default"; // Xám
      case "FORM_SENT":
        return "orange"; // Cam
      case "ON_GOING":
        return "blue"; // Xanh dương
      case "GENERATED_RESULT":
        return "purple"; // Tím
      case "COMPLETED":
        return "green"; // Xanh lá
      default:
        return "default";
    }
  };

  // Hàm ánh xạ trạng thái sang tiếng Việt
  const getStatusText = (status) => {
    switch (status) {
      case "NOT_STARTED":
        return "Chưa bắt đầu";
      case "FORM_SENT":
        return "Đã gửi form";
      case "ON_GOING":
        return "Đang diễn ra";
      case "GENERATED_RESULT":
        return "Đã tạo kết quả";
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
  const filteredNurseResults = Array.isArray(nurseResults)
    ? nurseResults.filter((item) => {
        const studentName = item?.studentDTO?.fullName || "";
        return studentName
          .toLowerCase()
          .includes(searchTermResult.toLowerCase());
      })
    : [];

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

  const handleExportResultToExcel = async (vaccineProgramId) => {
    const token = localStorage.getItem("token");
    try {
      const response = await axios.post(
        `http://localhost:8080/api/admin/export-vaccine-result-excel-by-vaccine-program/${vaccineProgramId}`,
        {
          responseType: "blob",
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      // Tạo link tải file
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", `vaccine-result-${vaccineProgramId}.xlsx`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      // Xử lý lỗi nếu cần
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
      const mappedData = res.data.map((item) => ({
        vaccineResultId: item.vaccineResultDTO?.vaccineResultId || null,
        vaccineFormId: item.id,
        reaction: item.vaccineResultDTO?.reaction || "",
        actionsTaken: item.vaccineResultDTO?.actionsTaken || "",
        resultNote: item.vaccineResultDTO?.resultNote || "",
        isInjected:
          typeof item.vaccineResultDTO?.isInjected === "boolean"
            ? item.vaccineResultDTO.isInjected
            : false,
        createdAt: item.vaccineResultDTO?.createdAt || "",
        studentDTO: item.studentDTO || null,
      }));
      setSampleResultData(mappedData);
      setEditableRows(mappedData.map((item) => ({ ...item })));
    } catch (error) {
      setSampleResultData([]);
      setEditableRows([]);
      message.error("Không lấy được dữ liệu kết quả!");
    } finally {
      setSelectedVaccineResultLoading(false);
    }
  };

  // ...existing code...
  const handleSendNotification = async (programId, deadline) => {
    const token = localStorage.getItem("token");
    setNotifyLoading(true);
    try {
      await axios.post(
        `http://localhost:8080/api/nurse/create-vaccine-form/${programId}?expDate=${
          deadline ? deadline.format("YYYY-MM-DD") : ""
        }`,
        null,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      Swal.fire({
        icon: "success",
        title: "Thành công",
        text: "Gửi thông báo thành công!",
        confirmButtonText: "OK",
      });
      setNotifyModalVisible(false);
      setNotifyDeadline(null);
      setNotifyProgramId(null);
      fetchProgram();
    } catch (error) {
      // Lấy message từ response nếu có
      let msg = "Gửi thông báo thất bại!";
      if (
        error.response &&
        error.response.data &&
        error.response.data.message
      ) {
        // Xóa "400 BAD_REQUEST" và các ký tự thừa
        msg = error.response.data.message
          .replace(/400 BAD_REQUEST\s*/g, "")
          .replace(/^"|"$/g, "")
          .trim();
      }
      Swal.fire({
        icon: "error",
        title: "Thất bại",
        text: msg,
        confirmButtonText: "OK",
      });
    } finally {
      setNotifyLoading(false);
    }
  };
  // ...existing code...

  const [editData, setEditData] = useState({});
  const memoInitialValues = useMemo(() => editData, [editData]);

  const [modalMode, setModalMode] = useState("create"); // "create" | "edit" | "view"

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
        onChange={(key) => {
          setActiveTab(key);
          if (key === "program") {
            fetchProgram();
          }
        }}
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
                            onClick={() => {
                              setModalMode("create");
                              setEditData({}); // Reset dữ liệu về rỗng
                              setProgram(null);
                              setCreateVisible(true);
                            }}
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

                          {/* Nút Lấy biểu mẫu căn trái */}
                          <TemplateDownloadButton userRole={userRole} />
                          
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
                          width: "100%",
                          minWidth: 0,
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
                              {program.vaccineProgramName}
                            </div>

                            <div style={{ color: "#555", marginBottom: 8 }}>
                              Ngày thực hiện:{" "}
                              <span
                                style={{ color: "#1890ff", fontWeight: 600 }}
                              >
                                {program.startDate
                                  ? new Date(
                                      program.startDate
                                    ).toLocaleDateString("vi-VN", {
                                      day: "2-digit",
                                      month: "2-digit",
                                      year: "numeric",
                                    })
                                  : "---"}
                              </span>
                            </div>
                            <div style={{ color: "#555", marginBottom: 8 }}>
                              Ngày gửi thông báo cho phụ huynh:{" "}
                              <span
                                style={{ color: "#52c41a", fontWeight: 600 }}
                              >
                                {program.dateSendForm
                                  ? new Date(
                                      program.dateSendForm
                                    ).toLocaleDateString("vi-VN", {
                                      day: "2-digit",
                                      month: "2-digit",
                                      year: "numeric",
                                    })
                                  : "---"}
                              </span>
                            </div>
                            <div style={{ color: "#555", marginBottom: 8 }}>
                              {program.vaccineFormDTOs &&
                                program.vaccineFormDTOs.length > 0 && (
                                  <>
                                    Ngày hết hạn đăng ký:{" "}
                                    <span
                                      style={{
                                        color: "#faad14",
                                        fontWeight: 600,
                                      }}
                                    >
                                      {program.vaccineFormDTOs[0].expDate}
                                    </span>
                                  </>
                                )}
                            </div>
                            <div style={{ color: "#555", marginBottom: 8 }}>
                              Địa điểm:{" "}
                              <span
                                style={{ color: "#d4380d", fontWeight: 600 }}
                              >
                                {program.location}
                              </span>
                            </div>
                            {/* <div style={{ color: "#555", marginBottom: 8 }}>
                              Tổng số mũi: {program.totalUnit}
                            </div> */}
                            <div style={{ color: "#555", marginBottom: 8 }}>
                              Người phụ trách:{" "}
                              <strong style={{ color: "#222" }}>
                                {program.nurse?.fullName}
                              </strong>{" "}
                              -{" "}
                              <span style={{ color: "#1890ff" }}>
                                SĐT: {program.nurse?.phone}
                              </span>
                            </div>
                          </div>
                          {/* Nếu là ADMIN thì cho phép chỉnh trạng thái, nếu là NURSE thì chỉ hiển thị Tag */}
                          <Tag
                            color={getStatusColor(program.status)}
                            style={{
                              fontSize: 13,
                              padding: "4px 10px",
                              borderRadius: 16,
                              fontWeight: 500,
                              textTransform: "capitalize",
                              color: "#333",
                              marginTop: 4,
                            }}
                          >
                            {getStatusText(program.status)}
                          </Tag>
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
                                {program.vaccineFormDTOs?.length || 0}
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
                                {program.vaccineFormDTOs
                                  ? program.vaccineFormDTOs.filter(
                                      (form) => form.commit === true
                                    ).length
                                  : 0}
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
                              type="default"
                              onClick={() => {
                                setModalMode("view");
                                setEditData({
                                  vaccineProgramName:
                                    program.vaccineProgramName,
                                  vaccineNameId: program.vaccineNameId,
                                  unit: program.unit || 1,
                                  startDate: program.startDate,
                                  sendFormDate: program.dateSendForm,
                                  classIds:
                                    program.participateClassDTOs?.map(
                                      (cls) => cls.classId
                                    ) || [], // SỬA ĐOẠN NÀY
                                  nurseId: program.nurseId,
                                  location: program.location,
                                  description: program.description,
                                });
                                setProgram(program);
                                setCreateVisible(true);
                              }}
                            >
                              Xem chi tiết
                            </Button>
                            {/* Chỉ hiển thị nút gửi thông báo nếu ngày gửi form là hôm nay */}
                            {dayjs(program.dateSendForm).isSame(
                              dayjs(),
                              "day"
                            ) &&
                              JSON.parse(localStorage.getItem("users"))?.id ===
                                program.nurseId &&
                              program.status === "ON_GOING" && (
                                <Button
                                  type="primary"
                                  style={{
                                    marginLeft: 8,
                                    background: "#1890ff",
                                    border: "none",
                                  }}
                                  onClick={() => {
                                    setNotifyModalVisible(true);
                                    setNotifyProgramId(program.vaccineId);
                                  }}
                                >
                                  Gửi thông báo
                                </Button>
                              )}
                            {/* Nút Tạo kết quả */}
                            {program.status === "FORM_SENT" &&
                              JSON.parse(localStorage.getItem("users"))?.id ===
                                program.nurseId &&
                              dayjs(program.startDate).isSame(
                                dayjs(),
                                "day"
                              ) && (
                                <Button
                                  type="primary"
                                  style={{
                                    marginLeft: 8,
                                    background: "#21ba45",
                                    border: "none",
                                  }}
                                  onClick={() => {
                                    setProgram(program); // Thêm dòng này!
                                    handleCreateProgramResult(program);
                                  }}
                                >
                                  Tạo kết quả
                                </Button>
                              )}
                            {/* Nút Điều chỉnh kết quả */}
                            {program.status === "GENERATED_RESULT" &&
                              JSON.parse(localStorage.getItem("users"))?.id ===
                                program.nurseId && (
                                <Button
                                  type="primary"
                                  style={{
                                    marginLeft: 8,
                                    background: "#faad14",
                                    border: "none",
                                  }}
                                  onClick={() => {
                                    setProgram(program); // Thêm dòng này!
                                    handleEditResult(program.vaccineId);
                                  }}
                                >
                                  Điều chỉnh kết quả
                                </Button>
                              )}
                            {/* Nút Xem kết quả và Xuất kết quả ra excel */}
                            {program.status === "COMPLETED" ||
                              (program.status === "GENERATED_RESULT" && (
                                <>
                                  <Button
                                    type="primary"
                                    style={{
                                      marginLeft: 8,
                                      background: "#1890ff",
                                      border: "none",
                                    }}
                                    onClick={() => {
                                      setProgram(program); // Thêm dòng này!
                                      handleViewResult(program.vaccineId);
                                    }}
                                  >
                                    Xem kết quả
                                  </Button>
                                </>
                              ))}

                            {(program.status === "COMPLETED" ||
                              program.status === "GENERATED_RESULT") &&
                              userRole === "ADMIN" && (
                                <Button
                                  type="default"
                                  style={{
                                    marginLeft: 8,
                                    border: "1.5px solid #21ba45",
                                    color: "#21ba45",
                                    background: "#fff",
                                  }}
                                  onClick={() =>
                                    handleExportResultToExcel(program.vaccineId)
                                  }
                                >
                                  Xuất kết quả ra excel
                                </Button>
                              )}

                            {/* Nút Bắt đầu chương trình */}
                            {program.status === "NOT_STARTED" &&
                              localStorage.getItem("role") === "ADMIN" && (
                                <Button
                                  type="primary"
                                  style={{
                                    marginLeft: 8,
                                    background: "#52c41a",
                                    border: "none",
                                  }}
                                  onClick={() =>
                                    handleUpdateStatus(
                                      program.vaccineId,
                                      "ON_GOING"
                                    )
                                  }
                                >
                                  Bắt đầu chương trình
                                </Button>
                              )}
                            {/* Nút Kết thúc chương trình */}
                            {program.status === "GENERATED_RESULT" &&
                              localStorage.getItem("role") === "ADMIN" && (
                                <Button
                                  type="primary"
                                  style={{
                                    marginLeft: 8,
                                    background: "#d4380d",
                                    border: "none",
                                    float: "right",
                                    minWidth: 180,
                                    fontWeight: 600,
                                  }}
                                  onClick={async () => {
                                    const confirm = await Swal.fire({
                                      title: "Kết thúc chương trình?",
                                      text: "Bạn có chắc muốn kết thúc chương trình này? Sau khi kết thúc, chương trình sẽ chuyển sang trạng thái hoàn thành.",
                                      icon: "warning",
                                      showCancelButton: true,
                                      confirmButtonText: "Kết thúc",
                                      cancelButtonText: "Hủy",
                                    });
                                    if (!confirm.isConfirmed) return;
                                    const token = localStorage.getItem("token");
                                    try {
                                      await axios.patch(
                                        `http://localhost:8080/api/admin/vaccine-program/${program.vaccineId}?status=COMPLETED`,
                                        {},
                                        {
                                          headers: {
                                            Authorization: `Bearer ${token}`,
                                          },
                                        }
                                      );
                                      message.success(
                                        "Đã kết thúc chương trình!"
                                      );
                                      fetchProgram(); // <-- Đã có dòng này, sẽ render lại trang
                                    } catch {
                                      message.error(
                                        "Kết thúc chương trình thất bại!"
                                      );
                                    }
                                  }}
                                >
                                  Kết thúc chương trình
                                </Button>
                              )}
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
                                    setModalMode("edit");
                                    setEditData({
                                      vaccineProgramName:
                                        program.vaccineProgramName,
                                      vaccineNameId: program.vaccineNameId,
                                      unit: program.unit || 1,
                                      startDate: program.startDate,
                                      sendFormDate: program.dateSendForm,
                                      classIds:
                                        program.participateClassDTOs?.map(
                                          (cls) => cls.classId
                                        ) || [], // SỬA ĐOẠN NÀY
                                      nurseId: program.nurseId,
                                      location: program.location,
                                      description: program.description,
                                    });
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
                <VaccineProgramModal
                  open={createVisible}
                  onCancel={() => {
                    setCreateVisible(false);
                    setEditMode(false);
                    setProgram(null);
                  }}
                  onFinish={modalMode === "edit" ? handleUpdate : handleCreate}
                  loading={loading}
                  editMode={modalMode === "edit"}
                  viewMode={modalMode === "view"}
                  program={program}
                  vaccineList={vaccineList}
                  initialValues={memoInitialValues} // memoInitialValues lấy từ editData, đã setEditData(program) khi mở modal
                />

                <AddVaccineModal
                  open={addVaccineVisible}
                  onCancel={() => {
                    setAddVaccineVisible(false);
                    addVaccineForm.resetFields();
                  }}
                  addVaccineForm={addVaccineForm}
                  fetchVaccineList={fetchVaccineList}
                />
                <VaccineImportModal
                  open={importVaccineVisible}
                  onCancel={() => setImportVaccineVisible(false)}
                  onSuccess={() => {
                    fetchVaccineList(); // Refresh data
                    setImportVaccineVisible(false);
                  }}
                />
                <Modal
                  title="Chọn ngày hết hạn đăng ký"
                  open={notifyModalVisible}
                  onCancel={() => {
                    setNotifyModalVisible(false);
                    setNotifyDeadline(null);
                    setNotifyProgramId(null);
                  }}
                  onOk={() =>
                    handleSendNotification(notifyProgramId, notifyDeadline)
                  }
                  okText="Gửi thông báo"
                  confirmLoading={notifyLoading}
                >
                  <DatePicker
                    value={notifyDeadline}
                    onChange={setNotifyDeadline}
                    format="YYYY-MM-DD"
                    style={{ width: "100%" }}
                    placeholder="Chọn ngày hết hạn đăng ký"
                    disabledDate={(current) =>
                      current && current < dayjs().startOf("day")
                    }
                  />
                </Modal>
              </>
            ),
          },
          {
            key: "result",
            label: "Kết quả chương trình",
            children: (
              <VaccineProgramResultTab
                program={program}
                searchTermResult={searchTermResult}
                setSearchTermResult={setSearchTermResult}
                sampleResultData={sampleResultData}
                setSampleResultData={setSampleResultData}
                editableRows={editableRows}
                handleEditCell={handleEditCell}
                handleSaveRow={handleSaveRow}
                selectedVaccineResultId={selectedVaccineResultId}
                selectedVaccineResult={selectedVaccineResult}
                filteredNurseResults={filteredNurseResults}
                selectedVaccineResultLoading={selectedVaccineResultLoading}
                nurseResultsLoading={nurseResultsLoading}
                resultTablePage={resultTablePage}
                resultTablePageSize={resultTablePageSize}
                setResultTablePage={setResultTablePage}
                handleEditResult={handleEditResult} // <-- thêm dòng này
                viewMode={modalMode === "view"}
              />
            ),
          },
        ]}
      />
    </div>
  );
};

export default VaccineProgramList;
