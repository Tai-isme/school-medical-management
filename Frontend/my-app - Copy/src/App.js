import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import InstructionFormPage from './pages/InstructionFormPage';
import StudentProfilePage from './pages/StudentProfilePage';
import MedicalIncidentPage from './pages/MedicalIncidentPage';
import NotificationPage from './pages/NotificationPage';
// import các page khác nếu có
import Navbar from './components/Navbar/Navbar'; // Giả sử bạn có một component Navbar
import AdminHome from './pages/AdminHome'; // Giả sử bạn có một trang AdminHome

function App() {
  return (
    <>
    <Navbar/>
    <AdminHome />

    </>

    // <Router>
    //   <Routes>

    //     {/* <Route path="/" element={<Home />} /> */}
    //     {/* <Route path="/instruction-form" element={<InstructionFormPage />} />
    //     <Route path="/student-profile" element={<StudentProfilePage />} />
    //     <Route path="/medical-incident" element={<MedicalIncidentPage />} />
    //     <Route path="/notification" element={<NotificationPage />} /> */}
    //     {/* Thêm các route khác ở đây nếu cần */}
    //   </Routes>
    // </Router>
  );
}

export default App;