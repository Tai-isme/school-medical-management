// AdminHome.jsx
import React, { useState } from 'react';
import AppSidebar from '../components/Admin/Sidebar/AppSidebar';
import ClassList from '../components/Admin/Student/ClassList';
import StudentList from '../components/Admin/Student/StudentList';
import MedicalRecord from '../components/Admin/MedicalRecord/MedicalRecord';
import HealthCheckProgramList from '../components/Admin/HealthCheckProgram/HealthCheckProgramList';
import MedicalRequest from '../components/Admin/MedicalRequest/MedicalRequest';
import MedicalIncidentList from '../components/Admin/MedicalIncidentList/MedicalIncidentList';
import Dashboard from '../components/Admin/Dashboard/Dashboard';

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
    <div style={{ display: 'flex', height: '100vh' }}>
  <AppSidebar onMenuSelect={handleMenuSelect} selectedMenu={selectedMenu} />

  <div style={{ flex: 1, display: 'flex', flexDirection: 'column', overflow: 'auto' }}>
      {selectedMenu === '1' && <Dashboard />}

      {selectedMenu === '2' && <div>Quản lý giáo viên</div>}

      {selectedMenu === '3' && (
        <div style={{ display: 'flex', flex: 1 }}>
          <ClassList onSelectClass={handleSelectClass} />
          {selectedClassId && (
            <StudentList
              classId={selectedClassId}
              onSelectStudent={handleSelectStudent}
            />
          )}
          {selectedStudentId && (
            <div style={{ flex: 1, padding: 24 }}>
              <MedicalRecord selectedStudentId={selectedStudentId} />
            </div>
          )}
        </div>
      )}

      {selectedMenu === '4' && <MedicalRequest />}

      {selectedMenu === '5-2' && <HealthCheckProgramList />}

      {selectedMenu === '7' && <MedicalIncidentList />}
    </div>
  </div>
  );
}