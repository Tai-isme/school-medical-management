import React, { useEffect } from 'react';
import { Radio, Input, Button, Descriptions, Divider, Alert, Typography, Space } from 'antd';

const { Title, Text } = Typography;

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
  // Tự động tick radio dựa vào commit khi notification thay đổi
  useEffect(() => {
    if (notification?.commit === true) {
      setChecked('agree');
    } else if (notification?.commit === false) {
      setChecked('disagree');
    } else {
      setChecked(null);
    }
  }, [notification, setChecked]);

  // Ensure the URL is absolute
  const vaccineUrl = notification.vaccineProgram?.vaccineName.url?.startsWith("http")
    ? notification.vaccineProgram?.vaccineName.url
    : `https://${notification.vaccineProgram?.vaccineName.url}`;

  return (
    <div style={{ padding: 12 }}>
      <Title level={4} style={{ color: '#1976d2', marginBottom: 8 }}>
        Xác nhận miễn trừ trách nhiệm tiêm chủng
      </Title>
      <Alert
        message={
          <span>
            <b>Lưu ý:</b> Quý phụ huynh không đăng ký (<b>{notification.vaccineProgram?.vaccineName.vaccineName}</b>) cho con vui lòng bỏ qua khảo sát này.
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
        <Descriptions.Item label="Tên vắc xin">
          {notification.vaccineProgram?.vaccineName.vaccineName}
        </Descriptions.Item>
        <Descriptions.Item label="Nhà sản xuất">
          {notification.vaccineProgram?.vaccineName.manufacture}
        </Descriptions.Item>
        <Descriptions.Item label="Ngày tiêm dự kiến">
          {notification.vaccineProgram?.vaccineDate}
        </Descriptions.Item>
        <Descriptions.Item label="Thông tin thêm">
          <a
            href={vaccineUrl}
            target="_blank"
            rel="noopener noreferrer"
            style={{ color: '#1890ff', textDecoration: 'underline' }}
          >
            Xem thêm về vắc xin
          </a>
        </Descriptions.Item>
        <Descriptions.Item label="Ghi chú của phụ huynh">
          <Text type="secondary">{parentNote || "Không có"}</Text>
        </Descriptions.Item>
      </Descriptions>
      <Divider />
      <Title level={5} style={{ color: '#21ba45', marginBottom: 8 }}>
        Nội dung xác nhận miễn trừ trách nhiệm
      </Title>
      <ol style={{ paddingLeft: 20, marginBottom: 16 }}>
        <li>
          Khi tôi đồng ý cho con tôi tham gia (<b>{notification.vaccineProgram?.vaccineName.vaccineName}</b>) được tổ chức vào ngày {notification.formDate} tại trường, nếu con tôi đủ điều kiện được tiêm, tôi đồng ý với đơn xác nhận này, miễn trừ trách nhiệm pháp lý và các giả định rủi ro.
        </li>
        <li>
          Tôi đã đọc và tìm hiểu các thông tin liên quan tới loại vắc xin được sử dụng cho học sinh trước khi đồng ý xác nhận cho con tiêm vắc xin tại trường.
        </li>
        <li>
          Tôi cam kết đã cho con ăn sáng đầy đủ (đối với học sinh tiêm ca sáng) trước khi tiêm và khai báo đầy đủ thông tin được yêu cầu trong thư mời về tình trạng sức khỏe của con.
        </li>
        <li>
          Tôi ý thức được việc đồng ý cho con tiêm vắc xin tại trường sẽ có nguy cơ gặp phải một số rủi ro như: sốc phản vệ, sốt, có vết sưng nóng đỏ tại vị trí tiêm, đau đầu, chóng mặt, đau bụng... và các vấn đề rủi ro khác trong y khoa có thể phát sinh. Tôi xin chấp nhận và tự chịu trách nhiệm nếu con tôi gặp các rủi ro trên trong khi tham gia tiêm chủng.
        </li>
        <li>
          Tôi xin đồng ý rằng nếu con tôi gặp chấn thương hoặc cần đến sự trợ giúp của y tế, phía Ban Tổ chức và nhà trường có thể sắp xếp việc điều trị và sơ tán khẩn cấp nếu cần thiết và tôi xin chịu trách nhiệm chi trả cho mọi chi phí y tế và chi phí xe cứu thương.
        </li>
        <li>
          Tôi chấp nhận trong trường hợp con tôi gặp rủi ro trong quá trình tiêm chủng, tôi sẽ không làm đơn khiếu nại nhà trường.
        </li>
      </ol>
      <Divider />
      <Text strong>
        Quý Phụ huynh có đồng ý với các điều khoản kể trên sau khi đã đọc kỹ đơn này và đồng ý miễn trừ trách nhiệm pháp lý và các giả định rủi ro cho Nhà trường? <Text type="danger">*</Text>
      </Text>
      <Radio.Group
        onChange={e => setChecked(e.target.value)}
        value={checked}
        style={{ margin: '12px 0', display: 'block' }}
      >
        <Space direction="vertical">
          <Radio value="agree" disabled={disabled}>
            Tôi đồng ý với đơn xác nhận này, miễn trừ trách nhiệm pháp lý và các giả định rủi ro cho Nhà trường
          </Radio>
          <Radio value="disagree" disabled={disabled}>
            Tôi không đồng ý với đơn xác nhận miễn trừ trách nhiệm này
          </Radio>
        </Space>
      </Radio.Group>
      {checked === 'disagree' && !disabled && (
        <Input.TextArea
          placeholder="Lý do (nếu có)"
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