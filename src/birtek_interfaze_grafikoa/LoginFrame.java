package birtek_interfaze_grafikoa;


import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Color;

public class LoginFrame extends JFrame {

    private JPanel edukiPanela;
    private JTextField textEmail;
    private JPasswordField passwordField;
    private JComboBox<String> comboLang;
    private JLabel lblTitulo, lblEmail, lblPass;
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
        setTitle(Hizkuntza.get("app_title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 350);
        edukiPanela = new JPanel();
        edukiPanela.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(edukiPanela);
        edukiPanela.setLayout(null);

        // --- HIZKUNTZA SELECTOR ---
        String[] langs = {"Euskera", "Castellano", "English"};
        comboLang = new JComboBox<>(langs);
        comboLang.setBounds(300, 10, 120, 25);
        // Ezarri defektuzko balioa
        if(Hizkuntza.selectedLang.equals("ES")) comboLang.setSelectedIndex(1);
        else if(Hizkuntza.selectedLang.equals("EN")) comboLang.setSelectedIndex(2);
        else comboLang.setSelectedIndex(0);

        comboLang.addActionListener(e -> aldatuHizkuntza());
        edukiPanela.add(comboLang);

        lblTitulo = new JLabel(Hizkuntza.get("app_title"));
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTitulo.setBounds(50, 20, 250, 30);
        edukiPanela.add(lblTitulo);

        lblEmail = new JLabel(Hizkuntza.get("email"));
        lblEmail.setBounds(50, 80, 100, 14);
        edukiPanela.add(lblEmail);

        textEmail = new JTextField();
        textEmail.setBounds(150, 77, 200, 20);
        edukiPanela.add(textEmail);
        textEmail.setColumns(10);

        lblPass = new JLabel(Hizkuntza.get("pass"));
        lblPass.setBounds(50, 120, 100, 14);
        edukiPanela.add(lblPass);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 117, 200, 20);
        edukiPanela.add(passwordField);

        btnSartu = new JButton(Hizkuntza.get("login_btn"));
        btnSartu.setBackground(new Color(0, 128, 128));
        btnSartu.setForeground(Color.WHITE);
        btnSartu.addActionListener(e -> loguearse());
        btnSartu.setBounds(150, 170, 100, 30);
        edukiPanela.add(btnSartu);
    }

    private void aldatuHizkuntza() {
        String seleccion = (String) comboLang.getSelectedItem();
        if ("Castellano".equals(seleccion)) Hizkuntza.selectedLang = "ES";
        else if ("English".equals(seleccion)) Hizkuntza.selectedLang = "EN";
        else Hizkuntza.selectedLang = "EU";

        // Eguneratu testuak momentuan
        setTitle(Hizkuntza.get("app_title"));
        lblTitulo.setText(Hizkuntza.get("app_title"));
        lblEmail.setText(Hizkuntza.get("email"));
        lblPass.setText(Hizkuntza.get("pass"));
        btnSartu.setText(Hizkuntza.get("login_btn"));
    }

    private void loguearse() {
        String email = textEmail.getText();
        String pass = new String(passwordField.getPassword());
        // String query = "SELECT id_langilea, izena, saila_id FROM langileak WHERE emaila = ? AND pasahitza = ?"; 
        // Deskomentatu goikoa DB konexioarekin probatzeko. Orain simulazioa egingo dugu:
        
        // SIMULACION PARA PROBAR SIN DB SI ES NECESARIO (Kendu hau DB baduzu)
        // abrirMenuDepartamento(1); return; 

        // SQL BENETAKOA:
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
        }
    }

    private void abrirMenuDepartamento(int sailaId) {
        switch (sailaId) {
            case 1: new MenuAdministrazioa().setVisible(true); break;
            case 2: new MenuTeknikoa().setVisible(true); break;
            case 3: new MenuSalmentak().setVisible(true); break;
            case 4: new MenuLogistika().setVisible(true); break;
            case 5: new MenuSistemak().setVisible(true); break;
            default: JOptionPane.showMessageDialog(null, "Saila ezezaguna");
        }
    }
    private void sysout() {
		// TODO Auto-generated method stub

	}
}

