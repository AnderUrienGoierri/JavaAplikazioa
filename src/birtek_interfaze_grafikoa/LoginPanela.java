package birtek_interfaze_grafikoa;

import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.awt.Font;
import java.awt.Color;
import java.awt.Image;
import java.io.File;

public class LoginPanela extends JFrame {

    private JPanel edukiPanela;
    
    // JComboBox erabiltzen dugu emailak aukeratzeko
    private JComboBox<String> emailEremua; 
    
    private JPasswordField pasahitzEremua;
    private JComboBox<String> hizkuntzaKaxa;
    
    // Testu etiketak
    private JLabel tituluLabela;
    private JLabel emailLabela;
    private JLabel pasahitzLabela;
    private JLabel lblIrudia;
    private JButton sartuBotoia;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                LoginPanela frame = new LoginPanela();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public LoginPanela() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // ALDAKETA 1: Leihoa zabalagoa egin dugu (750 -> 900) elementuak ondo sartzeko
        setBounds(100, 100, 900, 400);
        
        edukiPanela = new JPanel();
        edukiPanela.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(edukiPanela);
        edukiPanela.setLayout(null);

        // --- IRUDIA (EZKERRALDEAN) ---
        lblIrudia = new JLabel("");
        lblIrudia.setBounds(0, 0, 350, 360);
        try {
            ImageIcon originalIcon = null;
            // 1. Saiakera: Classpath bidez
            java.net.URL imgURL = LoginPanela.class.getResource("/birtek1.jpeg");
            
            if (imgURL != null) {
                originalIcon = new ImageIcon(imgURL);
            } else {
                // 2. Saiakera: Fitxategi-sisteman zuzenean
                if (new File("src/birtek1.jpeg").exists()) {
                    originalIcon = new ImageIcon("src/birtek1.jpeg");
                }
            }

            if (originalIcon != null) {
                Image img = originalIcon.getImage();
                Image newImg = img.getScaledInstance(350, 360, Image.SCALE_SMOOTH);
                lblIrudia.setIcon(new ImageIcon(newImg));
            } else {
                throw new Exception("Irudia ez da aurkitu");
            }

        } catch (Exception e) {
            lblIrudia.setText("Irudia ez da aurkitu / No image found");
            lblIrudia.setHorizontalAlignment(SwingConstants.CENTER);
            lblIrudia.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            System.err.println("Errorea irudia kargatzean: Ziurtatu 'birtek1.jpeg' src karpetan dagoela.");
        }
        edukiPanela.add(lblIrudia);

        // --- LOGIN FORMULARIOA (ESKUINALDEAN) ---
        int desplazamendua = 360;

        // Hizkuntza hautatzailea
        String[] langs = {"Euskera", "Castellano", "English"};
        hizkuntzaKaxa = new JComboBox<>(langs);
        // Hizkuntza kaxa pixka bat eskuinerago mugitu dugu leihoa zabaldu dugulako
        hizkuntzaKaxa.setBounds(desplazamendua + 350, 10, 120, 25);
        
        if("ES".equals(Hizkuntza.hizkuntzaAukeratua)) hizkuntzaKaxa.setSelectedIndex(1);
        else if("EN".equals(Hizkuntza.hizkuntzaAukeratua)) hizkuntzaKaxa.setSelectedIndex(2);
        else hizkuntzaKaxa.setSelectedIndex(0);

        hizkuntzaKaxa.addActionListener(e -> aldatuHizkuntza());
        edukiPanela.add(hizkuntzaKaxa);

        // Titulua
        tituluLabela = new JLabel();
        tituluLabela.setFont(new Font("Tahoma", Font.BOLD, 18));
        tituluLabela.setBounds(desplazamendua + 50, 50, 300, 30);
        edukiPanela.add(tituluLabela);

        // Emaila
        emailLabela = new JLabel();
        emailLabela.setBounds(desplazamendua + 50, 110, 100, 14);
        edukiPanela.add(emailLabela);

        // ALDAKETA 2: Email eremua askoz zabalagoa (200 -> 300)
        emailEremua = new JComboBox<>();
        emailEremua.setBounds(desplazamendua + 150, 107, 300, 25); 
        emailEremua.setEditable(false); 
        edukiPanela.add(emailEremua);
        
        // Datuak kargatu ComboBox-ean
        if (!java.beans.Beans.isDesignTime()) {
             kargatuEmailak();
        }

        // Pasahitza
        pasahitzLabela = new JLabel();
        pasahitzLabela.setBounds(desplazamendua + 50, 150, 100, 14);
        edukiPanela.add(pasahitzLabela);

        // ALDAKETA 3: Pasahitz eremua ere zabalagoa simetria mantentzeko (200 -> 300)
        pasahitzEremua = new JPasswordField();
        pasahitzEremua.setBounds(desplazamendua + 150, 147, 300, 25); // Altuera ere 25 jarri dut emailaren berdina izateko
        edukiPanela.add(pasahitzEremua);

        // Sartu Botoia
        sartuBotoia = new JButton();
        sartuBotoia.setBackground(new Color(0, 128, 128));
        sartuBotoia.setForeground(new Color(0, 0, 0));
        sartuBotoia.addActionListener(e -> saioaHasi());
        sartuBotoia.setBounds(desplazamendua + 150, 200, 120, 30); // Botoia ere pixka bat zabalago
        edukiPanela.add(sartuBotoia);
        
        // Testuak hasieratu
        eguneratuTextuak();
    }

    // METODOA emailak eta sailak kargatzeko
    private void kargatuEmailak() {
	        String query = "SELECT l.emaila, s.izena AS saila_izena " +
	                       "FROM langileak l " +
	                       "JOIN langile_sailak s ON l.saila_id = s.id_saila " +
	                       "ORDER BY l.emaila ASC";

        try (Connection konexioa = DB_konexioa.konektatu();
             Statement stmt = konexioa.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            emailEremua.removeAllItems();

            while (rs.next()) {
                String email = rs.getString("emaila");
                String saila = rs.getString("saila_izena");
                // "emaila (Saila)" formatua erakutsi
                emailEremua.addItem(email + " (" + saila + ")");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            emailEremua.addItem("Errorea datuak kargatzean");
        }
    }

    private void aldatuHizkuntza() {
        String seleccion = (String) hizkuntzaKaxa.getSelectedItem();
        if ("Castellano".equals(seleccion)) Hizkuntza.hizkuntzaAukeratua = "ES";
        else if ("English".equals(seleccion)) Hizkuntza.hizkuntzaAukeratua = "EN";
        else Hizkuntza.hizkuntzaAukeratua = "EU";

        eguneratuTextuak();
    }
    
    private void eguneratuTextuak() {
        setTitle(Hizkuntza.get("login_title"));
        tituluLabela.setText(Hizkuntza.get("app_title"));
        emailLabela.setText(Hizkuntza.get("email"));
        pasahitzLabela.setText(Hizkuntza.get("pass"));
        sartuBotoia.setText(Hizkuntza.get("login_btn"));
    }

    private void saioaHasi() {
        String aukeratutakoa = (String) emailEremua.getSelectedItem();
        
        if (aukeratutakoa == null || aukeratutakoa.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Aukeratu erabiltzaile bat / Seleccione un usuario");
            return;
        }

        // "emaila (Saila)" formatutik emaila bakarrik atera
        String email = aukeratutakoa.split(" \\(")[0];
        
        String pass = new String(pasahitzEremua.getPassword());
        
        String query = "SELECT id_langilea, izena, saila_id FROM langileak WHERE emaila = ? AND pasahitza = ?";
        try (Connection konexioa = DB_konexioa.konektatu();
             PreparedStatement pst = konexioa.prepareStatement(query)) {
            pst.setString(1, email);
            pst.setString(2, pass);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int sailaId = rs.getInt("saila_id");
                irekiSailMenua(sailaId);
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Login Error: Pasahitza okerra / Contraseña incorrecta", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errorea konexioan / Error de conexión");
        }
    }

    private void irekiSailMenua(int sailaId) {
        switch (sailaId) {
            case 1: new MenuZuzendaritza().setVisible(true); break;
            case 2: new MenuAdministrazioa().setVisible(true); break;
            case 3: new MenuSalmentak().setVisible(true); break;
            case 4: new MenuTeknikoa().setVisible(true); break;
            case 5: new MenuLogistika().setVisible(true); break;
            default: JOptionPane.showMessageDialog(null, "ID Ezezaguna / Desconocido");
        }
    }
}