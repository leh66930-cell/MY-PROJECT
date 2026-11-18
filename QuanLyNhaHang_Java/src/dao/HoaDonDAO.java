package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import ketnoi.KetNoi;
import model.ChiTietHoaDon;
import model.HoaDon;

public class HoaDonDAO {

    public static final String CHUA_THANH_TOAN = "Chưa thanh toán";
    public static final String DA_THANH_TOAN = "Đã thanh toán";

    private static final String SELECT_CHUNG
            = "SELECT h.MaHD, b.TenBan, nv.HoTen AS TenNV, kh.HoTen AS TenKH, h.NgayLap, h.TrangThai, h.TongTien "
            + "FROM HoaDon h "
            + "JOIN BanAn b ON h.MaBan = b.MaBan "
            + "JOIN NhanVien nv ON h.MaNV = nv.MaNV "
            + "LEFT JOIN KhachHang kh ON h.MaKH = kh.MaKH ";

    public List<HoaDon> layTatCa() throws SQLException {
        return truyVan(SELECT_CHUNG + "ORDER BY h.MaHD DESC");
    }

    public List<HoaDon> timKiem(String tuKhoa) throws SQLException {
        String mau = "%" + tuKhoa + "%";
        return truyVan(SELECT_CHUNG
                + "WHERE h.MaHD LIKE ? OR b.TenBan LIKE ? OR kh.HoTen LIKE ? ORDER BY h.MaHD DESC",
                mau, mau, mau);
    }

    public List<ChiTietHoaDon> layChiTiet(String maHD) throws SQLException {
        String sql = "SELECT ct.MaMon, m.TenMon, ct.SoLuong, ct.DonGia "
                + "FROM ChiTietHoaDon ct JOIN MonAn m ON ct.MaMon = m.MaMon "
                + "WHERE ct.MaHD = ? ORDER BY ct.MaMon";
        List<ChiTietHoaDon> ds = new ArrayList<ChiTietHoaDon>();
        try (Connection con = KetNoi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(new ChiTietHoaDon(
                            rs.getString("MaMon"),
                            rs.getString("TenMon"),
                            rs.getInt("SoLuong"),
                            rs.getLong("DonGia")));
                }
            }
        }
        return ds;
    }

   
    public void luuHoaDon(String maHD, String maBan, String maNV, String maKH, Timestamp ngayLap,
            String trangThai, List<ChiTietHoaDon> dsMon) throws SQLException {

        long tongTien = 0;
        for (ChiTietHoaDon ct : dsMon) {
            tongTien += ct.getThanhTien();
        }

        Connection con = KetNoi.getConnection();
        try {
            con.setAutoCommit(false);       

            String sqlHD = "INSERT INTO HoaDon (MaHD, MaBan, MaNV, MaKH, NgayLap, TrangThai, TongTien) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sqlHD)) {
                ps.setString(1, maHD);
                ps.setString(2, maBan);
                ps.setString(3, maNV);
                if (maKH == null) {
                    ps.setNull(4, Types.VARCHAR);
                } else {
                    ps.setString(4, maKH);
                }
                ps.setTimestamp(5, ngayLap);
                ps.setString(6, trangThai);
                ps.setLong(7, tongTien);
                ps.executeUpdate();
            }

            String sqlCT = "INSERT INTO ChiTietHoaDon (MaHD, MaMon, SoLuong, DonGia) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sqlCT)) {
                for (ChiTietHoaDon ct : dsMon) {
                    ps.setString(1, maHD);
                    ps.setString(2, ct.getMaMon());
                    ps.setInt(3, ct.getSoLuong());
                    ps.setLong(4, ct.getDonGia());
                    ps.executeUpdate();
                }
            }

            String sqlBan = "UPDATE BanAn SET TrangThai = ? WHERE MaBan = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlBan)) {
                ps.setString(1, trangThai.equals(DA_THANH_TOAN) ? "Trống" : "Đang phục vụ");
                ps.setString(2, maBan);
                ps.executeUpdate();
            }

            con.commit();                  
        } catch (SQLException ex) {
            con.rollback();                 
            throw ex;
        } finally {
            con.close();
        }
    }

    public int capNhatTrangThai(String maHD, String trangThai) throws SQLException {
        Connection con = KetNoi.getConnection();
        try {
            con.setAutoCommit(false);
            int soDong;
            try (PreparedStatement ps = con.prepareStatement("UPDATE HoaDon SET TrangThai = ? WHERE MaHD = ?")) {
                ps.setString(1, trangThai);
                ps.setString(2, maHD);
                soDong = ps.executeUpdate();
            }
            if (soDong > 0 && trangThai.equals(DA_THANH_TOAN)) {
                String sqlBan = "UPDATE BanAn SET TrangThai = ? "
                        + "WHERE MaBan = (SELECT MaBan FROM HoaDon WHERE MaHD = ?)";
                try (PreparedStatement ps = con.prepareStatement(sqlBan)) {
                    ps.setString(1, "Trống");
                    ps.setString(2, maHD);
                    ps.executeUpdate();
                }
            }
            con.commit();
            return soDong;
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.close();
        }
    }

    public int xoa(String maHD) throws SQLException {
        String sql = "DELETE FROM HoaDon WHERE MaHD = ?";
        try (Connection con = KetNoi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHD);
            return ps.executeUpdate();
        }
    }

    public String maTiepTheo() throws SQLException {
        return MaTuDong.tiepTheo("HoaDon", "MaHD", "HD", 3);
    }

    private List<HoaDon> truyVan(String sql, String... thamSo) throws SQLException {
        List<HoaDon> ds = new ArrayList<HoaDon>();
        SimpleDateFormat dinhDang = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try (Connection con = KetNoi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < thamSo.length; i++) {
                ps.setString(i + 1, thamSo[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String tenKH = rs.getString("TenKH");
                    if (tenKH == null) {
                        tenKH = "Khách vãng lai";
                    }
                    ds.add(new HoaDon(
                            rs.getString("MaHD"),
                            rs.getString("TenBan"),
                            rs.getString("TenNV"),
                            tenKH,
                            dinhDang.format(rs.getTimestamp("NgayLap")),
                            rs.getString("TrangThai"),
                            rs.getLong("TongTien")));
                }
            }
        }
        return ds;
    }
}
