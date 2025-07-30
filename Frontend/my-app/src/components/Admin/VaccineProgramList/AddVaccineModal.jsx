import React from "react";
import { Modal, Form, Input, Button } from "antd";

const AddVaccineModal = ({
  open,
  onCancel,
  onFinish,
  loading,
}) => (
  <Modal
    title="Thêm mới vaccine"
    open={open}
    onCancel={onCancel}
    footer={null}
    destroyOnClose
  >
    <Form
      layout="vertical"
      onFinish={onFinish}
    >
      <Form.Item
        label="Tên vaccine"
        name="vaccineName"
        rules={[{ required: true, message: "Nhập tên vaccine" }]}
      >
        <Input />
      </Form.Item>
      <Form.Item
        label="Nhà sản xuất"
        name="manufacture"
      >
        <Input />
      </Form.Item>
      <Form.Item
        label="URL thông tin vaccine"
        name="url"
        rules={[
          { required: true, message: "Nhập URL thông tin vaccine" },
          { type: "url", message: "URL không hợp lệ!" },
        ]}
      >
        <Input />
      </Form.Item>
      <Form.Item
        label="Ghi chú"
        name="note"
      >
        <Input.TextArea rows={2} />
      </Form.Item>
      <Form.Item>
        <Button
          type="primary"
          htmlType="submit"
          loading={loading}
          style={{ width: "100%" }}
        >
          Thêm mới
        </Button>
      </Form.Item>
    </Form>
  </Modal>
);

export default AddVaccineModal;