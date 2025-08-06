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
import { urlServer } from "../../../api/urlServer";
import { Form, Input, DatePicker, Select, Button, Upload, Row, Col, Divider, Card } from 'antd';
import { UploadOutlined, CloseCircleOutlined, PlusOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
const { Option } = Select;

export default function InstructionForm({ onShowHistory, value }) {
  const [form] = Form.useForm();
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
  const [selectedStudentId, setSelectedStudentId] = useState(students[0]?.studentId || '');
  const selectedStudent = students.find(s => String(s.studentId) === String(selectedStudentId));

  const [medicines, setMedicines] = useState([{ name: '', quantity: '', unit: '', usage: '', method: '' }]);
  const [note, setNote] = useState('');
  const [purpose, setPurpose] = useState(''); // Thêm state cho mục đích sử dụng thuốc
  const [usageTime, setUsageTime] = useState(''); // Thêm state cho thời gian sử dụng thuốc
  const [dateError, setDateError] = useState('');
  const [activeTab, setActiveTab] = useState('create'); // 'create' hoặc 'history'
  const [editingId, setEditingId] = useState(null);
  const [imageFile, setImageFile] = useState(null);
  const [imageUrl, setImageUrl] = useState(null); // Thêm state lưu url ảnh cũ
  const usageTimeOptions = [
  "Sau ăn sáng từ 9h-9h30",
  "Trước ăn trưa: 10h30-11h",
  "Sau ăn trưa: từ 11h30-12h"
];  
const handleAddMedicine = () => {
  setMedicines([...medicines, { name: '', quantity: '', unit: '', usage: '', method: '' }]);
};

const handleMedicineChange = (index, event) => {
  const { name, value } = event.target;
  const newMedicines = [...medicines];
  newMedicines[index][name] = value;
  setMedicines(newMedicines);
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

  const handleSubmit = async () => {
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

    const medicalRequestObject = {
      requestName: purpose,
      note,
      date: usageTime,
      studentId: selectedStudent.studentId,
      medicalRequestDetailRequests: medicines.map(med => ({
        medicineName: med.name,
        quantity: med.quantity,
        type: med.unit,
        method: med.method,
        timeSchedule: med.usage,
        note: med.usage ? '' : med.usage
      }))
    };

    const formData = new FormData();
    formData.append('request', JSON.stringify(medicalRequestObject));
    if (imageFile) {
      formData.append('image', imageFile);
    }

    try {
      const token = localStorage.getItem("token");
      let response;
      if (editingId) {
        response = await fetch(`${urlServer}/api/parent/medical-request/${editingId}`, {
          method: "PUT",
          headers: {
            Authorization: `Bearer ${token}`
          },
          body: formData
        });
      } else {
        response = await fetch(`${urlServer}/api/parent/medical-request`, {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`
          },
          body: formData
        });
      }

      if (!response.ok) throw new Error("Có lỗi khi gửi đơn thuốc!");

      setActiveTab('history');
      setPurpose('');
      setNote('');
      setUsageTime('');
      setMedicines([{ name: '', quantity: '', unit: '', usage: '', method: '' }]);
      setImageFile(null);
      setEditingId(null);
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
            quantity: item.quantity,
            unit: item.type,
            usage: item.timeSchedule,
            method: item.method || '',
          }))
        : [{ name: '', quantity: '', unit: '', usage: '', method: '' }]
    );
    setSelectedStudentId(req.studentDTO?.studentId || ''); // Tự động chọn lại học sinh
    console.log("Selected student ID:", req.studentDTO?.studentId);
    setActiveTab('create');
    setEditingId(req.requestId);
    setImageUrl(req.image || null);
    setImageFile(null);
  };
  window.addEventListener('edit-medicine-request', handleEdit);
  return () => window.removeEventListener('edit-medicine-request', handleEdit);
}, []);

useEffect(() => {
  if (value) setSelectedStudentId(value);
}, [value]);

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
          onClick={() => {
            // Reset form khi chuyển sang tab tạo đơn thuốc
            setPurpose('');
            setNote('');
            setUsageTime('');
            setMedicines([{ name: '', quantity: '', unit: '', usage: '', method: '' }]);
            setImageFile(null);
            setImageUrl(null);
            setEditingId(null);
            setActiveTab('create');
          }}
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
      <div className="form-content">
        {activeTab === 'create' && (
          <div className="student-info-section">
<StudentInfoCard
  key={selectedStudentId}
  value={selectedStudentId}
  onChange={setSelectedStudentId}
/>
          </div>
        )}

        {/* Nội dung từng tab */}
        {activeTab === 'create' && (
          <div className="prescription-details-section">
            <Form
      form={form}
      layout="vertical"
      onFinish={async (values) => {
        // Validate từng thuốc nếu muốn
        for (const [i, med] of (values.medicines || []).entries()) {
          if (!med.name || !med.quantity || !med.unit || !med.usage || !med.method) {
            message.error(`Vui lòng nhập đầy đủ thông tin cho thuốc thứ ${i + 1}`);
            return;
          }
        }
        // Nếu qua hết validate thì xác nhận gửi
        const result = await Swal.fire({
          title: 'Xác nhận gửi đơn thuốc?',
          text: 'Bạn có chắc chắn muốn gửi đơn thuốc này?',
          icon: 'question',
          showCancelButton: true,
          confirmButtonText: 'Gửi',
          cancelButtonText: 'Hủy',
        });
        if (!result.isConfirmed) return;

        // LẤY DỮ LIỆU THUỐC TỪ values.medicines
        const medicalRequestObject = {
          requestName: values.purpose,
          note: values.note, // LẤY GHI CHÚ TỪ FORM
          date: values.usageTime,
          studentId: selectedStudent.studentId,
          medicalRequestDetailRequests: (values.medicines || []).map(med => ({
            medicineName: med.name,
            quantity: med.quantity,
            type: med.unit,
            method: med.method,
            timeSchedule: med.usage,
            note: "" // ghi chú từng thuốc nếu muốn, còn không thì để rỗng
          }))
        };

        console.log("note:", values.note);
        const formData = new FormData();
        formData.append('request', JSON.stringify(medicalRequestObject));
        if (imageFile) {
          formData.append('image', imageFile);
        }

        try {
          const token = localStorage.getItem("token");
          let response;
          if (editingId) {
            response = await fetch(`${urlServer}/api/parent/medical-request/${editingId}`, {
              method: "PUT",
              headers: {
                Authorization: `Bearer ${token}`
              },
              body: formData
            });
          } else {
            response = await fetch(`${urlServer}/api/parent/medical-request`, {
              method: "POST",
              headers: {
                Authorization: `Bearer ${token}`
              },
              body: formData
            });
          }

          if (!response.ok) throw new Error("Có lỗi khi gửi đơn thuốc!");

          setActiveTab('history');
          setPurpose('');
          setNote('');
          setUsageTime('');
          setMedicines([{ name: '', quantity: '', unit: '', usage: '', method: '' }]);
          setImageFile(null);
          setEditingId(null);
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
      }}
    >
      <Form.Item
        label="Mục đích gửi thuốc"
        name="purpose"
        rules={[{ required: true, message: "Vui lòng nhập mục đích gửi thuốc!" }]}
        initialValue={purpose}
      >
        <Input
          value={purpose}
          onChange={e => setPurpose(e.target.value)}
          placeholder="Vd: Thuốc trị ho, thuốc hạ sốt..."
        />
      </Form.Item>
      <Row gutter={16}>
        <Col xs={24} md={12}>
          <Form.Item
            label="Ngày dùng"
            name="usageTime"
            rules={[{ required: true, message: "Vui lòng chọn ngày dùng!" }]}
            initialValue={usageTime ? dayjs(usageTime) : null}
          >
            <DatePicker
              value={usageTime ? dayjs(usageTime) : null}
              onChange={date => {
                setUsageTime(date ? date.format('YYYY-MM-DD') : '');
                setDateError('');
              }}
              style={{ width: '100%' }}
              disabledDate={current => current && current < dayjs().startOf('day')}
            />
          </Form.Item>
        </Col>
        <Col xs={24} md={12}>
          <Form.Item label="Ảnh đơn thuốc (nếu có)">
            <div style={{
      display: "flex",
      alignItems: "center",
      gap: 16,
      background: "#f5faff",
      border: "1px dashed #91d5ff",
      borderRadius: 8,
      padding: "16px 12px"
    }}>
      <Upload
        beforeUpload={file => {
          setImageFile(file);
          return false;
        }}
        showUploadList={false}
        accept="image/*"
      >
        <Button
          type="primary"
          icon={<UploadOutlined style={{ fontSize: 20 }} />}
          size="large"
          style={{
            fontWeight: 600,
            borderRadius: 8,
            boxShadow: "0 2px 8px #91d5ff55"
          }}
        >
          Chọn ảnh
        </Button>
      </Upload>
      {(imageFile || imageUrl) && (
        <div style={{ position: "relative", display: "inline-block" }}>
          <img
            src={imageFile ? URL.createObjectURL(imageFile) : imageUrl}
            alt="Preview"
            style={{
              maxWidth: 80,
              maxHeight: 80,
              borderRadius: 8,
              border: '2px solid #91d5ff',
              background: "#fff"
            }}
          />
          <Button
            type="text"
            icon={<CloseCircleOutlined style={{ color: "#d00", fontSize: 30 }} />}
            onClick={() => { setImageFile(null); setImageUrl(null); }}
            style={{
          color: "#d00",
          fontSize: 14, // tăng kích thước
          background: "#fff",
          borderRadius: "50%",
          padding: 20,
          boxShadow: "0 2px 8px #d002"
        }}
          />
        </div>
      )}
    </div>
          </Form.Item>
        </Col>
      </Row>
      <Divider orientation="left">Chi tiết đơn thuốc</Divider>
      <Form.List name="medicines" initialValue={medicines}>
  {(fields, { add, remove }) => (
    <>
      {fields.map(({ key, name, ...restField }, index) => (
        <Card
          key={key}
          style={{ marginBottom: 16, background: "#e0f2ff", borderRadius: 8, position: 'relative' }}
          bodyStyle={{ padding: 16 }}
        >
          <Button
            type="text"
            icon={<CloseCircleOutlined style={{ color: "#d00" }} />}
            onClick={() => remove(name)}
            style={{ position: 'absolute', top: 8, right: 8 }}
          />
          <Row gutter={16}>
            <Col xs={24} md={12} lg={10}>
              <Form.Item
                {...restField}
                name={[name, 'name']}
                label="Tên thuốc"
                rules={[{ required: true, message: "Vui lòng nhập tên thuốc!" }]}
              >
                <Input placeholder="Paracetamol, Aspirin..." />
              </Form.Item>
            </Col>
            <Col xs={12} md={6} lg={5}>
              <Form.Item
                {...restField}
                name={[name, 'quantity']}
                label="Số lượng"
                rules={[
                  { required: true, message: "Vui lòng nhập số lượng!" },
                  { pattern: /^[1-9][0-9]*$/, message: "Số lượng phải là số nguyên dương!" }
                ]}
              >
                <Input type="number" min={1} />
              </Form.Item>
            </Col>
            <Col xs={12} md={6} lg={9}>
              <Form.Item
                {...restField}
                name={[name, 'unit']}
                label="Đơn vị"
                rules={[{ required: true, message: "Vui lòng chọn đơn vị!" }]}
              >
                <Select placeholder="Chọn đơn vị">
                  <Option value="">-- Chọn đơn vị --</Option>
                  <Option value="Viên">Viên</Option>
                  <Option value="Gói">Gói</Option>
                  <Option value="Vỉ">Vỉ</Option>
                  <Option value="Chai">Chai</Option>
                  <Option value="Lọ">Lọ</Option>
                  <Option value="Tuýp">Tuýp</Option>
                  <Option value="Miếng dán">Miếng dán</Option>
                  <Option value="Liều">Liều</Option>
                  <Option value="Ống">Ống</Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>
          <Form.Item
            {...restField}
            name={[name, 'method']}
            label="Cách dùng"
            rules={[{ required: true, message: "Vui lòng nhập cách dùng!" }]}
          >
            <Input placeholder="Nhập cách dùng (vd: uống, bôi, tiêm...)" />
          </Form.Item>
          <Form.Item
            {...restField}
            name={[name, 'usage']}
            label="Thời gian"
            rules={[{ required: true, message: "Vui lòng chọn thời gian!" }]}
          >
            <Select placeholder="Chọn thời gian">
              <Option value="">-- Chọn thời gian --</Option>
              <Option value="Sau ăn sáng từ 9h-9h30">Sau ăn sáng từ 9h-9h30</Option>
              <Option value="Trước ăn trưa: 10h30-11h">Trước ăn trưa: 10h30-11h</Option>
              <Option value="Sau ăn trưa: từ 11h30-12h">Sau ăn trưa: từ 11h30-12h</Option>
            </Select>
          </Form.Item>
        </Card>
      ))}
      <Button type="dashed" icon={<PlusOutlined />} onClick={() => add()} style={{ marginBottom: 16 }}>
        Thêm thuốc
      </Button>
    </>
  )}
</Form.List>
      <Form.Item label="Ghi chú"
        name="note" // THÊM DÒNG NÀY
      >
        <Input.TextArea
          value={note}
          onChange={e => setNote(e.target.value)}
          placeholder="Bổ sung thêm thông tin .........."
          rows={3}
        />
      </Form.Item>
      <Form.Item>
        <div style={{ display: "flex", justifyContent: "center" }}>
          <Button
            type="primary"
            htmlType="submit"
            style={{ width: 180, fontWeight: 600 }}
          >
            {editingId ? "Cập nhật" : "Xác nhận gửi"}
          </Button>
        </div>
      </Form.Item>
    </Form>
          </div>
        )}
      </div>

      {activeTab === 'history' && (
        <MedicalRequestDetail />
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