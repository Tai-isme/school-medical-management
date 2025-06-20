import '../Login/Login.css';
import { useState, useEffect } from 'react';
import { auth, googleProvider } from '../../../src/config/firebase.js';
import { signInWithPopup } from "firebase/auth";
import { useNavigate } from "react-router-dom";


function Login({onClose}){
    // Khai báo các biến để nhận giá trị
    const [username, setUsername] = useState('')
    const [phone, setPhone] = useState('')
    const [password, setPassword] = useState('')
    const [otp, setOtp] = useState('')
    const [isOtpSent, setIsOtpSent] = useState(false);
    const [selectedRole, setSelectedRole] = useState('parent'); // Giá trị mặc định là 'parent'
    const [error, setError] = useState('');
    const [data, setData] = useState('');
    const navigate = useNavigate();


    useEffect(() => {
        setUsername(''); // Reset username và password khi chuyển sang parent
        setPassword('');
        setPhone(''); // Reset phoneNumber và otp khi chuyển sang admin
        setOtp('');
        setError(''); // Reset lỗi
  }, [selectedRole]);


    // Nút X đóng form login
    const Close = () =>  {
        onClose();
    }


    // Xác minh thông tin đăng nhập
    const handleSendOTP = (e) => {
      console.log("Gui ma OTP")
    }




     const handleLoginUsernamePassword = async () => {
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email:username, password: password }),
      });
  };


const handleGoogleLogin = async () => {
  console.log("Đăng nhập bằng Google");
  try {
    const result = await signInWithPopup(auth, googleProvider);
    const user = result.user;
    // Lấy idToken của user
    const idToken = await user.getIdToken();
    console.log("ID Token:", idToken);
    // Gửi idToken xuống API backend
    const response = await fetch('http://localhost:8080/api/auth/google', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ idToken }),
    });


    const data = await response.json();
    if (!response.ok) {
      setError(data.message || 'Lỗi khi gọi API backend');
      console.error(data.message || 'Lỗi khi gọi API backend');
      return;
    }


    // Call API backend thành công
   
    // const users = data.users;
    // console.log("User data:", users);
    // Lưu vào localStorage
    if(data.token && data.users) {
      localStorage.setItem('token', data.token);  
      localStorage.setItem('users', JSON.stringify(data.users));
      localStorage.setItem('students', JSON.stringify(data.students));
      localStorage.setItem('role', JSON.stringify(data.users.role));
      onClose();
      // Giả sử kết quả trả về có role
    const role = JSON.parse(localStorage.getItem('role')); // hoặc "nurse", "parent"
      console.log("Role:", role);
    // Chuyển hướng theo role
    if (role === "ADMIN" || role === "NURSE") {
      navigate("/dashboard");
    } else if (role === "PARENT") {
      navigate("/");
    }
    }else {
      setError("Tài khoản không tồn tại!");


    }  


   
  } catch (error) {
    setError("Đăng nhập Google thất bại!");
    console.log("1"+error)
  }
};


    return (
    <div className="modal-overlay">
          <div className="modal-content">


            <div className="modal-image">
              <img src="/logo1.png" alt="Anh" />
            </div>
            <div className="role-selection">
              <label>
                <input
                type="radio"
                name="role"
                value="parent"
                defaultChecked
                onChange={(e) => { setSelectedRole(e.target.value)}}
                />
                Phụ huynh
              </label>
              <label>
                <input
                type="radio"
                name="role"
                value="admin"
                onChange={(e) => { setSelectedRole(e.target.value)}}
                />
                Nhân viên y tế
              </label>
            </div>


            {selectedRole==='parent' ? (
                <>
                <div className="form-group">
                <label>Số điện thoại:</label>
                <input value={phone} onChange={(e) => {setPhone(e.target.value)}} type="text" placeholder="Nhập số điện thoại" />
              </div>
              <button  className="login-submit" onClick={handleSendOTP}>Gửi mã OTP</button>
                </>
            ): (
                <>
                <div className="form-group">
                <label>Tên đăng nhập:</label>
                <input
                type="text"
                placeholder="Tên đăng nhập"
                value={username}
                onChange={(e) => {setUsername(e.target.value)}}
                />
              </div>
              <div className="form-group">
                <label>Mật khẩu:</label>
                <input
                type="password"
                placeholder="Mật khẩu"
                value={password}
                onChange={(e) => {setPassword(e.target.value)}}
                />
              </div>
              <button onClick={handleLoginUsernamePassword} type="submit" className="login-submit">Đăng nhập</button>
                </>
            )}


            <button className="google-login" onClick={handleGoogleLogin}>Đăng nhập bằng tài khoản Google</button>
            <button className='btn-close-login-form' onClick={Close}>X</button>
          </div>
         
        </div>
    )
}


export default Login
