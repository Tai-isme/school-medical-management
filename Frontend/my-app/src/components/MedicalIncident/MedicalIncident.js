import React, { useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import './MedicalIncident.css';

const students = [
  { id: 'SE181818', name: 'Nguyễn Văn A', class: '5A', avatar: './logo512.png' },
  { id: 'SE181819', name: 'Trần Thị B', class: '4B', avatar: './logo512.png' },
  // Thêm học sinh khác nếu cần
];

const incidentsByStudent = {
  SE181818: [
    {
      id: 1,
      type: 'Học sinh bị té ngã',
      date: '11-11-1111',
      responsible: 'Nguyễn Văn A',
      description: 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa',
    },
    {
      id: 2,
      type: 'Học sinh bị té ngã 2',
      date: '11-11-11112',
      responsible: 'Nguyễn Văn 2',
      description: '2222222222',
    },
  ],
  SE181819: [
    {
      id: 2,
      type: 'Học sinh sốt cao',
      date: '22-22-2222',
      responsible: 'Nguyễn Văn B',
      description: 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb',
    },
  ],
  // Thêm các học sinh khác nếu cần
};

const MedicalIncident = () => {
  const [selectedStudentId, setSelectedStudentId] = useState(students[0].id);
  const student = students.find(s => s.id === selectedStudentId);

  // Lấy sự kiện y tế của học sinh đang chọn
  const incidents = incidentsByStudent[selectedStudentId] || [];

  return (
    <div className="medical-incident-container">
      <h1 className="main-title">Sự cố y tế</h1>
      <div className="incident-content">
        {/* Left Section: Student Overview */}
        <div className="left-panel">
          <h2>Học sinh</h2>
          <div className="incident-student-selector">
            <select
              value={selectedStudentId}
              onChange={e => setSelectedStudentId(e.target.value)}
              style={{
                padding: '6px 20px',
                borderRadius: 8,
                border: '1.5px solid #a7d9ff',
                fontSize: 18,
                width: '100%',
                marginBottom: 12,
                textAlign: 'center',
              }}
            >
              {students.map(student => (
                <option key={student.id} value={student.id}>
                  {student.name}
                </option>
              ))}
            </select>
          </div>
          <div className="avatar-section">
            <div className="avatar-placeholder">
              <img src={student.avatar} alt="Avatar" className="avatar-img" />
            </div>
          </div>
          <p className="student-id-display">
            <FontAwesomeIcon icon="id-card" /> {student.id}
          </p>
          <p className="student-class-display">Lớp: {student.class}</p>
        </div>

        {/* Right Section: Incident Details Table */}
        <div className="right-panel-incident">
          <h2>Các sự kiện</h2>
          <div className="incident-table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Sự cố</th>
                  <th>Ngày xảy ra</th>
                  <th>Nhân viên phụ trách</th>
                  <th>Mô tả chi tiết</th>
                </tr>
              </thead>
              <tbody>
                {incidents.length === 0 ? (
                  <tr>
                    <td colSpan={4} style={{ textAlign: 'center', color: '#888' }}>
                      Không có sự kiện y tế nào cho học sinh này.
                    </td>
                  </tr>
                ) : (
                  incidents.map((incident) => (
                    <tr key={incident.id}>
                      <td>{incident.type}</td>
                      <td>{incident.date}</td>
                      <td>{incident.responsible}</td>
                      <td>{incident.description}</td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MedicalIncident;