import React, { useState, useMemo } from "react";
import { Modal, Input, Select, DatePicker, Row, Col, Space, Button } from "antd";
import axios from "axios";
import VaccineNotificationModalContent from "./VaccineNotificationModalContent";
import HealthCheckNotificationModalContent from "./HealthCheckNotificationModalContent";
import "./NotificationsList.css";
const { Option } = Select;
const { RangePicker } = DatePicker;

const STATUS_OPTIONS = [
  { label: "Tất cả", value: "" },
  { label: "CHƯA ĐĂNG KÝ", value: "pending" },
  { label: "ĐÃ ĐĂNG KÝ", value: "registered" },
  { label: "KHÔNG THAM GIA", value: "not_participate" },
];

const getStatus = (notification) => {
  if (
    notification.commit === true ||
    notification.commit === "true"
  )
    return "registered";
  if (
    notification.commit === false ||
    notification.commit === "false" ||
    (new Date(notification.expDate) < new Date(new Date().toDateString()) &&
      notification.commit == null)
  )
    return "not_participate";
  return "pending";
};

const NotificationsList = ({ notifications, fetchNotifications }) => {
  const [modalOpen, setModalOpen] = useState(false);
  const [modalNotification, setModalNotification] = useState(null);
  const [checked, setChecked] = useState("");
  const [note, setNote] = useState("");
  const [loading, setLoading] = useState(false);

  // Filter states
  const [search, setSearch] = useState("");
  const [status, setStatus] = useState("");
  const [dateRange, setDateRange] = useState([]);

  const handleModalOpen = (notification) => {
    setModalNotification(notification);
    setModalOpen(true);
  };

  const handleModalClose = () => {
    setModalOpen(false);
    setModalNotification(null);
  };

  const handleConfirmVaccine = async () => {
    setLoading(true);
    const token = localStorage.getItem("token");
    try {
      await axios.patch(
        `http://localhost:8080/api/parent/vaccine-forms/${modalNotification.id}/commit`,
        {
          commit: true,
          note: note || "",
        },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      if (fetchNotifications) await fetchNotifications();
      setModalOpen(false);
      setModalNotification(null);
      setChecked("");
      setNote("");
    } catch (error) {
      console.error(error);
    }
    setLoading(false);
  };

  const handleConfirmHealthCheck = async ({ checked, note, notification }) => {
    setLoading(true);
    const token = localStorage.getItem("token");
    try {
      await axios.patch(
        `http://localhost:8080/api/parent/health-check-forms/${notification.id}/commit`,
        {
          commit: true,
          note: note || "",
        },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      if (fetchNotifications) await fetchNotifications();
      setModalOpen(false);
      setModalNotification(null);
      setChecked("");
      setNote("");
    } catch (error) {
      console.error(error);
    }
    setLoading(false);
  };

  const isExpired =
    modalNotification && new Date(modalNotification.formDate) < new Date();
  const isRegistered =
    modalNotification &&
    (modalNotification.commit === true ||
      modalNotification.commit === "true" ||
      modalNotification.commit === false ||
      modalNotification.commit === "false");
  const disableSend = isExpired || isRegistered;

  // Filter logic
  const filteredNotifications = useMemo(() => {
    return (notifications || [])
      .filter((n) => {
        // Filter by search
        const eventName =
          n.type === "healthcheck"
            ? n.healthCheckProgramDTO?.healthCheckName || ""
            : n.vaccineProgramDTO?.vaccineProgramName || "";
        if (search && !eventName.toLowerCase().includes(search.toLowerCase()))
          return false;
        // Filter by status
        if (status && getStatus(n) !== status) return false;
        // Filter by date range (formDate)
        if (
          dateRange.length === 2 &&
          (new Date(n.formDate) < dateRange[0]._d ||
            new Date(n.formDate) > dateRange[1]._d)
        )
          return false;
        return true;
      });
  }, [notifications, search, status, dateRange]);

  const handleResetFilters = () => {
    setSearch("");
    setStatus("");
    setDateRange([]);
  };

  return (
    <>
      <div style={{ marginBottom: 24 }}>
        <Row gutter={16} align="middle">
          <Col xs={24} sm={12} md={8} lg={6}>
            <Input
              placeholder="Tìm theo tên sự kiện"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              allowClear
            />
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Select
              style={{ width: "100%" }}
              value={status}
              onChange={setStatus}
              placeholder="Lọc theo trạng thái"
              allowClear
            >
              {STATUS_OPTIONS.map((opt) => (
                <Option key={opt.value} value={opt.value}>
                  {opt.label}
                </Option>
              ))}
            </Select>
          </Col>
          <Col xs={24} sm={24} md={8} lg={8}>
            <RangePicker
              style={{ width: "100%" }}
              value={dateRange}
              onChange={setDateRange}
              format="DD/MM/YYYY"
              placeholder={["Từ ngày", "Đến ngày"]}
              allowClear
            />
          </Col>
          <Col xs={24} sm={24} md={24} lg={4}>
            <Button onClick={handleResetFilters}>Đặt lại bộ lọc</Button>
          </Col>
        </Row>
      </div>
      <div className="notifications-list">
        {filteredNotifications && filteredNotifications.length > 0 ? (
          filteredNotifications.map((notification) => (
            <div
              className="notification-item"
              key={notification.id}
              onClick={() => handleModalOpen(notification)}
            >
              <div className="notification-title-row">
                <p
                  className="notification-type"
                  style={{
                    color: "#007bff",
                    marginBottom: 0,
                  }}
                >
                  {notification.type === "healthcheck"
                    ? `Khảo sát sức khỏe: ${
                        notification.healthCheckProgramDTO?.healthCheckName ||
                        ""
                      }`
                    : `Tiêm phòng: ${
                        notification.vaccineProgramDTO?.vaccineProgramName || ""
                      }`}
                </p>
              </div>
              <div className="notification-info-row-bottom">
                <p className="notification-date">
                  Ngày hết hạn:{" "}
                  {notification.expDate
                    ? new Date(notification.expDate).toLocaleDateString(
                        "vi-VN",
                        {
                          day: "2-digit",
                          month: "2-digit",
                          year: "numeric",
                        }
                      )
                    : "---"}
                </p>
                <span
                  className="notification-status"
                  style={{
                    fontWeight: "bold",
                    color: "#fff",
                    background:
                      notification.commit === true
                        ? "#52c41a"
                        : notification.commit === false
                        ? "#bfbfbf"
                        : new Date(notification.expDate) <
                            new Date(new Date().toDateString()) &&
                          notification.commit == null
                        ? "#bfbfbf"
                        : new Date(notification.formDate) < new Date()
                        ? "#ff4d4f"
                        : "#1890ff",
                    borderRadius: 8,
                    padding: "2px 12px",
                    display: "inline-block",
                  }}
                >
                  {notification.commit === true
                    ? "Đã đăng ký"
                    : notification.commit === false
                    ? "Không tham gia"
                    : new Date(notification.expDate) <
                        new Date(new Date().toDateString()) &&
                      notification.commit == null
                    ? "Không tham gia"
                    : new Date(notification.formDate) < new Date()
                    ? "Đã hết hạn"
                    : "Chưa đăng ký"}
                </span>
              </div>
            </div>
          ))
        ) : (
          <div>Không có thông báo nào.</div>
        )}
      </div>
      <Modal
        open={modalOpen}
        onCancel={handleModalClose}
        footer={null}
        width={600}
        centered
        title={
          modalNotification
            ? modalNotification.type === "vaccine"
              ? "Xác nhận miễn trừ trách nhiệm tiêm chủng"
              : "Thông tin khảo sát sức khỏe"
            : ""
        }
      >
        {modalNotification && modalNotification.type === "vaccine" && (
          <div style={{ maxWidth: 540, margin: "0 auto" }}>
            <VaccineNotificationModalContent
              notification={modalNotification}
              checked={checked}
              setChecked={setChecked}
              reason={note}
              setReason={setNote}
              onSubmit={handleConfirmVaccine}
              loading={loading}
              disabled={disableSend}
              parentNote={modalNotification.note}
            />
          </div>
        )}
        {modalNotification && modalNotification.type === "healthcheck" && (
          <div style={{ maxWidth: 540, margin: "0 auto" }}>
            <HealthCheckNotificationModalContent
              notification={modalNotification}
              checked={checked}
              setChecked={setChecked}
              note={note}
              setNote={setNote}
              onSubmit={handleConfirmHealthCheck}
              loading={loading}
              disabled={disableSend}
            />
          </div>
        )}
      </Modal>
    </>
  );
};

export default NotificationsList;