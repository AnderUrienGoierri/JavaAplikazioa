package birtek_interfaze_grafikoa;

import javax.swing.*;
import java.awt.*;

public class MenuZuzendaritza extends JFrame {
    
    private int uId;
    private String uIzena;
    private String uAbizena;
    private String uSaila;

    // --- ERAIKITZAILEA EGUNERATUA ---
    public MenuZuzendaritza (int id, String izena, String abizena, String saila) {
        this.uId = id;
        this.uIzena = izena;
        this.uAbizena = abizena;
        this.uSaila = saila;
        
        setTitle("Birtek - SISTEMAK (Super Admin)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 450);
        getContentPane().setLayout(new BorderLayout());

        // HEADER
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblUser = new JLabel(uSaila + " | " + uIzena + " " + uAbizena + " (ID: " + uId + ")");
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblUser.setForeground(new Color(0, 102, 102));
        panelTop.add(lblUser);
        getContentPane().add(panelTop, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridLayout(3, 2, 15, 15));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Botoiak beste menuetara pasatzeko datuak
        JButton b1 = new JButton("ADMINISTRAZIOA"); 
        b1.addActionListener(e -> new MenuAdministrazioa(uId, uIzena, uAbizena, "AdminMode").setVisible(true));
        
        JButton b2 = new JButton("TEKNIKOA"); 
        b2.addActionListener(e -> new MenuTeknikoa(uId, uIzena, uAbizena, "SatMode").setVisible(true));
        
        JButton b3 = new JButton("SALMENTAK"); 
        b3.addActionListener(e -> new MenuSalmentak(uId, uIzena, uAbizena, "SalesMode").setVisible(true));
        
        JButton b4 = new JButton("LOGISTIKA"); 
        b4.addActionListener(e -> new MenuLogistika(uId, uIzena, uAbizena, "LogisMode").setVisible(true));
        
        JButton b5 = new JButton("DB CHECK"); 
        b5.addActionListener(e -> JOptionPane.showMessageDialog(this, "Konexioa OK"));
        
        JButton btnExit = new JButton("SAIOA ITXI");
        btnExit.setBackground(Color.RED);
        btnExit.setForeground(Color.WHITE);
        btnExit.addActionListener(e -> cerrarSesion());

        panelBotones.add(b1); panelBotones.add(b2);
        panelBotones.add(b3); panelBotones.add(b4);
        panelBotones.add(b5); panelBotones.add(btnExit);

        getContentPane().add(panelBotones, BorderLayout.CENTER);
    }
    
    public MenuZuzendaritza() { this(5, "Aitor", "Mendizabal", "Sistemak"); }

    private void cerrarSesion() {
        if (JOptionPane.showConfirmDialog(this, "Irten?", "Logout", JOptionPane.YES_NO_OPTION) == 0) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}