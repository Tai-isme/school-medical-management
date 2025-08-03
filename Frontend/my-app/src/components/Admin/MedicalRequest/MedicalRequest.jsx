import React, { useEffect, useState } from "react";
import { Table, Button, Image, Tag, Modal, message, Input, Tabs } from "antd";
import axios from "axios";
import SendMedicineDetailModal from "./SendMedicineDetailModal";
import "./MedicalRequest.css";

const tabStatus = [
  { key: "ALL", label: "Tất cả" },
  { key: "PROCESSING", label: "Chờ duyệt" },
  { key: "CONFIRMED", label: "Đã duyệt" },
  { key: "COMPLETED", label: "Đã hoàn thành" },
  { key: "CANCELLED", label: "Từ chối" },
];

const MedicalRequest = () => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [selectedId, setSelectedId] = useState(null);
  const [activeTab, setActiveTab] = useState("ALL");

  // Lọc
  const [filterName, setFilterName] = useState("");
  const [filterStudent, setFilterStudent] = useState("");
  const [filterClass, setFilterClass] = useState("");

  useEffect(() => {
    fetchRequests(activeTab);
    // eslint-disable-next-line
  }, [activeTab]);

  const fetchRequests = async (status) => {
    setLoading(true);
    try {
      const token = localStorage.getItem("token");
      let url =
        status === "ALL"
          ? "http://localhost:8080/api/nurse/medical-request"
          : `http://localhost:8080/api/nurse/medical-request/${status}`;
      const res = await axios.get(url, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setRequests(res.data);
    } catch {
      message.error("Không thể tải dữ liệu!");
      setRequests([]);
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (id) => {
    const token = localStorage.getItem("token");
    try {
      await axios.put(
        `http://localhost:8080/api/nurse/${id}/status`,
        { status: "CONFIRMED", reason_rejected: null },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      message.success("Duyệt thành công!");
      fetchRequests(activeTab);
    } catch {
      message.error("Duyệt thất bại!");
    }
  };

  const handleDelete = async (id) => {
    Modal.confirm({
      title: "Bạn chắc chắn muốn xóa đơn thuốc này?",
      okText: "Xóa",
      okType: "danger",
      cancelText: "Hủy",
      onOk: async () => {
        const token = localStorage.getItem("token");
        try {
          await axios.delete(`http://localhost:8080/api/nurse/medical-request/${id}`, {
            headers: { Authorization: `Bearer ${token}` },
          });
          message.success("Xóa thành công!");
          fetchRequests(activeTab);
        } catch {
          message.error("Xóa thất bại!");
        }
      },
    });
  };

  const columns = [
    {
      title: "Mã đơn thuốc",
      dataIndex: "requestId",
      key: "requestId",
      align: "center",
      width: 30,
      render: (id) => <b>#{id}</b>,
    },
    {
      title: "Tên đơn thuốc",
      dataIndex: "requestName",
      key: "requestName",
      align: "center",
      width: 180,
    },
    {
      title: "Ngày cho uống",
      dataIndex: "date",
      key: "date",
      align: "center",
      width: 130,
      render: (date) =>
        date
          ? new Date(date).toLocaleDateString("vi-VN", {
              day: "2-digit",
              month: "2-digit",
              year: "numeric",
            })
          : "---",
    },
    {
      title: "Tên học sinh nhận thuốc",
      dataIndex: ["studentDTO", "fullName"],
      key: "studentName",
      align: "center",
      width: 180,
      render: (_, record) => record.studentDTO?.fullName || "---",
    },
    {
      title: "Lớp",
      dataIndex: ["studentDTO", "classDTO", "className"],
      key: "className",
      align: "center",
      width: 100,
      render: (_, record) => record.studentDTO?.classDTO?.className || "---",
    },
    {
      title: "Y tá phụ trách",
      dataIndex: ["nurseDTO", "fullName"],
      key: "nurseName",
      align: "center",
      width: 160,
      render: (_, record) =>
        record.nurseDTO?.fullName ? (
          <Tag color="blue">{record.nurseDTO.fullName}</Tag>
        ) : (
          <Tag color="default">Chưa có</Tag>
        ),
    },
    {
      title: "Ảnh",
      dataIndex: "image",
      key: "image",
      align: "center",
      width: 80,
      render: (img) =>
        img ? (
          <Image
            src={img}
            alt="Ảnh đơn thuốc"
            width={48}
            height={48}
            style={{ objectFit: "cover", borderRadius: 6, border: "1px solid #eee" }}
            preview={false}
          />
        ) : (
          <span>---</span>
        ),
    },
    {
      title: "Hành động",
      key: "actions",
      align: "center",
      width: 120,
      render: (_, record) => (
        <div style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: 4 }}>
          <Button
            type="link"
            style={{ marginBottom: 4, padding: 0 }}
            onClick={() => {
              setSelectedId(record.requestId);
              setModalVisible(true);
            }}
          >
            Xem chi tiết
          </Button>
          <div>
            <Button
              type="primary"
              style={{ marginRight: 8 }}
              disabled={record.status !== "PROCESSING"}
              onClick={() => handleApprove(record.requestId)}
            >
              Duyệt
            </Button>
            <Button
              danger
              onClick={() => handleDelete(record.requestId)}
              disabled={record.status !== "PROCESSING"}
            >
              Từ chối
            </Button>
          </div>
        </div>
      ),
    },
  ];

  // Lọc dữ liệu theo 3 trường filter (không lọc status nữa vì đã lấy đúng status từ API)
  const filteredData = requests
    .filter((item) =>
      item.requestName?.toLowerCase().includes(filterName.toLowerCase())
    )
    .filter((item) =>
      item.studentDTO?.fullName?.toLowerCase().includes(filterStudent.toLowerCase())
    )
    .filter((item) =>
      item.studentDTO?.classDTO?.className?.toLowerCase().includes(filterClass.toLowerCase())
    );

  return (
    <div className="medical-request-wrapper">
      <div className="medicine-table-container">
        <h2 style={{ marginBottom: 16 }}>Quản lý đơn thuốc</h2>
        {/* 3 ô lọc */}
        <div style={{ marginBottom: 16, display: "flex", gap: 12 }}>
          <Input
            placeholder="Lọc theo tên đơn thuốc"
            value={filterName}
            onChange={e => setFilterName(e.target.value)}
            style={{ width: 220 }}
            allowClear
          />
          <Input
            placeholder="Lọc theo tên học sinh"
            value={filterStudent}
            onChange={e => setFilterStudent(e.target.value)}
            style={{ width: 220 }}
            allowClear
          />
          <Input
            placeholder="Lọc theo lớp"
            value={filterClass}
            onChange={e => setFilterClass(e.target.value)}
            style={{ width: 180 }}
            allowClear
          />
        </div>
        <Tabs
          activeKey={activeTab}
          onChange={setActiveTab}
          items={tabStatus.map(tab => ({
            key: tab.key,
            label: tab.label,
          }))}
          style={{ marginBottom: 16 }}
        />
        <Table
          columns={columns}
          dataSource={filteredData}
          rowKey="requestId"
          loading={loading}
          pagination={{ pageSize: 9 }}
          bordered
          scroll={{ x: 1100 }}
        />
        <SendMedicineDetailModal
          open={modalVisible}
          onClose={() => setModalVisible(false)}
          requestId={selectedId}
        />
      </div>
    </div>
  );
};

export default MedicalRequest;