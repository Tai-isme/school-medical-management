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
  Pagination,
  Tabs,
  Table,
  Checkbox,
} from "antd";
import { PlusOutlined, SearchOutlined } from "@ant-design/icons";
import { CheckSquareTwoTone, BorderOutlined } from "@ant-design/icons";
import { Select } from "antd";
import axios from "axios";
import dayjs from "dayjs";
import Swal from "sweetalert2";
import HealthCheckProgramModal from "./HealthCheckProgramModal";

const HealthCheckProgramList = () => {
  const [programs, setPrograms] = useState([]);
  const [detailVisible, setDetailVisible] = useState(false);
  const [createVisible, setCreateVisible] = useState(false);
  const [loading, setLoading] = useState(false);
  const [program, setProgram] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [filterDate, setFilterDate] = useState(null);
  const [editMode, setEditMode] = useState(false);
  const [filterStatus, setFilterStatus] = useState("");
  const [resultVisible, setResultVisible] = useState(false);
  const [resultData, setResultData] = useState([]);
  const [resultLoading, setResultLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [studentsForResult, setStudentsForResult] = useState([]);
  const [createResultVisible, setCreateResultVisible] = useState(false);
  const [activeTab, setActiveTab] = useState("program");
  const [healthCheckResults, setHealthCheckResults] = useState([]);
  const [healthCheckResultsLoading, setHealthCheckResultsLoading] =
    useState(false);
  const [showResultPage, setShowResultPage] = useState(false);
  const [selectedProgramId, setSelectedProgramId] = useState(null);
  const [editableResults, setEditableResults] = useState([]);
  const [studentSearch, setStudentSearch] = useState("");
  const [sentNotificationIds, setSentNotificationIds] = useState([]);
  const pageSize = 3;
  const userRole = localStorage.getItem("role"); // Lấy role từ localStorage
  const [isViewResult, setIsViewResult] = useState(false);
  const [nurseOptions, setNurseOptions] = useState([]);
  const [classOptions, setClassOptions] = useState([]);

  const [editModalVisible, setEditModalVisible] = useState(false);
  const [currentEditRecord, setCurrentEditRecord] = useState(null);

  const [committedForms, setCommittedForms] = useState([]);

  useEffect(() => {
    fetchProgram();
  }, []);

  useEffect(() => {
    // Fetch y tá
    const fetchNurses = async () => {
      const token = localStorage.getItem("token");
      try {
        const res = await axios.get(
          "http://localhost:8080/api/nurse/nurse-list",
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        setNurseOptions(
          res.data
            .filter((nurse) => nurse.role === "NURSE") // Lọc đúng role
            .map((nurse) => ({
              value: nurse.id,
              label: nurse.fullName,
            }))
        );
      } catch {}
    };
    // Fetch lớp
    const fetchClasses = async () => {
      const token = localStorage.getItem("token");
      try {
        const res = await axios.get(
          "http://localhost:8080/api/nurse/class-list",
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        console.log(res.data);
        setClassOptions(
          res.data.map((cls) => ({
            value: cls.classId, // đúng với backend
            label: cls.className, // đúng với backend
          }))
        );
      } catch (err) {
        setClassOptions([]);
      }
    };
    fetchNurses();
    fetchClasses();
  }, []);

  const fetchProgram = async () => {
    const token = localStorage.getItem("token");
    try {
      const res = await axios.get(
        "http://localhost:8080/api/admin/health-check-program",
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setPrograms(res.data); // Dữ liệu đã đúng cấu trúc mới
    } catch (error) {
      setPrograms([]);
    }
  };

  const handleEditChange = (value, record, field) => {
    setEditableResults((prev) =>
      prev.map((item) =>
        item.healthResultId === record.healthResultId
          ? { ...item, [field]: value }
          : item
      )
    );
  };

  const handleEditResult = async (programId) => {
    setHealthCheckResultsLoading(true);
    setIsViewResult(false);
    const token = localStorage.getItem("token");

    try {
      const res = await axios.get(
        `http://localhost:8080/api/nurse/health-check-result/program/${programId}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      // ✅ Convert lại dữ liệu để phù hợp với table
      const convertedResults = res.data.map((form) => {
        const result = form.healthCheckResultDTO || {};
        return {
          healthResultId: result.healthResultId,
          vision: result.vision || "",
          hearing: result.hearing || "",
          weight: result.weight || null,
          height: result.height || null,
          dentalStatus: result.dentalStatus || "",
          bloodPressure: result.bloodPressure || "",
          heartRate: result.heartRate || "",
          generalCondition: result.generalCondition || "",
          note: result.note || "",
          isChecked: result.isChecked || false,
          healthCheckFormId: form.id, // dùng làm rowKey
          studentDTO: form.studentDTO,
        };
      });

      setEditableResults(convertedResults);
      setHealthCheckResults(convertedResults);
      setSelectedProgramId(programId);
      setActiveTab("result");
      setShowResultPage(true);

      console.log("✔ convertedResults:", convertedResults); // debug
    } catch (error) {
      Swal.fire("Lỗi", "Không thể lấy dữ liệu kết quả!", "error");
      setHealthCheckResults([]);
    } finally {
      setHealthCheckResultsLoading(false);
    }
  };

  const handleSendNotification = async (programId) => {
    const { value: expDate } = await Swal.fire({
      title: "Chọn ngày hết hạn",
      input: "date",
      inputLabel: "Ngày hết hạn gửi phiếu",
      inputAttributes: {
        min: new Date().toISOString().split("T")[0],
      },
      inputValidator: (value) => {
        if (!value) return "Vui lòng chọn ngày!";
      },
      confirmButtonText: "Tiếp tục",
      cancelButtonText: "Hủy",
      showCancelButton: true,
      confirmButtonColor: "#21ba45",
      cancelButtonColor: "#d33",
    });

    if (!expDate) return;

    const confirm = await Swal.fire({
      title: "Xác nhận gửi thông báo?",
      text: `Ngày hết hạn đăng ký: ${expDate}`,
      icon: "question",
      showCancelButton: true,
      confirmButtonText: "Gửi",
      cancelButtonText: "Hủy",
      confirmButtonColor: "#21ba45",
      cancelButtonColor: "#d33",
    });

    if (!confirm.isConfirmed) return;

    const token = localStorage.getItem("token");

    try {
      // Gửi thông báo (API này đã tự chuyển trạng thái FORM_SENT)
      await axios.post(
        `http://localhost:8080/api/nurse/health-check-form/${programId}?expDate=${expDate}`,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );

      await Swal.fire({
        icon: "success",
        title: "Gửi thông báo thành công!",
        showConfirmButton: false,
        timer: 1500,
      });

      // Cập nhật UI
      setSentNotificationIds((prev) => [...prev, programId]);
      fetchProgram();
    } catch (error) {
      Swal.fire({
        icon: "error",
        title: "Gửi thông báo thất bại!",
        text: error?.response?.data?.message || "",
        confirmButtonText: "Đóng",
        confirmButtonColor: "#3085d6",
      });
    }
  };

  const handleCreate = async (values) => {
    setLoading(true);
    const token = localStorage.getItem("token");
    const admin = JSON.parse(localStorage.getItem("users"));
    const adminId = admin?.id;
    try {
      await axios.post(
        "http://localhost:8080/api/admin/health-check-program",
        {
          healthCheckName: values.healthCheckName,
          description: values.description,
          startDate: values.startDate.format("YYYY-MM-DD"),
          dateSendForm: values.dateSendForm.format("YYYY-MM-DD"),
          location: values.location,
          nurseId: values.nurseId,
          adminId: adminId, // Sửa lại dòng này
          classIds: values.classIds,
          status: "NOT_STARTED",
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      await Swal.fire({
        icon: "success",
        title: "Thành công!",
        text: "Tạo chương trình sức khỏe thành công!",
        showConfirmButton: false,
        timer: 1500,
      });
      setCreateVisible(false);
      fetchProgram();
    } catch (error) {
      if (error.response && error.response.status === 400) {
        const errorMessage = error.response.data.message || "Đã xảy ra lỗi!";
        Swal.fire({
          icon: "error",
          title: "Lỗi!",
          text: errorMessage,
          confirmButtonColor: "#3085d6",
        });
      } else {
        message.error("Tạo chương trình sức khỏe thất bại!");
      }
    } finally {
      setLoading(false);
    }
  };

  const handleCreateResult = async (programId) => {
    setHealthCheckResultsLoading(true);
    const token = localStorage.getItem("token");

    try {
      // 1. Gọi API mới để lấy form đã commit
      const committedRes = await axios.get(
        `http://localhost:8080/api/nurse/health-check-forms-commit/program/${programId}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      const committedForms = committedRes.data || [];

      if (committedForms.length === 0) {
        await Swal.fire("Không có phiếu nào đã commit!", "", "info");
        return;
      }

      // 2. Mapping dữ liệu sang editableResults
      const resultForms = committedForms.map((form) => ({
        level: "",
        note: "",
        vision: "",
        hearing: "",
        weight: null,
        height: null,
        dentalStatus: "",
        bloodPressure: "",
        heartRate: null,
        generalCondition: "",
        isChecked: false,
        healthCheckFormId: form.id,
        studentDTO: form.studentDTO,
      }));

      // 3. Cập nhật UI
      setEditableResults(resultForms);
      setHealthCheckResults(resultForms); // nếu cần
      setSelectedProgramId(programId);
      setActiveTab("result");
      setShowResultPage(true);
    } catch (error) {
      console.error(error);
      Swal.fire(
        "Lỗi",
        error?.response?.data?.message || "Không thể tạo kết quả!",
        "error"
      );
    } finally {
      setHealthCheckResultsLoading(false);
    }
  };

  const handleResultChange = (value, idx, field) => {
    setStudentsForResult((prev) =>
      prev.map((item, i) => (i === idx ? { ...item, [field]: value } : item))
    );
  };

  const handleUpdate = async (values) => {
    setLoading(true);
    const token = localStorage.getItem("token");
    const admin = JSON.parse(localStorage.getItem("users"));
    const adminId = admin?.id; // Thêm dòng này
    try {
      await axios.put(
        `http://localhost:8080/api/admin/health-check-program/${program.id}`,
        {
          healthCheckName: values.healthCheckName,
          description: values.description,
          startDate: values.startDate.format("YYYY-MM-DD"),
          dateSendForm: values.dateSendForm.format("YYYY-MM-DD"),
          location: values.location,
          nurseId: values.nurseId,
          adminId: adminId, // Sửa lại dòng này
          classIds: values.classIds,
          status: "NOT_STARTED",
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      await Swal.fire({
        icon: "success",
        title: "Thành công!",
        text: "Cập nhật chương trình thành công!",
        showConfirmButton: false,
        timer: 1500,
      });
      setCreateVisible(false);
      setEditMode(false);
      fetchProgram();
    } catch (error) {
      if (error.response && error.response.status === 400) {
        const errorMessage = error.response.data.message || "Đã xảy ra lỗi!";
        Swal.fire({
          icon: "error",
          title: "Lỗi!",
          text: errorMessage,
          confirmButtonColor: "#3085d6",
        });
      } else {
        message.error("Cập nhật chương trình thất bại!");
      }
    } finally {
      setLoading(false);
    }
  };

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
          `http://localhost:8080/api/admin/health-check-program/${programId}`,
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

  // Lọc danh sách theo tên chương trình và ngày tiêm
  const filteredPrograms = programs.filter((program) => {
    const matchName = program.healthCheckName
      ?.toLowerCase()
      .includes(searchTerm.toLowerCase());
    const matchDate = filterDate
      ? dayjs(program.startDate).isSame(filterDate, "day")
      : true;
    const matchStatus = filterStatus ? program.status === filterStatus : true;
    return matchName && matchDate && matchStatus;
  });

  // Thêm hàm lấy màu theo trạng thái
  const getStatusColor = (status) => {
    switch (status) {
      case "NOT_STARTED":
        return "default";
      case "ON_GOING":
        return "blue";
      case "COMPLETED":
        return "green";
      case "FORM_SENT":
        return "gold";
      case "GENERATED_RESULT":
        return "red";
      default:
        return "default";
    }
  };
  const getStatusText = (status) => {
    switch (status) {
      case "NOT_STARTED":
        return "Chưa bắt đầu";
      case "ON_GOING":
        return "Đang diễn ra";
      case "COMPLETED":
        return "Đã hoàn thành";
      case "FORM_SENT":
        return "Đã gửi biểu mẫu";
      case "GENERATED_RESULT":
        return "Kết quả được tạo";
      default:
        return status;
    }
  };

  const handleViewResult = async (programId) => {
    setHealthCheckResultsLoading(true);
    setIsViewResult(true);

    const token = localStorage.getItem("token");

    try {
      const res = await axios.get(
        `http://localhost:8080/api/nurse/view-health-check-result-by-programId/${programId}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      // ✅ Convert đúng dạng bảng cần
      const convertedResults = res.data
        .map((form) => {
          const result = form.healthCheckResultDTO || {};
          return {
            healthResultId: result.healthResultId,
            vision: result.vision || "",
            hearing: result.hearing || "",
            weight: result.weight || null,
            height: result.height || null,
            dentalStatus: result.dentalStatus || "",
            bloodPressure: result.bloodPressure || "",
            heartRate: result.heartRate || "",
            generalCondition: result.generalCondition || "",
            note: result.note || "",
            isChecked: result.isChecked || false,
            healthCheckFormId: form.id, // dùng làm rowKey
            studentDTO: form.studentDTO,
          };
        })
        .sort((a, b) => {
          // Đưa học sinh có healthResultId lên trước
          if (a.healthResultId && !b.healthResultId) return -1;
          if (!a.healthResultId && b.healthResultId) return 1;
          return 0;
        });

      setEditableResults(convertedResults);
      setHealthCheckResults(convertedResults);
      setSelectedProgramId(programId);
      setActiveTab("result");
      setShowResultPage(true);
    } catch {
      setHealthCheckResults([]);
    } finally {
      setHealthCheckResultsLoading(false);
    }
  };

  const handleExportExcel = async (programId) => {
    const token = localStorage.getItem("token");
    try {
      const response = await axios.post(
        `http://localhost:8080/api/admin/export-health-check-result-excel-by-health-check-program/${programId}`,
        {}, // POST body nếu không có thì để {}
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
          responseType: "blob", // Đặt ở đây
        }
      );

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", `Ket_qua_kham_${programId}.xlsx`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error("Export Excel Error:", error);
      Swal.fire("Lỗi", "Không thể xuất file Excel!", "error");
    }
  };

  // Lọc và phân trang
  const pagedPrograms = filteredPrograms
    // .sort((a, b) => new Date(b.startDate) - new Date(a.startDate))
    .sort((a, b) => b.id - a.id) // (Hoặc sắp theo ID nếu startDate không ổn định)
    .slice((currentPage - 1) * pageSize, currentPage * pageSize);

  if (!programs.length) return <div>Đang tải...</div>;

  return (
    <div
      style={{
        padding: 24,
        marginLeft: 220,
        transition: "margin 0.2s",
        maxWidth: "100vw",
      }}
    >
      <Tabs
        activeKey={activeTab}
        onChange={setActiveTab}
        items={[
          {
            key: "program",
            label: "Chương trình khám định kỳ",
            children: (
              <React.Fragment>
                <div
                  style={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                    marginBottom: 16,
                  }}
                >
                  <h2 style={{ margin: 0 }}>
                    <span style={{ color: "#52c41a", marginRight: 8 }}>🛡️</span>
                    Quản Lý Chương Trình Khám Định Kỳ
                  </h2>
                  <div style={{ display: "flex", gap: 12 }}>
                    <Input
                      placeholder="Tìm kiếm tên chương trình..."
                      prefix={<SearchOutlined />}
                      value={searchTerm}
                      onChange={(e) => setSearchTerm(e.target.value)}
                      allowClear
                      style={{ width: 220, background: "#fff" }}
                    />
                    <DatePicker
                      placeholder="Lọc theo ngày khám"
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
                    {/* Ẩn nút lên lịch nếu là NURSE */}
                    {userRole === "ADMIN" && (
                      <Button
                        type="primary"
                        icon={<PlusOutlined />}
                        style={{ background: "#21ba45", border: "none" }}
                        onClick={() => setCreateVisible(true)}
                      >
                        Lên lịch khám định kỳ
                      </Button>
                    )}
                  </div>
                </div>
                {pagedPrograms.map((program) => (
                  <Card
                    key={program.id}
                    style={{
                      background: "#f6fcf7",
                      borderRadius: 10,
                      border: "1px solid #e6f4ea",
                      width: "calc(100vw - 260px)",
                      minWidth: 1200,
                      margin: "0 auto",
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
                          {program.healthCheckName}
                        </div>
                        <div style={{ color: "#555", marginBottom: 2 }}>
                          Ngày bắt đầu: {program.startDate}
                        </div>
                        <div style={{ color: "#555", marginBottom: 2 }}>
                          Ngày gửi biểu mẫu: {program.dateSendForm}
                        </div>
                        <div style={{ color: "#555", marginBottom: 2 }}>
                          Địa điểm: {program.location}
                        </div>
                        <div style={{ color: "#555", marginBottom: 2 }}>
                          Y tá phụ trách: {program.nurseDTO?.fullName}
                        </div>
                      </div>
                      <Tag
                        color={getStatusColor(program.status)}
                        style={{ fontSize: 14, marginTop: 4 }}
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
                            {program.healthCheckFormDTOs?.length ?? 0}
                          </div>
                          <div style={{ color: "#888", fontWeight: 500 }}>
                            Tổng học sinh dự kiến tham gia
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
                            {program.healthCheckFormDTOs?.filter(
                              (f) => f.commit
                            )?.length ?? 0}
                          </div>
                          <div style={{ color: "#888", fontWeight: 500 }}>
                            Đã xác nhận tham gia
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
                          style={{ marginRight: 8 }}
                          onClick={() => {
                            const freshProgram = programs.find(
                              (p) => p.id === program.id
                            );
                            setProgram(freshProgram || program);
                            setDetailVisible(true);
                          }}
                        >
                          Xem chi tiết
                        </Button>

                        {program.status === "NOT_STARTED" &&
                        userRole === "ADMIN" ? (
                          <Button
                            type="primary"
                            style={{ background: "#21ba45", border: "none" }}
                            onClick={async () => {
                              const confirm = await Swal.fire({
                                title: "Xác nhận bắt đầu chương trình?",
                                text: "Sau khi bắt đầu, chương trình sẽ chuyển sang trạng thái 'Đang diễn ra'.",
                                icon: "question",
                                showCancelButton: true,
                                confirmButtonColor: "#1890ff",
                                cancelButtonColor: "#aaa",
                                confirmButtonText: "Bắt đầu",
                                cancelButtonText: "Hủy",
                              });

                              if (!confirm.isConfirmed) return;

                              const token = localStorage.getItem("token");
                              try {
                                await axios.patch(
                                  `http://localhost:8080/api/admin/health-check-program/${program.id}?status=ON_GOING`,
                                  {},
                                  {
                                    headers: {
                                      Authorization: `Bearer ${token}`,
                                    },
                                  }
                                );
                                await Swal.fire({
                                  icon: "success",
                                  title: "Thành công!",
                                  text: "Chương trình đã được bắt đầu.",
                                  showConfirmButton: false,
                                  timer: 1500,
                                });
                                fetchProgram();
                              } catch (error) {
                                await Swal.fire({
                                  icon: "error",
                                  title: "Thất bại!",
                                  text: "Không thể bắt đầu chương trình!",
                                  confirmButtonColor: "#3085d6",
                                });
                              }
                            }}
                          >
                            Bắt đầu chương trình
                          </Button>
                        ) : program.status === "ON_GOING" &&
                          userRole === "NURSE" ? (
                          <Button
                            style={{
                              marginLeft: 8,
                              background: "#faad14",
                              color: "#fff",
                              border: "none",
                            }}
                            onClick={() => handleSendNotification(program.id)}
                          >
                            Gửi thông báo
                          </Button>
                        ) : program.status === "FORM_SENT" &&
                          userRole === "NURSE" ? (
                          <Button
                            style={{
                              marginLeft: 8,
                              background: "#13c2c2",
                              color: "#fff",
                              border: "none",
                            }}
                            onClick={() => handleCreateResult(program.id)}
                          >
                            Tạo kết quả
                          </Button>
                        ) : program.status === "GENERATED_RESULT" &&
                          (userRole === "ADMIN" || userRole === "NURSE") ? (
                          <>
                            {userRole === "NURSE" && (
                              <Button
                                style={{
                                  marginLeft: 8,
                                  background: "#ff7a45",
                                  color: "#fff",
                                  border: "none",
                                }}
                                onClick={() => handleEditResult(program.id)}
                              >
                                Chỉnh sửa kết quả
                              </Button>
                            )}

                            <Button
                              type="primary"
                              style={{
                                marginLeft: 8,
                                background: "#722ed1",
                                border: "none",
                              }}
                              onClick={() => handleViewResult(program.id)}
                            >
                              Xem kết quả
                            </Button>

                            {userRole === "ADMIN" && (
                              <Button
                                style={{
                                  marginLeft: 8,
                                  background: "#52c41a",
                                  color: "#fff",
                                  border: "none",
                                }}
                                onClick={async () => {
                                  const confirm = await Swal.fire({
                                    title: "Xác nhận hoàn thành chương trình?",
                                    text: "Bạn chắc chắn muốn chuyển sang trạng thái 'Đã hoàn thành'?",
                                    icon: "question",
                                    showCancelButton: true,
                                    confirmButtonColor: "#4caf50",
                                    cancelButtonColor: "#aaa",
                                    confirmButtonText: "Hoàn thành",
                                    cancelButtonText: "Hủy",
                                  });

                                  if (!confirm.isConfirmed) return;

                                  const token = localStorage.getItem("token");
                                  try {
                                    await axios.patch(
                                      `http://localhost:8080/api/admin/health-check-program/${program.id}?status=COMPLETED`,
                                      {},
                                      {
                                        headers: {
                                          Authorization: `Bearer ${token}`,
                                        },
                                      }
                                    );

                                    await Swal.fire({
                                      icon: "success",
                                      title: "Thành công!",
                                      text: "Chương trình đã chuyển sang trạng thái hoàn thành.",
                                      showConfirmButton: false,
                                      timer: 1500,
                                    });

                                    fetchProgram(); // Cập nhật lại UI
                                  } catch (error) {
                                    Swal.fire({
                                      icon: "error",
                                      title: "Lỗi!",
                                      text: "Không thể hoàn thành chương trình.",
                                    });
                                  }
                                }}
                              >
                                Hoàn thành chương trình
                              </Button>
                            )}
                          </>
                        ) : program.status === "COMPLETED" ? (
                          <>
                            <Button
                              type="primary"
                              style={{
                                marginLeft: 8,
                                background: "#722ed1",
                                border: "none",
                              }}
                              onClick={() => handleViewResult(program.id)}
                            >
                              Xem kết quả
                            </Button>

                            {userRole === "ADMIN" && (
                              <Button
                                style={{
                                  marginLeft: 8,
                                  background: "#2f54eb",
                                  color: "#fff",
                                  border: "none",
                                }}
                                onClick={() => handleExportExcel(program.id)}
                              >
                                Xuất Excel
                              </Button>
                            )}
                          </>
                        ) : null}
                      </div>
                      {/* Ẩn nút Sửa, Xóa nếu là NURSE */}
                      {userRole === "ADMIN" &&
                        program.status === "NOT_STARTED" && (
                          <div
                            style={{
                              display: "flex",
                              gap: 8,
                              marginLeft: "auto",
                            }}
                          >
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
                            <Button
                              danger
                              type="primary"
                              onClick={() => handleDelete(program.id)}
                            >
                              Xóa
                            </Button>
                          </div>
                        )}
                    </div>
                  </Card>
                ))}
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
                  title="Chi tiết chương trình khám sức khỏe"
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
                      <Descriptions.Item label="ID">
                        {program.id}
                      </Descriptions.Item>
                      <Descriptions.Item label="Tên chương trình">
                        {program.healthCheckName}
                      </Descriptions.Item>
                      <Descriptions.Item label="Mô tả">
                        {program.description}
                      </Descriptions.Item>
                      <Descriptions.Item label="Ngày bắt đầu">
                        {program.startDate}
                      </Descriptions.Item>
                      <Descriptions.Item label="Ngày gửi biểu mẫu">
                        {program.dateSendForm}
                      </Descriptions.Item>
                      <Descriptions.Item label="Địa điểm">
                        {program.location}
                      </Descriptions.Item>

                      <Descriptions.Item label="Trạng thái">
                        <Tag
                          color={getStatusColor(program.status)}
                          style={{ fontSize: 14 }}
                        >
                          {getStatusText(program.status)}
                        </Tag>
                      </Descriptions.Item>
                      <Descriptions.Item label="Y tá phụ trách">
                        {program.nurseDTO?.fullName} (ID: {program.nurseDTO?.id}
                        )
                      </Descriptions.Item>
                      <Descriptions.Item label="Admin tạo">
                        {program.adminDTO?.fullName} (ID: {program.adminDTO?.id}
                        )
                      </Descriptions.Item>
                      <Descriptions.Item label="Lớp tham gia">
                        {program.participateClasses &&
                        program.participateClasses.length > 0
                          ? program.participateClasses
                              .map((p) => p.classDTO?.className || "")
                              .filter(Boolean)
                              .join(", ")
                          : "-"}
                      </Descriptions.Item>
                    </Descriptions>
                  )}
                </Modal>
                <HealthCheckProgramModal
                  open={createVisible}
                  onCancel={() => {
                    setCreateVisible(false);
                    setEditMode(false);
                    setProgram(null);
                  }}
                  onFinish={editMode ? handleUpdate : handleCreate}
                  loading={loading}
                  editMode={editMode}
                  program={program}
                  nurseOptions={nurseOptions}
                  classOptions={classOptions}
                />
              </React.Fragment>
            ),
          },
          {
            key: "result",
            label: "Kết quả chương trình",
            children: (
              <div>
                {/* Thêm ô nhập tìm kiếm ở đây */}
                <div style={{ marginBottom: 16, textAlign: "right" }}>
                  <Input
                    placeholder="Tìm kiếm tên hoặc mã học sinh..."
                    value={studentSearch}
                    onChange={(e) => setStudentSearch(e.target.value)}
                    style={{ width: 300 }}
                    allowClear
                  />
                </div>
                <div
                  style={{
                    background: "#fff",
                    borderRadius: 10,
                    padding: 24,
                    minWidth: 1630,
                    maxWidth: "calc(100vw - 260px)",
                    margin: "0 auto",
                    boxShadow: "0 2px 8px rgba(0,0,0,0.05)",
                    overflowX: "auto",
                  }}
                >
                  <Table
                    columns={[
                      {
                        title: "Mã học sinh",
                        dataIndex: ["studentDTO", "id"],
                        key: "studentId",
                        align: "center",
                        width: 90, // Giảm chiều rộng
                        render: (_, record) =>
                          record.studentDTO?.studentId || "-",
                      },
                      {
                        title: "Tên học sinh",
                        dataIndex: ["studentDTO", "fullName"],
                        key: "studentName",
                        align: "center",
                        render: (_, record) =>
                          record.studentDTO?.fullName || "-",
                      },
                      {
                        title: "Lớp",
                        dataIndex: ["studentDTO", "classDTO", "className"],
                        key: "className",
                        align: "center",
                        render: (_, record) =>
                          record.studentDTO?.classDTO?.className || "-",
                      },
                      {
                        title: "Chiều cao (cm)",
                        dataIndex: "height",
                        key: "height",
                        align: "center",
                        render: (value, record) =>
                          isViewResult ? (
                            value ?? "-"
                          ) : (
                            <Input
                              value={value ?? ""}
                              onChange={(e) =>
                                handleEditChange(
                                  e.target.value,
                                  record,
                                  "height"
                                )
                              }
                              style={{ width: 80 }}
                              status={
                                value &&
                                (isNaN(value) ||
                                  Number(value) < 100 ||
                                  Number(value) > 200)
                                  ? "error"
                                  : ""
                              }
                              placeholder="100-200"
                            />
                          ),
                      },

                      {
                        title: "Cân nặng (kg)",
                        dataIndex: "weight",
                        key: "weight",
                        align: "center",
                        render: (value, record) =>
                          isViewResult ? (
                            value ?? "-"
                          ) : (
                            <Input
                              value={value ?? ""}
                              onChange={(e) =>
                                handleEditChange(
                                  e.target.value,
                                  record,
                                  "weight"
                                )
                              }
                              style={{ width: 80 }}
                              status={
                                value &&
                                (!/^\d+$/.test(value) ||
                                  Number(value) < 15 ||
                                  Number(value) > 120)
                                  ? "error"
                                  : ""
                              }
                              placeholder="15-120"
                            />
                          ),
                      },
                      {
                        title: "Thị lực (1-10/10)",
                        dataIndex: "vision",
                        key: "vision",
                        align: "center",
                        render: (value, record) =>
                          isViewResult ? (
                            value ?? "-"
                          ) : (
                            <Input
                              value={value ?? ""}
                              onChange={(e) =>
                                handleEditChange(
                                  e.target.value,
                                  record,
                                  "vision"
                                )
                              }
                              style={{ width: 80 }}
                              status={
                                value && !/^([1-9]|10)\/10$/.test(value)
                                  ? "error"
                                  : ""
                              }
                              placeholder="1/10-10/10"
                            />
                          ),
                      },
                      {
                        title: "Thính lực",
                        dataIndex: "hearing",
                        key: "hearing",
                        align: "center",
                        render: (value, record) =>
                          isViewResult ? (
                            value || "-"
                          ) : (
                            <Input.TextArea
                              value={value ?? ""}
                              onChange={(e) =>
                                handleEditChange(
                                  e.target.value,
                                  record,
                                  "hearing"
                                )
                              }
                              style={{ width: 80, minHeight: 32 }}
                              autoSize={{ minRows: 1, maxRows: 3 }}
                            />
                          ),
                      },
                      {
                        title: "Răng miệng",
                        dataIndex: "dentalStatus",
                        key: "dentalStatus",
                        align: "center",
                        render: (value, record) =>
                          isViewResult ? (
                            value || "-"
                          ) : (
                            <Input
                              value={value ?? ""}
                              onChange={(e) =>
                                handleEditChange(
                                  e.target.value,
                                  record,
                                  "dentalStatus"
                                )
                              }
                              style={{ width: 120 }}
                            />
                          ),
                      },
                      {
                        title: "Huyết áp",
                        dataIndex: "bloodPressure",
                        key: "bloodPressure",
                        align: "center",
                        render: (value, record) =>
                          isViewResult ? (
                            value || "-"
                          ) : (
                            <Input
                              value={value ?? ""}
                              onChange={(e) =>
                                handleEditChange(
                                  e.target.value,
                                  record,
                                  "bloodPressure"
                                )
                              }
                              style={{ width: 100 }}
                            />
                          ),
                      },
                      {
                        title: "Nhịp tim",
                        dataIndex: "heartRate",
                        key: "heartRate",
                        align: "center",
                        render: (value, record) =>
                          isViewResult ? (
                            value || "-"
                          ) : (
                            <Input
                              value={value ?? ""}
                              onChange={(e) =>
                                handleEditChange(
                                  e.target.value,
                                  record,
                                  "heartRate"
                                )
                              }
                              style={{ width: 100 }}
                            />
                          ),
                      },
                      {
                        title: "Tình trạng chung",
                        dataIndex: "generalCondition",
                        key: "generalCondition",
                        align: "center",
                        render: (value, record) =>
                          isViewResult ? (
                            value || "-"
                          ) : (
                            <Input
                              value={value ?? ""}
                              onChange={(e) =>
                                handleEditChange(
                                  e.target.value,
                                  record,
                                  "generalCondition"
                                )
                              }
                              style={{ width: 110 }}
                            />
                          ),
                      },
                      {
                        title: "Ghi chú",
                        dataIndex: "note",
                        key: "note",
                        align: "center",
                        render: (value, record) =>
                          isViewResult ? (
                            value || "-"
                          ) : (
                            <Input.TextArea
                              value={value ?? ""}
                              onChange={(e) =>
                                handleEditChange(e.target.value, record, "note")
                              }
                              style={{ width: 120, minHeight: 32 }}
                              autoSize={{ minRows: 1, maxRows: 3 }}
                            />
                          ),
                      },

                      {
                        title: "Đã khám?",
                        dataIndex: "isChecked",
                        key: "isChecked",
                        align: "center",
                        render: (value, record) =>
                          isViewResult ? (
                            value ? (
                              <CheckSquareTwoTone
                                twoToneColor="#52c41a"
                                style={{ fontSize: 22, cursor: "not-allowed" }}
                              />
                            ) : (
                              <BorderOutlined
                                style={{
                                  fontSize: 22,
                                  color: "#aaa",
                                  cursor: "not-allowed",
                                }}
                              />
                            )
                          ) : value ? (
                            <CheckSquareTwoTone
                              twoToneColor="#52c41a"
                              style={{ fontSize: 22, cursor: "pointer" }}
                              onClick={() =>
                                handleEditChange(false, record, "isChecked")
                              }
                            />
                          ) : (
                            <BorderOutlined
                              style={{
                                fontSize: 22,
                                color: "#aaa",
                                cursor: "pointer",
                              }}
                              onClick={() =>
                                handleEditChange(true, record, "isChecked")
                              }
                            />
                          ),
                      },
                      {
                        title: "Thao tác",
                        key: "action",
                        align: "center",
                        render: (_, record) =>
                          !isViewResult && (
                            <Button
                              type="primary"
                              onClick={() => {
                                setCurrentEditRecord(record);
                                setEditModalVisible(true);
                              }}
                            >
                              Ghi nhận
                            </Button>
                          ),
                      },
                    ]}
                    dataSource={editableResults.filter(
                      (item) =>
                        (item.studentDTO?.fullName || "")
                          .toLowerCase()
                          .includes(studentSearch.toLowerCase()) ||
                        (item.studentDTO?.id + "").includes(studentSearch)
                    )}
                    loading={healthCheckResultsLoading}
                    rowKey="healthCheckFormId"
                    bordered
                    size="middle"
                    style={{
                      paddingLeft: 5,
                      minWidth: 1350,
                      borderRadius: 12,
                      overflow: "hidden",
                      background: "#fff",
                      boxShadow: "0 2px 8px rgba(33,186,69,0.08)",
                    }}
                    locale={{
                      emptyText: (
                        <div
                          style={{
                            padding: 32,
                            color: "#888",
                            fontSize: 18,
                            textAlign: "center",
                          }}
                        >
                          <img
                            src="https://cdn-icons-png.flaticon.com/512/4076/4076549.png"
                            alt="No data"
                            width={64}
                            style={{ opacity: 0.5, marginBottom: 8 }}
                          />
                          <div>Không có dữ liệu</div>
                        </div>
                      ),
                    }}
                    pagination={{ pageSize: 8 }}
                    scroll={{ x: "max-content" }} // Thêm dòng này để bật thanh cuộn ngang
                  />
                </div>
              </div>
            ),
          },
        ]}
      />
      <Modal
        title="Kết quả khám định kỳ"
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
        title="Ghi nhận kết quả khám"
        open={editModalVisible}
        width={700} // 👈 giới hạn chiều rộng modal cho gọn hơn
        onCancel={() => {
          setEditModalVisible(false);
          setCurrentEditRecord(null);
        }}
        onOk={async () => {
          const r = currentEditRecord;

          // Validation...
          if (
            !r.height ||
            isNaN(r.height) ||
            r.height < 100 ||
            r.height > 200
          ) {
            Swal.fire("Lỗi", "Chiều cao phải từ 100 đến 200!", "error");
            return;
          }
          if (!r.weight || isNaN(r.weight) || r.weight < 15 || r.weight > 120) {
            Swal.fire("Lỗi", "Cân nặng phải từ 15 đến 120!", "error");
            return;
          }
          if (!r.vision || !/^([1-9]|10)\/10$/.test(r.vision)) {
            Swal.fire("Lỗi", "Thị lực không hợp lệ!", "error");
            return;
          }

          try {
            const token = localStorage.getItem("token");

            // Gửi kết quả
            await axios.post(
              `http://localhost:8080/api/nurse/create-healthCheckResult-byProgram-${selectedProgramId}`,
              {
                vision: r.vision,
                hearing: r.hearing,
                weight: r.weight,
                height: r.height,
                dentalStatus: r.dentalStatus,
                bloodPressure: r.bloodPressure,
                heartRate: r.heartRate,
                generalCondition: r.generalCondition,
                note: r.note,
                isChecked: r.isChecked,
                healthCheckFormId:
                  r.healthCheckFormDTO?.id ||
                  r.healthCheckFormId ||
                  r.healthCheckForm?.id,
              },
              {
                headers: { Authorization: `Bearer ${token}` },
              }
            );

            // ⚠️ GỌI LẠI API ĐỂ LOAD DANH SÁCH KẾT QUẢ ĐÃ TẠO
            // Sau khi post ghi nhận thành công
            const res = await axios.get(
              `http://localhost:8080/api/nurse/health-check-result/program/${selectedProgramId}`,
              {
                headers: { Authorization: `Bearer ${token}` },
              }
            );

            // Map lại
            const convertedResults = res.data.map((form) => {
              const result = form.healthCheckResultDTO || {};
              return {
                healthResultId: result.healthResultId,
                vision: result.vision || "",
                hearing: result.hearing || "",
                weight: result.weight || null,
                height: result.height || null,
                dentalStatus: result.dentalStatus || "",
                bloodPressure: result.bloodPressure || "",
                heartRate: result.heartRate || "",
                generalCondition: result.generalCondition || "",
                note: result.note || "",
                isChecked: result.isChecked || false,
                healthCheckFormId: form.id,
                studentDTO: form.studentDTO,
              };
            });

            setEditableResults(convertedResults);
            setHealthCheckResults(convertedResults);

            Swal.fire("Thành công", "Đã ghi nhận kết quả!", "success");
            setEditModalVisible(false);
            setCurrentEditRecord(null);
          } catch (err) {
            console.error(err);
            Swal.fire("Lỗi", "Không thể ghi nhận kết quả!", "error");
          }
        }}
      >
        {currentEditRecord && (
          <Form layout="vertical">
            <Row gutter={16}>
              <Col span={12}>
                <Form.Item label="Chiều cao (cm)">
                  <Input
                    value={currentEditRecord.height}
                    onChange={(e) =>
                      setCurrentEditRecord({
                        ...currentEditRecord,
                        height: e.target.value,
                      })
                    }
                    placeholder="VD: 150"
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item label="Cân nặng (kg)">
                  <Input
                    value={currentEditRecord.weight}
                    onChange={(e) =>
                      setCurrentEditRecord({
                        ...currentEditRecord,
                        weight: e.target.value,
                      })
                    }
                    placeholder="VD: 40"
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item label="Thị lực (1-10/10)">
                  <Input
                    value={currentEditRecord.vision}
                    onChange={(e) =>
                      setCurrentEditRecord({
                        ...currentEditRecord,
                        vision: e.target.value,
                      })
                    }
                    placeholder="VD: 10/10"
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item label="Thính lực">
                  <Input
                    value={currentEditRecord.hearing}
                    onChange={(e) =>
                      setCurrentEditRecord({
                        ...currentEditRecord,
                        hearing: e.target.value,
                      })
                    }
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item label="Răng miệng">
                  <Input
                    value={currentEditRecord.dentalStatus}
                    onChange={(e) =>
                      setCurrentEditRecord({
                        ...currentEditRecord,
                        dentalStatus: e.target.value,
                      })
                    }
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item label="Huyết áp">
                  <Input
                    value={currentEditRecord.bloodPressure}
                    onChange={(e) =>
                      setCurrentEditRecord({
                        ...currentEditRecord,
                        bloodPressure: e.target.value,
                      })
                    }
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item label="Nhịp tim">
                  <Input
                    value={currentEditRecord.heartRate}
                    onChange={(e) =>
                      setCurrentEditRecord({
                        ...currentEditRecord,
                        heartRate: e.target.value,
                      })
                    }
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item label="Tình trạng chung">
                  <Input
                    value={currentEditRecord.generalCondition}
                    onChange={(e) =>
                      setCurrentEditRecord({
                        ...currentEditRecord,
                        generalCondition: e.target.value,
                      })
                    }
                  />
                </Form.Item>
              </Col>
              

              <Col span={24}>
                <Form.Item label="Ghi chú">
                  <Input.TextArea
                    value={currentEditRecord.note}
                    onChange={(e) =>
                      setCurrentEditRecord({
                        ...currentEditRecord,
                        note: e.target.value,
                      })
                    }
                    autoSize={{ minRows: 2, maxRows: 4 }}
                  />
                </Form.Item>
              </Col>

              <Col span={24}>
                <Form.Item>
                  <Checkbox
                    checked={currentEditRecord.isChecked}
                    onChange={(e) =>
                      setCurrentEditRecord({
                        ...currentEditRecord,
                        isChecked: e.target.checked,
                      })
                    }
                  >
                    Đánh dấu đã khám
                  </Checkbox>
                </Form.Item>
              </Col>
              <Col span={24}>
                <Form.Item>
                  <Checkbox
                    checked={currentEditRecord.note === "Không tham gia khám"}
                    onChange={async (e) => {
                      const checked = e.target.checked;
                      if (checked) {
                        const confirm = await Swal.fire({
                          title: "Xác nhận?",
                          text: "Bạn chắc chắn muốn đánh dấu học sinh này là 'Không tham gia khám'?",
                          icon: "question",
                          showCancelButton: true,
                          confirmButtonText: "Đồng ý",
                          cancelButtonText: "Hủy",
                        });
                        if (!confirm.isConfirmed) return;
                      }
                      setCurrentEditRecord((prev) => ({
                        ...prev,
                        note: checked ? "Không tham gia khám" : "",
                      }));
                    }}
                  >
                    Đánh dấu không khám
                  </Checkbox>
                </Form.Item>
              </Col>
            </Row>
          </Form>
        )}
      </Modal>
    </div>
  );
};

export default HealthCheckProgramList;
