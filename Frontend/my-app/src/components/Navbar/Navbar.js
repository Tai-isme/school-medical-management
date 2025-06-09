import React, { useState, useEffect } from 'react';
import '../Navbar/Navbar.css';
import Login from '../../pages/Login/Login'; // Đảm bảo đường dẫn đúng đến component Login

const Navbar = () => {
  const [isLoginOpen, setIsLoginOpen] = useState(false);
  const [user, setUser] = useState(null);

  useEffect(() => {
    // Lấy user từ localStorage khi component mount hoặc khi login
    const storedUser = localStorage.getItem('users');
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
  }, [isLoginOpen]); // Cập nhật lại khi đóng/mở form login

  const handleLoginClick = () => {
    setIsLoginOpen(true);
  };

  const handleCloseLogin = () => {
    setIsLoginOpen(false);
    // Sau khi đóng form login, cập nhật lại user
    const storedUser = localStorage.getItem('users');
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('users');
    localStorage.removeItem('token');
    setUser(null);
  };

  return (
    <nav className="navbar">
      <ul className="nav-links">
        <li className="logo"><img src="/logo.png" alt="Anh logo" /> </li>
        <li><a href="http://localhost:3000/">Trang chủ</a></li>
        <li><a href="http://localhost:3000/">Tài liệu</a></li>
        <li><a href="http://localhost:3000/">Blog</a></li>
        <li><a href="http://localhost:3000/">Giới thiệu</a></li>
        <li className="search-bar">
          <input type="text" placeholder="Tìm kiếm ..." />
        </li>
        <li>
          {user ? (
            <>
              <span>Xin chào    
                <b style={{fontSize: "20px"}}>
                  { (user.fullName ? user.fullName.split(" ").pop() : "") || user.email}
                </b>
                </span>
              <button id='btn-logout' onClick={handleLogout} style={{marginLeft: '10px'}}>Đăng xuất</button>
            </>
          ) : (
            <button id='btn-login' onClick={handleLoginClick}>Đăng nhập</button>
          )}
        </li>
      </ul>

      {isLoginOpen && (
        <Login onClose={handleCloseLogin}/>
      )}
    </nav>
  );
};

export default Navbar;