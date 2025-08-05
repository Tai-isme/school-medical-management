import React, { useEffect, useState } from "react";
import { Modal, Form, Input, Select, DatePicker, Button } from "antd";
import dayjs from "dayjs";
import axios from "axios";
import { urlServer } from "../../../api/urlServer";
import { ExclamationCircleOutlined } from "@ant-design/icons";

const VaccineProgramModal = ({
  open,
  onCancel,
  onFinish,
  loading,
  initialValues = {},
  editMode ,
  viewMode ,
  vaccineList = [],
  program,
}) => {
  const [form] = Form.useForm();
  const [selectedClasses, setSelectedClasses] = useState([]);
  const [nurseOptions, setNurseOptions] = useState([]);
  const [selectedVaccineId, setSelectedVaccineId] = useState(null);
  const [classOptions, setClassOptions] = useState([]);
  const [selectedUnit, setSelectedUnit] = useState(1);

  console.log("Action:" +  viewMode);

  // Fetch nurse list from API
  useEffect(() => {
    const fetchNurses = async () => {
      try {
        const token = localStorage.getItem("token");
        const res = await axios.get(`${urlServer}/api/nurse/nurse-list`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setNurseOptions(
          (res.data || []).map((nurse) => ({
            value: nurse.id,
            label: `🧑‍⚕️ ${nurse.fullName} • 📞 ${
              nurse.phone || "Không có số"
            } • ✉️ ${nurse.email || "Không có email"}`,
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
        const res = await axios.get(`${urlServer}/api/nurse/class-list`, {
          headers: { Authorization: `Bearer ${token}` },
        });
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

  // Khi mở modal hoặc thay đổi initialValues, đồng bộ selectedUnit
  useEffect(() => {
    if (open) {
      setSelectedUnit(initialValues?.unit || 1);
    }
  }, [open, initialValues]);

  return (
    <Modal
  title={
    <div style={{ textAlign: "center" }}>
      {viewMode ? "Thông tin tiêm chủng" : "Lên lịch tiêm chủng"}
    </div>
  }
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
        requiredMark={false}
      >
        <Form.Item
          label={
            <span>
              ❗ Tên chương trình{" "}
              <ExclamationCircleOutlined
                style={{ color: "red", fontSize: 12, marginLeft: 4 }}
              />
            </span>
          }
          name="vaccineProgramName"
          rules={[
            { required: true, message: "Vui lòng nhập tên chương trình" },
          ]}
        >
          <Input
            placeholder="Nhập tên chương trình tiêm chủng"
            disabled={viewMode}
          />
        </Form.Item>

        {/* Loại vaccine và Mũi vaccine trên cùng 1 hàng */}
        <div style={{ display: "flex", gap: 16 }}>
          <Form.Item
            label={
              <span>
                💉 Loại vaccine{" "}
                <ExclamationCircleOutlined
                  style={{ color: "red", fontSize: 12, marginLeft: 4 }}
                />
              </span>
            }
            name="vaccineNameId"
            rules={[{ required: true, message: "Vui lòng chọn loại vaccine" }]}
            style={{ flex: 1, marginBottom: 0 }}
          >
            <Select
              placeholder="Chọn loại vaccine"
              options={vaccineOptions}
              showSearch
              optionFilterProp="label"
              onChange={(value) => {
                setSelectedVaccineId(value);
                const vaccine = vaccineList.find((v) => v.id === value);
                if (vaccine) {
                  form.setFieldsValue({ unit: 1 });
                }
              }}
              disabled={viewMode}
            />
          </Form.Item>

          <Form.Item
            label={
              <span>
                🧪 Mũi vaccine{" "}
                <ExclamationCircleOutlined
                  style={{ color: "red", fontSize: 12, marginLeft: 4 }}
                />
              </span>
            }
            name="unit"
            rules={[{ required: true, message: "Vui lòng chọn mũi vaccine" }]}
            style={{ flex: 1, marginBottom: 0 }}
          >
            <Select
              placeholder="Chọn mũi tiêm"
              options={doseOptions}
              disabled={viewMode || !selectedVaccineId}
              value={selectedUnit}
              onChange={(value) => {
                setSelectedUnit(value); // cập nhật state để re-render
                form.setFieldsValue({ unit: value });
              }}
            />
          </Form.Item>
        </div>

        {/* ĐEM PHẦN PHÁC ĐỒ TIÊM LÊN ĐÂY */}
        {(() => {
          const vaccine = vaccineList.find((v) => v.id === selectedVaccineId);
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
                          u.unit === selectedUnit
                            ? { fontWeight: "bold", color: "#1976d2" }
                            : {}
                        }
                      >
                        Mũi {u.unit}: {u.schedule}
                      </span>
                    </li>
                  ))}
                </ul>
                <b>Độ tuổi phù hợp:</b>{" "}
<span style={{ color: "#23b616ff", fontWeight: 600 }}>
  {vaccine.ageFrom + " - " + vaccine.ageTo} tuổi
</span>
                <div style={{ marginTop: 4, color: "#888" }}>
                  {vaccine.description}
                </div>
              </div>
            );
          }
          return null;
        })()}

        {/* Thời gian thực hiện và gửi form */}
        <div style={{ display: "flex", gap: 16, marginBottom: 16 , marginTop: 16 }}>
          <Form.Item
            label={
              <span>
                📅 Ngày thực hiện{" "}
                <ExclamationCircleOutlined
                  style={{ color: "red", fontSize: 12, marginLeft: 4 }}
                />
              </span>
            }
            name="startDate"
            rules={[
              { required: true, message: "Vui lòng chọn ngày thực hiện" },
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
          
            label={
              <span>
                📬 Ngày gửi thông báo cho học sinh{" "}
                <ExclamationCircleOutlined
                  style={{ color: "red", fontSize: 12, marginLeft: 4 }}
                />
              </span>
            }
            name="sendFormDate"
            dependencies={["startDate"]}
            rules={[
              { required: true, message: "Vui lòng chọn ngày gửi thông báo" },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  const startDate =
                    getFieldValue("startDate")?.format("YYYY-MM-DD");
                  if (!value || !startDate) return Promise.resolve();
                  if (!value.isBefore(startDate, "day")) {
                    return Promise.reject(
                      "Ngày gửi thông báo phải nhỏ hơn ngày thực hiện!"
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
          label={<span>🏫 Chọn lớp</span>}
          name="classIds"
          rules={[{ required: true, message: "Vui lòng chọn ít nhất một lớp" }]}
          style={{ width: "100%" }}
        >
          <Select
            mode="multiple"
            placeholder="Chọn lớp"
            options={classOptions}
            showSearch
            optionFilterProp="label"
            style={{ width: "100%" }}
            disabled={viewMode} // Thêm dòng này
          />
        </Form.Item>

        <Form.Item
          label={
            <span>
              🧑‍⚕️ Y tá quản lý{" "}
              <ExclamationCircleOutlined
                style={{ color: "red", fontSize: 12, marginLeft: 4 }}
              />
            </span>
          }
          name="nurseId"
          rules={[
            {
              required: true,
              message: "Vui lòng chọn y tá quản lý chương trình này",
            },
          ]}
        >
          <Select
            placeholder="Chọn y tá"
            showSearch
            optionFilterProp="label"
            optionLabelProp="label"
            filterOption={(input, option) =>
              option.label?.toLowerCase().includes(input.toLowerCase())
            }
            disabled={viewMode} // Thêm dòng này
          >
            {nurseOptions.map((nurse) => (
              <Select.Option
                key={nurse.value}
                value={nurse.value}
                label={nurse.label}
              >
                {nurse.node}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label={
            <span>
              📍 Địa điểm diễn ra{" "}
              <ExclamationCircleOutlined
                style={{ color: "red", fontSize: 12, marginLeft: 4 }}
              />
            </span>
          }
          name="location"
          rules={[
            {
              required: true,
              message: "Vui lòng nhập địa điểm diễn ra chương trình",
            },
          ]}
        >
          <Input placeholder="Nhập địa điểm diễn ra" disabled={viewMode} />
        </Form.Item>

        <Form.Item
          label={
            <span>
              📝 Mô tả chương trình{" "}
              <ExclamationCircleOutlined
                style={{ color: "red", fontSize: 12, marginLeft: 4 }}
              />
            </span>
          }
          name="description"
          rules={[
            { required: true, message: "Vui lòng điền mô tả của chương trình" },
          ]}
        >
          <Input.TextArea
            rows={3}
            placeholder="Nhập mô tả về chương trình tiêm chủng"
            disabled={viewMode} // Thêm dòng này
          />
        </Form.Item>

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
