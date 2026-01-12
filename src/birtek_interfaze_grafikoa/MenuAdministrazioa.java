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

    private static final long serialVersionUID = 1L;
    private JPanel edukiPanela;
    private JTable langileTaula;
    private JTextField bilatuTestua;
    private TableRowSorter<DefaultTableModel> ordenatzailea;
    
    // Fitxaketa informazioa
    private JLabel fitxaketaInfoEtiketa;
    
    // Erabiltzailearen datuak
    private int erabiltzaileId;
    private String erabiltzaileIzena;
    private String erabiltzaileAbizena;
    private String erabiltzaileSaila;

    /**
     * Eraikitzailea eguneratua.
     */
    public MenuAdministrazioa(int id, String izena, String abizena, String saila) {
        this.erabiltzaileId = id;
        this.erabiltzaileIzena = izena;
        this.erabiltzaileAbizena = abizena;
        this.erabiltzaileSaila = saila;
        
        setTitle("Birtek - ADMINISTRAZIOA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1050, 650); 
        edukiPanela = new JPanel();
        edukiPanela.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(edukiPanela);
        edukiPanela.setLayout(new BorderLayout(0, 0));

        // --- GOIKO PANELA ---
        JPanel goikoPanela = new JPanel(new BorderLayout());
        edukiPanela.add(goikoPanela, BorderLayout.NORTH);

        // EZKERRA: Bilatzailea
        JPanel bilatzailePanela = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bilatzailePanela.add(new JLabel("Bilatu: "));
        bilatuTestua = new JTextField(20);
        bilatuTestua.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { filtratu(); }
        });
        bilatzailePanela.add(bilatuTestua);
        goikoPanela.add(bilatzailePanela, BorderLayout.WEST);

        // ESKUINA: Erabiltzaile Info + Fitxaketa + Logout
        JPanel eskuinekoPanela = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        // 1. User Info Label
        JLabel erabiltzaileEtiketa = new JLabel(erabiltzaileSaila + " | " + erabiltzaileIzena + " " + erabiltzaileAbizena + " (ID: " + erabiltzaileId + ")");
        erabiltzaileEtiketa.setFont(new Font("SansSerif", Font.BOLD, 12));
        erabiltzaileEtiketa.setForeground(new Color(0, 102, 102));
        
        // 2. Fitxaketa Panela
        JPanel fitxaketaPanela = new JPanel();
        fitxaketaPanela.setLayout(new BoxLayout(fitxaketaPanela, BoxLayout.Y_AXIS));
        
        JPanel botoiPanela = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        JButton sarreraBotoia = new JButton("Sarrera");
        sarreraBotoia.setBackground(new Color(34, 139, 34)); sarreraBotoia.setForeground(Color.BLACK);
        sarreraBotoia.setFont(new Font("SansSerif", Font.BOLD, 10));
        sarreraBotoia.addActionListener(e -> fitxatu("Sarrera"));

        JButton irteeraBotoia = new JButton("Irteera");
        irteeraBotoia.setBackground(new Color(255, 140, 0)); irteeraBotoia.setForeground(Color.BLACK);
        irteeraBotoia.setFont(new Font("SansSerif", Font.BOLD, 10));
        irteeraBotoia.addActionListener(e -> fitxatu("Irteera"));

        JButton historialBotoia = new JButton("Historiala");
        historialBotoia.setBackground(new Color(100, 149, 237)); historialBotoia.setForeground(Color.BLACK);
        historialBotoia.setFont(new Font("SansSerif", Font.BOLD, 10));
        historialBotoia.addActionListener(e -> ikusiFitxaketaHistoriala());

        botoiPanela.add(sarreraBotoia);
        botoiPanela.add(irteeraBotoia);
        botoiPanela.add(historialBotoia);

        fitxaketaInfoEtiketa = new JLabel("Kargatzen...");
        fitxaketaInfoEtiketa.setFont(new Font("SansSerif", Font.PLAIN, 9));
        fitxaketaInfoEtiketa.setAlignmentX(Component.CENTER_ALIGNMENT);

        fitxaketaPanela.add(botoiPanela);
        fitxaketaPanela.add(fitxaketaInfoEtiketa);

        // 3. Logout
        JButton saioaItxiBotoia = new JButton("Saioa Itxi");
        saioaItxiBotoia.setBackground(new Color(220, 20, 60));
        saioaItxiBotoia.setForeground(Color.WHITE);
        saioaItxiBotoia.addActionListener(e -> saioaItxi());
        
        // Gehitu panelera
        eskuinekoPanela.add(erabiltzaileEtiketa);
        eskuinekoPanela.add(fitxaketaPanela);
        eskuinekoPanela.add(saioaItxiBotoia);
        
        goikoPanela.add(eskuinekoPanela, BorderLayout.EAST);

        // --- ERDIKO PANELA ---
        JTabbedPane pestainaPanela = new JTabbedPane(JTabbedPane.TOP);
        edukiPanela.add(pestainaPanela, BorderLayout.CENTER);

        JPanel langilePanela = new JPanel(new BorderLayout());
        pestainaPanela.addTab("Langileak", null, langilePanela, null);

        langileTaula = new JTable();
        langilePanela.add(new JScrollPane(langileTaula), BorderLayout.CENTER);
        
        JButton kargatuBotoia = new JButton("Datuak Kargatu");
        kargatuBotoia.addActionListener(e -> datuakKargatu());
        langilePanela.add(kargatuBotoia, BorderLayout.NORTH);

        if (!java.beans.Beans.isDesignTime()) {
            datuakKargatu();
            eguneratuFitxaketaEgoera();
        }
    }
    
    // Eraikitzaile lehenetsia (Testetarako)
    public MenuAdministrazioa() {
        this(1, "Ane", "Zubiaga", "Administrazioa");
    }

    // --- FITXAKETA LOGIKA ---
    private void fitxatu(String mota) {
        String egiaztatuGaldera = "SELECT mota FROM fitxaketak WHERE langilea_id = ? ORDER BY id_fitxaketa DESC LIMIT 1";
        try (Connection konexioa = DB_Konexioa.konektatu()) {
            try (PreparedStatement pstEgiaztatu = konexioa.prepareStatement(egiaztatuGaldera)) {
                pstEgiaztatu.setInt(1, this.erabiltzaileId);
                try (ResultSet rs = pstEgiaztatu.executeQuery()) {
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
            String sartuGaldera = "INSERT INTO fitxaketak (langilea_id, mota) VALUES (?, ?)";
            try (PreparedStatement pstSartu = konexioa.prepareStatement(sartuGaldera)) {
                pstSartu.setInt(1, this.erabiltzaileId);
                pstSartu.setString(2, mota);
                if (pstSartu.executeUpdate() > 0) {
                    // JOptionPane.showMessageDialog(this, mota + " erregistratuta.", "Ongi", JOptionPane.INFORMATION_MESSAGE);
                    eguneratuFitxaketaEgoera();
                }
            }
        } catch (SQLException e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Errorea: " + e.getMessage()); }
    }

    private void eguneratuFitxaketaEgoera() {
        String galdera = "SELECT mota, data, ordua FROM fitxaketak WHERE langilea_id = ? ORDER BY id_fitxaketa DESC LIMIT 1";
        try (Connection konexioa = DB_Konexioa.konektatu(); PreparedStatement sententzia = konexioa.prepareStatement(galdera)) {
            sententzia.setInt(1, this.erabiltzaileId);
            ResultSet rs = sententzia.executeQuery();
            if (rs.next()) {
                String mota = rs.getString("mota");
                Time ordua = rs.getTime("ordua");
                if ("Sarrera".equals(mota)) {
                    fitxaketaInfoEtiketa.setText("✅ BARRUAN (" + ordua + ")");
                    fitxaketaInfoEtiketa.setForeground(new Color(0, 100, 0));
                } else {
                    fitxaketaInfoEtiketa.setText("❌ KANPOAN (" + ordua + ")");
                    fitxaketaInfoEtiketa.setForeground(new Color(200, 0, 0));
                }
            } else {
                fitxaketaInfoEtiketa.setText("⚪ Ez dago erregistrorik.");
                fitxaketaInfoEtiketa.setForeground(Color.GRAY);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void ikusiFitxaketaHistoriala() {
        JDialog elkarrizketa = new JDialog(this, "Fitxaketa Historiala", true);
        elkarrizketa.setSize(500, 400);
        elkarrizketa.setLocationRelativeTo(this);
        elkarrizketa.setLayout(new BorderLayout());
        String[] zutabeak = {"Mota", "Data", "Ordua"};
        DefaultTableModel eredua = new DefaultTableModel(zutabeak, 0);
        JTable taula = new JTable(eredua);
        elkarrizketa.add(new JScrollPane(taula), BorderLayout.CENTER);
        
        String galdera = "SELECT mota, data, ordua FROM fitxaketak WHERE langilea_id = ? ORDER BY id_fitxaketa DESC";
        try (Connection konexioa = DB_Konexioa.konektatu(); PreparedStatement sententzia = konexioa.prepareStatement(galdera)) {
            sententzia.setInt(1, this.erabiltzaileId);
            ResultSet rs = sententzia.executeQuery();
            while (rs.next()) {
                eredua.addRow(new Object[]{rs.getString("mota"), rs.getDate("data"), rs.getTime("ordua")});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        elkarrizketa.setVisible(true);
    }

    private void datuakKargatu() {
        try (Connection konexioa = DB_Konexioa.konektatu();
             PreparedStatement sententzia = konexioa.prepareStatement("SELECT id_langilea, izena, abizena, emaila, saila_id FROM langileak")) {
            DefaultTableModel eredua = TaulaModelatzailea.ereduaEraiki(sententzia.executeQuery());
            langileTaula.setModel(eredua);
            ordenatzailea = new TableRowSorter<>(eredua);
            langileTaula.setRowSorter(ordenatzailea);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void filtratu() {
        String testua = bilatuTestua.getText();
        if (ordenatzailea != null) {
            if (testua.trim().length() == 0) ordenatzailea.setRowFilter(null);
            else ordenatzailea.setRowFilter(RowFilter.regexFilter("(?i)" + testua));
        }
    }

    private void saioaItxi() {
        if (JOptionPane.showConfirmDialog(this, "Ziur zaude?", "Saioa Itxi", JOptionPane.YES_NO_OPTION) == 0) {
            dispose(); new SaioaHastekoPanela().setVisible(true);
        }
    }
}