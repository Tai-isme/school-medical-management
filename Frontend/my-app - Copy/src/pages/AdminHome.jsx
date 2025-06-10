import React, { useState } from "react";
import Sidebar from "../components/Admin/Sidebar";
import StudentList from "../components/Admin/StudentList";
import "../pages/AdminHome.css";

// Dữ liệu mẫu cứng
const classes = [
  {
    className: "10a5",
    students: [
      { id: 1, name: "Lê Hải Anh" }
    ]
  }
];

export default function AdminHome() {
  const [activeTab, setActiveTab] = useState("hoso");
  const [openClass, setOpenClass] = useState(null);
  const [selectedStudent, setSelectedStudent] = useState(null);
  const [query, setQuery] = useState("");

  // Lấy danh sách học sinh của lớp đang mở
  const currentStudents =
    classes.find(cls => cls.className === openClass)?.students || [];

  return (
    <div className="app-container" style={{ display: "flex", height: "100vh" }}>
      {/* Sidebar */}
      <Sidebar activeTab={activeTab} onChangeTab={setActiveTab} />

      {/* Chỉ hiện khi chọn Hồ sơ sức khỏe */}
      {activeTab === "hoso" && (
        <>
          {/* Cột danh sách lớp */}
          <div style={{ width: 180, borderRight: "1px solid #eee", background: "#fff" }}>
            <h4 style={{ margin: "16px 0 8px 16px" }}>Danh sách lớp</h4>
            {classes.map(cls => (
              <div
                key={cls.className}
                style={{
                  fontWeight: "bold",
                  padding: "8px 16px",
                  cursor: "pointer",
                  background: openClass === cls.className ? "#e0f7fa" : "transparent"
                }}
                onClick={() => {
                  setOpenClass(cls.className);
                  setSelectedStudent(null);
                  setQuery("");
                }}
              >
                {cls.className}
              </div>
            ))}
          </div>

          {/* Cột danh sách học sinh */}
          <div style={{ width: 220, borderRight: "1px solid #eee", background: "#fff" }}>
            <h4 style={{ margin: "16px 0 8px 16px" }}>Học sinh</h4>
            {openClass && (
              <StudentList
                students={currentStudents}
                query={query}
                setQuery={setQuery}
                onSelect={setSelectedStudent}
                selectedStudent={selectedStudent}
              />
            )}
          </div>

          {/* Cột chi tiết học sinh */}
          <div style={{ flex: 1, background: "#f6f6fa", padding: 32 }}>
            {selectedStudent ? (
              <div>
                <h3>Hồ sơ học sinh</h3>
                <div><b>Họ tên:</b> {selectedStudent.name}</div>
                <div><b>Mã HS:</b> {selectedStudent.id}</div>
                {/* Thêm các thông tin khác nếu muốn */}
              </div>
            ) : (
              <div style={{ color: "gray" }}>Vui lòng chọn học sinh để xem hồ sơ.</div>
            )}
          </div>
        </>
      )}
    </div>
  );
}
