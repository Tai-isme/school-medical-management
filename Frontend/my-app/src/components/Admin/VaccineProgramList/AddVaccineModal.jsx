import React, { useEffect, useState } from "react";
import { Modal, Form, Input, Button } from "antd";
import Swal from "sweetalert2";
import axios from "axios";

const AddVaccineModal = ({
  open,
  onCancel,
  mode = "add", // "add" | "edit" | "view"
  initialValues = {},
  fetchVaccineList,
}) => {
  const [form] = Form.useForm();
  const [addVaccineLoading, setAddVaccineLoading] = useState(false);
  const [modalMode, setModalMode] = useState("create"); // "create" | "edit" | "view"

  useEffect(() => {
    if (open) form.setFieldsValue(initialValues);
  }, [open, initialValues]);

  const handleFinish = async (values) => {
    setAddVaccineLoading(true);
    const token = localStorage.getItem("token");
    try {
      if (mode === "add") {
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
      } else if (mode === "edit") {
        await axios.put(
          `http://localhost:8080/api/admin/update-VaccineName/${initialValues._id}`,
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
          title: "Cập nhật vaccine thành công!",
          showConfirmButton: false,
          timer: 1500,
        });
      }
      onCancel();
      form.resetFields();
      fetchVaccineList();
    } catch {
      Swal.fire({
        icon: "error",
        title:
          mode === "add"
            ? "Thêm vaccine mới thất bại!"
            : "Cập nhật vaccine thất bại!",
      });
    } finally {
      setAddVaccineLoading(false);
    }
  };

  return (
    <Modal
      open={open}
      onCancel={onCancel}
      title={
        mode === "edit"
          ? "Chỉnh sửa chương trình tiêm chủng"
          : mode === "view"
          ? "Xem chi tiết chương trình tiêm chủng"
          : "Thêm mới vaccine"
      }
      footer={
        mode === "view"
          ? null
          : [
              <Button key="cancel" onClick={onCancel}>
                Đóng
              </Button>,
              <Button
                key="submit"
                type="primary"
                htmlType="submit"
                onClick={() => form.submit()}
                loading={addVaccineLoading}
                disabled={mode === "view"}
              >
                {mode === "edit" ? "Cập nhật" : "Thêm mới"}
              </Button>,
            ]
      }
    >
      <Form
        form={form}
        layout="vertical"
        initialValues={initialValues}
        disabled={mode === "view"}
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
      </Form>
    </Modal>
  );
};

export default AddVaccineModal;