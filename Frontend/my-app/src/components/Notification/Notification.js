import React, { useEffect, useState } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import "./Notifications.css";
import StudentInfoCard from "../../common/StudentInfoCard";
import NotificationsList from "../NotificationsList";
import axios from "axios";

const students = [
  {
    id: "SE181818",
    name: "Nguyễn Văn A",
    class: "5A",
    avatar: "./logo512.png",
  },
  { id: "SE181819", name: "Trần Thị B", class: "4B", avatar: "./logo512.png" },
  // Thêm học sinh khác nếu cần
];

const Notifications = () => {
  const [selectedStudentId, setSelectedStudentId] = useState(students[0].id);
  const selectedStudent = students.find((s) => s.id === selectedStudentId);

  const [selectedNotification, setSelectedNotification] = useState(null);
  const [notifications, setNotifications] = useState([
    {
      id: 1,
      type: "Thông báo đăng ký tiêm phòng vaccin Sởi",
      date: "19-05-2004",
      status: "Chưa xem",
      statusClass: "status-unread",
    },
    {
      id: 2,
      type: "Đăng ký tham gia khám sức khỏe định kỳ tại trường",
      date: "19-05-2004",
      status: "Hoàn thành",
      statusClass: "status-completed",
    },
    {
      id: 3,
      type: "Đăng ký tham gia khám sức khỏe định kỳ tại trường ...",
      date: "19-05-2004",
      status: "Hoàn thành",
      statusClass: "status-completed",
    },
  ]);

  useEffect(() => {
    if (!selectedStudentId) return;
    const fetchEvents = async () => {
      try {
        const res = await axios.get(
          `http://localhost:8080/api/parent/vaccine-forms/student/${selectedStudentId}`,
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
        );
        setNotifications(Array.isArray(res.data) ? res.data : []);
        console.log("Fetched notifications:", notifications);
      } catch (error) {
        console.error("Error fetching notifications:", error);
      }
    };
    fetchEvents();
  }, [selectedStudentId]);

  const handleNotificationClick = (notification) => {
    if (notification.status === "Chưa xem") {
      setNotifications((prev) =>
        prev.map((n) =>
          n.id === notification.id
            ? { ...n, status: "Hoàn thành", statusClass: "status-completed" }
            : n
        )
      );
      setSelectedNotification({
        ...notification,
        status: "Hoàn thành",
        statusClass: "status-completed",
      });
    } else {
      setSelectedNotification(notification);
    }
  };

  return (
    <div className="notifications-container">
      <h1 className="main-title">Các thông báo</h1>
      <div className="notifications-content">
        {/* Left Section: Student Overview */}
        <div className="left-panel">
          <StudentInfoCard onChange={setSelectedStudentId} />
        </div>

        {/* Right Section: Notifications List */}
        <div className="right-panel-notifications">
          <NotificationsList notifications={notifications} />
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
