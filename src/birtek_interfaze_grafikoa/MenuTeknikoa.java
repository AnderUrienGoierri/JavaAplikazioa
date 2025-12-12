package birtek_interfaze_grafikoa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MenuTeknikoa extends JFrame {

    private JTable tableKonponketak;
    private JTable tableProduktuak;
    private JTextField txtBilatu;
    private TableRowSorter<DefaultTableModel> sorterKonponketak;
    private TableRowSorter<DefaultTableModel> sorterProduktuak;
    private TableRowSorter<DefaultTableModel> currentSorter;

    public MenuTeknikoa() {
        setTitle("Birtek - " + Hizkuntza.get("menu_tech"));
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

        // TAB 1: Konponketak
        JPanel panelRep = new JPanel(new BorderLayout());
        tableKonponketak = new JTable();
        tableKonponketak.setDefaultEditor(Object.class, null);
        panelRep.add(new JScrollPane(tableKonponketak), BorderLayout.CENTER);
        
        JButton btnRefresh = new JButton(Hizkuntza.get("refresh"));
        btnRefresh.addActionListener(e -> cargarDatos());
        panelRep.add(btnRefresh, BorderLayout.NORTH);

        tableKonponketak.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tableKonponketak.getSelectedRow();
                    if (row != -1) {
                        int modelRow = tableKonponketak.convertRowIndexToModel(row);
                        int id = (int) tableKonponketak.getModel().getValueAt(modelRow, 0);
                        new DetalleKonponketaDialog(MenuTeknikoa.this, id).setVisible(true);
                        cargarDatos();
                    }
                }
            }
        });

        tabbedPane.addTab(Hizkuntza.get("tab_repairs"), null, panelRep, null);

        // TAB 2: Produktuak
        JPanel panelProd = new JPanel(new BorderLayout());
        tableProduktuak = new JTable();
        panelProd.add(new JScrollPane(tableProduktuak), BorderLayout.CENTER);
        tabbedPane.addTab(Hizkuntza.get("tab_products"), null, panelProd, null);

        tabbedPane.addChangeListener(e -> {
            txtBilatu.setText("");
            currentSorter = (tabbedPane.getSelectedIndex() == 0) ? sorterKonponketak : sorterProduktuak;
        });

        cargarDatos();
    }

    private void cargarDatos() {
        try (Connection con = DBConnection.conectar()) {
            DefaultTableModel modelRep = TablaModelador.construirModelo(con.prepareStatement("SELECT id_konponketa, produktua_id, konponketa_egoera, oharrak FROM konponketak").executeQuery());
            tableKonponketak.setModel(modelRep);
            sorterKonponketak = new TableRowSorter<>(modelRep);
            tableKonponketak.setRowSorter(sorterKonponketak);

            DefaultTableModel modelProd = TablaModelador.construirModelo(con.prepareStatement("SELECT id_produktua, izena, marka, produktu_egoera, salgai FROM produktuak").executeQuery());
            tableProduktuak.setModel(modelProd);
            sorterProduktuak = new TableRowSorter<>(modelProd);
            tableProduktuak.setRowSorter(sorterProduktuak);
            
            if (currentSorter == null) currentSorter = sorterKonponketak;
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