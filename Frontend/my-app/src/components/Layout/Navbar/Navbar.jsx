// Navbar.js
import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from "react-router-dom";
import '../Navbar/Navbar.css';
import Login from '../Login/Login';
import NotificationSocket from './NotificationSocket';
import NotificationPanel from './NotificationPanel';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBell } from '@fortawesome/free-solid-svg-icons';

const Navbar = () => {
  const [isLoginOpen, setIsLoginOpen] = useState(false);
  const [user, setUser] = useState(null);
  const [isNotiOpen, setIsNotiOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [numberNoti, setNumberNoti] = useState();

  const navigate = useNavigate();

  useEffect(() => {
    const storedUser = localStorage.getItem('users');
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
  }, [isLoginOpen]);

  useEffect(() => {
    if (!user) return;

    const fetchNotifications = async () => {
      try {
        const token = localStorage.getItem('token');
        const students = JSON.parse(localStorage.getItem('students') || '[]');
        const studentId = students[0]?.id;

        const res = await fetch(`http://localhost:8080/api/notify/${studentId}`, {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });

        if (!res.ok) throw new Error("Lỗi khi lấy thông báo");

        const data = await res.json();
        setNotifications(data);
      } catch (err) {
        console.error("❌ Không thể lấy thông báo:", err);
      }
    };

    fetchNotifications();
  }, [user]);

  const handleLoginClick = () => setIsLoginOpen(true);
  const handleCloseLogin = () => setIsLoginOpen(false);

  const handleLogout = () => {
    ['users', 'token', 'students', 'role'].forEach((key) => localStorage.removeItem(key));
    setUser(null);
  };

  const toggleNotificationPanel = () => {
    setIsNotiOpen((prev) => !prev);
    setNumberNoti("0");
  };

  const renderGreeting = () => {
    const name = user?.fullName?.split(" ").pop() || user?.email || '';
    return <b style={{ fontSize: "20px" }}>{name}</b>;
  };

  return (
    <>
      <nav className="navbar">
        <ul className="nav-links">
          <li className="logo"><img src="./Logo.png" alt="Logo" /></li>
          <li> <a style={{cursor:'pointer'}} onClick={()=> {navigate("/")}}>Trang chủ</a></li>
          <li><a href="/">Tài liệu</a></li>
          <li><a href="/">Blog</a></li>
          <li><a href="/">Giới thiệu</a></li>
          <li className="search-bar">
            <input type="text" placeholder="Tìm kiếm ..." />
          </li>

          {user && (
            <li className="notification-wrapper">
              <button className="notification-button" onClick={toggleNotificationPanel}>
                <FontAwesomeIcon icon={faBell} style={{ fontSize: "32px", color: "#1976d2" }} />
                {(Number(numberNoti) >= 1 || notifications.length >= 1) && (
                  <span style={{
                    background: "#1976d2",
                    color: "#fff",
                    borderRadius: "50%",
                    padding: "2px 8px",
                    fontSize: "16px",
                    marginLeft: "2px",
                    position: "relative",
                    top: "-12px",
                    left: "-10px"
                  }}>
                    {numberNoti || notifications.length}
                  </span>
                )}
              </button>
              {isNotiOpen && (
                <NotificationPanel
                  notifications={notifications}
                  onClose={() => setIsNotiOpen(false)}
                />
              )}
            </li>
          )}

          <li>
            {user ? (
              <>
                <span>{renderGreeting()}</span>
                <button id="btn-logout" onClick={handleLogout} style={{ marginLeft: '10px' }}>
                  Đăng xuất
                </button>
              </>
            ) : (
              <button id="btn-login" onClick={handleLoginClick}>Đăng nhập</button>
            )}
          </li>
        </ul>

        {isLoginOpen && <Login onClose={handleCloseLogin} />}
      </nav>

      {user?.id && (
        <NotificationSocket
          parentId={user.id}
          onMessage={(message) => {
            // console.log("🔔 Nhận được noti:", message);
            setNotifications((prev) => [...prev, message]);
          }}
        />
      )}
    </>
  );
};

export default Navbar;
