package giaodien;

import java.awt.Component;
import java.awt.Font;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Enumeration;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/** Các hàm dùng chung cho tất cả các form: thông báo, định dạng tiền, đọc số, đọc ngày... */
public class TienIch {

    // Ngày nhập theo dạng dd/MM/yyyy (ví dụ 25/07/2000), kiểm tra chặt (không cho phép 31/02)
    private static final DateTimeFormatter DINH_DANG_NGAY
            = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    /** Đặt font chữ chung (hỗ trợ tiếng Việt) và chữ tiếng Việt cho các nút của hộp thoại. */
    public static void datFontChung() {
        FontUIResource font = new FontUIResource(new Font("Segoe UI", Font.PLAIN, 14));
        Enumeration<Object> cacKhoa = UIManager.getDefaults().keys();
        while (cacKhoa.hasMoreElements()) {
            Object khoa = cacKhoa.nextElement();
            if (UIManager.get(khoa) instanceof FontUIResource) {
                UIManager.put(khoa, font);
            }
        }
        UIManager.put("OptionPane.yesButtonText", "Có");
        UIManager.put("OptionPane.noButtonText", "Không");
        UIManager.put("OptionPane.cancelButtonText", "Hủy");
    }

    /** Tạo bảng dữ liệu chỉ để xem (không cho sửa trực tiếp trên ô). */
    public static DefaultTableModel taoModel(String[] tenCot) {
        return new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int dong, int cot) {
                return false;
            }
        };
    }

    /** Căn phải các cột số (ví dụ cột tiền). */
    public static void canPhai(JTable bang, int... cacCot) {
        DefaultTableCellRenderer canPhai = new DefaultTableCellRenderer();
        canPhai.setHorizontalAlignment(JLabel.RIGHT);
        for (int cot : cacCot) {
            bang.getColumnModel().getColumn(cot).setCellRenderer(canPhai);
        }
    }

    /** Đặt độ rộng cho từng cột, theo thứ tự từ trái sang phải. */
    public static void datDoRong(JTable bang, int... doRong) {
        for (int i = 0; i < doRong.length; i++) {
            bang.getColumnModel().getColumn(i).setPreferredWidth(doRong[i]);
        }
    }

    /** Định dạng số tiền: 1310000 -> "1.310.000". */
    public static String dinhDangTien(long soTien) {
        return NumberFormat.getInstance(Locale.forLanguageTag("vi-VN")).format(soTien);
    }

    /** Đọc số nguyên từ ô nhập, chấp nhận cả dạng có dấu chấm/phẩy như "45.000". */
    public static long docSo(String chuoi) throws NumberFormatException {
        String s = chuoi.trim().replace(".", "").replace(",", "");
        return Long.parseLong(s);
    }

    /** Đọc ngày dạng dd/MM/yyyy. Nếu sai định dạng sẽ ném DateTimeException. */
    public static java.sql.Date docNgay(String chuoi) {
        LocalDate ngay = LocalDate.parse(chuoi.trim(), DINH_DANG_NGAY);
        return java.sql.Date.valueOf(ngay);
    }

    /** Đổi ngày sang chuỗi dd/MM/yyyy (trả về chuỗi rỗng nếu ngày là null). */
    public static String hienNgay(java.sql.Date ngay) {
        if (ngay == null) {
            return "";
        }
        return ngay.toLocalDate().format(DINH_DANG_NGAY);
    }

    public static String ngayHomNay() {
        return LocalDate.now().format(DINH_DANG_NGAY);
    }

    // ---------- Các hộp thoại thông báo ----------
    public static void thongBao(Component cha, String noiDung) {
        JOptionPane.showMessageDialog(cha, noiDung, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void canhBao(Component cha, String noiDung) {
        JOptionPane.showMessageDialog(cha, noiDung, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean xacNhan(Component cha, String noiDung) {
        int chon = JOptionPane.showConfirmDialog(cha, noiDung, "Xác nhận",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return chon == JOptionPane.YES_OPTION;
    }

    /** Hiển thị lỗi SQL bằng tiếng Việt dễ hiểu (mã 547 = vi phạm khóa ngoại/CHECK, 2627/2601 = trùng khóa). */
    public static void baoLoi(Component cha, SQLException ex) {
        String noiDung;
        int maLoi = ex.getErrorCode();
        if (maLoi == 547) {
            noiDung = "Không thực hiện được vì vi phạm ràng buộc dữ liệu.\n"
                    + "Có thể dữ liệu này đang được dùng ở bảng khác (ví dụ đã có trong hóa đơn)\n"
                    + "hoặc giá trị nhập vào không hợp lệ.";
        } else if (maLoi == 2627 || maLoi == 2601) {
            noiDung = "Dữ liệu bị trùng (mã hoặc số điện thoại đã tồn tại).";
        } else {
            noiDung = "Lỗi cơ sở dữ liệu:\n" + ex.getMessage();
        }
        JOptionPane.showMessageDialog(cha, noiDung, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
