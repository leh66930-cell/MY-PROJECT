package model;

/** Một dòng món trong hóa đơn (bảng ChiTietHoaDon, kèm tên món để hiển thị). */
public class ChiTietHoaDon {

    private String maMon;
    private String tenMon;
    private int soLuong;
    private long donGia;

    public ChiTietHoaDon(String maMon, String tenMon, int soLuong, long donGia) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    public String getMaMon() {
        return maMon;
    }

    public String getTenMon() {
        return tenMon;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public long getDonGia() {
        return donGia;
    }

    /** Thành tiền = số lượng x đơn giá. */
    public long getThanhTien() {
        return soLuong * donGia;
    }
}
