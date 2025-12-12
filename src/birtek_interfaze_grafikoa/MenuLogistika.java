package birtek_interfaze_grafikoa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MenuLogistika extends JFrame {

    private JTable tableSarrerak;
    private JTextField txtBilatu;
    private TableRowSorter<DefaultTableModel> sorter;

    public MenuLogistika() {
        setTitle("Birtek - " + Hizkuntza.get("menu_logistics"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 500);
        
        JPanel contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        JPanel panelTop = new JPanel(new BorderLayout());
        contentPane.add(panelTop, BorderLayout.NORTH);

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
        
        JLabel lblTitulo = new JLabel(Hizkuntza.get("lbl_logistics"));
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
        contentPane.add(lblTitulo, BorderLayout.SOUTH);

        tableSarrerak = new JTable();
        contentPane.add(new JScrollPane(tableSarrerak), BorderLayout.CENTER);
        
        cargarDatos();
    }

    private void cargarDatos() {
        String sql = "SELECT s.id_sarrera, h.izena_soziala AS Hornitzailea, s.data, s.sarrera_egoera FROM sarrerak s JOIN hornitzaileak h ON s.hornitzailea_id = h.id_hornitzailea ORDER BY s.data DESC";
        try (Connection con = DBConnection.conectar();
             PreparedStatement pst = con.prepareStatement(sql)) {
            DefaultTableModel model = TablaModelador.construirModelo(pst.executeQuery());
            tableSarrerak.setModel(model);
            sorter = new TableRowSorter<>(model);
            tableSarrerak.setRowSorter(sorter);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void filtrar() {
        String texto = txtBilatu.getText();
        if (texto.trim().length() == 0) sorter.setRowFilter(null);
        else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
    }

    private void cerrarSesion() {
        if (JOptionPane.showConfirmDialog(this, Hizkuntza.get("logout_confirm"), "Logout", JOptionPane.YES_NO_OPTION) == 0) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}