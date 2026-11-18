package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import ketnoi.KetNoi;

public class MaTuDong {

   
    public static String tiepTheo(String tenBang, String tenCot, String tienTo, int soChuSo) throws SQLException {
        String sql = "SELECT MAX(" + tenCot + ") FROM " + tenBang;
        String maLonNhat = null;
        try (Connection con = KetNoi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                maLonNhat = rs.getString(1);
            }
        }

        int so = 0;
        if (maLonNhat != null && maLonNhat.startsWith(tienTo)) {
            try {
                so = Integer.parseInt(maLonNhat.substring(tienTo.length()));
            } catch (NumberFormatException e) {
                so = 0;
            }
        }
        return tienTo + String.format("%0" + soChuSo + "d", so + 1);
    }
}
