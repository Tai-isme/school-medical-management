import React, { useState } from "react";
import { CKEditor } from '@ckeditor/ckeditor5-react';
import ClassicEditor from '@ckeditor/ckeditor5-build-classic';

const categories = [
  { value: "news", label: "Thời sự" },
  { value: "sport", label: "Thể thao" },
  { value: "entertainment", label: "Giải trí" },
  // ... thêm các chuyên mục khác
];

const statusOptions = [
  { value: "draft", label: "Nháp" },
  { value: "pending", label: "Chờ duyệt" },
  { value: "published", label: "Đã xuất bản" },
  { value: "hidden", label: "Ẩn" },
];

export default function BlogAdminPage() {
  const [title, setTitle] = useState("");
  const [summary, setSummary] = useState("");
  const [content, setContent] = useState("");
  const [category, setCategory] = useState(categories[0].value);
  const [tags, setTags] = useState("");
  const [status, setStatus] = useState(statusOptions[0].value);
  const [thumbnail, setThumbnail] = useState(null);
  const [message, setMessage] = useState("");

  const handleThumbnailChange = (e) => {
    setThumbnail(e.target.files[0]);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    // In ra nội dung bài blog vừa soạn
    console.log(content);
    // Tạo formData để gửi cả file và dữ liệu text
    const formData = new FormData();
    formData.append("title", title);
    formData.append("summary", summary);
    formData.append("content", content);
    formData.append("category", category);
    formData.append("tags", tags);
    formData.append("status", status);
    if (thumbnail) formData.append("thumbnail", thumbnail);

    try {
      const token = localStorage.getItem("token");
      const res = await fetch("http://localhost:8080/api/admin/blogs", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
        body: formData,
      });
      if (res.ok) {
        setMessage("Tạo bài blog thành công!");
        // Reset form nếu muốn
      } else {
        setMessage("Tạo bài blog thất bại!");
      }
    } catch {
      setMessage("Có lỗi xảy ra!");
    }
  };

  return (
    <div style={{ maxWidth: 700, margin: "40px auto", background: "#fff", borderRadius: 12, padding: 32, boxShadow: "0 2px 8px #eee" }}>
      <h2>Tạo bài blog mới</h2>
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: 16 }}>
          <label>Tiêu đề</label>
          <input type="text" value={title} onChange={e => setTitle(e.target.value)} required style={{ width: "100%", padding: 8 }} />
        </div>
        <div style={{ marginBottom: 16 }}>
          <label>Tóm tắt</label>
          <textarea value={summary} onChange={e => setSummary(e.target.value)} rows={2} style={{ width: "100%", padding: 8 }} />
        </div>
        <div style={{ marginBottom: 16 }}>
          <label>Chuyên mục</label>
          <select value={category} onChange={e => setCategory(e.target.value)} style={{ width: "100%", padding: 8 }}>
            {categories.map(c => <option key={c.value} value={c.value}>{c.label}</option>)}
          </select>
        </div>
        <div style={{ marginBottom: 16 }}>
          <label>Tag (cách nhau bởi dấu phẩy)</label>
          <input type="text" value={tags} onChange={e => setTags(e.target.value)} style={{ width: "100%", padding: 8 }} />
        </div>
        <div style={{ marginBottom: 16 }}>
          <label>Trạng thái</label>
          <select value={status} onChange={e => setStatus(e.target.value)} style={{ width: "100%", padding: 8 }}>
            {statusOptions.map(s => <option key={s.value} value={s.value}>{s.label}</option>)}
          </select>
        </div>
        <div style={{ marginBottom: 16 }}>
          <label>Ảnh đại diện</label>
          <input type="file" accept="image/*" onChange={handleThumbnailChange} />
        </div>
        <div style={{ marginBottom: 16 }}>
          <label>Nội dung</label>
          <CKEditor
            editor={ClassicEditor}
            data={content}
            onChange={(event, editor) => setContent(editor.getData())}
            config={{
              placeholder: "Nhập nội dung bài viết...",
              toolbar: [
                'heading', '|',
                'bold', 'italic', 'underline', 'strikethrough', 'link', 'bulletedList', 'numberedList', '|',
                'blockQuote', 'insertTable', 'imageUpload', 'mediaEmbed', '|',
                'undo', 'redo', 'alignment', 'fontColor', 'fontBackgroundColor', 'fontSize', 'fontFamily'
              ],
              simpleUpload: {
                uploadUrl: 'http://localhost:8080/api/upload/image',
                headers: {
                  Authorization: `Bearer ${localStorage.getItem("token") || ""}`
                }
              }
            }}
          />
        </div>
        <button type="submit" style={{ padding: "8px 24px", borderRadius: 6, background: "#1976d2", color: "#fff", border: "none" }}>
          Đăng bài
        </button>
      </form>
      {message && <div style={{ marginTop: 16, color: "#1976d2" }}>{message}</div>}

      {/* Xem trước bài blog ngay khi nhập */}
      <div style={{ marginTop: 32, padding: 24, border: "1px solid #eee", borderRadius: 8, background: "#f8fafc" }}>
        <h3 style={{ color: "#1976d2" }}>Xem trước bài blog</h3>
        <h2>{title || "Tiêu đề bài viết"}</h2>
        <p style={{ color: "#888" }}>{summary}</p>
        {thumbnail && (
          <img
            src={URL.createObjectURL(thumbnail)}
            alt="thumbnail"
            style={{ maxWidth: 300, margin: "16px 0" }}
          />
        )}
        <div dangerouslySetInnerHTML={{ __html: content }} className="ck-content" />
        <div>Chuyên mục: {categories.find(c => c.value === category)?.label}</div>
        <div>Tag: {tags}</div>
        <div>Trạng thái: {statusOptions.find(s => s.value === status)?.label}</div>
      </div>
    </div>
  );
}