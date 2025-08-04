import React, { useState } from "react";
import { Form, Input, Button, Card } from "antd";
import axios from "axios";
import Swal from "sweetalert2";
import { urlServer } from "../../../api/urlServer";
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
  color: "#00796b",
  fontWeight: 600,
};

const ChangePasswordForm = () => {
  const [loading, setLoading] = useState(false);

  const onFinish = async (values) => {
    setLoading(true);
    try {
      const token = localStorage.getItem("token");
      await axios.post(
        `${urlServer}/api/auth/change-password`,
        {
          oldPassword: values.oldPassword,
          newPassword: values.newPassword,
        },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      Swal.fire({
        icon: "success",
        title: "Thành công",
        text: "Đổi mật khẩu thành công!",
      });
    } catch (err) {
      console.log(err.response);
      if (
        err.response &&
        err.response.data &&
        err.response.data.message
      ) {
        const serverMsg = err.response.data.message;
        const errorMessageMap = {
          "Old password is incorrect!": "Mật khẩu cũ không đúng!",
        };
        Swal.fire({
          icon: "error",
          title: "Lỗi",
          text: errorMessageMap[serverMsg] || serverMsg,
        });
      } else {
        Swal.fire({
          icon: "error",
          title: "Lỗi",
          text: "Đổi mật khẩu thất bại!",
        });
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card title={<span style={titleStyle}>Đổi mật khẩu</span>} style={cardStyle}>
      <Form layout="vertical" onFinish={onFinish}>
        <Form.Item
          label={<span style={labelStyle}>Mật khẩu cũ</span>}
          name="oldPassword"
          rules={[{ required: true, message: "Vui lòng nhập mật khẩu cũ" }]}
        >
          <Input.Password />
        </Form.Item>
        <Form.Item
          label={<span style={labelStyle}>Mật khẩu mới</span>}
          name="newPassword"
          rules={[{ required: true, message: "Vui lòng nhập mật khẩu mới" }]}
        >
          <Input.Password />
        </Form.Item>
        <Form.Item
          label={<span style={labelStyle}>Nhập lại mật khẩu mới</span>}
          name="confirmPassword"
          dependencies={["newPassword"]}
          rules={[
            { required: true, message: "Vui lòng nhập lại mật khẩu mới" },
            ({ getFieldValue }) => ({
              validator(_, value) {
                if (!value || getFieldValue("newPassword") === value) {
                  return Promise.resolve();
                }
                return Promise.reject("Mật khẩu nhập lại không khớp!");
              },
            }),
          ]}
        >
          <Input.Password />
        </Form.Item>
        <Form.Item>
          <Button
            type="primary"
            htmlType="submit"
            loading={loading}
            block
            style={{
              background: "#009688",
              borderColor: "#009688",
            }}
          >
            Đổi mật khẩu
          </Button>
        </Form.Item>
      </Form>
    </Card>
  );
};

export default ChangePasswordForm;
