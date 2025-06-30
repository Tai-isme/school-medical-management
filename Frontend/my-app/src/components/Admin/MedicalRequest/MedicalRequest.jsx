import React, { useEffect, useState } from "react";
import { Tabs, Button, Tag, Modal, Row, Col, Card, Empty, message } from "antd";
import dayjs from "dayjs";
import axios from "axios";
import "./MedicalRequest.css";

const { TabPane } = Tabs;

const statusMap = {
  PROCESSING: "Chờ xử lý",
  SUBMITTED: "Đã duyệt",
  COMPLETED: "Đã cho uống",
  CANCELLED: "Từ chối", // Đổi từ REJECTED sang CANCELLED
};

const colorMap = {
  PROCESSING: "gold",
  SUBMITTED: "green",
  COMPLETED: "blue",
  CANCELLED: "red", // Đổi từ REJECTED sang CANCELLED
};

const MedicalRequest = () => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedRequest, setSelectedRequest] = useState(null);
  const [modalVisible, setModalVisible] = useState(false);
  const [activeStatus, setActiveStatus] = useState("PROCESSING");
  const [searchTerm, setSearchTerm] = useState(""); // Thêm state tìm kiếm

  useEffect(() => {
    fetchRequests(activeStatus);
  }, [activeStatus]);

  const fetchRequests = async (status) => {
    setLoading(true);
    try {
      const token = localStorage.getItem("token");
      const res = await axios.get(
        `http://localhost:8080/api/nurse/status/${status}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setRequests(res.data);
    } catch (err) {
      message.error("Không thể tải dữ liệu!");
      setRequests([]);
    } finally {
      setLoading(false);
    }
  };

  const handleViewDetail = (record) => {
    setSelectedRequest(record);
    setModalVisible(true);
  };

  const handleApprove = async (id) => {
    const token = localStorage.getItem("token");
    try {
      await axios.put(
        `http://localhost:8080/api/nurse/${id}/status?status=SUBMITTED`,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );
      message.success("Duyệt thành công!");
      fetchRequests(activeStatus); // Refresh list
    } catch {
      message.error("Duyệt thất bại!");
    }
  };

  const handleReject = async (id) => {
    const token = localStorage.getItem("token");
    try {
      await axios.put(
        `http://localhost:8080/api/nurse/${id}/status?status=CANCELLED`,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );
      message.success("Từ chối thành công!");
      fetchRequests(activeStatus);
    } catch {
      message.error("Từ chối thất bại!");
    }
  };

  const handleGiveMedicine = async (id) => {
    const token = localStorage.getItem("token");
    try {
      await axios.put(
        `http://localhost:8080/api/nurse/${id}/status?status=COMPLETED`,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );
      message.success("Đã cho uống thuốc!");
      fetchRequests(activeStatus);
    } catch {
      message.error("Cập nhật thất bại!");
    }
  };

  // Lọc requests theo tên yêu cầu
  const filteredRequests = requests.filter((r) =>
    r.requestName?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // Sửa renderCards dùng filteredRequests
  const renderCards = () => {
    if (loading) return <div>Đang tải...</div>;
    if (filteredRequests.length === 0) return <Empty description="Không có dữ liệu" />;
    return (
      <Row gutter={[16, 16]}>
        {filteredRequests.map((record) => (
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
                  {Array.isArray(record.medicalRequestDetailDTO) && record.medicalRequestDetailDTO.length > 0 ? (
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
                {record.status === "SUBMITTED" && (
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
        {/* Ô tìm kiếm */}
        <div style={{ marginBottom: 16, maxWidth: 320 }}>
          <input
            type="text"
            placeholder="Tìm kiếm theo tên yêu cầu..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{
              width: "100%",
              padding: 8,
              borderRadius: 4,
              border: "1px solid #ccc",
              fontSize: 15,
            }}
          />
        </div>
        <Tabs
          defaultActiveKey="PROCESSING"
          activeKey={activeStatus}
          onChange={setActiveStatus}
        >
          <TabPane tab="Chờ xử lý" key="PROCESSING">
            {renderCards()}
          </TabPane>
          <TabPane tab="Đã duyệt" key="SUBMITTED">
            {renderCards()}
          </TabPane>
          <TabPane tab="Đã cho uống" key="COMPLETED">
            {renderCards()}
          </TabPane>
          <TabPane tab="Từ chối" key="CANCELLED">
            {renderCards()}
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
                {Array.isArray(selectedRequest.medicalRequestDetailDTO) && selectedRequest.medicalRequestDetailDTO.length > 0 ? (
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
