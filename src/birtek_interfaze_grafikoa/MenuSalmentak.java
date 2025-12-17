package birtek_interfaze_grafikoa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MenuSalmentak extends JFrame {

    private JTable tableBezeroak, tableEskaerak;
    private JTextField txtBilatu;
    private TableRowSorter<DefaultTableModel> sorterBezeroak, sorterEskaerak, currentSorter;
    
    // Erabiltzailearen datuak
    private int uId;
    private String uIzena;
    private String uAbizena;
    private String uSaila;

    // --- ERAIKITZAILEA EGUNERATUA ---
    public MenuSalmentak(int id, String izena, String abizena, String saila) {
        this.uId = id;
        this.uIzena = izena;
        this.uAbizena = abizena;
        this.uSaila = saila;
        
        setTitle("Birtek - SALMENTAK");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 950, 600);

        JPanel panelTop = new JPanel(new BorderLayout());
        getContentPane().add(panelTop, BorderLayout.NORTH);

        JPanel panelBus = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBus.add(new JLabel("Bilatu: "));
        txtBilatu = new JTextField(20);
        txtBilatu.addKeyListener(new KeyAdapter() { public void keyReleased(KeyEvent e) { filtrar(); } });
        panelBus.add(txtBilatu);
        panelTop.add(panelBus, BorderLayout.WEST);

        JPanel panelUser = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblUser = new JLabel(uSaila + " | " + uIzena + " " + uAbizena + " (ID: " + uId + ")");
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblUser.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));
        lblUser.setForeground(new Color(0, 102, 102));
        panelUser.add(lblUser);
        
        JButton btnLogout = new JButton("Saioa Itxi");
        btnLogout.setBackground(Color.RED); btnLogout.setForeground(Color.WHITE);
        btnLogout.addActionListener(e -> cerrarSesion());
        panelUser.add(btnLogout);
        panelTop.add(panelUser, BorderLayout.EAST);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        JPanel p1 = new JPanel(new BorderLayout());
        tableBezeroak = new JTable(); p1.add(new JScrollPane(tableBezeroak));
        tabbedPane.addTab("Bezeroak", p1);
        
        JPanel p2 = new JPanel(new BorderLayout());
        tableEskaerak = new JTable(); p2.add(new JScrollPane(tableEskaerak));
        tabbedPane.addTab("Eskaerak", p2);
        
        tabbedPane.addChangeListener(e -> {
            txtBilatu.setText("");
            currentSorter = (tabbedPane.getSelectedIndex() == 0) ? sorterBezeroak : sorterEskaerak;
        });
        
        cargarDatos();
    }
    
    public MenuSalmentak() { this(4, "Maite", "Lasa", "Salmentak"); }

    private void cargarDatos() {
        try(Connection c = DBConnection.conectar()) {
            DefaultTableModel m1 = TablaModelador.construirModelo(c.prepareStatement("SELECT id_bezeroa, izena_edo_soziala, emaila FROM bezeroak").executeQuery());
            tableBezeroak.setModel(m1); sorterBezeroak = new TableRowSorter<>(m1); tableBezeroak.setRowSorter(sorterBezeroak);
            
            DefaultTableModel m2 = TablaModelador.construirModelo(c.prepareStatement("SELECT * FROM eskaerak").executeQuery());
            tableEskaerak.setModel(m2); sorterEskaerak = new TableRowSorter<>(m2); tableEskaerak.setRowSorter(sorterEskaerak);
            
            if(currentSorter==null) currentSorter=sorterBezeroak;
        } catch(Exception e) { e.printStackTrace(); }
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
            dispose(); new LoginFrame().setVisible(true);
        }
    }
}