import React, { useState, useEffect } from "react";
import { Modal, Input, Button, Table, Form, ConfigProvider, Typography, message, Select } from "antd"; // Import Typography
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
  const [vaccineOptions, setVaccineOptions] = useState([]);

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
      createBy: "0",
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

  useEffect(() => {
    if (open) {
      axios.get('http://localhost:8080/api/parent/get=all-VaccineName', {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      }).then(res => {
        setVaccineOptions(res.data || []);
      });
    }
  }, [open]);

  const handleRemoveVaccine = (idx) => {
    setVaccineHistories(prev => prev.filter((_, i) => i !== idx));
  };

  const vaccineTableColumns = [
    {
      title: (
        <>
          Tên Vaccin
          <Text type="secondary" style={{ marginLeft: 4, fontWeight: 'normal' }}>(nếu có)</Text>
        </>
      ),
      dataIndex: "vaccineNameId",
      render: (text, record, idx) => (
        <Select
          value={text}
          onChange={value => handleVaccineChange(value, idx, "vaccineNameId")}
          placeholder="Chọn vaccin"
          style={{ width: 150 }}
          options={vaccineOptions.map(v => ({ label: v, value: v }))}
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
    {
      title: "Xóa",
      key: "action",
      align: "center",
      render: (_, record, idx) => (
        <Button
          danger
          type="link"
          onClick={() => {
            setVaccineHistories(prev => prev.filter((_, i) => i !== idx));
          }}
        >
          Xóa
        </Button>
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
                  rules={[
                    { required: true, message: 'Vui lòng nhập thị giác' },
                    {
                      validator: (_, value) => {
                        // Chấp nhận định dạng x/10 với x từ 1 đến 10
                        if (!value) return Promise.resolve();
                        const match = value.match(/^([1-9]|10)\/10$/);
                        if (match) return Promise.resolve();
                        return Promise.reject('Thị giác phải có định dạng từ 1/10 đến 10/10');
                      }
                    }
                  ]}
                >
                  <Input
                    placeholder="Vd: 10/10"
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
              <div style={{ display: 'flex', gap: 16, marginBottom: 8, fontWeight: 500, background: '#e3f2fd', borderRadius: 6, padding: '8px 0', color: '#1976d2' }}>
                <div style={{ flex: 2, textAlign: 'left', paddingLeft: 8 }}>Tên Vaccin (nếu có)</div>
                <div style={{ flex: 3, textAlign: 'left', paddingLeft: 8 }}>Mô tả (nếu có)</div>
                <div style={{ width: 60, textAlign: 'center' }}>Xóa</div>
              </div>
              {vaccineHistories.map((item, idx) => (
                <div key={idx} style={{ display: 'flex', gap: 16, marginBottom: 8 }}>
                  <Form.Item
                    name={['vaccineHistories', idx, 'vaccineName']}
                    style={{ flex: 2, marginBottom: 0 }}
                    rules={[
                      { required: true, message: 'Chọn loại vaccin' },
                      {
                        validator: (_, value) => {
                          // Lấy id vaccine đang chọn
                          const currentId = typeof value === 'object' ? value.id : value;
                          // Kiểm tra có trùng với vaccine khác không
                          const duplicate = vaccineHistories.some(
                            (v, i) =>
                              i !== idx &&
                              (typeof v.vaccineName === 'object' ? v.vaccineName.id : v.vaccineName) === currentId
                          );
                          return duplicate
                            ? Promise.reject('Không được chọn trùng loại vaccin!')
                            : Promise.resolve();
                        }
                      }
                    ]}
                  >
                    <Select
                      showSearch
                      placeholder="Chọn vaccin"
                      optionFilterProp="children"
                      value={
                        typeof item.vaccineName === 'object'
                          ? item.vaccineName.id
                          : item.vaccineName // nếu là id
                      }
                      onChange={value => {
                        const selectedVac = vaccineOptions.find(v => v.id === value);
                        handleVaccineChange(selectedVac, idx, 'vaccineName'); // lưu object nếu cần hiển thị
                        handleVaccineChange(selectedVac.id, idx, 'vaccineNameId'); // lưu id để gửi backend
                      }}
                      filterOption={(input, option) =>
                        option.children.toLowerCase().includes(input.toLowerCase())
                      }
                    >
                      {vaccineOptions.map(vac => (
                        <Select.Option key={vac.id} value={vac.id}>
                          {vac.vaccineName}
                        </Select.Option>
                      ))}
                    </Select>
                  </Form.Item>
                  <Form.Item
                    name={['vaccineHistories', idx, 'note']}
                    style={{ flex: 3, marginBottom: 0 }}
                  >
                    <Input
                      placeholder="Mô tả"
                      value={item.note}
                      onChange={e => handleVaccineChange(e.target.value, idx, 'note')}
                    />
                  </Form.Item>
                  {/* Nút xóa */}
                  <Button danger onClick={() => handleRemoveVaccine(idx)}>Xóa</Button>
                </div>
              ))}
              <Button type="primary" style={{ marginTop: 16 }} onClick={handleAddVaccine}>
                + Thêm mới vaccin
              </Button>
              {/* <div style={{ marginTop: 20, color: "#d84315", fontStyle: "italic", fontWeight: 500 }}>
                Quý phụ huynh vui lòng nhập đầy đủ các vaccin mà học sinh đã tiêm vì thông tin vaccin sau khi khai báo sẽ không được chỉnh sửa
              </div> */}
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