import React, { useState } from 'react';
import { Modal } from 'antd';
import axios from 'axios';
import VaccineNotificationModalContent from './VaccineNotificationModalContent';
import HealthCheckNotificationModalContent from './HealthCheckNotificationModalContent';
import './NotificationsList.css';

const NotificationsList = ({ notifications, fetchNotifications }) => {
  const [modalOpen, setModalOpen] = useState(false);
  const [modalNotification, setModalNotification] = useState(null);
  const [checked, setChecked] = useState('');
  const [note, setNote] = useState('');
  const [loading, setLoading] = useState(false);

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
    const token = localStorage.getItem('token');
    try {
        await axios.patch(
          `http://localhost:8080/api/parent/vaccine-forms/${modalNotification.id}/commit`,
          {
            commit: checked === 'agree',
            note: note || '',
          },
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
      // Sau khi gửi thành công:
      if (fetchNotifications) await fetchNotifications();
      setModalOpen(false);
      setModalNotification(null);
      setChecked('');
      setNote('');
    } catch (error) {
      console.error(error);
    }
    setLoading(false);
  };

  const handleConfirmHealthCheck = async ({ checked, note, notification }) => {
    setLoading(true);
    const token = localStorage.getItem('token');
    try {
      await axios.patch(
        `http://localhost:8080/api/parent/health-check-forms/${notification.id}/commit`,
        {
          commit: checked === 'agree',
          note: note || '',
        },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      // Sau khi gửi thành công:
      if (fetchNotifications) await fetchNotifications();
      setModalOpen(false);
      setModalNotification(null);
      setChecked('');
      setNote('');
    } catch (error) {
      console.error(error);
    }
    setLoading(false);
  };

  const isExpired = modalNotification && new Date(modalNotification.formDate) < new Date();
  const isRegistered =
    modalNotification &&
    (
      modalNotification.commit === true ||
      modalNotification.commit === 'true' ||
      modalNotification.commit === false ||
      modalNotification.commit === 'false'
    );
  const disableSend = isExpired || isRegistered;

  return (
    <>
      <div className="notifications-list">
        {notifications && notifications.length > 0 ? (
          notifications.map((notification) => (
            <div
              className="notification-item"
              key={notification.id}
              onClick={() => handleModalOpen(notification)}
            >
              <div className="notification-title-row">
                <p
                  className="notification-type"
                  style={{
                    color: '#007bff',
                    marginBottom: 0,
                  }}
                >
                  {notification.type === 'healthcheck'
                    ? `Khảo sát sức khỏe: ${notification.healthCheckProgram?.name || ''}`
                    : `Tiêm phòng: ${notification.vaccineProgram?.vaccineName.vaccineName || ''}`}
                </p>
              </div>
              <div className="notification-info-row-bottom">
                <p className="notification-date">
                  Ngày: {notification.formDate}
                </p>
                <span
                  className="notification-status"
                  style={{
                    fontWeight: 'bold',
                    color: '#fff',
                    background:
                      new Date(notification.formDate) < new Date()
                        ? '#ff4d4f'
                        : notification.commit === 'true' || notification.commit === true
                        ? '#52c41a'
                        : notification.commit === 'false' || notification.commit === false
                        ? '#bfbfbf'
                        : '#1890ff',
                    borderRadius: 8,
                    padding: '2px 12px',
                    display: 'inline-block',
                  }}
                >
                  {
                    // Nếu formDate < ngày hiện tại => kiểm tra hết hạn
                    new Date(notification.formDate) < new Date()
                      ? 'Đã hết hạn'
                      : notification.commit === 'true' || notification.commit === true
                        ? 'Đã đăng ký'
                        : notification.commit === 'false' || notification.commit === false
                          ? 'Không tham gia'
                          : notification.commit == null
                            ? 'Chưa đăng ký'
                            : ''
                  }
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
        width={900} // Đặt chiều rộng modal là 1200px
        centered // Căn giữa modal trên màn hình
        title={
          modalNotification
            ? modalNotification.type === 'vaccine'
              ? 'Xác nhận miễn trừ trách nhiệm tiêm chủng'
              : 'Thông tin khảo sát sức khỏe'
            : ''
        }
      >
        {modalNotification && modalNotification.type === 'vaccine' && (
          <div style={{ maxWidth: 1100, margin: '0 auto' }}>
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
        {modalNotification && modalNotification.type === 'healthcheck' && (
          <div style={{ maxWidth: 1100, margin: '0 auto' }}>
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