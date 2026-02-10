package com.mycompany.universalmarketcrud;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    // La conexión se queda guardada aquí para usarla en todo el programa
    public static Connection con = null;

    public static boolean conectar(String host, String port, String db, String user, String pass) {
        try {
            // URL para PostgreSQL
            String url = "jdbc:postgresql://" + host + ":" + port + "/" + db;
            con = DriverManager.getConnection(url, user, pass);
            return true; 
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
}