package birtek_interfaze_grafikoa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MenuTeknikoa extends JFrame {

    private JTable tableKonponketak, tableProduktuak;
    private JTextField txtBilatu;
    private TableRowSorter<DefaultTableModel> sorterKonponketak, sorterProduktuak, currentSorter;
    
    // Fitxaketa
    private JLabel lblFitxaketaInfo;
    
    // Erabiltzaile datuak
    private int uId;
    private String uIzena;
    private String uAbizena;
    private String uSaila;

    // --- ERAIKITZAILEA EGUNERATUA ---
    public MenuTeknikoa(int id, String izena, String abizena, String saila) {
        this.uId = id;
        this.uIzena = izena;
        this.uAbizena = abizena;
        this.uSaila = saila;
        
        setTitle("Birtek - TEKNIKOA (SAT)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1050, 650);

        JPanel panelTop = new JPanel(new BorderLayout());
        getContentPane().add(panelTop, BorderLayout.NORTH);

        JPanel panelBus = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBus.add(new JLabel("Bilatu: "));
        txtBilatu = new JTextField(20);
        txtBilatu.addKeyListener(new KeyAdapter() { public void keyReleased(KeyEvent e) { filtrar(); } });
        panelBus.add(txtBilatu);
        panelTop.add(panelBus, BorderLayout.WEST);

        // ESKUINA: User + Fitxaketa + Logout
        JPanel panelRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        JLabel lblUser = new JLabel(uSaila + " | " + uIzena + " " + uAbizena + " (ID: " + uId + ")");
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblUser.setForeground(new Color(0, 102, 102));
        
        // Fitxaketa
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

        JButton btnLogout = new JButton("Saioa Itxi");
        btnLogout.setBackground(Color.RED); btnLogout.setForeground(Color.WHITE);
        btnLogout.addActionListener(e -> cerrarSesion());
        
        panelRight.add(lblUser);
        panelRight.add(panelFitxaketa);
        panelRight.add(btnLogout);
        
        panelTop.add(panelRight, BorderLayout.EAST);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        // --- KONPONKETAK TAB ---
        JPanel p1 = new JPanel(new BorderLayout());
        tableKonponketak = new JTable(); 
        
        tableKonponketak.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { 
                    int row = tableKonponketak.getSelectedRow();
                    if (row != -1) {
                        int modelRow = tableKonponketak.convertRowIndexToModel(row);
                        Object idObj = tableKonponketak.getModel().getValueAt(modelRow, 0);
                        if (idObj != null) {
                            int idKonponketa = Integer.parseInt(idObj.toString());
                            irekiKonponketaDetallea(idKonponketa);
                        }
                    }
                }
            }
        });
        
        JLabel lblInfo = new JLabel("💡 Klik bikoitza egin konponketa bat editatzeko / Doble click para editar");
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        lblInfo.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        p1.add(lblInfo, BorderLayout.SOUTH);
        p1.add(new JScrollPane(tableKonponketak), BorderLayout.CENTER);
        tabbedPane.addTab("Konponketak", p1);
        
        // --- PRODUKTUAK TAB ---
        JPanel p2 = new JPanel(new BorderLayout());
        tableProduktuak = new JTable(); p2.add(new JScrollPane(tableProduktuak));
        tabbedPane.addTab("Produktuak", p2);
        
        tabbedPane.addChangeListener(e -> {
            txtBilatu.setText("");
            currentSorter = (tabbedPane.getSelectedIndex() == 0) ? sorterKonponketak : sorterProduktuak;
        });
        
        if (!java.beans.Beans.isDesignTime()) {
            cargarDatos();
            eguneratuFitxaketaEgoera();
        }
    }
    
    public MenuTeknikoa() { this(2, "Mikel", "Otegi", "Teknikoa"); }

    // --- FITXAKETA METODOAK ---
    private void fitxatu(String mota) {
        // ... (Logika berdina: SQL Insert) ...
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

    private void cargarDatos() {
        try(Connection c = DB_konexioa.konektatu()) {
            DefaultTableModel m1 = TablaModelador.construirModelo(c.prepareStatement("SELECT * FROM konponketak").executeQuery());
            tableKonponketak.setModel(m1); sorterKonponketak = new TableRowSorter<>(m1); tableKonponketak.setRowSorter(sorterKonponketak);
            
            DefaultTableModel m2 = TablaModelador.construirModelo(c.prepareStatement("SELECT id_produktua, izena, produktu_egoera FROM produktuak").executeQuery());
            tableProduktuak.setModel(m2); sorterProduktuak = new TableRowSorter<>(m2); tableProduktuak.setRowSorter(sorterProduktuak);
            
            if(currentSorter==null) currentSorter=sorterKonponketak;
        } catch(Exception e) { e.printStackTrace(); }
    }

    private void irekiKonponketaDetallea(int idKonponketa) {
        // Suposatuz DetalleKonponketaDialog existitzen dela
         JOptionPane.showMessageDialog(this, "Ireki konponketa ID: " + idKonponketa);
         // DetalleKonponketaDialog dialog = new DetalleKonponketaDialog(this, idKonponketa);
         // dialog.setVisible(true);
         // cargarDatos();
    }

    private void filtrar() {
        String t = txtBilatu.getText();
        if(currentSorter!=null) {
            if(t.isEmpty()) currentSorter.setRowFilter(null);
            else currentSorter.setRowFilter(RowFilter.regexFilter("(?i)"+t));
        }
    }

    private void cerrarSesion() {
        if(JOptionPane.showConfirmDialog(this,"Irten?","Logout",JOptionPane.YES_NO_OPTION)==0) {
            dispose(); new LoginPanela().setVisible(true);
        }
    }
}