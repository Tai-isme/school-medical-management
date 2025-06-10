import React from "react";
import { Navigate } from "react-router-dom";
import AdminHome from "../pages/AdminHome";
import Home from "../pages/Home";

export default function ProtectedRoute() {
  const user = JSON.parse(localStorage.getItem("users"));
  const role = user?.role;

  if (role === "admin" || role === "nurse") {
    return <AdminHome />;
  }
  if (role === "parent") {
    return <Home />;
  }
  // Nếu chưa đăng nhập hoặc không có role, chuyển về trang chủ
  return <Navigate to="/" />;
}