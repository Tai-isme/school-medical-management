import React, { useEffect, useState } from "react";
import { Table, Button, message, Input, Row, Col, Select, Modal, Form } from "antd";
import { EyeOutlined, EyeInvisibleOutlined, SearchOutlined } from "@ant-design/icons";
import axios from "axios";
import Swal from "sweetalert2";

import "./AccountManagement.css";

const AccountManagement = () => {
  const [accounts, setAccounts] = useState([]);
  const [visiblePasswords, setVisiblePasswords] = useState({});
  const [searchName, setSearchName] = useState("");
  const [searchEmail, setSearchEmail] = useState("");
  const [filterRole, setFilterRole] = useState(""); // Trạng thái cho vai trò
  const [createModalVisible, setCreateModalVisible] = useState(false);
  const [createLoading, setCreateLoading] = useState(false);
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [editLoading, setEditLoading] = useState(false);
  const [form] = Form.useForm();
  const [editForm] = Form.useForm();
  const [editingAccount, setEditingAccount] = useState(null);
  const userRole = localStorage.getItem("role"); // Lấy role từ localStorage

  useEffect(() => {
    const fetchAccounts = async () => {
      try {
        const token = localStorage.getItem("token");
        if (!token) {
          message.error("Không tìm thấy token. Vui lòng đăng nhập lại.");
          return;
        }

        const response = await axios.get("http://localhost:8080/api/admin/accounts", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        const responseData = response.data.map((item) => ({
          userId: item.id,
          fullName: item.fullName,
          email: item.email,
          password: item.password,
          phone: item.phone,
          address: item.address,
          role: item.role.toLowerCase(),
        }));

        setAccounts(responseData);
      } catch (error) {
        console.error("Lỗi khi fetch danh sách tài khoản:", error);
        message.error("Không thể tải danh sách tài khoản.");
      }
    };

    fetchAccounts();
  }, []);

  const togglePasswordVisibility = (userId) => {
    setVisiblePasswords((prev) => ({
      ...prev,
      [userId]: !prev[userId],
    }));
  };

  // Lọc dữ liệu theo họ tên, email và vai trò
  const filteredAccounts = accounts.filter((acc) => {
    // Nếu role của người dùng là "NURSE", loại bỏ các tài khoản có role là "ADMIN"
    if (userRole === "NURSE" && acc.role === "admin") {
      return false;
    }
    return (
      acc.fullName.toLowerCase().includes(searchName.toLowerCase()) &&
      acc.email.toLowerCase().includes(searchEmail.toLowerCase()) &&
      (filterRole === "" || acc.role === filterRole)
    );
  });

  // Định nghĩa columns, loại bỏ cột "Hành động" nếu không phải admin
  const columns = [
    { title: "Mã", dataIndex: "userId", key: "userId", align: "center", width: 80, ellipsis: true },
    {
      title: "Họ và tên",
      dataIndex: "fullName",
      key: "fullName",
      align: "center",
      width: 200,
      ellipsis: true,
    },
    { title: "Email", dataIndex: "email", key: "email", align: "center", width: 250, ellipsis: true },
    { title: "SĐT", dataIndex: "phone", key: "phone", align: "center", width: 150, ellipsis: true },
    { title: "Địa chỉ", dataIndex: "address", key: "address", align: "center", width: 200, ellipsis: true },
    {
      title: "Vai trò",
      dataIndex: "role",
      key: "role",
      align: "center",
      width: 120,
      ellipsis: true,
      sorter: (a, b) => a.role.localeCompare(b.role),
    },
    ...(userRole === "ADMIN"
      ? [
          {
            title: "Hành động",
            key: "action",
            align: "center",
            width: 150,
            render: (_, record) => (
              <div className="account-action-cell">
                <Button
                  className="account-action-btn"
                  size="small"
                  onClick={() => handleEditAccount(record)}
                >
                  Sửa
                </Button>
                <Button
                  className="account-action-btn danger"
                  size="small"
                  onClick={() => handleDisableAccount(record)}
                >
                  Vô hiệu hóa
                </Button>
              </div>
            ),
          },
        ]
      : []),
  ];

  // Hàm tạo tài khoản Nurse
  const handleCreateNurse = async (values) => {
    setCreateLoading(true);
    try {
      const token = localStorage.getItem("token");
      console.log("Token:", values);
      await axios.post(
        "http://localhost:8080/api/admin/create-nurses-account",
        {
          name: values.fullName,
          email: values.email,
          newPassword: values.password, // Đổi thành newPassword nếu backend yêu cầu
          phone: values.phone,
          address: values.address,
          role: "nurse",
        },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      message.success("Tạo tài khoản Nurse thành công!");
      setCreateModalVisible(false);
      form.resetFields();
      // Reload danh sách tài khoản
      const response = await axios.get("http://localhost:8080/api/admin/accounts", {
        headers: { Authorization: `Bearer ${token}` },
      });
      const responseData = response.data.map((item) => ({
        userId: item.id,
        fullName: item.fullName,
        email: item.email,
        password: item.password,
        phone: item.phone,
        address: item.address,
        role: item.role.toLowerCase(),
      }));
      setAccounts(responseData);
    } catch (err) {
      message.error("Tạo tài khoản Nurse thất bại!");
    } finally {
      setCreateLoading(false);
    }
  };

  const handleEditAccount = (record) => {
    setEditingAccount(record);
    setEditModalVisible(true);
    // Set form values
    editForm.setFieldsValue({
      fullName: record.fullName,
      email: record.email,
      password: record.password,
      phone: record.phone,
      address: record.address,
    });
  };

  const handleUpdateAccount = async (values) => {
    setEditLoading(true);
    try {
      const token = localStorage.getItem("token");
      await axios.put(
        "http://localhost:8080/api/auth/update-profile",
        {
          fullName: values.fullName,
          email: values.email,
          phone: values.phone,
          address: values.address,
        },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      message.success("Cập nhật tài khoản thành công!");
      setEditModalVisible(false);
      // Reload danh sách tài khoản
      const response = await axios.get("http://localhost:8080/api/admin/accounts", {
        headers: { Authorization: `Bearer ${token}` },
      });
      const responseData = response.data.map((item) => ({
        userId: item.id,
        fullName: item.fullName,
        email: item.email,
        password: item.password,
        phone: item.phone,
        address: item.address,
        role: item.role.toLowerCase(),
      }));
      setAccounts(responseData);
    } catch (err) {
      message.error("Cập nhật tài khoản thất bại!");
    } finally {
      setEditLoading(false);
    }
  };

  const handleDisableAccount = async (record) => {
    const result = await Swal.fire({
      title: "Xác nhận vô hiệu hóa",
      text: `Bạn có chắc chắn muốn vô hiệu hóa tài khoản "${record.fullName}"?`,
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      cancelButtonColor: "#3085d6",
      confirmButtonText: "Vô hiệu hóa",
      cancelButtonText: "Hủy",
    });

    if (result.isConfirmed) {
      const token = localStorage.getItem("token");
      if (!token) {
        Swal.fire("Lỗi", "Không tìm thấy token. Vui lòng đăng nhập lại.", "error");
        return;
      }
      try {
        await axios.patch(
          `http://localhost:8080/api/auth/update-account-status/${record.userId}/false`,
          {},
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        Swal.fire("Thành công", `Đã vô hiệu hóa tài khoản: ${record.fullName}`, "success");
        // Reload danh sách tài khoản
        const response = await axios.get("http://localhost:8080/api/admin/accounts", {
          headers: { Authorization: `Bearer ${token}` },
        });
        const responseData = response.data.map((item) => ({
          userId: item.id,
          fullName: item.fullName,
          email: item.email,
          password: item.password,
          phone: item.phone,
          address: item.address,
          role: item.role.toLowerCase(),
        }));
        setAccounts(responseData);
      } catch (err) {
        Swal.fire("Lỗi", "Vô hiệu hóa tài khoản thất bại!", "error");
        console.error(err);
      }
    }
  };

  return (
    <div className="account-container">
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 16 }}>
        <h2 style={{ margin: 0 }}>Quản lý tài khoản</h2>
        {/* Chỉ hiển thị nút tạo tài khoản Nurse nếu là ADMIN */}
        {userRole === "ADMIN" && (
          <Button
            type="primary"
            onClick={() => setCreateModalVisible(true)}
          >
            Tạo tài khoản Nurse
          </Button>
        )}
      </div>
      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col>
          <Input
            placeholder="Tìm theo họ và tên"
            prefix={<SearchOutlined />}
            value={searchName}
            onChange={(e) => setSearchName(e.target.value)}
            allowClear
          />
        </Col>
        <Col>
          <Input
            placeholder="Tìm theo email"
            prefix={<SearchOutlined />}
            value={searchEmail}
            onChange={(e) => setSearchEmail(e.target.value)}
            allowClear
          />
        </Col>
        <Col>
          <Select
            placeholder="Lọc theo vai trò"
            style={{ minWidth: 150 }}
            allowClear
            value={filterRole || undefined}
            onChange={(value) => setFilterRole(value || "")}
          >
            <Select.Option value="">Tất cả</Select.Option>
            <Select.Option value="admin">Admin</Select.Option>
            <Select.Option value="nurse">Nurse</Select.Option>
            <Select.Option value="parent">Parent</Select.Option>
            {/* Thêm các vai trò khác nếu có */}
          </Select>
        </Col>
      </Row>
      <Table
        dataSource={filteredAccounts}
        columns={columns}
        rowKey="userId"
        bordered
        pagination={{ pageSize: 11 }} // Hiển thị tối đa 10 dòng mỗi trang
      />

      {/* Modal tạo tài khoản Nurse */}
      <Modal
        title="Tạo tài khoản Nurse"
        open={createModalVisible}
        onCancel={() => setCreateModalVisible(false)}
        onOk={() => form.submit()}
        confirmLoading={createLoading}
        okText="Tạo"
        cancelText="Hủy"
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleCreateNurse}
        >
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
            label="Mật khẩu"
            name="password"
            rules={[{ required: true, message: "Vui lòng nhập mật khẩu" }]}
          >
            <Input.Password />
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
          <Form.Item label="Vai trò">
            <Input value="Nurse" disabled />
          </Form.Item>
        </Form>
      </Modal>

      {/* Modal chỉnh sửa tài khoản */}
      <Modal
        title="Chỉnh sửa tài khoản"
        open={editModalVisible}
        onCancel={() => setEditModalVisible(false)}
        onOk={() => editForm.submit()}
        confirmLoading={editLoading}
        okText="Cập nhật"
        cancelText="Hủy"
      >
        <Form
          form={editForm}
          layout="vertical"
          onFinish={handleUpdateAccount}
        >
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
            label="Mật khẩu"
            name="password"
            rules={[{ required: true, message: "Vui lòng nhập mật khẩu" }]}
          >
            <Input.Password />
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

export default AccountManagement;
