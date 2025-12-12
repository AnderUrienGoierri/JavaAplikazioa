package birtek_interfaze_grafikoa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MenuSalmentak extends JFrame {

    private JTable tableBezeroak;
    private JTable tableEskaerak;
    private JTextField txtBilatu;
    private TableRowSorter<DefaultTableModel> sorterBezeroak;
    private TableRowSorter<DefaultTableModel> sorterEskaerak;
    private TableRowSorter<DefaultTableModel> currentSorter;

    public MenuSalmentak() {
        setTitle("Birtek - " + Hizkuntza.get("menu_sales"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 950, 600);

        JPanel panelTop = new JPanel(new BorderLayout());
        getContentPane().add(panelTop, BorderLayout.NORTH);

        JPanel panelBus = new JPanel();
        panelTop.add(panelBus, BorderLayout.WEST);
        panelBus.add(new JLabel(Hizkuntza.get("search")));
        txtBilatu = new JTextField(20);
        txtBilatu.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { filtrar(); }
        });
        panelBus.add(txtBilatu);

        // SAIOA ITXI BOTOIA
        JButton btnLogout = new JButton(Hizkuntza.get("logout"));
        btnLogout.setBackground(Color.RED);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.addActionListener(e -> cerrarSesion());
        panelTop.add(btnLogout, BorderLayout.EAST);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        JPanel panelCli = new JPanel(new BorderLayout());
        tableBezeroak = new JTable();
        panelCli.add(new JScrollPane(tableBezeroak), BorderLayout.CENTER);
        tabbedPane.addTab(Hizkuntza.get("tab_clients"), null, panelCli, null);

        JPanel panelEsk = new JPanel(new BorderLayout());
        tableEskaerak = new JTable();
        panelEsk.add(new JScrollPane(tableEskaerak), BorderLayout.CENTER);
        tabbedPane.addTab(Hizkuntza.get("tab_orders"), null, panelEsk, null);

        tabbedPane.addChangeListener(e -> {
            txtBilatu.setText("");
            currentSorter = (tabbedPane.getSelectedIndex() == 0) ? sorterBezeroak : sorterEskaerak;
        });

        cargarDatos();
    }

    private void cargarDatos() {
        try (Connection con = DBConnection.conectar()) {
            DefaultTableModel m1 = TablaModelador.construirModelo(con.prepareStatement("SELECT id_bezeroa, izena_edo_soziala, emaila, telefonoa FROM bezeroak").executeQuery());
            tableBezeroak.setModel(m1);
            sorterBezeroak = new TableRowSorter<>(m1);
            tableBezeroak.setRowSorter(sorterBezeroak);

            DefaultTableModel m2 = TablaModelador.construirModelo(con.prepareStatement("SELECT * FROM eskaerak ORDER BY data DESC").executeQuery());
            tableEskaerak.setModel(m2);
            sorterEskaerak = new TableRowSorter<>(m2);
            tableEskaerak.setRowSorter(sorterEskaerak);

            if (currentSorter == null) currentSorter = sorterBezeroak;
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void filtrar() {
        String texto = txtBilatu.getText();
        if (currentSorter != null) {
            if (texto.trim().length() == 0) currentSorter.setRowFilter(null);
            else currentSorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
        }
    }

    private void cerrarSesion() {
        if (JOptionPane.showConfirmDialog(this, Hizkuntza.get("logout_confirm"), "Logout", JOptionPane.YES_NO_OPTION) == 0) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}