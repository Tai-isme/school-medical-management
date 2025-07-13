import React, { useState, useEffect } from 'react';
import './InstructionForm.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faXmark, faCaretDown, faHouse } from '@fortawesome/free-solid-svg-icons';
import MedicalRequestDetail from "./SendMedicineCardDetails";
import StudentInfoCard from '../../../common/StudentInfoCard';
import { message } from 'antd';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import Swal from 'sweetalert2';

export default function InstructionForm({ onShowHistory }) {
  // Lấy students từ localStorage
  const storedStudents = JSON.parse(localStorage.getItem('students') || '[]');
  const students = (storedStudents.length > 0
    ? storedStudents
    : [
        { id: 'SE181818', fullName: 'Nguyễn Văn A', classID: '5A' },
        { id: 'SE181819', fullName: 'Trần Thị B', classID: '4B' },
      ]
  ).map(student => ({
    ...student,
    avatar: './logo512.png' // luôn dùng avatar mặc định
  }));
  const [selectedStudentId, setSelectedStudentId] = useState(students[0]?.id || '');
  const selectedStudent = students.find(s => String(s.id) === String(selectedStudentId));

  const [medicines, setMedicines] = useState([{ name: '', quantity: '', usage: '' }]);
  const [note, setNote] = useState('');
  const [purpose, setPurpose] = useState(''); // Thêm state cho mục đích sử dụng thuốc
  const [usageTime, setUsageTime] = useState(''); // Thêm state cho thời gian sử dụng thuốc
  const [dateError, setDateError] = useState('');
  const [activeTab, setActiveTab] = useState('create'); // 'create' hoặc 'history'
  const [editingId, setEditingId] = useState(null);
  const usageTimeOptions = [
  "Sau ăn sáng từ 9h-9h30",
  "Trước ăn trưa: 10h30-11h",
  "Sau ăn trưa: từ 11h30-12h"
];  
const handleAddMedicine = () => {
    setMedicines([...medicines, { name: '', quantity: '', usage: '' }]);
  };
  const handleUsageTimeChange = (option) => {
  setUsageTime(prev =>
    prev.includes(option)
      ? prev.filter(item => item !== option)
      : [...prev, option]
  );
};

  const handleRemoveMedicine = (index) => {
    const newMedicines = medicines.filter((_, i) => i !== index);
    setMedicines(newMedicines);
  };

  const handleMedicineChange = (index, event) => {
    const { name, value } = event.target;
    const newMedicines = [...medicines];
    newMedicines[index][name] = value;
    setMedicines(newMedicines);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const result = await Swal.fire({
      title: 'Xác nhận gửi đơn thuốc?',
      text: 'Bạn có chắc chắn muốn gửi đơn thuốc này?',
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Gửi',
      cancelButtonText: 'Hủy',
    });

    if (!result.isConfirmed) return;

    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const selectedDate = new Date(usageTime);
    selectedDate.setHours(0, 0, 0, 0);

    if (selectedDate < today) {
      setDateError('Ngày dùng phải là hôm nay hoặc sau hôm nay!');
      return;
    } else {
      setDateError('');
    }

    const payload = {
      requestName: purpose,
      note: note,
      date: usageTime,
      studentId: selectedStudent.id,
      medicalRequestDetailRequests: medicines.map(med => ({
        medicineName: med.name,
        dosage: med.quantity,
        time: med.usage
      }))
    };

    try {
      const token = localStorage.getItem("token");
      let response;
      if (editingId) {
        response = await fetch(`http://localhost:8080/api/parent/medical-request/${editingId}`, {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`
          },
          body: JSON.stringify(payload)
        });
      } else {
        response = await fetch("http://localhost:8080/api/parent/medical-request", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`
          },
          body: JSON.stringify(payload)
        });
      }

      if (!response.ok) throw new Error("Có lỗi khi gửi đơn thuốc!");

      setActiveTab('history');
      setPurpose('');
      setNote('');
      setUsageTime('');
      setMedicines([{ name: '', quantity: '', usage: '' }]);
      setEditingId(null); // Reset sau khi gửi
      message.success(editingId ? 'Đã cập nhật đơn thuốc!' : 'Đơn thuốc đã được gửi thành công!');
      toast.success(editingId ? 'Đã cập nhật đơn thuốc!' : 'Đơn thuốc đã được gửi thành công!');
    } catch (error) {
      let errorMsg = 'Có lỗi xảy ra!';
      if (error && error.message) {
        try {
          const errObj = JSON.parse(error.message);
          if (errObj.message) errorMsg = errObj.message;
        } catch {
          errorMsg = error.message;
        }
      }
      message.error(errorMsg);
      toast.error(errorMsg);
    }
  };

  // Nếu đang dùng ở SendMedicineCardDetails.jsx hoặc file render bảng
const getStatusText = (status) => {
  switch (status) {
    case "PROCESSING":
      return "Chờ duyệt";
    case "SUBMITTED":
      return "Đã duyệt";
    case "COMPLETED":
      return "Đã cho uống";
    case "CANCELLED":
      return "Bị từ chối";
    default:
      return status;
  }
};

useEffect(() => {
  const handleEdit = (e) => {
    const req = e.detail;
    setPurpose(req.requestName || '');
    setNote(req.note || '');
    setUsageTime(req.date || '');
    setMedicines(
        req.medicalRequestDetailDTO && Array.isArray(req.medicalRequestDetailDTO)
        ? req.medicalRequestDetailDTO.map(item => ({
            name: item.medicineName,
            quantity: item.dosage,
            usage: item.time
          }))
        : [{ name: '', quantity: '', usage: '' }]
    );
    setSelectedStudentId(req.studentDTO?.id || '');
    setActiveTab('create');
    setEditingId(req.requestId);
  };
  window.addEventListener('edit-medicine-request', handleEdit);
  return () => window.removeEventListener('edit-medicine-request', handleEdit);
}, []);

  return (
    <div className="instruction-form-container" style={{ position: "relative" }}>
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

      {/* Tabs */}
      <h2 style={{ textAlign: 'center', marginBottom: '20px' , marginTop: '0px' }}>Gửi đơn thuốc</h2>
      <div className="tabs" style={{ display: 'flex', borderBottom: '2px solid #eee', marginBottom: 0 }}>
        <button
          onClick={() => setActiveTab('create')}
          className={activeTab === 'create' ? 'active' : ''}
          style={{
            padding: '12px 32px',
            border: 'none',
            borderBottom: activeTab === 'create' ? '3px solid #1976d2' : 'none',
            background: 'none',
            fontWeight: activeTab === 'create' ? 'bold' : 'normal',
            color: activeTab === 'create' ? '#1976d2' : '#333',
            cursor: 'pointer',
            fontSize: 18
          }}
        >
          Tạo đơn thuốc
        </button>
        <button
          onClick={() => setActiveTab('history')}
          className={activeTab === 'history' ? 'active' : ''}
          style={{
            padding: '12px 32px',
            border: 'none',
            borderBottom: activeTab === 'history' ? '3px solid #1976d2' : 'none',
            background: 'none',
            fontWeight: activeTab === 'history' ? 'bold' : 'normal',
            color: activeTab === 'history' ? '#1976d2' : '#333',
            cursor: 'pointer',
            fontSize: 18
          }}
        >
          Lịch sử gửi đơn thuốc
        </button>
      </div>

      {/* Nội dung từng tab */}
      {activeTab === 'create' && (
        <form onSubmit={handleSubmit}>
          <div className="form-content">
            {/* Phần thông tin Học sinh */}
            <div className="student-info-section">
              <StudentInfoCard onChange={setSelectedStudentId} />
            </div>
            
            {/* Phần Chi tiết đơn thuốc */}
            <div className="prescription-details-section">
              {/* Thêm input cho mục đích sử dụng thuốc */}
            <div className="input-group">
              <label style={{fontSize: '20px'}}>Mục đích gửi thuốc:</label>
              <input
                className="purpose-input"
                width= "calc(100% - 20px)"
                type="text"
                value={purpose}
                onChange={(e) => setPurpose(e.target.value)}
                placeholder="Vd: Thuốc trị ho, thuốc hạ sốt..."
                required
              />
            </div>
            {/* Input thời gian sử dụng thuốc */}
              <div className="input-group">
                <label style={{fontSize: '20px'}}>Ngày dùng:</label>
                <input
                  type="date"
                  value={usageTime}
                  min={new Date().toISOString().split('T')[0]} // Chỉ cho chọn từ hôm nay trở đi
                  onChange={e => {
                    setUsageTime(e.target.value);
                    setDateError(''); // Xóa lỗi khi người dùng thay đổi ngày
                  }}
                  required
                  style={{fontSize: '16px', padding: '6px', borderRadius: '6px'}}
                />
              </div>
              {dateError && (
                <span style={{ color: 'red', fontSize: '0.9em' }}>{dateError}</span>
              )}
              <h2 style={{margin: '0px 0px 5px 0px'}}>Chi tiết đơn thuốc</h2>
              <div className="medicine-list">
                {medicines.map((medicine, index) => (
                  <div key={index} className="medicine-item" style={{position: 'relative'}}>
                    {/* Nút X xoá */}
                    <button
                      type="button"
                      onClick={() => handleRemoveMedicine(index)}
                      style={{
                        position: 'absolute',
                        top: '-10px',
                        right: '-10px',
                        background: 'transparent',
                        border: 'none',
                        fontSize: '18px',
                        color: '#d00',
                        cursor: 'pointer',
                        fontWeight: 'bold'
                      }}
                      aria-label="Xoá thuốc"
                    >
                      <FontAwesomeIcon icon={faXmark} />
                    </button>
                    <div className="input-group">
                      <label>Tên thuốc:</label>
                      <input
                        type="text"
                        name="name"
                        value={medicine.name}
                        onChange={(e) => handleMedicineChange(index, e)}
                        placeholder="Paracetamol, Aspirin..."
                        required
                      />
                    </div>
                    <div className="input-group">
                      <label>Liều lượng:</label>
                      <input
                        type="text" // Đổi từ number sang text
                        name="quantity"
                        value={medicine.quantity}
                        onChange={(e) => handleMedicineChange(index, e)}
                        placeholder="Uống 1 viên, Uống 1 muỗng..."
                        required
                      />
                    </div>
                    <div className="input-group" style={{ marginBottom: '0px' }}>
                      <label>Thời gian:</label>
                      <select
                        name="usage"
                        value={medicine.usage}
                        onChange={(e) => handleMedicineChange(index, e)}
                        required
                      >
                        <option value="">-- Chọn thời gian --</option>
                        <option value="Sau ăn sáng từ 9h-9h30">Sau ăn sáng từ 9h-9h30</option>
                        <option value="Trước ăn trưa: 10h30-11h">Trước ăn trưa: 10h30-11h</option>
                        <option value="Sau ăn trưa: từ 11h30-12h">Sau ăn trưa: từ 11h30-12h</option>
                      </select>
                    </div>
                  </div>
                ))}
                <button type="button" onClick={handleAddMedicine} className="add-medicine-btn">
                  + Thêm
                </button>
              </div>

              <div className="note-section">
                <label>Ghi chú:</label>
                <textarea
                  value={note}
                  onChange={(e) => setNote(e.target.value)}
                  placeholder="Bổ sung thêm thông tin .........."
                  rows="3"
                ></textarea>
              </div>
              <div className="submit-btn-wrapper">
                <button type="submit" className="submit-btn">
                  {editingId ? "Cập nhật" : "Xác nhận gửi"}
                </button>
              </div>
            </div>
          </div>
        </form>
      )}

      {activeTab === 'history' && (
        <MedicalRequestDetail/>
      )}
      <ToastContainer
  position="bottom-right" // Thêm dòng này
  autoClose={3000}
  hideProgressBar={false}
  newestOnTop={false}
  closeOnClick
  rtl={false}
  pauseOnFocusLoss
  draggable
  pauseOnHover
/>
    </div>
  );
};