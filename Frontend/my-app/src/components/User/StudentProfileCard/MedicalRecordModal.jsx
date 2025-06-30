import React, { useState, useEffect } from "react";
import { Modal, Input, Button, Table, Form, ConfigProvider, Typography, message } from "antd"; // Import Typography
import axios from "axios";

const { Text } = Typography; // Sử dụng Text để có thể style chữ

// Định nghĩa locale tùy chỉnh
const customLocale = {
  Form: {
    optional: '(nếu có)',
  },
};

export default function MedicalRecordModal({ open, onCancel, initialValues, loading, studentId, fetchStudentInfo, editMode }) {
  const [form] = Form.useForm();
  const [vaccineHistories, setVaccineHistories] = useState(
    () => (initialValues?.vaccineHistories || []).map(v => ({ ...v }))
  );

  const handleAddVaccine = () => {
    setVaccineHistories(prev => [...prev, { vaccineName: "", note: "", key: Date.now() }]);
  };
  const handleVaccineChange = (value, index, field) => {
    setVaccineHistories(prevVaccines => {
      const newList = [...prevVaccines];
      newList[index] = { ...newList[index], [field]: value };
      return newList;
    });
  };

  const handleFinish = async (values) => {
    const payload = {
      studentId,
      allergies: values.allergies || "",
      chronicDisease: values.chronicDisease || "",
      treatmentHistory: values.treatmentHistory || "",
      vision: values.vision || "",
      hearing: values.hearing || "",
      weight: Number(values.weight) || 0,
      height: Number(values.height) || 0,
      note: values.note || "",
      vaccineHistories: vaccineHistories.length > 0 ? vaccineHistories : [],
    };

    try {
      if (editMode) {
        // Gọi PUT để cập nhật
        await axios.put(
          `http://localhost:8080/api/parent/medical-records/${studentId}`,
          payload,
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
        );
        message.success(`Cập nhật hồ sơ cho học sinh ${studentId} thành công`);
      } else {
        // Gọi POST để tạo mới
        await axios.post(
          "http://localhost:8080/api/parent/medical-records",
          payload,
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
        );
        message.success(`Khai báo hồ sơ cho học sinh ${studentId} thành công`);
      }
      localStorage.setItem("studentIdAlready", studentId);
      fetchStudentInfo(studentId);
      onCancel();
    } catch (error) {
      message.error("Có lỗi xảy ra, vui lòng thử lại!");
    }
  };

  useEffect(() => {
    if (open) {
      const currentVaccines = (initialValues?.vaccineHistories || []).map((v, idx) => ({ 
        ...v, 
        key: v.key || `initial-${idx}-${Date.now()}` 
      }));
      setVaccineHistories(currentVaccines);
      form.setFieldsValue(initialValues || {});
    }
  }, [initialValues, open, form]);

  const vaccineTableColumns = [
    {
      title: (
        <>
          Tên Vaccin
          <Text type="secondary" style={{ marginLeft: 4, fontWeight: 'normal' }}>(nếu có)</Text>
        </>
      ),
      dataIndex: "vaccineName",
      render: (text, record, idx) => (
        <Input
          value={text}
          onChange={e => handleVaccineChange(e.target.value, idx, "vaccineName")}
          placeholder="Bạch hầu, Ho gà, Uốn ván,..."
          style={{ width: 150 }}
        />
      ),
    },
    {
      title: (
        <>
          Mô tả
          <Text type="secondary" style={{ marginLeft: 4, fontWeight: 'normal' }}>(nếu có)</Text>
        </>
      ),
      dataIndex: "note",
      render: (text, record, idx) => (
        <Input.TextArea
          value={text}
          onChange={e => handleVaccineChange(e.target.value, idx, "note")}
          placeholder="Đã tiêm ở VNVC"
          autoSize={{ minRows: 1, maxRows: 4 }}
          style={{ width: 350 }}
        />
      ),
    },
  ];

  return (
    <Modal
      open={open}
      onCancel={onCancel}
      footer={null}
      width={1500}
      style={{ top: 20 }}
      destroyOnClose
      bodyStyle={{
        maxHeight: 'calc(100vh - 120px)',
        overflowY: "auto",
        padding: 0,
        background: "transparent"
      }}
      title={
        <div style={{ textAlign: "center", fontWeight: "bold", fontSize: 28, padding: '16px 24px' }}>
          Khai báo hồ sơ sức khỏe cho học sinh
        </div>
      }
    >
      <ConfigProvider locale={customLocale}>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleFinish}
          initialValues={initialValues}
          requiredMark={false}
          validateTrigger="onBlur"
        >
          <div style={{ display: "flex", gap: 24, padding: '0 24px 24px 24px' }}>
            {/* Thông tin học sinh */}
            <div style={{ flex: 1, background: "#f6fbff", borderRadius: 8, padding: 24 }}>
              <div style={{ textAlign: "center", fontWeight: "bold", fontSize: 20, marginBottom: 16 }}>
                Thông tin học sinh
              </div>
              <div style={{ display: "flex", gap: 16 }}>
                <Form.Item
                  label={
                    <span>
                      Thị giác <span style={{ color: "red" }}>*</span>
                    </span>
                  }
                  name="vision"
                  style={{ flex: 1 }}
                  validateTrigger="onBlur"
                  rules={[{ required: true, message: 'Vui lòng nhập thị giác' }]}
                >
                  <Input
                    placeholder="Vd: 10/10, Cận 3 độ, Viễn 2 độ,..."
                    onFocus={() => form.setFields([{ name: 'vision', errors: [] }])}
                  />
                </Form.Item>

                <Form.Item
                  label={
                    <span>
                      Thính lực <span style={{ color: "red" }}>*</span>
                    </span>
                  }
                  name="hearing"
                  style={{ flex: 1 }}
                  validateTrigger="onBlur"
                  rules={[{ required: true, message: 'Vui lòng nhập thính lực' }]}
                >
                  <Input
                    placeholder="Vd: Bình thường"
                    onFocus={() => form.setFields([{ name: 'hearing', errors: [] }])}
                  />
                </Form.Item>
              </div>
              <div style={{ display: "flex", gap: 16 }}>
                <Form.Item
                  label={
                    <span>
                      Cân nặng <span style={{ color: "red" }}>*</span>
                    </span>
                  }
                  name="weight"
                  style={{ flex: 1 }}
                  validateTrigger="onBlur"
                  rules={[
                    { required: true, message: 'Vui lòng nhập cân nặng' },
                    {
                      validator: (_, value) =>
                        value && Number(value) > 0
                          ? Promise.resolve()
                          : Promise.reject('Cân nặng phải lớn hơn 0'),
                    },
                  ]}
                >
                  <Input
                    addonAfter="kg"
                    placeholder="50"
                    onFocus={() => form.setFields([{ name: 'weight', errors: [] }])}
                  />
                </Form.Item>
                <Form.Item
                  label={
                    <span>
                      Chiều cao <span style={{ color: "red" }}>*</span>
                    </span>
                  }
                  name="height"
                  style={{ flex: 1 }}
                  validateTrigger="onBlur"
                  rules={[
                    { required: true, message: 'Vui lòng nhập chiều cao' },
                    {
                      validator: (_, value) =>
                        value && Number(value) > 0
                          ? Promise.resolve()
                          : Promise.reject('Chiều cao phải lớn hơn 0'),
                    },
                  ]}
                >
                  <Input
                    addonAfter="cm"
                    placeholder="120"
                    onFocus={() => form.setFields([{ name: 'height', errors: [] }])}
                  />
                </Form.Item>
              </div>
              <Form.Item label="Bị dị ứng với các loại nào" name="allergies">
                <Input.TextArea rows={2} placeholder="Hải sản, Tôm,..." />
              </Form.Item>
              <Form.Item label="Bệnh mãn tính" name="chronicDisease">
                <Input.TextArea rows={2} placeholder="hen suyễn,..." />
              </Form.Item>
              <Form.Item label="Lịch sử điều trị" name="treatmentHistory">
                <Input.TextArea rows={2} placeholder="Vd: từng điều trị ở BV Đa khoa Sài Gòn,..." />
              </Form.Item>
              <Form.Item label="Ghi chú" name="note">
                <Input.TextArea rows={2} placeholder="ghi chú thêm" />
              </Form.Item>
            </div>

            {/* Bảng vaccin */}
            <div style={{ flex: 1, background: "#f6fbff", borderRadius: 8, padding: 24 }}>
              <div style={{ textAlign: "center", fontWeight: "bold", fontSize: 20, marginBottom: 16 }}>
                Các loại vaccin đã tiêm
              </div>
              <Table
                dataSource={vaccineHistories}
                pagination={false}
                rowKey={(record) => record.key || record.vaccineName + record.note}
                columns={vaccineTableColumns}
                style={{ width: 660 }}
              />
              <Button type="primary" style={{ marginTop: 16 }} onClick={handleAddVaccine}>
                + Thêm mới vaccin
              </Button>
              <div style={{ marginTop: 20, color: "#d84315", fontStyle: "italic", fontWeight: 500 }}>
                Quý phụ huynh vui lòng nhập đầy đủ các vaccin mà học sinh đã tiêm vì thông tin vaccin sau khi khai báo sẽ không được chỉnh sửa
              </div>
              <div style={{ display: "flex", justifyContent: "center", marginTop: 40 }}>
                <Button
                  type="primary"
                  style={{
                    background: "#4caf50",
                    borderColor: "#4caf50",
                    color: "#fff",
                    fontWeight: "bold",
                    minWidth: 120,
                  }}
                  htmlType="submit"
                  loading={loading}
                >
                  {editMode ? "Lưu thay đổi" : "Gửi hồ sơ"}
                </Button>
              </div>
            </div>
          </div>
        </Form>
      </ConfigProvider>
    </Modal>
  );
}