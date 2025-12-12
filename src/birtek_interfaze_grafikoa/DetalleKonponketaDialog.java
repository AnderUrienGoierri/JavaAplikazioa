package birtek_interfaze_grafikoa;


import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DetalleKonponketaDialog extends JDialog {

    private int idKonponketa;
    private JComboBox<String> comboEstado;
    private JTextArea textOharrak;

    public DetalleKonponketaDialog(JFrame parent, int id) {
        super(parent, Hizkuntza.get("dialog_rep_title"), true);
        this.idKonponketa = id;
        
        setBounds(100, 100, 400, 300);
        getContentPane().setLayout(null);

        JLabel lblId = new JLabel("ID: " + id);
        lblId.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblId.setBounds(20, 20, 200, 20);
        getContentPane().add(lblId);

        JLabel lblEstado = new JLabel(Hizkuntza.get("status"));
        lblEstado.setBounds(20, 60, 100, 14);
        getContentPane().add(lblEstado);

        comboEstado = new JComboBox<>();
        comboEstado.addItem("Zain");
        comboEstado.addItem("Prozesuan");
        comboEstado.addItem("Konponduta");
        comboEstado.addItem("Konponezina");
        comboEstado.setBounds(130, 57, 150, 22);
        getContentPane().add(comboEstado);

        JLabel lblNotas = new JLabel(Hizkuntza.get("notes"));
        lblNotas.setBounds(20, 100, 100, 14);
        getContentPane().add(lblNotas);

        textOharrak = new JTextArea();
        textOharrak.setBounds(20, 120, 340, 80);
        getContentPane().add(textOharrak);

        JButton btnGorde = new JButton(Hizkuntza.get("save"));
        btnGorde.setBackground(new Color(0, 128, 0));
        btnGorde.setForeground(Color.WHITE);
        btnGorde.addActionListener(e -> guardarCambios());
        btnGorde.setBounds(130, 220, 140, 30);
        getContentPane().add(btnGorde);

        cargarDatosActuales();
    }

    private void cargarDatosActuales() {
        String sql = "SELECT konponketa_egoera, oharrak FROM konponketak WHERE id_konponketa = ?";
        try (Connection con = DBConnection.conectar();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, idKonponketa);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                comboEstado.setSelectedItem(rs.getString("konponketa_egoera"));
                textOharrak.setText(rs.getString("oharrak"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void guardarCambios() {
        String sql = "UPDATE konponketak SET konponketa_egoera = ?, oharrak = ? WHERE id_konponketa = ?";
        try (Connection con = DBConnection.conectar();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, (String) comboEstado.getSelectedItem());
            pst.setString(2, textOharrak.getText());
            pst.setInt(3, idKonponketa);
            pst.executeUpdate();
            dispose();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}