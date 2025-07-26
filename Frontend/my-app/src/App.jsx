import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import BlogPage from './pages/BlogPage';
import InstructionFormPage from './pages/InstructionFormPage';
import StudentProfilePage from './pages/StudentProfilePage';
import MedicalIncidentPage from './pages/MedicalIncidentPage';
import NotificationPage from './pages/NotificationPage';
import VaccineResultPage from './pages/VaccineResultPage';
import HealthCheckResultPage from './pages/HealthCheckResultPage';
import ProtectedRoute from './routes/ProtectedRoute';
import Navbar from "./components/Layout/Navbar/Navbar";
import '@fontsource/poppins/400.css';
import '@fontsource/poppins/600.css';
import '@fontsource/roboto/400.css';
import Footer from './components/Layout/Footer/Footer';


function App() {
  const [role, setRole] = useState(localStorage.getItem("role"));

  // Theo dõi thay đổi role trong localStorage (khi login/logout)
  useEffect(() => {
    const onStorage = () => setRole(localStorage.getItem("role"));
    window.addEventListener("storage", onStorage);
    return () => window.removeEventListener("storage", onStorage);
  }, []);

  // Khi login/logout trong cùng tab, cập nhật role
  useEffect(() => {
    const interval = setInterval(() => {
      const currentRole = localStorage.getItem("role");
      if (currentRole !== role) setRole(currentRole);
    }, 10);
    return () => clearInterval(interval);
  }, [role]);

  return (
    <Router>
      {/* Navbar chỉ hiện khi chưa đăng nhập hoặc không phải admin */}
      {(!role || role === "PARENT") && <Navbar />}
      <div className="main-content">
      <Routes>
        <Route path="/" element={<Home />} /> 
        <Route path="/blog" element={<BlogPage />} /> 
        <Route path="/instruction-form" element={<InstructionFormPage />} />
        <Route path="/student-profile" element={<StudentProfilePage />} />
        <Route path="/medical-incident" element={<MedicalIncidentPage />} />
        <Route path="/notification" element={<NotificationPage />} />
        <Route path="/vaccine-result" element={<VaccineResultPage />} />
        <Route path="/health-check" element={<HealthCheckResultPage />} />
        <Route path="/dashboard" element={<ProtectedRoute />} />
      </Routes>
      </div>
      {(!role || role === "PARENT") && <Footer />}
    </Router>
  ); 
}

export default App;