package giaodien;

import dao.DanhMucDAO;
import dao.MonAnDAO;
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
import model.MonAn;

/** Form quản lý món ăn: xem danh sách, thêm, sửa, xóa, tìm kiếm. */
public class FormMonAn extends JFrame implements ActionListener, ListSelectionListener {

    // Các ô nhập
    private JTextField txtMa = new JTextField();
    private JTextField txtTen = new JTextField();
    private JComboBox<String> cboLoai = new JComboBox<String>();
    private JTextField txtDonVi = new JTextField();
    private JTextField txtDonGia = new JTextField();
    private JComboBox<String> cboTrangThai = new JComboBox<String>(new String[]{"Còn bán", "Tạm hết"});
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
            new String[]{"Mã món", "Tên món", "Loại món", "ĐVT", "Đơn giá (VNĐ)", "Trạng thái"});
    private JTable bang = new JTable(modelBang);

    private MonAnDAO dao = new MonAnDAO();
    private List<MonAn> dsHienTai = new ArrayList<MonAn>();      // các món đang hiện trong bảng
    private List<String[]> dsLoai = new ArrayList<String[]>();   // các loại món (mã, tên) trong ô chọn

    public FormMonAn() {
        setTitle("Quản lý món ăn");
        setSize(950, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        taoGiaoDien();
        napLoaiMon();
        taiBang("");
        lamMoi();
    }

    private void taoGiaoDien() {
        JLabel lblTieuDe = new JLabel("QUẢN LÝ MÓN ĂN", JLabel.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTieuDe.setForeground(new Color(0, 102, 153));
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(12, 0, 8, 0));

        // Khu vực nhập thông tin (3 dòng x 4 cột: nhãn, ô, nhãn, ô)
        JPanel pnlNhap = new JPanel(new GridLayout(3, 4, 12, 12));
        pnlNhap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 15, 0, 15),
                BorderFactory.createTitledBorder("Thông tin món ăn")));
        pnlNhap.add(new JLabel("  Mã món:"));
        pnlNhap.add(txtMa);
        pnlNhap.add(new JLabel("  Tên món:"));
        pnlNhap.add(txtTen);
        pnlNhap.add(new JLabel("  Loại món:"));
        pnlNhap.add(cboLoai);
        pnlNhap.add(new JLabel("  Đơn vị tính:"));
        pnlNhap.add(txtDonVi);
        pnlNhap.add(new JLabel("  Đơn giá (VNĐ):"));
        pnlNhap.add(txtDonGia);
        pnlNhap.add(new JLabel("  Trạng thái:"));
        pnlNhap.add(cboTrangThai);

        JPanel pnlNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        pnlNut.add(btnThem);
        pnlNut.add(btnSua);
        pnlNut.add(btnXoa);
        pnlNut.add(btnLamMoi);

        JPanel pnlTren = new JPanel(new BorderLayout());
        pnlTren.add(lblTieuDe, BorderLayout.NORTH);
        pnlTren.add(pnlNhap, BorderLayout.CENTER);
        pnlTren.add(pnlNut, BorderLayout.SOUTH);

        // Khu vực tìm kiếm + bảng
        JPanel pnlTim = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pnlTim.add(new JLabel("Tìm theo mã hoặc tên món:"));
        pnlTim.add(txtTimKiem);
        pnlTim.add(btnTim);
        pnlTim.add(btnTatCa);

        bang.setRowHeight(26);
        bang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bang.getTableHeader().setFont(bang.getTableHeader().getFont().deriveFont(Font.BOLD));
        TienIch.datDoRong(bang, 70, 230, 120, 70, 120, 100);
        TienIch.canPhai(bang, 4);

        JPanel pnlGiua = new JPanel(new BorderLayout());
        pnlGiua.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        pnlGiua.add(pnlTim, BorderLayout.NORTH);
        pnlGiua.add(new JScrollPane(bang), BorderLayout.CENTER);

        add(pnlTren, BorderLayout.NORTH);
        add(pnlGiua, BorderLayout.CENTER);

        // Gắn sự kiện
        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnXoa.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnTim.addActionListener(this);
        btnTatCa.addActionListener(this);
        txtTimKiem.addActionListener(this);                      // nhấn Enter trong ô tìm kiếm
        bang.getSelectionModel().addListSelectionListener(this);
    }

    /** Đổ danh sách loại món từ CSDL vào ô chọn. */
    private void napLoaiMon() {
        try {
            dsLoai = new DanhMucDAO().layLoaiMon();
        } catch (SQLException ex) {
            TienIch.baoLoi(this, ex);
            return;
        }
        cboLoai.removeAllItems();
        for (String[] loai : dsLoai) {
            cboLoai.addItem(loai[0] + " - " + loai[1]);
        }
    }

    /** Tải dữ liệu vào bảng. tuKhoa rỗng = hiện tất cả. */
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
        for (MonAn m : dsHienTai) {
            modelBang.addRow(new Object[]{
                m.getMaMon(), m.getTenMon(), m.getTenLoai(), m.getDonViTinh(),
                TienIch.dinhDangTien(m.getDonGia()), m.getTrangThai()});
        }
    }

    /** Xóa trắng các ô nhập và gợi ý sẵn mã món mới. */
    private void lamMoi() {
        bang.clearSelection();
        txtMa.setText("");
        try {
            txtMa.setText(dao.maTiepTheo());
        } catch (SQLException ex) {
            // không lấy được mã gợi ý thì để trống, người dùng tự nhập
        }
        txtTen.setText("");
        txtDonVi.setText("");
        txtDonGia.setText("");
        if (cboLoai.getItemCount() > 0) {
            cboLoai.setSelectedIndex(0);
        }
        cboTrangThai.setSelectedIndex(0);
        txtTen.requestFocus();
    }

    /** Đọc và kiểm tra dữ liệu trên form. Trả về null nếu dữ liệu chưa hợp lệ. */
    private MonAn docForm() {
        String ma = txtMa.getText().trim();
        String ten = txtTen.getText().trim();
        String donVi = txtDonVi.getText().trim();

        if (ma.isEmpty() || ten.isEmpty() || donVi.isEmpty()) {
            TienIch.canhBao(this, "Vui lòng nhập đầy đủ mã món, tên món và đơn vị tính!");
            return null;
        }
        if (ma.length() > 10) {
            TienIch.canhBao(this, "Mã món tối đa 10 ký tự!");
            return null;
        }
        int viTriLoai = cboLoai.getSelectedIndex();
        if (viTriLoai < 0) {
            TienIch.canhBao(this, "Chưa có loại món nào. Hãy thêm loại món vào CSDL trước!");
            return null;
        }
        long donGia;
        try {
            donGia = TienIch.docSo(txtDonGia.getText());
        } catch (NumberFormatException ex) {
            TienIch.canhBao(this, "Đơn giá phải là một số nguyên, ví dụ 45000!");
            txtDonGia.requestFocus();
            return null;
        }
        if (donGia <= 0) {
            TienIch.canhBao(this, "Đơn giá phải lớn hơn 0!");
            txtDonGia.requestFocus();
            return null;
        }
        String maLoai = dsLoai.get(viTriLoai)[0];
        String tenLoai = dsLoai.get(viTriLoai)[1];
        String trangThai = cboTrangThai.getSelectedItem().toString();
        return new MonAn(ma, ten, maLoai, tenLoai, donVi, donGia, trangThai);
    }

    private void themMon() {
        MonAn m = docForm();
        if (m == null) {
            return;
        }
        try {
            if (dao.tonTai(m.getMaMon())) {
                TienIch.canhBao(this, "Mã món " + m.getMaMon() + " đã tồn tại! Hãy nhập mã khác.");
                return;
            }
            dao.them(m);
            TienIch.thongBao(this, "Thêm món ăn thành công!");
            txtTimKiem.setText("");
            taiBang("");
            lamMoi();
        } catch (SQLException ex) {
            TienIch.baoLoi(this, ex);
        }
    }

    private void suaMon() {
        MonAn m = docForm();
        if (m == null) {
            return;
        }
        try {
            int soDong = dao.sua(m);
            if (soDong == 0) {
                TienIch.canhBao(this, "Không tìm thấy món có mã " + m.getMaMon()
                        + ". Hãy chọn món cần sửa trong bảng!");
                return;
            }
            TienIch.thongBao(this, "Cập nhật món ăn thành công!");
            taiBang(txtTimKiem.getText().trim());
        } catch (SQLException ex) {
            TienIch.baoLoi(this, ex);
        }
    }

    private void xoaMon() {
        String ma = txtMa.getText().trim();
        if (ma.isEmpty()) {
            TienIch.canhBao(this, "Hãy chọn món cần xóa trong bảng!");
            return;
        }
        if (!TienIch.xacNhan(this, "Bạn có chắc muốn xóa món " + ma + " - " + txtTen.getText().trim() + "?")) {
            return;
        }
        try {
            int soDong = dao.xoa(ma);
            if (soDong == 0) {
                TienIch.canhBao(this, "Không tìm thấy món có mã " + ma);
                return;
            }
            TienIch.thongBao(this, "Đã xóa món ăn!");
            taiBang(txtTimKiem.getText().trim());
            lamMoi();
        } catch (SQLException ex) {
            // Món đã nằm trong hóa đơn thì SQL Server không cho xóa (khóa ngoại)
            TienIch.baoLoi(this, ex);
        }
    }

    private void timKiem() {
        String tuKhoa = txtTimKiem.getText().trim();
        if (tuKhoa.isEmpty()) {
            taiBang("");
            return;
        }
        taiBang(tuKhoa);
        if (dsHienTai.isEmpty()) {
            TienIch.thongBao(this, "Không tìm thấy món ăn nào phù hợp với \"" + tuKhoa + "\"");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object nguon = e.getSource();
        if (nguon == btnThem) {
            themMon();
        } else if (nguon == btnSua) {
            suaMon();
        } else if (nguon == btnXoa) {
            xoaMon();
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
        MonAn m = dsHienTai.get(dong);
        txtMa.setText(m.getMaMon());
        txtTen.setText(m.getTenMon());
        txtDonVi.setText(m.getDonViTinh());
        txtDonGia.setText(String.valueOf(m.getDonGia()));
        cboTrangThai.setSelectedItem(m.getTrangThai());
        for (int i = 0; i < dsLoai.size(); i++) {
            if (dsLoai.get(i)[0].equals(m.getMaLoai())) {
                cboLoai.setSelectedIndex(i);
                break;
            }
        }
    }
}
