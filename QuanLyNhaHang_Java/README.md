# Phần mềm quản lý nhà hàng (Java Swing + SQL Server)

Đồ án môn Lập trình Java: phần mềm quản lý nhà hàng viết bằng Java Swing, kết nối cơ sở dữ liệu **SQL Server** qua JDBC, lập trình trên **Apache NetBeans**.

## 1. Chức năng

| Form | Chức năng |
|---|---|
| **Form chính** (`FormChinh`) | Menu mở các form, kiểm tra kết nối CSDL |
| **Quản lý món ăn** (`FormMonAn`) | Xem danh sách, thêm, sửa, xóa, tìm kiếm theo mã/tên món |
| **Quản lý nhân viên** (`FormNhanVien`) | Xem danh sách, thêm, sửa, xóa, tìm kiếm theo mã/họ tên/số điện thoại |
| **Quản lý khách hàng** (`FormKhachHang`) | Xem danh sách, thêm, sửa, xóa, tìm kiếm theo mã/họ tên/số điện thoại |
| **Hóa đơn** (`FormHoaDon`) | Lập hóa đơn (chọn bàn, nhân viên, khách, thêm món, tự tính tổng tiền), xem danh sách, tìm kiếm, thanh toán, xóa hóa đơn, xem chi tiết món |

Một số điểm đáng chú ý:
- Tự gợi ý mã mới (M11, NV07, KH06, HD007...).
- Kiểm tra dữ liệu nhập (để trống, sai số, sai định dạng ngày dd/MM/yyyy, số điện thoại...).
- Lưu hóa đơn bằng **transaction**: hoặc lưu đủ hóa đơn và các món, hoặc không lưu gì.
- Lập hóa đơn chưa thanh toán thì bàn chuyển "Đang phục vụ"; thanh toán xong thì bàn về "Trống".
- Báo lỗi dễ hiểu khi xóa dữ liệu đang được dùng ở bảng khác (ví dụ xóa món đã có trong hóa đơn).

## 2. Cấu trúc mã nguồn

```
src/
├── ketnoi/KetNoi.java          Kết nối SQL Server (sửa URL, user, password ở đây)
├── model/                      Các lớp dữ liệu: MonAn, NhanVien, KhachHang, HoaDon, ChiTietHoaDon
├── dao/                        Các lớp truy vấn CSDL (JDBC): MonAnDAO, NhanVienDAO, KhachHangDAO,
│                               HoaDonDAO, DanhMucDAO, MaTuDong
└── giaodien/                   Các form Swing: FormChinh (chạy file này), FormMonAn, FormNhanVien,
                                FormKhachHang, FormHoaDon, TienIch (hàm dùng chung)
lib/mssql-jdbc-12.8.1.jre8.jar  Thư viện JDBC của SQL Server (đã gắn sẵn vào project)
nbproject/, build.xml           Cấu hình project NetBeans (không cần sửa)
database/QuanLyNhaHang.sql      Script tạo CSDL + dữ liệu mẫu
```

## 3. Cách mở và chạy trên NetBeans

Thư mục này **đã là một project NetBeans hoàn chỉnh** (có sẵn thư viện `mssql-jdbc` trong `lib/`), không cần tạo project mới.

### Cách 1 (khuyên dùng): mở trực tiếp project
1. **Giải nén** file zip ra một thư mục (không mở thẳng file zip).
2. Mở **Apache NetBeans** → menu **File → Open Project...** (phím tắt Ctrl+Shift+O).
3. Chọn thư mục `QuanLyNhaHang_Java` (thư mục có biểu tượng cốc cà phê) rồi bấm **Open Project**.
4. **Tạo CSDL**: mở SQL Server Management Studio, mở file `database/QuanLyNhaHang.sql` và bấm Execute (bỏ qua nếu đã tạo rồi).
5. Mở file `ketnoi/KetNoi.java`, sửa `URL`, `USER`, `PASSWORD` cho đúng với máy của bạn.
6. Chuột phải project → **Run** (hoặc bấm F6). Cửa sổ chính hiện dòng chữ xanh "Đã kết nối cơ sở dữ liệu QuanLyNhaHang" là thành công.

### Cách 2: chép mã nguồn vào project có sẵn
Dùng khi Cách 1 không mở được hoặc bạn muốn dùng project của mình:
1. Trong NetBeans: **File → New Project → Java with Ant → Java Application**, bỏ tick *Create Main Class*. Chuột phải *Libraries* → *Add JAR/Folder* → chọn file `lib/mssql-jdbc-12.8.1.jre8.jar`.
2. Chuột phải project → *Properties* → *Sources* → *Encoding* = `UTF-8` (nếu không chữ tiếng Việt sẽ bị lỗi font).
3. Chép 4 thư mục `ketnoi`, `model`, `dao`, `giaodien` trong `src/` vào thư mục `src` của project mới, rồi chuột phải project → *Refresh*.
4. Chuột phải project → *Properties* → *Run* → *Main Class* = `giaodien.FormChinh`, sau đó bấm F6.

## 4. Lỗi thường gặp

| Lỗi | Cách xử lý |
|---|---|
| "Chưa thêm thư viện mssql-jdbc" | Chuột phải *Libraries* → *Add JAR/Folder* → chọn file `lib/mssql-jdbc-12.8.1.jre8.jar` |
| "Login failed for user" | Sai user/mật khẩu trong `KetNoi.java`, hoặc SQL Server chưa bật chế độ đăng nhập SQL Server Authentication |
| "The TCP/IP connection to the host ... has failed" | Bật TCP/IP (cổng 1433) trong SQL Server Configuration Manager rồi khởi động lại dịch vụ SQL Server |
| Chữ tiếng Việt bị lỗi font | Chưa đặt Encoding UTF-8 cho project (Properties → Sources), sau đó chọn *Clean and Build* |
| SQL Server Express có tên instance | Dùng URL thứ hai đã ghi chú sẵn trong `KetNoi.java` |

## 5. Đưa project lên GitHub

1. Vào https://github.com, đăng nhập và bấm **New repository**, đặt tên (ví dụ `QuanLyNhaHang`), chọn **Public**, bấm *Create repository*.
2. **Quan trọng:** trước khi đưa lên, nên sửa `PASSWORD` trong `KetNoi.java` thành mật khẩu giả (ví dụ `"123456"`) để không lộ mật khẩu thật. Thư mục `lib/` đã có sẵn file jar nên thầy cô tải về mở lên là chạy được.
3. Mở Terminal / Git Bash tại **thư mục gốc của project NetBeans** (thư mục có `src`, `nbproject`, `build.xml`, chính là thư mục `QuanLyNhaHang_Java`) rồi chạy lần lượt:

```bash
git init
git add .
git commit -m "Phan mem quan ly nha hang"
git branch -M main
git remote add origin https://github.com/TEN_TAI_KHOAN/QuanLyNhaHang.git
git push -u origin main
```

   Khi `git push` hỏi mật khẩu, GitHub không nhận mật khẩu đăng nhập mà cần **Personal Access Token** (GitHub → Settings → Developer settings → Personal access tokens). Có thể dùng **GitHub Desktop** để đẩy code mà không cần gõ lệnh.
4. Mở lại trang repository trên GitHub, kiểm tra đã thấy các file rồi **sao chép đường link** (dạng `https://github.com/TEN_TAI_KHOAN/QuanLyNhaHang`) và nộp lên hệ thống.

> File `.gitignore` kèm theo đã loại bỏ thư mục `build/`, `dist/` và `nbproject/private/` (các file sinh tự động, không cần đưa lên GitHub).
