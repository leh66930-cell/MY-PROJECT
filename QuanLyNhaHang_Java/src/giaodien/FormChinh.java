package giaodien;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import ketnoi.KetNoi;

/** Form chính (menu) của phần mềm. Chạy file này để mở chương trình. */
public class FormChinh extends JFrame implements ActionListener {

    private JButton btnMonAn = new JButton("Quản lý món ăn");
    private JButton btnNhanVien = new JButton("Quản lý nhân viên");
    private JButton btnKhachHang = new JButton("Quản lý khách hàng");
    private JButton btnHoaDon = new JButton("Lập & quản lý hóa đơn");
    private JButton btnKiemTra = new JButton("Kiểm tra kết nối CSDL");
    private JButton btnThoat = new JButton("Thoát");
    private JLabel lblTrangThai = new JLabel(" ", JLabel.CENTER);

    public FormChinh() {
        setTitle("Phần mềm quản lý nhà hàng");
        setSize(600, 470);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel lblTieuDe = new JLabel("QUẢN LÝ NHÀ HÀNG", JLabel.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTieuDe.setForeground(new Color(0, 102, 153));
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(25, 0, 5, 0));

        JLabel lblPhu = new JLabel("Chọn chức năng bên dưới để bắt đầu", JLabel.CENTER);
        lblPhu.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel pnlTieuDe = new JPanel(new BorderLayout());
        pnlTieuDe.add(lblTieuDe, BorderLayout.CENTER);
        pnlTieuDe.add(lblPhu, BorderLayout.SOUTH);

        JPanel pnlNut = new JPanel(new GridLayout(3, 2, 15, 15));
        pnlNut.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        JButton[] cacNut = {btnMonAn, btnNhanVien, btnKhachHang, btnHoaDon, btnKiemTra, btnThoat};
        for (JButton nut : cacNut) {
            nut.setFont(new Font("Segoe UI", Font.BOLD, 15));
            nut.addActionListener(this);
            pnlNut.add(nut);
        }

        lblTrangThai.setBorder(BorderFactory.createEmptyBorder(5, 0, 12, 0));

        add(pnlTieuDe, BorderLayout.NORTH);
        add(pnlNut, BorderLayout.CENTER);
        add(lblTrangThai, BorderLayout.SOUTH);

        // Kiểm tra kết nối ngay khi mở chương trình
        String loi = thuKetNoi();
        if (loi != null) {
            TienIch.canhBao(this, "Chưa kết nối được cơ sở dữ liệu!\n\n" + loi
                    + "\n\nHãy kiểm tra lại URL, tài khoản, mật khẩu trong file ketnoi/KetNoi.java");
        }
    }

    /** Thử kết nối CSDL. Trả về null nếu thành công, ngược lại trả về nội dung lỗi. */
    private String thuKetNoi() {
        try (Connection con = KetNoi.getConnection()) {
            lblTrangThai.setText("Đã kết nối cơ sở dữ liệu QuanLyNhaHang");
            lblTrangThai.setForeground(new Color(0, 128, 0));
            return null;
        } catch (SQLException ex) {
            lblTrangThai.setText("Chưa kết nối được cơ sở dữ liệu");
            lblTrangThai.setForeground(Color.RED);
            return ex.getMessage();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object nguon = e.getSource();
        if (nguon == btnMonAn) {
            new FormMonAn().setVisible(true);
        } else if (nguon == btnNhanVien) {
            new FormNhanVien().setVisible(true);
        } else if (nguon == btnKhachHang) {
            new FormKhachHang().setVisible(true);
        } else if (nguon == btnHoaDon) {
            new FormHoaDon().setVisible(true);
        } else if (nguon == btnKiemTra) {
            String loi = thuKetNoi();
            if (loi == null) {
                TienIch.thongBao(this, "Kết nối cơ sở dữ liệu thành công!");
            } else {
                TienIch.canhBao(this, "Kết nối thất bại:\n" + loi);
            }
        } else if (nguon == btnThoat) {
            if (TienIch.xacNhan(this, "Bạn có chắc muốn thoát chương trình?")) {
                System.exit(0);
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // nếu lỗi thì dùng giao diện mặc định của Java
        }
        TienIch.datFontChung();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FormChinh().setVisible(true);
            }
        });
    }
}
