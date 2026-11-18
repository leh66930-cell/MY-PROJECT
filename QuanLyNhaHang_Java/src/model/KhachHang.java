package model;

/** Lớp đại diện cho một dòng của bảng KhachHang. */
public class KhachHang {

    private String maKH;
    private String hoTen;
    private String soDienThoai;
    private String diaChi;
    private int diemTichLuy;

    public KhachHang(String maKH, String hoTen, String soDienThoai, String diaChi, int diemTichLuy) {
        this.maKH = maKH;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.diemTichLuy = diemTichLuy;
    }

    public String getMaKH() {
        return maKH;
    }

    public String getHoTen() {
        return hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public int getDiemTichLuy() {
        return diemTichLuy;
    }
}
