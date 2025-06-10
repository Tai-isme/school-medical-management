export default function HealthCheckDetail() {
  // Dữ liệu mẫu cứng
  const check = {
    date: "2024-11-10",
    height: 125,
    weight: 28.5,
    vision: "9/10",
    hearing: "Tốt",
    teeth: "Bình thường",
    note: "1"
  };

  return (
    <div className="student-detail">
      <h2>Kết quả khám định kỳ</h2>
      <div className="section">
        <p><strong>Ngày khám:</strong> {check.date}</p>
        <p><strong>Chiều cao:</strong> {check.height} cm</p>
        <p><strong>Cân nặng:</strong> {check.weight} kg</p>
        <p><strong>Thị lực:</strong> {check.vision}</p>
        <p><strong>Thính lực:</strong> {check.hearing}</p>
        <p><strong>Tình trạng răng miệng:</strong> {check.teeth}</p>
        <p><strong>Ghi chú:</strong> {check.note}</p>
      </div>
    </div>
  );
}
