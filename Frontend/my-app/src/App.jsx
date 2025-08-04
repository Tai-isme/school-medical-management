import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom';
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

function AppRoutes() {
  const [role, setRole] = useState(localStorage.getItem("role"));
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const onStorage = () => setRole(localStorage.getItem("role"));
    window.addEventListener("storage", onStorage);
    return () => window.removeEventListener("storage", onStorage);
  }, []);

  useEffect(() => {
    const interval = setInterval(() => {
      const currentRole = localStorage.getItem("role");
      if (currentRole !== role) setRole(currentRole);
    }, 10);
    return () => clearInterval(interval);
  }, [role]);

  useEffect(() => {
    if ((role === "ADMIN" || role === "NURSE") && location.pathname !== "/dashboard") {
      navigate("/dashboard", { replace: true });
    }
  }, [role, location.pathname, navigate]);

  return (
    <>
      {(!role || role === "PARENT") && <Navbar />}
      <div className="main-content">
        <Routes>
          <Route
            path="/"
            element={
              role === "ADMIN" || role === "NURSE"
                ? <Navigate to="/dashboard" replace />
                : <Home />
            }
          />
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
    </>
  );
}

function App() {
  return (
    <Router>
      <AppRoutes />
    </Router>
  );
}

export default App;