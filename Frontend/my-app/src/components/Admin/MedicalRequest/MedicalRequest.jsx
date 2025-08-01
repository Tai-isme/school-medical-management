import React, { useEffect, useState } from "react";
import { Tabs, Button, Tag, Modal, Row, Col, Card, Empty, message, DatePicker, Pagination } from "antd";
import dayjs from "dayjs";
import isSameOrAfter from "dayjs/plugin/isSameOrAfter";
import isSameOrBefore from "dayjs/plugin/isSameOrBefore";
import axios from "axios";
import Swal from "sweetalert2"; // Thêm dòng này ở đầu file
import "./MedicalRequest.css";

dayjs.extend(isSameOrAfter);
dayjs.extend(isSameOrBefore);

const { TabPane } = Tabs;
const { RangePicker } = DatePicker;

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
  const [dateRange, setDateRange] = useState([null, null]);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(8); // Số lượng card mỗi trang
  const [rejectReason, setRejectReason] = useState(""); // State để lưu lý do từ chối
  const [rejectModalVisible, setRejectModalVisible] = useState(false); // State để hiển thị modal từ chối
  const [rejectRequestId, setRejectRequestId] = useState(null); // State để lưu requestId của yêu cầu bị từ chối

  // Gọi API lấy tất cả khi đổi tab hoặc lần đầu
  useEffect(() => {
    fetchRequests();
  }, []);

  useEffect(() => {
    setCurrentPage(1);
  }, [activeStatus, searchTerm, dateRange]);

  useEffect(() => {
    fetchRequests(activeStatus);
  }, [activeStatus]); // Gọi lại khi đổi tab

  const fetchRequests = async (status = "PROCESSING") => {
    setLoading(true);
    try {
      const token = localStorage.getItem("token");
      let apiUrl = "http://localhost:8080/api/nurse/medical-request";
      if (status !== "ALL") {
        apiUrl += `/${status}`;
      }
      const res = await axios.get(apiUrl, {
        headers: { Authorization: `Bearer ${token}` },
      });
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
        `http://localhost:8080/api/nurse/${id}/status`,
        {
          status: "SUBMITTED",
          reason_rejected: null
        },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      Swal.fire({
        icon: "success",
        title: "Duyệt thành công!",
        showConfirmButton: false,
        timer: 1500,
      });
      fetchRequests(activeStatus);
    } catch {
      Swal.fire({
        icon: "error",
        title: "Duyệt thất bại!",
        showConfirmButton: false,
        timer: 1500,
      });
    }
  };

  const handleRejectClick = (id) => {
    setRejectRequestId(id); // Lưu requestId
    setRejectModalVisible(true); // Hiển thị modal
  };

  const handleRejectConfirm = async () => {
    const token = localStorage.getItem("token");
    try {
      await axios.put(
        `http://localhost:8080/api/nurse/${rejectRequestId}/status`,
        { 
          status: "CANCELLED",
          reason_rejected: rejectReason
         }, // Gửi lý do từ chối trong body
        { headers: { Authorization: `Bearer ${token}` } }
      );
      Swal.fire({
        icon: "success",
        title: "Từ chối thành công!",
        showConfirmButton: false,
        timer: 1500,
      });
      fetchRequests(activeStatus); // Tải lại danh sách yêu cầu
    } catch {
      Swal.fire({
        icon: "error",
        title: "Từ chối thất bại!",
        showConfirmButton: false,
        timer: 1500,
      });
    } finally {
      setRejectModalVisible(false); // Đóng modal
      setRejectReason(""); // Xóa lý do từ chối
    }
  };

  const handleGiveMedicine = async (id) => {
    const token = localStorage.getItem("token");
    try {
      await axios.put(
        `http://localhost:8080/api/nurse/${id}/status`,
        {
          status: "COMPLETED",
          reason_rejected: null // Không cần lý do từ chối khi cho uống thuốc
        },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      Swal.fire({
        icon: "success",
        title: "Đã cho uống thuốc!",
        showConfirmButton: false,
        timer: 1500,
      });
      fetchRequests(activeStatus);
    } catch {
      Swal.fire({
        icon: "error",
        title: "Cập nhật thất bại!",
        showConfirmButton: false,
        timer: 1500,
      });
    }
  };

  // Lọc theo status cho từng tab
  const getFilteredRequests = () => {
    let filtered = requests;
    if (activeStatus !== "ALL") {
      filtered = filtered.filter(r => r.status === activeStatus);
    }
    // Lọc theo tên và ngày như cũ
    const matchTerm = r => r.requestName?.toLowerCase().includes(searchTerm.toLowerCase());
    const matchDate = r => {
      if (Array.isArray(dateRange) && dateRange[0] && dateRange[1]) {
        const reqDate = dayjs(r.date);
        return (
          reqDate.isSameOrAfter(dayjs(dateRange[0]), "day") &&
          reqDate.isSameOrBefore(dayjs(dateRange[1]), "day")
        );
      }
      return true;
    };
    return filtered.filter(r => matchTerm(r) && matchDate(r));
  };

  const renderCards = () => {
    const filteredRequests = getFilteredRequests();
    if (loading) return <div>Đang tải...</div>;
    if (filteredRequests.length === 0) return <Empty description="Không có dữ liệu" />;

    // Phân trang
    const startIdx = (currentPage - 1) * pageSize;
    const endIdx = startIdx + pageSize;
    const pageData = filteredRequests.slice(startIdx, endIdx);

    return (
      <>
        <Row gutter={[16, 16]}>
          {pageData.map((record) => (
            <Col xs={24} sm={12} md={8} lg={6} key={record.requestId}>
              <Card
                className={
                  record.status === "PROCESSING"
                    ? "card-processing"
                    : record.status === "COMFIRMED"
                    ? "card-submitted"
                    : record.status === "COMPLETED"
                    ? "card-completed"
                    : record.status === "CANCELLED"
                    ? "card-cancelled"
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
                {/* Chỉ giữ lại 3 thông tin */}
                <p style={{ fontWeight: 700, fontSize: 16, color: "#1476d1", marginBottom: 8 }}>
                  {record.requestName}
                </p>
                <p>
                  <strong>Ngày gửi:</strong> {dayjs(record.date).format("DD/MM/YYYY")}
                </p>
                <p>
                  <strong>Học sinh:</strong> {record.studentDTO?.fullName || "Không rõ"}
                </p>
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
                        onClick={() => handleRejectClick(record.requestId)} // Mở modal nhập lý do
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
                  {record.status === "COMPLETED" && (
                    <Button type="default" size="small" disabled style={{ color: "#1890ff", borderColor: "#1890ff" }}>
                      Đã cho uống
                    </Button>
                  )}
                  {record.status === "CANCELLED" && (
                    <Button type="default" size="small" disabled style={{ color: "#ff4d4f", borderColor: "#ff4d4f" }}>
                      Đã từ chối
                    </Button>
                  )}
                </div>
              </Card>
            </Col>
          ))}
        </Row>
        <div style={{ marginTop: 24, textAlign: "center" }}>
          <Pagination
            current={currentPage}
            pageSize={pageSize}
            total={filteredRequests.length}
            onChange={setCurrentPage}
            showSizeChanger={false}
          />
        </div>
      </>
    );
  };

  return (
    <div className="medical-request-wrapper">
      <div className="full-width-content">
        <h2>Danh sách yêu cầu gửi thuốc</h2>
        {/* Ô tìm kiếm */}
        <div
          style={{
            marginBottom: 16,
            display: "flex",
            gap: 12,
            maxWidth: 600,
            alignItems: "center",
          }}
        >
          <RangePicker
            style={{ flex: 1 }}
            format="DD/MM/YYYY"
            value={dateRange}
            onChange={setDateRange}
            allowClear
            placeholder={["Từ ngày", "Đến ngày"]}
          />
          <input
            type="text"
            placeholder="Tìm kiếm theo tên yêu cầu..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{
              flex: 1,
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
          <TabPane tab="Tất cả danh sách gửi thuốc" key="ALL">
            {renderCards()}
          </TabPane>
          <TabPane tab="Chờ " key="PROCESSING">
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
                <strong>Thời gian dùng thuốc:</strong>{" "}
                {selectedRequest.medicalRequestDetailDTO[0]?.time}
              </p>
              <p>
                <strong>Học sinh:</strong>{" "}
                {selectedRequest.studentDTO?.fullName + "   (" + selectedRequest.studentDTO?.id +")" || "Không rõ"} 
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
                      {item.medicineName} - {item.dosage}
                    </li>
                  ))
                ) : (
                  <li>Không có</li>
                )}
              </ul>
            </div>
          ) : null}
        </Modal>

        <Modal
          title="Nhập lý do từ chối"
          open={rejectModalVisible}
          onCancel={() => setRejectModalVisible(false)} // Đóng modal
          footer={[
            <Button key="cancel" onClick={() => setRejectModalVisible(false)}>
              Hủy
            </Button>,
            <Button
              key="confirm"
              type="primary"
              onClick={handleRejectConfirm}
              disabled={!rejectReason.trim()} // Vô hiệu hóa nếu lý do trống
            >
              Xác nhận
            </Button>,
          ]}
        >
          <textarea
            placeholder="Nhập lý do từ chối..."
            value={rejectReason}
            onChange={(e) => setRejectReason(e.target.value)}
            style={{
              width: "100%",
              height: "100px",
              borderRadius: "4px",
              border: "1px solid #ccc",
              padding: "8px",
              fontSize: "14px",
            }}
          />
        </Modal>
      </div>
    </div>
  );
};

export default MedicalRequest;