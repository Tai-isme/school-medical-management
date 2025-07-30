import React, { useState } from "react";
import { Modal, Form, Input, Button } from "antd";
import Swal from "sweetalert2";
import axios from "axios";

const AddVaccineModal = ({
  open,
  onCancel,
  addVaccineForm,
  fetchVaccineList,
}) => {
  const [addVaccineLoading, setAddVaccineLoading] = useState(false);

  const handleFinish = async (values) => {
    setAddVaccineLoading(true);
    const token = localStorage.getItem("token");
    try {
      await axios.post(
        "http://localhost:8080/api/admin/create-VaccineName",
        {
          vaccineName: values.vaccineName,
          manufacture: values.manufacture,
          url: values.url,
          note: values.note,
        },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      Swal.fire({
        icon: "success",
        title: "Thêm vaccine mới thành công!",
        showConfirmButton: false,
        timer: 1500,
      });
      onCancel();
      addVaccineForm.resetFields();
      fetchVaccineList();
    } catch {
      Swal.fire({
        icon: "error",
        title: "Thêm vaccine mới thất bại!",
      });
    } finally {
      setAddVaccineLoading(false);
    }
  };

  return (
    <Modal
      title="Thêm mới vaccine"
      open={open}
      onCancel={onCancel}
      footer={null}
      destroyOnClose
    >
      <Form
        form={addVaccineForm}
        layout="vertical"
        onFinish={handleFinish}
      >
        <Form.Item
          label="Tên vaccine"
          name="vaccineName"
          rules={[{ required: true, message: "Nhập tên vaccine" }]}
        >
          <Input />
        </Form.Item>
        <Form.Item label="Nhà sản xuất" name="manufacture">
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
        <Form.Item label="Ghi chú" name="note">
          <Input.TextArea rows={2} />
        </Form.Item>
        <Form.Item>
          <Button
            type="primary"
            htmlType="submit"
            loading={addVaccineLoading}
            style={{ width: "100%" }}
          >
            Thêm mới
          </Button>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default AddVaccineModal;