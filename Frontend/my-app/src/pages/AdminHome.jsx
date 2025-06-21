// AdminHome.jsx
import React, { useState } from 'react';
import AppSidebar from '../components/Admin/Sidebar/AppSidebar';
import ClassList from '../components/Admin/Student/ClassList';
import StudentList from '../components/Admin/Student/StudentList';
import MedicalRecord from '../components/Admin/MedicalRecord/MedicalRecord';
import HealthCheckProgramList from '../components/Admin/HealthCheckProgram/HealthCheckProgramList';
import MedicalRequest from '../components/Admin/MedicalRequest/MedicalRequest';
import MedicalIncidentList from '../components/Admin/MedicalIncidentList/MedicalIncidentList';
import MedicalDashboard from '../components/Admin/MedicalRecord/MedicalDashboard';

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
      

      {selectedMenu === '1'
      
      }

      {selectedMenu === '2'
      
      }

      {/* {selectedMenu === '3' && (
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
      )} */}

      {selectedMenu === '3' && (
        <>
          <MedicalDashboard/>
        </>
      )}

      {selectedMenu === '4' && <MedicalRequest/>}

      {selectedMenu === '5'
      
      }

      {selectedMenu === '5-1'
      
      }

      {selectedMenu === '5-2' && <HealthCheckProgramList />}

      {selectedMenu === '6'
      
      }

      {selectedMenu === '7' && <MedicalIncidentList />
      
      }

      {selectedMenu === 'logout'
      
      }

    </div>
  );
}