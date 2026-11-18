package model;

import java.sql.Date;

/** Lớp đại diện cho một dòng của bảng NhanVien. */
public class NhanVien {

    private String maNV;
    private String hoTen;
    private String gioiTinh;
    private Date ngaySinh;          // có thể null
    private String soDienThoai;
    private String chucVu;
    private long luong;
    private Date ngayVaoLam;

    public NhanVien(String maNV, String hoTen, String gioiTinh, Date ngaySinh,
            String soDienThoai, String chucVu, long luong, Date ngayVaoLam) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.soDienThoai = soDienThoai;
        this.chucVu = chucVu;
        this.luong = luong;
        this.ngayVaoLam = ngayVaoLam;
    }

    public String getMaNV() {
        return maNV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public String getChucVu() {
        return chucVu;
    }

    public long getLuong() {
        return luong;
    }

    public Date getNgayVaoLam() {
        return ngayVaoLam;
    }
}
