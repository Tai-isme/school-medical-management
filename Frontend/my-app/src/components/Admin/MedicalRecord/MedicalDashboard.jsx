import React, { useState } from "react";
import StudentList from "./StudentList";
import StudentProfileCard from "./StudentProfileCard";
import EmergencyContact from "./EmergencyContact";
import MedicalHistory from "./MedicalHistory";

export default function MedicalDashboard() {
  const [selectedStudent, setSelectedStudent] = useState(null);

  return (
    <div
      style={{
        minHeight: "100vh",
        width: "100%",
        marginLeft: 220, // hoặc đúng bằng chiều rộng sidebar của bạn
        background: "#f4f8fb",
        fontFamily: "Segoe UI, Arial, sans-serif",
        overflowX: "hidden",
      }}
    >
      <h2
        style={{
          textAlign: "left",
          fontWeight: "bold",
          fontSize: 24,
          margin: "0 0 0px 32px",
          letterSpacing: 1,
          color: "#1976d2",
        }}
      >
        Quản lý hồ sơ học sinh
      </h2>
      <div
        style={{
          display: "flex",
          height: "calc(100vh - 64px)",
          gap: 32,
          padding: 24,
          boxSizing: "border-box",
        }}
      >
        {/* Danh sách học sinh */}
        <div
          style={{
            flex: 2,
            borderRadius: 16,
            background: "#fff",
            boxShadow: "0 2px 16px #0001",
            minWidth: 340,
            maxWidth: 420,
            padding: 0,
            height: "100%",
            maxHeight: "calc(100vh - 120px)", // Giảm chiều cao tối đa
            overflowY: "auto",                // Cho phép cuộn bên trong
          }}
        >
          <StudentList
            onSelect={setSelectedStudent}
            selectedId={selectedStudent?.id}
            onFirstStudentLoaded={(student) => {
              if (!selectedStudent && student) setSelectedStudent(student);
            }}
          />
        </div>

        {/* Hồ sơ học sinh */}
        <div
          style={{
            flex: 3,
            border: "none",
            borderRadius: 16,
            background: "#fff",
            boxShadow: "0 2px 16px #0001",
            minWidth: 600,
            maxWidth: 800,
            padding: 0,
            margin: "0 0 0 0",
            display: "flex",
            alignItems: "flex-start",
            justifyContent: "center",
            height: "100%",
          }}
        >
          <div style={{ width: "100%", height: "100%" }}>
            <StudentProfileCard
              studentId={selectedStudent?.id}
              studentInfo={selectedStudent}
            />
          </div>
        </div>

        {/* Thông tin phụ huynh & lịch sử y tế */}
        <div
          style={{
            flex: 2,
            display: "flex",
            flexDirection: "column",
            height: "100%",
            minWidth: 340,
            maxWidth: 420,
            gap: 24,
            maxHeight: "calc(100vh - 120px)", // Giảm chiều cao tối đa
            overflowY: "auto",                // Cho phép cuộn bên trong
          }}
        >
          <div
            style={{
              border: "none",
              borderRadius: 16,
              background: "#fff",
              boxShadow: "0 2px 16px #0001",
              marginBottom: 0,
              overflow: "auto",
              padding: 0,
            }}
          >
            <EmergencyContact parentInfo={selectedStudent?.userDTO} />
          </div>
          <div
            style={{
              border: "none",
              borderRadius: 16,
              background: "#fff",
              boxShadow: "0 2px 16px #0001",
              flex: 1,
              overflow: "auto",
              padding: 0,
            }}
          >
            <MedicalHistory studentId={selectedStudent?.id} />
          </div>
        </div>
      </div>
    </div>
  );
}