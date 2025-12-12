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

    public MenuAdministrazioa() {
        setTitle("Birtek - " + Hizkuntza.get("menu_admin"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 950, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        // --- GOIKO PANELA (Bilatzailea + Saioa Itxi) ---
        JPanel panelTop = new JPanel(new BorderLayout());
        contentPane.add(panelTop, BorderLayout.NORTH);

        JPanel panelBuscador = new JPanel();
        panelTop.add(panelBuscador, BorderLayout.WEST);
        panelBuscador.add(new JLabel(Hizkuntza.get("search")));
        
        txtBilatu = new JTextField();
        txtBilatu.setColumns(20);
        txtBilatu.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) { filtrar(); }
        });
        panelBuscador.add(txtBilatu);

        // SAIOA ITXI BOTOIA
        JButton btnLogout = new JButton(Hizkuntza.get("logout"));
        btnLogout.setBackground(new Color(220, 20, 60)); // Gorria
        btnLogout.setForeground(Color.WHITE);
        btnLogout.addActionListener(e -> cerrarSesion());
        panelTop.add(btnLogout, BorderLayout.EAST);

        // --- ERDIKO PANELA ---
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        JPanel panelLangileak = new JPanel(new BorderLayout());
        tabbedPane.addTab(Hizkuntza.get("tab_employees"), null, panelLangileak, null);

        tableLangileak = new JTable();
        panelLangileak.add(new JScrollPane(tableLangileak), BorderLayout.CENTER);
        
        JButton btnLoad = new JButton(Hizkuntza.get("load_data"));
        btnLoad.addActionListener(e -> cargarDatos());
        panelLangileak.add(btnLoad, BorderLayout.NORTH);

        cargarDatos();
    }

    private void cargarDatos() {
        try (Connection con = DBConnection.conectar();
             PreparedStatement pst = con.prepareStatement("SELECT id_langilea, izena, abizena, emaila, saila_id FROM langileak")) {
            
            DefaultTableModel model = TablaModelador.construirModelo(pst.executeQuery());
            tableLangileak.setModel(model);
            sorter = new TableRowSorter<>(model);
            tableLangileak.setRowSorter(sorter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filtrar() {
        String texto = txtBilatu.getText();
        if (texto.trim().length() == 0) sorter.setRowFilter(null);
        else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
    }

    // Saioa ixteko metodoa
    private void cerrarSesion() {
        if (JOptionPane.showConfirmDialog(this, Hizkuntza.get("logout_confirm"), "Logout", JOptionPane.YES_NO_OPTION) == 0) {
            dispose(); // Leiho hau itxi
            new LoginFrame().setVisible(true); // Login pantaila ireki
        }
    }
}