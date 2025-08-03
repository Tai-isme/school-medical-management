"use client";

import { useEffect, useState } from "react";
import { Modal, Form, Input, Button, Space } from "antd";
import Swal from "sweetalert2";
import axios from "axios";
import {
  PlusCircleOutlined,
  EditOutlined,
  EyeOutlined,
  LoadingOutlined,
} from "@ant-design/icons";

const AddVaccineModal = ({
  open,
  onCancel,
  mode = "add", // "add" | "edit" | "view"
  initialValues = {},
  fetchVaccineList,
}) => {
  const [form] = Form.useForm();
  const [addVaccineLoading, setAddVaccineLoading] = useState(false);

  useEffect(() => {
    if (open) {
      form.setFieldsValue(initialValues);
      // Reset validation on open
      form.validateFields();
    } else {
      form.resetFields(); // Reset fields when modal closes
    }
  }, [open, initialValues, form]);

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
            adminId: 1, // hoặc từ localStorage
            vaccineUnits: values.vaccineUnits, // thêm dòng này
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
    } catch (error) {
      console.error("API Error:", error);
      Swal.fire({
        icon: "error",
        title:
          mode === "add"
            ? "Thêm vaccine mới thất bại!"
            : "Cập nhật vaccine thất bại!",
        text: error.response?.data?.message || "Vui lòng thử lại sau.", // Hiển thị thông báo lỗi từ server nếu có
      });
    } finally {
      setAddVaccineLoading(false);
    }
  };

  const getModalTitle = () => {
    switch (mode) {
      case "edit":
        return (
          <Space>
            <EditOutlined style={{ color: "#faad14" }} /> Chỉnh sửa chương trình
            tiêm chủng
          </Space>
        );
      case "view":
        return (
          <Space>
            <EyeOutlined style={{ color: "#1890ff" }} /> Xem chi tiết chương
            trình tiêm chủng
          </Space>
        );
      case "add":
      default:
        return (
          <Space>
            <PlusCircleOutlined style={{ color: "#52c41a" }} /> Thêm mới vaccine
          </Space>
        );
    }
  };

  return (
    <Modal
      open={open}
      onCancel={onCancel}
      title={getModalTitle()}
      footer={
        mode === "view" ? (
          <Button key="cancel" onClick={onCancel}>
            Đóng
          </Button>
        ) : (
          [
            <Button
              key="cancel"
              onClick={onCancel}
              disabled={addVaccineLoading}
            >
              Đóng
            </Button>,
            <Button
              key="submit"
              type="primary"
              htmlType="submit"
              onClick={() => form.submit()}
              loading={addVaccineLoading}
              disabled={mode === "view"}
              style={{
                backgroundColor: "#52c41a",
                borderColor: "#52c41a",
                boxShadow: "0 2px 0 rgba(0, 0, 0, 0.043)",
              }}
            >
              {addVaccineLoading ? (
                <LoadingOutlined />
              ) : mode === "edit" ? (
                "Cập nhật"
              ) : (
                "Thêm mới"
              )}
            </Button>,
          ]
        )
      }
      width={600} // Tăng chiều rộng modal một chút
      destroyOnClose // Đảm bảo form reset khi đóng
    >
      <Form
        form={form}
        layout="vertical"
        initialValues={initialValues}
        disabled={mode === "view"}
        onFinish={handleFinish}
        style={{ marginTop: 24 }} // Thêm khoảng cách với tiêu đề
      >
        <Form.Item
          label="Tên vaccine"
          name="vaccineName"
          rules={[{ required: true, message: "Vui lòng nhập tên vaccine!" }]}
        >
          <Input placeholder="Ví dụ: Vaccine COVID-19" />
        </Form.Item>
        <Form.Item label="Nhà sản xuất" name="manufacture">
          <Input placeholder="Ví dụ: Pfizer, Moderna" />
        </Form.Item>
        <Form.Item
          label="URL thông tin vaccine"
          name="url"
          rules={[
            { required: true, message: "Vui lòng nhập URL thông tin vaccine!" },
            { type: "url", message: "URL không hợp lệ!" },
          ]}
        >
          <Input placeholder="Ví dụ: https://example.com/vaccine-info" />
        </Form.Item>
        <Form.List name="vaccineUnits">
          {(fields, { add, remove }) => (
            <>
              {fields.map(({ key, name, ...restField }) => (
                <Space
                  key={key}
                  style={{ display: "flex", marginBottom: 8 }}
                  align="baseline"
                >
                  <Form.Item
                    {...restField}
                    name={[name, "unit"]}
                    rules={[{ required: true, message: "Nhập số mũi!" }]}
                  >
                    <Input placeholder="Mũi số" type="number" />
                  </Form.Item>
                  <Form.Item
                    {...restField}
                    name={[name, "schedule"]}
                    rules={[{ required: true, message: "Nhập lịch tiêm!" }]}
                  >
                    <Input placeholder="Lịch tiêm (ví dụ: Sau 3 tháng)" />
                  </Form.Item>
                  <Button type="link" danger onClick={() => remove(name)}>
                    Xóa
                  </Button>
                </Space>
              ))}
              <Form.Item>
                <Button
                  type="dashed"
                  onClick={() => add()}
                  icon={<PlusCircleOutlined />}
                >
                  Thêm mũi tiêm
                </Button>
              </Form.Item>
            </>
          )}
        </Form.List>
        <Form.Item label="Ghi chú" name="note">
          <Input.TextArea
            rows={3}
            placeholder="Ghi chú thêm về vaccine (nếu có)"
          />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default AddVaccineModal;
