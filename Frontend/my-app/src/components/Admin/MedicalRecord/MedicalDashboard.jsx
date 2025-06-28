import React, { useState } from "react";
import StudentList from "./StudentList"
import StudentProfileCard from "./StudentProfileCard";
import EmergencyContact from "./EmergencyContact";
import MedicalHistory from "./MedicalHistory";

export default function MedicalDashboard() {
  const [selectedStudent, setSelectedStudent] = useState(null);

  return (
    <div style={{ marginLeft: 240, display: "flex", height: "100vh", gap: 16 }}>
      <div style={{ flex: 2, borderRadius: 8 }}>
        <StudentList
          onSelect={setSelectedStudent}
          selectedId={selectedStudent?.id}
          onFirstStudentLoaded={student => {
            if (!selectedStudent && student) setSelectedStudent(student);
          }}
        />
      </div>
      <div style={{ flex: 3, border: "2px solid #ffeb3b", borderRadius: 8 }}>
        <StudentProfileCard
          studentId={selectedStudent?.id}
          studentInfo={selectedStudent}
        />
      </div>
      <div style={{
        flex: 2,
        display: "flex",
        flexDirection: "column",
        height: "100%",
        width: "415px",
      }}>
        <div style={{
          border: "2px solid #f44336",
          borderRadius: "8px 8px 0 0",
          marginBottom: 16,
          overflow: "auto"
        }}>
          <EmergencyContact parentInfo={selectedStudent?.userDTO} />
        </div>
        <div style={{
          border: "2px solid #4caf50",
          borderRadius: "0 0 8px 8px",
          flex: 1,
          overflow: "auto"
        }}>
          <MedicalHistory studentId={selectedStudent?.id}/>
        </div>
      </div>
    </div>
  );
}