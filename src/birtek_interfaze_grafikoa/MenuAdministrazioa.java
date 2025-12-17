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
        setBounds(100, 100, 950, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        // --- GOIKO PANELA ---
        JPanel panelTop = new JPanel(new BorderLayout());
        contentPane.add(panelTop, BorderLayout.NORTH);

        // Bilatzailea
        JPanel panelBuscador = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBuscador.add(new JLabel("Bilatu: "));
        txtBilatu = new JTextField(20);
        txtBilatu.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { filtrar(); }
        });
        panelBuscador.add(txtBilatu);
        panelTop.add(panelBuscador, BorderLayout.WEST);

        // Erabiltzaile Info + Logout
        JPanel panelUserInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblUser = new JLabel(uSaila + " | " + uIzena + " " + uAbizena + " (ID: " + uId + ")");
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblUser.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));
        lblUser.setForeground(new Color(0, 102, 102));
        panelUserInfo.add(lblUser);

        JButton btnLogout = new JButton("Saioa Itxi");
        btnLogout.setBackground(new Color(220, 20, 60));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.addActionListener(e -> cerrarSesion());
        panelUserInfo.add(btnLogout);
        
        panelTop.add(panelUserInfo, BorderLayout.EAST);

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

        cargarDatos();
    }
    
    // Default constructor (Testetarako)
    public MenuAdministrazioa() {
        this(1, "Ane", "Zubiaga", "Administrazioa");
    }

    private void cargarDatos() {
        try (Connection con = DBConnection.conectar();
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
            dispose(); new LoginFrame().setVisible(true);
        }
    }
}