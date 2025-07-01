import React from "react";
import { Button, Card, Row, Col, Tag } from "antd";
import { PlusOutlined } from "@ant-design/icons";

const HealthCheckProgramList = () => {
  // Dữ liệu mẫu, bạn thay bằng API thực tế
  const program = {
    name: "Cúm mùa 2024",
    classes: ["1A", "1B", "2A", "2B"],
    date: "20/11/2024",
    totalStudents: 120,
    confirmed: 95,
    status: "Chờ thực hiện",
  };

  return (
    <div style={{ padding: 24, marginLeft: 220, transition: "margin 0.2s", maxWidth: "100vw" }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 16 }}>
        <h2 style={{ margin: 0 }}>
          <span style={{ color: "#52c41a", marginRight: 8 }}>🛡️</span>
          Quản Lý Chương Trình Khám Sức Khỏe
        </h2>
        <Button type="primary" icon={<PlusOutlined />} style={{ background: "#21ba45", border: "none" }}>
          Lên lịch khám sức khỏe
        </Button>
      </div>
      <Card
        style={{
          background: "#f6fcf7",
          borderRadius: 10,
          border: "1px solid #e6f4ea",
          width: "calc(100vw - 260px)", // kéo dài hết bên phải, trừ sidebar
          minWidth: 1200, // tăng minWidth nếu muốn
          margin: "0 auto",
          transition: "width 0.2s",
        }}
        bodyStyle={{ padding: 24 }}
      >
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
          <div>
            <div style={{ fontWeight: 700, fontSize: 18, marginBottom: 4 }}>{program.name}</div>
            <div style={{ color: "#555", marginBottom: 2 }}>
              Lớp: {program.classes.join(", ")}
            </div>
            <div style={{ color: "#555", marginBottom: 8 }}>
              Ngày khám: {program.date}
            </div>
          </div>
          <Tag color="blue" style={{ fontSize: 14, marginTop: 4 }}>{program.status}</Tag>
        </div>
        <Row gutter={32} style={{ margin: "24px 0" }}>
          <Col span={12}>
            <div style={{ background: "#fff", borderRadius: 8, padding: 16, textAlign: "center" }}>
              <div style={{ color: "#1890ff", fontWeight: 700, fontSize: 32 }}>120</div>
              <div style={{ color: "#888", fontWeight: 500 }}>Tổng học sinh</div>
            </div>
          </Col>
          <Col span={12}>
            <div style={{ background: "#fff", borderRadius: 8, padding: 16, textAlign: "center" }}>
              <div style={{ color: "#21ba45", fontWeight: 700, fontSize: 32 }}>95</div>
              <div style={{ color: "#888", fontWeight: 500 }}>Đã xác nhận</div>
            </div>
          </Col>
        </Row>
        <div style={{ display: "flex", gap: 12 }}>
          <Button>Gửi thông báo</Button>
          <Button type="primary" style={{ background: "#21ba45", border: "none" }}>
            Bắt đầu tiêm
          </Button>
        </div>
      </Card>
    </div>
  );
};

export default HealthCheckProgramList;
