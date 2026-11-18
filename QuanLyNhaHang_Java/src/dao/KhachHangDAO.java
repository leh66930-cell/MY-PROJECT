package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ketnoi.KetNoi;
import model.KhachHang;

public class KhachHangDAO {

    private static final String SELECT_CHUNG
            = "SELECT MaKH, HoTen, SoDienThoai, DiaChi, DiemTichLuy FROM KhachHang ";

    public List<KhachHang> layTatCa() throws SQLException {
        return truyVan(SELECT_CHUNG + "ORDER BY MaKH");
    }

    public List<KhachHang> timKiem(String tuKhoa) throws SQLException {
        String mau = "%" + tuKhoa + "%";
        return truyVan(SELECT_CHUNG + "WHERE MaKH LIKE ? OR HoTen LIKE ? OR SoDienThoai LIKE ? ORDER BY MaKH",
                mau, mau, mau);
    }

    public boolean tonTai(String maKH) throws SQLException {
        String sql = "SELECT 1 FROM KhachHang WHERE MaKH = ?";
        try (Connection con = KetNoi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKH);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void them(KhachHang kh) throws SQLException {
        String sql = "INSERT INTO KhachHang (MaKH, HoTen, SoDienThoai, DiaChi, DiemTichLuy) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = KetNoi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, kh.getMaKH());
            ps.setString(2, kh.getHoTen());
            ps.setString(3, kh.getSoDienThoai());
            ps.setString(4, kh.getDiaChi());
            ps.setInt(5, kh.getDiemTichLuy());
            ps.executeUpdate();
        }
    }

    public int sua(KhachHang kh) throws SQLException {
        String sql = "UPDATE KhachHang SET HoTen = ?, SoDienThoai = ?, DiaChi = ?, DiemTichLuy = ? WHERE MaKH = ?";
        try (Connection con = KetNoi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, kh.getHoTen());
            ps.setString(2, kh.getSoDienThoai());
            ps.setString(3, kh.getDiaChi());
            ps.setInt(4, kh.getDiemTichLuy());
            ps.setString(5, kh.getMaKH());
            return ps.executeUpdate();
        }
    }

    public int xoa(String maKH) throws SQLException {
        String sql = "DELETE FROM KhachHang WHERE MaKH = ?";
        try (Connection con = KetNoi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKH);
            return ps.executeUpdate();
        }
    }

    public String maTiepTheo() throws SQLException {
        return MaTuDong.tiepTheo("KhachHang", "MaKH", "KH", 2);
    }

    private List<KhachHang> truyVan(String sql, String... thamSo) throws SQLException {
        List<KhachHang> ds = new ArrayList<KhachHang>();
        try (Connection con = KetNoi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < thamSo.length; i++) {
                ps.setString(i + 1, thamSo[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(new KhachHang(
                            rs.getString("MaKH"),
                            rs.getString("HoTen"),
                            rs.getString("SoDienThoai"),
                            rs.getString("DiaChi"),
                            rs.getInt("DiemTichLuy")));
                }
            }
        }
        return ds;
    }
}
