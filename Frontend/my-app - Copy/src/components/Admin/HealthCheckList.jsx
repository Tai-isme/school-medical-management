export default function HealthCheckList({ onSelect }) {
  // Dữ liệu mẫu cứng
  const students = [
    {
      id: 1,
      name: "Nguyễn Văn A",
      className: "10A1",
    },
  ];

  return (
    <div className="student-list">
      <h3>Danh sách khám định kỳ</h3>
      {students.map((student, index) => (
        <div
          key={index}
          onClick={() => onSelect(student)}
          className="student-item"
        >
          <strong>{student.name}</strong>
          <div style={{ fontSize: "12px", color: "#666" }}>
            {student.className}
          </div>
        </div>
      ))}
    </div>
  );
}
