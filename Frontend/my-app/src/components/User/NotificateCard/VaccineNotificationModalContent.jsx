import React from 'react';
import { Radio, Input, Button } from 'antd';

const VaccineNotificationModalContent = ({
  notification,
  checked,
  setChecked,
  reason,
  setReason,
  onSubmit, // nhận prop mới
  loading,  // nếu muốn hiển thị trạng thái loading khi gửi
  disabled, // nhận prop disabled từ cha
}) => (
  <div>
    <p>
      Quý Phụ Huynh không đăng ký (<b>{notification.vaccineProgram?.vaccineName.vaccineName}</b>) cho con vui lòng bỏ qua khảo sát này.
      <br />
      <br />
      Kính gửi quý Phụ Huynh,
      <br />
      Nhà trường kính gửi quý Phụ huynh <b>XÁC NHẬN MIỄN TRỪ TRÁCH NHIỆM CHO HỌC SINH THAM GIA ({notification.vaccineProgram?.vaccineName.vaccineName})</b>
      <br />
      Văn bản quan trọng này có thể ảnh hưởng tới quyền lợi và nghĩa vụ của quý phụ huynh.
    </p>
    <ol>
      <li>
        Khi tôi đồng ý cho con tôi tham gia (<b>{notification.vaccineProgram?.vaccineName.vaccineName.vaccineName}</b>) được tổ chức vào ngày {notification.formDate} tại trường TH, THCS & THPT Vinschool (cơ sở Grand Park), nếu con tôi đủ điều kiện được tiêm, tôi đồng ý với đơn xác nhận này, miễn trừ trách nhiệm pháp lý và các giả định rủi ro.
      </li>
      <li>
        Tôi đã đọc và tìm hiểu các thông tin liên quan tới loại vắc xin được sử dụng cho học sinh trước khi đồng ý xác nhận cho con tiêm vắc xin tại trường
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
    <p>
      Tôi, với tư cách phụ huynh/người giám hộ hợp pháp của học sinh, tự nguyện cho phép và chấp thuận việc con tôi tham gia tiêm chủng phòng, chống dịch Sởi tại nhà trường. Cá nhân tôi và đại diện cho con tôi, tôi đồng ý với các điều khoản kể trên sau khi đã đọc kĩ đơn này.
    </p>
    <p>
      Quý Phụ huynh có đồng ý với các điều khoản kể trên sau khi đã đọc kĩ đơn này và đồng ý miễn trừ trách nhiệm pháp lý và các giả định rủi ro cho Nhà trường; (* bắt buộc)
    </p>
    <Radio.Group 
      onChange={e => setChecked(e.target.value)}
      value={checked}
      style={{ marginBottom: 8 }}
    >
      <Radio  value="agree" disabled={disabled}>Tôi đồng ý với đơn xác nhận này, miễn trừ trách nhiệm pháp lý và các giả định rủi ro cho Nhà trường</Radio>
      <Radio value="disagree" disabled={disabled}>Tôi không đồng ý với đơn xác nhận miễn trừ trách nhiệm này</Radio>
    </Radio.Group>
    {checked === 'disagree' && (
      <Input.TextArea
        placeholder="Lý do (nếu có)"
        value={reason}
        onChange={e => setReason(e.target.value)}
        rows={3}
      />
    )}
    <div style={{ textAlign: 'right', marginTop: 16 }}>
      <Button
        type="primary"
        onClick={() => onSubmit && onSubmit({ checked, reason, notification })}
        disabled={disabled || !checked}
        loading={loading}
      >
        Gửi
      </Button>
    </div>
  </div>
);

export default VaccineNotificationModalContent;