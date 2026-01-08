package birtek_interfaze_grafikoa;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;

public class MenuAdministrazioa extends JFrame {

    private JPanel contentPane;
    private JTable tableLangileak;
    private JTextField txtBilatu;
    private TableRowSorter<DefaultTableModel> sorter;
    
    // Fitxaketa informazioa
    private JLabel lblFitxaketaInfo;
    
    // Erabiltzailearen datuak
    private int uId;
    private String uIzena;
    private String uAbizena;
    private String uSaila;

    // --- ERAIKITZAILEA EGUNERATUA ---
    public MenuAdministrazioa(int id, String izena, String abizena, String saila) {
        this.uId = id;
        this.uIzena = izena;
        this.uAbizena = abizena;
        this.uSaila = saila;
        
        setTitle("Birtek - ADMINISTRAZIOA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1050, 650); // Pixka bat zabaldu dugu
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        // --- GOIKO PANELA ---
        JPanel panelTop = new JPanel(new BorderLayout());
        contentPane.add(panelTop, BorderLayout.NORTH);

        // EZKERRA: Bilatzailea
        JPanel panelBuscador = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBuscador.add(new JLabel("Bilatu: "));
        txtBilatu = new JTextField(20);
        txtBilatu.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { filtrar(); }
        });
        panelBuscador.add(txtBilatu);
        panelTop.add(panelBuscador, BorderLayout.WEST);

        // ESKUINA: User Info + Fitxaketa + Logout
        JPanel panelRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        // 1. User Info Label
        JLabel lblUser = new JLabel(uSaila + " | " + uIzena + " " + uAbizena + " (ID: " + uId + ")");
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblUser.setForeground(new Color(0, 102, 102));
        
        // 2. Fitxaketa Panela (Logistika bezala)
        JPanel panelFitxaketa = new JPanel();
        panelFitxaketa.setLayout(new BoxLayout(panelFitxaketa, BoxLayout.Y_AXIS));
        
        JPanel panelBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        JButton btnSarrera = new JButton("Sarrera");
        btnSarrera.setBackground(new Color(34, 139, 34)); btnSarrera.setForeground(Color.BLACK);
        btnSarrera.setFont(new Font("SansSerif", Font.BOLD, 10));
        btnSarrera.addActionListener(e -> fitxatu("Sarrera"));

        JButton btnIrteera = new JButton("Irteera");
        btnIrteera.setBackground(new Color(255, 140, 0)); btnIrteera.setForeground(Color.BLACK);
        btnIrteera.setFont(new Font("SansSerif", Font.BOLD, 10));
        btnIrteera.addActionListener(e -> fitxatu("Irteera"));

        JButton btnHistoriala = new JButton("Historiala");
        btnHistoriala.setBackground(new Color(100, 149, 237)); btnHistoriala.setForeground(Color.BLACK);
        btnHistoriala.setFont(new Font("SansSerif", Font.BOLD, 10));
        btnHistoriala.addActionListener(e -> ikusiFitxaketaHistoriala());

        panelBtns.add(btnSarrera);
        panelBtns.add(btnIrteera);
        panelBtns.add(btnHistoriala);

        lblFitxaketaInfo = new JLabel("Kargatzen...");
        lblFitxaketaInfo.setFont(new Font("SansSerif", Font.PLAIN, 9));
        lblFitxaketaInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelFitxaketa.add(panelBtns);
        panelFitxaketa.add(lblFitxaketaInfo);

        // 3. Logout
        JButton btnLogout = new JButton("Saioa Itxi");
        btnLogout.setBackground(new Color(220, 20, 60));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.addActionListener(e -> cerrarSesion());
        
        // Gehitu panelera
        panelRight.add(lblUser);
        panelRight.add(panelFitxaketa);
        panelRight.add(btnLogout);
        
        panelTop.add(panelRight, BorderLayout.EAST);

        // --- ERDIKO PANELA ---
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        JPanel panelLangileak = new JPanel(new BorderLayout());
        tabbedPane.addTab("Langileak", null, panelLangileak, null);

        tableLangileak = new JTable();
        panelLangileak.add(new JScrollPane(tableLangileak), BorderLayout.CENTER);
        
        JButton btnLoad = new JButton("Datuak Kargatu");
        btnLoad.addActionListener(e -> cargarDatos());
        panelLangileak.add(btnLoad, BorderLayout.NORTH);

        if (!java.beans.Beans.isDesignTime()) {
            cargarDatos();
            eguneratuFitxaketaEgoera();
        }
    }
    
    // Default constructor (Testetarako)
    public MenuAdministrazioa() {
        this(1, "Ane", "Zubiaga", "Administrazioa");
    }

    // --- FITXAKETA LOGIKA ---
    private void fitxatu(String mota) {
        String sqlCheck = "SELECT mota FROM fitxaketak WHERE langilea_id = ? ORDER BY id_fitxaketa DESC LIMIT 1";
        try (Connection con = DB_konexioa.konektatu()) {
            try (PreparedStatement pstCheck = con.prepareStatement(sqlCheck)) {
                pstCheck.setInt(1, this.uId);
                try (ResultSet rs = pstCheck.executeQuery()) {
                    String azkenMota = null;
                    if (rs.next()) azkenMota = rs.getString("mota");
                    if ("Sarrera".equals(mota) && "Sarrera".equals(azkenMota)) {
                        JOptionPane.showMessageDialog(this, "Jada barruan zaude.", "Abisua", JOptionPane.WARNING_MESSAGE); return;
                    }
                    if ("Irteera".equals(mota) && "Irteera".equals(azkenMota)) {
                        JOptionPane.showMessageDialog(this, "Jada kanpoan zaude.", "Abisua", JOptionPane.WARNING_MESSAGE); return;
                    }
                    if ("Irteera".equals(mota) && azkenMota == null) {
                        JOptionPane.showMessageDialog(this, "Ezin duzu irten sartu gabe.", "Errorea", JOptionPane.WARNING_MESSAGE); return;
                    }
                }
            }
            String sqlInsert = "INSERT INTO fitxaketak (langilea_id, mota) VALUES (?, ?)";
            try (PreparedStatement pstInsert = con.prepareStatement(sqlInsert)) {
                pstInsert.setInt(1, this.uId);
                pstInsert.setString(2, mota);
                if (pstInsert.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, mota + " erregistratuta.", "Ongi", JOptionPane.INFORMATION_MESSAGE);
                    eguneratuFitxaketaEgoera();
                }
            }
        } catch (SQLException e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Errorea: " + e.getMessage()); }
    }

    private void eguneratuFitxaketaEgoera() {
        String sql = "SELECT mota, data, ordua FROM fitxaketak WHERE langilea_id = ? ORDER BY id_fitxaketa DESC LIMIT 1";
        try (Connection con = DB_konexioa.konektatu(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, this.uId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String mota = rs.getString("mota");
                Date data = rs.getDate("data");
                Time ordua = rs.getTime("ordua");
                if ("Sarrera".equals(mota)) {
                    lblFitxaketaInfo.setText("✅ BARRUAN (" + ordua + ")");
                    lblFitxaketaInfo.setForeground(new Color(0, 100, 0));
                } else {
                    lblFitxaketaInfo.setText("❌ KANPOAN (" + ordua + ")");
                    lblFitxaketaInfo.setForeground(new Color(200, 0, 0));
                }
            } else {
                lblFitxaketaInfo.setText("⚪ Ez dago erregistrorik.");
                lblFitxaketaInfo.setForeground(Color.GRAY);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void ikusiFitxaketaHistoriala() {
        JDialog dialog = new JDialog(this, "Fitxaketa Historiala", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        String[] zutabeak = {"Mota", "Data", "Ordua"};
        DefaultTableModel model = new DefaultTableModel(zutabeak, 0);
        JTable table = new JTable(model);
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        
        String sql = "SELECT mota, data, ordua FROM fitxaketak WHERE langilea_id = ? ORDER BY id_fitxaketa DESC";
        try (Connection con = DB_konexioa.konektatu(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, this.uId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("mota"), rs.getDate("data"), rs.getTime("ordua")});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        dialog.setVisible(true);
    }

    private void cargarDatos() {
        try (Connection con = DB_konexioa.konektatu();
             PreparedStatement pst = con.prepareStatement("SELECT id_langilea, izena, abizena, emaila, saila_id FROM langileak")) {
            DefaultTableModel model = TablaModelador.construirModelo(pst.executeQuery());
            tableLangileak.setModel(model);
            sorter = new TableRowSorter<>(model);
            tableLangileak.setRowSorter(sorter);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void filtrar() {
        String texto = txtBilatu.getText();
        if (sorter != null) {
            if (texto.trim().length() == 0) sorter.setRowFilter(null);
            else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
        }
    }

    private void cerrarSesion() {
        if (JOptionPane.showConfirmDialog(this, "Ziur zaude?", "Logout", JOptionPane.YES_NO_OPTION) == 0) {
            dispose(); new LoginPanela().setVisible(true);
        }
    }
}