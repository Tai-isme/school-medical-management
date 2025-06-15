import React, { useState, useEffect } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import './StudentProfile.css';
import axios from 'axios';
import StudentInfoCard from '../../common/StudentInfoCard';


const StudentProfile = () => {
  const [activeTab, setActiveTab] = useState('general');
  const [isEditing, setIsEditing] = useState(false);
  const [students, setStudents] = useState([]);
  const [selectedStudentId, setSelectedStudentId] = useState(null);
  const [studentInfo, setStudentInfo] = useState({
    eyes: '',
    ears: '',
    weight: '',
    height: '',
    allergies: '',
    chronicDiseases: '',
    medicalHistory: '',
    lastUpdated: '',
    note: '',
  });
  const [vaccineHistory, setVaccineHistory] = useState([]);

  // Lấy students từ localStorage
  useEffect(() => {
    const studentsData = JSON.parse(localStorage.getItem('students')) || [];
    setStudents(studentsData);
    if (studentsData.length > 0) setSelectedStudentId(studentsData[0].id);
  }, []);

  useEffect(() => {
    if (!selectedStudentId) return;

    const fetchStudentDetail = async () => {
      try {
        const token = localStorage.getItem('token');
        const res = await axios.get(
          `http://localhost:8080/api/parent/medical-records/${selectedStudentId}`,
          { headers: { Authorization: `Bearer ${token}` } }
        );
        const data = res.data;
        setStudentInfo({
          eyes: data.vision || '',
          ears: data.hearing || '',
          weight: data.weight || '',
          height: data.height || '',
          allergies: data.allergies || '',
          chronicDiseases: data.chronicDisease || '',
          medicalHistory: data.treatmentHistory || '',
          lastUpdated: data.lastUpdate || '',
          note: data.note || '',
        });
        setVaccineHistory(data.vaccineHistories || []);
      } catch (err) {
        // alert('Không lấy được thông tin học sinh!');
        setStudentInfo({
          eyes: '',
          ears: '',
          weight: '',
          height: '',
          allergies: '',
          chronicDiseases: '',
          medicalHistory: '',
          lastUpdated: '',
          note: '',
        });
        setVaccineHistory([]);
      }
    };

    fetchStudentDetail();
  }, [selectedStudentId, students]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setStudentInfo({
      ...studentInfo,
      [name]: value,
    });
  };

  const handleEditClick = () => {
    setIsEditing(true);
  };

  const handleSaveClick = async () => {
    const now = new Date().toISOString();

    setIsEditing(false);

    const body = {
      studentId: selectedStudentId,
      allergies: studentInfo.allergies,
      chronicDisease: studentInfo.chronicDiseases,
      treatmentHistory: studentInfo.medicalHistory,
      vision: studentInfo.eyes,
      hearing: studentInfo.ears,
      weight: Number(studentInfo.weight),
      height: Number(studentInfo.height),
      lastUpdate: now, // gửi đúng thời điểm hiện tại
      note: studentInfo.note,
    };

    try {
      const token = localStorage.getItem('token');
      await axios.put(
        `http://localhost:8080/api/parent/medical-records/${selectedStudentId}`,
        body,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      // Sau khi lưu thành công, cập nhật lại state để hiển thị đúng thời điểm mới nhất
      setStudentInfo(prev => ({
        ...prev,
        lastUpdated: now,
      }));
      alert('Cập nhật thành công!');
    } catch (err) {
      alert('Cập nhật thất bại!');
      console.error(err);
    }
  };

  const handleAddVaccineRow = () => {
    setVaccineHistory(prev => [
      ...prev,
      { id: Date.now(), name: '', description: '' }
    ]);
  };

  return (
    <div className="student-profile-container">
      <h1 className="main-title">Hồ sơ học sinh</h1>

      <div className="tabs">
        <button
          className={`tab-button ${activeTab === 'general' ? 'active' : ''}`}
          onClick={() => setActiveTab('general')}
        >
          Thông tin chung
        </button>
        <button
          className={`tab-button ${activeTab === 'vaccine' ? 'active' : ''}`}
          onClick={() => setActiveTab('vaccine')}
        >
          Lịch sử tiêm vaccin
        </button>
      </div>

      <div className="profile-content">
        {/* Left Section: Student Overview */}
        <div className="left-panel" >
          <StudentInfoCard onChange={setSelectedStudentId} />
        </div>

        {/* Right Section: Student Details / Vaccine History */}
        <div className="right-panel">
          {activeTab === 'general' && (
            <>
              <h2>Thông tin học sinh</h2>
              <div className="info-grid">
                <div className="info-row">
                  <label>Mắt</label>
                  <input
                    type="text"
                    name="eyes"
                    value={studentInfo.eyes}
                    onChange={handleInputChange}
                    readOnly={!isEditing}
                    className={isEditing ? 'editable' : ''}
                  />
                </div>
                <div className="info-row">
                  <label>Tai</label>
                  <input
                    type="text"
                    name="ears"
                    value={studentInfo.ears}
                    onChange={handleInputChange}
                    readOnly={!isEditing}
                    className={isEditing ? 'editable' : ''}
                  />
                </div>
                <div className="info-row">
                  <label>Cân nặng</label>
                  <input
                    type="text"
                    name="weight"
                    value={studentInfo.weight}
                    onChange={handleInputChange}
                    readOnly={!isEditing}
                    className={isEditing ? 'editable' : ''}
                  />
                </div>
                <div className="info-row">
                  <label>Chiều cao</label>
                  <input
                    type="text"
                    name="height"
                    value={studentInfo.height}
                    onChange={handleInputChange}
                    readOnly={!isEditing}
                    className={isEditing ? 'editable' : ''}
                  />
                </div>
                <div className="info-row">
                  <label>Dị ứng</label>
                  <input
                    type="text"
                    name="allergies"
                    value={studentInfo.allergies}
                    onChange={handleInputChange}
                    readOnly={!isEditing}
                    className={isEditing ? 'editable' : ''}
                  />
                </div>
                <div className="info-row">
                  <label>Bệnh mãn tính</label>
                  <input
                    type="text"
                    name="chronicDiseases"
                    value={studentInfo.chronicDiseases} // phải là chronicDiseases
                    onChange={handleInputChange}
                    readOnly={!isEditing}
                    className={isEditing ? 'editable' : ''}
                  />
                </div>
                <div className="info-row">
                  <label>Lịch sử điều trị</label>
                  <input
                    type="text"
                    name="medicalHistory"
                    value={studentInfo.medicalHistory}
                    onChange={handleInputChange}
                    readOnly={!isEditing}
                    className={isEditing ? 'editable' : ''}
                  />
                </div>
                <div className="info-row">
                  <label>Lần cập nhật gần đây</label>
                  <input
                    type="text"
                    name="lastUpdated"
                    value={studentInfo.lastUpdated}
                    onChange={handleInputChange}
                    readOnly
                    className="read-only"
                  />
                </div>
                <div className="info-row full-width">
                  <label>Ghi chú</label>
                  <textarea
                    name="note"
                    value={studentInfo.note}
                    onChange={handleInputChange}
                    readOnly={!isEditing}
                    className={isEditing ? 'editable' : ''}
                    rows="3"
                  ></textarea>
                </div>
              </div>
            </>
          )}

          {activeTab === 'vaccine' && (
            <div className="vaccine-history-section">
              <h2>Các loại vaccin đã tiêm</h2>
              <div className="vaccine-table-wrapper">
                <table>
                  <thead>
                    <tr>
                      <th>Tên Vaccin</th>
                      <th>Mô tả</th>
                    </tr>
                  </thead>
                  <tbody>
                    {vaccineHistory.map(vaccine => (
    <tr key={vaccine.id}>
      <td>
        <input
          type="text"
          value={vaccine.name}
          onChange={e => {
            const value = e.target.value;
            setVaccineHistory(prev =>
              prev.map(item =>
                item.id === vaccine.id ? { ...item, name: value } : item
              )
            );
          }}
        />
      </td>
      <td>
        <input
          type="text"
          value={vaccine.description}
          onChange={e => {
            const value = e.target.value;
            setVaccineHistory(prev =>
              prev.map(item =>
                item.id === vaccine.id ? { ...item, description: value } : item
              )
            );
          }}
        />
      </td>
    </tr>
  ))}
                  </tbody>
                </table>
              </div>
              {/* Đặt nút bên ngoài table wrapper và căn trái */}
              <button onClick={handleAddVaccineRow} className="add-row-button table-bottom-left">
                + Thêm dòng
              </button>
            </div>
          )}

          <div className="buttons-container">
            {!isEditing ? (
              <button onClick={handleEditClick} className="action-button edit-button">
                Chỉnh sửa
              </button>
            ) : (
              <button onClick={handleSaveClick} className="action-button save-button">
                Lưu
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default StudentProfile;