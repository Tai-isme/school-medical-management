import React from 'react';
import { Modal, Spin } from 'antd';

const SendMedicineDetailModal = ({ open, onClose, loading, detailData }) => (
  <Modal
    open={open}
    onCancel={onClose}
    footer={null}
    width={700}
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
      <div>
        <div style={{ marginBottom: 12 }}>
          <b>Mã đơn thuốc:</b> {detailData.requestId}
        </div>
        <div style={{ marginBottom: 12 }}>
          <b>Tên đơn thuốc:</b> {detailData.requestName}
        </div>
        <div style={{ marginBottom: 12 }}>
          <b>Ngày dùng:</b> {detailData.date}
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
                  : detailData.status === "SUBMITTED"
                  ? "#1976d2"
                  : detailData.status === "PROCESSING"
                  ? "#ff9800"
                  : detailData.status === "CANCLE"
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
              textAlign: "center"
            }}
          >
            {detailData.status === "COMPLETED"
              ? "Hoàn thành"
              : detailData.status === "SUBMITTED"
              ? "Chờ duyệt"
              : detailData.status === "PROCESSING"
              ? "Đang xử lý"
              : detailData.status === "CANCLE"
              ? "Bị từ chối"
              : detailData.status}
          </span>
        </div>
        <div style={{ marginBottom: 12 }}>
          <b>Mã học sinh:</b> {detailData.studentId}
        </div>
        <div style={{ margin: "16px 0 8px", fontWeight: "bold" }}>Chi tiết đơn thuốc:</div>
        {detailData.medicalRequestDetailDTO?.map((item, idx) => (
          <div key={item.detailId || idx} style={{
            background: "#f6fbff",
            borderRadius: 8,
            padding: 12,
            marginBottom: 10
          }}>
            <div><b>Mục đích gửi thuốc:</b> {item.medicineName}</div>
            <div><b>Liều lượng:</b> {item.dosage}</div>
            <div><b>Thời gian:</b> {item.time}</div>
          </div>
        ))}
      </div>
    ) : (
      <div>Không có dữ liệu</div>
    )}
  </Modal>
);

export default SendMedicineDetailModal;