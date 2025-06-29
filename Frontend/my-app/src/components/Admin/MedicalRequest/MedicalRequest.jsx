import React, { useEffect, useState } from "react";
import { Tabs, Button, Tag, Modal, Row, Col, Card, Empty } from "antd";
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
    requestName: "Yêu cầu uống Paracetamol", // Thêm dòng này
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
    requestName: "Yêu cầu uống Decolgen", // Thêm dòng này
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
    requestName: "Yêu cầu uống Vitamin C", // Thêm dòng này
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
    requestName: "Yêu cầu uống thuốc không hợp lệ", // Thêm dòng này
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

  // Hiển thị card cho từng request
  const renderCards = (status) => {
    const filtered = requests.filter((req) => req.status === status);
    if (filtered.length === 0) return <Empty description="Không có dữ liệu" />;
    return (
      <Row gutter={[16, 16]}>
        {filtered.map((record) => (
          <Col xs={24} sm={12} md={8} lg={6} key={record.requestId}>
            <Card
              className={
                record.status === "PROCESSING"
                  ? "card-processing"
                  : record.status === "APPROVED"
                  ? "card-approved"
                  : record.status === "GIVEN"
                  ? "card-given"
                  : record.status === "REJECTED"
                  ? "card-rejected"
                  : ""
              }
              title={
                <span>
                  <Tag color={colorMap[record.status]}>
                    {statusMap[record.status]}
                  </Tag>
                  <span style={{ marginLeft: 8 }}>#{record.requestId}</span>
                </span>
              }
              bordered
              extra={
                <Button type="link" onClick={() => handleViewDetail(record)}>
                  Xem chi tiết
                </Button>
              }
              style={{ minHeight: 220 }}
            >
              {/* Đưa tên yêu cầu lên đầu */}
              <p style={{ fontWeight: 700, fontSize: 16, color: "#1476d1", marginBottom: 8 }}>
                {record.requestName}
              </p>
              <p>
                <strong>Ngày gửi:</strong> {dayjs(record.date).format("DD/MM/YYYY")}
              </p>
              <p>
                <strong>Học sinh:</strong> {record.studentDTO?.fullName || "Không rõ"}
              </p>
              <p>
                <strong>Ghi chú:</strong> {record.note || "Không có"}
              </p>
              <div>
                <strong>Chi tiết thuốc:</strong>
                <ul style={{ margin: 0, paddingLeft: 18 }}>
                  {record.medicalRequestDetailDTO.length > 0 ? (
                    record.medicalRequestDetailDTO.map((item, idx) => (
                      <li key={idx}>
                        {item.medicineName} - {item.quantity}
                      </li>
                    ))
                  ) : (
                    <li>Không có</li>
                  )}
                </ul>
              </div>
              <div style={{ marginTop: 12 }}>
                {record.status === "PROCESSING" && (
                  <>
                    <Button
                      type="primary"
                      size="small"
                      onClick={() => handleApprove(record.requestId)}
                      style={{ marginRight: 8 }}
                    >
                      Duyệt
                    </Button>
                    <Button
                      danger
                      size="small"
                      onClick={() => handleReject(record.requestId)}
                    >
                      Từ chối
                    </Button>
                  </>
                )}
                {record.status === "APPROVED" && (
                  <Button
                    type="primary"
                    size="small"
                    onClick={() => handleGiveMedicine(record.requestId)}
                  >
                    Cho uống thuốc
                  </Button>
                )}
              </div>
            </Card>
          </Col>
        ))}
      </Row>
    );
  };

  return (
    <div className="medical-request-wrapper">
      <div className="full-width-content">
        <h2>Danh sách yêu cầu gửi thuốc</h2>
        <Tabs defaultActiveKey="PROCESSING">
          <TabPane tab="Chờ xử lý" key="PROCESSING">
            {renderCards("PROCESSING")}
          </TabPane>
          <TabPane tab="Đã duyệt" key="APPROVED">
            {renderCards("APPROVED")}
          </TabPane>
          <TabPane tab="Đã cho uống" key="GIVEN">
            {renderCards("GIVEN")}
          </TabPane>
          <TabPane tab="Từ chối" key="REJECTED">
            {renderCards("REJECTED")}
          </TabPane>
        </Tabs>

        <Modal
          title="Chi tiết yêu cầu"
          open={modalVisible}
          onCancel={() => setModalVisible(false)}
          footer={null}
        >
          {selectedRequest ? (
            <div>
              <p>
                <strong>Mã yêu cầu:</strong> {selectedRequest.requestId}
              </p>
              <p>
                <strong>Tên yêu cầu:</strong> {selectedRequest.requestName}
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
