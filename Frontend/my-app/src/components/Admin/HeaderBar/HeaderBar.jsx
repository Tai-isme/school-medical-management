import React, { useState } from "react";
import {
  BellOutlined,
  UserOutlined,
  SmileOutlined,
  SettingOutlined,
  EditOutlined,
  LogoutOutlined,
  KeyOutlined,
  MenuFoldOutlined,
} from "@ant-design/icons";
import { Dropdown, Menu, message, Modal, Form, Input } from "antd";

export default function HeaderBar({ onMenuSelect }) {
  const [isChangePwdVisible, setIsChangePwdVisible] = useState(false);
  const [form] = Form.useForm();

  const handleMenuClick = ({ key }) => {
    switch (key) {
      case "profile":
        onMenuSelect && onMenuSelect("profile");
        break;
      case "change-password":
        setIsChangePwdVisible(true);
        break;
      case "settings":
        message.info("Chức năng cài đặt đang được phát triển.");
        break;
      case "logout":
        message.success("Đăng xuất thành công!");
        localStorage.removeItem("token");
        window.location.href = "/";
        break;
      default:
        break;
    }
  };

  const handleChangePwdOk = async () => {
    try {
      const values = await form.validateFields();
      // Gọi API đổi mật khẩu ở đây
      // Ví dụ:
      /*
      const token = localStorage.getItem("token");
      await fetch("/api/admin/change-password", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          oldPassword: values.oldPassword,
          newPassword: values.newPassword,
        }),
      });
      */
      message.success("Đổi mật khẩu thành công!");
      setIsChangePwdVisible(false);
      form.resetFields();
    } catch (err) {
      // Nếu validate lỗi sẽ không vào đây
    }
  };

  const handleChangePwdCancel = () => {
    setIsChangePwdVisible(false);
    form.resetFields();
  };

  const userMenu = (
    <Menu onClick={handleMenuClick}>
      <Menu.Item key="profile" icon={<EditOutlined />}>
        Thông tin tài khoản
      </Menu.Item>
      <Menu.Item key="change-password" icon={<KeyOutlined />}>
        Đổi mật khẩu
      </Menu.Item>
      <Menu.Item key="settings" icon={<SettingOutlined />}>
        Cài đặt
      </Menu.Item>
      <Menu.Divider />
      <Menu.Item
        key="logout"
        icon={<LogoutOutlined />}
        danger
        style={{ fontWeight: "bold" }}
      >
        Đăng xuất
      </Menu.Item>
    </Menu>
  );

  return (
    <div
      style={{
        width: "100%",
        height: "60px",
        background: "linear-gradient(90deg, #43cea2 0%, #185a9d 100%)",
        color: "#fff",
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
        padding: "0 32px",
        fontSize: "20px",
        fontWeight: "bold",
        boxShadow: "0 2px 8px rgba(67, 206, 162, 0.08)",
        position: "fixed",
        top: 0,
        left: 0,
        zIndex: 1000,
        letterSpacing: 1,
      }}
    >
      <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
        <MenuFoldOutlined 
          style={{ fontSize: 26, marginRight: 10, cursor: "pointer" }}
        />
        <span
          style={{
            fontSize: 22,
            fontWeight: 700,
            fontFamily: "Segoe UI, Arial",
          }}
        >
          School Medical Management - Admin
        </span>
      </div>
      <div
        style={{
          display: "flex",
          alignItems: "center",
          gap: "20px",
          fontSize: "22px",
        }}
      >
        <BellOutlined style={{ cursor: "pointer", color: "#fff" }} />
        <Dropdown
          overlay={userMenu}
          placement="bottomRight"
          trigger={["click"]}
        >
          <UserOutlined style={{ cursor: "pointer", color: "#fff" }} />
        </Dropdown>
        <SmileOutlined style={{ color: "#fff" }} />
      </div>
      <Modal
        title="Đổi mật khẩu"
        open={isChangePwdVisible}
        onOk={handleChangePwdOk}
        onCancel={handleChangePwdCancel}
        okText="Đổi mật khẩu"
        cancelText="Hủy"
      >
        <Form form={form} layout="vertical">
          <Form.Item
            label="Mật khẩu cũ"
            name="oldPassword"
            rules={[{ required: true, message: "Vui lòng nhập mật khẩu cũ" }]}
          >
            <Input.Password />
          </Form.Item>
          <Form.Item
            label="Mật khẩu mới"
            name="newPassword"
            rules={[{ required: true, message: "Vui lòng nhập mật khẩu mới" }]}
          >
            <Input.Password />
          </Form.Item>
          <Form.Item
            label="Xác nhận mật khẩu mới"
            name="confirmPassword"
            dependencies={["newPassword"]}
            rules={[
              { required: true, message: "Vui lòng xác nhận mật khẩu mới" },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue("newPassword") === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(
                    new Error("Mật khẩu xác nhận không khớp!")
                  );
                },
              }),
            ]}
          >
            <Input.Password />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
