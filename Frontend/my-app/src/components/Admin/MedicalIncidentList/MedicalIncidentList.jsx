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
  Popover,
} from "antd";
import Swal from "sweetalert2";
import dayjs from "dayjs";
import { Upload } from "antd";
import {
  UploadOutlined,
  EditOutlined,
  DeleteOutlined,
} from "@ant-design/icons";

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

  const [fileList, setFileList] = useState([]);

  const renderLevelText = (level) => {
    switch (level) {
      case "LOW":
        return "Nhẹ";
      case "MEDIUM":
        return "Trung bình";
      case "HIGH":
        return "Nặng";
      default:
        return "Không rõ";
    }
  };

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
        parentName: item.parentDTO?.fullName || `ID ${item.parentId}`,
        parentphone: item.parentDTO?.phone || "Không có",
        nursePhone: item.nurseDTO?.phone || "Không có",
        email: item.nurseDTO?.email || "Không có",
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
        // Chế độ sửa
        await axios.put(
          `http://localhost:8080/api/nurse/medical-event/${editingId}`,
          payload,
          { headers: { Authorization: `Bearer ${token}` } }
        );
        Swal.fire({
          icon: "success",
          title: "Cập nhật sự kiện thành công!",
          showConfirmButton: false,
          timer: 2000,
        });

        fetchMedicalEvents(); // Cập nhật danh sách
      } else {
        // Chế độ tạo mới
        const res = await axios.post(
          "http://localhost:8080/api/nurse/medical-event",
          payload,
          { headers: { Authorization: `Bearer ${token}` } }
        );
        Swal.fire({
          icon: "success",
          title: "Tạo sự kiện thành công!",
          showConfirmButton: false,
          timer: 2000,
        });

        const newEvent = {
          ...res.data,
          studentName:
            res.data.studentDTO?.fullName || `ID ${res.data.studentId}`,
          className: res.data.classDTO?.className || "Không rõ",
          nurseName: res.data.nurseDTO?.fullName || `ID ${res.data.nurseId}`,
          parentName: res.data.parentDTO?.fullName || `ID ${res.data.parentId}`,
          parentphone: res.data.parentDTO?.phone || "Không có",
        };

        setData((prev) => [newEvent, ...prev]);
        setFilteredData((prev) => [newEvent, ...prev]);
      }

      // Reset form và modal
      form.resetFields();
      setUploadedImage(null);
      setEditingId(null);
      setCreateModalVisible(false);
    } catch (error) {
      console.error("Lỗi tạo/cập nhật sự kiện:", error);
      message.error("Không thể lưu sự kiện.");
    }
  };

  const handleEditClick = async (item) => {
    setEditingId(item.eventId);
    setCreateModalVisible(true);

    try {
      const token = localStorage.getItem("token");

      let loadedClassOptions = classOptions;

      // 1. Nếu chưa có danh sách lớp, tải
      if (classOptions.length === 0) {
        const classRes = await axios.get(
          "http://localhost:8080/api/nurse/class-list",
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        loadedClassOptions = classRes.data;
        setClassOptions(loadedClassOptions);
      }

      // 2. Tải danh sách học sinh theo lớp
      const classId = item.classDTO?.classId;
      const studentId = item.studentDTO?.studentId;

      const studentRes = await axios.get(
        `http://localhost:8080/api/admin/students/${classId}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      const loadedStudentOptions = studentRes.data;
      setStudentOptions(loadedStudentOptions);

      // 3. Set giá trị form
      form.setFieldsValue({
        eventName: item.eventName,
        typeEvent: item.typeEvent,
        date: dayjs(item.date),
        classId,
        studentId,
        levelCheck: item.levelCheck,
        location: item.location,
        description: item.description,
        actionsTaken: item.actionsTaken,
      });

      // 4. Set ảnh và hiển thị preview
      if (item.image) {
        setUploadedImage(item.image);
        setFileList([
          {
            uid: "-1",
            name: "Ảnh đã lưu",
            status: "done",
            url: item.image, // Preview ảnh đã lưu
          },
        ]);
      } else {
        setFileList([]); // Không có ảnh thì clear preview
      }
    } catch (error) {
      console.error("Lỗi khi load dữ liệu chỉnh sửa:", error);
      message.error("Không thể tải thông tin chỉnh sửa.");
    }
  };

  const handleDeleteEvent = (medicalEventId) => {
    Swal.fire({
      title: "Bạn có chắc muốn xoá sự kiện này?",
      text: "Hành động này không thể hoàn tác.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      cancelButtonColor: "#3085d6",
      confirmButtonText: "Xoá",
      cancelButtonText: "Hủy",
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          const token = localStorage.getItem("token");
          await axios.delete(
            `http://localhost:8080/api/nurse/medical-event/${medicalEventId}`,
            {
              headers: { Authorization: `Bearer ${token}` },
            }
          );
          Swal.fire("Đã xoá!", "Sự kiện đã được xoá.", "success");
          fetchMedicalEvents();
        } catch (err) {
          console.error("Lỗi xoá sự kiện:", err);
          Swal.fire("Lỗi!", "Không thể xoá sự kiện.", "error");
        }
      }
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
            <Option value="LOW">Nhẹ</Option>
            <Option value="MEDIUM">Trung bình</Option>
            <Option value="HIGH">Nặng</Option>
          </Select>
        </Col>
        <Col span={6}>
          <Button
            type="primary"
            onClick={() => {
              setEditingId(null);
              form.resetFields();
              setUploadedImage(null);
              setFileList([]);
              setCreateModalVisible(true);
            }}
          >
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
            >
              {/* Tag mức độ */}
              <div style={getLevelStyle(item.levelCheck)}>
                {renderLevelText(item.levelCheck)}
              </div>

              {/* Nội dung bên trái */}
              <div>
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
                <p>
                  <strong>Y tá phụ trách:</strong> {item.nurseName}
                </p>
                <Popover
                  title="📞 Thông tin liên hệ"
                  content={
                    <div>
                      <p>
                        <strong>Phụ huynh:</strong> {item.parentName}
                      </p>
                      <p>
                        <strong>Điện thoại:</strong> {item.parentphone}
                      </p>
                    </div>
                  }
                  trigger="click"
                >
                  <div
                    style={{
                      display: "inline-block",
                      padding: "8px 12px",
                      background: "#f0f0f0",
                      borderRadius: 6,
                      cursor: "pointer",
                      width: "fit-content",
                      marginTop: 8,
                    }}
                  >
                    📞 Xem thông tin phụ huynh
                  </div>
                </Popover>
              </div>

              {/* Hàng chứa các nút hành động */}
              <div
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                  marginTop: 16,
                }}
              >
                {/* Bên trái: Xem chi tiết */}
                <Button
                  type="link"
                  onClick={() => {
                    setSelectedEvent(item);
                    setDetailModalVisible(true);
                  }}
                >
                  Xem chi tiết
                </Button>

                {/* Bên phải: Sửa và Xoá */}
                <div style={{ display: "flex", gap: 8 }}>
                  <Button
                    type="primary"
                    icon={<EditOutlined />}
                    onClick={() => handleEditClick(item)}
                    style={{
                      backgroundColor: "#ffa940",
                      borderColor: "#ffa940",
                      color: "#fff",
                    }}
                  >
                    Sửa
                  </Button>
                  <Button
                    type="primary"
                    icon={<DeleteOutlined />}
                    danger
                    onClick={(e) => {
                      e.stopPropagation();
                      handleDeleteEvent(item.eventId);
                    }}
                    style={{
                      backgroundColor: "#ff4d4f",
                      borderColor: "#ff4d4f",
                      color: "#fff",
                    }}
                  >
                    Xoá
                  </Button>
                </div>
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
        ]}
        bodyStyle={{
          position: "relative",
          padding: 24,
          paddingTop: 40,
          backgroundColor: "#fefefe",
          borderRadius: 8,
          maxHeight: "70vh",
          overflowY: "auto",
        }}
      >
        {selectedEvent && (
          <div>
            <div style={getLevelStyle(selectedEvent.levelCheck)}>
              {renderLevelText(selectedEvent.levelCheck)}
            </div>

            <div
              style={{
                display: "grid",
                gridTemplateColumns: "1fr 1fr",
                gap: "12px 24px",
                lineHeight: "1.8em",
              }}
            >
              {/* Thông tin liên hệ */}
              <div style={{ gridColumn: "1 / span 2" }}>
                <div
                  style={{
                    border: "2px solid #91caff", // đậm hơn
                    borderRadius: 8,
                    padding: 16,
                    backgroundColor: "#e6f7ff", // xanh nhạt làm nổi bật card
                  }}
                >
                  <strong
                    style={{
                      display: "block",
                      marginBottom: 12,
                      fontSize: 16,
                      color: "#096dd9",
                    }}
                  >
                    📞 Thông tin liên hệ
                  </strong>

                  <div style={{ marginBottom: 8 }}>
                    <span style={{ fontWeight: 600, color: "#222" }}>
                      👤 Phụ huynh:
                    </span>{" "}
                    <span style={{ fontSize: 15 }}>
                      {selectedEvent.parentName}
                    </span>
                  </div>

                  <div>
                    <span style={{ fontWeight: 600, color: "#222" }}>
                      📱 Điện thoại:
                    </span>{" "}
                    <span style={{ fontSize: 15 }}>
                      {selectedEvent.parentphone}
                    </span>
                  </div>
                </div>
              </div>

              {/* Các thông tin khác */}
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
                <strong>Email Y Tá:</strong>
                <div>{selectedEvent.email}</div>
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
        title={editingId ? "Chỉnh sửa sự kiện y tế" : "Tạo sự kiện y tế"}
        open={createModalVisible}
        onCancel={() => {
          setCreateModalVisible(false);
          setEditingId(null);
        }}
        footer={null}
        destroyOnClose
        width={720} // mở rộng modal để chia 2 cột thoải mái
      >
        <Form layout="vertical" form={form} onFinish={handleCreateEvent}>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="Tên sự kiện"
                name="eventName"
                rules={[{ required: true }]}
              >
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="Loại sự kiện"
                name="typeEvent"
                rules={[{ required: true }]}
              >
                <Input />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            {/* <Col span={12}>
              <Form.Item
                label="Ngày"
                name="date"
                rules={[
                  { required: true, message: "Vui lòng chọn ngày" },
                  {
                    validator: (_, value) => {
                      if (!value) return Promise.resolve();
                      const today = dayjs().startOf("day");
                      const selected = value.startOf("day");

                      return selected.isSame(today)
                        ? Promise.resolve()
                        : Promise.reject("Chỉ được chọn ngày hôm nay");
                    },
                  },
                ]}
              >
                <DatePicker format="DD/MM/YYYY" style={{ width: "100%" }} />
              </Form.Item>
            </Col> */}
            <Col span={12}>
              <Form.Item
                label="Ngày"
                name="date"
                rules={[{ required: true, message: "Vui lòng chọn ngày" }]}
              >
                <DatePicker format="DD/MM/YYYY" style={{ width: "100%" }} />
              </Form.Item>
            </Col>

            <Col span={12}>
              <Form.Item
                label="Địa điểm"
                name="location"
                rules={[{ required: true }]}
              >
                <Input />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="Lớp"
                name="classId"
                rules={[{ required: true }]}
              >
                <Select placeholder="Chọn lớp" onChange={handleClassChange}>
                  {classOptions.map((cls) => (
                    <Option key={cls.classId} value={cls.classId}>
                      {cls.className}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="Học sinh"
                name="studentId"
                rules={[{ required: true }]}
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
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="Mức độ"
                name="levelCheck"
                rules={[{ required: true }]}
              >
                <Select>
                  <Option value="LOW">Nhẹ</Option>
                  <Option value="MEDIUM">Trung bình</Option>
                  <Option value="HIGH">Nặng</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="Ảnh">
                <Upload
                  listType="picture-card"
                  fileList={fileList}
                  beforeUpload={(file) => {
                    const reader = new FileReader();
                    reader.onload = (e) => {
                      const base64 = e.target.result;
                      setUploadedImage(base64);
                      setFileList([
                        {
                          uid: "-1",
                          name: file.name,
                          status: "done",
                          url: base64,
                        },
                      ]);
                    };
                    reader.readAsDataURL(file);
                    return false;
                  }}
                  onRemove={() => {
                    setUploadedImage(null);
                    setFileList([]);
                  }}
                  onPreview={(file) => {
                    setPreviewImage(file.url); // hoặc file.thumbUrl
                    setPreviewVisible(true);
                  }}
                  accept="image/*"
                  maxCount={1}
                >
                  {fileList.length === 0 && (
                    <Button icon={<UploadOutlined />}>Tải ảnh</Button>
                  )}
                </Upload>
              </Form.Item>
            </Col>
          </Row>

          <Form.Item label="Mô tả" name="description">
            <TextArea rows={2} placeholder="Mô tả ngắn gọn về sự kiện" />
          </Form.Item>

          <Form.Item label="Xử lý" name="actionsTaken">
            <TextArea rows={2} placeholder="Hành động đã thực hiện" />
          </Form.Item>

          <Form.Item style={{ textAlign: "center", marginTop: 24 }}>
            <Button type="primary" htmlType="submit" style={{ width: 200 }}>
              {editingId ? "Lưu chỉnh sửa" : "Tạo sự kiện"}
            </Button>
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        open={previewVisible}
        footer={null}
        onCancel={() => setPreviewVisible(false)}
        centered
        zIndex={2000}
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
