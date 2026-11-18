package ketnoi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Lớp kết nối đến cơ sở dữ liệu QuanLyNhaHang trên SQL Server.
 * Các lớp khác chỉ cần gọi KetNoi.getConnection() để lấy kết nối.
 */
public class KetNoi {

    // ===== CHỈ CẦN SỬA URL, USER, PASSWORD CHO ĐÚNG VỚI MÁY CỦA BẠN =====

    // Cách 1 (phổ biến): SQL Server cài mặc định, cổng 1433
    private static final String URL
            = "jdbc:sqlserver://localhost:1433;databaseName=QuanLyNhaHang;"
            + "encrypt=true;trustServerCertificate=true";

    // Cách 2: nếu dùng SQL Server Express có tên instance (ví dụ SQLEXPRESS) thì thay URL bằng:
    // "jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=QuanLyNhaHang;encrypt=true;trustServerCertificate=true"

    private static final String USER = "sa";
    private static final String PASSWORD = "123456";   // đổi thành mật khẩu SQL Server của bạn

    /** Mở một kết nối mới đến CSDL. Nơi gọi chịu trách nhiệm đóng kết nối (dùng try-with-resources). */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Chưa thêm thư viện mssql-jdbc (file .jar) vào Libraries của project!");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
