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
    ) : Array.isArray(detailData) && detailData.length > 0 ? (
      detailData.map((group, idx) => (
        <Row gutter={32} key={idx} style={{ marginBottom: 24 }}>
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
                <b>Mã đơn thuốc:</b> {group.requestId}
              </div>
              <div style={{ marginBottom: 12 }}>
                <b>Tên đơn thuốc:</b> {group.requestName}
              </div>
              <div style={{ marginBottom: 12 }}>
                <b>Ngày dùng:</b>{" "}
                {group.date
                  ? new Date(group.date).toLocaleDateString("vi-VN", {
                      day: "2-digit",
                      month: "2-digit",
                      year: "numeric",
                    })
                  : "---"}
              </div>
              <div style={{ marginBottom: 12 }}>
                <b>Ghi chú:</b> {group.note}
              </div>
              <div style={{ marginBottom: 12 }}>
                <b>Trạng thái:</b> {group.status}
              </div>
              <div style={{ marginBottom: 12 }}>
                <b>Học sinh nhận thuốc:</b> {group.studentDTO.fullName}
              </div>
              <div style={{ marginBottom: 12 }}>
                <b>Y tá phụ trách:</b>{" "}
                {group.nurseDTO && group.nurseDTO.fullName
                  ? group.nurseDTO.fullName
                  : "chưa có"}
              </div>
              {group.image && (
                <div style={{ marginBottom: 16 }}>
                  <b>Ảnh đơn thuốc:</b>
                  <br />
                  <Image
                    src={group.image}
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
                Chi tiết đơn thuốc (Khung giờ: {group.timeScheduleGroup}):
              </div>
              {group.medicalRequestDetailDTO.map((item, idx2) => (
                <div
                  key={item.detailId || idx2}
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
                  </div>
                  {item.note && (
                    <div>
                      <b>Ghi chú của y tá:</b> {item.note}
                    </div>
                  )}
                </div>
              ))}
            </div>
          </Col>
        </Row>
      ))
    ) : (
      <div>Không có dữ liệu</div>
    )}
  </Modal>
);

export default SendMedicineDetailModal;
