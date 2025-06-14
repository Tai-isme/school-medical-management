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

  return (
    <div style={{ width: "200px", padding: "10px", borderRight: "1px solid #ccc" }}>
      <h4>Học sinh</h4>
      <input
        type="text"
        placeholder="Tìm theo tên..."
        value={searchQuery}
        onChange={(e) => setSearchQuery(e.target.value)}
        style={{
          width: "100%",
          padding: "4px 6px",
          marginBottom: "10px",
          fontSize: "14px",
        }}
      />
      {/* Dữ liệu sẽ được truyền từ API vào đây */}
      {filteredStudents.map((student) => (
        <div
          key={student.student_id}
          // onClick={() => onSelect(student)}
          style={{
            padding: "6px 4px",
            cursor: "pointer",
            fontWeight: selectedStudent?.student_id === student.student_id ? "bold" : "normal",
            backgroundColor:
              selectedStudent?.student_id === student.student_id ? "#e0f7fa" : "transparent",
            borderRadius: "4px",
          }}
        >
          {student.id +" "+ student.fullName}
        </div>
      ))}
      {filteredStudents.length === 0 && <p style={{ fontSize: 12 }}>Không tìm thấy học sinh.</p>}
    </div>
  );
}