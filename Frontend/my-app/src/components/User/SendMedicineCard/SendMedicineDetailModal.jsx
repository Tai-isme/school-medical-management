import React from "react";
import { Modal, Spin, Row, Col, Divider, Image } from "antd";


const SendMedicineDetailModal = ({ open, onClose, loading, detailData }) => (
  <Modal
    open={open}
    onCancel={onClose}
    footer={null}
    width={900}
    centered
    title={
      <div style={{ textAlign: "center", fontWeight: "bold", fontSize: 20 }}>
        Chi tiết đơn thuốc đã gửi
      </div>
    }
  >
    {loading ? (
      <Spin />
    ) : detailData ? (
      <Row gutter={32}>
        {/* Thông tin đơn thuốc */}
        <Col xs={24} md={12}>
          <div
            style={{
              background: "#f6fbff",
              borderRadius: 12,
              padding: 24,
              height: "100%",
            }}
          >
            <div style={{ marginBottom: 12 }}>
              <b>Mã đơn thuốc:</b> {detailData.requestId}
            </div>
            <div style={{ marginBottom: 12 }}>
              <b>Tên đơn thuốc:</b> {detailData.requestName}
            </div>
            <div style={{ marginBottom: 12 }}>
              <b>Ngày dùng:</b>{" "}
              {detailData.date
                ? new Date(detailData.date).toLocaleDateString("vi-VN", {
                    day: "2-digit",
                    month: "2-digit",
                    year: "numeric",
                  })
                : "---"}
            </div>
            <div style={{ marginBottom: 12 }}>
              <b>Ghi chú:</b> {detailData.note}
            </div>
            <div style={{ marginBottom: 12 }}>
              <b>Trạng thái:</b>{" "}
              <span
                style={{
                  background:
                    detailData.status === "COMPLETED"
                      ? "#4caf50"
                      : detailData.status === "CONFIRMED"
                      ? "#1976d2"
                      : detailData.status === "PROCESSING"
                      ? "#ff9800"
                      : detailData.status === "CANCLE" ||
                        detailData.status === "CANCELLED"
                      ? "#f44336"
                      : "#888",
                  color: "#fff",
                  padding: "4px 0",
                  borderRadius: 6,
                  fontWeight: "bold",
                  fontSize: 14,
                  letterSpacing: 1,
                  display: "inline-block",
                  minWidth: 130,
                  textAlign: "center",
                }}
              >
                {detailData.status === "COMPLETED"
                  ? "Đã cho uống"
                  : detailData.status === "CONFIRMED"
                  ? "Đã duyệt"
                  : detailData.status === "PROCESSING"
                  ? "Chờ duyệt"
                  : detailData.status === "CANCLE" ||
                    detailData.status === "CANCELLED"
                  ? "Bị từ chối"
                  : detailData.status}
              </span>
              {(detailData.status === "CANCLE" ||
                detailData.status === "CANCELLED") && (
                <div
                  style={{ color: "#f44336", fontWeight: 500, marginTop: 4 }}
                >
                  Lý do: {detailData.reason ?? ""}
                </div>
              )}
            </div>
            <div style={{ marginBottom: 12 }}>
              <b>Học sinh nhận thuốc:</b> {detailData.studentDTO.fullName}
            </div>
            <div style={{ marginBottom: 12 }}>
              <b>Y tá phụ trách:</b>{" "}
              {detailData.nurseDTO && detailData.nurseDTO.fullName
                ? detailData.nurseDTO.fullName
                : "chưa có"}
            </div>
            {detailData.image && (
              <div style={{ marginBottom: 16 }}>
                <b>Ảnh đơn thuốc:</b>
                <br />
                <Image
                  src={detailData.image}
                  alt="Ảnh đơn thuốc"
                  style={{
                    maxWidth: 220,
                    maxHeight: 220,
                    borderRadius: 8,
                    border: "1px solid #eee",
                    marginTop: 8,
                    background: "#fff",
                  }}
                  preview={true}
                />
              </div>
            )}
          </div>
        </Col>
        {/* Chi tiết đơn thuốc */}
        <Col xs={24} md={12}>
          <div
            style={{
              background: "#f6fbff",
              borderRadius: 12,
              padding: 24,
              height: "100%",
            }}
          >
            <div style={{ fontWeight: "bold", marginBottom: 12 }}>
              Chi tiết đơn thuốc:
            </div>
            {detailData.medicalRequestDetailDTO?.map((item, idx) => (
              <div
                key={item.detailId || idx}
                style={{
                  background: "#e3f2fd",
                  borderRadius: 8,
                  padding: 12,
                  marginBottom: 10,
                }}
              >
                <div>
                  <b>Tên thuốc:</b> {item.medicineName}
                </div>
                <div>
                  <b>Liều lượng:</b> {item.quantity} {item.type}
                </div>
                <div>
                  <b>Cách dùng:</b> {item.method}
                </div>
                <div>
                  <b>Thời gian:</b> {item.timeSchedule}
                </div>
                <div>
                  <b>Trạng thái uống thuốc:</b>{" "}
                  {item.status === "TAKEN" ? (
                    <span style={{ color: "#21ba45" }}>Đã cho uống</span>
                  ) : (
                    <span style={{ color: "#faad14" }}>Chưa cho uống</span>
                  )}
                  {item.note && (
                    <div>
                      <b>Ghi chú của y tá:</b> {item.note}
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        </Col>
      </Row>
    ) : (
      <div>Không có dữ liệu</div>
    )}
  </Modal>
);

export default SendMedicineDetailModal;
