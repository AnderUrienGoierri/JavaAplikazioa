package birtek_interfaze_grafikoa;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.File;

public class MenuLogistika extends JFrame {

    private JPanel edukiPanela;
    private JTextField txtBilatu;
    
    // Erabiltzailearen datuak (Saioa hasita duena)
    private int unekoLangileaId;
    private String unekoLangileaIzena;
    private String unekoLangileaAbizena;
    private String unekoLangileaSaila;
    
    // Fitxaketa informazioa erakusteko etiketa
    private JLabel lblFitxaketaInfo;
    
    // Taulak
    private JTable tableSarrerak;
    private JTable tableBiltegiak;
    private JTable tableProduktuak;
    
    // Sarrera Berria elementuak
    private JComboBox<HornitzaileItem> comboHornitzaileak;
    private JCheckBox chkHornitzaileBerria;
    private JTextField txtNewHornIzena;
    private JTextField txtNewHornEmail;
    private JTextField txtNewHornIFZ;
    
    private JTextField txtProduktuIzena;
    private JTextField txtMarka;
    private JComboBox<KategoriaItem> comboKategoriak;
    private JComboBox<String> comboMotak;
    private JComboBox<BiltegiItem> comboBiltegiakSarrera;
    private JTextField txtKantitatea;
    
    private JTable tableLerroakBerriak;
    private DefaultTableModel modelLerroakBerriak;
    
    // Iragazkia
    private JComboBox<String> comboFilterEgoera;

    // Fitxak
    private JTabbedPane tabbedPane;
    
    // Sorters
    private TableRowSorter<DefaultTableModel> sorterSarrerak;
    private TableRowSorter<DefaultTableModel> sorterBiltegiak;
    private TableRowSorter<DefaultTableModel> sorterProduktuak;
    // --- MAIN ---
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MenuLogistika frame = new MenuLogistika();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // --- ERAIKITZAILEAK (CONSTRUCTORS) ---

    public MenuLogistika(int langileaId, String langileaIzena, String langileaAbizena, String langileaSaila) {
        this.unekoLangileaId = langileaId;
        this.unekoLangileaIzena = langileaIzena;
        this.unekoLangileaAbizena = langileaAbizena;
        this.unekoLangileaSaila = langileaSaila;
        initUI();
    }

    public MenuLogistika() {
        this(3, "Jon", "Etxebarria", "Logistika");
    }

    private void initUI() {
        setTitle("Birtek - LOGISTIKA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1150, 700); // Pixka bat zabalagoa informazioa sartzeko
        
        edukiPanela = new BackgroundPanel();
        edukiPanela.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(edukiPanela);
        edukiPanela.setLayout(new BorderLayout(0, 0));

        // --- GOIKO PANELA ---
        JPanel panelTop = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255, 255, 255, 220)); 
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelTop.setOpaque(false); 
        panelTop.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 128, 128), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        edukiPanela.add(panelTop, BorderLayout.NORTH);

        // EZKERRA: Bilatzailea
        JPanel panelBuscador = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBuscador.setOpaque(false); 
        JLabel lblBilatu = new JLabel("Bilatu: ");
        lblBilatu.setFont(new Font("SansSerif", Font.BOLD, 12));
        panelBuscador.add(lblBilatu);
        
        txtBilatu = new JTextField();
        txtBilatu.setColumns(20);
        txtBilatu.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) { filtrar(); }
        });
        panelBuscador.add(txtBilatu);
        panelTop.add(panelBuscador, BorderLayout.WEST);

        // ESKUINA: Erabiltzailea + Fitxaketa + Logout
        // GridBagLayout erabiltzen dugu elementuak hobeto kokatzeko
        JPanel panelUserInfo = new JPanel(new GridBagLayout());
        panelUserInfo.setOpaque(false);
        // 1. Erabiltzailearen izena
        JLabel lblUser = new JLabel(unekoLangileaSaila + " | " + unekoLangileaIzena + " " + unekoLangileaAbizena);
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblUser.setForeground(new Color(0, 102, 102));
        
        GridBagConstraints gbcUser = new GridBagConstraints();
        gbcUser.insets = new Insets(0, 10, 0, 10);
        gbcUser.fill = GridBagConstraints.VERTICAL;
        gbcUser.gridx = 0; gbcUser.gridy = 0;
        panelUserInfo.add(lblUser, gbcUser);

        // 2. Fitxaketa Panela (Botoiak + Info Labela)
        JPanel panelFitxaketaContainer = new JPanel();
        panelFitxaketaContainer.setOpaque(false);
        panelFitxaketaContainer.setLayout(new BoxLayout(panelFitxaketaContainer, BoxLayout.Y_AXIS)); // Bertikala
        
        // Botoiak
        JPanel panelBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panelBtns.setOpaque(false);
        
        JButton btnSarrera = new JButton("Sarrera");
        btnSarrera.setBackground(new Color(34, 139, 34)); // Berdea
        btnSarrera.setForeground(new Color(0, 0, 0));
        btnSarrera.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnSarrera.addActionListener(e -> fitxatu("Sarrera"));
        
        JButton btnIrteera = new JButton("Irteera");
        btnIrteera.setBackground(new Color(255, 140, 0)); // Laranja
        btnIrteera.setForeground(new Color(0, 0, 0));
        btnIrteera.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnIrteera.addActionListener(e -> fitxatu("Irteera"));
        
        // --- BOTOI BERRIA: HISTORIALA ---
        JButton btnHistoriala = new JButton("Historiala");
        btnHistoriala.setBackground(new Color(100, 149, 237)); // Urdin argia (CornflowerBlue)
        btnHistoriala.setForeground(new Color(0, 0, 0));
        btnHistoriala.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnHistoriala.addActionListener(e -> ikusiFitxaketaHistoriala());
        
        panelBtns.add(btnSarrera);
        panelBtns.add(btnIrteera);
        panelBtns.add(btnHistoriala); // Botoia gehitu
        
        // Info Labela
        lblFitxaketaInfo = new JLabel("Egoera kargatzen...");
        lblFitxaketaInfo.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblFitxaketaInfo.setHorizontalAlignment(SwingConstants.CENTER);
        lblFitxaketaInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panelFitxaketaContainer.add(panelBtns);
        panelFitxaketaContainer.add(Box.createVerticalStrut(2)); // Tarte txikia
        panelFitxaketaContainer.add(lblFitxaketaInfo);
        
        GridBagConstraints gbcFitxa = new GridBagConstraints();
        gbcFitxa.insets = new Insets(0, 10, 0, 10);
        gbcFitxa.fill = GridBagConstraints.VERTICAL;
        gbcFitxa.gridx = 1; gbcFitxa.gridy = 0;
        panelUserInfo.add(panelFitxaketaContainer, gbcFitxa);

        // 3. Logout botoia
        JButton btnLogout = new JButton("Saioa Itxi");
        btnLogout.setBackground(new Color(220, 20, 60));
        btnLogout.setForeground(new Color(0, 0, 0));
        btnLogout.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnLogout.addActionListener(e -> cerrarSesion());
        
        GridBagConstraints gbcLogout = new GridBagConstraints();
        gbcLogout.insets = new Insets(0, 10, 0, 10);
        gbcLogout.fill = GridBagConstraints.VERTICAL;
        gbcLogout.gridx = 2; gbcLogout.gridy = 0;
        panelUserInfo.add(btnLogout, gbcLogout);
        
        panelTop.add(panelUserInfo, BorderLayout.EAST);

        // --- FITXAK ---
        UIManager.put("TabbedPane.contentOpaque", false);
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setOpaque(false);
        
        edukiPanela.add(tabbedPane, BorderLayout.CENTER);

        // Tab-ak sortu
        crearTabSarrerak();
        crearTabBiltegiak();
        crearTabProduktuak();
        crearTabSarreraBerria();

        tabbedPane.addChangeListener(e -> {
            txtBilatu.setText(""); 
            int index = tabbedPane.getSelectedIndex();
            if (index == 0) cargarDatosSarrerak();
            else if (index == 1) cargarDatosBiltegiak();
            else if (index == 2) cargarDatosProduktuak();
            else if (index == 3) cargarCombosSarrera(); 
        });

        // Hasierako karga
        if (!java.beans.Beans.isDesignTime()) {
            cargarDatosSarrerak();
            cargarDatosBiltegiak();
            cargarDatosProduktuak();
            cargarCombosSarrera();
            
            // Fitxaketa egoera kargatu hasieran
            eguneratuFitxaketaEgoera();
        } else {
            // DUMMY DATUAK WINDOWBUILDER-ENTZAKO
            
            // 1. Sarrerak
            String[] colSarrerak = {"id_sarrera", "Hornitzailea", "data", "sarrera_egoera"};
            Object[][] dataSarrerak = {
                {5, "PC Componentes Pro", "2025-12-17T10:03:10", "Bidean"},
                {4, "PC Componentes Pro", "2025-12-15T16:42:59", "Jasota"},
                {3, "Amazon Business", "2025-12-08T03:44:45", "Jasota"},
                {2, "Ingram Micro", "2024-11-15T09:30", "Jasota"},
                {1, "PC Componentes Pro", "2024-11-01T10:00", "Jasota"}
            };
            DefaultTableModel modelSarrerak = new DefaultTableModel(dataSarrerak, colSarrerak);
            tableSarrerak.setModel(modelSarrerak);
            
            // 2. Biltegiak
            String[] colBiltegiak = {"id_biltegia", "izena", "biltegi_sku"};
            Object[][] dataBiltegiak = {
                {1, "Biltegi Nagusia", "BIL-001"},
                {2, "Biltegi Txikia", "BIL-002"}
            };
            DefaultTableModel modelBiltegiak = new DefaultTableModel(dataBiltegiak, colBiltegiak);
            tableBiltegiak.setModel(modelBiltegiak);
            
            // 3. Produktuak
            String[] colProduktuak = {"id_produktua", "Produktua", "Biltegia", "Sarrera ID", "Egoera", "Sarrera Data", "id_sarrera_lerroa"};
            Object[][] dataProduktuak = {
                {101, "Lenovo ThinkPad", "Biltegi Nagusia", 5, "Bidean", "2025-12-17", 10},
                {102, "Dell XPS 13", "Biltegi Txikia", 4, "Jasota", "2025-12-15", 11}
            };
            DefaultTableModel modelProduktuak = new DefaultTableModel(dataProduktuak, colProduktuak);
            tableProduktuak.setModel(modelProduktuak);

            // 4. Combos
            comboHornitzaileak.addItem(new HornitzaileItem(1, "PC Componentes Pro"));
            comboHornitzaileak.addItem(new HornitzaileItem(2, "Amazon Business"));
            
            comboKategoriak.addItem(new KategoriaItem(1, "Ordenagailuak"));
            comboKategoriak.addItem(new KategoriaItem(2, "Monitoreak"));
            
            comboBiltegiakSarrera.addItem(new BiltegiItem(1, "Biltegi Nagusia"));
            comboBiltegiakSarrera.addItem(new BiltegiItem(2, "Biltegi Txikia"));
            
            // 5. Fitxaketa Label
            lblFitxaketaInfo.setText("✅ BARRUAN (Sarrera: 2025-01-07 08:30:00)");
            lblFitxaketaInfo.setForeground(new Color(0, 100, 0));
        }
    }

    // --- FITXAKETA KUDEAKETA ---

    private void fitxatu(String mota) {
        String sqlCheck = "SELECT mota FROM fitxaketak WHERE langilea_id = ? ORDER BY id_fitxaketa DESC LIMIT 1";
        
        try (Connection con = DB_konexioa.konektatu()) {
            
            // 1. Egiaztatu
            try (PreparedStatement pstCheck = con.prepareStatement(sqlCheck)) {
                pstCheck.setInt(1, this.unekoLangileaId);
                try (ResultSet rs = pstCheck.executeQuery()) {
                    String azkenMota = null;
                    if (rs.next()) {
                        azkenMota = rs.getString("mota");
                    }
                    
                    if ("Sarrera".equals(mota) && "Sarrera".equals(azkenMota)) {
                        JOptionPane.showMessageDialog(this, "Jada barruan zaude (Sarrera fitxatuta).", "Errorea", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if ("Irteera".equals(mota) && "Irteera".equals(azkenMota)) {
                        JOptionPane.showMessageDialog(this, "Jada kanpoan zaude (Irteera fitxatuta).", "Errorea", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if ("Irteera".equals(mota) && azkenMota == null) {
                        JOptionPane.showMessageDialog(this, "Ezin duzu irten sartu gabe.", "Errorea", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            // 2. Txertatu
            String sqlInsert = "INSERT INTO fitxaketak (langilea_id, mota) VALUES (?, ?)";
            try (PreparedStatement pstInsert = con.prepareStatement(sqlInsert)) {
                pstInsert.setInt(1, this.unekoLangileaId);
                pstInsert.setString(2, mota);
                
                int rows = pstInsert.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, mota + " erregistratuta.", "Ongi", JOptionPane.INFORMATION_MESSAGE);
                    // Eguneratu label-a berehala
                    eguneratuFitxaketaEgoera();
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errorea: " + e.getMessage());
        }
    }

    // EGOERA EGUNERATZEKO
    private void eguneratuFitxaketaEgoera() {
        String sql = "SELECT mota, data, ordua FROM fitxaketak WHERE langilea_id = ? ORDER BY id_fitxaketa DESC LIMIT 1";
        
        try (Connection con = DB_konexioa.konektatu();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setInt(1, this.unekoLangileaId);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                String mota = rs.getString("mota");
                Date data = rs.getDate("data");
                Time ordua = rs.getTime("ordua");
                
                String testua = "";
                if ("Sarrera".equals(mota)) {
                    testua = "✅ BARRUAN (Sarrera: " + data + " " + ordua + ")";
                    lblFitxaketaInfo.setForeground(new Color(0, 100, 0)); // Berde iluna
                } else {
                    testua = "❌ KANPOAN (Irteera: " + data + " " + ordua + ")";
                    lblFitxaketaInfo.setForeground(new Color(200, 0, 0)); // Gorria
                }
                lblFitxaketaInfo.setText(testua);
            } else {
                lblFitxaketaInfo.setText("⚪ Ez dago erregistrorik.");
                lblFitxaketaInfo.setForeground(Color.GRAY);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            lblFitxaketaInfo.setText("Errorea datuak irakurtzean");
        }
    }

    // --- METODO BERRIA: HISTORIALA IKUSTEKO ---
    private void ikusiFitxaketaHistoriala() {
        JDialog dialog = new JDialog(this, "Nire Fitxaketa Historiala", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Taula sortu
        String[] zutabeak = {"Mota", "Data", "Ordua"};
        DefaultTableModel model = new DefaultTableModel(zutabeak, 0);
        JTable table = new JTable(model);
        
        // Estilo pixka bat
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.setRowHeight(25);
        
        JScrollPane scroll = new JScrollPane(table);
        dialog.add(scroll, BorderLayout.CENTER);

        // Datuak kargatu
        String sql = "SELECT mota, data, ordua FROM fitxaketak WHERE langilea_id = ? ORDER BY id_fitxaketa DESC";
        
        try (Connection con = DB_konexioa.konektatu();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setInt(1, this.unekoLangileaId);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("mota"),
                    rs.getDate("data"),
                    rs.getTime("ordua")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "Errorea historiala kargatzean: " + e.getMessage());
        }
        
        // Itxi botoia behean
        JButton btnItxi = new JButton("Itxi");
        btnItxi.addActionListener(e -> dialog.dispose());
        JPanel pnlBtn = new JPanel();
        pnlBtn.add(btnItxi);
        dialog.add(pnlBtn, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // --- TAB SARRERAK ---
    private void crearTabSarrerak() {
        JPanel panelSarrerak = new JPanel(new BorderLayout());
        panelSarrerak.setOpaque(false);
        
        JPanel panelSarrerakTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSarrerakTop.setOpaque(false);
        panelSarrerakTop.add(new JLabel("Egoera Iragazi:"));
        
        comboFilterEgoera = new JComboBox<>();
        comboFilterEgoera.addItem("Denak");
        comboFilterEgoera.addItem("Bidean");
        comboFilterEgoera.addItem("Jasota");
        comboFilterEgoera.addActionListener(e -> cargarDatosSarrerak()); 
        panelSarrerakTop.add(comboFilterEgoera);
        
        JButton btnRefrescarSarrerak = new JButton("Eguneratu Zerrenda");
        btnRefrescarSarrerak.addActionListener(e -> cargarDatosSarrerak());
        panelSarrerakTop.add(btnRefrescarSarrerak);
        
        panelSarrerak.add(panelSarrerakTop, BorderLayout.NORTH);
        
        tableSarrerak = new JTable();
        JScrollPane scroll = new JScrollPane(tableSarrerak);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        panelSarrerak.add(scroll, BorderLayout.CENTER);
        
        tabbedPane.addTab("Sarrerak", null, panelSarrerak, null);
    }

    // --- TAB BILTEGIAK ---
    private void crearTabBiltegiak() {
        JPanel panelBiltegiak = new JPanel(new BorderLayout());
        panelBiltegiak.setOpaque(false);
        
        JPanel panelBiltegiBtns = new JPanel();
        panelBiltegiBtns.setOpaque(false);
        JButton btnSortuBiltegia = new JButton("Sortu Biltegia");
        JButton btnAldatuBiltegia = new JButton("Aldatu");
        JButton btnEzabatuBiltegia = new JButton("Ezabatu");
        
        btnSortuBiltegia.addActionListener(e -> sortuBiltegia());
        btnAldatuBiltegia.addActionListener(e -> aldatuBiltegia());
        btnEzabatuBiltegia.addActionListener(e -> ezabatuBiltegia());
        
        panelBiltegiBtns.add(btnSortuBiltegia);
        panelBiltegiBtns.add(btnAldatuBiltegia);
        panelBiltegiBtns.add(btnEzabatuBiltegia);
        panelBiltegiak.add(panelBiltegiBtns, BorderLayout.NORTH);
        
        tableBiltegiak = new JTable();
        JScrollPane scroll = new JScrollPane(tableBiltegiak);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        panelBiltegiak.add(scroll, BorderLayout.CENTER);
        
        tabbedPane.addTab("Biltegiak", null, panelBiltegiak, null);
    }

    // --- TAB PRODUKTUAK ---
    private void crearTabProduktuak() {
        JPanel panelStock = new JPanel(new BorderLayout());
        panelStock.setOpaque(false);
        
        JPanel panelStockBtns = new JPanel();
        panelStockBtns.setOpaque(false);
        JButton btnAldatuKokapena = new JButton("Aldatu Biltegia");
        JButton btnJasoProduktua = new JButton("Markatu 'Jasota'");
        JButton btnBideanProduktua = new JButton("Markatu 'Bidean'");
        
        btnAldatuKokapena.addActionListener(e -> aldatuProduktuarenBiltegia());
        btnJasoProduktua.addActionListener(e -> markatuProduktuaJasota());
        btnBideanProduktua.addActionListener(e -> markatuProduktuaBidean());
        
        panelStockBtns.add(btnAldatuKokapena);
        panelStockBtns.add(btnJasoProduktua);
        panelStockBtns.add(btnBideanProduktua);
        panelStock.add(panelStockBtns, BorderLayout.NORTH);
        
        tableProduktuak = new JTable();
        JScrollPane scroll = new JScrollPane(tableProduktuak);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        panelStock.add(scroll, BorderLayout.CENTER);
        
        tabbedPane.addTab("Produktuak eta Kokapena", null, panelStock, null);
    }

    // --- TAB SARRERA BERRIA ---
    private void crearTabSarreraBerria() {
        JPanel panelSarreraBerria = new JPanel(new BorderLayout());
        panelSarreraBerria.setOpaque(false);
        
        JPanel panelFormContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255, 255, 255, 230)); 
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelFormContainer.setOpaque(false);
        panelFormContainer.setBorder(BorderFactory.createTitledBorder("Sarrera eta Produktu Berriaren Datuak"));
        
        JPanel panelHornitzailea = new JPanel(new GridLayout(2, 1));
        panelHornitzailea.setOpaque(false);
        panelHornitzailea.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        
        JPanel panelHornExisting = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelHornExisting.setOpaque(false);
        comboHornitzaileak = new JComboBox<>();
        chkHornitzaileBerria = new JCheckBox("Hornitzaile Berria Sortu?");
        chkHornitzaileBerria.setOpaque(false);
        chkHornitzaileBerria.addActionListener(e -> toggleHornitzaileMode()); 
        
        panelHornExisting.add(new JLabel("Hornitzailea Aukeratu: "));
        panelHornExisting.add(comboHornitzaileak);
        panelHornExisting.add(chkHornitzaileBerria);
        
        JPanel panelHornNew = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelHornNew.setOpaque(false);
        txtNewHornIzena = new JTextField(15);
        txtNewHornEmail = new JTextField(15);
        txtNewHornIFZ = new JTextField(10);
        
        panelHornNew.add(new JLabel("Izena:"));
        panelHornNew.add(txtNewHornIzena);
        panelHornNew.add(new JLabel("Emaila:"));
        panelHornNew.add(txtNewHornEmail);
        panelHornNew.add(new JLabel("IFZ:"));
        panelHornNew.add(txtNewHornIFZ);
        
        setNewHornitzaileEnabled(false);
        
        panelHornitzailea.add(panelHornExisting);
        panelHornitzailea.add(panelHornNew);
        panelFormContainer.add(panelHornitzailea, BorderLayout.NORTH); 
        
        JPanel panelForm = new JPanel(new GridBagLayout()); 
        panelForm.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        txtProduktuIzena = new JTextField(15);
        txtMarka = new JTextField(15);
        comboKategoriak = new JComboBox<>();
        comboMotak = new JComboBox<>();
        comboBiltegiakSarrera = new JComboBox<>();
        txtKantitatea = new JTextField(5);
        
        comboMotak.addItem("Generikoa");
        comboMotak.addItem("Eramangarria");
        comboMotak.addItem("Mahai-gainekoa");
        comboMotak.addItem("Mugikorra");
        comboMotak.addItem("Tableta");
        comboMotak.addItem("Zerbitzaria");
        comboMotak.addItem("Pantaila");
        comboMotak.addItem("Softwarea");
        comboMotak.addItem("Periferikoak");
        comboMotak.addItem("Kableak");

        // Formularioa (WindowBuilder-ek gbc klonatua behar du)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0; panelForm.add(new JLabel("Prod. Izena:"), (GridBagConstraints)gbc.clone());
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; panelForm.add(txtProduktuIzena, (GridBagConstraints)gbc.clone());
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.0; panelForm.add(new JLabel("Marka:"), (GridBagConstraints)gbc.clone());
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 1.0; panelForm.add(txtMarka, (GridBagConstraints)gbc.clone());

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; panelForm.add(new JLabel("Kategoria:"), (GridBagConstraints)gbc.clone());
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; panelForm.add(comboKategoriak, (GridBagConstraints)gbc.clone());
        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.0; panelForm.add(new JLabel("Mota:"), (GridBagConstraints)gbc.clone());
        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 1.0; panelForm.add(comboMotak, (GridBagConstraints)gbc.clone());

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0; panelForm.add(new JLabel("Biltegia:"), (GridBagConstraints)gbc.clone());
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0; panelForm.add(comboBiltegiakSarrera, (GridBagConstraints)gbc.clone());
        gbc.gridx = 2; gbc.gridy = 2; gbc.weightx = 0.0; panelForm.add(new JLabel("Kantitatea:"), (GridBagConstraints)gbc.clone());
        gbc.gridx = 3; gbc.gridy = 2; gbc.weightx = 1.0; panelForm.add(txtKantitatea, (GridBagConstraints)gbc.clone());
        
        JButton btnGehituLerroa = new JButton("Gehitu Zerrendara +");
        btnGehituLerroa.addActionListener(e -> gehituLerroaTaulara());
        
        JPanel panelCenterForm = new JPanel(new BorderLayout());
        panelCenterForm.setOpaque(false);
        panelCenterForm.add(panelForm, BorderLayout.CENTER);
        
        JPanel panelBtn = new JPanel();
        panelBtn.setOpaque(false);
        panelBtn.add(btnGehituLerroa);
        panelCenterForm.add(panelBtn, BorderLayout.SOUTH);
        
        panelFormContainer.add(panelCenterForm, BorderLayout.CENTER);
        panelSarreraBerria.add(panelFormContainer, BorderLayout.NORTH);
        
        String[] colNames = {"Izena", "Marka", "Kategoria", "Mota", "Biltegia", "Kantitatea"};
        modelLerroakBerriak = new DefaultTableModel(colNames, 0);
        tableLerroakBerriak = new JTable(modelLerroakBerriak);
        
        JScrollPane scrollNew = new JScrollPane(tableLerroakBerriak);
        scrollNew.setOpaque(false);
        scrollNew.getViewport().setOpaque(false);
        panelSarreraBerria.add(scrollNew, BorderLayout.CENTER);
        
        JButton btnGordeSarrera = new JButton("GORDE SARRERA ETA SORTU PRODUKTUAK");
        btnGordeSarrera.setBackground(new Color(0, 128, 0));
        btnGordeSarrera.setForeground(Color.WHITE);
        btnGordeSarrera.setFont(new Font("Arial", Font.BOLD, 14));
        btnGordeSarrera.addActionListener(e -> gordeSarreraOsoa());
        panelSarreraBerria.add(btnGordeSarrera, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Sarrera Berria", null, panelSarreraBerria, null);
    }

    // --- LAGUNTZAILEAK (COMBOS, LOGIKA) ---
    private void toggleHornitzaileMode() {
        boolean isNew = chkHornitzaileBerria.isSelected();
        setNewHornitzaileEnabled(isNew);
        comboHornitzaileak.setEnabled(!isNew);
    }
    
    private void setNewHornitzaileEnabled(boolean enabled) {
        txtNewHornIzena.setEnabled(enabled);
        txtNewHornEmail.setEnabled(enabled);
        txtNewHornIFZ.setEnabled(enabled);
        if(!enabled) {
            txtNewHornIzena.setText("");
            txtNewHornEmail.setText("");
            txtNewHornIFZ.setText("");
        }
    }

    private void cargarCombosSarrera() {
        comboHornitzaileak.removeAllItems();
        comboKategoriak.removeAllItems();
        comboBiltegiakSarrera.removeAllItems();
        if (java.beans.Beans.isDesignTime()) return;
        try (Connection con = DB_konexioa.konektatu()) {
            Statement st = con.createStatement();
            ResultSet rsH = st.executeQuery("SELECT id_hornitzailea, izena_soziala FROM hornitzaileak");
            while(rsH.next()) comboHornitzaileak.addItem(new HornitzaileItem(rsH.getInt(1), rsH.getString(2)));
            ResultSet rsK = st.executeQuery("SELECT id_kategoria, izena FROM produktu_kategoriak");
            while(rsK.next()) comboKategoriak.addItem(new KategoriaItem(rsK.getInt(1), rsK.getString(2)));
            ResultSet rsB = st.executeQuery("SELECT id_biltegia, izena FROM biltegiak");
            while(rsB.next()) comboBiltegiakSarrera.addItem(new BiltegiItem(rsB.getInt(1), rsB.getString(2)));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void gehituLerroaTaulara() {
        String izena = txtProduktuIzena.getText();
        String marka = txtMarka.getText();
        KategoriaItem kat = (KategoriaItem) comboKategoriak.getSelectedItem();
        String mota = (String) comboMotak.getSelectedItem();
        BiltegiItem bilt = (BiltegiItem) comboBiltegiakSarrera.getSelectedItem();
        String kantiStr = txtKantitatea.getText();
        if (izena.isEmpty() || marka.isEmpty() || kantiStr.isEmpty() || kat == null || bilt == null) {
            JOptionPane.showMessageDialog(this, "Mesedez, bete produktuaren eremu guztiak.");
            return;
        }
        try {
            int kanti = Integer.parseInt(kantiStr);
            if (kanti <= 0) throw new NumberFormatException();
            modelLerroakBerriak.addRow(new Object[]{izena, marka, kat, mota, bilt, kanti});
            txtProduktuIzena.setText(""); txtMarka.setText(""); txtKantitatea.setText("");
            txtProduktuIzena.requestFocus();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Kantitateak zenbakia izan behar du.");
        }
    }

    private void gordeSarreraOsoa() {
        if (modelLerroakBerriak.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Ez dago produkturik zerrendan."); return;
        }
        int hornitzaileaId = -1;
        if (chkHornitzaileBerria.isSelected()) {
            String izena = txtNewHornIzena.getText().trim();
            String email = txtNewHornEmail.getText().trim();
            String ifz = txtNewHornIFZ.getText().trim();
            if (izena.isEmpty() || email.isEmpty() || ifz.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Bete hornitzaile berriaren datuak."); return;
            }
            try (Connection con = DB_konexioa.konektatu()) {
                PreparedStatement pstCheck = con.prepareStatement("SELECT COUNT(*) FROM hornitzaileak WHERE emaila = ? OR ifz_nan = ?");
                pstCheck.setString(1, email); pstCheck.setString(2, ifz);
                ResultSet rsCheck = pstCheck.executeQuery();
                if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "ERROREA: Hornitzaile hori existitzen da jada.", "Bikoiztua", JOptionPane.ERROR_MESSAGE); return;
                }
                String sqlInsertH = "INSERT INTO hornitzaileak (izena_soziala, ifz_nan, emaila, pasahitza, helbidea, herria_id, posta_kodea) VALUES (?, ?, ?, '1234', 'Zehaztugabea', 1, '00000')";
                PreparedStatement pstH = con.prepareStatement(sqlInsertH, Statement.RETURN_GENERATED_KEYS);
                pstH.setString(1, izena); pstH.setString(2, ifz); pstH.setString(3, email);
                pstH.executeUpdate();
                ResultSet rsKey = pstH.getGeneratedKeys();
                if (rsKey.next()) hornitzaileaId = rsKey.getInt(1);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Errorea hornitzailea sortzean: " + e.getMessage()); return;
            }
        } else {
            HornitzaileItem item = (HornitzaileItem) comboHornitzaileak.getSelectedItem();
            if (item != null) hornitzaileaId = item.id;
            else { JOptionPane.showMessageDialog(this, "Aukeratu hornitzaile bat."); return; }
        }

        Connection con = null;
        try {
            con = DB_konexioa.konektatu(); con.setAutoCommit(false); 
            String sqlSarrera = "INSERT INTO sarrerak (hornitzailea_id, langilea_id, sarrera_egoera) VALUES (?, ?, 'Bidean')";
            PreparedStatement pstSarrera = con.prepareStatement(sqlSarrera, Statement.RETURN_GENERATED_KEYS);
            pstSarrera.setInt(1, hornitzaileaId); pstSarrera.setInt(2, this.unekoLangileaId); 
            pstSarrera.executeUpdate();
            ResultSet rsKeys = pstSarrera.getGeneratedKeys();
            int sarreraId = -1;
            if (rsKeys.next()) sarreraId = rsKeys.getInt(1); else throw new SQLException("Ez da sarrera IDrik sortu.");

            String sqlProd = "INSERT INTO produktuak (izena, marka, kategoria_id, mota, biltegi_id, hornitzaile_id, stock, produktu_egoera, salgai) VALUES (?, ?, ?, ?, ?, ?, ?, 'Berria', 0)";
            PreparedStatement pstProd = con.prepareStatement(sqlProd, Statement.RETURN_GENERATED_KEYS);
            String sqlLerroa = "INSERT INTO sarrera_lerroak (sarrera_id, produktua_id, kantitatea, sarrera_lerro_egoera) VALUES (?, ?, ?, 'Bidean')";
            PreparedStatement pstLerroa = con.prepareStatement(sqlLerroa);

            for (int i = 0; i < modelLerroakBerriak.getRowCount(); i++) {
                String izena = (String) modelLerroakBerriak.getValueAt(i, 0);
                String marka = (String) modelLerroakBerriak.getValueAt(i, 1);
                KategoriaItem kat = (KategoriaItem) modelLerroakBerriak.getValueAt(i, 2);
                String mota = (String) modelLerroakBerriak.getValueAt(i, 3);
                BiltegiItem bilt = (BiltegiItem) modelLerroakBerriak.getValueAt(i, 4);
                int kanti = (int) modelLerroakBerriak.getValueAt(i, 5);
                pstProd.setString(1, izena); pstProd.setString(2, marka); pstProd.setInt(3, kat.id);
                pstProd.setString(4, mota); pstProd.setInt(5, bilt.id); pstProd.setInt(6, hornitzaileaId);
                pstProd.setInt(7, kanti); pstProd.executeUpdate();
                ResultSet rsProdKey = pstProd.getGeneratedKeys();
                int prodId = -1;
                if (rsProdKey.next()) prodId = rsProdKey.getInt(1);
                pstLerroa.setInt(1, sarreraId); pstLerroa.setInt(2, prodId); pstLerroa.setInt(3, kanti); pstLerroa.executeUpdate();
            }
            con.commit();
            JOptionPane.showMessageDialog(this, "Sarrera ondo sortu da! ID: " + sarreraId);
            modelLerroakBerriak.setRowCount(0); txtNewHornIzena.setText(""); txtNewHornEmail.setText(""); txtNewHornIFZ.setText("");
            chkHornitzaileBerria.setSelected(false); toggleHornitzaileMode(); cargarCombosSarrera(); 
        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            JOptionPane.showMessageDialog(this, "Errorea prozesuan: " + e.getMessage()); e.printStackTrace();
        } finally {
            try { if (con != null) { con.setAutoCommit(true); con.close(); } } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    private void cargarDatosSarrerak() {
        String baseSql = "SELECT s.id_sarrera, h.izena_soziala AS Hornitzailea, s.data, s.sarrera_egoera FROM sarrerak s JOIN hornitzaileak h ON s.hornitzailea_id = h.id_hornitzailea ";
        String filter = (String) comboFilterEgoera.getSelectedItem();
        String sql = baseSql;
        if ("Bidean".equals(filter)) sql += " WHERE s.sarrera_egoera = 'Bidean' ";
        else if ("Jasota".equals(filter)) sql += " WHERE s.sarrera_egoera = 'Jasota' ";
        sql += " ORDER BY s.data DESC";
        if (java.beans.Beans.isDesignTime()) return;
        try (Connection con = DB_konexioa.konektatu(); PreparedStatement pst = con.prepareStatement(sql)) {
            DefaultTableModel model = TablaModelador.construirModelo(pst.executeQuery());
            tableSarrerak.setModel(model); sorterSarrerak = new TableRowSorter<>(model); tableSarrerak.setRowSorter(sorterSarrerak);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void cargarDatosBiltegiak() {
        String sql = "SELECT id_biltegia, izena, biltegi_sku FROM biltegiak";
        if (java.beans.Beans.isDesignTime()) return;
        try (Connection con = DB_konexioa.konektatu(); PreparedStatement pst = con.prepareStatement(sql)) {
            DefaultTableModel model = TablaModelador.construirModelo(pst.executeQuery());
            tableBiltegiak.setModel(model); sorterBiltegiak = new TableRowSorter<>(model); tableBiltegiak.setRowSorter(sorterBiltegiak);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void cargarDatosProduktuak() {
        String sql = "SELECT p.id_produktua, p.izena AS Produktua, b.izena AS Biltegia, s.id_sarrera AS 'Sarrera ID', sl.sarrera_lerro_egoera AS Egoera, s.data AS 'Sarrera Data', sl.id_sarrera_lerroa FROM sarrera_lerroak sl JOIN sarrerak s ON sl.sarrera_id = s.id_sarrera JOIN produktuak p ON sl.produktua_id = p.id_produktua JOIN biltegiak b ON p.biltegi_id = b.id_biltegia ORDER BY s.data DESC";
        if (java.beans.Beans.isDesignTime()) return;
        try (Connection con = DB_konexioa.konektatu(); PreparedStatement pst = con.prepareStatement(sql)) {
            DefaultTableModel model = TablaModelador.construirModelo(pst.executeQuery());
            tableProduktuak.setModel(model); sorterProduktuak = new TableRowSorter<>(model); tableProduktuak.setRowSorter(sorterProduktuak);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void filtrar() {
        String texto = txtBilatu.getText();
        TableRowSorter<DefaultTableModel> currentSorter = null;
        int index = tabbedPane.getSelectedIndex();
        if (index == 0) currentSorter = sorterSarrerak;
        else if (index == 1) currentSorter = sorterBiltegiak;
        else if (index == 2) currentSorter = sorterProduktuak;
        if (currentSorter != null) {
            if (texto.trim().length() == 0) currentSorter.setRowFilter(null);
            else currentSorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
        }
    }

    private void sortuBiltegia() {
        JTextField izenaField = new JTextField(); JTextField skuField = new JTextField();
        Object[] message = { "Biltegi Izena:", izenaField, "SKU Kodea:", skuField };
        int option = JOptionPane.showConfirmDialog(this, message, "Biltegi Berria", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection con = DB_konexioa.konektatu(); PreparedStatement pst = con.prepareStatement("INSERT INTO biltegiak (izena, biltegi_sku) VALUES (?, ?)")) {
                pst.setString(1, izenaField.getText()); pst.setString(2, skuField.getText()); pst.executeUpdate();
                cargarDatosBiltegiak(); JOptionPane.showMessageDialog(this, "Biltegia sortuta.");
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Errorea: " + e.getMessage()); }
        }
    }

    private void ezabatuBiltegia() {
        int row = tableBiltegiak.getSelectedRow(); if (row == -1) return;
        int modelRow = tableBiltegiak.convertRowIndexToModel(row);
        Object val = tableBiltegiak.getModel().getValueAt(modelRow, 0);
        int id = (val instanceof Number) ? ((Number)val).intValue() : Integer.parseInt(val.toString());
        try (Connection con = DB_konexioa.konektatu()) {
            PreparedStatement pstCheck = con.prepareStatement("SELECT COUNT(*) FROM produktuak WHERE biltegi_id = ?");
            pstCheck.setInt(1, id); ResultSet rs = pstCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "EZIN DA EZABATU: Produktuak ditu barruan.", "Errorea", JOptionPane.ERROR_MESSAGE); return;
            }
            if (JOptionPane.showConfirmDialog(this, "Ziur ezabatu nahi duzula?", "Ezabatu", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                PreparedStatement pst = con.prepareStatement("DELETE FROM biltegiak WHERE id_biltegia = ?");
                pst.setInt(1, id); pst.executeUpdate(); cargarDatosBiltegiak();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void aldatuBiltegia() {
        int row = tableBiltegiak.getSelectedRow(); if (row == -1) return;
        int modelRow = tableBiltegiak.convertRowIndexToModel(row);
        int id = Integer.parseInt(tableBiltegiak.getModel().getValueAt(modelRow, 0).toString());
        String oldName = (String) tableBiltegiak.getModel().getValueAt(modelRow, 1);
        String oldSku = (String) tableBiltegiak.getModel().getValueAt(modelRow, 2);
        JTextField izenaField = new JTextField(oldName); JTextField skuField = new JTextField(oldSku);
        if (JOptionPane.showConfirmDialog(this, new Object[]{ "Izena:", izenaField, "SKU:", skuField }, "Aldatu", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try (Connection con = DB_konexioa.konektatu(); PreparedStatement pst = con.prepareStatement("UPDATE biltegiak SET izena = ?, biltegi_sku = ? WHERE id_biltegia = ?")) {
                pst.setString(1, izenaField.getText()); pst.setString(2, skuField.getText()); pst.setInt(3, id); pst.executeUpdate(); cargarDatosBiltegiak();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void aldatuProduktuarenBiltegia() {
        int row = tableProduktuak.getSelectedRow(); if (row == -1) return;
        int modelRow = tableProduktuak.convertRowIndexToModel(row);
        int idProd = Integer.parseInt(tableProduktuak.getModel().getValueAt(modelRow, 0).toString());
        JComboBox<BiltegiItem> combo = new JComboBox<>();
        try (Connection con = DB_konexioa.konektatu()) {
            ResultSet rs = con.createStatement().executeQuery("SELECT id_biltegia, izena FROM biltegiak");
            while(rs.next()) combo.addItem(new BiltegiItem(rs.getInt(1), rs.getString(2)));
        } catch (Exception e) { e.printStackTrace(); return; }
        if (JOptionPane.showConfirmDialog(this, combo, "Aukeratu Biltegi Berria", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try (Connection con = DB_konexioa.konektatu(); PreparedStatement pst = con.prepareStatement("UPDATE produktuak SET biltegi_id = ? WHERE id_produktua = ?")) {
                pst.setInt(1, ((BiltegiItem)combo.getSelectedItem()).id); pst.setInt(2, idProd); pst.executeUpdate(); cargarDatosProduktuak();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void egiaztatuSarreraEgoera(int idSarrera) {
        try (Connection con = DB_konexioa.konektatu()) {
            PreparedStatement pst = con.prepareStatement("SELECT COUNT(*) FROM sarrera_lerroak WHERE sarrera_id = ? AND sarrera_lerro_egoera != 'Jasota'");
            pst.setInt(1, idSarrera); ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String egoera = (rs.getInt(1) == 0) ? "Jasota" : "Bidean";
                PreparedStatement pstUpd = con.prepareStatement("UPDATE sarrerak SET sarrera_egoera = ? WHERE id_sarrera = ?");
                pstUpd.setString(1, egoera); pstUpd.setInt(2, idSarrera); pstUpd.executeUpdate();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void markatuProduktuaJasota() { cambiarEstadoProducto("Jasota"); }
    private void markatuProduktuaBidean() { cambiarEstadoProducto("Bidean"); }

    private void cambiarEstadoProducto(String nuevoEstado) {
        int row = tableProduktuak.getSelectedRow(); if (row == -1) return;
        int modelRow = tableProduktuak.convertRowIndexToModel(row);
        Object valSarrera = tableProduktuak.getModel().getValueAt(modelRow, 3);
        Object valLerroa = tableProduktuak.getModel().getValueAt(modelRow, 6);
        int idSarrera = Integer.parseInt(valSarrera.toString()); int idLerroa = Integer.parseInt(valLerroa.toString());
        try (Connection con = DB_konexioa.konektatu()) {
            PreparedStatement pst = con.prepareStatement("UPDATE sarrera_lerroak SET sarrera_lerro_egoera = ? WHERE id_sarrera_lerroa = ?");
            pst.setString(1, nuevoEstado); pst.setInt(2, idLerroa);
            if (pst.executeUpdate() > 0) { egiaztatuSarreraEgoera(idSarrera); cargarDatosProduktuak(); cargarDatosSarrerak(); }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void cerrarSesion() {
        if (JOptionPane.showConfirmDialog(this, "Ziur zaude saioa itxi nahi duzula?", "Logout", JOptionPane.YES_NO_OPTION) == 0) {
            dispose(); new LoginPanela().setVisible(true);
        }
    }

    // --- HELPER CLASSES ---
    static class BiltegiItem { int id; String izena; public BiltegiItem(int id, String izena) { this.id = id; this.izena = izena; } public String toString() { return izena; } }
    static class HornitzaileItem { int id; String izena; public HornitzaileItem(int id, String izena) { this.id = id; this.izena = izena; } public String toString() { return izena; } }
    static class KategoriaItem { int id; String izena; public KategoriaItem(int id, String izena) { this.id = id; this.izena = izena; } public String toString() { return izena; } }

    static class BackgroundPanel extends JPanel {
        private Image img;
        public BackgroundPanel() {
            try {
                java.net.URL imgURL = MenuLogistika.class.getResource("/birtek_biltegia.png");
                if (imgURL != null) img = new ImageIcon(imgURL).getImage();
                else if (new File("src/birtek_biltegia.png").exists()) img = new ImageIcon("src/birtek_biltegia.png").getImage();
            } catch (Exception e) { e.printStackTrace(); }
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) {
                int panelW = getWidth(); int panelH = getHeight(); int imgW = img.getWidth(this); int imgH = img.getHeight(this);
                if (imgW > 0 && imgH > 0) {
                    double scale = Math.max((double)panelW / imgW, (double)panelH / imgH);
                    int newW = (int) (imgW * scale); int newH = (int) (imgH * scale);
                    int x = (panelW - newW) / 2; int y = (panelH - newH) / 2;
                    g.drawImage(img, x, y, newW, newH, this);
                } else { g.drawImage(img, 0, 0, panelW, panelH, this); }
                g.setColor(new Color(0, 0, 0, 100)); g.fillRect(0, 0, panelW, panelH);
            } else { g.setColor(new Color(245, 245, 245)); g.fillRect(0, 0, getWidth(), getHeight()); }
        }
    }
}