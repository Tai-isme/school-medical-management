export const studentsData = [
  {
    student_id: 101,
    name: "Lê Hải Anh",
    className: "10a5",
    dob: "15/03/2019",
    gender: "Nam",
    health: {
      chronicType: "Dị ứng",
      description: "Khi ăn đậu hoặc đồ ăn liên quan đến đậu thì bị nổi mẩn đỏ.",
      solution: "Hạn chế món đồ ăn có đậu cho bé.",
    },
    incident: {
      type: "Ngã",
      date: "07/10/2020",
      action: "Sơ ý",
    },
    emergency: {
      name: "J97",
      relation: "Ba",
      phone: "0123456789",
      address: "số 22, xóm 6/thôn 19, Xã Vũ Ninh, H...",
    },
  },
  {
    student_id: 101,
    name: "Lê Hải Anh",
    className: "10a3",
    dob: "15/03/2019",
    gender: "Nam",
    health: {
      chronicType: "Dị ứng",
      description: "Khi ăn đậu hoặc đồ ăn liên quan đến đậu thì bị nổi mẩn đỏ.",
      solution: "Hạn chế món đồ ăn có đậu cho bé.",
    },
    incident: {
      type: "Ngã",
      date: "07/10/2020",
      action: "Sơ ý",
    },
    emergency: {
      name: "J97",
      relation: "Ba",
      phone: "0123456789",
      address: "số 22, xóm 6/thôn 19, Xã Vũ Ninh, H...",
    },
  },
];

export const medicalRecords = [
  {
    record_id: 1,
    student_id: 101,
    allergies: "Phấn hoa, đậu phộng",
    chronic_disease: "Hen suyễn",
    treatment_history: "Dùng thuốc hít định kỳ",
    vision: "9/10",
    hearing: "Tốt",
    weight: 28.5,
    high: 125,
    last_update: "2024-10-15",
    note: "Cần tránh vận động quá sức",
  },
];

export const healthChecks = [
  {
    student_id: 101,
    date: "2024-11-10",
    height: 125,
    weight: 28.5,
    vision: "9/10",
    hearing: "Tốt",
    teeth: "Bình thường",
    note: "Cần theo dõi cân nặng",
  },
];
