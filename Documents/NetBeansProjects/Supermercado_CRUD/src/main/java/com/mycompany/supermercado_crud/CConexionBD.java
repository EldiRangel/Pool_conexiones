package com.mycompany.supermercado_crud;

import java.sql.Connection;
import java.sql.DriverManager; 
import java.sql.SQLException;   
import javafx.scene.control.Alert; 

public class CConexionBD {
    
    public Connection conectar = null;
    
    String usuario = "postgres";
    String contrasena = "31379406";
    String bd = "supermercado";
    String ip = "localhost";
    String puerto = "5432";
    
    // Cadena corregida para PostgreSQL
    String cadena = "jdbc:postgresql://" + ip + ":" + puerto + "/" + bd;
    
    public Connection establecerConexion() {
        try {
            // Driver corregido para Postgres
            Class.forName("org.postgresql.Driver");
            conectar = DriverManager.getConnection(cadena, usuario, contrasena);
            System.out.println(" Conexión exitosa a la base de datos");
            
        } catch (Exception e) {
            System.out.println(" Error de conexión: " + e.toString());
        } 
        return conectar;
    }

    public void cerrarConexion() {
        try {
            if (conectar != null && !conectar.isClosed()) {
                conectar.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (Exception e) {
            System.out.println("Error al cerrar: " + e.toString());
        }
    }
}