// AdminHome.jsx
import React, { useState } from "react";
import AppSidebar from "../components/Admin/Sidebar/AppSidebar";
import ClassList from "../components/Admin/Student/ClassList";
import StudentList from "../components/Admin/Student/StudentList";
import MedicalRecord from "../components/Admin/MedicalRecord/MedicalRecord";
import HealthCheckProgramList from "../components/Admin/HealthCheckProgram/HealthCheckProgramList";
import MedicalRequest from "../components/Admin/MedicalRequest/MedicalRequest";
import MedicalIncidentList from "../components/Admin/MedicalIncidentList/MedicalIncidentList";
import VaccineProgramList from "../components/Admin/VaccineProgramList/VaccineProgramList";
import AccountManagement from "../components/Admin/AccountManagement/AccountManagement";
import MedicalDashboard from "../components/Admin/MedicalRecord/MedicalDashboard";
import Dashboard from "../components/Admin/Dashboard/Dashboard";
import FeedbackList from "../components/Admin/FeedBack/FeedbackList";

import BlogAdminPage from "../components/Admin/BlogManagement/BlogAdminPage";
import AccountInfo from "../components/Admin/Sidebar/AccountInfo";
import ChangePasswordForm from "../components/Admin/Sidebar/ChangePasswordForm";
import "./AdminHome.css"; // Import your CSS file for styling

export default function AdminHome() {
  const [selectedMenu, setSelectedMenu] = useState("1"); // <-- Đặt mặc định là "1"
  const [selectedClassId, setSelectedClassId] = useState(null);
  const [selectedStudentId, setSelectedStudentId] = useState(null);

  const handleMenuSelect = (key) => {
    setSelectedMenu(key);
    setSelectedClassId(null);
    setSelectedStudentId(null);
  };

  const handleSelectClass = (cls) => {
    setSelectedClassId(cls.classId);
    setSelectedStudentId(null);
  };

  const handleSelectStudent = (student) => {
    setSelectedStudentId(student.id);
  };

  return (
    <div>
      <div
        style={{
          display: "flex",
          height: "100vh", // Sửa lại cho sát trên cùng
          paddingTop: 0, // Xóa padding top
        }}
      >
        <AppSidebar
          onMenuSelect={handleMenuSelect}
          selectedMenu={selectedMenu}
        />

        {selectedMenu === "1" && <Dashboard />}

        {selectedMenu === "2" && <AccountManagement />}

        {selectedMenu === "3" && <MedicalDashboard />}

        {selectedMenu === "4" && <MedicalRequest />}

        {selectedMenu === "5"}

        {selectedMenu === "5-1" && <VaccineProgramList />}

        {selectedMenu === "5-2" && <HealthCheckProgramList />}

        {selectedMenu === "6"}

        {selectedMenu === "7" && <MedicalIncidentList />}

        {selectedMenu === "8" && <FeedbackList />}

        {selectedMenu === "9" && <BlogAdminPage />}

        {selectedMenu === "account-info" && <AccountInfo />}

        {selectedMenu === "changepass" && <ChangePasswordForm />}

        {/* {selectedMenu === "logout"} */}
      </div>
    </div>
  );
}
