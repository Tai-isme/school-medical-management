import React from "react";
import { Modal, Form, Input, DatePicker, Button } from "antd";
import dayjs from "dayjs";

const HealthCheckProgramModal = ({
  open,
  onCancel,
  onFinish,
  loading,
  editMode,
  program,
}) => (
  <Modal
    title={editMode ? "Sửa chương trình khám định kỳ" : "Lên lịch khám định kỳ"}
    open={open}
    onCancel={onCancel}
    footer={null}
    destroyOnClose
  >
    <Form
      layout="vertical"
      onFinish={onFinish}
      initialValues={
        editMode && program
          ? {
              name: program.name,
              description: program.description,
              startDate: dayjs(program.startDate),
              endDate: dayjs(program.endDate),
              note: program.note,
            }
          : {}
      }
    >
      <Form.Item
        label="Tên chương trình"
        name="name"
        rules={[{ required: true, message: "Nhập tên chương trình" }]}
      >
        <Input />
      </Form.Item>
      <Form.Item label="Mô tả" name="description">
        <Input.TextArea rows={2} />
      </Form.Item>
      <Form.Item
        label="Ngày bắt đầu"
        name="startDate"
        rules={[
          { required: true, message: "Chọn ngày bắt đầu" },
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
      >
        <DatePicker
          style={{ width: "100%" }}
          format="YYYY-MM-DD"
          disabledDate={(current) =>
            current && current < dayjs().startOf("day")
          }
        />
      </Form.Item>
      <Form.Item
        label="Ngày kết thúc"
        name="endDate"
        rules={[
          { required: true, message: "Chọn ngày kết thúc" },
          ({ getFieldValue }) => ({
            validator(_, value) {
              const start = getFieldValue("startDate");
              if (!value || !start) return Promise.resolve();
              if (value.isBefore(start, "day")) {
                return Promise.reject("Ngày kết thúc phải sau ngày bắt đầu!");
              }
              return Promise.resolve();
            },
          }),
        ]}
      >
        <DatePicker
          style={{ width: "100%" }}
          format="YYYY-MM-DD"
          disabledDate={(current) =>
            current && current < dayjs().startOf("day")
          }
        />
      </Form.Item>
      <Form.Item label="Ghi chú" name="note">
        <Input.TextArea rows={2} />
      </Form.Item>
      <Form.Item>
        <Button
          type="primary"
          htmlType="submit"
          loading={loading}
          style={{ width: "100%" }}
        >
          {editMode ? "Cập nhật" : "Tạo chương trình"}
        </Button>
      </Form.Item>
    </Form>
  </Modal>
);

export default HealthCheckProgramModal;