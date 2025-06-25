import React, { useEffect, useState } from "react";
import { Table, Tabs, Button, Tag, Modal } from "antd";
import dayjs from "dayjs";
import "./MedicalRequest.css";

const { TabPane } = Tabs;

const statusMap = {
  PROCESSING: "Chờ xử lý",
  APPROVED: "Đã duyệt",
  GIVEN: "Đã cho uống",
  REJECTED: "Từ chối",
};

const colorMap = {
  PROCESSING: "gold",
  APPROVED: "green",
  GIVEN: "blue",
  REJECTED: "red",
};

const mockData = [
  {
    requestId: 1001,
    date: "2025-06-20",
    status: "PROCESSING",
    note: "Uống sau bữa trưa",
    medicalRequestDetailDTO: [
      { medicineName: "Paracetamol", quantity: "1 viên" },
    ],
    studentDTO: {
      fullName: "Nguyễn Văn A",
    },
  },
  {
    requestId: 1002,
    date: "2025-06-21",
    status: "APPROVED",
    note: "Thuốc cảm",
    medicalRequestDetailDTO: [
      { medicineName: "Decolgen", quantity: "2 viên" },
    ],
    studentDTO: {
      fullName: "Trần Thị B",
    },
  },
  {
    requestId: 1003,
    date: "2025-06-22",
    status: "GIVEN",
    note: "Vitamin C mỗi sáng",
    medicalRequestDetailDTO: [
      { medicineName: "Vitamin C", quantity: "1 viên" },
    ],
    studentDTO: {
      fullName: "Lê Văn C",
    },
  },
  {
    requestId: 1004,
    date: "2025-06-23",
    status: "REJECTED",
    note: "Không hợp lệ",
    medicalRequestDetailDTO: [],
    studentDTO: {
      fullName: "Phạm Thị D",
    },
  },
];

const MedicalRequest = () => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedRequest, setSelectedRequest] = useState(null);
  const [modalVisible, setModalVisible] = useState(false);

  useEffect(() => {
    fetchRequests();
  }, []);

  const fetchRequests = () => {
    setLoading(true);
    setTimeout(() => {
      setRequests(mockData);
      setLoading(false);
    }, 500);
  };

  const handleViewDetail = (record) => {
    setSelectedRequest(record);
    setModalVisible(true);
  };

  const handleApprove = (id) => {
    const updated = requests.map((req) =>
      req.requestId === id ? { ...req, status: "APPROVED" } : req
    );
    setRequests(updated);
  };

  const handleReject = (id) => {
    const updated = requests.map((req) =>
      req.requestId === id ? { ...req, status: "REJECTED" } : req
    );
    setRequests(updated);
  };

  const handleGiveMedicine = (id) => {
    const updated = requests.map((req) =>
      req.requestId === id ? { ...req, status: "GIVEN" } : req
    );
    setRequests(updated);
  };

  const columns = [
    {
      title: "Mã yêu cầu",
      dataIndex: "requestId",
      key: "requestId",
      align: "center",
    },
    {
      title: "Ngày gửi",
      dataIndex: "date",
      key: "date",
      align: "center",
      render: (text) => dayjs(text).format("DD/MM/YYYY"),
    },
    {
      title: "Trạng thái",
      dataIndex: "status",
      key: "status",
      align: "center",
      render: (status) => (
        <Tag color={colorMap[status]}>{statusMap[status]}</Tag>
      ),
    },
    {
      title: "Xem chi tiết",
      key: "detail",
      align: "center",
      render: (_, record) => (
        <Button type="link" onClick={() => handleViewDetail(record)}>
          Xem
        </Button>
      ),
    },
    {
      title: "Hành động",
      key: "action",
      align: "center",
      render: (_, record) => {
        if (record.status === "PROCESSING") {
          return (
            <div>
              <Button
                type="primary"
                onClick={() => handleApprove(record.requestId)}
              >
                Duyệt
              </Button>{" "}
              <Button danger onClick={() => handleReject(record.requestId)}>
                Từ chối
              </Button>
            </div>
          );
        }
        if (record.status === "APPROVED") {
          return (
            <Button
              type="primary"
              onClick={() => handleGiveMedicine(record.requestId)}
            >
              Cho uống thuốc
            </Button>
          );
        }
        return null;
      },
    },
  ];

  const renderTable = (status) => {
    const filtered = requests.filter((req) => req.status === status);
    return (
      <Table
        rowKey="requestId"
        columns={columns}
        dataSource={filtered}
        loading={loading}
        pagination={{ pageSize: 5 }}
      />
    );
  };

  return (
    <div className="medical-request-wrapper">
      <div className="full-width-content">
        <h2>Danh sách yêu cầu gửi thuốc</h2>
        <Tabs defaultActiveKey="PROCESSING">
          <TabPane tab="Chờ xử lý" key="PROCESSING">
            {renderTable("PROCESSING")}
          </TabPane>
          <TabPane tab="Đã duyệt" key="APPROVED">
            {renderTable("APPROVED")}
          </TabPane>
          <TabPane tab="Đã cho uống" key="GIVEN">
            {renderTable("GIVEN")}
          </TabPane>
          <TabPane tab="Từ chối" key="REJECTED">
            {renderTable("REJECTED")}
          </TabPane>
        </Tabs>

        <Modal
          title="Chi tiết yêu cầu"
          visible={modalVisible}
          onCancel={() => setModalVisible(false)}
          footer={null}
        >
          {selectedRequest ? (
            <div>
              <p>
                <strong>Mã yêu cầu:</strong> {selectedRequest.requestId}
              </p>
              <p>
                <strong>Ngày gửi:</strong>{" "}
                {dayjs(selectedRequest.date).format("DD/MM/YYYY")}
              </p>
              <p>
                <strong>Học sinh:</strong>{" "}
                {selectedRequest.studentDTO?.fullName || "Không rõ"}
              </p>
              <p>
                <strong>Ghi chú:</strong> {selectedRequest.note || "Không có"}
              </p>
              <p>
                <strong>Chi tiết thuốc:</strong>
              </p>
              <ul>
                {selectedRequest.medicalRequestDetailDTO.length > 0 ? (
                  selectedRequest.medicalRequestDetailDTO.map((item, idx) => (
                    <li key={idx}>
                      {item.medicineName} - {item.quantity}
                    </li>
                  ))
                ) : (
                  <li>Không có</li>
                )}
              </ul>
            </div>
          ) : null}
        </Modal>
      </div>
    </div>
  );
};

export default MedicalRequest;
