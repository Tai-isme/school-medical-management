import React, { useState, useEffect } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import './StudentProfileCard.css';
import axios from 'axios';
import StudentInfoCard from '../../../common/StudentInfoCard';
import { Button, Modal, Input, Form, Table } from 'antd'; // Import Button and Modal from antd
import MedicalRecordModal from './MedicalRecordModal'; // Import MedicalRecordModal component
import { faHouse } from '@fortawesome/free-solid-svg-icons'; // Thêm dòng này vào đầu file


const StudentProfile = () => {
  const [activeTab, setActiveTab] = useState('general');
  const [isEditing, setIsEditing] = useState(false);
  const [students, setStudents] = useState([]);
  const [selectedStudentId, setSelectedStudentId] = useState();
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
  const [openMedicalForm, setOpenMedicalForm] = useState(false);
  const [editMode, setEditMode] = useState(false);
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  // Lấy students từ localStorage
  useEffect(() => {
    const studentsData = JSON.parse(localStorage.getItem('students')) || [];
    setStudents(studentsData);

    const studentIdAlready = localStorage.getItem('studentIdAlready');
    if (studentIdAlready && studentIdAlready !== "null") {
      setSelectedStudentId(Number.studentIdAlready);
    }
  }, []);

  useEffect(() => {
    if (selectedStudentId) {
      fetchStudentInfo(selectedStudentId); // Gọi API lấy thông tin học sinh
    }
  }, [selectedStudentId]);

  const fetchStudentInfo = async (studentId) => {
    try {
      const token = localStorage.getItem('token');
      const res = await axios.get(
        `http://localhost:8080/api/parent/medical-records/${studentId}`,
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

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setStudentInfo({
      ...studentInfo,
      [name]: value,
    });
  };

  const handleEditClick = () => {
    setEditMode(true);
    setOpenMedicalForm(true);
  };

  const handleCloseMedicalForm = () => {
    setOpenMedicalForm(false);
    setEditMode(false);
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
    <div className="student-profile-container" style={{ position: "relative" }}>
      {/* Nút Home ở góc trên trái */}
      <div style={{ position: "absolute", top: 16, left: 32, display: "flex", alignItems: "center", zIndex: 10 }}>
        <button
          onClick={() => window.location.href = '/'}
          style={{
            background: "#e3f2fd",
            border: "none",
            borderRadius: "50%",
            width: 40,
            height: 40,
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            boxShadow: "0 2px 8px #1976d220",
            cursor: "pointer",
            marginRight: 8
          }}
          title="Về trang chủ"
        >
          <FontAwesomeIcon icon={faHouse} style={{ color: "#1976d2", fontSize: 22 }} />
        </button>
        <span
          style={{
            color: "#1976d2",
            fontWeight: 500,
            fontSize: 15,
            background: "#e3f2fd",
            borderRadius: 8,
            padding: "4px 14px",
            cursor: "pointer"
          }}
          onClick={() => window.location.href = '/'}
          title="Về trang chủ"
        >
          Về trang chủ
        </span>
      </div>
        
      <h1 className="main-title" style={{ margin: '0px 0px 20px 0px', fontSize: '24px'  }}>Hồ sơ học sinh</h1>

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
          Lịch sử tiêm vaccine
        </button>
      </div>

      <div className="profile-content">
        {/* Left Section: Student Overview */}
        <div className="left-panel" >
          <StudentInfoCard onChange={setSelectedStudentId} />
        </div>

        {/* Right Section: Student Details / Vaccine History */}
        <div className="right-panel">
          {(!studentInfo || Object.values(studentInfo).every(val => !val)) ? (
  <div
    style={{
      background: "#fffbe6",
      border: "1px solid #ffe58f",
      borderRadius: 8,
      padding: 32,
      textAlign: "center",
      marginTop: 24,
    }}
  >
    <div style={{ fontSize: 18, marginBottom: 16 }}>
      Hồ sơ sức khỏe của học sinh <b>{selectedStudent?.fullName || selectedStudent?.name}</b> chưa được khai báo.<br />
      Vui lòng nhấn vào nút bên dưới để khai báo hồ sơ học sinh.
    </div>
    <button
      style={{
        background: "#1976d2",
        color: "#fff",
        border: "none",
        borderRadius: 6,
        padding: "10px 24px",
        fontWeight: "bold",
        fontSize: 16,
        cursor: "pointer",
      }}
      onClick={() => setOpenMedicalForm(true)} // Viết hàm này để mở form khai báo
    >
      Tiến hành khai báo hồ sơ
    </button>
  </div>
) : (
            <>
              {activeTab === 'general' && (
                <>
                  <h2 style={{margin: '0px 0px 20px 0px'}}>Hồ sơ sức khỏe</h2>
                  <div className="info-grid">
                    <div className="info-row">
                      <label>Thị giác</label>
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
                      <label>Thính giác</label>
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
                      <label>Cân nặng (kg)</label>
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
                      <label>Chiều cao (cm) </label>
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
                      <label>Dị ứng với</label>
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
                  <h2 style={{ textAlign: 'center', fontWeight: 'bold', marginBottom: 24 }}>Các loại vaccin đã tiêm</h2>
                  <Table
                    columns={vaccineColumns}
                    dataSource={vaccineHistory.map((item, idx) => ({ ...item, key: idx }))}
                    pagination={{ pageSize: 5 }}
                    bordered
                    style={{ background: '#fff', borderRadius: 8 }}
                  />
                </div>
              )}
            </>
          )}

          <div className="buttons-container">
            {activeTab === 'general' && (
              <>
                {/* Chỉ hiện nút nếu đã có hồ sơ */}
                {studentInfo && Object.values(studentInfo).some(val => val) && (
                  !isEditing ? (
                    <Button type="primary" onClick={handleEditClick}>
                      <FontAwesomeIcon icon="fa-solid fa-pen-to-square" /> Chỉnh sửa
                    </Button>
                  ) : (
                    <Button type="primary" onClick={handleSaveClick}>
                      <FontAwesomeIcon icon="fa-solid fa-floppy-disk" /> Lưu
                    </Button>
                  )
                )}
              </>
            )}
          </div>
        </div>
      </div>

      <MedicalRecordModal
        open={openMedicalForm}
        onCancel={handleCloseMedicalForm}
        loading={loading}
        studentId={selectedStudentId}
        fetchStudentInfo={fetchStudentInfo}
        initialValues={{
          allergies: studentInfo.allergies,
          chronicDisease: studentInfo.chronicDiseases,
          treatmentHistory: studentInfo.medicalHistory,
          vision: studentInfo.eyes,
          hearing: studentInfo.ears,
          weight: studentInfo.weight,
          height: studentInfo.height,
          note: studentInfo.note,
          vaccineHistories: vaccineHistory.length > 0 ? vaccineHistory : [{ vaccineName: "", note: "" }]
        }}
        editMode={editMode}
      />
    </div>
  );
};

export default StudentProfile;