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
  Avatar,
} from "antd";
import Swal from "sweetalert2";
import dayjs from "dayjs";
import { Upload } from "antd";
import {
  UploadOutlined,
  EditOutlined,
  DeleteOutlined,
} from "@ant-design/icons";
import { ExclamationCircleOutlined } from "@ant-design/icons";

import { urlServer } from "../../../api/urlServer";

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
  const [selectedStudent, setSelectedStudent] = useState(null);

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
      const res = await axios.get(`${urlServer}/api/nurse/class-list`, {
        headers: { Authorization: `Bearer ${token}` },
      });
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
        `${urlServer}/api/admin/students/${classId}`,
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
      const res = await axios.get(`${urlServer}/api/nurse/medical-event`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      const apiData = res.data.map((item) => ({
        ...item,
        classId:
          item.studentDTO?.classDTO?.classId ||
          item.classDTO?.classId ||
          item.classId, // Ưu tiên lấy từ studentDTO
        studentName: item.studentDTO?.fullName || `ID ${item.studentId}`,
        className:
          item.classDTO?.className ||
          item.studentDTO?.classDTO?.className ||
          "Không rõ",
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

  const [submitLoading, setSubmitLoading] = useState(false);

  const handleCreateEvent = async (values) => {
    setSubmitLoading(true);
    try {
      const token = localStorage.getItem("token");
      const formData = new FormData();
      const requestObj = {
        typeEvent: values.typeEvent,
        date: dayjs(values.date).format("YYYY-MM-DDTHH:mm:ss"),
        classId: values.classId,
        studentId: values.studentId,
        levelCheck: values.levelCheck,
        location: values.location,
        description: values.description || "",
        actionsTaken: values.actionsTaken || "",
      };
      formData.append("request", JSON.stringify(requestObj));
      if (fileList.length > 0 && fileList[0].originFileObj) {
        formData.append("image", fileList[0].originFileObj);
      }

      let res;
      if (editingId) {
        // Sửa sự kiện
        res = await axios.put(
          `${urlServer}/api/nurse/medical-event/${editingId}`,
          formData,
          {
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "multipart/form-data",
            },
          }
        );
        Swal.fire({
          icon: "success",
          title: "Cập nhật sự kiện thành công!",
          showConfirmButton: false,
          timer: 2000,
        });
        fetchMedicalEvents();
      } else {
        // Tạo mới
        res = await axios.post(
          `${urlServer}/api/nurse/medical-event`,
          formData,
          {
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "multipart/form-data",
            },
          }
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
      setFileList([]);
    } catch (error) {
      console.error("Lỗi tạo/cập nhật sự kiện:", error);
      message.error("Không thể lưu sự kiện.");
    } finally {
      setSubmitLoading(false);
    }
  };

  const handleEditClick = async (item) => {
    setEditingId(item.eventId);
    setCreateModalVisible(true);

    try {
      const token = localStorage.getItem("token");

      let loadedClassOptions = classOptions;

      // Nếu chưa có danh sách lớp, tải
      if (classOptions.length === 0) {
        const classRes = await axios.get(`${urlServer}/api/nurse/class-list`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        loadedClassOptions = classRes.data;
        setClassOptions(loadedClassOptions);
      }

      // Tải danh sách học sinh theo lớp
      const classId = item.classId; // Lấy classId đã gán ở trên
      const studentId = item.studentDTO?.studentId;

      const studentRes = await axios.get(
        `${urlServer}/api/admin/students/${classId}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      const loadedStudentOptions = studentRes.data;
      setStudentOptions(loadedStudentOptions);

      // Set giá trị form, chọn sẵn lớp và học sinh
      form.setFieldsValue({
        typeEvent: item.typeEvent,
        date: dayjs(item.date),
        description: item.description,
        actionsTaken: item.actionsTaken,
        levelCheck: item.levelCheck,
        location: item.location,
        classId: classId, // <-- chọn sẵn lớp
        studentId,
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

      // Set selectedStudent nếu có studentId
      const student = loadedStudentOptions.find(
        (s) => s.studentId === studentId
      );
      setSelectedStudent(student || null);
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
            `${urlServer}/api/nurse/medical-event/${medicalEventId}`,
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
        title={
          <div style={{ textAlign: "center" }}>
            {editingId ? "Chỉnh sửa sự kiện y tế" : "Tạo sự kiện y tế"}
          </div>
        }
        open={createModalVisible}
        onCancel={() => {
          setCreateModalVisible(false);
          setEditingId(null);
          setSelectedStudent(null);
        }}
        footer={null}
        destroyOnClose
        width={720}
      >
        <Form
          layout="vertical"
          form={form}
          onFinish={handleCreateEvent}
          requiredMark={false}
        >
          {/* Hiển thị avatar nếu có */}
          {selectedStudent && selectedStudent.avatarUrl && (
            <div style={{ textAlign: "center", marginBottom: 16 }}>
              <Avatar
                src={selectedStudent.avatarUrl}
                size={80}
                style={{ border: "2px solid #91caff" }}
                alt={selectedStudent.fullName}
              />
              <div style={{ marginTop: 8, fontWeight: 500 }}>
                {selectedStudent.fullName}
              </div>
            </div>
          )}

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label={
                  <span>
                    Sự cố{" "}
                    <ExclamationCircleOutlined
                      style={{ color: "red", fontSize: 12, marginLeft: 4 }}
                    />
                  </span>
                }
                name="typeEvent"
                rules={[
                  { required: true, message: "Vui lòng điền sự cố xảy ra" },
                ]}
              >
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={
                  <span>
                    Ngày{" "}
                    <ExclamationCircleOutlined
                      style={{ color: "red", fontSize: 12, marginLeft: 4 }}
                    />
                  </span>
                }
                name="date"
                rules={[
                  {
                    required: true,
                    message: "Vui lòng chọn ngày xảy ra sự cố",
                  },
                  {
                    validator: (_, value) =>
                      value && value.isAfter(dayjs(), "day")
                        ? Promise.reject("Ngày không được vượt quá hôm nay")
                        : Promise.resolve(),
                  },
                ]}
              >
                <DatePicker
                  format="DD/MM/YYYY"
                  style={{ width: "100%" }}
                  disabledDate={(current) =>
                    current && current > dayjs().endOf("day")
                  }
                />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label={
                  <span>
                    Lớp{" "}
                    <ExclamationCircleOutlined
                      style={{ color: "red", fontSize: 12, marginLeft: 4 }}
                    />
                  </span>
                }
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
            </Col>
            <Col span={12}>
              <Form.Item
                label={
                  <span>
                    Học sinh{" "}
                    <ExclamationCircleOutlined
                      style={{ color: "red", fontSize: 12, marginLeft: 4 }}
                    />
                  </span>
                }
                name="studentId"
                rules={[{ required: true, message: "Vui lòng chọn học sinh" }]}
              >
                <Select
                  placeholder="Chọn học sinh"
                  disabled={studentOptions.length === 0}
                  onChange={(studentId) => {
                    const student = studentOptions.find(
                      (s) => s.studentId === studentId
                    );
                    setSelectedStudent(student || null);
                  }}
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
                label={
                  <span>
                    Địa điểm{" "}
                    <ExclamationCircleOutlined
                      style={{ color: "red", fontSize: 12, marginLeft: 4 }}
                    />
                  </span>
                }
                name="location"
                rules={[
                  { required: true, message: "Vui lòng điền địa điểm xảy ra" },
                ]}
              >
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={
                  <span>
                    Mức độ{" "}
                    <ExclamationCircleOutlined
                      style={{ color: "red", fontSize: 12, marginLeft: 4 }}
                    />
                  </span>
                }
                name="levelCheck"
                rules={[
                  { required: true, message: "Vui lòng chọn mức độ nguy hiểm" },
                ]}
              >
                <Select>
                  <Option value="LOW">Nhẹ</Option>
                  <Option value="MEDIUM">Trung bình</Option>
                  <Option value="HIGH">Nặng</Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
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
                        originFileObj: file, // Giữ lại file gốc ở đây!
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
                  setPreviewImage(file.url);
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
          </Row>

          <Form.Item label="Mô tả" name="description">
            <TextArea rows={2} placeholder="Mô tả ngắn gọn về sự kiện" />
          </Form.Item>

          <Form.Item label="Xử lý" name="actionsTaken">
            <TextArea rows={2} placeholder="Hành động đã thực hiện" />
          </Form.Item>

          <Form.Item style={{ textAlign: "center", marginTop: 24 }}>
            <Button
              type="primary"
              htmlType="submit"
              style={{ width: 200 }}
              loading={submitLoading} // Thêm loading
              disabled={submitLoading} // Ngăn bấm thêm
            >
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
