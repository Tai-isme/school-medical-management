import React from "react";
import { Modal, Form, Input, DatePicker, Button, Select, Row, Col } from "antd";
import dayjs from "dayjs";

const HealthCheckProgramModal = ({
  open,
  onCancel,
  onFinish,
  loading,
  editMode,
  program,
  nurseOptions = [],
  classOptions = [],
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
              healthCheckName: program.healthCheckName,
        description: program.description,
        startDate: program.startDate ? dayjs(program.startDate) : null,
        dateSendForm: program.dateSendForm ? dayjs(program.dateSendForm) : null,
        location: program.location,
        nurseId: program.nurseId, // phải là nurseId, không phải nurseID
        classIds: program.classIds,
            }
          : {}
      }
    >
      <Form.Item
        label="Tên chương trình"
        name="healthCheckName"
        rules={[{ required: true, message: "Nhập tên chương trình" }]}
      >
        <Input />
      </Form.Item>
      <Row gutter={16}>
        <Col span={12}>
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
        </Col>
        <Col span={12}>
          <Form.Item
            label="Ngày gửi form"
            name="dateSendForm"
            rules={[{ required: true, message: "Chọn ngày gửi form" }]}
          >
            <DatePicker
              style={{ width: "100%" }}
              format="YYYY-MM-DD"
              disabledDate={(current) =>
                current && current < dayjs().startOf("day")
              }
            />
          </Form.Item>
        </Col>
      </Row>
      <Form.Item
        label="Chọn lớp"
        name="classIds"
        rules={[{ required: true, message: "Chọn ít nhất một lớp" }]}
      >
        <Select
          mode="multiple"
          options={classOptions}
          placeholder="Chọn lớp"
          showSearch
          optionFilterProp="label"
        />
      </Form.Item>
      <Form.Item
        label="Y tá phụ trách"
        name="nurseId"
        rules={[{ required: true, message: "Chọn y tá phụ trách" }]}
      >
        <Select
          options={nurseOptions}
          placeholder="Chọn y tá"
          showSearch
          optionFilterProp="label"
        />
      </Form.Item>
      <Form.Item
        label="Địa điểm"
        name="location"
        rules={[{ required: true, message: "Nhập địa điểm" }]}
      >
        <Input />
      </Form.Item>
      <Form.Item label="Mô tả" name="description">
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