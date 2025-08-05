import React, { useEffect, useState } from 'react';
import { Checkbox, Input, Button, Descriptions, Divider, Alert, Typography, Space, Modal } from 'antd';
import VaccineConfirmContentModal from './VaccineConfirmContentModal'; // Import modal nội dung xác nhận

const { Text } = Typography;

const VaccineNotificationModalContent = ({
  notification,
  checked,
  setChecked,
  reason,
  setReason,
  onSubmit,
  loading,
  disabled,
  parentNote,
}) => {
  const [confirmModalOpen, setConfirmModalOpen] = useState(false);

  useEffect(() => {
    if (notification?.commit === true) {
      setChecked(true);
    } else if (notification?.commit === false) {
      setChecked(false);
    } else {
      setChecked(false);
    }
  }, [notification, setChecked]);

  return (
    <div style={{ padding: 12 }}>
      <Alert
        message={
          <span>
            <b>Lưu ý:</b> Quý phụ huynh không đăng ký (<b>{notification.vaccineProgramDTO?.vaccineNameDTO?.vaccineName || "--"}</b>) cho con vui lòng bỏ qua khảo sát này.
          </span>
        }
        type="info"
        showIcon
        style={{ marginBottom: 16 }}
      />
      <Descriptions
        bordered
        column={1}
        size="small"
        style={{ marginBottom: 16, background: '#fafcff', borderRadius: 8, padding: 8 }}
        labelStyle={{ width: 180, fontWeight: 500 }}
      >
        <Descriptions.Item label="Chương trình">
          {notification.vaccineProgramDTO?.vaccineProgramName || "--"}
        </Descriptions.Item>
        
                <Descriptions.Item label="Tên vắc xin">
          {notification.vaccineProgramDTO?.vaccineNameDTO?.vaccineName || "--"}
        </Descriptions.Item>
        <Descriptions.Item label="Mũi thực hiện">
          {notification.vaccineProgramDTO?.unit
            ? `${notification.vaccineProgramDTO.unit}`
            : "--"}
        </Descriptions.Item>
        <Descriptions.Item label="Nhà sản xuất">
          {notification.vaccineProgramDTO?.vaccineNameDTO?.manufacture || "--"}
        </Descriptions.Item>
        <Descriptions.Item label="Phác đồ tiêm">
  {notification.vaccineProgramDTO?.vaccineNameDTO?.totalUnit ? (
    <div>
      <b>Phác đồ {notification.vaccineProgramDTO.vaccineNameDTO.totalUnit} mũi:</b>
      <div style={{ marginTop: 8 }}>
        {notification.vaccineProgramDTO.vaccineNameDTO.vaccineUnitDTOs?.map((unitItem) => (
          <div key={unitItem.unit}>
            {notification.vaccineProgramDTO.unit === unitItem.unit ? (
              <b style={{ color: '#d4380d' }}>
                Mũi {unitItem.unit}: {unitItem.schedule}
              </b>
            ) : (
              <>Mũi {unitItem.unit}: {unitItem.schedule}</>
            )}
          </div>
        ))}
      </div>
    </div>
  ) : "--"}
</Descriptions.Item>
<Descriptions.Item label="Độ tuổi áp dụng">
          {notification.vaccineProgramDTO?.vaccineNameDTO?.ageFrom + " - " + notification.vaccineProgramDTO?.vaccineNameDTO?.ageTo + " (Tuổi)" || "--"}
        </Descriptions.Item>
        <Descriptions.Item label="Ngày tiêm dự kiến">
          {notification.vaccineProgramDTO?.startDate || "--"}
        </Descriptions.Item>
        <Descriptions.Item label="Địa điểm">
          {notification.vaccineProgramDTO?.location || "--"}
        </Descriptions.Item>
        <Descriptions.Item label="Người phụ trách">
          {notification.vaccineProgramDTO?.nurseDTO?.fullName || "--"}
          <br />
          <span style={{ marginLeft: 16 }}>
            <b>SĐT:</b> {notification.vaccineProgramDTO?.nurseDTO?.phone || "--"}
          </span>
          <br />
          <span style={{ marginLeft: 16 }}>
            <b>Email:</b> {notification.vaccineProgramDTO?.nurseDTO?.email || "--"}
          </span>
        </Descriptions.Item>
        <Descriptions.Item label="Ghi chú của phụ huynh">
          <Text type="secondary">{notification.note || "Không có"}</Text>
        </Descriptions.Item>
        <Descriptions.Item label="Mô tả">
          {notification.vaccineProgramDTO?.description || "--"}
        </Descriptions.Item>
      </Descriptions>
      <Divider />
      <Checkbox
        checked={checked}
        onChange={e => setChecked(e.target.checked)}
        disabled={disabled}
        style={{ marginBottom: 8 }}
      >
        Tôi đã đọc kỹ <a href="#" onClick={e => { e.preventDefault(); setConfirmModalOpen(true); }}>Nội dung xác nhận miễn trừ trách nhiệm</a> và đồng ý miễn trừ trách nhiệm pháp lý và các giả định rủi ro cho Nhà trường <Text type="danger">*</Text>
      </Checkbox>
      <Modal
        open={confirmModalOpen}
        onCancel={() => setConfirmModalOpen(false)}
        footer={null}
        width={700}
        centered
        title="Nội dung xác nhận miễn trừ trách nhiệm tiêm chủng"
      >
        <VaccineConfirmContentModal />
      </Modal>
      {checked && !disabled && (
        <Input.TextArea
          placeholder="Ghi chú (nếu có, ví dụ: yêu cầu đặc biệt cho học sinh của tôi)"
          value={reason}
          onChange={e => setReason(e.target.value)}
          rows={3}
          style={{ marginBottom: 12 }}
        />
      )}
      <div style={{ textAlign: 'right', marginTop: 16 }}>
        <Button
          type="primary"
          onClick={() => onSubmit && onSubmit({ checked, reason, notification })}
          disabled={disabled || !checked}
          loading={loading}
          style={{ minWidth: 100 }}
        >
          Gửi
        </Button>
      </div>
    </div>
  );
};

export default VaccineNotificationModalContent;