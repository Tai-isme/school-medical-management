import React, { useEffect, useState } from "react";
import { Modal, Form, Input, Select, DatePicker, Button } from "antd";
import dayjs from "dayjs";
import axios from "axios";

const vaccineOptions = [
  { value: 1, label: "Viêm gan B" },
  { value: 2, label: "Viêm gan A" },
  { value: 3, label: "Sởi" },
];

const doseOptions = [
  { value: 1, label: "Mũi 1" },
  { value: 2, label: "Mũi 2" },
  { value: 3, label: "Mũi 3" },
  { value: 4, label: "Mũi 4" },
  { value: 5, label: "Mũi 5" },
];

// Giả lập dữ liệu lớp
const classOptions = [
  { value: "1A", label: "Lớp 1A" },
  { value: "1B", label: "Lớp 1B" },
  { value: "2A", label: "Lớp 2A" },
  { value: "2B", label: "Lớp 2B" },
  { value: "3A", label: "Lớp 3A" },
  { value: "3B", label: "Lớp 3B" },
  { value: "4A", label: "Lớp 4A" },
  { value: "4B", label: "Lớp 4B" },
  { value: "5A", label: "Lớp 5A" },
  { value: "5B", label: "Lớp 5B" },
];

const VaccineProgramModal = ({
  open,
  onCancel,
  onFinish,
  loading,
  initialValues = {},
}) => {
  const [form] = Form.useForm();
  const [selectedClasses, setSelectedClasses] = useState([]);
  const [nurseOptions, setNurseOptions] = useState([]);

  // Fetch nurse list from API
  useEffect(() => {
    const fetchNurses = async () => {
      try {
        const token = localStorage.getItem("token");
        const res = await axios.get("http://localhost:8080/api/nurse/nurse-list", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setNurseOptions(
          (res.data || []).map(nurse => ({
            value: nurse.id,
            label: nurse.fullName + (nurse.phone ? ` (${nurse.phone})` : ""),
          }))
        );
      } catch (err) {
        setNurseOptions([]);
      }
    };
    fetchNurses();
  }, []);

  // Đảm bảo đồng bộ khi mở modal chỉnh sửa
  React.useEffect(() => {
    setSelectedClasses(initialValues.classes || []);
  }, [initialValues, open]);

  const handleClassToggle = (value) => {
    setSelectedClasses((prev) =>
      prev.includes(value)
        ? prev.filter((v) => v !== value)
        : [...prev, value]
    );
    form.setFieldsValue({
      classes: selectedClasses.includes(value)
        ? selectedClasses.filter((v) => v !== value)
        : [...selectedClasses, value],
    });
  };

  return (
    <Modal
      title="Lên lịch tiêm chủng"
      open={open}
      onCancel={onCancel}
      footer={null}
      destroyOnClose
      width={600}
    >
      <Form
        layout="vertical"
        form={form}
        onFinish={onFinish}
        initialValues={{
          ...(initialValues || {}),
          startDate: initialValues?.startDate ? dayjs(initialValues.startDate) : null,
          sendFormDate: initialValues?.sendFormDate ? dayjs(initialValues.sendFormDate) : null,
          classes: initialValues?.classes || [],
        }}
      >
        <Form.Item
          label="Tên chương trình"
          name="programName"
          rules={[{ required: true, message: "Nhập tên chương trình" }]}
        >
          <Input placeholder="Nhập tên chương trình tiêm chủng" />
        </Form.Item>

        {/* Loại vaccine và Mũi vaccine trên cùng 1 hàng */}
        <div style={{ display: "flex", gap: 16 }}>
          <Form.Item
            label="Loại vaccine"
            name="vaccineType"
            rules={[{ required: true, message: "Chọn loại vaccine" }]}
            style={{ flex: 1, marginBottom: 0 }}
          >
            <Select
              placeholder="Chọn loại vaccine"
              options={vaccineOptions}
              showSearch
              optionFilterProp="label"
            />
          </Form.Item>
          <Form.Item
            label="Mũi vaccine"
            rules={[{ required: true, message: "Chọn mũi vaccine" }]}
            style={{ flex: 1, marginBottom: 0 }}
          >
            <Select
              placeholder="Chọn mũi tiêm"
              options={doseOptions}
            />
          </Form.Item>
        </div>

        {/* Thời gian thực hiện và gửi form */}
        <div style={{ display: "flex", gap: 16 }}>
          <Form.Item
            label="Ngày thực hiện"
            name="startDate"
            rules={[
              { required: true, message: "Chọn ngày thực hiện" },
              {
                validator: (_, value) => {
                  if (!value) return Promise.resolve();
                  if (value.isBefore(dayjs(), "day")) {
                    return Promise.reject("Chỉ được chọn ngày trong tương lai!");
                  }
                  return Promise.resolve();
                },
              },
            ]}
            style={{ flex: 1, marginBottom: 0 }}
          >
            <DatePicker
              style={{ width: "100%" }}
              format="YYYY-MM-DD"
              disabledDate={current => current && current < dayjs().startOf("day")}
            />
          </Form.Item>
          <Form.Item
            label="Ngày gửi form cho học sinh"
            name="sendFormDate"
            rules={[
              { required: true, message: "Chọn ngày gửi form" },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  const vaccineDate = getFieldValue("vaccineDate");
                  if (!value || !vaccineDate) return Promise.resolve();
                  if (value.isAfter(vaccineDate, "day")) {
                    return Promise.reject("Ngày gửi form phải trước hoặc bằng ngày thực hiện!");
                  }
                  return Promise.resolve();
                },
              }),
            ]}
            style={{ flex: 1, marginBottom: 0 }}
          >
            <DatePicker
              style={{ width: "100%" }}
              format="YYYY-MM-DD"
              disabledDate={current => current && current < dayjs().startOf("day")}
            />
          </Form.Item>
        </div>

        {/* Chọn lớp dạng button group đẹp */}
        <Form.Item
          label={<span><span role="img" aria-label="class">🏫</span> Chọn lớp</span>}
          name="classes"
          rules={[{ required: true, message: "Chọn ít nhất một lớp" }]}
        >
          <Select
            mode="multiple"
            placeholder="Chọn lớp"
            options={classOptions}
            showSearch
            optionFilterProp="label"
            style={{ width: "100%" }}
          />
        </Form.Item>

        <Form.Item
          label="Y tá quản lý"
          name="nurse"
          rules={[{ required: true, message: "Chọn y tá quản lý" }]}
        >
          <Select
            placeholder="Chọn y tá"
            options={nurseOptions}
            showSearch
            optionFilterProp="label"
          />
        </Form.Item>

        <Form.Item
          label="Địa điểm diễn ra"
          name="location"
          rules={[{ required: true, message: "Nhập địa điểm diễn ra" }]}
        >
          <Input placeholder="Nhập địa điểm diễn ra" />
        </Form.Item>

        <Form.Item
          label="Mô tả"
          name="description"
          rules={[{ required: false }]}
        >
          <Input.TextArea rows={3} placeholder="Nhập mô tả về chương trình tiêm chủng" />
        </Form.Item>

        <Form.Item>
          <Button
            type="primary"
            htmlType="submit"
            loading={loading}
            style={{ width: "100%" }}
          >
            Tạo chương trình
          </Button>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default VaccineProgramModal;