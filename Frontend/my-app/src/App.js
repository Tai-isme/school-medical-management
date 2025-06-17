// import React from 'react';
// import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
// import Home from './pages/Home';
// import InstructionFormPage from './pages/InstructionFormPage';
// import StudentProfilePage from './pages/StudentProfilePage';
// import MedicalIncidentPage from './pages/MedicalIncidentPage';
// import NotificationPage from './pages/NotificationPage';
// import AdminHome from './pages/AdminHome';
// import Navbar from './components/Navbar/Navbar';
// import ProtectedRoute from './routes/ProtectedRoute';
// import MedicalRecordDetail from "./components/Admin/MedicalRecordDetail";

// function App() {
//   return (
//     <Router>
//       <Routes>
//         <Route path="/" element={<Home />} /> 
//         <Route path="/instruction-form" element={<InstructionFormPage />} />
//         <Route path="/student-profile" element={<StudentProfilePage />} />
//         <Route path="/medical-incident" element={<MedicalIncidentPage />} />
//         <Route path="/notification" element={<NotificationPage />} />
//         <Route path="/dashboard" element={<AdminHome />} />
//         <Route path="/medical-record/:studentId" element={<MedicalRecordDetail />} />
//       </Routes>
//     </Router>
//   );
// }

// export default App;

import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import InstructionFormPage from './pages/InstructionFormPage';
import StudentProfilePage from './pages/StudentProfilePage';
import MedicalIncidentPage from './pages/MedicalIncidentPage';
import NotificationPage from './pages/NotificationPage';
// import các page khác nếu có
import AdminHome from './pages/AdminHome'; // Giả sử bạn có một trang AdminHome
import Navbar from './components/Navbar/Navbar';
import ProtectedRoute from './routes/ProtectedRoute'; // hoặc './components/common/ProtectedRoute'


function App() {
  return (
    <>
    <Router>
      <Routes>
        <Route path="/" element={<Home />} /> 
        <Route path="/instruction-form" element={<InstructionFormPage />} />
        <Route path="/student-profile" element={<StudentProfilePage />} />
        <Route path="/medical-incident" element={<MedicalIncidentPage />} />
        <Route path="/notification" element={<NotificationPage />} />
        <Route path="/dashboard" element={<ProtectedRoute />} />
      </Routes>
    </Router>

    {/* <AdminHome /> */}
    </>

  );
}

export default App;