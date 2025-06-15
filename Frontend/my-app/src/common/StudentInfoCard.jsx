import React, { useState, useEffect } from "react";
import { Select, Typography, Avatar } from "antd";
const { Title, Text } = Typography;

export default function StudentInfoCard({ onChange }) {
  const [students, setStudents] = useState([]);
  const [selectedStudentId, setSelectedStudentId] = useState();

  useEffect(() => {
    const stored = JSON.parse(localStorage.getItem("students") || "[]");
    setStudents(stored);
    if (stored.length > 0) setSelectedStudentId(stored[0].id);
  }, []);

  useEffect(() => {
    if (onChange && selectedStudentId) onChange(selectedStudentId);
  }, [selectedStudentId, onChange]);

  const selectedStudent = students.find(s => s.id === selectedStudentId);

  return (
    <div
      style={{
        background: "#f6fbff",
        borderRadius: 16,
        padding: 24,
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        minWidth: 452,
      }}
    >
      <Title level={3} style={{ color: "#8ab4e6" }}>Học sinh</Title>
      <Select
        style={{ width: "100%", marginBottom: 24 }}
        value={selectedStudentId}
        onChange={setSelectedStudentId}
        options={students.map(s => ({
          value: s.id,
          label: s.fullName || s.name || s.id
        }))}
        placeholder="Chọn học sinh"
      />
      <Avatar
        size={120}
        src={selectedStudent?.avatar}
        style={{ marginBottom: 16, background: "#e3f2fd" }}
        icon={<img src="https://cdn-icons-png.flaticon.com/512/847/847969.png" alt="avatar" />}
      />
      <Text strong>{selectedStudent?.fullName || selectedStudent?.name || "--"}</Text>
      <br />
      <Text>{selectedStudent?.id || "--"}</Text>
      <br />
      <Text type="secondary">Lớp: {selectedStudent?.className || selectedStudent?.classID || "--"}</Text>
    </div>
  );
}