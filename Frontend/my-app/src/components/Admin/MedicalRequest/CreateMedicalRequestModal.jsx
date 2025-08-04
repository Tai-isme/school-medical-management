import React, { useState, useEffect } from "react";
import axios from "axios";
import { Modal, Input, Button, Select, DatePicker, Upload, message, Form, Space, Divider } from "antd";
import { PlusOutlined, UploadOutlined } from "@ant-design/icons";
import { urlServer } from "../../../api/urlServer";
const { TextArea } = Input;

const timeOptions = [
  { value: "Sau ăn sáng từ 9h-9h30", label: "Sau ăn sáng từ 9h-9h30" },
  { value: "Trước ăn trưa: 10h30-11h", label: "Trước ăn trưa: 10h30-11h" },
  { value: "Sau ăn trưa: từ 11h30-12h", label: "Sau ăn trưa: từ 11h30-12h" },
];

const unitOptions = [
  { value: "", label: "-- Chọn đơn vị --" },
  { value: "Viên", label: "Viên" },
  { value: "Gói", label: "Gói" },
  { value: "Vỉ", label: "Vỉ" },
  { value: "Chai", label: "Chai" },
  { value: "Lọ", label: "Lọ" },
  { value: "Tuýp", label: "Tuýp" },
  { value: "Miếng dán", label: "Miếng dán" },
  { value: "Liều", label: "Liều" },
  { value: "Ống", label: "Ống" },
];

const CreateMedicalRequestModal = ({
  open,
  onCancel,
  onSubmit,
  students = [],
  loading = false,
  activeKey, // Đúng tên prop
  onChangeTab,
}) => {
  const [form] = Form.useForm();
  const [details, setDetails] = useState([
    { medicineName: "", quantity: "", unit: "", usage: "", time: "" },
  ]);
  const [image, setImage] = useState(null);

  // Thêm state cho lớp và học sinh
  const [classList, setClassList] = useState([]);
  const [selectedClass, setSelectedClass] = useState(null);
  const [studentList, setStudentList] = useState([]);
  const [allClassData, setAllClassData] = useState([]);
  const token = localStorage.getItem("token");

  // Gọi API lấy danh sách lớp và học sinh khi mở modal
  useEffect(() => {
    if (open && allClassData.length === 0 && token) {
      const fetchData = async () => {
        try {
          const res = await axios.get(`${urlServer}/api/nurse/students`, {
            headers: { Authorization: `Bearer ${token}` },
          });
          setAllClassData(res.data);
          setClassList(
            res.data.map(cls => ({
              id: cls.classId,
              className: cls.className,
            }))
          );
        } catch {
          setClassList([]);
          setAllClassData([]);
        }
      };
      fetchData();
    }
    // Reset khi đóng modal
    if (!open) {
      setSelectedClass(null);
      setStudentList([]);
      setAllClassData([]);
      setClassList([]);
      form.resetFields();
    }
  }, [open, token]);

  // Khi chọn lớp, lấy học sinh từ lớp đó
  const handleClassChange = (val) => {
    setSelectedClass(val);
    if (val) {
      const foundClass = allClassData.find(cls => cls.classId === val);
      const students = foundClass?.students || [];
      setStudentList(
        students.map(s => ({
          ...s,
          id: s.studentId,
          avatar: s.avatarUrl ? s.avatarUrl : "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a1/Hoa_gi%E1%BA%A5y_c%E1%BB%95_th%E1%BB%A5.jpg/1200px-Hoa_gi%E1%BA%A5y_c%E1%BB%95_th%E1%BB%A5.jpg",
          gender: s.gender === "MALE" ? "Nam" : "Nữ",
          className: foundClass.className,
        }))
      );
      form.setFieldsValue({ studentId: undefined });
    } else {
      setStudentList([]);
      form.setFieldsValue({ studentId: undefined });
    }
  };

  const handleAddDetail = () => {
    setDetails([...details, { medicineName: "", quantity: "", unit: "", usage: "", time: "" }]);
  };

  const handleRemoveDetail = (idx) => {
    setDetails(details.filter((_, i) => i !== idx));
  };

  const handleDetailChange = (idx, field, value) => {
    const newDetails = [...details];
    newDetails[idx][field] = value;
    setDetails(newDetails);
  };

  const handleImageChange = (info) => {
    if (info.fileList && info.fileList.length > 0) {
      setImage(info.fileList[0].originFileObj);
    } else {
      setImage(null);
    }
  };

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      if (details.some(d => !d.medicineName || !d.quantity || !d.unit || !d.usage || !d.time)) {
        message.error("Vui lòng nhập đầy đủ thông tin từng thuốc!");
        return;
      }

      // Lấy thông tin học sinh đã chọn
      const selectedStudent = studentList.find(s => s.id === values.studentId);

      // Chuẩn bị object gửi lên API
      const medicalRequestObject = {
        requestName: values.purpose,
        note: values.note,
        date: values.date.format("YYYY-MM-DD"),
        studentId: selectedStudent.studentId || selectedStudent.id,
        medicalRequestDetailRequests: details.map(med => ({
          medicineName: med.medicineName,
          quantity: med.quantity,
          type: med.unit,
          method: med.usage, // method là cách dùng
          timeSchedule: med.time,
          note: med.usage ? '' : med.usage
        }))
      };

      const formData = new FormData();
      formData.append('request', JSON.stringify(medicalRequestObject));
      if (image) {
        formData.append('image', image);
      }

      const token = localStorage.getItem("token");
      const response = await fetch(`${urlServer}/api/nurse/medical-request`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`
          // KHÔNG đặt Content-Type ở đây!
        },
        body: formData
      });

      if (response.ok) {
        message.success("Tạo đơn thuốc thành công!");
        form.resetFields();
        setDetails([{ medicineName: "", quantity: "", unit: "", usage: "", time: "" }]);
        setImage(null);
        onSubmit && onSubmit();
        onChangeTab && onChangeTab("CONFIRMED");
      } else {
        message.error("Tạo đơn thuốc thất bại!");
      }
    } catch (err) {
      message.error("Vui lòng nhập đầy đủ thông tin!");
    }
  };

  // Thêm dòng này để theo dõi studentId
  const selectedStudentId = Form.useWatch("studentId", form);

  // Thêm hàm này trước return
  const disabledDate = (current) => {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const maxDate = new Date(today);
    maxDate.setDate(today.getDate() + 3); // 3 ngày tới

    return (
      current &&
      (current < today || current > maxDate)
    );
  };

  return (
    <Modal
      open={open}
      onCancel={onCancel}
      onOk={handleOk}
      title="Tạo đơn thuốc cho học sinh"
      width={900}
      footer={[
        <Button key="cancel" onClick={onCancel}>Hủy</Button>,
        <Button key="submit" type="primary" loading={loading} onClick={handleOk}>
          Xác nhận gửi
        </Button>,
      ]}
      destroyOnClose
    >
      <div style={{ display: "flex", gap: 32 }}>
        {/* Left: Thông tin học sinh */}
        <div style={{
          background: "#eaf6ff",
          borderRadius: 16,
          padding: 24,
          width: 320,
          display: "flex",
          flexDirection: "column",
          alignItems: "center"
        }}>
          <div style={{ fontSize: 22, color: "#1890ff", fontWeight: 600, marginBottom: 16 }}>Học sinh</div>
          <Form form={form} layout="vertical" style={{ width: "100%" }}>
            <Form.Item
              label="Chọn lớp"
              name="classId"
              rules={[{ required: true, message: "Chọn lớp" }]}
              style={{ marginBottom: 12 }}
            >
              <Select
                placeholder="Chọn lớp"
                options={classList.map(cls => ({
                  value: cls.id,
                  label: cls.className,
                }))}
                onChange={handleClassChange}
                allowClear
              />
            </Form.Item>
            <Form.Item
              name="studentId"
              label="Chọn học sinh"
              rules={[{ required: true, message: "Chọn học sinh" }]}
              style={{ marginBottom: 16 }}
            >
              <Select
                placeholder="Chọn học sinh"
                options={studentList.map(s => ({
                  value: s.id,
                  label: s.fullName,
                }))}
                showSearch
                filterOption={(input, option) =>
                  option.label.toLowerCase().includes(input.toLowerCase())
                }
                disabled={!selectedClass}
              />
            </Form.Item>
          </Form>
          {/* Ảnh đại diện và info demo */}
          {(() => {
            const selectedStudent = studentList.find(s => s.id === selectedStudentId);
            return (
              <>
                <img
                  src={selectedStudent?.avatar || "https://via.placeholder.com/160"}
                  alt="avatar"
                  style={{ width: 160, height: 160, borderRadius: "50%", objectFit: "cover", margin: "16px 0" }}
                />
                <div style={{ fontWeight: 600, fontSize: 18, marginBottom: 8 }}>
                  {selectedStudent?.fullName || ""}
                </div>
                <div style={{ color: "#555", marginBottom: 8 }}>
                  <span role="img" aria-label="id">🎓</span> Mã học sinh: {selectedStudent?.id || "--"}
                </div>
                <div style={{ color: "#555", marginBottom: 8 }}>
                  <span role="img" aria-label="dob">📅</span> Ngày sinh: {selectedStudent?.dob || "--"}
                </div>
                <div style={{ color: "#555", marginBottom: 8 }}>
                  <span role="img" aria-label="class">🎒</span> Lớp: {selectedStudent?.className || "--"}
                </div>
                <div style={{ color: "#555" }}>
                  <span role="img" aria-label="gender">🧑‍🎓</span> Giới tính: {selectedStudent?.gender || "--"}
                </div>
              </>
            );
          })()}
        </div>
        {/* Right: Form nhập đơn */}
        <div style={{ flex: 1 }}>
          <Form form={form} layout="vertical">
            <Form.Item
              label="Mục đích gửi thuốc:"
              name="purpose"
              rules={[{ required: true, message: "Nhập mục đích gửi thuốc" }]}
            >
              <Input placeholder="Vd: Thuốc trị ho, thuốc hạ sốt..." />
            </Form.Item>
            <Form.Item
              label="Ngày dùng:"
              name="date"
              rules={[{ required: true, message: "Chọn ngày dùng" }]}
            >
              <DatePicker 
                style={{ width: "100%" }} 
                format="DD/MM/YYYY" 
                disabledDate={disabledDate} 
              />
            </Form.Item>
            <Form.Item label="Ảnh đơn thuốc (nếu có):" name="image">
              <Upload
                beforeUpload={() => false}
                maxCount={1}
                onChange={handleImageChange}
                accept="image/*"
              >
                <Button icon={<UploadOutlined />}>Chọn file</Button>
              </Upload>
            </Form.Item>
            <Divider orientation="left" style={{ color: "#1890ff" }}>Chi tiết đơn thuốc</Divider>
            {details.map((detail, idx) => (
              <div
                key={idx}
                style={{
                  background: "#eaf6ff",
                  borderRadius: 8,
                  padding: 16,
                  marginBottom: 12,
                  position: "relative",
                  overflow: "visible" // Thêm dòng này
                }}
              >
                {details.length > 1 && (
                  <Button
                    type="text"
                    danger
                    style={{ position: "absolute", top: 8, right: 8, zIndex: 2 }} // Thêm zIndex
                    onClick={() => handleRemoveDetail(idx)}
                  >
                    X
                  </Button>
                )}
                <Form.Item label="Tên thuốc:" required>
                  <Input
                    placeholder="Paracetamol, Aspirin..."
                    value={detail.medicineName}
                    onChange={e => handleDetailChange(idx, "medicineName", e.target.value)}
                  />
                </Form.Item>
                <Space style={{ display: "flex", gap: 16 }}>
                  <Form.Item label="Số lượng:" required style={{ flex: 1 }}>
                    <Input
                      style={{ width: "225px" }}
                      value={detail.quantity}
                      onChange={e => handleDetailChange(idx, "quantity", e.target.value)}
                    />
                  </Form.Item>
                  <Form.Item label="Đơn vị:" required style={{ flex: 2 }}>
                    <Select
                      style={{ width: "225px" }}
                      options={unitOptions}
                      value={detail.unit}
                      onChange={val => handleDetailChange(idx, "unit", val)}
                      placeholder="-- Chọn đơn vị --"
                    />
                  </Form.Item>
                </Space>
                <Form.Item label="Cách dùng:" required>
                  <Input
                    placeholder="Nhập cách dùng (vd: uống, bôi, tiêm...)"
                    value={detail.usage}
                    onChange={e => handleDetailChange(idx, "usage", e.target.value)}
                  />
                </Form.Item>
                <Form.Item label="Thời gian:" required>
                  <Select
                    style={{ width: "100%" }}
                    options={timeOptions}
                    value={detail.time}
                    onChange={val => handleDetailChange(idx, "time", val)}
                    placeholder="-- Chọn thời gian --"
                  />
                </Form.Item>
              </div>
            ))}
            <Button type="link" icon={<PlusOutlined />} onClick={handleAddDetail}>
              Thêm
            </Button>
            <Form.Item label="Ghi chú:" name="note">
              <TextArea rows={3} placeholder="Bổ sung thêm thông tin ..." />
            </Form.Item>
          </Form>
        </div>
      </div>
    </Modal>
  );
};

export default CreateMedicalRequestModal;