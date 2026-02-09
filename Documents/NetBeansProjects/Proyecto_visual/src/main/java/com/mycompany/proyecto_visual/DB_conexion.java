package com.mycompany.proyecto_visual;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB_conexion {
    // Variable para mantener la conexión activa
    public static Connection cadena;

    public static Connection conectar(String host, String puerto, String nombreBD, String usuario, String contra) {
        try {
            // URL dinámica para conectar a cualquier base de datos de tu PC
            String url = "jdbc:postgresql://" + host + ":" + puerto + "/" + nombreBD;
            cadena = DriverManager.getConnection(url, usuario, contra);
            System.out.println("Conexión exitosa a " + nombreBD);
        } catch (SQLException e) {
            System.out.println("Error al conectar: " + e.getMessage());
            cadena = null;
        }
        return cadena;
    }
}