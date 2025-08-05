import React from "react";
import { urlServer } from "../../../api/urlServer.js";

const NotificationPanel = ({ notifications, onClose, onRead }) => {
  const handleViewDetail = async (notification) => {
    console.log("🔍 Chi tiết thông báo:", notification);
    const token = localStorage.getItem("token");
    try {
      await fetch(`http://localhost:8080/api/notify/${notification.id}/read`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });
      // Gọi callback để cập nhật lại UI
      if (onRead) onRead(notification.id);
    } catch (error) {
      console.error("Lỗi đánh dấu đã đọc:", error);
    }
  };

  const sortedNotifications = [...notifications].sort((a, b) => {
    if (a.isRead === b.isRead) {
      return new Date(b.createdAt) - new Date(a.createdAt); // mới nhất trước
    }
    return a.isRead ? 1 : -1; // chưa đọc lên đầu
  });

  return (
    <div className="notification-popup" style={styles.popup}>
      <div className="popup-header" style={styles.header}>
        <h4 style={styles.title}>Thông báo</h4>
        <button style={styles.closeBtn} onClick={onClose}>
          ×
        </button>
      </div>
      <div className="popup-body" style={styles.body}>
        {notifications.length > 0 ? (
          <ul style={styles.list}>
            {sortedNotifications.map((n, index) => (
              <li
                key={index}
                onClick={() => handleViewDetail(n)}
                style={{
                  ...styles.item,
                  backgroundColor: n.isRead ? "#f2f2f2" : "#e6f7ff",
                  cursor: "pointer",
                }}
              >
                <div>
                  <strong>{n.title}</strong>
                </div>
                <div>{n.content}</div>
                <div style={styles.time}>
                  {new Date(n.createdAt).toLocaleString("vi-VN")}
                </div>
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

const styles = {
  popup: {
    position: "absolute",
    top: 60,
    right: 20,
    width: 300,
    backgroundColor: "#fff",
    border: "1px solid #ddd",
    borderRadius: 8,
    boxShadow: "0 4px 8px rgba(0,0,0,0.1)",
    zIndex: 999,
  },
  header: {
    padding: "10px 12px",
    borderBottom: "1px solid #eee",
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
  },
  title: {
    margin: 0,
  },
  closeBtn: {
    background: "transparent",
    border: "none",
    fontSize: 20,
    cursor: "pointer",
  },
  body: {
    maxHeight: 400,
    overflowY: "auto",
    padding: 10,
  },
  list: {
    listStyle: "none",
    padding: 0,
    margin: 0,
  },
  item: {
    borderBottom: "1px solid #eee",
    paddingBottom: 10,
    marginBottom: 10,
  },
  time: {
    fontSize: 12,
    color: "#888",
  },
  detailBtn: {
    marginTop: 5,
    backgroundColor: "#007bff",
    color: "#fff",
    border: "none",
    borderRadius: 4,
    padding: "5px 10px",
    cursor: "pointer",
  },
};

export default NotificationPanel;
