import React, { useState } from "react";

export default function MedicalRecordDetail({ record }) {
  const [activeSubTab, setActiveSubTab] = useState("info");

  if (!record) {
    return <div style={{ padding: 20 }}>Chọn một học sinh để xem hồ sơ sức khỏe.</div>;
  }

  return (
    <div className="student-detail">
      <h2>Hồ sơ sức khỏe</h2>

      <div style={{ marginBottom: "16px" }}>
        <button onClick={() => setActiveSubTab("info")}>Thông tin</button>
        <button onClick={() => setActiveSubTab("chronic")}>
          Bệnh mãn tính
        </button>
        <button onClick={() => setActiveSubTab("incident")}>Sự cố</button>
        <button onClick={() => setActiveSubTab("emergency")}>Liên hệ</button>
      </div>

      {/* Dữ liệu sẽ được truyền từ API vào đây */}
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