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
    setIsViewResult(false); // Đảm bảo dòng này luôn chạy
    const token = localStorage.getItem("token");
    try {
      const res = await axios.get(
        `http://localhost:8080/api/nurse/health-check-result/program/${programId}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setHealthCheckResults(res.data);
      setSelectedProgramId(programId);
      setActiveTab("result"); // Đảm bảo chuyển tab
      setShowResultPage(true);
    } catch (error) {
      Swal.fire("Lỗi", "Không thể lấy dữ liệu kết quả!", "error");
      setHealthCheckResults([]);
    } finally {
      setHealthCheckResultsLoading(false);
    }
  };

  const handleSendNotification = async (programId) => {
    const confirm = await Swal.fire({
      title: "Bạn có chắc muốn gửi thông báo?",
      text: "Thông báo sẽ được gửi đến tất cả học sinh trong chương trình này.",
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
      // Gửi thông báo
      await axios.post(
        `http://localhost:8080/api/nurse/create-health-check-form/${programId}`,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );
      await Swal.fire({
        icon: "success",
        title: "Gửi thông báo thành công!",
        showConfirmButton: false,
        timer: 1500,
      });
      // Chuyển trạng thái thành FORM_SENT
      await axios.patch(
        `http://localhost:8080/api/admin/health-check-program/${programId}?status=FORM_SENT`,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );

      // Cập nhật UI
      setSentNotificationIds((prev) => [...prev, programId]);
      fetchProgram();
    } catch (error) {
      Swal.fire({
        icon: "error",
        title: "Gửi thông báo thất bại!",
        text: error?.response?.data?.message || "",
        showConfirmButton: true,
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
      // 1. Gọi API tạo kết quả
      await axios.post(
        `http://localhost:8080/api/nurse/create-healthCheckResult-byProgram-/${programId}`,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );

      // 2. Gọi lại API lấy danh sách kết quả
      const res = await axios.get(
        `http://localhost:8080/api/nurse/health-check-result/program/${programId}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      // 3. Cập nhật state
      setHealthCheckResults(res.data);
      setEditableResults(res.data);
      setSelectedProgramId(programId);
      setActiveTab("result");
      setShowResultPage(true);

      // 4. Nếu là y tá, cập nhật trạng thái
      if (userRole === "NURSE") {
        setPrograms((prev) =>
          prev.map((p) =>
            p.id === programId ? { ...p, status: "COMPLETED" } : p
          )
        );
      }
    } catch (error) {
      Swal.fire("Lỗi", "Không thể tạo kết quả!", "error");
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
    setIsViewResult(true); // Chế độ xem

    const token = localStorage.getItem("token");

    try {
      const res = await axios.get(
        `http://localhost:8080/api/nurse/health-check-result/program/${programId}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      // ✅ Chỉ setState sau khi có dữ liệu
      setHealthCheckResults(res.data);
      setEditableResults(res.data);
      setSelectedProgramId(programId);

      // ✅ Sau khi state được cập nhật mới mở tab
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
      const response = await axios.get(
        `http://localhost:8080/api/admin/export-health-check-result-excel-by-health-check-program/${programId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
          responseType: "blob", // QUAN TRỌNG để nhận file binary
        }
      );

      // Tạo URL và link để tải file
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", `Ket_qua_kham_${programId}.xlsx`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url); // Giải phóng
    } catch (error) {
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
                        {/* <div style={{ color: "#555", marginBottom: 2 }}>
                          Mô tả: {program.description}
                        </div> */}
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
                        {/* <div style={{ color: "#555", marginBottom: 2 }}>
                          Lớp tham gia:&nbsp;
                          {program.participateClasses &&
                          program.participateClasses.length > 0
                            ? program.participateClasses
                                .map((p) =>
                                  p.classDTO?.className
                                    ? `${p.classDTO.className} (GV: ${
                                        p.classDTO.teacherName || "-"
                                      }, Sĩ số: ${p.classDTO.quantity || "-"})`
                                    : ""
                                )
                                .filter(Boolean)
                                .join(", ")
                            : "-"}
                        </div> */}
                        {/* <div style={{ color: "#555", marginBottom: 2 }}>
                          Admin tạo: {program.adminDTO?.fullName} (ID:{" "}
                          {program.adminDTO?.id})
                        </div> */}
                        {/* <div style={{ color: "#555", marginBottom: 2 }}>
                          Trạng thái: {getStatusText(program.status)}
                        </div> */}
                        {/* <div style={{ color: "#555", marginBottom: 2 }}>
                          Ghi chú: {program.note || "-"}
                        </div> */}
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
                          onClick={() => {
                            // Tìm lại program từ danh sách mới nhất
                            const freshProgram = programs.find(
                              (p) => p.id === program.id
                            );
                            setProgram(freshProgram || program);
                            setDetailVisible(true);
                          }}
                        >
                          Xem chi tiết
                        </Button>
                        <Button
                          type="primary"
                          style={{
                            marginLeft: 8,
                            background: "#21ba45",
                            border: "none",
                          }}
                          onClick={async () => {
                            const token = localStorage.getItem("token");
                            try {
                              await axios.patch(
                                `http://localhost:8080/api/admin/health-check-program/${program.id}?status=ON_GOING`,
                                {},
                                {
                                  headers: { Authorization: `Bearer ${token}` },
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
                          disabled={program.status !== "NOT_STARTED"}
                        >
                          Bắt đầu chương trình
                        </Button>

                        <Button
                          type="primary"
                          style={{
                            background: "#00bcd4",
                            border: "none",
                            marginLeft: 8,
                          }}
                          onClick={() => handleViewResult(program.id)} // CHỈ gọi 1 dòng duy nhất
                        >
                          Xem kết quả
                        </Button>

                        <Button
                          type="default"
                          style={{
                            marginLeft: 8,
                            background: "#1976d2",
                            color: "#fff",
                            border: "none",
                          }}
                          onClick={() => handleCreateResult(program.id)}
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
                          onClick={() => handleEditResult(program.id)}
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
                            cursor: "pointer",
                          }}
                          onClick={() => handleSendNotification(program.id)}
                        >
                          Gửi thông báo
                        </Button>
                        {program.status === "COMPLETED" && (
                          <Button
                            type="default"
                            style={{
                              marginLeft: 8,
                              background: "#4caf50",
                              color: "#fff",
                              border: "none",
                            }}
                            onClick={() => handleExportExcel(program.id)}
                          >
                            Xuất Excel
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
                                setProgram(program);
                                setEditMode(true);
                                setCreateVisible(true);
                              }}
                              disabled={userRole === "NURSE"}
                            >
                              Sửa
                            </Button>
                          )}
                          <Button
                            danger
                            type="primary"
                            onClick={() => handleDelete(program.id)}
                            disabled={
                              userRole === "NURSE" ||
                              program.status === "COMPLETED" ||
                              program.status === "ON_GOING"
                            }
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
                              .map((p) =>
                                p.classDTO?.className
                                  ? `${p.classDTO.className} (Sĩ số: ${
                                      p.classDTO.quantity || "-"
                                    })`
                                  : ""
                              )
                              .filter(Boolean)
                              .join(", ")
                          : "-"}
                      </Descriptions.Item>
                      {/* <Descriptions.Item label="Danh sách biểu mẫu">
                        {program.healthCheckFormDTOs &&
                        program.healthCheckFormDTOs.length > 0
                          ? program.healthCheckFormDTOs.map((form, idx) => (
                              <div key={form.id} style={{ marginBottom: 8 }}>
                                <b>Form ID:</b> {form.id} | <b>Student ID:</b>{" "}
                                {form.studentId} | <b>Parent ID:</b>{" "}
                                {form.parentId} | <b>Nurse ID:</b>{" "}
                                {form.nurseId} | <b>Ngày hết hạn:</b>{" "}
                                {form.expDate} | <b>Ghi chú:</b> {form.notes} |{" "}
                                <b>Đã xác nhận:</b>{" "}
                                {form.commit ? "Có" : "Không"}
                              </div>
                            ))
                          : "Không có"}
                      </Descriptions.Item> */}
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
                      // {
                      //   title: "Ngày sinh",
                      //   dataIndex: ["studentDTO", "dob"],
                      //   key: "dob",
                      //   align: "center",
                      //   render: (_, record) => record.studentDTO?.dob || "-",
                      // },
                      {
                        title: "Giới tính",
                        dataIndex: ["studentDTO", "gender"],
                        key: "gender",
                        align: "center",
                        render: (_, record) => record.studentDTO?.gender || "-",
                      },
                      {
                        title: "Y tá phụ trách",
                        dataIndex: ["nurseDTO", "fullName"],
                        key: "fullName",
                        align: "center",
                        render: (value, record) =>
                          isViewResult ? (
                            value
                          ) : (
                            <Select
                              value={value}
                              onChange={(val) =>
                                handleEditChange(val, record, "fullName")
                              }
                              style={{ width: 120 }}
                              options={nurseOptions}
                              placeholder="Chọn y tá"
                              disabled={isViewResult}
                            />
                          ),
                      },
                      {
                        title: "Chiều cao (cm)",
                        dataIndex: "height",
                        key: "height",
                        align: "center",
                        render: (value, record) => (
                          <Input
                            value={value ?? ""}
                            onChange={(e) =>
                              handleEditChange(e.target.value, record, "height")
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
                        render: (value, record) => (
                          <Input
                            value={value ?? ""}
                            onChange={(e) =>
                              handleEditChange(e.target.value, record, "weight")
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
                        render: (value, record) => (
                          <Input
                            value={value ?? ""}
                            onChange={(e) =>
                              handleEditChange(e.target.value, record, "vision")
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
                        render: (value, record) => (
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
                        render: (value, record) => (
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
                        render: (value, record) => (
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
                        render: (value, record) => (
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
                        render: (value, record) => (
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
                        render: (value, record) => (
                          <Input.TextArea
                            value={value ?? ""}
                            onChange={(e) =>
                              handleEditChange(e.target.value, record, "note")
                            }
                            disabled={isViewResult}
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
                              onClick={async () => {
                                // Validate dữ liệu trước khi lưu
                                if (
                                  !record.height ||
                                  isNaN(record.height) ||
                                  Number(record.height) < 100 ||
                                  Number(record.height) > 200
                                ) {
                                  Swal.fire(
                                    "Lỗi",
                                    "Chiều cao phải là số từ 100 đến 200!",
                                    "error"
                                  );
                                  return;
                                }
                                if (
                                  !record.weight ||
                                  isNaN(record.weight) ||
                                  Number(record.weight) < 15 ||
                                  Number(record.weight) > 120
                                ) {
                                  Swal.fire(
                                    "Lỗi",
                                    "Cân nặng phải là số từ 15 đến 120!",
                                    "error"
                                  );
                                  return;
                                }
                                if (
                                  !record.vision ||
                                  !/^([1-9]|10)\/10$/.test(record.vision)
                                ) {
                                  Swal.fire(
                                    "Lỗi",
                                    "Thị lực phải có dạng 1/10 - 10/10!",
                                    "error"
                                  );
                                  return;
                                }
                                // Nếu hợp lệ thì gọi API
                                const token = localStorage.getItem("token");
                                try {
                                  await axios.put(
                                    `http://localhost:8080/api/nurse/health-check-result/${record.healthResultId}`,
                                    {
                                      vision: record.vision,
                                      hearing: record.hearing,
                                      weight: record.weight,
                                      height: record.height,
                                      dentalStatus: record.dentalStatus,
                                      bloodPressure: record.bloodPressure,
                                      heartRate: record.heartRate,
                                      generalCondition: record.generalCondition,
                                      note: record.note,
                                      isChecked: record.isChecked,
                                      healthCheckFormId:
                                        record.healthCheckFormDTO?.id,
                                    },
                                    {
                                      headers: {
                                        Authorization: `Bearer ${token}`,
                                      },
                                    }
                                  );
                                  Swal.fire(
                                    "Thành công",
                                    "Đã lưu kết quả!",
                                    "success"
                                  );
                                } catch {
                                  Swal.fire(
                                    "Lỗi",
                                    "Không thể lưu kết quả!",
                                    "error"
                                  );
                                }
                              }}
                            >
                              Lưu
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
                    rowKey="healthResultId"
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
    </div>
  );
};

export default HealthCheckProgramList;
