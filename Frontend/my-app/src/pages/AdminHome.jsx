import React, { useState } from "react";
import Sidebar from "../components/Admin/Sidebar";
import ClassList from "../components/Admin/ClassList";
import StudentList from "../components/Admin/StudentList";
import MedicalRecordDetail from "../components/Admin/MedicalRecordDetail";
import "../pages/AdminHome.css";

export default function AdminHome() {
  const [activeTab, setActiveTab] = useState("");
  const [selectedClass, setSelectedClass] = useState("");
  const [selectedStudent, setSelectedStudent] = useState(null);

  const showClassTabs = ["hoso", "kham", "sucoyte", "vaccine"];

  return (
    <div className="app-container" style={{ display: "flex", height: "100vh" }}>
      <Sidebar activeTab={activeTab} onChangeTab={setActiveTab} />

      {showClassTabs.includes(activeTab) && (
        <div style={{ display: "flex", flex: 1 }}>
          {/* Cột giữa: Class + Student chung 1 khối */}
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

          {/* Cột phải: Thông tin chi tiết */}
          <div style={{ flex: 1, overflowY: "auto", padding: 16 }}>
            <MedicalRecordDetail student={selectedStudent} />
          </div>
        </div>
      )}
    </div>
  );
}
