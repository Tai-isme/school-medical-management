import React, { useState, useEffect } from "react";
import {
  Modal,
  Input,
  Button,
  Table,
  Form,
  ConfigProvider,
  Typography,
  message,
  Select,
} from "antd"; // Import Typography
import axios from "axios";
import { urlServer } from "../../../api/urlServer";
import {
  UserOutlined,
  SoundOutlined,
  DashboardOutlined,
  ColumnHeightOutlined,
  EyeOutlined,
  ExclamationCircleOutlined,
  WarningOutlined,
  MedicineBoxOutlined,
  FileTextOutlined,
} from "@ant-design/icons";

const { Text } = Typography; // Sử dụng Text để có thể style chữ

// Định nghĩa locale tùy chỉnh
const customLocale = {
  Form: {
    optional: "(nếu có)",
  },
};

export default function MedicalRecordModal({
  open,
  onCancel,
  initialValues,
  loading,
  studentId,
  fetchStudentInfo,
  editMode,
}) {
  const [form] = Form.useForm();
  const [vaccineHistories, setVaccineHistories] = useState(() =>
    (initialValues?.vaccineHistories || []).map((v) => ({ ...v }))
  );
  const [vaccineOptions, setVaccineOptions] = useState([]);

  const handleAddVaccine = () => {
    setVaccineHistories((prev) => [
      ...prev,
      { vaccineName: "", vaccineNameId: "", doseNumber: 1, note: "" },
    ]);
  };
  const handleVaccineChange = (value, index, field) => {
    setVaccineHistories((prevVaccines) => {
      let newList = [...prevVaccines];
      newList[index] = { ...newList[index], [field]: value };

      // Chỉ validate khi người dùng đổi hoặc xóa vaccineNameId
      if (field === "vaccineNameId" && value) {
        const isDuplicate = newList.some(
          (v, idx) => v.vaccineNameId === value && idx !== index && value !== ""
        );
        if (isDuplicate) {
          form.setFields([
            {
              name: ["vaccineHistories", index, "vaccineNameId"],
              errors: ["Không được chọn 2 loại vaccine cùng tên!"],
            },
          ]);
        } else {
          form.setFields([
            {
              name: ["vaccineHistories", index, "vaccineNameId"],
              errors: [],
            },
          ]);
        }
      }
      return newList;
    });
  };

  const handleFinish = async (values) => {
    // Lấy vaccineHistories từ form để đảm bảo lấy đúng doseNumber mới nhất
    const filteredVaccineHistories = (values.vaccineHistories || [])
      .filter((v) => v.vaccineNameId)
      .map((v) => ({
        vaccineNameId: v.vaccineNameId,
        unit: v.doseNumber, // lấy đúng giá trị mới nhất từ form
        note: v.note || "",
        createBy: true,
      }));

    const payload = {
      studentId,
      allergies: values.allergies || "",
      chronicDisease: values.chronicDisease || "",
      vision: values.vision || "",
      hearing: values.hearing || "",
      weight: Number(values.weight) || 0,
      height: Number(values.height) || 0,
      lastUpdate: new Date().toISOString(),
      note: values.note || "",
      vaccineHistories: filteredVaccineHistories,
    };

    try {
      if (editMode) {
        await axios.put(
          `${urlServer}/api/parent/medical-records/${studentId}`,
          payload,
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
        );
        message.success(`Cập nhật hồ sơ cho học sinh ${studentId} thành công`);
      } else {
        await axios.post(`${urlServer}/api/parent/medical-records`, payload, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        });
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
      // Đảm bảo mỗi vaccineHistory có vaccineNameId là id của vaccine
      const currentVaccines = (initialValues?.vaccineHistories || []).map(
        (v, idx) => ({
          ...v,
          vaccineNameId:
            typeof v.vaccineName === "object"
              ? v.vaccineName.id
              : v.vaccineNameId || v.vaccineName,
          vaccineName: v.vaccineName, // giữ lại object nếu cần
          key: v.key || `initial-${idx}-${Date.now()}`,
        })
      );
      setVaccineHistories(currentVaccines);
      // Set initialValues cho Form, bao gồm vaccineHistories đã có vaccineNameId
      form.setFieldsValue({
        ...initialValues,
        vaccineHistories: currentVaccines,
      });
    }
  }, [initialValues, open, form]);

  // Khi nhận response từ API, lưu vào vaccineOptions
  useEffect(() => {
    if (open) {
      axios
        .get(`${urlServer}/api/parent/get=all-VaccineName`, {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        })
        .then((res) => {
          setVaccineOptions(res.data || []);
        });
    }
  }, [open]);

  const handleRemoveVaccine = (idx) => {
    setVaccineHistories((prev) => {
      const newList = prev.filter((_, i) => i !== idx);
      // Sau khi xóa, kiểm tra lại các dòng còn lại xem có trùng không và clear lỗi nếu cần
      newList.forEach((item, i) => {
        const isDuplicate = newList.some(
          (v, j) =>
            v.vaccineNameId === item.vaccineNameId &&
            i !== j &&
            item.vaccineNameId !== ""
        );
        form.setFields([
          {
            name: ["vaccineHistories", i, "vaccineNameId"],
            errors: isDuplicate
              ? ["Không được chọn 2 loại vaccine cùng tên!"]
              : [],
          },
        ]);
      });
      return newList;
    });
  };

  const vaccineTableColumns = [
    {
      title: (
        <>
          Tên Vaccin
          <Text
            type="secondary"
            style={{ marginLeft: 4, fontWeight: "normal" }}
          >
            (nếu có)
          </Text>
        </>
      ),
      dataIndex: "vaccineNameId",
      render: (text, record, idx) => (
        <Select
          value={text}
          onChange={(value) => handleVaccineChange(value, idx, "vaccineNameId")}
          placeholder="Chọn vaccine"
          style={{ width: 150 }}
          options={vaccineOptions.map((v) => ({ label: v, value: v }))}
        />
      ),
    },
    {
      title: (
        <>
          Mô tả
          <Text
            type="secondary"
            style={{ marginLeft: 4, fontWeight: "normal" }}
          >
            (nếu có)
          </Text>
        </>
      ),
      dataIndex: "note",
      render: (text, record, idx) => (
        <Input.TextArea
          value={text}
          onChange={(e) => handleVaccineChange(e.target.value, idx, "note")}
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
            setVaccineHistories((prev) => prev.filter((_, i) => i !== idx));
          }}
        >
          Xóa
        </Button>
      ),
    },
  ];
  const visionOptions = Array.from({ length: 10 }, (_, i) => ({
    label: `${i + 1}/10`,
    value: `${i + 1}/10`,
  }));

  return (
    <Modal
      open={open}
      onCancel={onCancel}
      footer={null}
      width={1500}
      style={{ top: 20 }}
      destroyOnClose
      bodyStyle={{
        maxHeight: "calc(100vh - 120px)",
        overflowY: "auto",
        padding: 0,
        background: "transparent",
      }}
      title={
        <div
          style={{
            textAlign: "center",
            fontWeight: "bold",
            fontSize: 28,
            padding: "16px 24px",
          }}
        >
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
          <div
            style={{ display: "flex", gap: 24, padding: "0 24px 24px 24px" }}
          >
            {/* Thông tin học sinh */}
            <div
              style={{
                flex: 1,
                background: "#f6fbff",
                borderRadius: 8,
                padding: 24,
              }}
            >
              <div
                style={{
                  textAlign: "center",
                  fontWeight: "bold",
                  fontSize: 20,
                  marginBottom: 16,
                }}
              >
                Thông tin học sinh
              </div>
              <div style={{ display: "flex", gap: 16 }}>
                <Form.Item
                  label={
                    <span>
                      <EyeOutlined style={{ marginRight: 8 }} />
                      Thị giác{" "}
                      <ExclamationCircleOutlined
                        style={{ color: "#faad14", marginLeft: 6 }}
                      />
                    </span>
                  }
                  name="vision"
                  style={{ flex: 1 }}
                  rules={[
                    { required: true, message: "Vui lòng chọn thị giác" },
                  ]}
                >
                  <Select
                    placeholder="🔥 Đây là dropdown thị giác"
                    options={visionOptions}
                    showSearch
                    optionFilterProp="label"
                  />
                </Form.Item>

                <Form.Item
                  label={
                    <span>
                      👂Thính lực <span style={{ color: "red" }}>*</span>
                    </span>
                  }
                  name="hearing"
                  style={{ flex: 1 }}
                  validateTrigger="onBlur"
                  rules={[
                    {
                      required: true,
                      message: "Vui lòng nhập thông tin thính lực",
                    },
                  ]}
                >
                  <Input
                    placeholder="Vd: Bình thường"
                    onFocus={() =>
                      form.setFields([{ name: "hearing", errors: [] }])
                    }
                  />
                </Form.Item>
              </div>
              <div style={{ display: "flex", gap: 16 }}>
                <Form.Item
                  label={
                    <span>
                      <DashboardOutlined style={{ marginRight: 8 }} />
                      Cân nặng <span style={{ color: "red" }}>*</span>
                    </span>
                  }
                  name="weight"
                  style={{ flex: 1 }}
                  validateTrigger="onBlur"
                  rules={[
                    { required: true, message: "Vui lòng nhập cân nặng" },
                    {
                      validator: (_, value) =>
                        value && Number(value) > 0
                          ? Promise.resolve()
                          : Promise.reject("Cân nặng phải là số lớn hơn 0"),
                    },
                  ]}
                >
                  <Input
                    addonAfter="kg"
                    placeholder="50"
                    onFocus={() =>
                      form.setFields([{ name: "weight", errors: [] }])
                    }
                  />
                </Form.Item>

                <Form.Item
                  label={
                    <span>
                      <ColumnHeightOutlined style={{ marginRight: 8 }} />
                      Chiều cao <span style={{ color: "red" }}>*</span>
                    </span>
                  }
                  name="height"
                  style={{ flex: 1 }}
                  validateTrigger="onBlur"
                  rules={[
                    { required: true, message: "Vui lòng nhập chiều cao" },
                    {
                      validator: (_, value) =>
                        value && Number(value) > 0
                          ? Promise.resolve()
                          : Promise.reject("Chiều cao phải là số lớn hơn 0"),
                    },
                  ]}
                >
                  <Input
                    addonAfter="cm"
                    placeholder="120"
                    onFocus={() =>
                      form.setFields([{ name: "height", errors: [] }])
                    }
                  />
                </Form.Item>
              </div>
              <Form.Item
                label={
                  <span>
                    <WarningOutlined style={{ marginRight: 8 }} />
                    Bị dị ứng với các loại nào
                  </span>
                }
                name="allergies"
              >
                <Input.TextArea rows={2} placeholder="Hải sản, Tôm,..." />
              </Form.Item>

              <Form.Item
                label={
                  <span>
                    <MedicineBoxOutlined style={{ marginRight: 8 }} />
                    Bệnh mãn tính
                  </span>
                }
                name="chronicDisease"
              >
                <Input.TextArea rows={2} placeholder="hen suyễn,..." />
              </Form.Item>

              <Form.Item
                label={
                  <span>
                    <FileTextOutlined style={{ marginRight: 8 }} />
                    Ghi chú
                  </span>
                }
                name="note"
              >
                <Input.TextArea rows={2} placeholder="ghi chú thêm" />
              </Form.Item>
            </div>

            {/* Bảng vaccin */}
            <div
              style={{
                flex: 1,
                background: "#f6fbff",
                borderRadius: 8,
                padding: 24,
              }}
            >
              <div
                style={{
                  textAlign: "center",
                  fontWeight: "bold",
                  fontSize: 20,
                  marginBottom: 16,
                }}
              >
                Các loại vaccin đã tiêm
              </div>
              <div
                style={{
                  display: "flex",
                  gap: 16,
                  marginBottom: 8,
                  fontWeight: 500,
                  background: "#e3f2fd",
                  borderRadius: 6,
                  padding: "8px 0",
                  color: "#1976d2",
                }}
              >
                <div style={{ flex: 2, textAlign: "left", paddingLeft: 8 }}>
                  Tên Vaccin (nếu có)
                </div>
                <div style={{ width: 100, textAlign: "left", paddingLeft: 8 }}>
                  Mũi thứ
                </div>
                <div style={{ flex: 3, textAlign: "left", paddingLeft: 8 }}>
                  Mô tả (nếu có)
                </div>
                <div style={{ width: 60, textAlign: "center" }}>Xóa</div>
              </div>
              {vaccineHistories.map((item, idx) => {
                // Tìm vaccine đã chọn để lấy số mũi tiêm từ vaccineUnitDTOs
                const selectedVaccine = vaccineOptions.find(
                  (v) => v.id === item.vaccineNameId
                );
                const unitCount = selectedVaccine?.vaccineUnitDTOs?.length || 1;
                const isReadOnly = editMode && item.createBy;
                return (
                  <div
                    key={idx}
                    style={{ display: "flex", gap: 16, marginBottom: 8 }}
                  >
                    {/* Tên vaccine */}
                    <Form.Item
                      name={["vaccineHistories", idx, "vaccineNameId"]}
                      style={{ flex: 2, marginBottom: 0 }}
                      rules={[
                        { required: true, message: "Chọn loại vaccine" },
                        {
                          validator: (_, value) => {
                            if (!value) return Promise.resolve();
                            // Lấy toàn bộ vaccineNameId hiện tại
                            const allIds = form
                              .getFieldValue("vaccineHistories")
                              ?.map((v) => v.vaccineNameId);
                            // Nếu có nhiều hơn 1 dòng có cùng id, báo lỗi
                            if (
                              allIds &&
                              allIds.filter((id) => id === value).length > 1
                            ) {
                              return Promise.reject(
                                "Không được chọn 2 loại vaccine cùng tên!"
                              );
                            }
                            return Promise.resolve();
                          },
                        },
                      ]}
                    >
                      <Select
                        showSearch
                        placeholder="Chọn vaccin"
                        optionFilterProp="children"
                        value={item.vaccineNameId}
                        onChange={(value) => {
                          if (editMode && item.createBy) return; // Không cho đổi nếu createBy
                          const selectedVac = vaccineOptions.find(
                            (v) => v.id === value
                          );
                          handleVaccineChange(
                            selectedVac.vaccineName,
                            idx,
                            "vaccineName"
                          );
                          handleVaccineChange(
                            selectedVac.id,
                            idx,
                            "vaccineNameId"
                          );
                        }}
                        disabled={editMode && item.createBy}
                        filterOption={(input, option) =>
                          option.children
                            .toLowerCase()
                            .includes(input.toLowerCase())
                        }
                      >
                        {vaccineOptions.map((vac) => (
                          <Select.Option key={vac.id} value={vac.id}>
                            {vac.vaccineName}
                          </Select.Option>
                        ))}
                      </Select>
                    </Form.Item>
                    {/* Mũi thứ */}
                    <Form.Item
                      name={["vaccineHistories", idx, "doseNumber"]}
                      style={{ width: 100, marginBottom: 0 }}
                      rules={[{ required: true, message: "Chọn mũi tiêm" }]}
                      initialValue={item.doseNumber}
                    >
                      <Select
                        placeholder="Mũi thứ"
                        value={item.doseNumber}
                        onChange={(value) => {
                          if (editMode && item.createBy) return;
                          handleVaccineChange(value, idx, "doseNumber");
                        }}
                        // disabled={editMode && item.createBy}
                      >
                        {Array.from({ length: unitCount }, (_, i) => i + 1)
                          .filter(
                            (num) => !item.createBy || num >= item.doseNumber
                          )
                          .map((num) => (
                            <Select.Option key={num} value={num}>
                              Mũi {num}
                            </Select.Option>
                          ))}
                      </Select>
                    </Form.Item>
                    {/* Mô tả */}
                    <Form.Item
                      name={["vaccineHistories", idx, "note"]}
                      style={{
                        flex: 3,
                        marginBottom: 0,
                        minWidth: 200,
                        maxWidth: 350,
                      }}
                    >
                      <Input.TextArea
                        placeholder="Mô tả"
                        value={item.note}
                        onChange={(e) => {
                          if (editMode && item.createBy) return;
                          handleVaccineChange(e.target.value, idx, "note");
                        }}
                        autoSize={{ minRows: 1, maxRows: 3 }}
                        style={{ width: "100%" }}
                        // disabled={editMode && item.createBy}
                      />
                    </Form.Item>
                    {/* Xóa */}
                    <div style={{ width: 60, textAlign: "center" }}>
                      {!(editMode && item.createBy) && (
                        <Button danger onClick={() => handleRemoveVaccine(idx)}>
                          Xóa
                        </Button>
                      )}
                    </div>
                  </div>
                );
              })}
              <Button
                type="primary"
                style={{ marginTop: 16 }}
                onClick={handleAddVaccine}
              >
                + Thêm mới vaccin
              </Button>
              {/* <div style={{ marginTop: 20, color: "#d84315", fontStyle: "italic", fontWeight: 500 }}>
                Quý phụ huynh vui lòng nhập đầy đủ các vaccin mà học sinh đã tiêm vì thông tin vaccin sau khi khai báo sẽ không được chỉnh sửa
              </div> */}
              <div
                style={{
                  display: "flex",
                  justifyContent: "center",
                  marginTop: 40,
                }}
              >
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
