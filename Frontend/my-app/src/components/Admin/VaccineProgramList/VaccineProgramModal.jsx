import React from "react";
import { Modal, Form, Input, Select, DatePicker, Button } from "antd";
import dayjs from "dayjs";
import AddVaccineModal from "./AddVaccineModal";

const VaccineProgramModal = ({
  open,
  onCancel,
  onFinish,
  loading,
  editMode,
  program,
  vaccineList,
  addVaccineVisible,
  setAddVaccineVisible,
  handleAddVaccine,
  addVaccineLoading,
}) => (
  <Modal
    title={editMode ? "Sửa chương trình tiêm chủng" : "Lên lịch tiêm chủng"}
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
              vaccineNameId: program.vaccineName?.id || program.vaccineNameId,
              manufacture: program.manufacture,
              description: program.description,
              vaccineDate: dayjs(program.vaccineDate),
              note: program.note,
            }
          : {}
      }
    >
      <Form.Item
        label="Tên vaccine"
        name="vaccineNameId"
        rules={[{ required: true, message: "Chọn vaccine" }]}
      >
        <Select
          showSearch
          placeholder="Chọn vaccine"
          optionFilterProp="children"
          filterOption={(input, option) =>
            option.children.toLowerCase().includes(input.toLowerCase())
          }
        >
          {vaccineList.map((v) => (
            <Select.Option key={v.id} value={v.id}>
              {v.vaccineName}
            </Select.Option>
          ))}
        </Select>
      </Form.Item>
      <Form.Item label="Mô tả" name="description">
        <Input.TextArea rows={2} />
      </Form.Item>
      <Form.Item
        label="Ngày tiêm"
        name="vaccineDate"
        rules={[
          { required: true, message: "Chọn ngày tiêm" },
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
    <AddVaccineModal
      open={addVaccineVisible}
      onCancel={() => setAddVaccineVisible(false)}
      onFinish={handleAddVaccine}
      loading={addVaccineLoading}
    />
  </Modal>
);

export default VaccineProgramModal;