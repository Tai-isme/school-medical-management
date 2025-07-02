import React, { useState } from "react";
import { Menu, Avatar, Dropdown } from "antd";
import {
  HomeOutlined,
  UserSwitchOutlined,
  CalendarOutlined,
  MedicineBoxOutlined,
  BarChartOutlined,
  UnorderedListOutlined,
  AlertOutlined,
  CommentOutlined,
  LogoutOutlined,
  UserOutlined,
} from "@ant-design/icons";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "./AppSidebar.css";

const { SubMenu } = Menu;

const AppSidebar = ({ onMenuSelect, selectedMenu }) => {
  const [selectedClassId, setSelectedClassId] = useState(null);
  const [selectedStudentId, setSelectedStudentId] = useState(null);
  const navigate = useNavigate();

  // Lấy thông tin user từ localStorage
  let user = null;
  try {
    user = JSON.parse(localStorage.getItem("users"));
  } catch {
    user = null;
  }

  // Hàm xử lý đăng xuất
  const handleLogout = async () => {
    const token = localStorage.getItem("token");
    try {
      await axios.post(
        "http://localhost:8080/api/auth/logout",
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
    } catch (error) {
      // Có thể log lỗi hoặc bỏ qua
    } finally {
      localStorage.removeItem("token");
      localStorage.removeItem("users");
      localStorage.removeItem("students");
      localStorage.removeItem("role");
      navigate("/");
    }
  }; // vừa thêm

  // Xử lý chọn menu trong dropdown user info
  const handleUserMenuClick = ({ key }) => {
    if (key === "info") {
      onMenuSelect("account-info");
    } else if (key === "changepass") {
      onMenuSelect("changepass"); // Không dùng navigate!
    } else if (key === "logout") {
      handleLogout();
    }
  };

  const handleMenuSelect = (key) => {
    console.log("Menu selected:", key);
    onMenuSelect(key);
    setSelectedClassId(null);
    setSelectedStudentId(null);
  };

  const userMenu = (
    <Menu onClick={handleUserMenuClick}>
      <Menu.Item key="info" icon={<UserOutlined />}>
        Thông tin tài khoản
      </Menu.Item>
      <Menu.Item key="changepass" icon={<UserSwitchOutlined />}>
        Đổi mật khẩu
      </Menu.Item>
      <Menu.Divider />
      <Menu.Item key="logout" icon={<LogoutOutlined />} style={{ color: "red" }}>
        Đăng xuất
      </Menu.Item>
    </Menu>
  );

  return (
    <div className="sidebar-container">
      <div className="sidebar-logo">
        <img src="/logo1.png" alt="Logo Y Tế" />
      </div>
      {/* User Info Dropdown */}
      <Dropdown overlay={userMenu} trigger={["click"]}>
        <div
          style={{
            display: "flex",
            alignItems: "center",
            gap: 12,
            padding: "12px 16px 8px 16px",
            borderBottom: "1px solid #f0f0f0",
            marginBottom: 8,
            cursor: "pointer",
          }}
        >
          <div className="sidebar-userinfo">
            <Avatar
              size={32}
              src={user?.avatar || undefined}
              icon={<UserOutlined />}
              style={{ background: "#e6f7ff", color: "#1476d1" }}
            />
            <div className="sidebar-userinfo-name">
              {user?.fullName || user?.username || "User"}
            </div>
          </div>
        </div>
      </Dropdown>
      <Menu
        mode="inline"
        theme="light"
        selectedKeys={[selectedMenu]}
        onClick={({ key }) => {
          onMenuSelect(key); // Luôn gọi cho mọi key
        }}
        style={{ flex: 1, borderRight: 0, minHeight: 0 }}
      >
        
        <Menu.Item key="1" icon={<HomeOutlined />}>
          Dashboard
        </Menu.Item>
        <Menu.Item key="2" icon={<UserSwitchOutlined />}>
          Quản lý tài khoản
        </Menu.Item>
        <Menu.Item key="3" icon={<CalendarOutlined />}>
          Hồ sơ sức khỏe
        </Menu.Item>
        <Menu.Item key="4" icon={<MedicineBoxOutlined />}>
          Yêu cầu gửi thuốc
        </Menu.Item>
        <SubMenu key="5" icon={<BarChartOutlined />} title="Các chương trình">
          <Menu.Item key="5-1">Vaccine</Menu.Item>
          <Menu.Item key="5-2">Khám định kỳ</Menu.Item>
        </SubMenu>
        <Menu.Item key="7" icon={<AlertOutlined />}>
          Sự cố y tế
        </Menu.Item>
        <Menu.Item key="8" icon={<CommentOutlined />}>
          Phản hồi
        </Menu.Item>
        <Menu.Item key="9" icon={<CommentOutlined />}>
          Blog
        </Menu.Item>
      </Menu>
    </div>
  );
};

export default AppSidebar;
