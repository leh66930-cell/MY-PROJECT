package model;

/** Lớp đại diện cho một dòng của bảng MonAn (kèm tên loại món để hiển thị). */
public class MonAn {

    private String maMon;
    private String tenMon;
    private String maLoai;
    private String tenLoai;
    private String donViTinh;
    private long donGia;
    private String trangThai;

    public MonAn(String maMon, String tenMon, String maLoai, String tenLoai,
            String donViTinh, long donGia, String trangThai) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.maLoai = maLoai;
        this.tenLoai = tenLoai;
        this.donViTinh = donViTinh;
        this.donGia = donGia;
        this.trangThai = trangThai;
    }

    public String getMaMon() {
        return maMon;
    }

    public String getTenMon() {
        return tenMon;
    }

    public String getMaLoai() {
        return maLoai;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public long getDonGia() {
        return donGia;
    }

    public String getTrangThai() {
        return trangThai;
    }
}
