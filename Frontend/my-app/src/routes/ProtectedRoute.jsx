import React from "react";
import { Navigate } from "react-router-dom";
import AdminHome from "../pages/AdminHome";
import Home from "../pages/Home";


export default function ProtectedRoute() {
  const role = JSON.parse(localStorage.getItem("role"));
  // const role = user?.role;


  if (role === "ADMIN" || role === "NURSE") {
    return <AdminHome />;
  }
  if (role === "PARENT") {
    return <Home />;
  }
  // Nếu chưa đăng nhập hoặc không có role, chuyển về trang chủ
  return <Navigate to="/" />;
}
