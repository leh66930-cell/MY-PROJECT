package giaodien;

import dao.KhachHangDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import model.KhachHang;

/** Form quản lý khách hàng: xem danh sách, thêm, sửa, xóa, tìm kiếm. */
public class FormKhachHang extends JFrame implements ActionListener, ListSelectionListener {

    // Các ô nhập
    private JTextField txtMa = new JTextField();
    private JTextField txtHoTen = new JTextField();
    private JTextField txtSdt = new JTextField();
    private JTextField txtDiaChi = new JTextField();
    private JTextField txtDiem = new JTextField();
    private JTextField txtTimKiem = new JTextField(22);

    // Các nút
    private JButton btnThem = new JButton("Thêm");
    private JButton btnSua = new JButton("Sửa");
    private JButton btnXoa = new JButton("Xóa");
    private JButton btnLamMoi = new JButton("Làm mới");
    private JButton btnTim = new JButton("Tìm kiếm");
    private JButton btnTatCa = new JButton("Hiện tất cả");

    // Bảng hiển thị
    private DefaultTableModel modelBang = TienIch.taoModel(
            new String[]{"Mã KH", "Họ tên", "Số điện thoại", "Địa chỉ", "Điểm tích lũy"});
    private JTable bang = new JTable(modelBang);

    private KhachHangDAO dao = new KhachHangDAO();
    private List<KhachHang> dsHienTai = new ArrayList<KhachHang>();

    public FormKhachHang() {
        setTitle("Quản lý khách hàng");
        setSize(950, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        taoGiaoDien();
        taiBang("");
        lamMoi();
    }

    private void taoGiaoDien() {
        JLabel lblTieuDe = new JLabel("QUẢN LÝ KHÁCH HÀNG", JLabel.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTieuDe.setForeground(new Color(0, 102, 153));
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(12, 0, 8, 0));

        // 3 dòng x 4 cột (ô cuối để trống cho cân đối)
        JPanel pnlNhap = new JPanel(new GridLayout(3, 4, 12, 12));
        pnlNhap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 15, 0, 15),
                BorderFactory.createTitledBorder("Thông tin khách hàng")));
        pnlNhap.add(new JLabel("  Mã khách hàng:"));
        pnlNhap.add(txtMa);
        pnlNhap.add(new JLabel("  Họ và tên:"));
        pnlNhap.add(txtHoTen);
        pnlNhap.add(new JLabel("  Số điện thoại:"));
        pnlNhap.add(txtSdt);
        pnlNhap.add(new JLabel("  Địa chỉ:"));
        pnlNhap.add(txtDiaChi);
        pnlNhap.add(new JLabel("  Điểm tích lũy:"));
        pnlNhap.add(txtDiem);
        pnlNhap.add(new JLabel(""));
        pnlNhap.add(new JLabel(""));

        JPanel pnlNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        pnlNut.add(btnThem);
        pnlNut.add(btnSua);
        pnlNut.add(btnXoa);
        pnlNut.add(btnLamMoi);

        JPanel pnlTren = new JPanel(new BorderLayout());
        pnlTren.add(lblTieuDe, BorderLayout.NORTH);
        pnlTren.add(pnlNhap, BorderLayout.CENTER);
        pnlTren.add(pnlNut, BorderLayout.SOUTH);

        JPanel pnlTim = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pnlTim.add(new JLabel("Tìm theo mã, họ tên hoặc số điện thoại:"));
        pnlTim.add(txtTimKiem);
        pnlTim.add(btnTim);
        pnlTim.add(btnTatCa);

        bang.setRowHeight(26);
        bang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bang.getTableHeader().setFont(bang.getTableHeader().getFont().deriveFont(Font.BOLD));
        TienIch.datDoRong(bang, 70, 200, 120, 260, 100);
        TienIch.canPhai(bang, 4);

        JPanel pnlGiua = new JPanel(new BorderLayout());
        pnlGiua.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        pnlGiua.add(pnlTim, BorderLayout.NORTH);
        pnlGiua.add(new JScrollPane(bang), BorderLayout.CENTER);

        add(pnlTren, BorderLayout.NORTH);
        add(pnlGiua, BorderLayout.CENTER);

        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnXoa.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnTim.addActionListener(this);
        btnTatCa.addActionListener(this);
        txtTimKiem.addActionListener(this);
        bang.getSelectionModel().addListSelectionListener(this);
    }

    private void taiBang(String tuKhoa) {
        try {
            if (tuKhoa.isEmpty()) {
                dsHienTai = dao.layTatCa();
            } else {
                dsHienTai = dao.timKiem(tuKhoa);
            }
        } catch (SQLException ex) {
            TienIch.baoLoi(this, ex);
            return;
        }
        modelBang.setRowCount(0);
        for (KhachHang kh : dsHienTai) {
            modelBang.addRow(new Object[]{
                kh.getMaKH(), kh.getHoTen(), kh.getSoDienThoai(), kh.getDiaChi(), kh.getDiemTichLuy()});
        }
    }

    private void lamMoi() {
        bang.clearSelection();
        txtMa.setText("");
        try {
            txtMa.setText(dao.maTiepTheo());
        } catch (SQLException ex) {
            // không lấy được mã gợi ý thì để trống
        }
        txtHoTen.setText("");
        txtSdt.setText("");
        txtDiaChi.setText("");
        txtDiem.setText("0");
        txtHoTen.requestFocus();
    }

    /** Đọc và kiểm tra dữ liệu trên form. Trả về null nếu dữ liệu chưa hợp lệ. */
    private KhachHang docForm() {
        String ma = txtMa.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSdt.getText().trim();
        String diaChi = txtDiaChi.getText().trim();

        if (ma.isEmpty() || hoTen.isEmpty() || sdt.isEmpty()) {
            TienIch.canhBao(this, "Vui lòng nhập đầy đủ mã khách hàng, họ tên và số điện thoại!");
            return null;
        }
        if (ma.length() > 10) {
            TienIch.canhBao(this, "Mã khách hàng tối đa 10 ký tự!");
            return null;
        }
        if (!sdt.matches("\\d{9,11}")) {
            TienIch.canhBao(this, "Số điện thoại chỉ gồm 9 đến 11 chữ số!");
            txtSdt.requestFocus();
            return null;
        }

        int diem;
        try {
            // ô để trống thì hiểu là 0 điểm
            diem = txtDiem.getText().trim().isEmpty() ? 0 : (int) TienIch.docSo(txtDiem.getText());
        } catch (NumberFormatException ex) {
            TienIch.canhBao(this, "Điểm tích lũy phải là một số nguyên!");
            txtDiem.requestFocus();
            return null;
        }
        if (diem < 0) {
            TienIch.canhBao(this, "Điểm tích lũy không được âm!");
            txtDiem.requestFocus();
            return null;
        }
        return new KhachHang(ma, hoTen, sdt, diaChi, diem);
    }

    private void themKhach() {
        KhachHang kh = docForm();
        if (kh == null) {
            return;
        }
        try {
            if (dao.tonTai(kh.getMaKH())) {
                TienIch.canhBao(this, "Mã khách hàng " + kh.getMaKH() + " đã tồn tại! Hãy nhập mã khác.");
                return;
            }
            dao.them(kh);
            TienIch.thongBao(this, "Thêm khách hàng thành công!");
            txtTimKiem.setText("");
            taiBang("");
            lamMoi();
        } catch (SQLException ex) {
            TienIch.baoLoi(this, ex);
        }
    }

    private void suaKhach() {
        KhachHang kh = docForm();
        if (kh == null) {
            return;
        }
        try {
            int soDong = dao.sua(kh);
            if (soDong == 0) {
                TienIch.canhBao(this, "Không tìm thấy khách hàng có mã " + kh.getMaKH()
                        + ". Hãy chọn khách hàng cần sửa trong bảng!");
                return;
            }
            TienIch.thongBao(this, "Cập nhật khách hàng thành công!");
            taiBang(txtTimKiem.getText().trim());
        } catch (SQLException ex) {
            TienIch.baoLoi(this, ex);
        }
    }

    private void xoaKhach() {
        String ma = txtMa.getText().trim();
        if (ma.isEmpty()) {
            TienIch.canhBao(this, "Hãy chọn khách hàng cần xóa trong bảng!");
            return;
        }
        if (!TienIch.xacNhan(this, "Bạn có chắc muốn xóa khách hàng " + ma + " - " + txtHoTen.getText().trim() + "?")) {
            return;
        }
        try {
            int soDong = dao.xoa(ma);
            if (soDong == 0) {
                TienIch.canhBao(this, "Không tìm thấy khách hàng có mã " + ma);
                return;
            }
            TienIch.thongBao(this, "Đã xóa khách hàng!");
            taiBang(txtTimKiem.getText().trim());
            lamMoi();
        } catch (SQLException ex) {
            // Khách hàng đã có hóa đơn thì không xóa được (khóa ngoại)
            TienIch.baoLoi(this, ex);
        }
    }

    private void timKiem() {
        String tuKhoa = txtTimKiem.getText().trim();
        taiBang(tuKhoa);
        if (!tuKhoa.isEmpty() && dsHienTai.isEmpty()) {
            TienIch.thongBao(this, "Không tìm thấy khách hàng nào phù hợp với \"" + tuKhoa + "\"");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object nguon = e.getSource();
        if (nguon == btnThem) {
            themKhach();
        } else if (nguon == btnSua) {
            suaKhach();
        } else if (nguon == btnXoa) {
            xoaKhach();
        } else if (nguon == btnLamMoi) {
            lamMoi();
        } else if (nguon == btnTim || nguon == txtTimKiem) {
            timKiem();
        } else if (nguon == btnTatCa) {
            txtTimKiem.setText("");
            taiBang("");
        }
    }

    /** Khi chọn một dòng trong bảng thì đưa dữ liệu lên các ô nhập. */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        int dong = bang.getSelectedRow();
        if (dong < 0 || dong >= dsHienTai.size()) {
            return;
        }
        KhachHang kh = dsHienTai.get(dong);
        txtMa.setText(kh.getMaKH());
        txtHoTen.setText(kh.getHoTen());
        txtSdt.setText(kh.getSoDienThoai());
        txtDiaChi.setText(kh.getDiaChi());
        txtDiem.setText(String.valueOf(kh.getDiemTichLuy()));
    }
}
