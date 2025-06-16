import React, { useEffect, useState } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import "./MedicalIncident.css";
import axios from "axios";
import { Table, Tag, Select, Typography, Avatar } from "antd";
import StudentInfoCard from "../../common/StudentInfoCard";
const { Title, Text } = Typography;

const columns = [
  {
    title: "Loại sự kiện", // Tên cột hiển thị
    dataIndex: "typeEvent", // Key tương ứng trong object dữ liệu
    key: "typeEvent", // Key duy nhất cho cột này
    // Bạn có thể tùy chỉnh render cho cột này nếu muốn
    // render: (text) => <Tag color="blue">{text.toUpperCase()}</Tag>,
  },
  {
    title: "Ngày diễn ra",
    dataIndex: "date",
    key: "date",
    // Sắp xếp theo ngày
    sorter: (a, b) => new Date(a.date) - new Date(b.date),
    render: (text) => new Date(text).toLocaleDateString("vi-VN"), // Định dạng ngày tháng VN
  },
  {
    title: "ID Y tá",
    dataIndex: "nurseId",
    key: "nurseId",
    render: (text) => text.toString(), // Đảm bảo ID lớn được hiển thị đúng dạng chuỗi
  },
  {
    title: "Mô tả",
    dataIndex: "description",
    key: "description",
  },
];

const MedicalIncident = () => {
  const [students, setStudents] = useState([]);
  const [selectedStudentId, setSelectedStudentId] = useState();
  const [events, setEvents] = useState([]);

  // Lấy danh sách học sinh từ localStorage
  useEffect(() => {
    const stored = JSON.parse(localStorage.getItem("students") || "[]");
    setStudents(stored);
    if (stored.length > 0) setSelectedStudentId(stored[0].id);
  }, []);

  // Call API khi selectedStudentId thay đổi
  useEffect(() => {
    if (!selectedStudentId) return;
    const fetchEvents = async () => {
      try {
        const token = localStorage.getItem("token");
        const res = await axios.get(
          `http://localhost:8080/api/parent/medical-events/${selectedStudentId}`,
          { headers: { Authorization: `Bearer ${token}` } }
        );
        console.log(`http://localhost:8080/api/parent/medical-events/${selectedStudentId}`);
        setEvents(res.data);
      } catch (err) {
        setEvents([]);
      }
    };
    fetchEvents();
  }, [selectedStudentId]);

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
            dataSource={events}
            columns={columns}
            rowKey="eventId"
            pagination={{ pageSize: 5 }}
          />
        </div>
      </div>
    </div>
  );
};

export default MedicalIncident;