package birtek_interfaze_grafikoa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MenuZuzendaritza extends JFrame {
    
    private int uId;
    private String uIzena;
    private String uAbizena;
    private String uSaila;

    // Fitxaketa
    private JLabel lblFitxaketaInfo;

    // --- ERAIKITZAILEA EGUNERATUA ---
    public MenuZuzendaritza (int id, String izena, String abizena, String saila) {
        this.uId = id;
        this.uIzena = izena;
        this.uAbizena = abizena;
        this.uSaila = saila;
        
        setTitle("Birtek - SISTEMAK (Super Admin)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 500); // Pixka bat handitu dugu
        getContentPane().setLayout(new BorderLayout());

        // HEADER
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        
        JLabel lblUser = new JLabel(uSaila + " | " + uIzena + " " + uAbizena + " (ID: " + uId + ")");
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblUser.setForeground(new Color(0, 102, 102));
        
        // Fitxaketa Panela hemen ere!
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

        panelBtns.add(btnSarrera); panelBtns.add(btnIrteera); panelBtns.add(btnHistoriala);
        
        lblFitxaketaInfo = new JLabel("Kargatzen...");
        lblFitxaketaInfo.setFont(new Font("SansSerif", Font.PLAIN, 9));
        lblFitxaketaInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panelFitxaketa.add(panelBtns);
        panelFitxaketa.add(lblFitxaketaInfo);

        panelTop.add(lblUser);
        panelTop.add(panelFitxaketa);
        getContentPane().add(panelTop, BorderLayout.NORTH);

        // BOTONES CENTRALES
        JPanel panelBotones = new JPanel(new GridLayout(3, 2, 15, 15));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton b1 = new JButton("ADMINISTRAZIOA"); 
        b1.addActionListener(e -> new MenuAdministrazioa(uId, uIzena, uAbizena, "AdminMode").setVisible(true));
        
        JButton b2 = new JButton("TEKNIKOA"); 
        b2.addActionListener(e -> new MenuTeknikoa(uId, uIzena, uAbizena, "SatMode").setVisible(true));
        
        JButton b3 = new JButton("SALMENTAK"); 
        b3.addActionListener(e -> new MenuSalmentak(uId, uIzena, uAbizena, "SalesMode").setVisible(true));
        
        JButton b4 = new JButton("LOGISTIKA"); 
        b4.addActionListener(e -> new MenuLogistika(uId, uIzena, uAbizena, "LogisMode").setVisible(true));
        
        JButton b5 = new JButton("DB CHECK"); 
        b5.addActionListener(e -> JOptionPane.showMessageDialog(this, "Konexioa OK"));
        
        JButton btnExit = new JButton("SAIOA ITXI");
        btnExit.setBackground(Color.RED);
        btnExit.setForeground(Color.WHITE);
        btnExit.addActionListener(e -> cerrarSesion());

        panelBotones.add(b1); panelBotones.add(b2);
        panelBotones.add(b3); panelBotones.add(b4);
        panelBotones.add(b5); panelBotones.add(btnExit);

        getContentPane().add(panelBotones, BorderLayout.CENTER);

        if (!java.beans.Beans.isDesignTime()) {
            eguneratuFitxaketaEgoera();
        }
    }
    
    public MenuZuzendaritza() { this(5, "Aitor", "Mendizabal", "Sistemak"); }

    // --- FITXAKETA LOGIKA (Berdina) ---
    private void fitxatu(String mota) {
        String sqlCheck = "SELECT mota FROM fitxaketak WHERE langilea_id = ? ORDER BY id_fitxaketa DESC LIMIT 1";
        try (Connection con = DB_konexioa.konektatu()) {
            try (PreparedStatement pstCheck = con.prepareStatement(sqlCheck)) {
                pstCheck.setInt(1, this.uId);
                try (ResultSet rs = pstCheck.executeQuery()) {
                    String azkenMota = null;
                    if (rs.next()) azkenMota = rs.getString("mota");
                    if ("Sarrera".equals(mota) && "Sarrera".equals(azkenMota)) {
                        JOptionPane.showMessageDialog(this, "Jada barruan zaude.", "Errorea", JOptionPane.WARNING_MESSAGE); return;
                    }
                    if ("Irteera".equals(mota) && "Irteera".equals(azkenMota)) {
                        JOptionPane.showMessageDialog(this, "Jada kanpoan zaude.", "Errorea", JOptionPane.WARNING_MESSAGE); return;
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

    private void cerrarSesion() {
        if (JOptionPane.showConfirmDialog(this, "Irten?", "Logout", JOptionPane.YES_NO_OPTION) == 0) {
            dispose();
            new LoginPanela().setVisible(true);
        }
    }
}