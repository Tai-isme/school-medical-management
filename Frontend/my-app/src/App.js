import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import InstructionFormPage from './pages/InstructionFormPage';
import StudentProfilePage from './pages/StudentProfilePage';
import MedicalIncidentPage from './pages/MedicalIncidentPage';
import NotificationPage from './pages/NotificationPage';
import ProtectedRoute from './routes/ProtectedRoute';
import Navbar from './components/Navbar/Navbar';
import '@fontsource/poppins/400.css';
import '@fontsource/poppins/600.css';
import '@fontsource/roboto/400.css';

function App() {
  // Lấy role từ localStorage
  const role = localStorage.getItem("role");

  return (
    <Router>
      {/* Chỉ render Navbar nếu role là ADMIN hoặc NURSE */}
      <Navbar/>
      <Routes>
        <Route path="/" element={<Home />} /> 
        <Route path="/instruction-form" element={<InstructionFormPage />} />
        <Route path="/student-profile" element={<StudentProfilePage />} />
        <Route path="/medical-incident" element={<MedicalIncidentPage />} />
        <Route path="/notification" element={<NotificationPage />} />
        <Route path="/dashboard" element={<ProtectedRoute />} />
      </Routes>
    </Router>
  );
}

export default App;