// AdminHome.jsx
import React, { useState } from 'react';
import AppSidebar from '../components/Admin/Sidebar/AppSidebar'; // Import AppSidebar của bạn
import ClassList from '../components/Admin/Student/ClassList';
import StudentList from '../components/Admin/Student/StudentList';
import MedicalRecord from '../components/Admin/MedicalRecord/MedicalRecord'; // Import MedicalRecord nếu cần

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
      {selectedMenu === '1' && (
        <>
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
        </>
      )}
      {selectedMenu === '2'
      
      }

      {selectedMenu === '3'
      
      }

      {selectedMenu === '4'
      
      }

      {selectedMenu === '5'
      
      }
    </div>
  );
}