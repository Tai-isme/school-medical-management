import React, { useEffect, useState } from "react";
import { Card, Descriptions, Spin, message, Button, Modal, Form, Input } from "antd";
import axios from "axios";

const cardStyle = {
  background: "#f4fff8",
  borderRadius: 16,
  boxShadow: "0 4px 16px rgba(67, 181, 129, 0.08)",
  border: "1px solid #b2dfdb",
  maxWidth: 900,
  width: "100%",
  margin: "32px auto",
};

const titleStyle = {
  color: "#009688",
  fontSize: 22,
  fontWeight: "bold",
  textAlign: "center",
};

const labelStyle = {
  background: "#e0f7fa",
  color: "#00796b",
  fontWeight: 600,
};

const contentStyle = {
  background: "#ffffff",
  color: "#1b5e20",
  fontSize: 16,
};

const AccountInfo = () => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [editVisible, setEditVisible] = useState(false);
  const [editLoading, setEditLoading] = useState(false);
  const [form] = Form.useForm();

  useEffect(() => {
    fetchUser();
    // eslint-disable-next-line
  }, []);

  const fetchUser = async () => {
    setLoading(true);
    try {
      const token = localStorage.getItem("token");
      const res = await axios.get("http://localhost:8080/api/auth/me", {
        headers: { Authorization: `Bearer ${token}` },
      });
      setUser(res.data.data);
    } catch (err) {
      message.error("Không thể lấy thông tin tài khoản!");
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = () => {
    form.setFieldsValue({
      fullName: user?.fullName,
      email: user?.email,
      phone: user?.phone,
      address: user?.address,
    });
    setEditVisible(true);
  };

  const handleUpdate = async (values) => {
    setEditLoading(true);
    try {
      const token = localStorage.getItem("token");
      await axios.put(
        "http://localhost:8080/api/auth/update-profile",
        values,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      message.success("Cập nhật thông tin thành công!");
      setEditVisible(false);
      fetchUser();
    } catch (err) {
      message.error("Cập nhật thất bại!");
    } finally {
      setEditLoading(false);
    }
  };

  if (loading) return <Spin style={{ display: "block", margin: "60px auto" }} />;

  return (
    <div style={{ width: "100%", display: "flex", justifyContent: "center", alignItems: "flex-start" }}>
      <Card
        title={<span style={titleStyle}>Thông tin tài khoản</span>}
        style={cardStyle}
        extra={
          <Button type="primary" onClick={handleEdit} style={{ background: "#009688", borderColor: "#009688" }}>
            Chỉnh sửa
          </Button>
        }
      >
        <Descriptions
          column={1}
          bordered
          labelStyle={labelStyle}
          contentStyle={contentStyle}
        >
          <Descriptions.Item label="Họ và tên">{user?.fullName}</Descriptions.Item>
          <Descriptions.Item label="Email">{user?.email}</Descriptions.Item>
          <Descriptions.Item label="Số điện thoại">{user?.phone}</Descriptions.Item>
          <Descriptions.Item label="Địa chỉ">{user?.address}</Descriptions.Item>
          <Descriptions.Item label="Vai trò">{user?.role}</Descriptions.Item>
        </Descriptions>
      </Card>

      <Modal
        title="Chỉnh sửa thông tin"
        open={editVisible}
        onCancel={() => setEditVisible(false)}
        onOk={() => form.submit()}
        confirmLoading={editLoading}
        okText="Lưu"
        cancelText="Hủy"
      >
        <Form form={form} layout="vertical" onFinish={handleUpdate}>
          <Form.Item
            label="Họ và tên"
            name="fullName"
            rules={[{ required: true, message: "Vui lòng nhập họ và tên" }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="Email"
            name="email"
            rules={[
              { required: true, message: "Vui lòng nhập email" },
              { type: "email", message: "Email không hợp lệ" },
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="Số điện thoại"
            name="phone"
            rules={[{ required: true, message: "Vui lòng nhập số điện thoại" }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="Địa chỉ"
            name="address"
            rules={[{ required: true, message: "Vui lòng nhập địa chỉ" }]}
          >
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default AccountInfo;