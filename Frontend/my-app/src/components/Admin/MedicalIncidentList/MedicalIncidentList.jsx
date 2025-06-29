import React, { useEffect, useState } from "react";
import { Table, Button, Modal, Form, Input, message, Row, Col, DatePicker, Card, Tag, Pagination } from "antd";
import { CheckCircleOutlined, ClockCircleOutlined } from "@ant-design/icons";
import axios from "axios";
import dayjs from "dayjs";
import "./MedicalIncidentList.css";

const MedicalIncidentList = () => {
  const [data, setData] = useState([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [form] = Form.useForm();
  const [searchEvent, setSearchEvent] = useState("");
  const [searchDate, setSearchDate] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 5;

  // Tải danh sách sự kiện y tế từ API
  useEffect(() => {
    const fetchEvents = async () => {
      try {
        const token = localStorage.getItem("token");
        const res = await axios.get(
          "http://localhost:8080/api/nurse/medical-event",
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );

        const apiData = res.data.map((item) => ({
          ...item,
          studentName: item.studentName || `ID ${item.studentId}`,
          parentPhone: item.parentPhone || "Chưa có",
        }));

        setData(apiData);
      } catch (err) {
        console.error("Lỗi tải sự kiện:", err);
        message.error("Không thể tải danh sách sự kiện!");
      }
    };

    fetchEvents();
  }, []);

  const showModal = () => setIsModalVisible(true);

  const handleCancel = () => {
    form.resetFields();
    setIsModalVisible(false);
  };

  const handleCreate = async () => {
    try {
      const values = await form.validateFields();

      const payload = {
        typeEvent: values.typeEvent,
        description: values.description,
        studentId: Number(values.studentId),
      };

      const token = localStorage.getItem("token");

      const res = await axios.post(
        "http://localhost:8080/api/nurse/medical-event",
        payload,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      const created = {
        ...res.data,
        eventId: res.data.eventId || Date.now(),
        date: dayjs().format("YYYY-MM-DD"),
        studentName: res.data.studentName || `ID ${values.studentId}`,
        nurseId: `ID ${res.data.nurseId || "---"}`,
        parentPhone: res.data.parentPhone || "Chưa có",
      };

      setData((prev) => [...prev, created]);
      form.resetFields();
      setIsModalVisible(false);
      message.success("Tạo sự kiện thành công!");
    } catch (error) {
      console.error("Lỗi tạo sự kiện:", error);
      message.error("Không thể tạo sự kiện. Kiểm tra lại thông tin.");
    }
  };

  // Lọc dữ liệu theo tên sự kiện và ngày
  const filteredData = data.filter((item) => {
    const matchEvent = item.typeEvent
      .toLowerCase()
      .includes(searchEvent.toLowerCase());
    const matchDate = searchDate
      ? dayjs(item.date).isSame(searchDate, "day")
      : true;
    return matchEvent && matchDate;
  });

  // Dữ liệu trang hiện tại
  const pagedData = filteredData.slice(
    (currentPage - 1) * pageSize,
    currentPage * pageSize
  );

  const statusMap = {
    DONE: { text: "Đã xử lý", color: "geekblue", icon: <CheckCircleOutlined /> },
    PROCESSING: { text: "Đang xử lý", color: "volcano", icon: <ClockCircleOutlined /> },
  };

  useEffect(() => {
    setCurrentPage(1);
  }, [searchEvent, searchDate]);

  return (
    <div className="incident-container">
      <div className="incident-header">
        <h2>Sự Kiện Y Tế Gần Đây</h2>
        <Button type="primary" danger onClick={showModal}>
          + Thêm sự kiện
        </Button>
      </div>

      {/* Thanh tìm kiếm */}
      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col>
          <Input
            placeholder="Tìm theo tên sự kiện"
            value={searchEvent}
            onChange={(e) => setSearchEvent(e.target.value)}
            allowClear
          />
        </Col>
        <Col>
          <DatePicker
            placeholder="Tìm theo ngày"
            value={searchDate}
            onChange={setSearchDate}
            allowClear
            format="YYYY-MM-DD"
          />
        </Col>
      </Row>

      {/* Danh sách card */}
      <div style={{ padding: 8 }}>
        {pagedData.length === 0 ? (
          <div style={{ textAlign: "center", color: "#888" }}>Không có sự kiện nào</div>
        ) : (
          pagedData.map((item) => (
            <Card
              key={item.eventId}
              style={{
                marginBottom: 16,
                borderRadius: 10,
                border: "1px solid #eee",
                boxShadow: "0 2px 8px rgba(0,0,0,0.03)",
              }}
              bodyStyle={{ padding: 20 }}
            >
              <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
                <div>
                  <div style={{ fontWeight: 600, fontSize: 17, marginBottom: 4 }}>
                    {item.studentName} {item.className ? `- ${item.className}` : ""}
                  </div>
                  <div style={{ color: "#444", marginBottom: 8 }}>{item.typeEvent}</div>
                  <div style={{ color: "#888", fontSize: 14, marginBottom: 8 }}>
                    {item.time || ""} {item.date ? `- ${dayjs(item.date).format("DD/MM/YYYY")}` : ""}
                  </div>
                  <div style={{ color: "#555", fontSize: 15, marginBottom: 12 }}>{item.description}</div>
                  <Button size="small" style={{ marginRight: 8 }}>Chi tiết</Button>
                  {item.status !== "DONE" && (
                    <Button
                      size="small"
                      type="primary"
                      style={{ background: "#52c41a", border: "none" }}
                    >
                      Đánh dấu hoàn thành
                    </Button>
                  )}
                </div>
                <div style={{ textAlign: "right" }}>
                  <Tag
                    color={statusMap[item.status]?.color || "default"}
                    style={{ fontSize: 15, padding: "4px 14px", borderRadius: 16 }}
                    icon={statusMap[item.status]?.icon}
                  >
                    {statusMap[item.status]?.text || "Không rõ"}
                  </Tag>
                </div>
              </div>
            </Card>
          ))
        )}
      </div>

      {/* Phân trang */}
      <div style={{ textAlign: "center", margin: "16px 0" }}>
        <Pagination
          current={currentPage}
          pageSize={pageSize}
          total={filteredData.length}
          onChange={setCurrentPage}
          showSizeChanger={false}
        />
      </div>

      {/* Modal tạo sự kiện giữ nguyên */}
      <Modal
        title="Tạo sự cố y tế"
        open={isModalVisible}
        onCancel={handleCancel}
        onOk={handleCreate}
        okText="Tạo"
        cancelText="Hủy"
      >
        <Form form={form} layout="vertical">
          <Form.Item
            label="Tên sự kiện"
            name="typeEvent"
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="ID học sinh"
            name="studentId"
            rules={[{ required: true }]}
          >
            <Input placeholder="Nhập ID học sinh" />
          </Form.Item>
          <Form.Item
            label="Mô tả"
            name="description"
            rules={[{ required: true }]}
          >
            <Input.TextArea />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default MedicalIncidentList;
