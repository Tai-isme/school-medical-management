import React, { useState } from 'react';
import './InstructionForm.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faXmark, faCaretDown } from '@fortawesome/free-solid-svg-icons';
import { createMedicalRequest } from '../../api/medicalRequestApi';

const InstructionForm = () => {
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
    const payload = {
      requestName: purpose,
      note: note,
      date: usageTime, // usageTime là ngày dùng, dạng yyyy-mm-dd
      studentId: selectedStudent.id,
      medicalRequestDetailRequests: medicines.map(med => ({
        medicineName: med.name,
        dosage: med.quantity, // đổi quantity thành dosage
        instructions: med.usage,
        time: usageTime // hoặc để rỗng nếu không cần
      }))
    };
    try {
      await createMedicalRequest(payload);
      alert('Đơn hướng dẫn đã được gửi!');
    } catch (error) {
      alert('Gửi thất bại!');
    }
  };

  return (
    <div className="instruction-form-container">
      <h1>Tạo đơn hướng dẫn nhân viên y tế sử dụng cho học sinh</h1>
      <form onSubmit={handleSubmit}>
        <div className="form-content">
          {/* Phần thông tin Học sinh */}
          <div className="student-info-section">
            <h2>Chọn học sinh</h2>
            <div className="student-profile">
              <div className="select-wrapper" style={{ position: 'relative', width: '100%' }}>
                <select
                  value={selectedStudentId}
                  onChange={e => setSelectedStudentId(e.target.value)}
                  style={{
                    marginBottom: 8,
                    padding: 6,
                    borderRadius: 6,
                    width: '50%',
                    textAlign: 'center'
                  }}
                >
                  {students.map(student => (
                    <option key={student.id} value={student.id}>
                      {student.fullName}
                    </option>
                  ))}
                </select>
              </div>
              <div className="avatar-placeholder">
                <img src={selectedStudent.avatar} alt="Avatar" className="avatar-img" />
              </div>
              <p className="student-id">
                <i className="fas fa-id-card"></i> {selectedStudent.id}
              </p>
              <p className="student-class">Lớp: {selectedStudent.classID}</p>
            </div>
          </div>

          

          {/* Phần Chi tiết đơn thuốc */}
          <div className="prescription-details-section">
            {/* Thêm input cho mục đích sử dụng thuốc */}
          <div className="input-group">
            <label style={{fontSize: '20px'}}>Đơn thuốc:</label>
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
                onChange={e => setUsageTime(e.target.value)}
                required
                style={{fontSize: '16px', padding: '6px', borderRadius: '6px'}}
              />
            </div>
            <h2 style={{marginTop: '0px'}}>Chi tiết đơn thuốc</h2>
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
          </div>
        </div>
        <button type="submit" className="submit-btn">
          Xác nhận gửi
        </button>
      </form>
    </div>
  );
};

export default InstructionForm;