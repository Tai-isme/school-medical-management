import React, { useEffect, useState } from "react";
import axios from "axios";
import NotificationsList from "./NotificationsList";
import "./NotificateCard.css";
import StudentInfoCard from "../../../common/StudentInfoCard";

const Notifications = () => {
  const students = JSON.parse(localStorage.getItem("students") || "[]");
  const [selectedStudentId, setSelectedStudentId] = useState(students[0].id);
  const selectedStudent = students.find((s) => s.id === selectedStudentId);

  const [selectedNotification, setSelectedNotification] = useState(null);
  const [notifications, setNotifications] = useState([]);

  const fetchNotifications = async () => {
    if (!selectedStudentId) return;
    const fetchEvents = async () => {
      try {
        const res = await axios.get(
          `http://localhost:8080/api/parent/all-forms/${selectedStudentId}`,
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
        );
        const { healthCheckForms = [], vaccineForms = [] } = res.data;

        const healthNoti = healthCheckForms.map((item) => ({
          ...item,
          type: "healthcheck",
        }));

        const vaccineNoti = vaccineForms.map((item) => ({
          ...item,
          type: "vaccine",
        }));

        setNotifications([...healthNoti, ...vaccineNoti]);
        console.log("Fetched notifications:", notifications);
      } catch (error) {
        console.error("Error fetching notifications:", error);
        setNotifications([]);
      }
    };
    fetchEvents();
  };

  useEffect(() => {
    if (!selectedStudentId) return;
    const fetchEvents = async () => {
      try {
        const res = await axios.get(
          `http://localhost:8080/api/parent/all-forms/${selectedStudentId}`,
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
        );
        const { healthCheckForms = [], vaccineForms = [] } = res.data;

        const healthNoti = healthCheckForms.map((item) => ({
          ...item,
          type: "healthcheck",
        }));

        const vaccineNoti = vaccineForms.map((item) => ({
          ...item,
          type: "vaccine",
        }));

        setNotifications([...healthNoti, ...vaccineNoti]);
        console.log("Fetched notifications:", notifications);
      } catch (error) {
        console.error("Error fetching notifications:", error);
        setNotifications([]);
      }
    };
    fetchEvents();
  }, [selectedStudentId]);

  

  return (
    <div className="notifications-container">
      <h1 className="main-title" style={{marginTop: '0px'}}>Các thông báo</h1>
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
