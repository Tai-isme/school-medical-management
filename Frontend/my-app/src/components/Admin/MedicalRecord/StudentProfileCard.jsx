import React, { useState, useEffect } from "react";
import { Card, Tabs, Form, Input, Row, Col, Avatar, Spin } from "antd";
import axios from "axios";
import { urlServer } from "../../../api/urlServer"; 

export default function StudentProfileCard({ studentId, studentInfo }) {
  const [tab, setTab] = useState("chronic");
  const [record, setRecord] = useState(null);
  const [vaccineHistories, setVaccineHistories] = useState([]);
  const [loading, setLoading] = useState(false);

  console.log("aaa" + urlServer);

  useEffect(() => {
    if (!studentId) return;
    setLoading(true);
    const fetchRecord = async () => {
      try {
        const token = localStorage.getItem("token");
        const res = await axios.get(
          `${urlServer}/api/admin/medical-records/${studentId}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        setRecord(res.data.medicalRecord);
        setVaccineHistories(res.data.vaccineHistories || []);
      } catch (err) {
        setRecord(null);
        setVaccineHistories([]);
      } finally {
        setLoading(false);
      }
    };
    fetchRecord();
  }, [studentId]);

  if (!studentId) return <Card>Chọn học sinh để xem hồ sơ</Card>;
  if (loading) return <Spin tip="Đang tải..." />;

  const safeRecord = record || {
    vision: "",
    hearing: "",
    weight: "",
    height: "",
    allergies: "",
    chronicDisease: "",
    treatmentHistory: "",
    note: "",
    studentId: "",
  };

  return (
    <Card
      style={{ borderRadius: 8, minHeight: 900, width: 800, margin: "0 auto" }}
      bodyStyle={{ padding: 24 }}
    >
      <Row gutter={32} align="middle" style={{ marginBottom: 16 }}>
        <Col>
          <Avatar
            src={
              studentInfo?.avatarUrl
                ? studentInfo.avatarUrl
                : "https://i.pinimg.com/1200x/8f/1c/a2/8f1ca2029e2efceebd22fa05cca423d7.jpg"
            }
            size={120}
            style={{ border: "2px solid #e0e0e0", background: "#fafafa" }}
          />
        </Col>
        <Col flex="auto">
          <div style={{ fontWeight: "bold", fontSize: 24, marginBottom: 6 }}>
            Mã số học sinh: {safeRecord.studentId}
          </div>
          <div style={{ fontSize: 18, marginBottom: 2 }}>
            Tên học sinh: {studentInfo?.fullName || ""}
          </div>
          <div style={{ fontSize: 16, marginBottom: 2 }}>
            Lớp: {studentInfo?.classDTO?.className || ""}
          </div>
          <div style={{ fontSize: 16 }}>
            Giới tính:{" "}
            {studentInfo?.gender === "MALE"
              ? "Nam"
              : studentInfo?.gender === "FEMALE"
              ? "Nữ"
              : "---"}
          </div>
        </Col>
      </Row>

      <Tabs
        activeKey={tab}
        onChange={setTab}
        items={[
          {
            key: "chronic",
            label: "Hồ sơ sức khỏe",
            children: (
              <Form
                layout="vertical"
                style={{ maxWidth: 700, margin: "0 auto" }}
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
                <Row gutter={16}>
                  <Col span={12}>
                    <Form.Item label="Thị giác">
                      <Input value={safeRecord.vision || ""} readOnly />
                    </Form.Item>
                  </Col>
                  <Col span={12}>
                    <Form.Item label="Thính lực">
                      <Input value={safeRecord.hearing || ""} readOnly />
                    </Form.Item>
                  </Col>
                </Row>
                <Row gutter={16}>
                  <Col span={12}>
                    <Form.Item label="Cân nặng">
                      <Input value={safeRecord.weight || ""} readOnly />
                    </Form.Item>
                  </Col>
                  <Col span={12}>
                    <Form.Item label="Chiều cao">
                      <Input value={safeRecord.height || ""} readOnly />
                    </Form.Item>
                  </Col>
                </Row>
                <Form.Item label="Bị dị ứng với các loại nào">
                  <Input.TextArea
                    value={safeRecord.allergies || ""}
                    readOnly
                    rows={3}
                  />
                </Form.Item>
                <Form.Item label="Bệnh mãn tính">
                  <Input.TextArea
                    value={safeRecord.chronicDisease || ""}
                    readOnly
                    rows={2}
                  />
                </Form.Item>
                {/* <Form.Item label="Lịch sử điều trị">
                  <Input.TextArea value={safeRecord.treatmentHistory || ""} readOnly  rows={2} />
                </Form.Item> */}
                <Form.Item label="Ghi chú">
                  <Input.TextArea
                    value={safeRecord.note || ""}
                    readOnly
                    rows={2}
                  />
                </Form.Item>
              </Form>
            ),
          },
          {
            key: "vaccine",
            label: "Các Vaccine đã tiêm",
            children: (
              <div
                style={{ background: "#f9fbfd", borderRadius: 8, padding: 12 }}
              >
                <div
                  style={{
                    textAlign: "center",
                    fontWeight: "bold",
                    fontSize: 20,
                    marginBottom: 12,
                  }}
                >
                  Các loại vaccin đã tiêm
                </div>
                <Row style={{ fontWeight: "bold", marginBottom: 8 }}>
                  <Col span={12}>Tên Vaccin</Col>
                  <Col span={12}>Mô tả</Col>
                </Row>
                {(vaccineHistories || []).map((v, idx) => (
                  <Row key={idx} gutter={8} style={{ marginBottom: 8 }}>
                    <Col span={12}>
                      <Input
                        value={v.vaccineNameDTO?.vaccineName || ""}
                        readOnly
                        style={{ fontSize: 16 }}
                      />
                    </Col>
                    <Col span={12}>
                      <Input.TextArea
                        value={v.note || ""}
                        readOnly
                        autoSize
                        style={{ fontSize: 16 }}
                      />
                    </Col>
                  </Row>
                ))}
              </div>
            ),
          },
        ]}
      />
    </Card>
  );
}
