import React, { useEffect, useState } from "react";
import { Table, Button, message, Input, Row, Col, Select } from "antd";
import { EyeOutlined, EyeInvisibleOutlined, SearchOutlined } from "@ant-design/icons";
import axios from "axios";
import "./AccountManagement.css";

const AccountManagement = () => {
  const [accounts, setAccounts] = useState([]);
  const [visiblePasswords, setVisiblePasswords] = useState({});
  const [searchName, setSearchName] = useState("");
  const [searchEmail, setSearchEmail] = useState("");
  const [filterRole, setFilterRole] = useState(""); // Trạng thái cho vai trò

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
  const filteredAccounts = accounts.filter(
    (acc) =>
      acc.fullName.toLowerCase().includes(searchName.toLowerCase()) &&
      acc.email.toLowerCase().includes(searchEmail.toLowerCase()) &&
      (filterRole === "" || acc.role === filterRole)
  );

  const columns = [
    { title: "Mã", dataIndex: "userId", key: "userId", align: "center" },
    {
      title: "Họ và tên",
      dataIndex: "fullName",
      key: "fullName",
      align: "center",
    },
    { title: "Email", dataIndex: "email", key: "email", align: "center" },
    {
      title: "Mật khẩu",
      key: "password",
      align: "center",
      render: (_, record) => (
        <div
          style={{
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            gap: 8,
          }}
        >
          <span>
            {visiblePasswords[record.userId] ? record.password : "••••••••"}
          </span>
          <Button
            type="text"
            icon={
              visiblePasswords[record.userId] ? (
                <EyeInvisibleOutlined />
              ) : (
                <EyeOutlined />
              )
            }
            onClick={() => togglePasswordVisibility(record.userId)}
          />
        </div>
      ),
    },
    { title: "SĐT", dataIndex: "phone", key: "phone", align: "center" },
    { title: "Địa chỉ", dataIndex: "address", key: "address", align: "center" },
    {
      title: "Vai trò",
      dataIndex: "role",
      key: "role",
      align: "center",
      sorter: (a, b) => a.role.localeCompare(b.role),
    },
  ];

  return (
    <div className="account-container">
      <h2>Quản lý tài khoản</h2>
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
        pagination={{ pageSize: 8 }}
      />
    </div>
  );
};

export default AccountManagement;
