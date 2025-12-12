package birtek_interfaze_grafikoa;

import javax.swing.*;
import java.awt.*;

public class MenuSistemak extends JFrame {

    public MenuSistemak() {
        setTitle("Birtek - " + Hizkuntza.get("menu_systems"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 450);
        getContentPane().setLayout(new BorderLayout());

        JPanel panelBotones = new JPanel(new GridLayout(3, 2, 15, 15));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton btnAdmin = new JButton(Hizkuntza.get("menu_admin"));
        btnAdmin.addActionListener(e -> new MenuAdministrazioa().setVisible(true));
        
        JButton btnSat = new JButton(Hizkuntza.get("menu_tech"));
        btnSat.addActionListener(e -> new MenuTeknikoa().setVisible(true));
        
        JButton btnVentas = new JButton(Hizkuntza.get("menu_sales"));
        btnVentas.addActionListener(e -> new MenuSalmentak().setVisible(true));
        
        JButton btnLogistika = new JButton(Hizkuntza.get("menu_logistics"));
        btnLogistika.addActionListener(e -> new MenuLogistika().setVisible(true));
        
        JButton btnDB = new JButton(Hizkuntza.get("check_db"));
        btnDB.addActionListener(e -> chequearDB());

        // SAIOA ITXI BOTOIA
        JButton btnExit = new JButton(Hizkuntza.get("logout"));
        btnExit.setBackground(Color.RED);
        btnExit.setForeground(Color.WHITE);
        btnExit.setFont(new Font("Tahoma", Font.BOLD, 14));
        btnExit.addActionListener(e -> cerrarSesion());

        panelBotones.add(btnAdmin);
        panelBotones.add(btnSat);
        panelBotones.add(btnVentas);
        panelBotones.add(btnLogistika);
        panelBotones.add(btnDB);
        panelBotones.add(btnExit);

        getContentPane().add(panelBotones, BorderLayout.CENTER);
        
        JLabel lblTitulo = new JLabel(Hizkuntza.get("sys_title"), SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
        getContentPane().add(lblTitulo, BorderLayout.NORTH);
    }
    
    private void chequearDB() {
        if(DBConnection.conectar() != null) {
            JOptionPane.showMessageDialog(this, "OK!");
        }
    }

    private void cerrarSesion() {
        if (JOptionPane.showConfirmDialog(this, Hizkuntza.get("logout_confirm"), "Logout", JOptionPane.YES_NO_OPTION) == 0) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}