import React from "react";
import { Modal, Spin, Collapse } from "antd";

const { Panel } = Collapse;

const HealthCheckResultDetailModal = ({ open, onClose, data, loading }) => {
  console.log("Selected data:", data);
  if (!data) return null;

  const student = data.studentDTO || {};
  const program = data.healthCheckFormDTO?.healthCheckProgramDTO || {};
  const nurse = data.healthCheckFormDTO?.nurseDTO || {};

  const statusText = data.isChecked ? "Đã khám" : "Chưa khám";

  const formatDate = (dateString) => {
    return dateString ? dateString.split("-").reverse().join("/") : "---";
  };

  const calculateAge = (dobString) => {
    if (!dobString) return null;
    const today = new Date();
    const dob = new Date(dobString);
    let age = today.getFullYear() - dob.getFullYear();
    const m = today.getMonth() - dob.getMonth();
    if (m < 0 || (m === 0 && today.getDate() < dob.getDate())) {
      age--;
    }
    return age;
  };

  function getHeartRateStatus(heartRate, age) {
    let status = "BÌNH THƯỜNG";
    if (age <= 1)
      status =
        heartRate < 80 ? "THẤP" : heartRate > 160 ? "CAO" : "BÌNH THƯỜNG";
    else if (age <= 2)
      status =
        heartRate < 80 ? "THẤP" : heartRate > 130 ? "CAO" : "BÌNH THƯỜNG";
    else if (age <= 5)
      status =
        heartRate < 80 ? "THẤP" : heartRate > 120 ? "CAO" : "BÌNH THƯỜNG";
    else if (age <= 11)
      status =
        heartRate < 75 ? "THẤP" : heartRate > 110 ? "CAO" : "BÌNH THƯỜNG";
    else
      status =
        heartRate < 60 ? "THẤP" : heartRate > 100 ? "CAO" : "BÌNH THƯỜNG";

    const color =
      status === "CAO" ? "red" : status === "THẤP" ? "orange" : "green";

    return { status, color };
  }

  const age = student.dob ? calculateAge(student.dob) : null;
  const heartRateInfo = (() => {
    if (!data.heartRate || age === null) return "---";
    const { status, color } = getHeartRateStatus(data.heartRate, age);
    return (
      <>
        {data.heartRate} bpm{" "}
        <span
          style={{
            fontWeight: "bold",
            color,
            textTransform: "uppercase",
          }}
        >
          ({status})
        </span>
      </>
    );
  })();

  const calculateBMI = (weight, height) => {
    if (weight != null && height != null && weight > 0 && height > 0) {
      const heightInMeters = height / 100;
      const bmi = weight / (heightInMeters * heightInMeters);
      return bmi.toFixed(2);
    }
    return null;
  };

  const getBMIStatusAndColor = (bmi) => {
    if (bmi < 18.5) return { status: "THIẾU CÂN", color: "orange" };
    if (bmi < 24.9) return { status: "BÌNH THƯỜNG", color: "green" };
    if (bmi < 29.9) return { status: "THỪA CÂN", color: "darkorange" };
    return { status: "BÉO PHÌ", color: "red" };
  };
  const bmiValue = calculateBMI(data.weight, data.height);
  const bmiInfo = bmiValue
    ? (() => {
        const { status, color } = getBMIStatusAndColor(bmiValue);
        return (
          <>
            {bmiValue}{" "}
            <span
              style={{
                fontWeight: "bold",
                color,
                textTransform: "uppercase",
              }}
            >
              ({status})
            </span>
          </>
        );
      })()
    : "---";

  return (
    <Modal
      open={open}
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
            {/* Cột trái: Kết quả khám sức khỏe */}
            <div style={{ flex: 1 }}>
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
                  backgroundColor: "#f6ffed",
                  border: "1px solid #b7eb8f",
                  padding: "1rem",
                  borderRadius: "8px",
                }}
              >
                <p>
                  <b>Ngày khám:</b>{" "}
                  <span style={{ fontWeight: "bold" }}>
                    {formatDate(program.startDate)}
                  </span>
                </p>
                <p>
                  <b>Trạng thái khám:</b>{" "}
                  <span
                    style={{
                      color: data.isChecked ? "#52c41a" : "#f5222d",
                      fontWeight: "bold",
                    }}
                  >
                    {statusText}
                  </span>
                </p>
                <div style={{ display: "flex", gap: "2rem" }}>
                  <p style={{ margin: 0 }}>
                    <b>Chiều cao:</b>{" "}
                    {data.height != null
                      ? `${Math.floor(data.height / 100)}m${data.height % 100}`
                      : "---"}
                  </p>
                  <p style={{ margin: 0 }}>
                    <b>Cân nặng:</b>{" "}
                    {data.weight != null
                      ? `${data.weight.toFixed(1).replace(".", ",")} kg`
                      : "---"}
                  </p>
                </div>
                <p>
                  <b>Chỉ số BMI:</b> {bmiInfo} –{" "}
                  <a
                    href="https://tamanhhospital.vn/chi-so-bmi-binh-thuong/"
                    target="_blank"
                    rel="noopener noreferrer"
                  >
                    BMI là gì?
                  </a>
                </p>

                <p>
                  <b>Thị lực:</b> {data.vision || "---"}
                </p>
                <p>
                  <b>Thính lực:</b> {data.hearing || "---"}
                </p>
                <p>
                  <b>Răng miệng:</b> {data.dentalStatus || "---"}
                </p>
                <p>
                  <b>Huyết áp:</b> {data.bloodPressure || "---"}
                </p>
                <p>
                  <b>Nhịp tim:</b> {heartRateInfo} –{" "}
                  <a
                    href="https://www.vinmec.com/vie/bai-viet/nhip-tim-chuan-la-bao-nhieu-vi"
                    target="_blank"
                    rel="noopener noreferrer"
                  >
                    Tham khao nhịp tim chuẩn
                  </a>
                </p>

                <p>
                  <b>Kết luận:</b> {data.generalCondition || "---"}
                </p>
                <p>
                  <b>Ghi chú từ y tá:</b> {data.note || "---"}
                </p>
              </div>
            </div>

            {/* Cột phải: Thông tin học sinh */}
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
                  {student.dob ? formatDate(student.dob) : "---"}
                </p>
                <p>
                  <b>Lớp:</b> {student.classDTO?.className || "---"}
                </p>
                <p>
                  <b>Giáo viên chủ nhiệm:</b>{" "}
                  {student.classDTO?.teacherName || "---"}
                </p>
              </div>
            </div>
          </div>
          <hr />

          <Collapse bordered style={{ marginTop: 16 }}>
            {/* Panel 1: Chương trình khám */}
            <Panel header="📋 Thông tin chương trình khám" key="1">
              <p>
                <b>Tên chương trình:</b> {program.healthCheckName || "---"}
              </p>
              <p>
                <b>Ngày khám:</b>{" "}
                {program.startDate
                  ? program.startDate.split("-").reverse().join("/")
                  : "---"}
              </p>
              <p>
                <b>Mô tả:</b> {program.description || "---"}
              </p>
              <p>
                <b>Địa điểm:</b> {program.location || "---"}
              </p>
            </Panel>

            {/* Panel 3: Y tá phụ trách */}
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
      )}
    </Modal>
  );
};

export default HealthCheckResultDetailModal;
