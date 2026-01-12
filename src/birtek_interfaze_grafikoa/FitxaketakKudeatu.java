package birtek_interfaze_grafikoa;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class FitxaketakKudeatu extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel edukiPanela;
	private JTable taula;
	private DefaultTableModel eredua;

	/**
	 * Applikazioa abiarazi.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FitxaketakKudeatu frame = new FitxaketakKudeatu();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Leioa sortu.
	 */
	public FitxaketakKudeatu() {
		setTitle("Birtek - Fitxaketak Kudeatu");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 500);
		edukiPanela = new JPanel();
		edukiPanela.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(edukiPanela);
		edukiPanela.setLayout(new BorderLayout(0, 0));

		// Goiko panela (Izenburua)
		JPanel goikoPanela = new JPanel();
		goikoPanela.setBackground(new Color(0, 102, 102));
		edukiPanela.add(goikoPanela, BorderLayout.NORTH);
		JLabel izenburuEtiketa = new JLabel("FITXAKETEN KUDEAKETA");
		izenburuEtiketa.setForeground(Color.WHITE);
		izenburuEtiketa.setFont(new Font("SansSerif", Font.BOLD, 18));
		goikoPanela.add(izenburuEtiketa);

		// Erdiko panela (Taula)
		JScrollPane korritzePanela = new JScrollPane();
		edukiPanela.add(korritzePanela, BorderLayout.CENTER);
		taula = new JTable();
		korritzePanela.setViewportView(taula);

		// Beheko panela (Botoiak)
		JPanel behekoPanela = new JPanel();
		edukiPanela.add(behekoPanela, BorderLayout.SOUTH);
		behekoPanela.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

		JButton kargatuBotoia = new JButton("Kargatu");
		kargatuBotoia.addActionListener(e -> datuakKargatu());
		behekoPanela.add(kargatuBotoia);

		JButton berriaBotoia = new JButton("Berria");
		berriaBotoia.addActionListener(e -> fitxaketaBerria());
		behekoPanela.add(berriaBotoia);

		JButton aldatuBotoia = new JButton("Modifikatu");
		aldatuBotoia.addActionListener(e -> fitxaketaAldatu());
		behekoPanela.add(aldatuBotoia);

		JButton ezabatuBotoia = new JButton("Ezabatu");
		ezabatuBotoia.addActionListener(e -> fitxaketaEzabatu());
		behekoPanela.add(ezabatuBotoia);

		JButton irtenBotoia = new JButton("Atera");
		irtenBotoia.addActionListener(e -> dispose());
		behekoPanela.add(irtenBotoia);

		// Hasierako datuak kargatu
		datuakKargatu();
	}

	private void datuakKargatu() {
		String galdera = "SELECT * FROM fitxaketak ORDER BY id_fitxaketa DESC";
		try (Connection konexioa = DB_Konexioa.konektatu(); 
			 PreparedStatement sententziaPrestatua = konexioa.prepareStatement(galdera); 
			 ResultSet emaitza = sententziaPrestatua.executeQuery()) {
			
			eredua = TaulaModelatzailea.ereduaEraiki(emaitza);
			taula.setModel(eredua);
			
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Errorea datuak kargatzean: " + e.getMessage(), "Errorea", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void fitxaketaBerria() {
		JTextField langileaIdTestua = new JTextField();
		String[] aukerak = {"Sarrera", "Irteera"};
		JComboBox<String> motaHautatzailea = new JComboBox<>(aukerak);

		Object[] mezua = {
			"Langile ID:", langileaIdTestua,
			"Mota:", motaHautatzailea
		};

		int aukera = JOptionPane.showConfirmDialog(null, mezua, "Fitxaketa Berria", JOptionPane.OK_CANCEL_OPTION);
		if (aukera == JOptionPane.OK_OPTION) {
			try (Connection konexioa = DB_Konexioa.konektatu()) {
				String galdera = "INSERT INTO fitxaketak (langilea_id, mota) VALUES (?, ?)";
				PreparedStatement sententziaPrestatua = konexioa.prepareStatement(galdera);
				sententziaPrestatua.setInt(1, Integer.parseInt(langileaIdTestua.getText()));
				sententziaPrestatua.setString(2, (String) motaHautatzailea.getSelectedItem());
				
				if (sententziaPrestatua.executeUpdate() > 0) {
					// JOptionPane.showMessageDialog(this, "Fitxaketa ongi gehitu da.");
					datuakKargatu();
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Errorea gordetzean: " + e.getMessage());
			}
		}
	}

	private void fitxaketaAldatu() {
		int errenkada = taula.getSelectedRow();
		if (errenkada == -1) {
			JOptionPane.showMessageDialog(this, "Hautatu fitxaketa bat modifikatzeko.");
			return;
		}

		int id = Integer.parseInt(taula.getValueAt(errenkada, 0).toString()); 
		String langileaIdKatea = taula.getValueAt(errenkada, 1).toString();
		String motaHautatua = taula.getValueAt(errenkada, 2).toString();

		JTextField langileaIdTestua = new JTextField(langileaIdKatea);
		String[] aukerak = {"Sarrera", "Irteera"};
		JComboBox<String> motaHautatzailea = new JComboBox<>(aukerak);
		motaHautatzailea.setSelectedItem(motaHautatua);

		Object[] mezua = {
			"Langile ID:", langileaIdTestua,
			"Mota:", motaHautatzailea
		};

		int aukera = JOptionPane.showConfirmDialog(null, mezua, "Aldatu Fitxaketa", JOptionPane.OK_CANCEL_OPTION);
		if (aukera == JOptionPane.OK_OPTION) {
			try (Connection konexioa = DB_Konexioa.konektatu()) {
				String galdera = "UPDATE fitxaketak SET langilea_id = ?, mota = ? WHERE id_fitxaketa = ?";
				PreparedStatement sententziaPrestatua = konexioa.prepareStatement(galdera);
				sententziaPrestatua.setInt(1, Integer.parseInt(langileaIdTestua.getText()));
				sententziaPrestatua.setString(2, (String) motaHautatzailea.getSelectedItem());
				sententziaPrestatua.setInt(3, id);
				
				if (sententziaPrestatua.executeUpdate() > 0) {
					// JOptionPane.showMessageDialog(this, "Fitxaketa ongi eguneratu da.");
					datuakKargatu();
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Errorea eguneratzean: " + e.getMessage());
			}
		}
	}

	private void fitxaketaEzabatu() {
		int errenkada = taula.getSelectedRow();
		if (errenkada == -1) {
			JOptionPane.showMessageDialog(this, "Hautatu fitxaketa bat ezabatzeko.");
			return;
		}

		int id = Integer.parseInt(taula.getValueAt(errenkada, 0).toString());
		int berretsi = JOptionPane.showConfirmDialog(this, "Ziur al zaude fitxaketa hau ezabatu nahi duzula?", "Berretsi", JOptionPane.YES_NO_OPTION);
		
		if (berretsi == JOptionPane.YES_OPTION) {
			try (Connection konexioa = DB_Konexioa.konektatu()) {
				String galdera = "DELETE FROM fitxaketak WHERE id_fitxaketa = ?";
				PreparedStatement sententziaPrestatua = konexioa.prepareStatement(galdera);
				sententziaPrestatua.setInt(1, id);
				
				if (sententziaPrestatua.executeUpdate() > 0) {
					// JOptionPane.showMessageDialog(this, "Fitxaketa ezabatu da.");
					datuakKargatu();
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(this, "Errorea ezabatzean: " + e.getMessage());
			}
		}
	}
}
