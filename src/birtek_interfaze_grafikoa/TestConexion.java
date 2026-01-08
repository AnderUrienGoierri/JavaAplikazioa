	package birtek_interfaze_grafikoa;

import java.sql.Connection;

	public class TestConexion {
	    public static void main(String[] args) {
	        System.out.println("Probando conexión...");
	        Connection con = DB_konexioa.konektatu();
	        
	        if (con != null) {
	            System.out.println("¡KONEKTATUTA!  driverrak funtzionatzen du eta datu baseak erantzun du.");
	        } else {
	            System.out.println("ERROREA: Errebisatu erabiltzailea, pasahitza, edota XAMPP.");
	        }
	    }
	}