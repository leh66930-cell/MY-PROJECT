package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ketnoi.KetNoi;
import model.MonAn;

/** Các thao tác với bảng MonAn: xem, tìm kiếm, thêm, sửa, xóa. */
public class MonAnDAO {

    private static final String SELECT_CHUNG
            = "SELECT m.MaMon, m.TenMon, m.MaLoai, l.TenLoai, m.DonViTinh, m.DonGia, m.TrangThai "
            + "FROM MonAn m JOIN LoaiMon l ON m.MaLoai = l.MaLoai ";

    public List<MonAn> layTatCa() throws SQLException {
        return truyVan(SELECT_CHUNG + "ORDER BY m.MaMon");
    }

    /** Tìm theo mã món hoặc tên món (chứa từ khóa). */
    public List<MonAn> timKiem(String tuKhoa) throws SQLException {
        String mau = "%" + tuKhoa + "%";
        return truyVan(SELECT_CHUNG + "WHERE m.MaMon LIKE ? OR m.TenMon LIKE ? ORDER BY m.MaMon", mau, mau);
    }

    /** Chỉ lấy các món đang bán (dùng khi lập hóa đơn). */
    public List<MonAn> layMonDangBan() throws SQLException {
        return truyVan(SELECT_CHUNG + "WHERE m.TrangThai = ? ORDER BY m.MaMon", "Còn bán");
    }

    public boolean tonTai(String maMon) throws SQLException {
        String sql = "SELECT 1 FROM MonAn WHERE MaMon = ?";
        try (Connection con = KetNoi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maMon);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void them(MonAn m) throws SQLException {
        String sql = "INSERT INTO MonAn (MaMon, TenMon, MaLoai, DonViTinh, DonGia, TrangThai) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = KetNoi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getMaMon());
            ps.setString(2, m.getTenMon());
            ps.setString(3, m.getMaLoai());
            ps.setString(4, m.getDonViTinh());
            ps.setLong(5, m.getDonGia());
            ps.setString(6, m.getTrangThai());
            ps.executeUpdate();
        }
    }

    /** Trả về số dòng được sửa (0 nghĩa là không tìm thấy mã món). */
    public int sua(MonAn m) throws SQLException {
        String sql = "UPDATE MonAn SET TenMon = ?, MaLoai = ?, DonViTinh = ?, DonGia = ?, TrangThai = ? "
                + "WHERE MaMon = ?";
        try (Connection con = KetNoi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getTenMon());
            ps.setString(2, m.getMaLoai());
            ps.setString(3, m.getDonViTinh());
            ps.setLong(4, m.getDonGia());
            ps.setString(5, m.getTrangThai());
            ps.setString(6, m.getMaMon());
            return ps.executeUpdate();
        }
    }

    /** Trả về số dòng được xóa (0 nghĩa là không tìm thấy mã món). */
    public int xoa(String maMon) throws SQLException {
        String sql = "DELETE FROM MonAn WHERE MaMon = ?";
        try (Connection con = KetNoi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maMon);
            return ps.executeUpdate();
        }
    }

    public String maTiepTheo() throws SQLException {
        return MaTuDong.tiepTheo("MonAn", "MaMon", "M", 2);
    }

    // Hàm dùng chung: chạy câu SELECT với các tham số chuỗi rồi đổi kết quả thành danh sách MonAn
    private List<MonAn> truyVan(String sql, String... thamSo) throws SQLException {
        List<MonAn> ds = new ArrayList<MonAn>();
        try (Connection con = KetNoi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < thamSo.length; i++) {
                ps.setString(i + 1, thamSo[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(new MonAn(
                            rs.getString("MaMon"),
                            rs.getString("TenMon"),
                            rs.getString("MaLoai"),
                            rs.getString("TenLoai"),
                            rs.getString("DonViTinh"),
                            rs.getLong("DonGia"),
                            rs.getString("TrangThai")));
                }
            }
        }
        return ds;
    }
}
