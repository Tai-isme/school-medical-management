import React, { useEffect, useState } from "react";
import axios from "axios";
import NotificationsList from "./NotificationsList";
import "./NotificateCard.css";
import StudentInfoCard from "../../../common/StudentInfoCard";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHouse } from '@fortawesome/free-solid-svg-icons';
import { urlServer } from "../../../api/urlServer";
const Notifications = () => {
  const students = JSON.parse(localStorage.getItem("students") || "[]");
  const [selectedStudentId, setSelectedStudentId] = useState(
    students.length > 0 ? students[0].id : null
  );
  const selectedStudent = students.find((s) => s.id === selectedStudentId);

  const [selectedNotification, setSelectedNotification] = useState(null);
  const [notifications, setNotifications] = useState([]);

  const fetchNotifications = async () => {
    if (!selectedStudentId) {
      setNotifications([]);
      return;
    }
    try {
      const res = await axios.get(
        `${urlServer}/api/parent/all-forms/${selectedStudentId}`,
        {
    headers: {
      Authorization: `Bearer ${localStorage.getItem("token")}`,
    },
  }
      )
      const { healthCheckForms = [], vaccineForms = [] } = res.data;

      // Chuẩn hóa healthCheckForms
      const healthCheckNotifications = healthCheckForms.map((item) => ({
        ...item,
        type: "healthcheck",
        formDate: item.healthCheckProgramDTO?.dateSendForm || item.expDate, // dùng dateSendForm nếu có
        note: item.notes, // chú ý: healthCheck dùng notes, vaccine dùng note
      }));

      // Chuẩn hóa vaccineForms
      const vaccineNotifications = vaccineForms.map((item) => ({
        ...item,
        type: "vaccine",
        formDate: item.vaccineProgramDTO?.dateSendForm || item.expDate,
        note: item.note,
      }));

      setNotifications([...healthCheckNotifications, ...vaccineNotifications]);
    } catch (error) {
      setNotifications([]);
    }
  };

  useEffect(() => {
    setNotifications([]); // Reset notifications khi đổi học sinh
    fetchNotifications();
    // eslint-disable-next-line
  }, [selectedStudentId]);
  
  return (
    <div className="notifications-container" style={{ position: "relative" }}>
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
      <h1 className="main-title" style={{ margin: '0px 0px 20px 0px', fontSize: '24px'  }}>Các thông báo</h1>
      <div className="notifications-content">
        {/* Left Section: Student Overview */}
        <div className="left-panel">
          <StudentInfoCard onChange={setSelectedStudentId} />
        </div>

        {/* Right Section: Notifications List */}
        <div className="right-panel-notifications">
          <NotificationsList
            notifications={notifications}
            fetchNotifications={fetchNotifications}
          />
        </div>
      </div>

      {/* Popup chi tiết (nếu muốn) */}
      {selectedNotification && (
        <div className="notification-popup">
          <div className="popup-content">
            <h3>{selectedNotification.type}</h3>
            <p>Ngày: {selectedNotification.date}</p>
            <p>Trạng thái: {selectedNotification.status}</p>
            <button onClick={() => setSelectedNotification(null)}>Đóng</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Notifications;
