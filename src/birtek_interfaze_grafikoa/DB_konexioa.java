package birtek_interfaze_grafikoa;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB_Konexioa {
    private static final String URL_HELBIDEA = "jdbc:mysql://localhost:3306/birtek_db";
    private static final String ERABILTZAILEA = "root";
    private static final String PASAHITZA = "1MG32025";

    public static Connection konektatu() {
        Connection konexioa = null;
        try {
            konexioa = DriverManager.getConnection(URL_HELBIDEA, ERABILTZAILEA, PASAHITZA);
            System.out.println("ONDO konektatu da datu basera: Birtek DB");
        } catch (SQLException e) {
            System.err.println("Errorea konektatzean: " + e.getMessage());
        }
        return konexioa;
    }
}
