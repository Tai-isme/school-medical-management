import React from "react";
import { Menu, Avatar } from "antd";
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
  return (
    <div className="sidebar-container">
      <div className="sidebar-logo">
        <img src="/logo1.png" alt="Logo Y Tế" />
      </div>
      {/* User Info */}
      <div
        style={{
          display: "flex",
          alignItems: "center",
          gap: 12,
          padding: "12px 16px 8px 16px",
          borderBottom: "1px solid #f0f0f0",
          marginBottom: 8,
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
      <Menu
        mode="inline"
        theme="light"
        selectedKeys={[selectedMenu]}
        onClick={({ key }) => onMenuSelect(key)}
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
      </Menu>

      <div
        className="logout-button"
        onClick={handleLogout}
        style={{
          padding: "10px 16px",
          borderTop: "1px solid #f0f0f0",
          cursor: "pointer",
          color: "red",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          fontSize: 14,
        }}
      >
        <LogoutOutlined style={{ marginRight: 8 }} />
        Đăng xuất
      </div>
    </div>
  );
};

export default AppSidebar;
