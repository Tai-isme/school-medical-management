import React, { useEffect, useState } from "react";

export default function StudentList({ classId, onSelect, selectedStudent }) {
  const [students, setStudents] = useState([]);
  const [searchQuery, setSearchQuery] = useState("");

  useEffect(() => {
    if (!classId) return;
    const token = localStorage.getItem("token");
    fetch(`http://localhost:8080/api/admin/students/${classId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((res) => res.json())
      .then((data) => setStudents(data))
      .catch((err) => console.error("Lỗi lấy danh sách học sinh:", err));
  }, [classId]);

  const filteredStudents = students.filter((student) =>
    student.fullName.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const handleSelect = (student) => {
    if (onSelect) onSelect(student);
  };

  return (
    <div
      style={{
        width: "100%",
        display: "flex",
        flexDirection: "column",
        alignItems: "flex-start",
        height: "100%", // Cho phép chiếm full chiều cao cha
      }}
    >
      <h4 style={{ marginBottom: 8 }}>Học sinh</h4>
      <input
        type="text"
        placeholder="Tìm theo tên..."
        value={searchQuery}
        onChange={(e) => setSearchQuery(e.target.value)}
        style={{
          width: "100%",
          padding: "6px 8px",
          marginBottom: "10px",
          fontSize: "14px",
          borderRadius: 4,
          border: "1px solid #ccc",
        }}
      />

      {/* VÙNG CUỘN */}
      <div
        style={{
          width: "100%",
          flex: 1,
          overflowY: "auto",
          maxHeight: "calc(84vh - 180px)", // Chiều cao khả dụng trừ header/search
        }}
      >
        {filteredStudents.map((student) => (
          <div
            key={student.student_id}
            onClick={() => handleSelect(student)}
            style={{
              padding: "6px 4px",
              cursor: "pointer",
              fontWeight:
                selectedStudent?.student_id === student.student_id
                  ? "bold"
                  : "normal",
              backgroundColor:
                selectedStudent?.student_id === student.student_id
                  ? "#e0f7fa"
                  : "transparent",
              borderRadius: "4px",
              marginBottom: 4,
            }}
          >
            {student.id + " " + student.fullName}
          </div>
        ))}
        {filteredStudents.length === 0 && (
          <p style={{ fontSize: 12 }}>Không tìm thấy học sinh.</p>
        )}
      </div>
    </div>
  );
}
