import React, { useEffect, useState } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faHouse } from "@fortawesome/free-solid-svg-icons";
import "./MedicalEventCard.css";
import axios from "axios";
import { Table, Tag, Select, Typography, Avatar } from "antd";
import StudentInfoCard from "../../../common/StudentInfoCard";
const { Title, Text } = Typography;

const columns = [
  {
    title: "Loại sự kiện",
    dataIndex: "typeEvent",
    key: "typeEvent",
    minWidth: 200,
    render: (text) => (
      <span style={{
        fontSize: 15,
        color: '#333',
        fontFamily: 'inherit',
        padding: 4,
        whiteSpace: 'pre-wrap',
        overflowWrap: 'break-word',
        fontWeight: 400,
      }}>{text}</span>
    ),
  },
  {
    title: "Ngày diễn ra",
    dataIndex: "date",
    key: "date",
    minWidth: 150,
    sorter: (a, b) => new Date(a.date) - new Date(b.date),
    render: (text) => (
      <span style={{
        fontSize: 15,
        color: '#333',
        fontFamily: 'inherit',
        padding: 4,
        whiteSpace: 'pre-wrap',
        overflowWrap: 'break-word',
        fontWeight: 400,
      }}>{new Date(text).toLocaleDateString("vi-VN")}</span>
    ),
  },
  {
    title: "Y tá phụ trách",
    key: "nurseDTO",
    minWidth: 150,
    render: (_, record) => (
      <span style={{
        fontSize: 15,
        color: '#333',
        fontFamily: 'inherit',
        padding: 4,
        whiteSpace: 'pre-wrap',
        overflowWrap: 'break-word',
        fontWeight: 400,
      }}>{record.nurseDTO?.fullName || "Không rõ"}</span>
    ),
  },
  {
    title: "Mô tả",
    dataIndex: "description",
    key: "description",
    render: (text) => {
      const lines = text ? text.split('\n').length : 1;
      const approxLines = Math.max(lines, Math.ceil((text?.length || 0) / 80));
      return (
        <textarea
          value={text}
          readOnly
          rows={approxLines}
          style={{
            width: '100%',
            border: 'none',
            background: 'transparent',
            fontSize: 15,
            color: '#333',
            padding: 4,
            whiteSpace: 'pre-wrap',
            overflowWrap: 'break-word',
            resize: 'none',
          }}
        />
      );
    },
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
    <div className="medical-incident-container" style={{ position: "relative" }}>
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
      <h1 className="main-title" style={{ margin: '0px 0px 20px 0px', fontSize: '24px'  }}>Sự cố y tế</h1>
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