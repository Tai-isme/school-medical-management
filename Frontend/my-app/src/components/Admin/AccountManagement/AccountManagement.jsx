import React, { useEffect, useState } from "react";
import { Table, Button } from "antd";
import { EyeOutlined, EyeInvisibleOutlined } from "@ant-design/icons";
import "./AccountManagement.css";

const AccountManagement = () => {
  const [accounts, setAccounts] = useState([]);
  const [visiblePasswords, setVisiblePasswords] = useState({});

  useEffect(() => {
    // Dữ liệu cứng để test giao diện
    const mockData = [
      {
        userId: 1,
        fullName: "Nguyễn Văn A",
        email: "a@example.com",
        password: "123456",
        phone: "0901234567",
        address: "Hồ Chí Minh",
        role: "admin",
      },
      {
        userId: 2,
        fullName: "Trần Thị B",
        email: "b@example.com",
        password: "abcdef",
        phone: "0912345678",
        address: "Hà Nội",
        role: "nurse",
      },
      {
        userId: 3,
        fullName: "Lê Văn C",
        email: "c@example.com",
        password: "123abc",
        phone: "0923456789",
        address: "Đà Nẵng",
        role: "parent",
      },
    ];

    setAccounts(mockData);
  }, []);

  const togglePasswordVisibility = (userId) => {
    setVisiblePasswords((prev) => ({
      ...prev,
      [userId]: !prev[userId],
    }));
  };

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
    { title: "Vai trò", dataIndex: "role", key: "role", align: "center" },
  ];

  return (
    <div className="account-container">
      <h2>Quản lý tài khoản</h2>
      <Table
        dataSource={accounts}
        columns={columns}
        rowKey="userId"
        bordered
        pagination={{ pageSize: 8 }}
      />
    </div>
  );
};

export default AccountManagement;
