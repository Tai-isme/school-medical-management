import { useState } from "react";

export default function StudentList({ onSelect }) {
  const [query, setQuery] = useState("");
  const [openClasses, setOpenClasses] = useState({});

  // Dữ liệu mẫu cứng với tất cả trường là "1"
  const students = [
    {
      id: "12",
      name: "12",
      className: "12"
    },
    {
      id: "123",
      name: "123",
      className: "123"
    }
  ];
  // Lọc học sinh theo tên
  const filtered = students.filter(student =>
    student.name.toLowerCase().includes(query.toLowerCase())
  );

  // Nhóm học sinh theo lớp
  const grouped = filtered.reduce((acc, student) => {
    if (!acc[student.className]) acc[student.className] = [];
    acc[student.className].push(student);
    return acc;
  }, {});

  // Hàm toggle mở/đóng lớp
  const handleToggleClass = (className) => {
    setOpenClasses(prev => ({
      [className]: !prev[className] // chỉ mở 1 lớp, các lớp khác đóng
    }));
  };

  return (
    <div className="student-list">
      <h3>Danh sách học sinh</h3>
      <input
        type="text"
        placeholder="Tìm theo tên..."
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        style={{
          width: '100%',
          padding: '8px',
          marginBottom: '10px',
          border: '1px solid #ccc',
          borderRadius: '4px'
        }}
      />
      {filtered.length === 0 ? (
        <p>Không tìm thấy học sinh.</p>
      ) : (
        Object.keys(grouped).map(className => (
          <div key={className} style={{ marginBottom: '16px' }}>
            <div
              style={{
                fontWeight: 'bold',
                fontSize: '18px',
                margin: '8px 0',
                cursor: 'pointer',
                userSelect: 'none'
              }}
              onClick={() => handleToggleClass(className)}
            >
              Lớp {className} {openClasses[className] ? "▲" : "▼"}
            </div>
            {openClasses[className] && grouped[className].map((student, i) => (
              <div
                key={student.id || i}
                onClick={() => onSelect(student)}
                className="student-item"
                style={{ cursor: 'pointer', paddingLeft: '12px', marginBottom: '4px' }}
              >
                <div>{student.name}</div>
              </div>
            ))}
          </div>
        ))
      )}
    </div>
  );
}
