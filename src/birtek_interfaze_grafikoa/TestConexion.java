	package birtek_interfaze_grafikoa;

import java.sql.Connection;

	public class TestConexion {
	    public static void main(String[] args) {
	        System.out.println("Probando conexión...");
	        Connection con = DBConnection.conectar();
	        
	        if (con != null) {
	            System.out.println("¡ÉXITO! El driver funciona y la base de datos responde.");
	        } else {
	            System.out.println("FALLO: Revisa el usuario, contraseña o si MySQL está encendido (XAMPP/WAMP).");
	        }
	    }
	}