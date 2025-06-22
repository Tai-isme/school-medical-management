import React, { useEffect, useState } from "react";
import { Table, Button, Modal, Form, Input, message } from "antd";
import axios from "axios";
import dayjs from "dayjs";
import "./MedicalIncidentList.css";

const MedicalIncidentList = () => {
  const [data, setData] = useState([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [form] = Form.useForm();

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

  const columns = [
    { title: "ID Sự kiện", dataIndex: "eventId", key: "eventId" },
    { title: "Tên sự kiện", dataIndex: "typeEvent", key: "typeEvent" },
    { title: "Ngày xảy ra", dataIndex: "date", key: "date" },
    { title: "Tên học sinh", dataIndex: "studentName", key: "studentName" },
    { title: "Người phụ trách", dataIndex: "nurseId", key: "nurseId" },
    { title: "Mô tả", dataIndex: "description", key: "description" },
    {
      title: "Liên hệ PH",
      key: "contact",
      render: (_, record) => (
        <a href={`tel:${record.parentPhone}`} className="contact-link">
          {record.parentPhone}
        </a>
      ),
    },
  ];

  return (
    <div className="incident-container">
      <div className="incident-header">
        <h2>Danh sách sự cố y tế</h2>
        <Button type="primary" onClick={showModal}>
          + Tạo sự kiện
        </Button>
      </div>

      <Table
        dataSource={data}
        columns={columns}
        rowKey="eventId"
        bordered
        pagination={{ pageSize: 5 }}
      />

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
