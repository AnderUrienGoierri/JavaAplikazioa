package birtek_interfaze_grafikoa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/birtek_db";
    private static final String USER = "ander_sysadmin"; // Usamos el usuario admin para el login general
    private static final String PASS = "1234";

    public static Connection conectar() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Conexión exitosa a Birtek DB");
        } catch (SQLException e) {
            System.err.println("Error al conectar: " + e.getMessage());
        }
        return con;
    }
}