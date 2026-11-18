-- ================================================
-- CƠ SỞ DỮ LIỆU QUẢN LÝ NHÀ HÀNG (SQL Server)
-- ================================================
USE master;
GO
IF DB_ID('QuanLyNhaHang') IS NOT NULL
    DROP DATABASE QuanLyNhaHang;
GO
CREATE DATABASE QuanLyNhaHang;
GO
USE QuanLyNhaHang;
GO

-- 1. Bảng BanAn
CREATE TABLE BanAn (
    MaBan      VARCHAR(10)   NOT NULL,
    TenBan     NVARCHAR(50)  NOT NULL,
    SoChoNgoi  INT           NOT NULL,
    ViTri      NVARCHAR(50)  NULL,
    TrangThai  NVARCHAR(20)  NOT NULL CONSTRAINT DF_BanAn_TrangThai DEFAULT N'Trống',
    CONSTRAINT PK_BanAn PRIMARY KEY (MaBan),
    CONSTRAINT CK_BanAn_SoCho CHECK (SoChoNgoi > 0),
    CONSTRAINT CK_BanAn_TrangThai
        CHECK (TrangThai IN (N'Trống', N'Đang phục vụ', N'Đã đặt'))
);
GO

-- 2. Bảng LoaiMon
CREATE TABLE LoaiMon (
    MaLoai   VARCHAR(10)   NOT NULL,
    TenLoai  NVARCHAR(50)  NOT NULL,
    MoTa     NVARCHAR(100) NULL,
    CONSTRAINT PK_LoaiMon PRIMARY KEY (MaLoai),
    CONSTRAINT UQ_LoaiMon_TenLoai UNIQUE (TenLoai)
);
GO

-- 3. Bảng MonAn
CREATE TABLE MonAn (
    MaMon      VARCHAR(10)   NOT NULL,
    TenMon     NVARCHAR(100) NOT NULL,
    MaLoai     VARCHAR(10)   NOT NULL,
    DonViTinh  NVARCHAR(20)  NOT NULL,
    DonGia     DECIMAL(12,0) NOT NULL,
    TrangThai  NVARCHAR(20)  NOT NULL CONSTRAINT DF_MonAn_TrangThai DEFAULT N'Còn bán',
    CONSTRAINT PK_MonAn PRIMARY KEY (MaMon),
    CONSTRAINT FK_MonAn_LoaiMon FOREIGN KEY (MaLoai) REFERENCES LoaiMon(MaLoai),
    CONSTRAINT CK_MonAn_DonGia CHECK (DonGia > 0),
    CONSTRAINT CK_MonAn_TrangThai CHECK (TrangThai IN (N'Còn bán', N'Tạm hết'))
);
GO

-- 4. Bảng NhanVien
CREATE TABLE NhanVien (
    MaNV         VARCHAR(10)   NOT NULL,
    HoTen        NVARCHAR(100) NOT NULL,
    GioiTinh     NVARCHAR(5)   NOT NULL,
    NgaySinh     DATE          NULL,
    SoDienThoai  VARCHAR(15)   NOT NULL,
    ChucVu       NVARCHAR(30)  NOT NULL,
    Luong        DECIMAL(12,0) NOT NULL CONSTRAINT DF_NhanVien_Luong DEFAULT 0,
    NgayVaoLam   DATE          NOT NULL CONSTRAINT DF_NhanVien_NgayVaoLam DEFAULT GETDATE(),
    CONSTRAINT PK_NhanVien PRIMARY KEY (MaNV),
    CONSTRAINT UQ_NhanVien_SDT UNIQUE (SoDienThoai),
    CONSTRAINT CK_NhanVien_GioiTinh CHECK (GioiTinh IN (N'Nam', N'Nữ')),
    CONSTRAINT CK_NhanVien_Luong CHECK (Luong >= 0)
);
GO

-- 5. Bảng KhachHang
CREATE TABLE KhachHang (
    MaKH          VARCHAR(10)   NOT NULL,
    HoTen         NVARCHAR(100) NOT NULL,
    SoDienThoai   VARCHAR(15)   NOT NULL,
    DiaChi        NVARCHAR(100) NULL,
    DiemTichLuy   INT           NOT NULL CONSTRAINT DF_KhachHang_Diem DEFAULT 0,
    CONSTRAINT PK_KhachHang PRIMARY KEY (MaKH),
    CONSTRAINT UQ_KhachHang_SDT UNIQUE (SoDienThoai),
    CONSTRAINT CK_KhachHang_Diem CHECK (DiemTichLuy >= 0)
);
GO

-- 6. Bảng HoaDon
CREATE TABLE HoaDon (
    MaHD       VARCHAR(10)   NOT NULL,
    MaBan      VARCHAR(10)   NOT NULL,
    MaNV       VARCHAR(10)   NOT NULL,
    MaKH       VARCHAR(10)   NULL,
    NgayLap    DATETIME      NOT NULL CONSTRAINT DF_HoaDon_NgayLap DEFAULT GETDATE(),
    TrangThai  NVARCHAR(20)  NOT NULL
        CONSTRAINT DF_HoaDon_TrangThai DEFAULT N'Chưa thanh toán',
    TongTien   DECIMAL(14,0) NOT NULL CONSTRAINT DF_HoaDon_TongTien DEFAULT 0,
    CONSTRAINT PK_HoaDon PRIMARY KEY (MaHD),
    CONSTRAINT FK_HoaDon_BanAn FOREIGN KEY (MaBan) REFERENCES BanAn(MaBan),
    CONSTRAINT FK_HoaDon_NhanVien FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV),
    CONSTRAINT FK_HoaDon_KhachHang FOREIGN KEY (MaKH) REFERENCES KhachHang(MaKH),
    CONSTRAINT CK_HoaDon_TrangThai
        CHECK (TrangThai IN (N'Chưa thanh toán', N'Đã thanh toán')),
    CONSTRAINT CK_HoaDon_TongTien CHECK (TongTien >= 0)
);
GO

-- 7. Bảng ChiTietHoaDon
CREATE TABLE ChiTietHoaDon (
    MaHD     VARCHAR(10)   NOT NULL,
    MaMon    VARCHAR(10)   NOT NULL,
    SoLuong  INT           NOT NULL,
    DonGia   DECIMAL(12,0) NOT NULL,
    CONSTRAINT PK_ChiTietHoaDon PRIMARY KEY (MaHD, MaMon),
    CONSTRAINT FK_CTHD_HoaDon FOREIGN KEY (MaHD) REFERENCES HoaDon(MaHD) ON DELETE CASCADE,
    CONSTRAINT FK_CTHD_MonAn FOREIGN KEY (MaMon) REFERENCES MonAn(MaMon),
    CONSTRAINT CK_CTHD_SoLuong CHECK (SoLuong > 0),
    CONSTRAINT CK_CTHD_DonGia CHECK (DonGia > 0)
);
GO

USE QuanLyNhaHang;
GO

-- Bảng BanAn
INSERT INTO BanAn (MaBan, TenBan, SoChoNgoi, ViTri, TrangThai) VALUES
('B01', N'Bàn 01', 4, N'Tầng 1', N'Đang phục vụ'),
('B02', N'Bàn 02', 4, N'Tầng 1', N'Trống'),
('B03', N'Bàn 03', 6, N'Tầng 1', N'Trống'),
('B04', N'Bàn 04', 6, N'Tầng 2', N'Trống'),
('B05', N'Bàn 05', 8, N'Tầng 2', N'Đã đặt'),
('B06', N'Bàn 06', 10, N'Phòng VIP', N'Trống');

-- Bảng LoaiMon
INSERT INTO LoaiMon (MaLoai, TenLoai, MoTa) VALUES
('L01', N'Khai vị', N'Các món ăn nhẹ dùng trước bữa chính'),
('L02', N'Món chính', N'Cơm, thịt, cá và các món xào, kho'),
('L03', N'Lẩu', N'Các loại lẩu dùng cho nhóm'),
('L04', N'Tráng miệng', N'Chè, trái cây, kem'),
('L05', N'Đồ uống', N'Nước giải khát và bia');

-- Bảng MonAn
INSERT INTO MonAn (MaMon, TenMon, MaLoai, DonViTinh, DonGia, TrangThai) VALUES
('M01', N'Gỏi cuốn tôm thịt', 'L01', N'Phần', 45000, N'Còn bán'),
('M02', N'Chả giò', 'L01', N'Phần', 50000, N'Còn bán'),
('M03', N'Cơm chiên dương châu', 'L02', N'Đĩa', 60000, N'Còn bán'),
('M04', N'Bò lúc lắc', 'L02', N'Đĩa', 120000, N'Còn bán'),
('M05', N'Cá kho tộ', 'L02', N'Phần', 95000, N'Còn bán'),
('M06', N'Lẩu Thái hải sản', 'L03', N'Nồi', 250000, N'Còn bán'),
('M07', N'Lẩu gà lá é', 'L03', N'Nồi', 220000, N'Tạm hết'),
('M08', N'Chè khúc bạch', 'L04', N'Ly', 30000, N'Còn bán'),
('M09', N'Nước cam ép', 'L05', N'Ly', 35000, N'Còn bán'),
('M10', N'Bia Hà Nội', 'L05', N'Lon', 20000, N'Còn bán');

-- Bảng NhanVien
INSERT INTO NhanVien (MaNV, HoTen, GioiTinh, NgaySinh, SoDienThoai, ChucVu, Luong, NgayVaoLam) VALUES
('NV01', N'Nguyễn Văn An', N'Nam', '1998-03-12',
 '0905123456', N'Quản lý', 15000000, '2022-01-10'),
('NV02', N'Trần Thị Bình', N'Nữ', '2000-07-25',
 '0912345678', N'Thu ngân', 8000000, '2023-03-05'),
('NV03', N'Lê Minh Châu', N'Nữ', '2001-11-02',
 '0933456789', N'Phục vụ', 6500000, '2023-06-15'),
('NV04', N'Phạm Quốc Dũng', N'Nam', '1999-05-18',
 '0944567890', N'Phục vụ', 6500000, '2023-09-01'),
('NV05', N'Hoàng Thị Em', N'Nữ', '1997-09-30',
 '0977678901', N'Đầu bếp', 12000000, '2022-05-20'),
('NV06', N'Vũ Đức Phúc', N'Nam', '2002-01-08',
 '0988789012', N'Phục vụ', 6000000, '2024-02-12');

-- Bảng KhachHang
INSERT INTO KhachHang (MaKH, HoTen, SoDienThoai, DiaChi, DiemTichLuy) VALUES
('KH01', N'Đỗ Thanh Hà', '0901111222', N'Cầu Giấy, Hà Nội', 120),
('KH02', N'Bùi Quang Huy', '0902222333', N'Thanh Xuân, Hà Nội', 80),
('KH03', N'Ngô Mai Lan', '0903333444', N'Đống Đa, Hà Nội', 250),
('KH04', N'Đặng Văn Long', '0904444555', N'Hà Đông, Hà Nội', 40),
('KH05', N'Trịnh Thu Nga', '0905555666', N'Hoàng Mai, Hà Nội', 0);

-- Bảng HoaDon (TongTien để mặc định 0, sẽ cập nhật sau khi nhập chi tiết)
INSERT INTO HoaDon (MaHD, MaBan, MaNV, MaKH, NgayLap, TrangThai) VALUES
('HD001', 'B01', 'NV03', 'KH01', '20261026 18:30:00', N'Đã thanh toán'),
('HD002', 'B03', 'NV04', 'KH02', '20261026 19:15:00', N'Đã thanh toán'),
('HD003', 'B06', 'NV03', 'KH03', '20261027 12:00:00', N'Đã thanh toán'),
('HD004', 'B02', 'NV06', NULL, '20261028 19:45:00', N'Đã thanh toán'),
('HD005', 'B04', 'NV04', 'KH04', '20261029 18:00:00', N'Đã thanh toán'),
('HD006', 'B01', 'NV03', 'KH05', '20261030 17:30:00', N'Chưa thanh toán');

-- Bảng ChiTietHoaDon
INSERT INTO ChiTietHoaDon (MaHD, MaMon, SoLuong, DonGia) VALUES
('HD001', 'M01', 2, 45000),
('HD001', 'M04', 1, 120000),
('HD001', 'M03', 2, 60000),
('HD001', 'M09', 2, 35000),
('HD002', 'M06', 1, 250000),
('HD002', 'M08', 3, 30000),
('HD002', 'M10', 4, 20000),
('HD003', 'M06', 2, 250000),
('HD003', 'M05', 2, 95000),
('HD003', 'M02', 2, 50000),
('HD003', 'M04', 3, 120000),
('HD003', 'M10', 8, 20000),
('HD004', 'M03', 1, 60000),
('HD004', 'M02', 1, 50000),
('HD004', 'M09', 1, 35000),
('HD005', 'M04', 2, 120000),
('HD005', 'M05', 1, 95000),
('HD005', 'M01', 1, 45000),
('HD005', 'M08', 1, 30000),
('HD005', 'M09', 1, 35000),
('HD006', 'M02', 1, 50000),
('HD006', 'M03', 2, 60000),
('HD006', 'M10', 3, 20000);

-- Cập nhật tổng tiền cho từng hóa đơn
UPDATE HoaDon
SET TongTien = (SELECT SUM(SoLuong * DonGia)
                FROM ChiTietHoaDon
                WHERE ChiTietHoaDon.MaHD = HoaDon.MaHD);
GO
