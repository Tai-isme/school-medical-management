import React, { useEffect, useState } from "react";
import axios from "axios";
import {
  Card,
  Modal,
  message,
  Pagination,
  Input,
  Select,
  Row,
  Col,
  Button,
  Form,
  DatePicker,
} from "antd";
import dayjs from "dayjs";
import { Upload } from "antd";
import { UploadOutlined } from "@ant-design/icons";

const { Search } = Input;
const { Option } = Select;
const { TextArea } = Input;

const MedicalEventList = () => {
  const [data, setData] = useState([]);
  const [filteredData, setFilteredData] = useState([]);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [createModalVisible, setCreateModalVisible] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [searchText, setSearchText] = useState("");
  const [selectedClass, setSelectedClass] = useState("");
  const [selectedLevel, setSelectedLevel] = useState("");

  const [classOptions, setClassOptions] = useState([]);
  const [studentOptions, setStudentOptions] = useState([]);

  const [uploadedImage, setUploadedImage] = useState(null);
  const [previewVisible, setPreviewVisible] = useState(false);
  const [previewImage, setPreviewImage] = useState("");
  

  const [editingId, setEditingId] = useState(null);

  useEffect(() => {
    if (createModalVisible) {
      fetchClassList();
    }
  }, [createModalVisible]);

  const fetchClassList = async () => {
    try {
      const token = localStorage.getItem("token");
      const res = await axios.get(
        "http://localhost:8080/api/nurse/class-list",
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setClassOptions(res.data);
    } catch (err) {
      message.error("Không thể tải danh sách lớp");
    }
  };

  const handleClassChange = async (classId) => {
    form.setFieldsValue({ studentId: undefined }); // reset student select
    try {
      const token = localStorage.getItem("token");
      const res = await axios.get(
        `http://localhost:8080/api/admin/students/${classId}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setStudentOptions(res.data);
    } catch (err) {
      message.error("Không thể tải danh sách học sinh");
    }
  };

  const [form] = Form.useForm();

  const pageSize = 3;

  useEffect(() => {
    fetchMedicalEvents();
  }, []);

  const fetchMedicalEvents = async () => {
    try {
      const token = localStorage.getItem("token");
      const res = await axios.get(
        "http://localhost:8080/api/nurse/medical-event",
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      const apiData = res.data.map((item) => ({
        ...item,
        studentName: item.studentDTO?.fullName || `ID ${item.studentId}`,
        className: item.classDTO?.className || "Không rõ",
        date: item.date,
        nurseName: item.nurseDTO?.fullName || `ID ${item.nurseId}`,
      }));

      setData(apiData);
      setFilteredData(apiData);
    } catch (error) {
      console.error("Lỗi tải sự kiện y tế:", error);
      message.error("Không thể tải dữ liệu sự kiện y tế!");
    }
  };

  useEffect(() => {
    let tempData = [...data];

    if (searchText.trim() !== "") {
      tempData = tempData.filter(
        (item) =>
          item.eventName?.toLowerCase().includes(searchText.toLowerCase()) ||
          item.studentName?.toLowerCase().includes(searchText.toLowerCase())
      );
    }

    if (selectedClass) {
      tempData = tempData.filter((item) => item.className === selectedClass);
    }

    if (selectedLevel) {
      tempData = tempData.filter((item) => item.levelCheck === selectedLevel);
    }

    setFilteredData(tempData);
    setCurrentPage(1);
  }, [searchText, selectedClass, selectedLevel, data]);

  const getUniqueClassNames = () => [
    ...new Set(data.map((item) => item.className)),
  ];

  const paginatedData = filteredData.slice(
    (currentPage - 1) * pageSize,
    currentPage * pageSize
  );

  const getLevelStyle = (level) => {
    const baseStyle = {
      position: "absolute",
      top: 16,
      right: 16,
      padding: "4px 10px",
      borderRadius: 20,
      color: "#fff",
      fontWeight: "bold",
      fontSize: 12,
      textTransform: "uppercase",
    };

    switch (level) {
      case "LOW":
        return { ...baseStyle, backgroundColor: "#52c41a" };
      case "MEDIUM":
        return { ...baseStyle, backgroundColor: "#faad14" };
      case "HIGH":
        return { ...baseStyle, backgroundColor: "#ff4d4f" };
      default:
        return { ...baseStyle, backgroundColor: "#d9d9d9" };
    }
  };

  const handleCreateEvent = async (values) => {
    try {
      const token = localStorage.getItem("token");
      const payload = {
        ...values,
        date: values.date.toISOString(),
        image: uploadedImage,
      };

      if (editingId) {
        // Gọi API PUT để sửa
        await axios.put(
          `http://localhost:8080/api/nurse/medical-event/${editingId}`,
          payload,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        message.success("Cập nhật sự kiện thành công!");
      } else {
        // Gọi API POST để tạo mới
        await axios.post(
          "http://localhost:8080/api/nurse/medical-event",
          payload,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        message.success("Tạo sự kiện thành công!");
      }

      form.resetFields();
      setUploadedImage(null);
      setEditingId(null);
      setCreateModalVisible(false);
      fetchMedicalEvents();
    } catch (error) {
      console.error("Lỗi tạo/cập nhật sự kiện:", error);
      message.error("Không thể lưu sự kiện.");
    }
  };

  const handleEditClick = async (event) => {
    try {
      const token = localStorage.getItem("token");
      const res = await axios.get(
        `http://localhost:8080/api/nurse/medical-event/${event.eventId}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      const detail = res.data;

      // Fetch lại học sinh theo lớp
      await handleClassChange(event.classId);

      form.setFieldsValue({
        ...detail,
        date: dayjs(detail.date),
        classId: event.classId,
      });

      setUploadedImage(detail.image || null);
      setEditingId(event.eventId);
      setCreateModalVisible(true);
      setDetailModalVisible(false);
    } catch (err) {
      message.error("Không thể tải dữ liệu để chỉnh sửa");
    }
  };

  const handleDeleteEvent = async (medicalEventId) => {
    console.log("👉 eventId cần xoá:", medicalEventId);
    Modal.confirm({
      title: "Bạn có chắc muốn xoá sự kiện này?",
      okText: "Xoá",
      okType: "danger",
      cancelText: "Huỷ",
      onOk: async () => {
        try {
          const token = localStorage.getItem("token");
          await axios.delete(
            `http://localhost:8080/api/nurse/medical-event/${medicalEventId}`,
            {
              headers: { Authorization: `Bearer ${token}` },
            }
          );
          message.success("Xoá sự kiện thành công!");
          fetchMedicalEvents(); // refresh danh sách
        } catch (err) {
          console.error("Lỗi xoá sự kiện:", err);
          message.error("Không thể xoá sự kiện.");
        }
      },
    });
  };

  return (
    <div
      style={{
        marginLeft: "240px",
        flex: 1,
        padding: "24px",
        background: "#fafbfc",
        minHeight: "100vh",
        borderRadius: "10px",
        boxSizing: "border-box",
        boxShadow: "0 4px 12px rgba(0, 0, 0, 0.04)",
        fontFamily: "'Segoe UI', Tahoma, sans-serif",
      }}
    >
      <h2
        style={{
          fontSize: "24px",
          color: "#1476d1",
          marginBottom: "24px",
          fontWeight: 600,
        }}
      >
        Danh sách sự kiện y tế
      </h2>

      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={6}>
          <Search
            placeholder="Tìm theo tên sự kiện hoặc học sinh"
            onChange={(e) => setSearchText(e.target.value)}
            allowClear
          />
        </Col>
        <Col span={6}>
          <Select
            placeholder="Lọc theo lớp"
            style={{ width: "100%" }}
            onChange={(value) => setSelectedClass(value)}
            allowClear
          >
            {getUniqueClassNames().map((cls) => (
              <Option key={cls} value={cls}>
                {cls}
              </Option>
            ))}
          </Select>
        </Col>
        <Col span={6}>
          <Select
            placeholder="Lọc theo mức độ"
            style={{ width: "100%" }}
            onChange={(value) => setSelectedLevel(value)}
            allowClear
          >
            <Option value="LOW">LOW</Option>
            <Option value="MEDIUM">MEDIUM</Option>
            <Option value="HIGH">HIGH</Option>
          </Select>
        </Col>
        <Col span={6}>
          <Button type="primary" onClick={() => setCreateModalVisible(true)}>
            Tạo sự cố
          </Button>
        </Col>
      </Row>

      {filteredData.length === 0 ? (
        <p>Không có dữ liệu phù hợp.</p>
      ) : (
        <>
          {paginatedData.map((item) => (
            <Card
              key={item.eventId}
              style={{
                marginBottom: 16,
                borderRadius: 10,
                boxShadow: "0 2px 8px rgba(0,0,0,0.05)",
                cursor: "pointer",
                transition: "box-shadow 0.2s",
                position: "relative",
              }}
              onClick={() => {
                setSelectedEvent(item);
                setDetailModalVisible(true);
              }}
            >
              <div style={getLevelStyle(item.levelCheck)}>
                {item.levelCheck}
              </div>
              <h3
                style={{
                  color: "#ff4d4f",
                  textTransform: "uppercase",
                  fontWeight: "bold",
                }}
              >
                {item.eventName}
              </h3>
              <p>
                <strong>Học sinh:</strong> {item.studentName}
              </p>
              <p>
                <strong>Lớp:</strong> {item.className}
              </p>
              <p>
                <strong>Ngày:</strong> {dayjs(item.date).format("DD/MM/YYYY")}
              </p>

              {/* Nút hành động */}
              <div style={{ display: "flex", gap: 8, marginTop: 12 }}>
                <Button
                  type="link"
                  onClick={() => {
                    setSelectedEvent(item);
                    setDetailModalVisible(true);
                  }}
                >
                  Xem chi tiết
                </Button>
                <Button
                  type="link"
                  danger
                  onClick={(e) => {
                    e.stopPropagation();
                    handleDeleteEvent(item.eventId);
                  }}
                >
                  Xoá
                </Button>
              </div>
            </Card>
          ))}

          <Pagination
            current={currentPage}
            pageSize={pageSize}
            total={filteredData.length}
            onChange={(page) => setCurrentPage(page)}
            style={{ marginTop: 24, textAlign: "center" }}
          />
        </>
      )}

      {/* Modal Chi tiết */}
      <Modal
        title={
          <span style={{ color: "#1476d1", fontSize: 20, fontWeight: 600 }}>
            Chi tiết sự kiện y tế
          </span>
        }
        open={detailModalVisible}
        onCancel={() => setDetailModalVisible(false)}
        footer={[
          <Button key="close" onClick={() => setDetailModalVisible(false)}>
            Đóng
          </Button>,
          <Button
            key="edit"
            type="primary"
            onClick={() => handleEditClick(selectedEvent)}
          >
            Sửa
          </Button>,
        ]}
        bodyStyle={{
          position: "relative",
          padding: 24,
          paddingTop: 40,
          backgroundColor: "#fefefe",
          borderRadius: 8,
        }}
      >
        {selectedEvent && (
          <div>
            <div style={getLevelStyle(selectedEvent.levelCheck)}>
              {selectedEvent.levelCheck}
            </div>
            <div
              style={{
                display: "grid",
                gridTemplateColumns: "1fr 1fr",
                gap: "12px 24px",
                lineHeight: "1.8em",
              }}
            >
              <div>
                <strong>Tên sự kiện:</strong>
                <div>{selectedEvent.eventName}</div>
              </div>
              <div>
                <strong>Loại sự kiện:</strong>
                <div>{selectedEvent.typeEvent}</div>
              </div>
              <div>
                <strong>Học sinh:</strong>
                <div>{selectedEvent.studentName}</div>
              </div>
              <div>
                <strong>Lớp:</strong>
                <div>{selectedEvent.className}</div>
              </div>
              <div>
                <strong>Y tá phụ trách:</strong>
                <div>{selectedEvent.nurseName}</div>
              </div>
              <div>
                <strong>Địa điểm:</strong>
                <div>{selectedEvent.location}</div>
              </div>
              <div>
                <strong>Ngày:</strong>
                <div>{dayjs(selectedEvent.date).format("DD/MM/YYYY")}</div>
              </div>
            </div>
            <div
              style={{ margin: "16px 0", borderTop: "1px solid #f0f0f0" }}
            ></div>
            <div style={{ lineHeight: "1.8em" }}>
              <div>
                <strong>Mô tả:</strong>
                <div>{selectedEvent.description || "(Không có)"}</div>
              </div>
              <div style={{ marginTop: 12 }}>
                <strong>Xử lý:</strong>
                <div>{selectedEvent.actionsTaken || "(Không có)"}</div>
              </div>
            </div>
            {selectedEvent.image && (
              <div style={{ marginTop: 16 }}>
                <strong>Hình ảnh sự cố:</strong>
                <div>
                  <img
                    src={selectedEvent.image}
                    alt="Ảnh sự kiện"
                    style={{
                      maxWidth: "40%",
                      borderRadius: 8,
                      marginTop: 8,
                      cursor: "pointer",
                    }}
                    onClick={() => {
                      setPreviewImage(selectedEvent.image);
                      setPreviewVisible(true);
                    }}
                  />
                </div>
              </div>
            )}
          </div>
        )}
      </Modal>

      {/* Modal Tạo sự kiện */}
      <Modal
        title="Tạo sự kiện y tế"
        open={createModalVisible}
        onCancel={() => {
          setCreateModalVisible(false);
          setEditingId(null);
        }}
        footer={null}
        destroyOnClose
      >
        <Form layout="vertical" form={form} onFinish={handleCreateEvent}>
          <Form.Item
            label="Tên sự kiện"
            name="eventName"
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="Loại sự kiện"
            name="typeEvent"
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>
          <Form.Item label="Ngày" name="date" rules={[{ required: true }]}>
            <DatePicker format="DD/MM/YYYY" style={{ width: "100%" }} />
          </Form.Item>

          <Form.Item
            label="Lớp"
            name="classId"
            rules={[{ required: true, message: "Vui lòng chọn lớp" }]}
          >
            <Select placeholder="Chọn lớp" onChange={handleClassChange}>
              {classOptions.map((cls) => (
                <Option key={cls.classId} value={cls.classId}>
                  {cls.className}
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            label="Học sinh"
            name="studentId"
            rules={[{ required: true, message: "Vui lòng chọn học sinh" }]}
          >
            <Select
              placeholder="Chọn học sinh"
              disabled={studentOptions.length === 0}
            >
              {studentOptions.map((s) => (
                <Option key={s.studentId} value={s.studentId}>
                  {s.fullName}
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            label="Mức độ"
            name="levelCheck"
            rules={[{ required: true }]}
          >
            <Select>
              <Option value="LOW">LOW</Option>
              <Option value="MEDIUM">MEDIUM</Option>
              <Option value="HIGH">HIGH</Option>
            </Select>
          </Form.Item>
          <Form.Item
            label="Địa điểm"
            name="location"
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>
          <Form.Item label="Mô tả" name="description">
            <TextArea rows={2} />
          </Form.Item>
          <Form.Item label="Xử lý" name="actionsTaken">
            <TextArea rows={2} />
          </Form.Item>
          <Form.Item label="Ảnh">
            <Upload
              beforeUpload={(file) => {
                const reader = new FileReader();
                reader.onload = (e) => {
                  setUploadedImage(e.target.result); // Base64 image
                };
                reader.readAsDataURL(file);
                return false; // Prevent auto upload
              }}
              showUploadList={{ showRemoveIcon: true }}
              onRemove={() => setUploadedImage(null)}
              accept="image/*"
              maxCount={1}
            >
              <Button icon={<UploadOutlined />}>Chọn ảnh</Button>
            </Upload>
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" block>
              Tạo sự kiện
            </Button>
          </Form.Item>
        </Form>
      </Modal>
      <Modal
        open={previewVisible}
        footer={null}
        onCancel={() => setPreviewVisible(false)}
        centered
        bodyStyle={{ padding: 0, backgroundColor: "transparent" }}
      >
        <img
          alt="preview"
          src={previewImage}
          style={{ width: "100%", maxHeight: "80vh", objectFit: "contain" }}
        />
      </Modal>
    </div>
  );
};

export default MedicalEventList;
