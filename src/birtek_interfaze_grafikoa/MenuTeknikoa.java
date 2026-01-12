package birtek_interfaze_grafikoa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MenuTeknikoa extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTable konponketaTaula, produktuTaula;
    private JTextField bilatuTestua;
    private TableRowSorter<DefaultTableModel> konponketaOrdenatzailea, produktuOrdenatzailea, unekoOrdenatzailea;
    
    // Fitxaketa
    private JLabel fitxaketaInfoEtiketa;
    
    // Erabiltzaile datuak
    private int erabiltzaileId;
    private String erabiltzaileIzena;
    private String erabiltzaileAbizena;
    private String erabiltzaileSaila;

    /**
     * Eraikitzailea eguneratua.
     */
    public MenuTeknikoa(int id, String izena, String abizena, String saila) {
        this.erabiltzaileId = id;
        this.erabiltzaileIzena = izena;
        this.erabiltzaileAbizena = abizena;
        this.erabiltzaileSaila = saila;
        
        setTitle("Birtek - TEKNIKOA (SAT)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1050, 650);

        JPanel goikoPanela = new JPanel(new BorderLayout());
        getContentPane().add(goikoPanela, BorderLayout.NORTH);

        JPanel bilatzailePanela = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bilatzailePanela.add(new JLabel("Bilatu: "));
        bilatuTestua = new JTextField(20);
        bilatuTestua.addKeyListener(new KeyAdapter() { public void keyReleased(KeyEvent e) { filtratu(); } });
        bilatzailePanela.add(bilatuTestua);
        goikoPanela.add(bilatzailePanela, BorderLayout.WEST);

        // ESKUINA: Erabiltzailea + Fitxaketa + Logout
        JPanel eskuinekoPanela = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        JLabel erabiltzaileEtiketa = new JLabel(erabiltzaileSaila + " | " + erabiltzaileIzena + " " + erabiltzaileAbizena + " (ID: " + erabiltzaileId + ")");
        erabiltzaileEtiketa.setFont(new Font("SansSerif", Font.BOLD, 12));
        erabiltzaileEtiketa.setForeground(new Color(0, 102, 102));
        
        // Fitxaketa
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

        botoiPanela.add(sarreraBotoia); botoiPanela.add(irteeraBotoia); botoiPanela.add(historialBotoia);
        
        fitxaketaInfoEtiketa = new JLabel("Kargatzen...");
        fitxaketaInfoEtiketa.setFont(new Font("SansSerif", Font.PLAIN, 9));
        fitxaketaInfoEtiketa.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        fitxaketaPanela.add(botoiPanela);
        fitxaketaPanela.add(fitxaketaInfoEtiketa);

        JButton saioaItxiBotoia = new JButton("Saioa Itxi");
        saioaItxiBotoia.setBackground(Color.RED); saioaItxiBotoia.setForeground(Color.WHITE);
        saioaItxiBotoia.addActionListener(e -> saioaItxi());
        
        eskuinekoPanela.add(erabiltzaileEtiketa);
        eskuinekoPanela.add(fitxaketaPanela);
        eskuinekoPanela.add(saioaItxiBotoia);
        
        goikoPanela.add(eskuinekoPanela, BorderLayout.EAST);

        JTabbedPane pestainaPanela = new JTabbedPane(JTabbedPane.TOP);
        getContentPane().add(pestainaPanela, BorderLayout.CENTER);

        // --- KONPONKETAK TAB ---
        JPanel konponketaPanela = new JPanel(new BorderLayout());
        konponketaTaula = new JTable(); 
        
        konponketaTaula.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { 
                    int errenkada = konponketaTaula.getSelectedRow();
                    if (errenkada != -1) {
                        int errenkadaModeloa = konponketaTaula.convertRowIndexToModel(errenkada);
                        Object idObj = konponketaTaula.getModel().getValueAt(errenkadaModeloa, 0);
                        if (idObj != null) {
                            int idKonponketa = Integer.parseInt(idObj.toString());
                            irekiKonponketaXehetasuna(idKonponketa);
                        }
                    }
                }
            }
        });
        
        JLabel informazioEtiketa = new JLabel("💡 Klik bikoitza egin konponketa bat editatzeko / Doble click para editar");
        informazioEtiketa.setHorizontalAlignment(SwingConstants.CENTER);
        informazioEtiketa.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        konponketaPanela.add(informazioEtiketa, BorderLayout.SOUTH);
        konponketaPanela.add(new JScrollPane(konponketaTaula), BorderLayout.CENTER);
        pestainaPanela.addTab("Konponketak", konponketaPanela);
        
        // --- PRODUKTUAK TAB ---
        JPanel produktuPanela = new JPanel(new BorderLayout());
        produktuTaula = new JTable(); produktuPanela.add(new JScrollPane(produktuTaula));
        pestainaPanela.addTab("Produktuak", produktuPanela);
        
        pestainaPanela.addChangeListener(e -> {
            bilatuTestua.setText("");
            unekoOrdenatzailea = (pestainaPanela.getSelectedIndex() == 0) ? konponketaOrdenatzailea : produktuOrdenatzailea;
        });
        
        if (!java.beans.Beans.isDesignTime()) {
            datuakKargatu();
            eguneratuFitxaketaEgoera();
        }
    }
    
    public MenuTeknikoa() { this(2, "Mikel", "Otegi", "Teknikoa"); }

    // --- FITXAKETA METODOAK ---
    private void fitxatu(String mota) {
        String egiaztatuGaldera = "SELECT mota FROM fitxaketak WHERE langilea_id = ? ORDER BY id_fitxaketa DESC LIMIT 1";
        try (Connection konexioa = DB_Konexioa.konektatu()) {
            try (PreparedStatement pstEgiaztatu = konexioa.prepareStatement(egiaztatuGaldera)) {
                pstEgiaztatu.setInt(1, this.erabiltzaileId);
                try (ResultSet rs = pstEgiaztatu.executeQuery()) {
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
            String sartuGaldera = "INSERT INTO fitxaketak (langilea_id, mota) VALUES (?, ?)";
            try (PreparedStatement pstSartu = konexioa.prepareStatement(sartuGaldera)) {
                pstSartu.setInt(1, this.erabiltzaileId);
                pstSartu.setString(2, mota);
                if (pstSartu.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, mota + " erregistratuta.", "Ongi", JOptionPane.INFORMATION_MESSAGE);
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
        try(Connection konexioa = DB_Konexioa.konektatu()) {
            DefaultTableModel m1 = TaulaModelatzailea.ereduaEraiki(konexioa.prepareStatement("SELECT * FROM konponketak").executeQuery());
            konponketaTaula.setModel(m1); konponketaOrdenatzailea = new TableRowSorter<>(m1); konponketaTaula.setRowSorter(konponketaOrdenatzailea);
            
            DefaultTableModel m2 = TaulaModelatzailea.ereduaEraiki(konexioa.prepareStatement("SELECT id_produktua, izena, produktu_egoera FROM produktuak").executeQuery());
            produktuTaula.setModel(m2); produktuOrdenatzailea = new TableRowSorter<>(m2); produktuTaula.setRowSorter(produktuOrdenatzailea);
            
            if(unekoOrdenatzailea==null) unekoOrdenatzailea=konponketaOrdenatzailea;
        } catch(Exception e) { e.printStackTrace(); }
    }

    private void irekiKonponketaXehetasuna(int idKonponketa) {
        // Balioak lortu taulatik (suposatuz ordena: id, egoera, oharrak...)
        int errenkada = konponketaTaula.getSelectedRow();
        String egoera = konponketaTaula.getValueAt(errenkada, 5).toString(); // suposatuz egoera 5. zutabea dela
        String oharrak = konponketaTaula.getValueAt(errenkada, 6).toString(); // suposatuz oharrak 6. zutabea dela
        
        KonponketaXehetasunaElkarrizketa elkarrizketa = new KonponketaXehetasunaElkarrizketa(idKonponketa, egoera, oharrak);
        elkarrizketa.setModal(true);
        elkarrizketa.setVisible(true);
        datuakKargatu();
    }

    private void filtratu() {
        String t = bilatuTestua.getText();
        if(unekoOrdenatzailea!=null) {
            if(t.isEmpty()) unekoOrdenatzailea.setRowFilter(null);
            else unekoOrdenatzailea.setRowFilter(RowFilter.regexFilter("(?i)"+t));
        }
    }

    private void saioaItxi() {
        if(JOptionPane.showConfirmDialog(this,"Irten?","Saioa Itxi",JOptionPane.YES_NO_OPTION)==0) {
            dispose(); new SaioaHastekoPanela().setVisible(true);
        }
    }
}