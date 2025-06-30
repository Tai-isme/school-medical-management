import React, { useState, useEffect } from 'react';
import { Table } from 'antd';
import StudentInfoCard from '../../../common/StudentInfoCard';
import './VaccineResult.css';

const VaccineResultCard = () => {
  const [students, setStudents] = useState([]);
  const [selectedStudentId, setSelectedStudentId] = useState();
  const [vaccineHistory, setVaccineHistory] = useState([]);

  useEffect(() => {
    const studentsData = JSON.parse(localStorage.getItem('students')) || [];
    setStudents(studentsData);

    const studentIdAlready = localStorage.getItem('studentIdAlready');
    if (studentIdAlready && studentIdAlready !== "null") {
      setSelectedStudentId(Number(studentIdAlready));
    }
  }, []);

  useEffect(() => {
    if (selectedStudentId) {
      fetchVaccineHistory(selectedStudentId);
    }
  }, [selectedStudentId]);

  const fetchVaccineHistory = async (studentId) => {
    try {
      const token = localStorage.getItem('token');
      const res = await fetch(
        `http://localhost:8080/api/parent/medical-records/${studentId}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      const data = await res.json();
      setVaccineHistory(data.vaccineHistories || []);
    } catch (err) {
      setVaccineHistory([]);
    }
  };

  const vaccineColumns = [
    {
      title: 'Loại Vaccin đã tiêm',
      dataIndex: 'vaccineName',
      key: 'vaccineName',
      align: 'center',
      minWidth: 250,
      render: (text) => <span style={{ fontWeight: 500 }}>{text}</span>,
    },
    {
      title: 'Mô tả',
      dataIndex: 'note',
      key: 'note',
      align: 'center',
      minWidth: 650,
    },
  ];

  const selectedStudent = students.find(s => s.id === selectedStudentId);

  return (
    <div className="student-profile-container">
      <h1 className="main-title" style={{ marginTop: '0px' }}>Kết quả tiêm vaccine</h1>
      <div className="profile-content">
        {/* Left Section: Student Overview */}
        <div className="left-panel">
          <StudentInfoCard onChange={setSelectedStudentId} />
        </div>
        {/* Right Section: Vaccine History */}
        <div className="right-panel">
          <div className="vaccine-history-section">
            <h2 style={{ textAlign: 'center', fontWeight: 'bold', marginBottom: 24 }}>
              Các loại vaccin đã tiêm cho {selectedStudent?.fullName || selectedStudent?.name}
            </h2>
            <Table
              columns={vaccineColumns}
              dataSource={vaccineHistory.map((item, idx) => ({ ...item, key: idx }))}
              pagination={{ pageSize: 5 }}
              bordered
              style={{ background: '#fff', borderRadius: 8 }}
              locale={{
                emptyText: "Chưa có dữ liệu tiêm vaccine cho học sinh này."
              }}
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default VaccineResultCard;