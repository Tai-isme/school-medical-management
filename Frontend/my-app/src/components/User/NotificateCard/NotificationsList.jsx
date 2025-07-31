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
                    ? `Khảo sát sức khỏe: ${notification.healthCheckProgramDTO?.healthCheckName || ''}`
                    : `Tiêm phòng: ${notification.vaccineProgramDTO?.vaccineProgramName || ''}`}
                </p>
              </div>
              <div className="notification-info-row-bottom">
                <p className="notification-date">
                  Ngày hết hạn: {notification.expDate}
                </p>
                <span
                  className="notification-status"
                  style={{
                    fontWeight: 'bold',
                    color: '#fff',
                    background:
                      notification.commit === true
                        ? '#52c41a'
                        : notification.commit === false
                        ? '#bfbfbf'
                        : new Date(notification.expDate) < new Date(new Date().toDateString()) && notification.commit == null
                        ? '#bfbfbf' // Màu xám cho "Không tham gia"
                        : new Date(notification.formDate) < new Date()
                        ? '#ff4d4f'
                        : '#1890ff',
                    borderRadius: 8,
                    padding: '2px 12px',
                    display: 'inline-block',
                  }}
                >
                  {
                    notification.commit === true
                      ? 'Đã đăng ký'
                      : notification.commit === false
                      ? 'Không tham gia'
                      : new Date(notification.expDate) < new Date(new Date().toDateString()) && notification.commit == null
                      ? 'Không tham gia'
                      : new Date(notification.formDate) < new Date()
                      ? 'Đã hết hạn'
                      : 'Chưa đăng ký'
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
        width={600} // Đổi từ 900 thành 600 để modal hẹp hơn
        centered
        title={
          modalNotification
            ? modalNotification.type === 'vaccine'
              ? 'Xác nhận miễn trừ trách nhiệm tiêm chủng'
              : 'Thông tin khảo sát sức khỏe'
            : ''
        }
      >
        {modalNotification && modalNotification.type === 'vaccine' && (
          <div style={{ maxWidth: 540, margin: '0 auto' }}>
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
          <div style={{ maxWidth: 540, margin: '0 auto' }}>
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