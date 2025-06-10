export default function HealthCheckList({ students, onSelect }) {
  return (
    <div className="student-list">
      <h3>Danh sách khám định kỳ</h3>
      {/* Dữ liệu sẽ được truyền từ API vào đây */}
      {students && students.length > 0 && students.map((student, index) => (
        <div
          key={index}
          onClick={() => onSelect(student)}
          className="student-item"
        >
          <strong>{student.name}</strong>
          <div style={{ fontSize: '12px', color: '#666' }}>
            {student.className}
          </div>
        </div>
      ))}
    </div>
  );
}