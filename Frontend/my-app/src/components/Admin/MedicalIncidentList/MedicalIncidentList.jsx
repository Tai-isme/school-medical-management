import React, { useEffect, useState } from "react";
import {
  Table,
  Button,
  Modal,
  Form,
  Input,
  message,
  Row,
  Col,
  DatePicker,
  Card,
  Pagination,
  Select,
  Upload,
} from "antd";
import { PlusOutlined } from "@ant-design/icons";
import axios from "axios";
import dayjs from "dayjs";
import "./MedicalIncidentList.css";
import Swal from "sweetalert2"; // Thêm dòng này ở đầu file nếu chưa có

const MedicalIncidentList = () => {
  const [data, setData] = useState([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [form] = Form.useForm();
  const [searchEvent, setSearchEvent] = useState("");
  const [searchDate, setSearchDate] = useState(null);
  const [searchStudent, setSearchStudent] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [classList, setClassList] = useState([]);
  const [studentList, setStudentList] = useState([]);
  const [selectedClass, setSelectedClass] = useState(null);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [fileList, setFileList] = useState([]);
  const [previewVisible, setPreviewVisible] = useState(false);
  const [previewImage, setPreviewImage] = useState("");
  const pageSize = 3;

  // Tải danh sách sự kiện y tế từ API
  useEffect(() => {
    const fetchEvents = async () => {
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
          studentName: item.studentDTO.fullName || `ID ${item.studentId}`,
          parentPhone: item.parentPhone || "Chưa có",
        }));

        setData(apiData);
      } catch (err) {
        console.error("Lỗi tải sự kiện:", err);
        message.error("Không thể tải danh sách sự kiện!");
      }
    };

    fetchEvents();
  }, []);

  // Lấy danh sách lớp khi mở modal
  const showModal = async () => {
    setIsModalVisible(true);
    try {
      const token = localStorage.getItem("token");
      const res = await axios.get("http://localhost:8080/api/admin/class", {
        headers: { Authorization: `Bearer ${token}` },
      });
      setClassList(res.data);
    } catch {
      setClassList([]);
    }
  };

  // Khi chọn lớp, lấy danh sách học sinh của lớp đó
  const handleClassChange = async (classId) => {
    setSelectedClass(classId);
    form.setFieldsValue({ studentId: undefined }); // reset chọn học sinh
    try {
      const token = localStorage.getItem("token");
      const res = await axios.get(
        `http://localhost:8080/api/admin/students/${classId}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setStudentList(res.data);
    } catch {
      setStudentList([]);
    }
  };

  const handleCancel = () => {
    form.resetFields();
    setIsModalVisible(false);
  };

  const handleCreate = async () => {
    try {
      const values = await form.validateFields();

      // Nếu upload nhiều ảnh, lấy danh sách url hoặc file
      // Nếu upload 1 ảnh, lấy fileList[0]
      let imageUrl = "";
      if (fileList.length > 0) {
        // Nếu backend nhận file, dùng FormData, nếu chỉ cần url thì lấy url
        // Ví dụ: imageUrl = fileList[0].thumbUrl hoặc fileList[0].originFileObj
        imageUrl = fileList[0].thumbUrl || "";
      }

      const payload = {
        typeEvent: values.typeEvent,
        description: values.description,
        studentId: Number(values.studentId),
        actionsTaken: values.actionsTaken,
        level: values.level,
        location: values.location,
        nurseID: values.nurseID,
        imageUrl, // Thêm trường ảnh vào payload
      };

      const token = localStorage.getItem("token");

      const res = await axios.post(
        "http://localhost:8080/api/nurse/medical-event",
        payload,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      // Tìm học sinh vừa chọn trong studentList để lấy tên và lớp
      const selectedStudent = studentList.find(
        (stu) => stu.id === values.studentId
      );

      const created = {
        ...res.data,
        eventId: res.data.eventId || Date.now(),
        date: dayjs().format("YYYY-MM-DD"),
        studentName: selectedStudent
          ? selectedStudent.fullName
          : `ID ${values.studentId}`,
        className: selectedStudent ? selectedStudent.className : "Không rõ",
        nurseId: `ID ${res.data.nurseId || "---"}`,
        parentPhone: res.data.parentPhone || "Chưa có",
      };

      setData((prev) => [...prev, created]);
      form.resetFields();
      setIsModalVisible(false);
      Swal.fire({
        icon: "success",
        title: "Tạo sự kiện thành công!",
        showConfirmButton: false,
        timer: 1500,
      });
    } catch (error) {
      console.error("Lỗi tạo sự kiện:", error);
      message.error("Không thể tạo sự kiện. Kiểm tra lại thông tin.");
    }
  };

  // Lọc dữ liệu theo tên sự kiện và ngày
  const filteredData = data.filter((item) => {
    const matchEvent = item.typeEvent
      .toLowerCase()
      .includes(searchEvent.toLowerCase());
    const matchStudent = item.studentName
      ?.toLowerCase()
      .includes(searchStudent.toLowerCase());
    const matchDate = searchDate
      ? dayjs(item.date).isSame(searchDate, "day")
      : true;
    return matchEvent && matchStudent && matchDate;
  });

  // Dữ liệu trang hiện tại
  const pagedData = filteredData.slice(
    (currentPage - 1) * pageSize,
    currentPage * pageSize
  );

  useEffect(() => {
    setCurrentPage(1);
  }, [searchEvent, searchDate, searchStudent]);

  // Hàm xử lý preview
  const handlePreview = async (file) => {
    setPreviewImage(file.thumbUrl || file.url || "");
    setPreviewVisible(true);
  };

  return (
    <div className="incident-container">
      <div className="incident-header">
        <h2>Sự Kiện Y Tế Gần Đây</h2>
        <Button type="primary" danger onClick={showModal}>
          + Thêm sự kiện
        </Button>
      </div>

      {/* Thanh tìm kiếm */}
      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col>
          <Input
            placeholder="Tìm theo tên sự kiện"
            value={searchEvent}
            onChange={(e) => setSearchEvent(e.target.value)}
            allowClear
          />
        </Col>
        <Col>
          <Input
            placeholder="Tìm theo tên học sinh"
            value={searchStudent}
            onChange={(e) => setSearchStudent(e.target.value)}
            allowClear
          />
        </Col>
        <Col>
          <DatePicker
            placeholder="Tìm theo ngày"
            value={searchDate}
            onChange={setSearchDate}
            allowClear
            format="YYYY-MM-DD"
          />
        </Col>
      </Row>

      {/* Danh sách card */}
      <div style={{ padding: 8 }}>
        {pagedData.length === 0 ? (
          <div style={{ textAlign: "center", color: "#888" }}>
            Không có sự kiện nào
          </div>
        ) : (
          pagedData.map((item) => (
            <Card
              key={item.eventId}
              style={{
                marginBottom: 16,
                borderRadius: 10,
                border: "1px solid #eee",
                boxShadow: "0 2px 8px rgba(0,0,0,0.03)",
              }}
              bodyStyle={{ padding: 20 }}
            >
              <div
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "flex-start",
                }}
              >
                <div style={{ width: "100%" }}>
                  {/* Tên sự kiện ở trên cùng, căn giữa, nổi bật */}
                  <div
                    style={{
                      color: "#ff4d4f",
                      fontWeight: 700,
                      fontSize: 18,
                      marginBottom: 12,
                      textTransform: "uppercase",
                      letterSpacing: 1,
                    }}
                  >
                    {item.typeEvent}
                  </div>
                  <div
                    style={{ fontWeight: 600, fontSize: 16, marginBottom: 6 }}
                  >
                    <span style={{ color: "#1476d1" }}>Tên học sinh:</span>{" "}
                    {item.studentDTO.fullName}{" "}
                    {item.className ? `- ${item.className}` : ""}
                  </div>
                  <div style={{ color: "#888", fontSize: 14, marginBottom: 6 }}>
                    <span style={{ color: "#1476d1" }}>Ngày:</span>{" "}
                    {item.date ? dayjs(item.date).format("DD/MM/YYYY") : ""}
                  </div>
                  <div
                    style={{ color: "#555", fontSize: 15, marginBottom: 12 }}
                  >
                    <span style={{ color: "#1476d1" }}>Mô tả:</span>{" "}
                    {item.description}
                  </div>
                  <Button
                    size="small"
                    style={{ marginRight: 8 }}
                    onClick={() => {
                      setSelectedEvent(item);
                      setDetailModalVisible(true);
                    }}
                  >
                    Chi tiết
                  </Button>
                </div>
              </div>
            </Card>
          ))
        )}
      </div>

      {/* Phân trang */}
      <div style={{ textAlign: "center", margin: "16px 0" }}>
        <Pagination
          current={currentPage}
          pageSize={pageSize}
          total={filteredData.length}
          onChange={setCurrentPage}
          showSizeChanger={false}
        />
      </div>

      {/* Modal tạo sự kiện giữ nguyên */}
      <Modal
        title="Tạo sự cố y tế"
        open={isModalVisible}
        onCancel={handleCancel}
        onOk={handleCreate}
        okText="Tạo"
        cancelText="Hủy"
      >
        <Form form={form} layout="vertical">
          <Form.Item
            label="Tên sự kiện"
            name="typeEvent"
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="Chọn lớp"
                name="classId"
                rules={[{ required: true, message: "Vui lòng chọn lớp" }]}
              >
                <Select
                  placeholder="Chọn lớp"
                  onChange={handleClassChange}
                  showSearch
                  optionFilterProp="children"
                  filterOption={(input, option) =>
                    (option?.children ?? "")
                      .toLowerCase()
                      .includes(input.toLowerCase())
                  }
                >
                  {classList.map((cls) => (
                    <Select.Option key={cls.classId} value={cls.classId}>
                      {cls.className}
                    </Select.Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="Chọn học sinh"
                name="studentId"
                rules={[{ required: true, message: "Vui lòng chọn học sinh" }]}
              >
                <Select
                  placeholder="Chọn học sinh"
                  disabled={!selectedClass}
                  showSearch
                  optionFilterProp="children"
                  filterOption={(input, option) =>
                    (option?.children ?? "")
                      .toLowerCase()
                      .includes(input.toLowerCase())
                  }
                >
                  {studentList.map((stu) => (
                    <Select.Option key={stu.id} value={stu.id}>
                      {stu.fullName}
                    </Select.Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="Mức độ"
                name="level"
                rules={[{ required: true, message: "Vui lòng chọn mức độ" }]}
              >
                <Select placeholder="Chọn mức độ">
                  <Select.Option value="Nhẹ">Nhẹ</Select.Option>
                  <Select.Option value="TB">Trung bình</Select.Option>
                  <Select.Option value="Nặng">Nặng</Select.Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="Y tá phụ trách (ID)"
                name="nurseID"
                rules={[{ required: true, message: "Vui lòng nhập ID y tá" }]}
              >
                <Input placeholder="Nhập ID y tá phụ trách" />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item
            label="Địa điểm"
            name="location"
            rules={[{ required: true, message: "Vui lòng nhập địa điểm" }]}
          >
            <Input placeholder="Phòng y tế, lớp học, ..." />
          </Form.Item>
          <Form.Item
            label="Mô tả"
            name="description"
            rules={[{ required: true }]}
          >
            <Input.TextArea />
          </Form.Item>
          <Form.Item
            label="Xử lý đã thực hiện"
            name="actionsTaken"
            rules={[
              { required: true, message: "Vui lòng nhập xử lý đã thực hiện" },
            ]}
          >
            <Input placeholder="Đã uống thuốc hạ sốt, Đã băng bó, ..." />
          </Form.Item>
          <Form.Item label="Hình ảnh sự cố">
            <Upload
              listType="picture-card"
              fileList={fileList}
              beforeUpload={() => false}
              onChange={({ fileList }) => setFileList(fileList)}
              maxCount={1}
              onPreview={handlePreview} // Thêm dòng này
            >
              {fileList.length >= 1 ? null : (
                <div>
                  <PlusOutlined />
                  <div style={{ marginTop: 8 }}>Thêm ảnh</div>
                </div>
              )}
            </Upload>
            <Modal
              open={previewVisible}
              footer={null}
              onCancel={() => setPreviewVisible(false)}
            >
              <img
                alt="Hình ảnh sự cố"
                style={{ width: "100%" }}
                src={previewImage}
              />
            </Modal>
          </Form.Item>
        </Form>
      </Modal>

      {/* Modal chi tiết sự kiện */}
      <Modal
        title="Chi tiết sự kiện y tế"
        open={detailModalVisible}
        onCancel={() => setDetailModalVisible(false)}
        footer={null}
      >
        {selectedEvent && (
          <div>
            <div
              style={{
                color: "#ff4d4f",
                fontWeight: 700,
                fontSize: 20,
                marginBottom: 15,
                textTransform: "uppercase",
                letterSpacing: 1,
              }}
            >
              {selectedEvent.typeEvent}
            </div>
            <Row gutter={16} style={{ marginBottom: 0 }}>
              <Col span={12}>
                <p>
                  <strong>Tên học sinh :</strong> {selectedEvent.studentName}
                </p>
              </Col>
              <Col span={12}>
                <p>
                  <strong>Lớp :</strong>{" "}
                  {selectedEvent.classDTO?.className ||
                    selectedEvent.className ||
                    "Không rõ"}
                </p>
              </Col>
            </Row>
            <p>
              <strong>Ngày :</strong>{" "}
              {selectedEvent.date
                ? dayjs(selectedEvent.date).format("DD/MM/YYYY")
                : ""}
            </p>
            <p>
              <strong>Y tá phụ trách :</strong> {selectedEvent.nurseID}
            </p>
            <p>
              <strong>Mức độ :</strong> {selectedEvent.level}
            </p>
            <p>
              <strong>Địa điểm :</strong> {selectedEvent.location}
            </p>
            <p>
              <strong>Mô tả :</strong> {selectedEvent.description}
            </p>
            <p>
              <strong>Xử lý đã thực hiện :</strong> {selectedEvent.actionsTaken}
            </p>
            <div style={{ marginTop: 12 }}>
              <strong>Hình ảnh sự cố:</strong>
              <div>
                {selectedEvent.imageUrl ? (
                  <img
                    src={selectedEvent.imageUrl}
                    alt="Hình ảnh sự cố"
                    style={{ maxWidth: 320, marginTop: 8, borderRadius: 8 }}
                  />
                ) : (
                  <span style={{ color: "#888", marginLeft: 8 }}>
                    Không có hình ảnh
                  </span>
                )}
              </div>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default MedicalIncidentList;
