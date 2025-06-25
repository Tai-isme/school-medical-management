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
import HeaderBar from "../components/Admin/HeaderBar/HeaderBar";
import ProfilePage from "../components/Admin/HeaderBar/ProfilePage";
import FeedbackList from "../components/Admin/FeedBack/FeedbackList";

export default function AdminHome() {
  const [selectedMenu, setSelectedMenu] = useState(null);
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
      <HeaderBar />
      <HeaderBar onMenuSelect={handleMenuSelect} />

      <div
        style={{
          display: "flex",
          height: "calc(100vh - 60px)",
          paddingTop: "60px",
        }}
      >
        <AppSidebar
          onMenuSelect={handleMenuSelect}
          selectedMenu={selectedMenu}
        />

        {selectedMenu === "1" && <Dashboard />}

        {selectedMenu === "2" && <AccountManagement />}

        {selectedMenu === "3" && (
          <>
            <MedicalDashboard />
          </>
        )}

        {selectedMenu === "4" && <MedicalRequest />}

        {selectedMenu === "5"}

        {selectedMenu === "5-1" && <VaccineProgramList />}

        {selectedMenu === "5-2" && <HealthCheckProgramList />}

        {selectedMenu === "6"}

        {selectedMenu === "7" && <MedicalIncidentList />}

        {selectedMenu === "8" && <FeedbackList />}

        {selectedMenu === "logout"}

        {selectedMenu === "profile" && <ProfilePage />}
      </div>
    </div>
  );
}
