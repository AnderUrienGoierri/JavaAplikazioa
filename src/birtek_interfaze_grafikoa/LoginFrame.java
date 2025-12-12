package birtek_interfaze_grafikoa;

import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.Font;
import java.awt.Color;
import java.awt.Image; // Irudiak eskalatzeko beharrezkoa

public class LoginFrame extends JFrame {

    private JPanel contentPane;
    private JTextField textEmail;
    private JPasswordField passwordField;
    private JComboBox<String> comboLang;
    
    // Testu etiketak
    private JLabel lblTitulo;
    private JLabel lblEmail;
    private JLabel lblPass;
    private JLabel lblIrudia; // Irudiarentzako etiketa berria
    private JButton btnSartu;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                LoginFrame frame = new LoginFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public LoginFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Leiho zabalagoa irudia sartzeko (750x400)
        setBounds(100, 100, 750, 400);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // --- IRUDIA (EZKERRALDEAN) ---
        lblIrudia = new JLabel("");
        lblIrudia.setBounds(0, 0, 350, 360); // Irudiaren tamaina eta posizioa
        try {
            // Irudia kargatu (ziurtatu 'birtek1.jpeg' src karpetan dagoela)
            ImageIcon originalIcon = new ImageIcon(LoginFrame.class.getResource("src/birtek1.jpeg"));
            
            // Irudia eskalatu leihoaren zatira egokitzeko
            Image img = originalIcon.getImage();
            Image newImg = img.getScaledInstance(350, 360, Image.SCALE_SMOOTH);
            lblIrudia.setIcon(new ImageIcon(newImg));
        } catch (Exception e) {
            // Irudia ez bada aurkitzen, mezu bat erakutsi
            lblIrudia.setText("Irudia ez da aurkitu / No image found");
            lblIrudia.setHorizontalAlignment(SwingConstants.CENTER);
            lblIrudia.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }
        contentPane.add(lblIrudia);

        // --- LOGIN FORMULARIOA (ESKUINALDEAN) ---
        int desplazamendua = 360; // Elementuak eskuinera mugitzeko X koordenatua

        // Hizkuntza hautatzailea
        String[] langs = {"Euskera", "Castellano", "English"};
        comboLang = new JComboBox<>(langs);
        comboLang.setBounds(desplazamendua + 220, 10, 120, 25);
        
        if("ES".equals(Hizkuntza.selectedLang)) comboLang.setSelectedIndex(1);
        else if("EN".equals(Hizkuntza.selectedLang)) comboLang.setSelectedIndex(2);
        else comboLang.setSelectedIndex(0);

        comboLang.addActionListener(e -> aldatuHizkuntza());
        contentPane.add(comboLang);

        // Titulua
        lblTitulo = new JLabel();
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTitulo.setBounds(desplazamendua + 50, 50, 250, 30);
        contentPane.add(lblTitulo);

        // Emaila
        lblEmail = new JLabel();
        lblEmail.setBounds(desplazamendua + 50, 110, 100, 14);
        contentPane.add(lblEmail);

        textEmail = new JTextField();
        textEmail.setBounds(desplazamendua + 150, 107, 200, 20);
        contentPane.add(textEmail);
        textEmail.setColumns(10);

        // Pasahitza
        lblPass = new JLabel();
        lblPass.setBounds(desplazamendua + 50, 150, 100, 14);
        contentPane.add(lblPass);

        passwordField = new JPasswordField();
        passwordField.setBounds(desplazamendua + 150, 147, 200, 20);
        contentPane.add(passwordField);

        // Sartu Botoia
        btnSartu = new JButton();
        btnSartu.setBackground(new Color(0, 128, 128)); // Birtek kolore korporatiboa (Teal)
        btnSartu.setForeground(Color.WHITE);
        btnSartu.addActionListener(e -> loguearse());
        btnSartu.setBounds(desplazamendua + 150, 200, 100, 30);
        contentPane.add(btnSartu);
        
        // Testuak hasieratu
        actualizarTextos();
    }

    private void aldatuHizkuntza() {
        String seleccion = (String) comboLang.getSelectedItem();
        if ("Castellano".equals(seleccion)) Hizkuntza.selectedLang = "ES";
        else if ("English".equals(seleccion)) Hizkuntza.selectedLang = "EN";
        else Hizkuntza.selectedLang = "EU";

        actualizarTextos();
    }
    
    private void actualizarTextos() {
        setTitle(Hizkuntza.get("login_title"));
        lblTitulo.setText(Hizkuntza.get("app_title"));
        lblEmail.setText(Hizkuntza.get("email"));
        lblPass.setText(Hizkuntza.get("pass"));
        btnSartu.setText(Hizkuntza.get("login_btn"));
    }

    private void loguearse() {
        String email = textEmail.getText();
        String pass = new String(passwordField.getPassword());
        
        String query = "SELECT id_langilea, izena, saila_id FROM langileak WHERE emaila = ? AND pasahitza = ?";
        try (Connection con = DBConnection.conectar();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, email);
            pst.setString(2, pass);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int sailaId = rs.getInt("saila_id");
                abrirMenuDepartamento(sailaId);
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Login Error", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errorea konexioan / Error de conexión");
        }
    }

    private void abrirMenuDepartamento(int sailaId) {
        switch (sailaId) {
            case 1: new MenuAdministrazioa().setVisible(true); break;
            case 2: new MenuTeknikoa().setVisible(true); break;
            case 3: new MenuSalmentak().setVisible(true); break;
            case 4: new MenuLogistika().setVisible(true); break;
            case 5: new MenuSistemak().setVisible(true); break;
            default: JOptionPane.showMessageDialog(null, "ID Ezezaguna / Desconocido");
        }
    }
   
}

