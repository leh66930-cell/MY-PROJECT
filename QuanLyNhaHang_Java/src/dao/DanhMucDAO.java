package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ketnoi.KetNoi;


public class DanhMucDAO {

    public List<String[]> layLoaiMon() throws SQLException {
        return truyVan("SELECT MaLoai, TenLoai FROM LoaiMon ORDER BY MaLoai", false);
    }

    public List<String[]> layBan() throws SQLException {
        return truyVan("SELECT MaBan, TenBan, SoChoNgoi, ViTri FROM BanAn ORDER BY MaBan", true);
    }

    public List<String[]> layNhanVien() throws SQLException {
        return truyVan("SELECT MaNV, HoTen FROM NhanVien ORDER BY MaNV", false);
    }

    public List<String[]> layKhachHang() throws SQLException {
        return truyVan("SELECT MaKH, HoTen FROM KhachHang ORDER BY MaKH", false);
    }

    private List<String[]> truyVan(String sql, boolean laBan) throws SQLException {
        List<String[]> ds = new ArrayList<String[]>();
        try (Connection con = KetNoi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String ma = rs.getString(1);
                String ten = rs.getString(2);
                if (laBan) {
                    ten = ten + " (" + rs.getInt(3) + " chỗ, " + rs.getString(4) + ")";
                }
                ds.add(new String[]{ma, ten});
            }
        }
        return ds;
    }
}
