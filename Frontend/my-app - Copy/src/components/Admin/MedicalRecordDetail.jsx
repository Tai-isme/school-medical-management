import React, { useState } from "react";
import StudentList from "./StudentList";

export default function MedicalRecordDetail() {
  // Dữ liệu mẫu cứng
  const students = [
    {
      id: 1,
      name: "Nguyễn Văn A",
      className: "10A1",
      allergies: "1",
      note: "1",
      vision: "1",
      hearing: "1",
      weight: "1",
      height: "1",
      updatedAt: "1",
    },
  ];

  const [selectedRecord, setSelectedRecord] = useState(null);

  const handleSelectStudent = (student) => {
    setSelectedRecord(student);
  };

  return (
    <div style={{ display: "flex" }}>
      <StudentList students={students} onSelect={handleSelectStudent} />
      <div style={{ flex: 1, marginLeft: 24 }}>
        {selectedRecord ? (
          <div>
            <h3>Thông tin sức khỏe tổng quát</h3>
            <div>
              <b>Họ tên:</b> {selectedRecord.name}
            </div>
            <div>
              <b>Lớp:</b> {selectedRecord.className}
            </div>
            <div>
              <b>Dị ứng:</b> {selectedRecord.allergies}
            </div>
            <div>
              <b>Ghi chú:</b> {selectedRecord.note}
            </div>
            <div>
              <b>Thị lực:</b> {selectedRecord.vision}
            </div>
            <div>
              <b>Thính lực:</b> {selectedRecord.hearing}
            </div>
            <div>
              <b>Cân nặng:</b> {selectedRecord.weight} kg
            </div>
            <div>
              <b>Chiều cao:</b> {selectedRecord.height} cm
            </div>
            <div>
              <b>Ngày cập nhật:</b> {selectedRecord.updatedAt}
            </div>
          </div>
        ) : (
          <div>Chọn học sinh để xem thông tin sức khỏe.</div>
        )}
      </div>
    </div>
  );
}
