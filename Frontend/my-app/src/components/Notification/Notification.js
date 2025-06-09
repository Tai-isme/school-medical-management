import React, { useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import './Notifications.css';

const students = [
  { id: 'SE181818', name: 'Nguyễn Văn A', class: '5A', avatar: './logo512.png' },
  { id: 'SE181819', name: 'Trần Thị B', class: '4B', avatar: './logo512.png' },
  // Thêm học sinh khác nếu cần
];

const Notifications = () => {
  const [selectedStudentId, setSelectedStudentId] = useState(students[0].id);
  const selectedStudent = students.find(s => s.id === selectedStudentId);

  const [selectedNotification, setSelectedNotification] = useState(null);
  const [notifications, setNotifications] = useState([
    {
      id: 1,
      type: 'Thông báo đăng ký tiêm phòng vaccin Sởi',
      date: '19-05-2004',
      status: 'Chưa xem',
      statusClass: 'status-unread'
    },
    {
      id: 2,
      type: 'Đăng ký tham gia khám sức khỏe định kỳ tại trường',
      date: '19-05-2004',
      status: 'Hoàn thành',
      statusClass: 'status-completed'
    },
    {
      id: 3,
      type: 'Đăng ký tham gia khám sức khỏe định kỳ tại trường ...',
      date: '19-05-2004',
      status: 'Hoàn thành',
      statusClass: 'status-completed'
    },
  ]);

  const handleNotificationClick = (notification) => {
    if (notification.status === 'Chưa xem') {
      setNotifications(prev =>
        prev.map(n =>
          n.id === notification.id
            ? { ...n, status: 'Hoàn thành', statusClass: 'status-completed' }
            : n
        )
      );
      setSelectedNotification({
        ...notification,
        status: 'Hoàn thành',
        statusClass: 'status-completed'
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
          <h2>Học sinh</h2>
          <div className="student-selector">
            <select
              value={selectedStudentId}
              onChange={e => setSelectedStudentId(e.target.value)}
              style={{
                padding: '6px 20px',
                borderRadius: 8,
                border: '1.5px solid #a7d9ff',
                fontSize: 18,
                width: '100%',
                marginBottom: 12,
                textAlign: 'center',
              }}
            >
              {students.map(student => (
                <option key={student.id} value={student.id}>
                  {student.name}
                </option>
              ))}
            </select>
          </div>
          <div className="avatar-section">
            <img src={selectedStudent.avatar} alt="Student Avatar" className="student-avatar" />
          </div>
          <p className="student-id-display">
            <FontAwesomeIcon icon="id-card" /> {selectedStudent.id}
          </p>
          <p className="student-class-display">Lớp: {selectedStudent.class}</p>
        </div>

        {/* Right Section: Notifications List */}
        <div className="right-panel-notifications">
          <h2>Khảo sát</h2>
          <div className="notifications-list">
            {notifications.map((notification) => (
              <div key={notification.id} className="notification-item">
                <div className="notification-title-row">
                  <p
                    className="notification-type"
                    onClick={() => handleNotificationClick(notification)}
                    style={{ cursor: 'pointer', textDecoration: 'underline', color: '#007bff', marginBottom: 0 }}
                  >
                    {notification.type}
                  </p>
                </div>
                <div className="notification-info-row-bottom">
                  <p className="notification-date">Ngày: {notification.date}</p>
                  <span className={`notification-status ${notification.statusClass}`}>
                    {notification.status}
                  </span>
                </div>
              </div>
            ))}
          </div>
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