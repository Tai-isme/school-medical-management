import React, { useEffect, useState } from "react";
import {
  Button,
  Card,
  Row,
  Col,
  Tag,
  Modal,
  Descriptions,
  Form,
  Input,
  DatePicker,
  message,
  Tabs,
  Pagination,
} from "antd";
import { PlusOutlined, SearchOutlined } from "@ant-design/icons";
import { Select } from "antd"; // Thêm dòng này
import axios from "axios";
import dayjs from "dayjs";
import Swal from "sweetalert2";
import { Table } from "antd"; // Thêm import này

const VaccineProgramList = () => {
  const [programs, setPrograms] = useState([]);
  const [detailVisible, setDetailVisible] = useState(false);
  const [createVisible, setCreateVisible] = useState(false);
  const [loading, setLoading] = useState(false);
  const [program, setProgram] = useState(null);
  const [searchTerm, setSearchTerm] = useState(""); // Tìm kiếm theo tên
  const [filterDate, setFilterDate] = useState(null); // Thêm state lọc ngày
  const [editMode, setEditMode] = useState(false);
  const [filterStatus, setFilterStatus] = useState(""); // Thêm state này
  const [resultVisible, setResultVisible] = useState(false);
  const [resultData, setResultData] = useState([]);
  const [resultLoading, setResultLoading] = useState(false);
  const [createResultVisible, setCreateResultVisible] = useState(false);
  const [createResultLoading, setCreateResultLoading] = useState(false);
  const [resultForm] = Form.useForm();
  const [activeTab, setActiveTab] = useState("program");
  const [currentPage, setCurrentPage] = useState(1);
  const [showResultPage, setShowResultPage] = useState(false);
  const [resultPageData, setResultPageData] = useState([]);
  const [resultPageLoading, setResultPageLoading] = useState(false);
  const pageSize = 3; // Số chương trình mỗi trang
  const userRole = localStorage.getItem("role"); // Lấy role từ localStorage

  // Thêm state để lưu danh sách vaccine
  const [vaccineList, setVaccineList] = useState([]);
  const [addVaccineVisible, setAddVaccineVisible] = useState(false);
  const [addVaccineLoading, setAddVaccineLoading] = useState(false);
  const [addVaccineForm] = Form.useForm();

  useEffect(() => {
    fetchProgram();
    // Lấy danh sách vaccine khi load trang
    fetchVaccineList();
  }, []);

  const fetchProgram = async () => {
    const token = localStorage.getItem("token");
    try {
      const res = await axios.get(
        "http://localhost:8080/api/admin/vaccine-program",
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setPrograms(res.data);
    } catch (error) {
      setPrograms([]);
    }
  };

  const fetchVaccineList = async () => {
    const token = localStorage.getItem("token");
    try {
      const res = await axios.get(
        "http://localhost:8080/api/admin/get=all-VaccineName",
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setVaccineList(res.data);
    } catch {
      setVaccineList([]);
    }
  };

  const handleCreate = async (values) => {
    setLoading(true);
    const token = localStorage.getItem("token");
    try {
      await axios.post(
        "http://localhost:8080/api/admin/vaccine-program",
        {
          vaccineNameId: values.vaccineNameId,
          description: values.description,
          vaccineDate: values.vaccineDate.format("YYYY-MM-DD"),
          note: values.note,
        },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      message.success("Tạo chương trình tiêm chủng thành công!");
      setCreateVisible(false);
      fetchProgram();
    } catch (error) {
      message.error("Tạo chương trình tiêm chủng thất bại!");
    } finally {
      setLoading(false);
    }
  };

  const handleUpdate = async (values) => {
    setLoading(true);
    const token = localStorage.getItem("token");
    try {
      await axios.put(
        `http://localhost:8080/api/admin/vaccine-program/${program.vaccineId}`,
        {
          vaccineName: values.vaccineName,
          manufacture: values.manufacture,
          description: values.description,
          vaccineDate: values.vaccineDate.format("YYYY-MM-DD"),
          note: values.note,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      message.success("Cập nhật chương trình thành công!");
      setCreateVisible(false);
      setEditMode(false);
      fetchProgram();
    } catch (error) {
      message.error("Cập nhật chương trình thất bại!");
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (programId) => {
    const result = await Swal.fire({
      title: "Bạn có chắc muốn xóa chương trình này?",
      text: "Hành động này không thể hoàn tác!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      cancelButtonColor: "#3085d6",
      confirmButtonText: "Xóa",
      cancelButtonText: "Hủy",
    });

    if (result.isConfirmed) {
      const token = localStorage.getItem("token");
      try {
        await axios.delete(
          `http://localhost:8080/api/admin/vaccine-program/${programId}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        await Swal.fire(
          "Đã xóa!",
          "Chương trình đã được xóa thành công.",
          "success"
        );
        fetchProgram();
      } catch {
        Swal.fire("Lỗi", "Xóa thất bại!", "error");
      }
    }
  };

  const handleViewResult = async (programId) => {
    setResultLoading(true);
    setResultVisible(true);
    try {
      const token = localStorage.getItem("token");
      const res = await axios.get(
        "http://localhost:8080/api/admin/vaccine-results-status-by-program",
        { headers: { Authorization: `Bearer ${token}` } }
      );
      // Lọc kết quả theo programId
      const filtered = res.data.filter((item) => item.programId === programId);
      setResultData(filtered);
    } catch (err) {
      setResultData([]);
    } finally {
      setResultLoading(false);
    }
  };

  const handleUpdateStatus = async (vaccineId, status) => {
    const token = localStorage.getItem("token");
    try {
      await axios.patch(
        `http://localhost:8080/api/admin/vaccine-program/${vaccineId}?status=${status}`,
        {},
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      message.success("Cập nhật trạng thái thành công!");
      fetchProgram();
    } catch (error) {
      message.error("Cập nhật trạng thái thất bại!");
    }
  };

  const handleCreateResult = async (values) => {
    setCreateResultLoading(true);
    const token = localStorage.getItem("token");
    try {
      await axios.post(
        "http://localhost:8080/api/nurse/vaccine-result",
        {
          statusHealth: values.statusHealth,
          resultNote: values.resultNote,
          reaction: values.reaction,
          createdAt: new Date().toISOString(),
          vaccineFormId: program.vaccineId, // hoặc trường phù hợp với backend
          // Thêm các trường khác nếu cần
        },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      message.success("Tạo kết quả thành công!");
      setCreateResultVisible(false);
      resultForm.resetFields();
    } catch (error) {
      message.error("Tạo kết quả thất bại!");
    } finally {
      setCreateResultLoading(false);
    }
  };

  // Lọc danh sách theo tên chương trình và ngày tiêm
  const filteredPrograms = programs.filter((program) => {
    const matchName =
      typeof program.vaccineName === "object"
        ? program.vaccineName?.vaccineName
            ?.toLowerCase()
            .includes(searchTerm.toLowerCase())
        : typeof program.vaccineName === "string"
        ? program.vaccineName.toLowerCase().includes(searchTerm.toLowerCase())
        : false;
    const matchDate = filterDate
      ? dayjs(program.vaccineDate).isSame(filterDate, "day")
      : true;
    const matchStatus = filterStatus ? program.status === filterStatus : true;
    return matchName && matchDate && matchStatus;
  });

  // Phân trang
  const pagedPrograms = filteredPrograms.slice(
    (currentPage - 1) * pageSize,
    currentPage * pageSize
  );

  // Reset trang về 1 khi filter/search thay đổi
  useEffect(() => {
    setCurrentPage(1);
  }, [searchTerm, filterDate, filterStatus]);

  // Thêm hàm lấy màu theo trạng thái
  const getStatusColor = (status) => {
    switch (status) {
      case "NOT_STARTED":
        return "default";
      case "ON_GOING":
        return "blue";
      case "COMPLETED":
        return "green";
      default:
        return "default";
    }
  };

  // Hàm ánh xạ trạng thái sang tiếng Việt
  const getStatusText = (status) => {
    switch (status) {
      case "NOT_STARTED":
        return "Chưa bắt đầu";
      case "ON_GOING":
        return "Đang diễn ra";
      case "COMPLETED":
        return "Đã hoàn thành";
      default:
        return status;
    }
  };

  const handleShowResultPage = async () => {
    setResultPageLoading(true);
    setShowResultPage(true);
    try {
      const token = localStorage.getItem("token");
      const res = await axios.get(
        "http://localhost:8080/api/admin/vaccine-results-status-by-program",
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setResultPageData(res.data);
    } catch (err) {
      setResultPageData([]);
    } finally {
      setResultPageLoading(false);
    }
  };

  const testResultData = [
    {
      key: 1,
      programName: "Chương trình tiêm chủng 1",
      statusHealth: "Tốt",
      count: 120,
    },
    {
      key: 2,
      programName: "Chương trình tiêm chủng 2",
      statusHealth: "Bình thường",
      count: 80,
    },
    {
      key: 3,
      programName: "Chương trình tiêm chủng 3",
      statusHealth: "Có phản ứng nhẹ",
      count: 10,
    },
  ];

  const resultColumns = [
    {
      title: "Tên chương trình",
      dataIndex: "programName",
      key: "programName",
    },
    {
      title: "Trạng thái sức khỏe",
      dataIndex: "statusHealth",
      key: "statusHealth",
    },
    {
      title: "Số lượng",
      dataIndex: "count",
      key: "count",
    },
  ];

  return (
    <div
      style={{
        padding: 24,
        marginLeft: 220,
        transition: "margin 0.2s",
        maxWidth: "100vw",
      }}
    >
      <Tabs
        activeKey={activeTab}
        onChange={setActiveTab}
        items={[
          {
            key: "program",
            label: "Chương trình tiêm chủng",
            children: (
              // --- Toàn bộ nội dung hiện tại của bạn ---
              <>
                {/* Header, filter, nút luôn hiển thị */}
                <div
                  style={{
                    display: "flex",
                    alignItems: "center",
                    marginBottom: 16,
                    justifyContent: "space-between",
                    width: "100%",
                  }}
                >
                  <h2
                    style={{
                      margin: 0,
                      flex: 1,
                      fontWeight: 700,
                      whiteSpace: "nowrap",
                    }}
                  >
                    <span style={{ color: "#52c41a", marginRight: 8 }}>🛡️</span>
                    Quản Lý Chương Trình Tiêm Chủng
                  </h2>
                  <div style={{ display: "flex", gap: 12, marginLeft: 24 }}>
                    <Input
                      placeholder="Tìm kiếm tên chương trình..."
                      prefix={<SearchOutlined />}
                      value={searchTerm}
                      onChange={(e) => setSearchTerm(e.target.value)}
                      allowClear
                      style={{ width: 220, background: "#fff" }}
                    />
                    <DatePicker
                      placeholder="Lọc theo ngày tiêm"
                      value={filterDate}
                      onChange={setFilterDate}
                      allowClear
                      style={{ width: 170 }}
                      format="YYYY-MM-DD"
                    />
                    <Select
                      placeholder="Lọc theo trạng thái"
                      value={filterStatus}
                      onChange={setFilterStatus}
                      allowClear
                      style={{ width: 170 }}
                      options={[
                        { value: "", label: "Tất cả trạng thái" },
                        { value: "NOT_STARTED", label: "Chưa bắt đầu" },
                        { value: "ON_GOING", label: "Đang diễn ra" },
                        { value: "COMPLETED", label: "Đã hoàn thành" },
                      ]}
                    />
                    {/* Ẩn nút lên lịch tiêm chủng nếu là NURSE */}
                    {userRole === "ADMIN" && (
                      <>
                        <Button
                          type="primary"
                          icon={<PlusOutlined />}
                          style={{ background: "#21ba45", border: "none" }}
                          onClick={() => setCreateVisible(true)}
                        >
                          Lên lịch tiêm chủng
                        </Button>
                        <Button
                          type="default"
                          style={{
                            border: "1px solid #21ba45",
                            color: "#21ba45",
                          }}
                          onClick={() => setAddVaccineVisible(true)}
                        >
                          Thêm mới vaccine
                        </Button>
                      </>
                    )}
                  </div>
                </div>
                {/* Danh sách chương trình hoặc thông báo rỗng */}
                <div style={{ minHeight: 350 }}>
                  {filteredPrograms.length === 0 ? (
                    <div
                      style={{
                        textAlign: "center",
                        color: "#888",
                        marginTop: 48,
                        fontSize: 18,
                      }}
                    >
                      Không có chương trình tiêm chủng nào.
                    </div>
                  ) : (
                    pagedPrograms.map((program) => (
                      <Card
                        key={program.vaccineId}
                        style={{
                          background: "#f6fcf7",
                          borderRadius: 10,
                          border: "1px solid #e6f4ea",
                          width: "calc(100vw - 260px)",
                          minWidth: 1200,
                          margin: "0 auto",
                          transition: "width 0.2s",
                          marginBottom: 16,
                        }}
                        bodyStyle={{ padding: 24 }}
                      >
                        <div
                          style={{
                            display: "flex",
                            justifyContent: "space-between",
                            alignItems: "flex-start",
                          }}
                        >
                          <div>
                            <div
                              style={{
                                fontWeight: 700,
                                fontSize: 18,
                                marginBottom: 4,
                              }}
                            >
                              {program.vaccineName?.vaccineName ||
                                program.vaccineName}
                            </div>
                            <div style={{ color: "#555", marginBottom: 2 }}>
                              Mô tả: {program.description}
                            </div>
                            <div style={{ color: "#555", marginBottom: 8 }}>
                              Ngày tiêm: {program.vaccineDate}
                            </div>
                          </div>
                          {/* Nếu là ADMIN thì cho phép chỉnh trạng thái, nếu là NURSE thì chỉ hiển thị Tag */}
                          {userRole === "ADMIN" ? (
                            <Select
                              value={program.status}
                              style={{
                                width: 160,
                                marginTop: 4,
                                fontWeight: 600,
                                color:
                                  program.status === "ON_GOING"
                                    ? "#1890ff"
                                    : program.status === "COMPLETED"
                                    ? "#21ba45"
                                    : "#595959",
                              }}
                              onChange={(status) =>
                                handleUpdateStatus(program.vaccineId, status)
                              }
                              options={[
                                {
                                  value: "NOT_STARTED",
                                  label: (
                                    <span style={{ color: "#595959" }}>
                                      Chưa bắt đầu
                                    </span>
                                  ),
                                },
                                {
                                  value: "ON_GOING",
                                  label: (
                                    <span style={{ color: "#1890ff" }}>
                                      Đang diễn ra
                                    </span>
                                  ),
                                },
                                {
                                  value: "COMPLETED",
                                  label: (
                                    <span style={{ color: "#21ba45" }}>
                                      Đã hoàn thành
                                    </span>
                                  ),
                                },
                              ]}
                              dropdownStyle={{ minWidth: 160 }}
                            />
                          ) : (
                            <Tag
                              color={getStatusColor(program.status)}
                              style={{ fontSize: 14, marginTop: 4 }}
                            >
                              {getStatusText(program.status)}
                            </Tag>
                          )}
                        </div>
                        <Row gutter={32} style={{ margin: "24px 0" }}>
                          <Col span={12}>
                            <div
                              style={{
                                background: "#fff",
                                borderRadius: 8,
                                padding: 16,
                                textAlign: "center",
                              }}
                            >
                              <div
                                style={{
                                  color: "#1890ff",
                                  fontWeight: 700,
                                  fontSize: 32,
                                }}
                              >
                                {program.totalStudents}
                              </div>
                              <div style={{ color: "#888", fontWeight: 500 }}>
                                Tổng học sinh
                              </div>
                            </div>
                          </Col>
                          <Col span={12}>
                            <div
                              style={{
                                background: "#fff",
                                borderRadius: 8,
                                padding: 16,
                                textAlign: "center",
                              }}
                            >
                              <div
                                style={{
                                  color: "#21ba45",
                                  fontWeight: 700,
                                  fontSize: 32,
                                }}
                              >
                                {program.confirmed}
                              </div>
                              <div style={{ color: "#888", fontWeight: 500 }}>
                                Đã xác nhận
                              </div>
                            </div>
                          </Col>
                        </Row>
                        <div
                          style={{
                            display: "flex",
                            justifyContent: "space-between",
                            alignItems: "center",
                          }}
                        >
                          <div>
                            <Button
                              onClick={() => {
                                setDetailVisible(true);
                                setProgram(program);
                              }}
                            >
                              Xem chi tiết
                            </Button>
                            {/* Đã xóa nút Tạo kết quả */}
                          </div>
                          {/* Ẩn nút Sửa, Xóa nếu là NURSE */}
                          {userRole === "ADMIN" && (
                            <div
                              style={{
                                display: "flex",
                                gap: 8,
                                marginLeft: "auto",
                              }}
                            >
                              {program.status === "NOT_STARTED" && (
                                <Button
                                  type="default"
                                  onClick={() => {
                                    setProgram(program);
                                    setEditMode(true);
                                    setCreateVisible(true);
                                  }}
                                >
                                  Sửa
                                </Button>
                              )}
                              <Button
                                danger
                                type="primary"
                                onClick={() => handleDelete(program.vaccineId)}
                              >
                                Xóa
                              </Button>
                            </div>
                          )}
                        </div>
                      </Card>
                    ))
                  )}
                </div>
                <div style={{ marginTop: 24, textAlign: "center" }}>
                  <Pagination
                    current={currentPage}
                    pageSize={pageSize}
                    total={filteredPrograms.length}
                    onChange={setCurrentPage}
                    showSizeChanger={false}
                  />
                </div>
                <Modal
                  title="Chi tiết chương trình tiêm chủng"
                  open={detailVisible}
                  onCancel={() => setDetailVisible(false)}
                  footer={[
                    <Button key="close" onClick={() => setDetailVisible(false)}>
                      Đóng
                    </Button>,
                  ]}
                >
                  {program && (
                    <Descriptions column={1} bordered size="small">
                      <Descriptions.Item label="Vaccine ID">
                        {program.vaccineId}
                      </Descriptions.Item>
                      <Descriptions.Item label="Tên vaccine">
                        {typeof program.vaccineName === "object"
                          ? program.vaccineName.vaccineName
                          : program.vaccineName}
                      </Descriptions.Item>
                      <Descriptions.Item label="Mô tả">
                        {program.description}
                      </Descriptions.Item>
                      <Descriptions.Item label="Ngày tiêm">
                        {program.vaccineDate}
                      </Descriptions.Item>
                      <Descriptions.Item label="Trạng thái">
                        <Tag
                          color={getStatusColor(program.status)}
                          style={{ fontSize: 14 }}
                        >
                          {getStatusText(program.status)}
                        </Tag>
                      </Descriptions.Item>
                      <Descriptions.Item label="Ghi chú">
                        {program.note}
                      </Descriptions.Item>
                    </Descriptions>
                  )}
                </Modal>
                <Modal
                  title={
                    editMode
                      ? "Sửa chương trình tiêm chủng"
                      : "Lên lịch tiêm chủng"
                  }
                  open={createVisible}
                  onCancel={() => {
                    setCreateVisible(false);
                    setEditMode(false);
                    setProgram(null);
                  }}
                  footer={null}
                  destroyOnClose
                >
                  <Form
                    layout="vertical"
                    onFinish={editMode ? handleUpdate : handleCreate}
                    initialValues={
                      editMode && program
                        ? {
                            vaccineName: program.vaccineName,
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
                          option.children
                            .toLowerCase()
                            .includes(input.toLowerCase())
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
                      rules={[{ required: true, message: "Chọn ngày tiêm" }]}
                    >
                      <DatePicker
                        style={{ width: "100%" }}
                        format="YYYY-MM-DD"
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
                </Modal>
                <Modal
                  title="Kết quả tiêm chủng"
                  open={resultVisible}
                  onCancel={() => setResultVisible(false)}
                  footer={[
                    <Button key="close" onClick={() => setResultVisible(false)}>
                      Đóng
                    </Button>,
                  ]}
                >
                  {resultLoading ? (
                    <div>Đang tải...</div>
                  ) : resultData.length === 0 ? (
                    <div>Không có dữ liệu kết quả cho chương trình này.</div>
                  ) : (
                    <Descriptions column={1} bordered size="small">
                      {resultData.map((item, idx) => (
                        <React.Fragment key={idx}>
                          <Descriptions.Item label="Trạng thái sức khỏe">
                            {item.statusHealth}
                          </Descriptions.Item>
                          <Descriptions.Item label="Số lượng">
                            {item.count}
                          </Descriptions.Item>
                        </React.Fragment>
                      ))}
                    </Descriptions>
                  )}
                </Modal>
                <Modal
                  title="Tạo kết quả tiêm chủng"
                  open={createResultVisible}
                  onCancel={() => {
                    setCreateResultVisible(false);
                    resultForm.resetFields();
                  }}
                  footer={null}
                  destroyOnClose
                >
                  <Form
                    form={resultForm}
                    layout="vertical"
                    onFinish={handleCreateResult}
                  >
                    <Form.Item
                      label="Tình trạng sức khỏe"
                      name="statusHealth"
                      rules={[
                        { required: true, message: "Nhập tình trạng sức khỏe" },
                      ]}
                    >
                      <Input />
                    </Form.Item>
                    <Form.Item
                      label="Phản ứng"
                      name="reaction"
                      rules={[{ required: true, message: "Nhập phản ứng" }]}
                    >
                      <Input />
                    </Form.Item>
                    <Form.Item label="Ghi chú kết quả" name="resultNote">
                      <Input.TextArea rows={2} />
                    </Form.Item>
                    <Form.Item>
                      <Button
                        type="primary"
                        htmlType="submit"
                        loading={createResultLoading}
                        style={{ width: "100%" }}
                      >
                        Tạo kết quả
                      </Button>
                    </Form.Item>
                  </Form>
                </Modal>
                <Modal
                  title="Thêm mới vaccine"
                  open={addVaccineVisible}
                  onCancel={() => {
                    setAddVaccineVisible(false);
                    addVaccineForm.resetFields();
                  }}
                  footer={null}
                  destroyOnClose
                >
                  <Form
                    form={addVaccineForm}
                    layout="vertical"
                    onFinish={async (values) => {
                      setAddVaccineLoading(true);
                      const token = localStorage.getItem("token");
                      try {
                        await axios.post(
                          "http://localhost:8080/api/admin/create-VaccineName", // Sửa lại endpoint này
                          {
                            vaccineName: values.vaccineName,
                            manufacture: values.manufacture,
                            note: values.note,
                          },
                          { headers: { Authorization: `Bearer ${token}` } }
                        );
                        Swal.fire({
                          icon: "success",
                          title: "Thêm vaccine mới thành công!",
                          showConfirmButton: false,
                          timer: 1500,
                        });
                        setAddVaccineVisible(false);
                        addVaccineForm.resetFields();
                        fetchVaccineList();
                      } catch {
                        Swal.fire({
                          icon: "error",
                          title: "Thêm vaccine mới thất bại!",
                        });
                      } finally {
                        setAddVaccineLoading(false);
                      }
                    }}
                    //     message.success("Thêm vaccine mới thành công!");
                    //     setAddVaccineVisible(false);
                    //     addVaccineForm.resetFields();
                    //     fetchVaccineList();
                    //   } catch {
                    //     message.error("Thêm vaccine mới thất bại!");
                    //   } finally {
                    //     setAddVaccineLoading(false);
                    //   }
                    // }}
                  >
                    <Form.Item
                      label="Tên vaccine"
                      name="vaccineName"
                      rules={[{ required: true, message: "Nhập tên vaccine" }]}
                    >
                      <Input />
                    </Form.Item>
                    <Form.Item label="Nhà sản xuất" name="manufacture">
                      <Input />
                    </Form.Item>
                    <Form.Item label="Ghi chú" name="note">
                      <Input.TextArea rows={2} />
                    </Form.Item>
                    <Form.Item>
                      <Button
                        type="primary"
                        htmlType="submit"
                        loading={addVaccineLoading}
                        style={{ width: "100%" }}
                      >
                        Thêm mới
                      </Button>
                    </Form.Item>
                  </Form>
                </Modal>
              </>
            ),
          },
          {
            key: "result",
            label: "Kết quả chương trình",
            children: (
              <div>
                {!showResultPage ? (
                  <Row gutter={32} style={{ marginTop: 24 }}>
                    <Col span={8}>
                      <Card
                        hoverable
                        style={{
                          textAlign: "center",
                          border: "1px solid #e6f4ea",
                          borderRadius: 10,
                        }}
                        onClick={() => setCreateResultVisible(true)}
                      >
                        <div
                          style={{
                            fontSize: 40,
                            color: "#1890ff",
                            marginBottom: 12,
                          }}
                        >
                          <PlusOutlined />
                        </div>
                        <div
                          style={{
                            fontWeight: 700,
                            fontSize: 20,
                            marginBottom: 8,
                          }}
                        >
                          Tạo kết quả
                        </div>
                        <div style={{ color: "#888" }}>
                          Nhập kết quả tiêm chủng mới cho học sinh
                        </div>
                      </Card>
                    </Col>
                    <Col span={8}>
                      <Card
                        hoverable
                        style={{
                          textAlign: "center",
                          border: "1px solid #e6f4ea",
                          borderRadius: 10,
                        }}
                        onClick={handleShowResultPage}
                      >
                        <div
                          style={{
                            fontSize: 40,
                            color: "#21ba45",
                            marginBottom: 12,
                          }}
                        >
                          <SearchOutlined />
                        </div>
                        <div
                          style={{
                            fontWeight: 700,
                            fontSize: 20,
                            marginBottom: 8,
                          }}
                        >
                          Xem kết quả
                        </div>
                        <div style={{ color: "#888" }}>
                          Xem danh sách kết quả tiêm chủng đã nhập
                        </div>
                      </Card>
                    </Col>
                  </Row>
                ) : (
                  <div style={{ marginTop: 24 }}>
                    <Button
                      onClick={() => setShowResultPage(false)}
                      style={{ marginBottom: 16 }}
                    >
                      Quay lại
                    </Button>
                    {/* Hiển thị table với dữ liệu cứng để test */}
                    <div
                      style={{
                        background: "#fff",
                        borderRadius: 10,
                        padding: 24,
                        minWidth: 1200,
                        maxWidth: "calc(100vw - 260px)", // 260px là sidebar
                        margin: "0 auto",
                        boxShadow: "0 2px 8px rgba(0,0,0,0.05)",
                        overflowX: "auto",
                      }}
                    >
                      <Table
                        columns={resultColumns}
                        dataSource={testResultData}
                        pagination={false}
                        bordered
                        style={{ minWidth: 900 }}
                      />
                    </div>
                  </div>
                )}
              </div>
            ),
          },
        ]}
      />
    </div>
  );
};

export default VaccineProgramList;
