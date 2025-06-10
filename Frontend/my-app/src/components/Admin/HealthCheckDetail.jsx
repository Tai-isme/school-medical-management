export default function HealthCheckDetail({ check }) {
  if (!check) {
    return <div style={{ padding: 20 }}>Chọn một học sinh để xem kết quả khám.</div>;
  }

  return (
    <div className="student-detail" style={{ flex: 1, padding: "20px" }}>
      {/* Dữ liệu sẽ được truyền từ API vào đây */}
    </div>
  );
}