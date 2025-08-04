import React, { useEffect, useState } from "react";
import { Modal, Form, Input, Select, DatePicker, Button } from "antd";
import dayjs from "dayjs";
import axios from "axios";

const VaccineProgramModal = ({
  open,
  onCancel,
  onFinish,
  loading,
  initialValues = {},
  editMode = false,
  viewMode = false,
  vaccineList = [],
  program,
}) => {
  const [form] = Form.useForm();
  const [selectedClasses, setSelectedClasses] = useState([]);
  const [nurseOptions, setNurseOptions] = useState([]);
  const [selectedVaccineId, setSelectedVaccineId] = useState(null);
  const [classOptions, setClassOptions] = useState([]);

  // Fetch nurse list from API
  useEffect(() => {
    const fetchNurses = async () => {
      try {
        const token = localStorage.getItem("token");
        const res = await axios.get(
          "http://localhost:8080/api/nurse/nurse-list",
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        setNurseOptions(
          (res.data || []).map((nurse) => ({
            value: nurse.id,
            label: `🧑‍⚕️ ${nurse.fullName} • 📞 ${nurse.phone || "Không có số"} • ✉️ ${nurse.email || "Không có email"}`,
            node: (
              <div>
                <div style={{ fontWeight: 500 }}>🧑‍⚕️ {nurse.fullName}</div>
                <div style={{ fontSize: 12, color: "#888" }}>
                  📞 {nurse.phone || "Không có số"} | ✉️{" "}
                  {nurse.email || "Không có email"}
                </div>
              </div>
            ),
          }))
        );
      } catch (err) {
        setNurseOptions([]);
      }
    };
    fetchNurses();
  }, []);

  // Fetch class list from API
  useEffect(() => {
    const fetchClasses = async () => {
      try {
        const token = localStorage.getItem("token");
        const res = await axios.get(
          "http://localhost:8080/api/nurse/class-list",
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        // Map to { value, label }
        setClassOptions(
          (res.data || []).map((cls) => ({
            value: cls.classId,
            label: cls.className,
          }))
        );
      } catch (err) {
        setClassOptions([]);
      }
    };
    fetchClasses();
  }, []);

  // Đảm bảo đồng bộ khi mở modal chỉnh sửa
  React.useEffect(() => {
    setSelectedClasses(initialValues.classIds || []);
  }, [initialValues, open]);

  useEffect(() => {
    if (open) {
      if (Object.keys(initialValues).length === 0) {
        form.resetFields(); // Reset form khi tạo mới
      } else {
        form.setFieldsValue({
          ...initialValues,
          startDate: initialValues?.startDate
            ? dayjs(initialValues.startDate)
            : null,
          sendFormDate: initialValues?.sendFormDate
            ? dayjs(initialValues.sendFormDate)
            : null,
          classIds: initialValues?.classIds || [],
          unit: initialValues?.unit || 1,
          nurseId: initialValues?.nurseId,
        });
      }
    }
  }, [initialValues, open, form]);

  const handleClassToggle = (value) => {
    setSelectedClasses((prev) =>
      prev.includes(value) ? prev.filter((v) => v !== value) : [...prev, value]
    );
    form.setFieldsValue({
      classIds: selectedClasses.includes(value)
        ? selectedClasses.filter((v) => v !== value)
        : [...selectedClasses, value],
    });
  };

  const vaccineOptions = vaccineList.map((vac) => ({
    value: vac.id, // hoặc vac.vaccineNameId nếu API trả về
    label: vac.vaccineName,
  }));

  const doseOptions = React.useMemo(() => {
    const vaccine = vaccineList.find((v) => v.id === selectedVaccineId);
    if (!vaccine) return [];
    return Array.from({ length: vaccine.totalUnit }, (_, i) => ({
      value: i + 1,
      label: `Mũi ${i + 1}`,
    }));
  }, [selectedVaccineId, vaccineList]);

  return (
    <Modal
      title="Lên lịch tiêm chủng"
      open={open}
      onCancel={onCancel}
      footer={null}
      destroyOnClose
      width={600}
    >
      <Form layout="vertical" form={form} onFinish={onFinish}>
        <Form.Item
          label="Tên chương trình"
          name="vaccineProgramName" // Đổi tên trường này
          rules={[{ required: true, message: "Nhập tên chương trình" }]}
        >
          <Input
            placeholder="Nhập tên chương trình tiêm chủng"
            disabled={viewMode}
          />
        </Form.Item>

        {/* Loại vaccine và Mũi vaccine trên cùng 1 hàng */}
        <div style={{ display: "flex", gap: 16 }}>
          <Form.Item
            label="Loại vaccine"
            name="vaccineNameId"
            rules={[{ required: true, message: "Chọn loại vaccine" }]}
            style={{ flex: 1, marginBottom: 0 }}
          >
            <Select
              placeholder="Chọn loại vaccine"
              options={vaccineOptions}
              showSearch
              optionFilterProp="label"
              onChange={(value) => {
                setSelectedVaccineId(value);
                // Tự động chọn mũi đầu tiên khi chọn vaccine
                const vaccine = vaccineList.find((v) => v.id === value);
                if (vaccine) {
                  form.setFieldsValue({ unit: 1 });
                }
              }}
              disabled={viewMode}
            />
          </Form.Item>
          <Form.Item
            label="Mũi vaccine"
            name="unit"
            rules={[{ required: true, message: "Chọn mũi vaccine" }]}
            style={{ flex: 1, marginBottom: 0 }}
          >
            <Select
              placeholder="Chọn mũi tiêm"
              options={doseOptions}
              disabled={viewMode || !selectedVaccineId}
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
                    return Promise.reject(
                      "Chỉ được chọn ngày trong tương lai!"
                    );
                  }
                  return Promise.resolve();
                },
              },
            ]}
            style={{ flex: 1, marginBottom: 0 }}
          >
            <DatePicker
              style={{ width: "100%" }}
              format="DD/MM/YYYY"
              disabledDate={(current) =>
                current && current < dayjs().startOf("day")
              }
            />
          </Form.Item>
          <Form.Item
            label="Ngày gửi form cho học sinh"
            name="sendFormDate"
            dependencies={["startDate"]}
            rules={[
              { required: true, message: "Chọn ngày gửi form" },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  const startDate = getFieldValue("startDate")?.format("YYYY-MM-DD");
                  if (!value || !startDate) return Promise.resolve();
                  if (!value.isBefore(startDate, "day")) {
                    return Promise.reject(
                      "Ngày gửi form phải nhỏ hơn ngày thực hiện!"
                    );
                  }
                  return Promise.resolve();
                },
              }),
            ]}
            validateTrigger={["onChange", "onBlur"]}
            style={{ flex: 1, marginBottom: 0 }}
          >
            <DatePicker
              style={{ width: "100%" }}
              format="DD/MM/YYYY"
              disabledDate={(current) =>
                current && current < dayjs().startOf("day")
              }
            />
          </Form.Item>
        </div>

        {/* Chọn lớp dạng button group đẹp */}
        <Form.Item
          label={
            <span>
              <span role="img" aria-label="class">
                🏫
              </span>{" "}
              Chọn lớp
            </span>
          }
          name="classIds"
          rules={[{ required: true, message: "Chọn ít nhất một lớp" }]}
          style={{ width: "100%" }}
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
          name="nurseId" // Sửa lại thành nurseId
          rules={[{ required: true, message: "Chọn y tá quản lý" }]}
        >
          <Select
            placeholder="Chọn y tá"
            showSearch
            optionFilterProp="label"
            optionLabelProp="label"
            filterOption={(input, option) =>
              option.label?.toLowerCase().includes(input.toLowerCase())
            }
          >
            {nurseOptions.map((nurse) => (
              <Select.Option key={nurse.value} value={nurse.value} label={nurse.label}>
                {nurse.node}
              </Select.Option>
            ))}
          </Select>
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
          <Input.TextArea
            rows={3}
            placeholder="Nhập mô tả về chương trình tiêm chủng"
          />
        </Form.Item>

        {/* Hiển thị phác đồ tiêm */}
        {(() => {
          const vaccine = vaccineList.find((v) => v.id === selectedVaccineId);
          const unit = form.getFieldValue("unit");
          if (vaccine && vaccine.vaccineUnitDTOs?.length) {
            return (
              <div
                style={{
                  margin: "8px 0 16px 0",
                  padding: "12px",
                  background: "#f6f6f6",
                  borderRadius: 6,
                  border: "1px solid #e4e4e4",
                }}
              >
                <b>Phác đồ tiêm:</b>
                <div>
                  <b>Vaccine:</b> {vaccine.vaccineName}
                </div>
                <ul style={{ margin: 0, paddingLeft: 20 }}>
                  {vaccine.vaccineUnitDTOs.map((u) => (
                    <li key={u.unit} style={{ marginBottom: 2 }}>
                      <span
                        style={
                          u.unit === unit
                            ? { fontWeight: "bold", color: "#1976d2" }
                            : {}
                        }
                      >
                        Mũi {u.unit}: {u.schedule}
                      </span>
                    </li>
                  ))}
                </ul>
                <div style={{ marginTop: 4, color: "#888" }}>
                  {vaccine.description}
                </div>
              </div>
            );
          }
          return null;
        })()}

        <Form.Item>
          {!viewMode && (
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              style={{ width: "100%" }}
            >
              {editMode ? "Cập nhật chương trình" : "Tạo chương trình"}
            </Button>
          )}
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default VaccineProgramModal;
