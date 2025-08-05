import React from "react";
import { Modal, Spin, Collapse, Image } from "antd"; // Đã dùng Image

const { Panel } = Collapse;

const MedicalEventDetailModal = ({ visible, event, onClose, loading }) => {
  if (!event) return null;

  const student = event.studentDTO || {};
  const nurse = event.nurseDTO || {};

  const getLevelCheckInfo = (level) => {
    switch (level) {
      case "LOW":
        return { text: "THẤP", color: "green" };
      case "MEDIUM":
        return { text: "TRUNG BÌNH", color: "orange" };
      case "HIGH":
        return { text: "CAO", color: "red" };
      default:
        return { text: "---", color: "#333" };
    }
  };
  const levelInfo = getLevelCheckInfo(event.levelCheck);

  return (
    <Modal
      visible={visible}
      onCancel={onClose}
      footer={null}
      confirmLoading={loading}
      centered
      width={700}
    >
      {loading ? (
        <div style={{ textAlign: "center", padding: 48 }}>
          <Spin size="large" />
        </div>
      ) : (
        <div>
          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              gap: "2rem",
            }}
          >
            {/* Cột trái: Thông tin sự kiện y tế */}
            <div style={{ flex: 1 }}>
              <h4
                style={{
                  marginBottom: "1rem",
                  fontSize: "1.2rem",
                  color: "#333",
                  textAlign: "center",
                }}
              >
                Thông tin sự cố
              </h4>
              <div
                style={{
                  flex: 1,
                  backgroundColor: "#fffbe6",
                  border: "1px solid #ffe58f",
                  padding: "1rem",
                  borderRadius: "8px",
                }}
              >
                <p>
                  <b>Sự cố:</b> {event.typeEvent || "---"}
                </p>
                <p>
                  <b>Thời điểm xảy ra:</b>{" "}
                  {new Date(event.date).toLocaleString("vi-VN", {
                    day: "2-digit",
                    month: "2-digit",
                    year: "numeric",
                    hour: "2-digit",
                    minute: "2-digit",
                  }) || "---"}
                </p>
                
                <p style={{ wordBreak: "break-word", whiteSpace: "pre-wrap" }}>
                  <b>Y tá xử lý:</b> {event.actionsTaken || "---"}
                </p>
                <p style={{ wordBreak: "break-word", whiteSpace: "pre-wrap" }}>
                  <b>Mô tả:</b> {event.description || "---"}
                </p>
                <p>
                  <b>Độ nguy hiểm:</b>{" "}
                  <span
                    style={{
                      fontWeight: "bold",
                      color: levelInfo.color,
                      textTransform: "uppercase",
                    }}
                  >
                    {levelInfo.text}
                  </span>
                </p>
                <p>
                  <b>Địa điểm:</b> {event.location || "---"}
                </p>
                <p>
                  <b>Hình ảnh sự cố:</b>
                  {event.image ? (
                    <div style={{ marginTop: 8 }}>
                      <Image
                        src={event.image}
                        alt="Ảnh sự kiện"
                        style={{
                          maxWidth: "100%",
                          maxHeight: "300px",
                          borderRadius: 8,
                          border: "1px solid #eee",
                          background: "#fff",
                        }}
                        preview={true}
                      />
                    </div>
                  ) : (
                    " ---"
                  )}
                </p>
              </div>
            </div>

            {/* Cột phải: Học sinh và Y tá */}
            <div style={{ flex: 1 }}>
              <h4
                style={{
                  marginBottom: "1rem",
                  fontSize: "1.2rem",
                  color: "#333",
                  textAlign: "center",
                }}
              >
                Học sinh
              </h4>
              <div style={{ flex: 1 }}>
                <p>
                  <b>Học sinh:</b> {student.fullName || "---"}
                </p>
                <p>
                  <b>Giới tính:</b>{" "}
                  {student.gender === "MALE"
                    ? "Nam"
                    : student.gender === "FEMALE"
                    ? "Nữ"
                    : "---"}
                </p>
                <p>
                  <b>Ngày sinh:</b>{" "}
                  {student.dob
                    ? student.dob.split("-").reverse().join("/")
                    : "---"}
                </p>
                <p>
                  <b>Lớp:</b> {student.classDTO?.className || "---"}
                </p>
                <p>
                  <b>Giáo viên chủ nhiệm:</b>{" "}
                  {student.classDTO?.teacherName || "---"}
                </p>
              </div>

              <hr style={{ margin: "16px 0" }} />

              <h4
                style={{
                  marginBottom: "1rem",
                  fontSize: "1.2rem",
                  color: "#333",
                  textAlign: "center",
                }}
              >
                🧑‍⚕️ Y tá phụ trách
              </h4>
              <div style={{ flex: 1 }}>
                <p>
                  <b>Họ tên:</b> {nurse.fullName || "---"}
                </p>
                <p>
                  <b>SĐT:</b> {nurse.phone || "---"}
                </p>
                <p>
                  <b>Email:</b> {nurse.email || "---"}
                </p>
              </div>
            </div>
          </div>
        </div>
      )}
    </Modal>
  );
};

export default MedicalEventDetailModal;
