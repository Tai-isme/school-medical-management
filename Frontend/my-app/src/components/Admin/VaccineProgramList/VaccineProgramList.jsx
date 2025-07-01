import React, { useEffect, useState } from "react";
import { Button, Card, Row, Col, Tag, Modal, Descriptions, Form, Input, DatePicker, message } from "antd";
import { PlusOutlined } from "@ant-design/icons";
import axios from "axios";
import dayjs from "dayjs";

const VaccineProgramList = () => {
  const [programs, setPrograms] = useState([]); // đổi thành mảng
  const [detailVisible, setDetailVisible] = useState(false);
  const [createVisible, setCreateVisible] = useState(false);
  const [loading, setLoading] = useState(false);
  const [program, setProgram] = useState(null); // chương trình đang xem chi tiết

  useEffect(() => {
    fetchProgram();
  }, []);

  const fetchProgram = async () => {
    const token = localStorage.getItem("token");
    try {
      const res = await axios.get(
        "http://localhost:8080/api/admin/vaccine-program", // API trả về danh sách
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setPrograms(res.data); // lưu mảng chương trình
    } catch (error) {
      setPrograms([]);
    }
  };

  const handleCreate = async (values) => {
    setLoading(true);
    const token = localStorage.getItem("token");
    try {
      await axios.post(
        "http://localhost:8080/api/admin/vaccine-program",
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
      message.success("Tạo chương trình tiêm chủng thành công!");
      setCreateVisible(false);
      fetchProgram();
    } catch (error) {
      message.error("Tạo chương trình tiêm chủng thất bại!");
    } finally {
      setLoading(false);
    }
  };

  if (!programs.length) return <div>Đang tải...</div>;

  return (
    <div style={{ padding: 24, marginLeft: 220, transition: "margin 0.2s", maxWidth: "100vw" }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 16 }}>
        <h2 style={{ margin: 0 }}>
          <span style={{ color: "#52c41a", marginRight: 8 }}>🛡️</span>
          Quản Lý Chương Trình Tiêm Chủng
        </h2>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          style={{ background: "#21ba45", border: "none" }}
          onClick={() => setCreateVisible(true)}
        >
          Lên lịch tiêm chủng
        </Button>
      </div>
      {programs.map((program) => (
        <Card
          key={program.vaccineId}
          style={{
            background: "#f6fcf7",
            borderRadius: 10,
            border: "1px solid #e6f4ea",
            width: "calc(100vw - 260px)", // kéo dài hết bên phải, trừ sidebar
            minWidth: 1200, // tăng minWidth nếu muốn
            margin: "0 auto",
            transition: "width 0.2s",
            marginBottom: 16,
          }}
          bodyStyle={{ padding: 24 }}
        >
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
            <div>
              <div style={{ fontWeight: 700, fontSize: 18, marginBottom: 4 }}>{program.vaccineName}</div>
              <div style={{ color: "#555", marginBottom: 2 }}>
                Mô tả: {program.description}
              </div>
              <div style={{ color: "#555", marginBottom: 8 }}>
                Ngày tiêm: {program.vaccineDate}
              </div>
            </div>
            <Tag color="blue" style={{ fontSize: 14, marginTop: 4 }}>{program.status}</Tag>
          </div>
          <Row gutter={32} style={{ margin: "24px 0" }}>
            <Col span={12}>
              <div style={{ background: "#fff", borderRadius: 8, padding: 16, textAlign: "center" }}>
                <div style={{ color: "#1890ff", fontWeight: 700, fontSize: 32 }}>{program.totalStudents}</div>
                <div style={{ color: "#888", fontWeight: 500 }}>Tổng học sinh</div>
              </div>
            </Col>
            <Col span={12}>
              <div style={{ background: "#fff", borderRadius: 8, padding: 16, textAlign: "center" }}>
                <div style={{ color: "#21ba45", fontWeight: 700, fontSize: 32 }}>{program.confirmed}</div>
                <div style={{ color: "#888", fontWeight: 500 }}>Đã xác nhận</div>
              </div>
            </Col>
          </Row>
          <div style={{ display: "flex", gap: 12 }}>
            <Button onClick={() => {
              setDetailVisible(true);
              setProgram(program); // set chương trình đang xem chi tiết
            }}>
              Xem chi tiết
            </Button>
            <Button type="primary" style={{ background: "#21ba45", border: "none" }}>
              Gửi thông báo
            </Button>
          </div>
        </Card>
      ))}
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
            <Descriptions.Item label="Vaccine ID">{program.vaccineId}</Descriptions.Item>
            <Descriptions.Item label="Tên vaccine">{program.vaccineName}</Descriptions.Item>
            <Descriptions.Item label="Mô tả">{program.description}</Descriptions.Item>
            <Descriptions.Item label="Ngày tiêm">{program.vaccineDate}</Descriptions.Item>
            <Descriptions.Item label="Trạng thái">{program.status}</Descriptions.Item>
            <Descriptions.Item label="Ghi chú">{program.note}</Descriptions.Item>
          </Descriptions>
        )}
      </Modal>
      <Modal
        title="Lên lịch tiêm chủng"
        open={createVisible}
        onCancel={() => setCreateVisible(false)}
        footer={null}
        destroyOnClose
      >
        <Form layout="vertical" onFinish={handleCreate}>
          <Form.Item label="Tên vaccine" name="vaccineName" rules={[{ required: true, message: "Nhập tên vaccine" }]}>
            <Input />
          </Form.Item>
          <Form.Item label="Nhà sản xuất" name="manufacture" rules={[{ required: true, message: "Nhập nhà sản xuất" }]}>
            <Input />
          </Form.Item>
          <Form.Item label="Mô tả" name="description">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item label="Ngày tiêm" name="vaccineDate" rules={[{ required: true, message: "Chọn ngày tiêm" }]}>
            <DatePicker style={{ width: "100%" }} format="YYYY-MM-DD" />
          </Form.Item>
          <Form.Item label="Ghi chú" name="note">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading} style={{ width: "100%" }}>
              Tạo chương trình
            </Button>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default VaccineProgramList;