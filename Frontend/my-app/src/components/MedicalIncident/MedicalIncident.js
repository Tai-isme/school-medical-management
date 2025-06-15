import React, { useEffect, useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import './MedicalIncident.css';
import axios from 'axios';
import { Table, Tag, Select, Typography, Avatar } from 'antd';
import StudentInfoCard from '../../common/StudentInfoCard';
const { Title, Text } = Typography;



const columns = [
  {
    title: 'Loại sự kiện', // Tên cột hiển thị
    dataIndex: 'typeEvent',   // Key tương ứng trong object dữ liệu
    key: 'typeEvent',         // Key duy nhất cho cột này
    // Bạn có thể tùy chỉnh render cho cột này nếu muốn
    // render: (text) => <Tag color="blue">{text.toUpperCase()}</Tag>,
  },
  {
    title: 'Ngày diễn ra',
    dataIndex: 'date',
    key: 'date',
    // Sắp xếp theo ngày
    sorter: (a, b) => new Date(a.date) - new Date(b.date),
    render: (text) => new Date(text).toLocaleDateString('vi-VN'), // Định dạng ngày tháng VN
  },
  {
    title: 'ID Y tá',
    dataIndex: 'nurseId',
    key: 'nurseId',
    render: (text) => text.toString(), // Đảm bảo ID lớn được hiển thị đúng dạng chuỗi

    
  },
  {
    title: 'Mô tả',
    dataIndex: 'description',
    key: 'description',
  },
];

// Dữ liệu mẫu (trong ứng dụng thực tế, dữ liệu này sẽ đến từ API hoặc state)
const sampleData = [
  {
    key: '1', // Ant Design Table cần một 'key' duy nhất cho mỗi dòng
    typeEvent: "Khám định kỳ",
    date: "2025-06-15",
    description: "Kiểm tra sức khỏe tổng quát cho bệnh nhân A.",
    nurseId: 9007199254740991, // Số này bằng Number.MAX_SAFE_INTEGER
  },
  {
    key: '2',
    typeEvent: "Tiêm chủng",
    date: "2025-07-01",
    description: "Tiêm vắc-xin phòng cúm mùa cho bệnh nhân B.",
    nurseId: 9007199254740992, // Số này lớn hơn Number.MAX_SAFE_INTEGER, nên là chuỗi
  },
  {
    key: '3',
    typeEvent: "Tư vấn dinh dưỡng",
    date: "2025-05-20",
    description: "Hướng dẫn chế độ ăn uống lành mạnh.",
    nurseId: 1234567890123456,
  },
];

// Chuyển đổi nurseId thành chuỗi nếu nó là số lớn để tránh mất độ chính xác
const processedData = sampleData.map(item => ({
  ...item,
  nurseId: item.nurseId.toString(), // Quan trọng cho các ID số lớn
}));

const MedicalIncident = () => {
  // Lấy danh sách học sinh từ localStorage
  const [students, setStudents] = useState([]);
  const [selectedStudentId, setSelectedStudentId] = useState();

  useEffect(() => {
    const stored = JSON.parse(localStorage.getItem("students") || "[]");
    setStudents(stored);
    if (stored.length > 0) setSelectedStudentId(stored[0].id);
  }, []);

  const selectedStudent = students.find(s => s.id === selectedStudentId);

  return (
    <div className="medical-incident-container">
      <h1 className="main-title">Sự cố y tế</h1>
      <div className="incident-content">
        {/* Left Section: Student Overview */}
        <div className="left-panel">
          <StudentInfoCard
            onChange={setSelectedStudentId} // Cập nhật ID học sinh khi thay đổi
            style={{ minWidth: 300 }} // Đặt chiều rộng tối thiểu cho StudentInfoCard       
            />
        </div>

        {/* Right Section: Incident Details Table */}
        <div className="right-panel-incident">
          <h2>Các sự kiện</h2>


          <Table 
        dataSource={sampleData} // Sử dụng dữ liệu đã xử lý
        columns={columns} 
        bordered // Thêm viền cho bảng
        pagination={{ pageSize: 5 }} // Cấu hình phân trang, 5 mục mỗi trang
        scroll={{ x: 800 }} // Cho phép cuộn ngang nếu nội dung quá rộng
      />
        </div>
      </div>
    </div>
  );
};

export default MedicalIncident;