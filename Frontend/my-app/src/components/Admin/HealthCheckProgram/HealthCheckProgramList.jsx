import React, { useEffect, useState } from "react";
import { Button, Card, Row, Col, Tag, Modal, Descriptions, Form, Input, DatePicker, message } from "antd";
import { PlusOutlined, SearchOutlined } from "@ant-design/icons";
import { Select } from "antd"; // Thêm dòng này
import axios from "axios";
import dayjs from "dayjs";
import Swal from "sweetalert2";

const HealthCheckProgramList = () => {
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

  useEffect(() => {
    fetchProgram();
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
      setPrograms(res.data);
    } catch (error) {
      setPrograms([]);
    }
  };

  const handleCreate = async (values) => {
    setLoading(true);
    const token = localStorage.getItem("token");
    try {
      await axios.post(
        "http://localhost:8080/api/admin/health-check-program",
        {
          healthCheckName: values.name,
          description: values.description,
          startDate: values.startDate.format("YYYY-MM-DD"),
          endDate: values.endDate.format("YYYY-MM-DD"),
          note: values.note,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      message.success("Tạo chương trình sức khỏe thành công!");
      setCreateVisible(false);
      fetchProgram();
    } catch (error) {
      if (
        error.response &&
        error.response.data &&
        error.response.data.message &&
        error.response.data.message.includes("Another health check program is still active")
      ) {
        message.error("Bạn không thể tạo chương trình mới khi còn chương trình chưa hoàn thành.");
      } else {
        message.error("Tạo chương trình sức khỏe thất bại!");
      }
    } finally {
      setLoading(false);
    }
  };

  const handleUpdate = async (values) => {
    setLoading(true);
    const token = localStorage.getItem("token");
    try {
      await axios.put(
        `http://localhost:8080/api/admin/health-check-program/${program.id}`,
        {
          name: values.name,
          description: values.description,
          startDate: values.startDate.format("YYYY-MM-DD"),
          endDate: values.endDate.format("YYYY-MM-DD"),
          note: values.note,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      message.success("Cập nhật chương trình thành công!");
      setCreateVisible(false);
      setEditMode(false);
      fetchProgram();
    } catch (error) {
      message.error("Cập nhật chương trình thất bại!");
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
        await axios.delete(`http://localhost:8080/api/admin/health-check-program/${programId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        await Swal.fire("Đã xóa!", "Chương trình đã được xóa thành công.", "success");
        fetchProgram();
      } catch {
        Swal.fire("Lỗi", "Xóa thất bại!", "error");
      }
    }
  };

  

  const handleUpdateStatus = async (id, status) => {
    const token = localStorage.getItem("token");
    try {
      await axios.patch(
        `http://localhost:8080/api/admin/health-check-program/${id}?status=${status}`,
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

  // Lọc danh sách theo tên chương trình và ngày tiêm
  const filteredPrograms = programs.filter((program) => {
    const matchName = program.name?.toLowerCase().includes(searchTerm.toLowerCase());
    const matchDate = filterDate
      ? dayjs(program.startDate).isSame(filterDate, "day") // Sửa ở đây
      : true;
    const matchStatus = filterStatus
      ? program.status === filterStatus
      : true;
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

  const handleViewResult = async (programId) => {
    setResultLoading(true);
    setResultVisible(true);
    try {
      const token = localStorage.getItem("token");
      const res = await axios.get(
        "http://localhost:8080/api/admin/health-check-results-status-by-program",
        { headers: { Authorization: `Bearer ${token}` } }
      );
      // Lọc kết quả theo programId
      const filtered = res.data.filter(item => item.programId === programId);
      setResultData(filtered);
    } catch (err) {
      setResultData([]);
    } finally {
      setResultLoading(false);
    }
  };

  if (!programs.length) return <div>Đang tải...</div>;

  return (
    <div style={{ padding: 24, marginLeft: 220, transition: "margin 0.2s", maxWidth: "100vw" }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 16 }}>
        <h2 style={{ margin: 0 }}>
          <span style={{ color: "#52c41a", marginRight: 8 }}>🛡️</span>
          Quản Lý Chương Trình Khám Định Kỳ
        </h2>
        <div style={{ display: "flex", gap: 12 }}>
          <Input
            placeholder="Tìm kiếm tên chương trình..."
            prefix={<SearchOutlined />}
            value={searchTerm}
            onChange={e => setSearchTerm(e.target.value)}
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
          <Button
            type="primary"
            icon={<PlusOutlined />}
            style={{ background: "#21ba45", border: "none" }}
            onClick={() => setCreateVisible(true)}
          >
            Lên lịch khám định kỳ
          </Button>
        </div>
      </div>
      {filteredPrograms.map((program) => (
        <Card
          key={program.id}
          style={{
            background: "#f6fcf7",
            borderRadius: 10,
            border: "1px solid #e6f4ea",
            width: "calc(100vw - 260px)",
            minWidth: 1200,
            margin: "0 auto",
            transition: "width 0.2s",
            marginBottom: 16,
          }}
          bodyStyle={{ padding: 24 }}
        >
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
            <div>
              <div style={{ fontWeight: 700, fontSize: 18, marginBottom: 4 }}>{program.name}</div>
              <div style={{ color: "#555", marginBottom: 2 }}>
                Mô tả: {program.description}
              </div>
              <div style={{ color: "#555", marginBottom: 8 }}>
                Ngày bắt đầu: {program.startDate} <br />
                Ngày kết thúc: {program.endDate}
              </div>
            </div>
            <Select
              value={program.status}
              style={{ width: 160 }}
              onChange={status => handleUpdateStatus(program.id, status)}
              options={[
                { value: "NOT_STARTED", label: "Chưa bắt đầu" },
                { value: "ON_GOING", label: "Đang diễn ra" },
                { value: "COMPLETED", label: "Đã hoàn thành" },
              ]}
            />
          </div>
          <Row gutter={32} style={{ margin: "24px 0" }}>
            <Col span={12}>
              <div style={{ background: "#fff", borderRadius: 8, padding: 16, textAlign: "center" }}>
                <div style={{ color: "#1890ff", fontWeight: 700, fontSize: 32 }}>{program.totalStudents}</div>
                <div style={{ color: "#888", fontWeight: 500 }}>Tổng học sinh</div>
              </div>
            </Col>
            <Col span={12}>
              <div style={{ background: "#fff", borderRadius: 8, padding: 16, textAlign: "center" }}>
                <div style={{ color: "#21ba45", fontWeight: 700, fontSize: 32 }}>{program.confirmed}</div>
                <div style={{ color: "#888", fontWeight: 500 }}>Đã xác nhận</div>
              </div>
            </Col>
          </Row>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
            <div>
              <Button onClick={() => {
                setDetailVisible(true);
                setProgram(program);
              }}>
                Xem chi tiết
              </Button>
              <Button
                type="primary"
                style={{ background: "#21ba45", border: "none", marginLeft: 8 }}
                onClick={() => handleViewResult(program.id)}
              >
                Xem kết quả
              </Button>
            </div>
            <div style={{ display: "flex", gap: 8, marginLeft: "auto" }}>
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
              <Button
                danger
                type="primary"
                onClick={() => handleDelete(program.id)}
              >
                Xóa
              </Button>
            </div>
          </div>
        </Card>
      ))}
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
            <Descriptions.Item label="ID">{program.id}</Descriptions.Item>
            <Descriptions.Item label="Tên chương trình">{program.name}</Descriptions.Item>
            <Descriptions.Item label="Mô tả">{program.description}</Descriptions.Item>
            <Descriptions.Item label="Ngày bắt đầu">{program.startDate}</Descriptions.Item>
            <Descriptions.Item label="Ngày kết thúc">{program.endDate}</Descriptions.Item>
            <Descriptions.Item label="Trạng thái">
              <Tag color={getStatusColor(program.status)} style={{ fontSize: 14 }}>
                {getStatusText(program.status)}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label="Ghi chú">{program.note}</Descriptions.Item>
          </Descriptions>
        )}
      </Modal>
      <Modal
        title={editMode ? "Sửa chương trình tiêm chủng" : "Lên lịch khám định kỳ"}
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
                  name: program.name,
                  description: program.description,
                  startDate: dayjs(program.startDate),
                  endDate: dayjs(program.endDate),
                  note: program.note,
                }
              : {}
          }
        >
          <Form.Item label="Tên chương trình" name="name" rules={[{ required: true, message: "Nhập tên chương trình" }]}>
            <Input />
          </Form.Item>
          <Form.Item label="Mô tả" name="description">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item label="Ngày bắt đầu" name="startDate" rules={[{ required: true, message: "Chọn ngày bắt đầu" }]}>
            <DatePicker style={{ width: "100%" }} format="YYYY-MM-DD" />
          </Form.Item>
          <Form.Item label="Ngày kết thúc" name="endDate" rules={[{ required: true, message: "Chọn ngày kết thúc" }]}>
            <DatePicker style={{ width: "100%" }} format="YYYY-MM-DD" />
          </Form.Item>
          <Form.Item label="Ghi chú" name="note">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading} style={{ width: "100%" }}>
              {editMode ? "Cập nhật" : "Tạo chương trình"}
            </Button>
          </Form.Item>
        </Form>
      </Modal>
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
                <Descriptions.Item label="Trạng thái sức khỏe">{item.statusHealth}</Descriptions.Item>
                <Descriptions.Item label="Số lượng">{item.count}</Descriptions.Item>
              </React.Fragment>
            ))}
          </Descriptions>
        )}
      </Modal>
    </div>
  );
};

export default HealthCheckProgramList;