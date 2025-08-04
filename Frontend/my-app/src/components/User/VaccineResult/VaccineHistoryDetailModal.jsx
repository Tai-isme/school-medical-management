import React from "react";
import { Modal, Collapse } from "antd";

const { Panel } = Collapse;

const VaccineHistoryDetailModal = ({ open, onClose, data, loading }) => {
  // Log dữ liệu response để kiểm tra
  console.log("VaccineHistoryDetailModal data:", data);
  const result = data && data.length > 0 ? data[0] : null;
  // Nếu không có dữ liệu, không render modal
  if (!result) return null;

  const vaccineForm = result.vaccineFormDTO || {};
  const vaccineProgram = vaccineForm.vaccineProgramDTO || {};
  const vaccineName = vaccineForm.vaccineNameDTO || {};
  const nurse = vaccineForm.nurseDTO || {};
  const student = vaccineForm.studentDTO || result.studentDTO || {};

  const vaccineUrl =
    typeof vaccineName.url === "string" && vaccineName.url.startsWith("http")
      ? vaccineName.url
      : vaccineName.url
      ? `https://${vaccineName.url}`
      : null;

  const schedule =
    Array.isArray(vaccineName.vaccineUnitDTOs) && vaccineProgram.unit
      ? vaccineName.vaccineUnitDTOs.find((u) => u.unit === vaccineProgram.unit)
          ?.schedule
      : "---";

  const statusText = result.isInjected ? "Đã tiêm" : "Chưa tiêm";

  return (
    <Modal
      visible={open}
      onCancel={onClose}
      footer={null}
      confirmLoading={loading}
      centered
      width={700}
    >
      <div>
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            gap: "2rem",
          }}
        >
          {/* Cột trái: Kết quả tiêm - làm nổi bật */}
          <div style={{ flex: 1 }}>
            {/* Tiêu đề nằm ngoài khung màu xanh */}
            <h4
              style={{
                marginBottom: "1rem",
                fontSize: "1.2rem",
                color: "#333",
                textAlign: "center",
              }}
            >
              Kết quả
            </h4>

            <div
              style={{
                flex: 1,
                backgroundColor: "#f6ffed", // xanh nhạt
                border: "1px solid #b7eb8f",
                padding: "1rem",
                borderRadius: "8px",
              }}
            >
              <p>
                <b>Trạng thái tiêm:</b>{" "}
                <span
                  style={{
                    color: result.isInjected ? "#52c41a" : "#f5222d",
                    fontWeight: "bold",
                  }}
                >
                  {statusText}
                </span>
              </p>
              <p>
                <b>Ngày ghi nhận kết quả:</b>{" "}
                {result.createdAt
                  ? result.createdAt
                      .split("T")[0]
                      .split("-")
                      .reverse()
                      .join("/")
                  : "---"}
              </p>
              <p style={{ wordBreak: "break-word", whiteSpace: "pre-wrap" }}>
                <b>Phản ứng sau tiêm:</b> {result.reaction || "---"}
              </p>
              <p style={{ wordBreak: "break-word", whiteSpace: "pre-wrap" }}>
                <b>Hành động xử lý:</b> {result.actionsTaken || "---"}
              </p>
              <p style={{ wordBreak: "break-word", whiteSpace: "pre-wrap" }}>
                <b>Ghi chú của y tá:</b> {result.resultNote || "---"}
              </p>
            </div>
          </div>

          {/* Cột phải: Thông tin học sinh */}
          <div style={{ flex: 1 }}>
            {/* Tiêu đề nằm ngoài khung màu xanh */}
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
                <b>Họ tên học sinh:</b> {student.fullName || "---"}
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
                <b>Lớp:</b> {student.classDTO.className || "---"}
              </p>
              <p>
                <b>Giáo viên chủ nhiệm:</b>{" "}
                {student.classDTO.teacherName || "---"}
              </p>
            </div>
          </div>
        </div>
        <hr />

        <Collapse bordered style={{ marginTop: 16 }}>
          {/* 2. Chương trình tiêm */}
          <Panel header="📋 Thông tin chương trình tiêm" key="1">
            <p>
              <b>Tên chương trình:</b>{" "}
              {vaccineProgram.vaccineProgramName || "---"}
            </p>
            <p>
              <b>Ngày tiêm:</b>{" "}
              {vaccineProgram.startDate
                ? vaccineProgram.startDate.split("-").reverse().join("/")
                : "---"}
            </p>
            <p>
              <b>Địa điểm:</b> {vaccineProgram.location || "---"}
            </p>
            <p>
              <b>Chi tiết:</b> {vaccineProgram.description || "---"}
            </p>
          </Panel>

          {/* 3. Vắc xin */}
          <Panel header="💉 Thông tin vắc xin" key="2">
            <p>
              <b>Vắc xin đã tiêm:</b> {vaccineName.vaccineName || "---"}
            </p>
            <p>
              <b>Mũi thực hiện:</b>{" "}
              {vaccineProgram.unit ? `Mũi ${vaccineProgram.unit}` : "---"}
            </p>
            <p>
              <b>Lịch tiêm mũi này:</b>{" "}
              {Array.isArray(vaccineName.vaccineUnitDTOs) && vaccineProgram.unit
                ? vaccineName.vaccineUnitDTOs.find(
                    (u) => u.unit === vaccineProgram.unit
                  )?.schedule || "---"
                : "---"}
            </p>
            <p>
              <b>Nhà sản xuất:</b> {vaccineName.manufacture || "---"}
            </p>
            <p>
              <b>Độ tuổi áp dụng:</b>{" "}
              {vaccineName.ageFrom && vaccineName.ageTo
                ? `${vaccineName.ageFrom} - ${vaccineName.ageTo} tuổi`
                : "---"}
            </p>
            <p>
              <b>Mô tả:</b> {vaccineName.description || "---"}
            </p>

            {vaccineUrl && (
              <p>
                <b>Thông tin chi tiết vắc xin:</b>{" "}
                <a
                  href={vaccineUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  style={{ color: "#1890ff", textDecoration: "underline" }}
                >
                  Xem tại đây
                </a>
              </p>
            )}
          </Panel>

          {/* 4. Y tá */}
          <Panel header="🧑‍⚕️ Y tá phụ trách" key="3">
            <p>
              <b>Họ tên:</b> {nurse.fullName || "---"}
            </p>
            <p>
              <b>SĐT:</b> {nurse.phone || "---"}
            </p>
            <p>
              <b>Email:</b> {nurse.email || "---"}
            </p>
          </Panel>
        </Collapse>
      </div>
    </Modal>
  );
};

export default VaccineHistoryDetailModal;
