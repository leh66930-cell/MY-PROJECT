package model;

/** Thông tin một hóa đơn dùng để hiển thị trong danh sách (đã ghép tên bàn, nhân viên, khách hàng). */
public class HoaDon {

    private String maHD;
    private String tenBan;
    private String tenNV;
    private String tenKH;       // "Khách vãng lai" nếu hóa đơn không gắn khách hàng
    private String ngayLap;     // đã định dạng dd/MM/yyyy HH:mm
    private String trangThai;
    private long tongTien;

    public HoaDon(String maHD, String tenBan, String tenNV, String tenKH,
            String ngayLap, String trangThai, long tongTien) {
        this.maHD = maHD;
        this.tenBan = tenBan;
        this.tenNV = tenNV;
        this.tenKH = tenKH;
        this.ngayLap = ngayLap;
        this.trangThai = trangThai;
        this.tongTien = tongTien;
    }

    public String getMaHD() {
        return maHD;
    }

    public String getTenBan() {
        return tenBan;
    }

    public String getTenNV() {
        return tenNV;
    }

    public String getTenKH() {
        return tenKH;
    }

    public String getNgayLap() {
        return ngayLap;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public long getTongTien() {
        return tongTien;
    }
}
