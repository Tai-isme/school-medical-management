import React, { useState } from "react";
import Sidebar from "../components/Admin/Sidebar";
import ClassList from "../components/Admin/ClassList";
import StudentList from "../components/Admin/StudentList";
import "../pages/AdminHome.css";

export default function AdminHome() {
  const [activeTab, setActiveTab] = useState("");
  const [selectedClass, setSelectedClass] = useState("");

  // Các tab cần hiện ClassList và StudentList
  const showClassTabs = ["hoso", "kham", "sucoyte", "vaccine"];

  return (
    <div className="app-container" style={{ display: "flex", height: "100vh" }}>
      <Sidebar activeTab={activeTab} onChangeTab={setActiveTab} />
      {showClassTabs.includes(activeTab) && (
        <>
          <ClassList
            selectedClass={selectedClass}
            onSelectClass={setSelectedClass}
          />
          {selectedClass && <StudentList classId={selectedClass} />}
        </>
      )}
    </div>
  );
}
