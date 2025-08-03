import React from "react";
import { Modal, Form, Input, Button } from "antd";


const HealthRecordModal = ({
  open,
  onCancel,
  onSubmit,
  data,
  loading = false,
}) => {
  const [form] = Form.useForm();


  React.useEffect(() => {
    if (data) {
      form.setFieldsValue({
        fullName: data.studentDTO?.fullName,
        dob: data.studentDTO?.dob,
        gender: data.studentDTO?.gender === "MALE" ? "Nam" : "Nữ",
        className: data.studentDTO?.classDTO?.className,
        parentName: data.studentDTO?.parentDTO?.fullName,
        height: data.height || "",
        weight: data.weight || "",
        chronicDisease: data.chronicDisease || "",
        allergy: data.allergy || "",
        note: data.note || "",
      });
    } else {
      form.resetFields();
    }
  }, [data, form, open]);


  return (
    <Modal
      open={open}
      onCancel={onCancel}
      footer={null}
      title="THÊM HỒ SƠ SỨC KHỎE MỚI"
      destroyOnClose
    >
      <Form form={form} layout="vertical" onFinish={onSubmit}>
        <Form.Item label="Họ tên:" name="fullName">
          <Input disabled />
        </Form.Item>
        <Form.Item label="Ngày sinh:" name="dob">
          <Input disabled />
        </Form.Item>
        <Form.Item label="Giới tính:" name="gender">
          <Input disabled />
        </Form.Item>
        <Form.Item label="Lớp:" name="className">
          <Input disabled />
        </Form.Item>
        <Form.Item label="Phụ huynh:" name="parentName">
          <Input disabled />
        </Form.Item>
        <Form.Item label="Chiều cao (cm):" name="height">
          <Input />
        </Form.Item>
        <Form.Item label="Cân nặng (kg):" name="weight">
          <Input />
        </Form.Item>
        <Form.Item label="Bệnh mãn tính:" name="chronicDisease">
          <Input />
        </Form.Item>
        <Form.Item label="Dị ứng:" name="allergy">
          <Input />
        </Form.Item>
        <Form.Item label="Ghi chú:" name="note">
          <Input.TextArea />
        </Form.Item>
        <Form.Item style={{ textAlign: "center" }}>
          <Button type="primary" htmlType="submit" loading={loading} style={{ minWidth: 100 }}>
            Lưu
          </Button>
          <Button onClick={onCancel} style={{ marginLeft: 16, minWidth: 100 }}>
            Hủy
          </Button>
        </Form.Item>
      </Form>
    </Modal>
  );
};


export default HealthRecordModal;

