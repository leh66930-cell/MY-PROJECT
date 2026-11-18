package giaodien;

import dao.DanhMucDAO;
import dao.HoaDonDAO;
import dao.MonAnDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import model.ChiTietHoaDon;
import model.HoaDon;
import model.MonAn;

/**
 * Form hóa đơn gồm 2 tab:
 * - Tab 1 "Lập hóa đơn mới": chọn bàn, nhân viên, khách hàng, thêm các món rồi lưu hóa đơn.
 * - Tab 2 "Danh sách hóa đơn": xem, tìm kiếm, thanh toán, xóa hóa đơn và xem chi tiết các món.
 */
public class FormHoaDon extends JFrame implements ActionListener, ListSelectionListener {

    private HoaDonDAO dao = new HoaDonDAO();

    // ===== Tab 1: lập hóa đơn mới =====
    private JTextField txtMaHD = new JTextField();
    private JTextField txtNgayLap = new JTextField();
    private JComboBox<String> cboBan = new JComboBox<String>();
    private JComboBox<String> cboNhanVien = new JComboBox<String>();
    private JComboBox<String> cboKhachHang = new JComboBox<String>();
    private JComboBox<String> cboTrangThai = new JComboBox<String>(
            new String[]{HoaDonDAO.CHUA_THANH_TOAN, HoaDonDAO.DA_THANH_TOAN});
    private JComboBox<String> cboMon = new JComboBox<String>();
    private JTextField txtSoLuong = new JTextField("1", 5);
    private JButton btnThemMon = new JButton("Thêm món");
    private JButton btnXoaMon = new JButton("Xóa món đã chọn");
    private JButton btnLamMoi = new JButton("Làm mới");
    private JButton btnLuu = new JButton("Lưu hóa đơn");
    private JLabel lblTong = new JLabel("Tổng tiền: 0 VNĐ", JLabel.RIGHT);

    private DefaultTableModel modelGio = TienIch.taoModel(
            new String[]{"Mã món", "Tên món", "Số lượng", "Đơn giá", "Thành tiền"});
    private JTable bangGio = new JTable(modelGio);

    // Dữ liệu song song với các ô chọn: vị trí được chọn trong ô chọn = vị trí trong danh sách
    private List<String[]> dsBan = new ArrayList<String[]>();
    private List<String[]> dsNhanVien = new ArrayList<String[]>();
    private List<String[]> dsKhachHang = new ArrayList<String[]>();   // phần tử đầu là "Khách vãng lai"
    private List<MonAn> dsMon = new ArrayList<MonAn>();
    private List<ChiTietHoaDon> gioHang = new ArrayList<ChiTietHoaDon>();   // các món đã chọn cho hóa đơn

    // ===== Tab 2: danh sách hóa đơn =====
    private JTextField txtTimKiem = new JTextField(12);
    private JButton btnTim = new JButton("Tìm kiếm");
    private JButton btnTatCa = new JButton("Hiện tất cả");
    private JButton btnThanhToan = new JButton("Thanh toán");
    private JButton btnXoaHD = new JButton("Xóa hóa đơn");

    private DefaultTableModel modelHD = TienIch.taoModel(new String[]{
        "Mã HĐ", "Bàn", "Nhân viên", "Khách hàng", "Ngày lập", "Trạng thái", "Tổng tiền (VNĐ)"});
    private JTable bangHD = new JTable(modelHD);
    private DefaultTableModel modelCT = TienIch.taoModel(
            new String[]{"Mã món", "Tên món", "Số lượng", "Đơn giá", "Thành tiền"});
    private JTable bangCT = new JTable(modelCT);
    private List<HoaDon> dsHoaDon = new ArrayList<HoaDon>();

    public FormHoaDon() {
        setTitle("Quản lý hóa đơn");
        setSize(1080, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel lblTieuDe = new JLabel("QUẢN LÝ HÓA ĐƠN", JLabel.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTieuDe.setForeground(new Color(0, 102, 153));
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(12, 0, 8, 0));

        JTabbedPane tab = new JTabbedPane();
        tab.addTab("Lập hóa đơn mới", taoTabLap());
        tab.addTab("Danh sách hóa đơn", taoTabDanhSach());

        add(lblTieuDe, BorderLayout.NORTH);
        add(tab, BorderLayout.CENTER);

        napDanhMuc();
        lamMoi();
        taiDanhSach("");
    }

    // ==================== TAB 1 ====================
    private JPanel taoTabLap() {
        JPanel pnlThongTin = new JPanel(new GridLayout(3, 4, 12, 12));
        pnlThongTin.setBorder(BorderFactory.createTitledBorder("Thông tin hóa đơn"));
        txtMaHD.setEditable(false);
        txtNgayLap.setEditable(false);
        pnlThongTin.add(new JLabel("  Mã hóa đơn:"));
        pnlThongTin.add(txtMaHD);
        pnlThongTin.add(new JLabel("  Ngày lập:"));
        pnlThongTin.add(txtNgayLap);
        pnlThongTin.add(new JLabel("  Bàn:"));
        pnlThongTin.add(cboBan);
        pnlThongTin.add(new JLabel("  Nhân viên lập:"));
        pnlThongTin.add(cboNhanVien);
        pnlThongTin.add(new JLabel("  Khách hàng:"));
        pnlThongTin.add(cboKhachHang);
        pnlThongTin.add(new JLabel("  Trạng thái:"));
        pnlThongTin.add(cboTrangThai);

        JPanel pnlChonMon = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        pnlChonMon.setBorder(BorderFactory.createTitledBorder("Chọn món"));
        cboMon.setPreferredSize(new Dimension(380, 28));
        pnlChonMon.add(new JLabel("Món ăn:"));
        pnlChonMon.add(cboMon);
        pnlChonMon.add(new JLabel("Số lượng:"));
        pnlChonMon.add(txtSoLuong);
        pnlChonMon.add(btnThemMon);

        JPanel pnlTren = new JPanel(new BorderLayout(0, 8));
        pnlTren.add(pnlThongTin, BorderLayout.NORTH);
        pnlTren.add(pnlChonMon, BorderLayout.CENTER);

        bangGio.setRowHeight(26);
        bangGio.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bangGio.getTableHeader().setFont(bangGio.getTableHeader().getFont().deriveFont(Font.BOLD));
        TienIch.datDoRong(bangGio, 80, 300, 80, 120, 130);
        TienIch.canPhai(bangGio, 2, 3, 4);

        lblTong.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTong.setForeground(Color.RED);

        JPanel pnlNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 5));
        pnlNut.add(btnXoaMon);
        pnlNut.add(btnLamMoi);
        pnlNut.add(btnLuu);

        JPanel pnlDuoi = new JPanel(new BorderLayout());
        pnlDuoi.add(lblTong, BorderLayout.NORTH);
        pnlDuoi.add(pnlNut, BorderLayout.SOUTH);

        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        p.add(pnlTren, BorderLayout.NORTH);
        p.add(new JScrollPane(bangGio), BorderLayout.CENTER);
        p.add(pnlDuoi, BorderLayout.SOUTH);

        btnThemMon.addActionListener(this);
        btnXoaMon.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnLuu.addActionListener(this);
        return p;
    }

    /** Đổ dữ liệu bàn, nhân viên, khách hàng, món ăn từ CSDL vào các ô chọn. */
    private void napDanhMuc() {
        try {
            DanhMucDAO dm = new DanhMucDAO();
            dsBan = dm.layBan();
            dsNhanVien = dm.layNhanVien();
            dsKhachHang = new ArrayList<String[]>();
            dsKhachHang.add(new String[]{null, "Khách vãng lai"});      // mã null = không gắn khách hàng
            dsKhachHang.addAll(dm.layKhachHang());
            dsMon = new MonAnDAO().layMonDangBan();
        } catch (SQLException ex) {
            TienIch.baoLoi(this, ex);
            return;
        }
        cboBan.removeAllItems();
        for (String[] b : dsBan) {
            cboBan.addItem(b[0] + " - " + b[1]);
        }
        cboNhanVien.removeAllItems();
        for (String[] nv : dsNhanVien) {
            cboNhanVien.addItem(nv[0] + " - " + nv[1]);
        }
        cboKhachHang.removeAllItems();
        for (String[] kh : dsKhachHang) {
            cboKhachHang.addItem(kh[0] == null ? kh[1] : kh[0] + " - " + kh[1]);
        }
        cboMon.removeAllItems();
        for (MonAn m : dsMon) {
            cboMon.addItem(m.getTenMon() + " - " + TienIch.dinhDangTien(m.getDonGia()) + " đ/" + m.getDonViTinh());
        }
    }

    /** Bắt đầu một hóa đơn mới: xóa giỏ món, lấy mã hóa đơn mới, cập nhật ngày giờ. */
    private void lamMoi() {
        gioHang.clear();
        capNhatGio();
        capNhatMaHD();
        txtSoLuong.setText("1");
        if (cboKhachHang.getItemCount() > 0) {
            cboKhachHang.setSelectedIndex(0);
        }
        cboTrangThai.setSelectedIndex(0);
    }

    /** Lấy mã hóa đơn kế tiếp và ngày giờ hiện tại để hiện lên form. */
    private void capNhatMaHD() {
        try {
            txtMaHD.setText(dao.maTiepTheo());
        } catch (SQLException ex) {
            TienIch.baoLoi(this, ex);
        }
        txtNgayLap.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
    }

    /** Vẽ lại bảng các món đã chọn và tính tổng tiền. */
    private void capNhatGio() {
        modelGio.setRowCount(0);
        long tong = 0;
        for (ChiTietHoaDon ct : gioHang) {
            modelGio.addRow(new Object[]{
                ct.getMaMon(), ct.getTenMon(), ct.getSoLuong(),
                TienIch.dinhDangTien(ct.getDonGia()), TienIch.dinhDangTien(ct.getThanhTien())});
            tong += ct.getThanhTien();
        }
        lblTong.setText("Tổng tiền: " + TienIch.dinhDangTien(tong) + " VNĐ");
    }

    private void themMon() {
        int viTri = cboMon.getSelectedIndex();
        if (viTri < 0) {
            TienIch.canhBao(this, "Chưa có món nào để chọn!");
            return;
        }
        long soLuong;
        try {
            soLuong = TienIch.docSo(txtSoLuong.getText());
        } catch (NumberFormatException ex) {
            TienIch.canhBao(this, "Số lượng phải là một số nguyên!");
            txtSoLuong.requestFocus();
            return;
        }
        if (soLuong <= 0 || soLuong > 1000) {
            TienIch.canhBao(this, "Số lượng phải từ 1 đến 1000!");
            txtSoLuong.requestFocus();
            return;
        }

        MonAn mon = dsMon.get(viTri);
        // Nếu món đã có trong hóa đơn thì cộng dồn số lượng
        for (ChiTietHoaDon ct : gioHang) {
            if (ct.getMaMon().equals(mon.getMaMon())) {
                ct.setSoLuong(ct.getSoLuong() + (int) soLuong);
                capNhatGio();
                return;
            }
        }
        gioHang.add(new ChiTietHoaDon(mon.getMaMon(), mon.getTenMon(), (int) soLuong, mon.getDonGia()));
        capNhatGio();
    }

    private void xoaMon() {
        int dong = bangGio.getSelectedRow();
        if (dong < 0) {
            TienIch.canhBao(this, "Hãy chọn món cần xóa trong bảng!");
            return;
        }
        gioHang.remove(dong);
        capNhatGio();
    }

    private void luuHoaDon() {
        if (gioHang.isEmpty()) {
            TienIch.canhBao(this, "Hóa đơn chưa có món nào!\nHãy chọn món và bấm \"Thêm món\".");
            return;
        }
        int viTriBan = cboBan.getSelectedIndex();
        int viTriNV = cboNhanVien.getSelectedIndex();
        if (viTriBan < 0 || viTriNV < 0) {
            TienIch.canhBao(this, "Chưa có dữ liệu bàn hoặc nhân viên trong CSDL!");
            return;
        }

        String maHD = txtMaHD.getText().trim();
        String maBan = dsBan.get(viTriBan)[0];
        String maNV = dsNhanVien.get(viTriNV)[0];
        String maKH = dsKhachHang.get(cboKhachHang.getSelectedIndex())[0];   // null nếu khách vãng lai
        String trangThai = cboTrangThai.getSelectedItem().toString();

        long tong = 0;
        for (ChiTietHoaDon ct : gioHang) {
            tong += ct.getThanhTien();
        }

        try {
            dao.luuHoaDon(maHD, maBan, maNV, maKH, new Timestamp(System.currentTimeMillis()), trangThai, gioHang);
            TienIch.thongBao(this, "Đã lưu hóa đơn " + maHD + "\nTổng tiền: " + TienIch.dinhDangTien(tong) + " VNĐ");
            lamMoi();
            txtTimKiem.setText("");
            taiDanhSach("");
        } catch (SQLException ex) {
            TienIch.baoLoi(this, ex);
        }
    }

    // ==================== TAB 2 ====================
    private JPanel taoTabDanhSach() {
        JPanel pnlTim = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pnlTim.add(new JLabel("Tìm (mã HĐ, bàn, khách):"));
        pnlTim.add(txtTimKiem);
        pnlTim.add(btnTim);
        pnlTim.add(btnTatCa);
        pnlTim.add(btnThanhToan);
        pnlTim.add(btnXoaHD);

        bangHD.setRowHeight(26);
        bangHD.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bangHD.getTableHeader().setFont(bangHD.getTableHeader().getFont().deriveFont(Font.BOLD));
        TienIch.datDoRong(bangHD, 70, 80, 150, 150, 130, 120, 130);
        TienIch.canPhai(bangHD, 6);

        bangCT.setRowHeight(26);
        bangCT.getTableHeader().setFont(bangCT.getTableHeader().getFont().deriveFont(Font.BOLD));
        TienIch.datDoRong(bangCT, 80, 300, 80, 120, 130);
        TienIch.canPhai(bangCT, 2, 3, 4);

        JScrollPane cuonHD = new JScrollPane(bangHD);
        cuonHD.setBorder(BorderFactory.createTitledBorder("Danh sách hóa đơn (chọn một dòng để xem chi tiết)"));
        JScrollPane cuonCT = new JScrollPane(bangCT);
        cuonCT.setBorder(BorderFactory.createTitledBorder("Chi tiết hóa đơn đang chọn"));

        JPanel pnlBang = new JPanel(new GridLayout(2, 1, 0, 8));
        pnlBang.add(cuonHD);
        pnlBang.add(cuonCT);

        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));
        p.add(pnlTim, BorderLayout.NORTH);
        p.add(pnlBang, BorderLayout.CENTER);

        btnTim.addActionListener(this);
        btnTatCa.addActionListener(this);
        btnThanhToan.addActionListener(this);
        btnXoaHD.addActionListener(this);
        txtTimKiem.addActionListener(this);
        bangHD.getSelectionModel().addListSelectionListener(this);
        return p;
    }

    private void taiDanhSach(String tuKhoa) {
        try {
            if (tuKhoa.isEmpty()) {
                dsHoaDon = dao.layTatCa();
            } else {
                dsHoaDon = dao.timKiem(tuKhoa);
            }
        } catch (SQLException ex) {
            TienIch.baoLoi(this, ex);
            return;
        }
        modelHD.setRowCount(0);
        modelCT.setRowCount(0);
        for (HoaDon hd : dsHoaDon) {
            modelHD.addRow(new Object[]{
                hd.getMaHD(), hd.getTenBan(), hd.getTenNV(), hd.getTenKH(),
                hd.getNgayLap(), hd.getTrangThai(), TienIch.dinhDangTien(hd.getTongTien())});
        }
    }

    private void timKiem() {
        String tuKhoa = txtTimKiem.getText().trim();
        taiDanhSach(tuKhoa);
        if (!tuKhoa.isEmpty() && dsHoaDon.isEmpty()) {
            TienIch.thongBao(this, "Không tìm thấy hóa đơn nào phù hợp với \"" + tuKhoa + "\"");
        }
    }

    /** Lấy hóa đơn đang được chọn trong bảng (null nếu chưa chọn). */
    private HoaDon hoaDonDangChon() {
        int dong = bangHD.getSelectedRow();
        if (dong < 0 || dong >= dsHoaDon.size()) {
            TienIch.canhBao(this, "Hãy chọn một hóa đơn trong bảng!");
            return null;
        }
        return dsHoaDon.get(dong);
    }

    private void thanhToan() {
        HoaDon hd = hoaDonDangChon();
        if (hd == null) {
            return;
        }
        if (hd.getTrangThai().equals(HoaDonDAO.DA_THANH_TOAN)) {
            TienIch.thongBao(this, "Hóa đơn " + hd.getMaHD() + " đã được thanh toán rồi.");
            return;
        }
        if (!TienIch.xacNhan(this, "Xác nhận thanh toán hóa đơn " + hd.getMaHD()
                + " với số tiền " + TienIch.dinhDangTien(hd.getTongTien()) + " VNĐ?")) {
            return;
        }
        try {
            dao.capNhatTrangThai(hd.getMaHD(), HoaDonDAO.DA_THANH_TOAN);
            TienIch.thongBao(this, "Đã thanh toán hóa đơn " + hd.getMaHD());
            taiDanhSach(txtTimKiem.getText().trim());
        } catch (SQLException ex) {
            TienIch.baoLoi(this, ex);
        }
    }

    private void xoaHoaDon() {
        HoaDon hd = hoaDonDangChon();
        if (hd == null) {
            return;
        }
        if (!TienIch.xacNhan(this, "Bạn có chắc muốn xóa hóa đơn " + hd.getMaHD()
                + " (kèm toàn bộ món trong hóa đơn)?")) {
            return;
        }
        try {
            dao.xoa(hd.getMaHD());
            TienIch.thongBao(this, "Đã xóa hóa đơn " + hd.getMaHD());
            taiDanhSach(txtTimKiem.getText().trim());
            capNhatMaHD();
        } catch (SQLException ex) {
            TienIch.baoLoi(this, ex);
        }
    }

    // ==================== SỰ KIỆN ====================
    @Override
    public void actionPerformed(ActionEvent e) {
        Object nguon = e.getSource();
        if (nguon == btnThemMon) {
            themMon();
        } else if (nguon == btnXoaMon) {
            xoaMon();
        } else if (nguon == btnLamMoi) {
            lamMoi();
        } else if (nguon == btnLuu) {
            luuHoaDon();
        } else if (nguon == btnTim || nguon == txtTimKiem) {
            timKiem();
        } else if (nguon == btnTatCa) {
            txtTimKiem.setText("");
            taiDanhSach("");
        } else if (nguon == btnThanhToan) {
            thanhToan();
        } else if (nguon == btnXoaHD) {
            xoaHoaDon();
        }
    }

    /** Khi chọn một hóa đơn trong danh sách thì hiện các món của hóa đơn đó ở bảng bên dưới. */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        int dong = bangHD.getSelectedRow();
        modelCT.setRowCount(0);
        if (dong < 0 || dong >= dsHoaDon.size()) {
            return;
        }
        try {
            List<ChiTietHoaDon> chiTiet = dao.layChiTiet(dsHoaDon.get(dong).getMaHD());
            for (ChiTietHoaDon ct : chiTiet) {
                modelCT.addRow(new Object[]{
                    ct.getMaMon(), ct.getTenMon(), ct.getSoLuong(),
                    TienIch.dinhDangTien(ct.getDonGia()), TienIch.dinhDangTien(ct.getThanhTien())});
            }
        } catch (SQLException ex) {
            TienIch.baoLoi(this, ex);
        }
    }
}
