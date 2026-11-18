package giaodien;

import dao.NhanVienDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import model.NhanVien;

/** Form quản lý nhân viên: xem danh sách, thêm, sửa, xóa, tìm kiếm. */
public class FormNhanVien extends JFrame implements ActionListener, ListSelectionListener {

    // Các ô nhập
    private JTextField txtMa = new JTextField();
    private JTextField txtHoTen = new JTextField();
    private JComboBox<String> cboGioiTinh = new JComboBox<String>(new String[]{"Nam", "Nữ"});
    private JTextField txtNgaySinh = new JTextField();
    private JTextField txtSdt = new JTextField();
    private JComboBox<String> cboChucVu
            = new JComboBox<String>(new String[]{"Quản lý", "Thu ngân", "Phục vụ", "Đầu bếp"});
    private JTextField txtLuong = new JTextField();
    private JTextField txtNgayVaoLam = new JTextField();
    private JTextField txtTimKiem = new JTextField(22);

    // Các nút
    private JButton btnThem = new JButton("Thêm");
    private JButton btnSua = new JButton("Sửa");
    private JButton btnXoa = new JButton("Xóa");
    private JButton btnLamMoi = new JButton("Làm mới");
    private JButton btnTim = new JButton("Tìm kiếm");
    private JButton btnTatCa = new JButton("Hiện tất cả");

    // Bảng hiển thị
    private DefaultTableModel modelBang = TienIch.taoModel(new String[]{
        "Mã NV", "Họ tên", "Giới tính", "Ngày sinh", "Số điện thoại", "Chức vụ", "Lương (VNĐ)", "Ngày vào làm"});
    private JTable bang = new JTable(modelBang);

    private NhanVienDAO dao = new NhanVienDAO();
    private List<NhanVien> dsHienTai = new ArrayList<NhanVien>();

    public FormNhanVien() {
        setTitle("Quản lý nhân viên");
        setSize(1000, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        taoGiaoDien();
        taiBang("");
        lamMoi();
    }

    private void taoGiaoDien() {
        JLabel lblTieuDe = new JLabel("QUẢN LÝ NHÂN VIÊN", JLabel.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTieuDe.setForeground(new Color(0, 102, 153));
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(12, 0, 8, 0));

        // 4 dòng x 4 cột: nhãn, ô, nhãn, ô
        JPanel pnlNhap = new JPanel(new GridLayout(4, 4, 12, 12));
        pnlNhap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 15, 0, 15),
                BorderFactory.createTitledBorder("Thông tin nhân viên")));
        pnlNhap.add(new JLabel("  Mã nhân viên:"));
        pnlNhap.add(txtMa);
        pnlNhap.add(new JLabel("  Họ và tên:"));
        pnlNhap.add(txtHoTen);
        pnlNhap.add(new JLabel("  Giới tính:"));
        pnlNhap.add(cboGioiTinh);
        pnlNhap.add(new JLabel("  Ngày sinh (dd/MM/yyyy):"));
        pnlNhap.add(txtNgaySinh);
        pnlNhap.add(new JLabel("  Số điện thoại:"));
        pnlNhap.add(txtSdt);
        pnlNhap.add(new JLabel("  Chức vụ:"));
        pnlNhap.add(cboChucVu);
        pnlNhap.add(new JLabel("  Lương (VNĐ):"));
        pnlNhap.add(txtLuong);
        pnlNhap.add(new JLabel("  Ngày vào làm (dd/MM/yyyy):"));
        pnlNhap.add(txtNgayVaoLam);

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
        TienIch.datDoRong(bang, 60, 170, 70, 90, 110, 90, 100, 100);
        TienIch.canPhai(bang, 6);

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
        for (NhanVien nv : dsHienTai) {
            modelBang.addRow(new Object[]{
                nv.getMaNV(), nv.getHoTen(), nv.getGioiTinh(), TienIch.hienNgay(nv.getNgaySinh()),
                nv.getSoDienThoai(), nv.getChucVu(), TienIch.dinhDangTien(nv.getLuong()),
                TienIch.hienNgay(nv.getNgayVaoLam())});
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
        cboGioiTinh.setSelectedIndex(0);
        txtNgaySinh.setText("");
        txtSdt.setText("");
        cboChucVu.setSelectedIndex(0);
        txtLuong.setText("");
        txtNgayVaoLam.setText(TienIch.ngayHomNay());
        txtHoTen.requestFocus();
    }

    /** Đọc và kiểm tra dữ liệu trên form. Trả về null nếu dữ liệu chưa hợp lệ. */
    private NhanVien docForm() {
        String ma = txtMa.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSdt.getText().trim();

        if (ma.isEmpty() || hoTen.isEmpty() || sdt.isEmpty()) {
            TienIch.canhBao(this, "Vui lòng nhập đầy đủ mã nhân viên, họ tên và số điện thoại!");
            return null;
        }
        if (ma.length() > 10) {
            TienIch.canhBao(this, "Mã nhân viên tối đa 10 ký tự!");
            return null;
        }
        if (!sdt.matches("\\d{9,11}")) {
            TienIch.canhBao(this, "Số điện thoại chỉ gồm 9 đến 11 chữ số!");
            txtSdt.requestFocus();
            return null;
        }

        long luong;
        try {
            luong = TienIch.docSo(txtLuong.getText());
        } catch (NumberFormatException ex) {
            TienIch.canhBao(this, "Lương phải là một số nguyên, ví dụ 6500000!");
            txtLuong.requestFocus();
            return null;
        }
        if (luong < 0) {
            TienIch.canhBao(this, "Lương không được âm!");
            txtLuong.requestFocus();
            return null;
        }

        java.sql.Date ngaySinh = null;                       // ngày sinh có thể để trống
        String chuoiNgaySinh = txtNgaySinh.getText().trim();
        if (!chuoiNgaySinh.isEmpty()) {
            try {
                ngaySinh = TienIch.docNgay(chuoiNgaySinh);
            } catch (DateTimeException ex) {
                TienIch.canhBao(this, "Ngày sinh không hợp lệ!\nHãy nhập theo dạng dd/MM/yyyy, ví dụ 25/07/2000");
                txtNgaySinh.requestFocus();
                return null;
            }
        }

        String chuoiVaoLam = txtNgayVaoLam.getText().trim();
        if (chuoiVaoLam.isEmpty()) {
            chuoiVaoLam = TienIch.ngayHomNay();              // để trống thì lấy ngày hôm nay
        }
        java.sql.Date ngayVaoLam;
        try {
            ngayVaoLam = TienIch.docNgay(chuoiVaoLam);
        } catch (DateTimeException ex) {
            TienIch.canhBao(this, "Ngày vào làm không hợp lệ!\nHãy nhập theo dạng dd/MM/yyyy, ví dụ 10/01/2024");
            txtNgayVaoLam.requestFocus();
            return null;
        }

        return new NhanVien(ma, hoTen, cboGioiTinh.getSelectedItem().toString(), ngaySinh, sdt,
                cboChucVu.getSelectedItem().toString(), luong, ngayVaoLam);
    }

    private void themNhanVien() {
        NhanVien nv = docForm();
        if (nv == null) {
            return;
        }
        try {
            if (dao.tonTai(nv.getMaNV())) {
                TienIch.canhBao(this, "Mã nhân viên " + nv.getMaNV() + " đã tồn tại! Hãy nhập mã khác.");
                return;
            }
            dao.them(nv);
            TienIch.thongBao(this, "Thêm nhân viên thành công!");
            txtTimKiem.setText("");
            taiBang("");
            lamMoi();
        } catch (SQLException ex) {
            TienIch.baoLoi(this, ex);
        }
    }

    private void suaNhanVien() {
        NhanVien nv = docForm();
        if (nv == null) {
            return;
        }
        try {
            int soDong = dao.sua(nv);
            if (soDong == 0) {
                TienIch.canhBao(this, "Không tìm thấy nhân viên có mã " + nv.getMaNV()
                        + ". Hãy chọn nhân viên cần sửa trong bảng!");
                return;
            }
            TienIch.thongBao(this, "Cập nhật nhân viên thành công!");
            taiBang(txtTimKiem.getText().trim());
        } catch (SQLException ex) {
            TienIch.baoLoi(this, ex);
        }
    }

    private void xoaNhanVien() {
        String ma = txtMa.getText().trim();
        if (ma.isEmpty()) {
            TienIch.canhBao(this, "Hãy chọn nhân viên cần xóa trong bảng!");
            return;
        }
        if (!TienIch.xacNhan(this, "Bạn có chắc muốn xóa nhân viên " + ma + " - " + txtHoTen.getText().trim() + "?")) {
            return;
        }
        try {
            int soDong = dao.xoa(ma);
            if (soDong == 0) {
                TienIch.canhBao(this, "Không tìm thấy nhân viên có mã " + ma);
                return;
            }
            TienIch.thongBao(this, "Đã xóa nhân viên!");
            taiBang(txtTimKiem.getText().trim());
            lamMoi();
        } catch (SQLException ex) {
            // Nhân viên đã lập hóa đơn thì không xóa được (khóa ngoại)
            TienIch.baoLoi(this, ex);
        }
    }

    private void timKiem() {
        String tuKhoa = txtTimKiem.getText().trim();
        taiBang(tuKhoa);
        if (!tuKhoa.isEmpty() && dsHienTai.isEmpty()) {
            TienIch.thongBao(this, "Không tìm thấy nhân viên nào phù hợp với \"" + tuKhoa + "\"");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object nguon = e.getSource();
        if (nguon == btnThem) {
            themNhanVien();
        } else if (nguon == btnSua) {
            suaNhanVien();
        } else if (nguon == btnXoa) {
            xoaNhanVien();
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
        NhanVien nv = dsHienTai.get(dong);
        txtMa.setText(nv.getMaNV());
        txtHoTen.setText(nv.getHoTen());
        cboGioiTinh.setSelectedItem(nv.getGioiTinh());
        txtNgaySinh.setText(TienIch.hienNgay(nv.getNgaySinh()));
        txtSdt.setText(nv.getSoDienThoai());
        cboChucVu.setSelectedItem(nv.getChucVu());
        txtLuong.setText(String.valueOf(nv.getLuong()));
        txtNgayVaoLam.setText(TienIch.hienNgay(nv.getNgayVaoLam()));
    }
}
