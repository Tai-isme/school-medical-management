// filepath: src/api/studentApi.js
import axios from 'axios';

export const fetchMedicalRecords = async (token) => {
  return axios.get('http://localhost:8080/api/parent/medical-records', {
    headers: { Authorization: `Bearer ${token}` }
  });
};