package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import ketnoi.KetNoi;
import model.NhanVien;

/** Các thao tác với bảng NhanVien: xem, tìm kiếm, thêm, sửa, xóa. */
public class NhanVienDAO {

    private static final String SELECT_CHUNG
            = "SELECT MaNV, HoTen, GioiTinh, NgaySinh, SoDienThoai, ChucVu, Luong, NgayVaoLam FROM NhanVien ";

    public List<NhanVien> layTatCa() throws SQLException {
        return truyVan(SELECT_CHUNG + "ORDER BY MaNV");
    }

    /** Tìm theo mã, họ tên hoặc số điện thoại. */
    public List<NhanVien> timKiem(String tuKhoa) throws SQLException {
        String mau = "%" + tuKhoa + "%";
        return truyVan(SELECT_CHUNG + "WHERE MaNV LIKE ? OR HoTen LIKE ? OR SoDienThoai LIKE ? ORDER BY MaNV",
                mau, mau, mau);
    }

    public boolean tonTai(String maNV) throws SQLException {
        String sql = "SELECT 1 FROM NhanVien WHERE MaNV = ?";
        try (Connection con = KetNoi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void them(NhanVien nv) throws SQLException {
        String sql = "INSERT INTO NhanVien (MaNV, HoTen, GioiTinh, NgaySinh, SoDienThoai, ChucVu, Luong, NgayVaoLam) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = KetNoi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nv.getMaNV());
            ps.setString(2, nv.getHoTen());
            ps.setString(3, nv.getGioiTinh());
            if (nv.getNgaySinh() == null) {
                ps.setNull(4, Types.DATE);
            } else {
                ps.setDate(4, nv.getNgaySinh());
            }
            ps.setString(5, nv.getSoDienThoai());
            ps.setString(6, nv.getChucVu());
            ps.setLong(7, nv.getLuong());
            ps.setDate(8, nv.getNgayVaoLam());
            ps.executeUpdate();
        }
    }

    /** Trả về số dòng được sửa (0 nghĩa là không tìm thấy mã nhân viên). */
    public int sua(NhanVien nv) throws SQLException {
        String sql = "UPDATE NhanVien SET HoTen = ?, GioiTinh = ?, NgaySinh = ?, SoDienThoai = ?, "
                + "ChucVu = ?, Luong = ?, NgayVaoLam = ? WHERE MaNV = ?";
        try (Connection con = KetNoi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nv.getHoTen());
            ps.setString(2, nv.getGioiTinh());
            if (nv.getNgaySinh() == null) {
                ps.setNull(3, Types.DATE);
            } else {
                ps.setDate(3, nv.getNgaySinh());
            }
            ps.setString(4, nv.getSoDienThoai());
            ps.setString(5, nv.getChucVu());
            ps.setLong(6, nv.getLuong());
            ps.setDate(7, nv.getNgayVaoLam());
            ps.setString(8, nv.getMaNV());
            return ps.executeUpdate();
        }
    }

    public int xoa(String maNV) throws SQLException {
        String sql = "DELETE FROM NhanVien WHERE MaNV = ?";
        try (Connection con = KetNoi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNV);
            return ps.executeUpdate();
        }
    }

    public String maTiepTheo() throws SQLException {
        return MaTuDong.tiepTheo("NhanVien", "MaNV", "NV", 2);
    }

    private List<NhanVien> truyVan(String sql, String... thamSo) throws SQLException {
        List<NhanVien> ds = new ArrayList<NhanVien>();
        try (Connection con = KetNoi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < thamSo.length; i++) {
                ps.setString(i + 1, thamSo[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date ngaySinh = rs.getDate("NgaySinh");      // null nếu để trống
                    ds.add(new NhanVien(
                            rs.getString("MaNV"),
                            rs.getString("HoTen"),
                            rs.getString("GioiTinh"),
                            ngaySinh,
                            rs.getString("SoDienThoai"),
                            rs.getString("ChucVu"),
                            rs.getLong("Luong"),
                            rs.getDate("NgayVaoLam")));
                }
            }
        }
        return ds;
    }
}
