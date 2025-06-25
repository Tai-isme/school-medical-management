import React, { useState, useEffect } from "react";
import { Card, Avatar, Descriptions, Button, Modal, Form, Input, message } from "antd";
import { UserOutlined, EditOutlined } from "@ant-design/icons";

export default function ProfilePage() {
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [form] = Form.useForm();

  const [userInfo, setUserInfo] = useState({
    fullName: "",
    role: "",
    email: "",
    phone: "",
    username: "",
  });

  // Call API lấy thông tin tài khoản khi component mount
  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const token = localStorage.getItem("token");
        const username = localStorage.getItem("username");
        const res = await fetch("http://localhost:8080/api/admin/accounts", {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        });
        const data = await res.json();
        console.log("API data:", data, "Username:", username); // Thêm dòng này
        const currentUser = Array.isArray(data) ? data[0] : null;
        if (currentUser) {
          setUserInfo({
            fullName: currentUser.fullName,
            role: currentUser.role,
            email: currentUser.email,
            phone: currentUser.phone,
            username: currentUser.username || "",
          });
        } else {
          message.error("Không tìm thấy tài khoản phù hợp!");
        }
      } catch (error) {
        message.error("Không thể lấy thông tin tài khoản!");
      }
    };
    fetchProfile();
  }, []);

  const showModal = () => {
    form.setFieldsValue(userInfo); // Điền dữ liệu cũ vào form
    setIsModalVisible(true);
  };

  const handleOk = () => {
    form
      .validateFields()
      .then((values) => {
        setUserInfo({ ...userInfo, ...values });
        setIsModalVisible(false);
        message.success("Cập nhật thông tin thành công!");
      })
      .catch((info) => {
        console.log("Validate Failed:", info);
      });
  };

  const handleCancel = () => {
    setIsModalVisible(false);
  };

  return (
    <div style={styles.container}>
      <Card
        title="Thông tin tài khoản"
        bordered={false}
        style={styles.card}
        extra={<Button icon={<EditOutlined />} type="primary" onClick={showModal}>Chỉnh sửa</Button>}
      >
        <div style={styles.header}>
          <Avatar size={100} icon={<UserOutlined />} style={styles.avatar} />
          <div style={styles.nameSection}>
            <h2 style={{ margin: 0 }}>{userInfo.fullName}</h2>
            <span style={styles.roleTag}>{userInfo.role}</span>
          </div>
        </div>

        <Descriptions
          bordered
          column={1}
          size="middle"
          style={{ marginTop: 24 }}
          labelStyle={{ width: 200, fontWeight: 'bold' }}
        >
          <Descriptions.Item label="Tên người dùng">{userInfo.username}</Descriptions.Item>
          <Descriptions.Item label="Email">{userInfo.email}</Descriptions.Item>
          <Descriptions.Item label="Số điện thoại">{userInfo.phone}</Descriptions.Item>
        </Descriptions>
      </Card>

      <Modal
        title="Chỉnh sửa thông tin tài khoản"
        open={isModalVisible}
        onOk={handleOk}
        onCancel={handleCancel}
        okText="Lưu"
        cancelText="Hủy"
      >
        <Form layout="vertical" form={form}>
          <Form.Item label="Họ và tên" name="fullName" rules={[{ required: true, message: "Vui lòng nhập họ tên" }]}>
            <Input />
          </Form.Item>
          <Form.Item label="Email" name="email" rules={[{ type: "email", message: "Email không hợp lệ" }]}>
            <Input />
          </Form.Item>
          <Form.Item label="Số điện thoại" name="phone" rules={[{ required: true, message: "Vui lòng nhập số điện thoại" }]}>
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}

const styles = {
  container: {
    flex: 1,
    padding: "40px 24px",
    background: "#f5f7fa",
    minHeight: "100vh",
  },
  card: {
    maxWidth: 800,
    margin: "0 auto",
    boxShadow: "0 2px 8px rgba(0, 0, 0, 0.1)",
    borderRadius: 12,
  },
  header: {
    display: "flex",
    alignItems: "center",
    gap: 24,
  },
  avatar: {
    backgroundColor: "#1890ff",
  },
  nameSection: {
    display: "flex",
    flexDirection: "column",
    justifyContent: "center",
  },
  roleTag: {
    background: "#e3f2fd",
    color: "#1976d2",
    fontWeight: "500",
    padding: "4px 12px",
    borderRadius: "20px",
    display: "inline-block",
    marginTop: "8px",
    fontSize: "14px",
  },
};
