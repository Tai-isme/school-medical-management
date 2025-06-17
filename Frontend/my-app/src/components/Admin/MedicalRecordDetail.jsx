import React, { useEffect, useState } from "react";

export default function MedicalRecordDetail({ student }) {
  const [medicalRecord, setMedicalRecord] = useState(null);
  const [activeSubTab, setActiveSubTab] = useState("info_chronic");

useEffect(() => {
  if (!student) return;

  const token = localStorage.getItem("token"); // hoặc sessionStorage nếu bạn dùng session

  fetch(`http://localhost:8080/api/admin/medical-records/${student.id}`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token}`, // Token được truyền vào đây
    },
  })
    .then((res) => {
      if (!res.ok) {
        throw new Error("Lỗi khi lấy dữ liệu hồ sơ y tế");
      }
      return res.json();
    })
    .then((data) => setMedicalRecord(data))
    .catch((err) => {
      console.error("Lỗi:", err);
      setMedicalRecord(null); // hoặc hiển thị thông báo lỗi cho người dùng
    });
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
        <button onClick={() => setActiveSubTab("info_chronic")}>Thông tin & Bệnh mãn tính</button>
        <button onClick={() => setActiveSubTab("incident")}>Sự cố</button>
        <button onClick={() => setActiveSubTab("emergency")}>Liên hệ</button>
      </div>
      {activeSubTab === "info_chronic" && (
        <div className="section">
          {/* Thông tin sức khỏe tổng quát */}
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