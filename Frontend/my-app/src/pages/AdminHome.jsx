import React, { useState } from "react";
import Sidebar from "../components/Admin/Sidebar";
import ClassList from "../components/Admin/ClassList";
import StudentList from "../components/Admin/StudentList";
import MedicalRecordDetail from "../components/Admin/MedicalRecordDetail";
import MedicineRequestList from "../components/Admin/MedicineRequestList";
import PeriodicHealthCheck from "../components/Admin/PeriodicHealthCheck"; // ✅ Thêm dòng này
import "../pages/AdminHome.css";

export default function AdminHome() {
  const [activeTab, setActiveTab] = useState("");
  const [selectedClass, setSelectedClass] = useState("");
  const [selectedStudent, setSelectedStudent] = useState(null);

  const showClassTabs = ["hoso", "kham", "sucoyte", "vaccine"];

  return (
    <div className="app-container" style={{ display: "flex", height: "100vh" }}>
      <Sidebar activeTab={activeTab} onChangeTab={setActiveTab} />

      {/* Nếu là tab Yêu cầu gửi thuốc */}
      {activeTab === "yeucauguithuoc" && (
        <div style={{ flex: 1, overflowY: "auto" }}>
          <MedicineRequestList />
        </div>
      )}

      {/* Nếu là các tab cần hiện danh sách lớp và học sinh */}
      {showClassTabs.includes(activeTab) && (
        <div style={{ display: "flex", flex: 1 }}>
          {/* Sidebar lớp và học sinh */}
          <div
            style={{
              display: "flex",
              flexDirection: "column",
              width: "250px",
              borderRight: "1px solid #ccc",
              padding: "10px",
              boxSizing: "border-box",
            }}
          >
            <div style={{ marginBottom: "12px" }}>
              <ClassList
                selectedClass={selectedClass}
                onSelectClass={(cls) => {
                  setSelectedClass(cls);
                  setSelectedStudent(null);
                }}
              />
            </div>
            {selectedClass && (
              <StudentList
                classId={selectedClass}
                selectedStudent={selectedStudent}
                onSelect={setSelectedStudent}
              />
            )}
          </div>

          {/* Nội dung hiển thị bên phải */}
          <div style={{ flex: 1, overflowY: "auto", padding: 16 }}>
            {activeTab === "hoso" && <MedicalRecordDetail student={selectedStudent} />}
            {activeTab === "kham" && <PeriodicHealthCheck student={selectedStudent} />}
            {/* Bạn có thể thêm các tab khác tại đây nếu cần */}
          </div>
        </div>
      )}
    </div>
  );
}
