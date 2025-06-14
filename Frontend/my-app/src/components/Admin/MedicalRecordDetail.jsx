import React, { useEffect, useState } from "react";

export default function MedicalRecordDetail({ student }) {
  const [medicalRecord, setMedicalRecord] = useState(null);
  const [activeSubTab, setActiveSubTab] = useState("info");

  useEffect(() => {
    if (!student) return;
    fetch(`http://localhost:8080/api/admin/medical-record/${student.student_id}`)
      .then((res) => res.json())
      .then((data) => setMedicalRecord(data));
  }, [student]);

  if (!student) {
    return <div style={{ padding: 20 }}>Chọn học sinh để xem chi tiết hồ sơ y tế</div>;
  }

  if (!medicalRecord) {
    return <div style={{ padding: 20 }}>Đang tải...</div>;
  }

  return (
    <div className="student-detail">
      <h2>Chi tiết hồ sơ y tế của học sinh {medicalRecord.fullName}</h2>
      <div style={{ marginBottom: "16px" }}>
        <button onClick={() => setActiveSubTab("info")}>Thông tin</button>
        <button onClick={() => setActiveSubTab("chronic")}>Bệnh mãn tính</button>
        <button onClick={() => setActiveSubTab("incident")}>Sự cố</button>
        <button onClick={() => setActiveSubTab("emergency")}>Liên hệ</button>
      </div>
      {activeSubTab === "info" && (
        <div className="section">
          {/* Thông tin sức khỏe tổng quát */}
        </div>
      )}
      {activeSubTab === "chronic" && (
        <div className="section">
          {/* Bệnh mãn tính */}
        </div>
      )}
      {activeSubTab === "incident" && (
        <div className="section">
          {/* Sự cố y tế */}
        </div>
      )}
      {activeSubTab === "emergency" && (
        <div className="section">
          {/* Liên hệ khẩn cấp */}
        </div>
      )}
    </div>
  );
}