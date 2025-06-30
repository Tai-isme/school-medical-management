// NotificationPanel.js
import React from 'react';

const NotificationPanel = ({ notifications, onClose }) => {
  const handleViewDetail = (notification) => {
    console.log("🔍 Chi tiết thông báo:", notification);
  };

  return (
    <div className="notification-popup">
      <div className="popup-header">
        <h4>Thông báo</h4>
        <button onClick={onClose}>×</button>
      </div>
      <div className="popup-body">
        {notifications.length > 0 ? (
          <ul>
            {notifications.map((n, index) => (
              <li key={index} className="notification-item">
                <strong>{n.title}</strong><br />
                {n.content}<br />
                <small>{new Date(n.createdAt).toLocaleString()}</small><br />
                <button onClick={() => handleViewDetail(n)}>Xem chi tiết</button>
              </li>
            ))}
          </ul>
        ) : (
          <p>Không có thông báo mới.</p>
        )}
      </div>
    </div>
  );
};

export default NotificationPanel;
