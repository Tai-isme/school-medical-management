export async function createMedicalRequest(data) {
  const token = localStorage.getItem('token'); // hoặc nơi bạn lưu token
  const response = await fetch('http://localhost:8080/api/parent/medical-request', {
    method: 'POST',
    headers: { 
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}` // thêm dòng này
    },
    body: JSON.stringify(data)
  });
  if (!response.ok) throw new Error('Gửi thất bại');
  return response.json();
}