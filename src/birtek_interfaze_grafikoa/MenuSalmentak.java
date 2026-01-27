package birtek_interfaze_grafikoa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.File;

public class MenuSalmentak extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTable bezeroTaula, eskaeraTaula, eskaeraLerroTaula;
    private JTextField bilatuTestua;
    private TableRowSorter<DefaultTableModel> bezeroOrdenatzailea, eskaeraOrdenatzailea, unekoOrdenatzailea;

    // Fitxaketa
    private JLabel fitxaketaInfoEtiketa;

    // Erabiltzailearen datuak
    private int erabiltzaileId;
    private String erabiltzaileIzena;
    private String erabiltzaileAbizena;
    private String erabiltzaileSaila;

    /**
     * Eraikitzailea eguneratua.
     */
    public MenuSalmentak(int id, String izena, String abizena, String saila) {
        this.erabiltzaileId = id;
        this.erabiltzaileIzena = izena;
        this.erabiltzaileAbizena = abizena;
        this.erabiltzaileSaila = saila;

        setTitle("Birtek - SALMENTAK");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1050, 650);

        JPanel goikoPanela = new JPanel(new BorderLayout());
        getContentPane().add(goikoPanela, BorderLayout.NORTH);

        JPanel bilatzailePanela = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bilatzailePanela.add(new JLabel("Bilatu: "));
        bilatuTestua = new JTextField(20);
        bilatuTestua.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filtratu();
            }
        });
        bilatzailePanela.add(bilatuTestua);
        goikoPanela.add(bilatzailePanela, BorderLayout.WEST);

        // ESKUINA: Erabiltzailea + Fitxaketa + Logout
        JPanel eskuinekoPanela = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        JLabel erabiltzaileEtiketa = new JLabel(erabiltzaileSaila + " | " + erabiltzaileIzena + " "
                + erabiltzaileAbizena + " (ID: " + erabiltzaileId + ")");
        erabiltzaileEtiketa.setFont(new Font("SansSerif", Font.BOLD, 12));
        erabiltzaileEtiketa.setForeground(new Color(0, 102, 102));

        // Fitxaketa Panela
        JPanel fitxaketaPanela = new JPanel();
        fitxaketaPanela.setLayout(new BoxLayout(fitxaketaPanela, BoxLayout.Y_AXIS));
        JPanel botoiPanela = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));

        JButton sarreraBotoia = new JButton("Sarrera");
        sarreraBotoia.setBackground(new Color(34, 139, 34));
        sarreraBotoia.setForeground(Color.BLACK);
        sarreraBotoia.setFont(new Font("SansSerif", Font.BOLD, 10));
        sarreraBotoia.addActionListener(e -> fitxatu("Sarrera"));

        JButton irteeraBotoia = new JButton("Irteera");
        irteeraBotoia.setBackground(new Color(255, 140, 0));
        irteeraBotoia.setForeground(Color.BLACK);
        irteeraBotoia.setFont(new Font("SansSerif", Font.BOLD, 10));
        irteeraBotoia.addActionListener(e -> fitxatu("Irteera"));

        JButton historialBotoia = new JButton("Historiala");
        historialBotoia.setBackground(new Color(100, 149, 237));
        historialBotoia.setForeground(Color.BLACK);
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

        JButton saioaItxiBotoia = new JButton("Saioa Itxi");
        saioaItxiBotoia.setBackground(Color.RED);
        saioaItxiBotoia.setForeground(Color.WHITE);
        saioaItxiBotoia.addActionListener(e -> saioaItxi());

        if ("Zuzendaritza".equalsIgnoreCase(Sesioa.sailaIzena)) {
            JButton atzeraBotoia = new JButton("ATZERA");
            atzeraBotoia.setBackground(new Color(100, 100, 100));
            atzeraBotoia.setForeground(Color.WHITE);
            atzeraBotoia.addActionListener(e -> {
                dispose();
                new MenuZuzendaritza().setVisible(true);
            });
            eskuinekoPanela.add(atzeraBotoia);
        }

        eskuinekoPanela.add(erabiltzaileEtiketa);
        eskuinekoPanela.add(fitxaketaPanela);
        eskuinekoPanela.add(saioaItxiBotoia);

        goikoPanela.add(eskuinekoPanela, BorderLayout.EAST);

        JTabbedPane pestainaPanela = new JTabbedPane(JTabbedPane.TOP);
        getContentPane().add(pestainaPanela, BorderLayout.CENTER);

        JPanel bezeroPanela = new JPanel(new BorderLayout());
        bezeroTaula = new JTable();
        bezeroPanela.add(new JScrollPane(bezeroTaula));
        pestainaPanela.addTab("Bezeroak", bezeroPanela);

        // ESKAERAK PANELA (SplitPane)
        eskaeraTaula = new JTable();
        eskaeraTaula.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fokatutakoEskaeraKargatu();
            }
        });

        eskaeraLerroTaula = new JTable();

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(eskaeraTaula),
                new JScrollPane(eskaeraLerroTaula));
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);

        JPanel eskaeraPanela = new JPanel(new BorderLayout());
        eskaeraPanela.add(splitPane, BorderLayout.CENTER);
        pestainaPanela.addTab("Eskaerak", eskaeraPanela);

        // --- ESKAERA BOTOIAK ---
        JPanel eskaeraBotoiPanela = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton eskaeraGehituBotoia = new JButton("Gehitu");
        JButton eskaeraEditatuBotoia = new JButton("Editatu");
        JButton eskaeraEzabatuBotoia = new JButton("Ezabatu");
        JButton eskaeraFakturaBotoia = new JButton("Faktura");

        eskaeraGehituBotoia.addActionListener(e -> eskaeraGehitu());
        eskaeraEditatuBotoia.addActionListener(e -> eskaeraEditatu());
        eskaeraEzabatuBotoia.addActionListener(e -> eskaeraEzabatu());
        eskaeraFakturaBotoia.addActionListener(e -> fakturaSortu());

        eskaeraBotoiPanela.add(eskaeraGehituBotoia);
        eskaeraBotoiPanela.add(eskaeraEditatuBotoia);
        eskaeraBotoiPanela.add(eskaeraEzabatuBotoia);
        eskaeraBotoiPanela.add(eskaeraFakturaBotoia);

        eskaeraPanela.add(eskaeraBotoiPanela, BorderLayout.SOUTH);

        pestainaPanela.addChangeListener(e -> {
            bilatuTestua.setText("");
            unekoOrdenatzailea = (pestainaPanela.getSelectedIndex() == 0) ? bezeroOrdenatzailea : eskaeraOrdenatzailea;
        });

        if (!java.beans.Beans.isDesignTime()) {
            datuakKargatu();
            eguneratuFitxaketaEgoera();
        }
    }

    public MenuSalmentak() {
        this(Sesioa.idLangilea, Sesioa.izena, Sesioa.abizena, Sesioa.sailaIzena);
    }

    // --- FITXAKETA LOGIKA ---
    private void fitxatu(String mota) {
        String egiaztatuGaldera = "SELECT mota FROM fitxaketak WHERE langilea_id = ? ORDER BY id_fitxaketa DESC LIMIT 1";
        try (Connection konexioa = DB_Konexioa.konektatu()) {
            try (PreparedStatement pstEgiaztatu = konexioa.prepareStatement(egiaztatuGaldera)) {
                pstEgiaztatu.setInt(1, this.erabiltzaileId);
                try (ResultSet rs = pstEgiaztatu.executeQuery()) {
                    String azkenMota = null;
                    if (rs.next())
                        azkenMota = rs.getString("mota");
                    if ("Sarrera".equals(mota) && "Sarrera".equals(azkenMota)) {
                        JOptionPane.showMessageDialog(this, "Jada barruan zaude.", "Errorea",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if ("Irteera".equals(mota) && "Irteera".equals(azkenMota)) {
                        JOptionPane.showMessageDialog(this, "Jada kanpoan zaude.", "Errorea",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if ("Irteera".equals(mota) && azkenMota == null) {
                        JOptionPane.showMessageDialog(this, "Ezin duzu irten sartu gabe.", "Errorea",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }
            String sartuGaldera = "INSERT INTO fitxaketak (langilea_id, mota) VALUES (?, ?)";
            try (PreparedStatement pstSartu = konexioa.prepareStatement(sartuGaldera)) {
                pstSartu.setInt(1, this.erabiltzaileId);
                pstSartu.setString(2, mota);
                if (pstSartu.executeUpdate() > 0) {
                    // JOptionPane.showMessageDialog(this, mota + " erregistratuta.", "Ongi",
                    // JOptionPane.INFORMATION_MESSAGE);
                    eguneratuFitxaketaEgoera();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errorea: " + e.getMessage());
        }
    }

    private void eguneratuFitxaketaEgoera() {
        String galdera = "SELECT mota, data, ordua FROM fitxaketak WHERE langilea_id = ? ORDER BY id_fitxaketa DESC LIMIT 1";
        try (Connection konexioa = DB_Konexioa.konektatu();
                PreparedStatement sententzia = konexioa.prepareStatement(galdera)) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ikusiFitxaketaHistoriala() {
        JDialog elkarrizketa = new JDialog(this, "Fitxaketa Historiala", true);
        elkarrizketa.setSize(500, 400);
        elkarrizketa.setLocationRelativeTo(this);
        elkarrizketa.setLayout(new BorderLayout());
        String[] zutabeak = { "Mota", "Data", "Ordua" };
        DefaultTableModel eredua = new DefaultTableModel(zutabeak, 0);
        JTable taula = new JTable(eredua);
        elkarrizketa.add(new JScrollPane(taula), BorderLayout.CENTER);

        String galdera = "SELECT mota, data, ordua FROM fitxaketak WHERE langilea_id = ? ORDER BY id_fitxaketa DESC";
        try (Connection konexioa = DB_Konexioa.konektatu();
                PreparedStatement sententzia = konexioa.prepareStatement(galdera)) {
            sententzia.setInt(1, this.erabiltzaileId);
            ResultSet rs = sententzia.executeQuery();
            while (rs.next()) {
                eredua.addRow(new Object[] { rs.getString("mota"), rs.getDate("data"), rs.getTime("ordua") });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        elkarrizketa.setVisible(true);
    }

    private void datuakKargatu() {
        try (Connection konexioa = DB_Konexioa.konektatu()) {
            DefaultTableModel m1 = TaulaModelatzailea.ereduaEraiki(konexioa
                    .prepareStatement("SELECT id_bezeroa, izena_edo_soziala, emaila FROM bezeroak").executeQuery());
            bezeroTaula.setModel(m1);
            bezeroOrdenatzailea = new TableRowSorter<>(m1);
            bezeroTaula.setRowSorter(bezeroOrdenatzailea);

            DefaultTableModel m2 = TaulaModelatzailea
                    .ereduaEraiki(konexioa.prepareStatement("SELECT * FROM eskaerak").executeQuery());
            eskaeraTaula.setModel(m2);
            eskaeraOrdenatzailea = new TableRowSorter<>(m2);
            eskaeraTaula.setRowSorter(eskaeraOrdenatzailea);

            if (unekoOrdenatzailea == null)
                unekoOrdenatzailea = bezeroOrdenatzailea;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fokatutakoEskaeraKargatu() {
        int aukeratutakoLerroa = eskaeraTaula.getSelectedRow();
        if (aukeratutakoLerroa != -1) {
            aukeratutakoLerroa = eskaeraTaula.convertRowIndexToModel(aukeratutakoLerroa);
            Object idObj = eskaeraTaula.getModel().getValueAt(aukeratutakoLerroa, 0);
            int idEskaera = (idObj instanceof Number) ? ((Number) idObj).intValue()
                    : Integer.parseInt(idObj.toString());

            try (Connection konexioa = DB_Konexioa.konektatu()) {
                String sql = "SELECT p.izena, el.kantitatea, el.unitate_prezioa, (el.kantitatea * el.unitate_prezioa) as guztira "
                        +
                        "FROM eskaera_lerroak el JOIN produktuak p ON el.produktua_id = p.id_produktua " +
                        "WHERE el.eskaera_id = ?";
                PreparedStatement pst = konexioa.prepareStatement(sql);
                pst.setInt(1, idEskaera);
                eskaeraLerroTaula.setModel(TaulaModelatzailea.ereduaEraiki(pst.executeQuery()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            eskaeraLerroTaula.setModel(new DefaultTableModel());
        }
    }

    private void filtratu() {
        String t = bilatuTestua.getText();
        if (unekoOrdenatzailea != null) {
            if (t.isEmpty())
                unekoOrdenatzailea.setRowFilter(null);
            else
                unekoOrdenatzailea.setRowFilter(RowFilter.regexFilter("(?i)" + t));
        }
    }

    private void saioaItxi() {
        if (JOptionPane.showConfirmDialog(this, "Irten?", "Saioa Itxi", JOptionPane.YES_NO_OPTION) == 0) {
            dispose();
            new SaioaHastekoPanela().setVisible(true);
        }
    }

    // --- ESKAERA METODOAK ---
    private void eskaeraGehitu() {
        EskaeraDialog dialog = new EskaeraDialog(this, "Gehitu Eskaera", null, "Prestatzen");
        dialog.setVisible(true);
        if (dialog.isOnartua()) {
            String sqlEskaera = "INSERT INTO eskaerak (bezeroa_id, langilea_id, data, eguneratze_data, guztira_prezioa, eskaera_egoera) VALUES (?, ?, NOW(), NOW(), ?, ?)";
            String sqlLerroa = "INSERT INTO eskaera_lerroak (eskaera_id, produktua_id, kantitatea, unitate_prezioa) VALUES (?, ?, ?, ?)";

            try (Connection konexioa = DB_Konexioa.konektatu()) {
                konexioa.setAutoCommit(false); // Transakzioa hasi

                try (PreparedStatement pst = konexioa.prepareStatement(sqlEskaera, Statement.RETURN_GENERATED_KEYS)) {
                    pst.setInt(1, dialog.getBezeroaId());
                    pst.setInt(2, this.erabiltzaileId);
                    pst.setBigDecimal(3, dialog.getPrezioTotala());
                    pst.setString(4, dialog.getEgoera());
                    pst.executeUpdate();

                    ResultSet rs = pst.getGeneratedKeys();
                    if (rs.next()) {
                        int idEskaera = rs.getInt(1);

                        try (PreparedStatement pstLerroa = konexioa.prepareStatement(sqlLerroa)) {
                            for (Object[] lerroa : dialog.getLerroak()) {
                                pstLerroa.setInt(1, idEskaera);
                                pstLerroa.setInt(2, (int) lerroa[0]); // Produktua ID
                                pstLerroa.setInt(3, (int) lerroa[1]); // Kantitatea
                                pstLerroa.setBigDecimal(4, (java.math.BigDecimal) lerroa[2]); // Unitate Prezioa
                                pstLerroa.addBatch();
                            }
                            pstLerroa.executeBatch();
                        }
                    }
                }

                konexioa.commit(); // Transakzioa baieztatu
                datuakKargatu();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Errorea eskaera sortzean: " + e.getMessage());
            }
        }
    }

    private void eskaeraEditatu() {
        int aukeratutakoLerroa = eskaeraTaula.getSelectedRow();
        if (aukeratutakoLerroa == -1) {
            JOptionPane.showMessageDialog(this, "Aukeratu eskaera bat editatzeko.");
            return;
        }

        // Datuak lortu taulatik (Modeloaren indizeak erabili behar dira ordenatzailea
        // badago)
        aukeratutakoLerroa = eskaeraTaula.convertRowIndexToModel(aukeratutakoLerroa);
        DefaultTableModel model = (DefaultTableModel) eskaeraTaula.getModel();

        Object idObj = model.getValueAt(aukeratutakoLerroa, 0);
        int idEskaera = (idObj instanceof Number) ? ((Number) idObj).intValue()
                : Integer.parseInt(idObj.toString());

        Object bezeroObj = model.getValueAt(aukeratutakoLerroa, 1);
        int bezeroaId = (bezeroObj instanceof Number) ? ((Number) bezeroObj).intValue()
                : Integer.parseInt(bezeroObj.toString());

        String egoera = (String) model.getValueAt(aukeratutakoLerroa, 6);

        EskaeraDialog dialog = new EskaeraDialog(this, "Editatu Eskaera", bezeroaId, egoera);

        // Lerro zaharrak kargatu
        try (Connection konexioa = DB_Konexioa.konektatu();
                PreparedStatement pst = konexioa.prepareStatement(
                        "SELECT el.produktua_id, p.izena, el.unitate_prezioa, el.kantitatea " +
                                "FROM eskaera_lerroak el JOIN produktuak p ON el.produktua_id = p.id_produktua " +
                                "WHERE el.eskaera_id = ?")) {
            pst.setInt(1, idEskaera);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                // OHARRA: Deskontua ez dugu gordetzen DBn, beraz 0 jartzen dugu edo kalkulatzen
                // saiatu gaitezke.
                // Sinplifikatzeko, 0 deskontua jartzen dugu eta prezioa mantentzen dugu.
                // Prezio originala ez dakigunez (zuk unitate prezioa gordetzen duzu
                // deskontuarekin),
                // deskontua 0 dela onartuko dugu hemen.
                dialog.addZuzeneanLerroa(
                        rs.getInt("produktua_id"),
                        rs.getString("izena"),
                        rs.getBigDecimal("unitate_prezioa"),
                        rs.getInt("kantitatea"),
                        0.0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dialog.setVisible(true);

        if (dialog.isOnartua()) {
            String sqlUpdate = "UPDATE eskaerak SET bezeroa_id = ?, guztira_prezioa = ?, eskaera_egoera = ?, eguneratze_data = NOW() WHERE id_eskaera = ?";
            String sqlDeleteLerroak = "DELETE FROM eskaera_lerroak WHERE eskaera_id = ?";
            String sqlInsertLerroa = "INSERT INTO eskaera_lerroak (eskaera_id, produktua_id, kantitatea, unitate_prezioa) VALUES (?, ?, ?, ?)";

            try (Connection konexioa = DB_Konexioa.konektatu()) {
                konexioa.setAutoCommit(false);

                // Eskaera eguneratu
                try (PreparedStatement pst = konexioa.prepareStatement(sqlUpdate)) {
                    pst.setInt(1, dialog.getBezeroaId());
                    pst.setBigDecimal(2, dialog.getPrezioTotala());
                    pst.setString(3, dialog.getEgoera());
                    pst.setInt(4, idEskaera);
                    pst.executeUpdate();
                }

                // Lerro zaharrak ezabatu
                try (PreparedStatement pstDalete = konexioa.prepareStatement(sqlDeleteLerroak)) {
                    pstDalete.setInt(1, idEskaera);
                    pstDalete.executeUpdate();
                }

                // Lerro berriak sartu
                try (PreparedStatement pstLerroa = konexioa.prepareStatement(sqlInsertLerroa)) {
                    for (Object[] lerroa : dialog.getLerroak()) {
                        pstLerroa.setInt(1, idEskaera);
                        pstLerroa.setInt(2, (int) lerroa[0]);
                        pstLerroa.setInt(3, (int) lerroa[1]);
                        pstLerroa.setBigDecimal(4, (java.math.BigDecimal) lerroa[2]);
                        pstLerroa.addBatch();
                    }
                    pstLerroa.executeBatch();
                }

                konexioa.commit();
                datuakKargatu();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Errorea eskaera editatzean: " + e.getMessage());
            }
        }
    }

    private void eskaeraEzabatu() {
        int aukeratutakoLerroa = eskaeraTaula.getSelectedRow();
        if (aukeratutakoLerroa == -1) {
            JOptionPane.showMessageDialog(this, "Aukeratu eskaera bat ezabatzeko.");
            return;
        }

        aukeratutakoLerroa = eskaeraTaula.convertRowIndexToModel(aukeratutakoLerroa);
        Object idObj = eskaeraTaula.getModel().getValueAt(aukeratutakoLerroa, 0);
        int idEskaera = (idObj instanceof Number) ? ((Number) idObj).intValue() : Integer.parseInt(idObj.toString());

        if (JOptionPane.showConfirmDialog(this, "Ziur zaude eskaera hau ezabatu nahi duzula?", "Ezabatu",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM eskaerak WHERE id_eskaera = ?";
            try (Connection konexioa = DB_Konexioa.konektatu();
                    PreparedStatement pst = konexioa.prepareStatement(sql)) {
                pst.setInt(1, idEskaera);
                pst.executeUpdate();
                datuakKargatu();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Errorea eskaera ezabatzean: " + e.getMessage());
            }
        }
    }

    private void fakturaSortu() {
        int aukeratutakoLerroa = eskaeraTaula.getSelectedRow();
        if (aukeratutakoLerroa == -1) {
            JOptionPane.showMessageDialog(this, "Aukeratu eskaera bat faktura sortzeko.");
            return;
        }

        aukeratutakoLerroa = eskaeraTaula.convertRowIndexToModel(aukeratutakoLerroa);
        DefaultTableModel model = (DefaultTableModel) eskaeraTaula.getModel();

        Object idObj = model.getValueAt(aukeratutakoLerroa, 0);
        int idEskaera = (idObj instanceof Number) ? ((Number) idObj).intValue() : Integer.parseInt(idObj.toString());
        String egoera = (String) model.getValueAt(aukeratutakoLerroa, 6);

        if (!"Osatua/Bidalita".equalsIgnoreCase(egoera)) {
            JOptionPane.showMessageDialog(this,
                    "Faktura bakarrik 'Osatua/Bidalita' egoeran dauden eskaeretarako sor daiteke.");
            return;
        }

        // Faktura sortu
        File karpeta = new File("C:\\xampp\\htdocs\\fakturak");
        if (!karpeta.exists()) {
            karpeta.mkdirs();
        }

        File fakturaFitxategia = new File(karpeta, "faktura_" + idEskaera + ".html");

        try (Connection konexioa = DB_Konexioa.konektatu()) {
            // Eskaera burua lortu
            PreparedStatement pst = konexioa.prepareStatement(
                    "SELECT e.id_eskaera, e.data, b.izena_edo_soziala, b.ifz_nan, b.helbidea, b.emaila " +
                            "FROM eskaerak e JOIN bezeroak b ON e.bezeroa_id = b.id_bezeroa WHERE e.id_eskaera = ?");
            pst.setInt(1, idEskaera);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // Lerroak lortu eta kalkuluak egin
                PreparedStatement pstLerroak = konexioa.prepareStatement(
                        "SELECT p.izena, el.kantitatea, el.unitate_prezioa " +
                                "FROM eskaera_lerroak el JOIN produktuak p ON el.produktua_id = p.id_produktua " +
                                "WHERE el.eskaera_id = ?");
                pstLerroak.setInt(1, idEskaera);
                ResultSet rsLerroak = pstLerroak.executeQuery();

                StringBuilder lerroakHtml = new StringBuilder();
                java.math.BigDecimal oinarria = java.math.BigDecimal.ZERO;

                lerroakHtml.append(
                        "<table><tr><th>Produktua</th><th>Kantitatea</th><th>Prezioa</th><th>Guztira</th></tr>");
                while (rsLerroak.next()) {
                    String pIzena = rsLerroak.getString("izena");
                    int kantitatea = rsLerroak.getInt("kantitatea");
                    java.math.BigDecimal prezioa = rsLerroak.getBigDecimal("unitate_prezioa");
                    java.math.BigDecimal totala = prezioa.multiply(new java.math.BigDecimal(kantitatea));
                    oinarria = oinarria.add(totala);

                    lerroakHtml.append("<tr>");
                    lerroakHtml.append("<td>").append(pIzena).append("</td>");
                    lerroakHtml.append("<td>").append(kantitatea).append("</td>");
                    lerroakHtml.append("<td>").append(prezioa).append(" €</td>");
                    lerroakHtml.append("<td>").append(totala).append(" €</td>");
                    lerroakHtml.append("</tr>");
                }
                lerroakHtml.append("</table>");

                java.math.BigDecimal bez = oinarria.multiply(new java.math.BigDecimal("0.21"));
                java.math.BigDecimal guztira = oinarria.add(bez);

                // HTML sortu
                StringBuilder htmlEdukia = new StringBuilder();
                htmlEdukia.append("<html><head><title>Faktura " + idEskaera + "</title>");
                htmlEdukia.append("<style>");
                htmlEdukia.append(
                        "body{font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: 40px; color: #333;}");
                htmlEdukia.append(
                        ".header{display: flex; justify-content: space-between; align-items: center; margin-bottom: 40px; border-bottom: 2px solid #ddd; padding-bottom: 20px;}");
                htmlEdukia.append(".logo-img { max-height: 80px; }");
                htmlEdukia.append(".company-info { text-align: right; font-size: 0.9em; }");
                htmlEdukia.append("table{width:100%; border-collapse: collapse; margin-top: 20px;}");
                htmlEdukia.append("th, td{border-bottom: 1px solid #ddd; padding: 12px; text-align: left;}");
                htmlEdukia.append("th{background-color: #f8f9fa; color: #555;}");
                htmlEdukia.append(".totals { margin-top: 30px; text-align: right; }");
                htmlEdukia.append(".totals p { margin: 5px 0; font-size: 1.1em; }");
                htmlEdukia.append(".prop { font-weight: bold; }");
                htmlEdukia.append("</style>");
                htmlEdukia.append("</head><body>");

                // HEADER (Logo + Company Info)
                htmlEdukia.append("<div class='header'>");
                htmlEdukia.append(
                        "<div><img src='img/birtek_logo_zuri_borobila.png' class='logo-img' alt='Birtek Logo'></div>");
                htmlEdukia.append(
                        "<div class='company-info'><h3>Birtek S.L.</h3><p>Kale Nagusia 123, Ordizia</p><p>Campus Goierri Campusa</p><p>IFZ: B12345678</p><p>Tel: 944 123 456</p></div>");
                htmlEdukia.append("</div>");

                htmlEdukia.append("<h2>FAKTURA: " + rs.getInt("id_eskaera") + "</h2>");
                htmlEdukia.append("<p><strong>Data:</strong> " + rs.getTimestamp("data") + "</p>");

                htmlEdukia.append("<h4>Bezeroa:</h4>");
                htmlEdukia.append("<p>" + rs.getString("izena_edo_soziala") + "<br>");
                htmlEdukia.append(rs.getString("ifz_nan") + "<br>");
                htmlEdukia.append(rs.getString("helbidea") + "<br>");
                htmlEdukia.append(rs.getString("emaila") + "</p>");

                htmlEdukia.append(lerroakHtml.toString());

                htmlEdukia.append("<div class='totals'>");
                htmlEdukia.append(
                        "<p><span class='prop'>Oinarria:</span> " + String.format("%.2f", oinarria) + " €</p>");
                htmlEdukia.append("<p><span class='prop'>BEZ (%21):</span> " + String.format("%.2f", bez) + " €</p>");
                htmlEdukia.append(
                        "<h3><span class='prop'>GUZTIRA:</span> " + String.format("%.2f", guztira) + " €</h3>");
                htmlEdukia.append("</div>");
                htmlEdukia.append("</body></html>");

                try (java.io.BufferedWriter writer = new java.io.BufferedWriter(
                        new java.io.FileWriter(fakturaFitxategia))) {
                    writer.write(htmlEdukia.toString());

                    // DBan gorde
                    String fakturaZenbakia = "FAK-" + idEskaera + "-" + System.currentTimeMillis();
                    // ON DUPLICATE KEY UPDATE logic: eskaera_id existitzen bada, eguneratu
                    String sqlInsert = "INSERT INTO bezero_fakturak (faktura_zenbakia, eskaera_id, fitxategia_url, data) VALUES (?, ?, ?, NOW()) "
                            +
                            "ON DUPLICATE KEY UPDATE faktura_zenbakia = VALUES(faktura_zenbakia), fitxategia_url = VALUES(fitxategia_url), data = NOW()";

                    try (PreparedStatement pstInsert = konexioa.prepareStatement(sqlInsert)) {
                        pstInsert.setString(1, fakturaZenbakia);
                        pstInsert.setInt(2, idEskaera);
                        pstInsert.setString(3, fakturaFitxategia.getAbsolutePath());
                        pstInsert.executeUpdate();
                    }

                    JOptionPane.showMessageDialog(this,
                            "Faktura sortu eta gordeta: " + fakturaFitxategia.getAbsolutePath());
                    java.awt.Desktop.getDesktop().open(fakturaFitxategia);
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Errorea faktura idaztean: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errorea datu-basearekin: " + e.getMessage());
        }
    }
}
