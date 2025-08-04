import React, { useState, useEffect } from 'react';
import { auth, googleProvider } from '../../../config/firebase'
import { signInWithPopup } from "firebase/auth";
import { useNavigate } from "react-router-dom";
import { FcGoogle } from "react-icons/fc";

import './Login.css'; // Đảm bảo import file CSS
import {urlServer} from '../../../api/urlServer';

function Login({ onClose }) {
    const [username, setUsername] = useState('');
    const [phone, setPhone] = useState('');
    const [password, setPassword] = useState('');
    const [otp, setOtp] = useState('');
    const [isOtpSent, setIsOtpSent] = useState(false);
    const [selectedRole, setSelectedRole] = useState('parent');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        setUsername('');
        setPassword('');
        setPhone('');
        setOtp('');
        setIsOtpSent(false);
        setError('');
    }, [selectedRole]);

    const Close = () => {
        onClose();
    };

    const handleSendOTP = () => {
        console.log("Gửi mã OTP đến: " + phone);
        setIsOtpSent(true);
    };

    const handleVerifyOTP = () => {
        console.log("Xác thực OTP: " + otp);
        // Thêm logic xác thực OTP thực tế ở đây
    };

    const handleLoginUsernamePassword = async () => {
        try {
            const response = await fetch(`${urlServer}/api/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: username, password: password }),
            });

            const data = await response.json();
            if (!response.ok) {
                setError(data.message || 'Tên đăng nhập hoặc mật khẩu không đúng!');
                console.error(data.message || 'Lỗi khi đăng nhập bằng tài khoản và mật khẩu.');
                return;
            }

            if (data.token && data.users) {
                // Sau khi lưu localStorage và đóng modal đăng nhập:
                localStorage.setItem('token', data.token);
                localStorage.setItem('users', JSON.stringify(data.users));
                localStorage.setItem('students', JSON.stringify(data.students));
                localStorage.setItem('role', data.users.role);
                window.dispatchEvent(new Event("user-login")); // <-- Thêm dòng này
                onClose();

                const role = data.users.role;
                console.log("Role:", role);

                if (role === "ADMIN" || role === "NURSE") {
                    navigate("/dashboard");
                } else if (role === "PARENT") {
                    navigate("/");
                }
            } else {
                setError("Tài khoản không tồn tại hoặc thông tin không chính xác!");
            }
        } catch (error) {
            setError("Đăng nhập thất bại. Vui lòng kiểm tra kết nối mạng hoặc thông tin đăng nhập.");
            console.error("Lỗi đăng nhập:", error);
        }
    };

    const handleGoogleLogin = async () => {
        console.log("Đăng nhập bằng Google");
        try {
            const result = await signInWithPopup(auth, googleProvider);
            const user = result.user;
            const idToken = await user.getIdToken();
            console.log("ID Token:", idToken);

            const response = await fetch(`${urlServer}/api/auth/google`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ idToken }),
            });

            const data = await response.json();
            if (!response.ok) {
                setError(data.message || 'Lỗi khi gọi API backend với Google Login');
                console.error(data.message || 'Lỗi khi gọi API backend với Google Login');
                return;
            }

            if (data.token && data.users) {
                // Sau khi lưu localStorage và đóng modal đăng nhập:
                localStorage.setItem('token', data.token);
                localStorage.setItem('users', JSON.stringify(data.users));
                localStorage.setItem('students', JSON.stringify(data.students));
                localStorage.setItem('role', data.users.role);
                window.dispatchEvent(new Event("user-login")); // <-- Thêm dòng này
                onClose();

                const role = data.users.role;
                console.log("Role:", role);

                if (role === "ADMIN" || role === "NURSE") {
                    navigate("/dashboard");
                } else if (role === "PARENT") {
                    navigate("/");
                }
            } else {
                setError("Tài khoản không tồn tại!");
            }
        } catch (error) {
            setError("Đăng nhập Google thất bại! " + error.message);
            console.error("Lỗi Firebase hoặc API:", error);
        }
    };

    return (
        <div className="modal-overlay"> {/* Sử dụng class từ CSS */}
            <div className="modal-content"> {/* Sử dụng class từ CSS */}
                <div className="modal-image"> {/* Sử dụng class từ CSS */}
                    <img
                        src="../../../../public/logo.png"
                        alt="School Health Logo"
                    />
                </div>

                <h2 className="login-title">Đăng nhập</h2>

                {error && <p className="error-message">{error}</p>}

                <div className="form-input-button-group">
                    <input
                        type="text"
                        placeholder="Email"
                        className="form-input"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                    <input
                        type="password"
                        placeholder="Mật khẩu"
                        className="form-input"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                    <button
                        className="primary-button login-button"
                        onClick={handleLoginUsernamePassword}
                    >
                        Đăng nhập
                    </button>
                    <button
                        className="google-login-button"
                        onClick={handleGoogleLogin}
                    >
                        <FcGoogle size={20} /> Đăng nhập bằng Google
                    </button>
                </div>

                <button className="btn-close-login-form" onClick={Close}>X</button> {/* Sử dụng class từ CSS */}
            </div>
        </div>
    );
}

export default Login;