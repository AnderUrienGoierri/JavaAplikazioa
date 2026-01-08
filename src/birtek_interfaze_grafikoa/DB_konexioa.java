package birtek_interfaze_grafikoa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB_konexioa {
    private static final String URL = "jdbc:mysql://localhost:3306/birtek_db";
    private static final String USER = "root"; // orain root bezela hasiko dugu
    private static final String PASS = "1MG32025";

    public static Connection konektatu() {
        Connection konexioa = null;
        try {
            konexioa = DriverManager.getConnection(URL, USER, PASS);
            // terminalean hau agertuko da:
            System.out.println("ONDO konektatu da datu basera: a Birtek DB");
        } catch (SQLException e) {
            System.err.println("Errorea konektatzean: " + e.getMessage());
        }
        return konexioa;
    }
}