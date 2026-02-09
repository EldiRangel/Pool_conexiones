package com.mycompany.proyecto_visual;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

public class PrimaryController {

    // Estos nombres deben coincidir con el fx:id que pusiste en Scene Builder
    @FXML
    private TextField txtHost;
    @FXML
    private TextField txtPuerto;
    @FXML
    private TextField txtBD;
    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtContra; // Si usaste PasswordField en el diseño
    @FXML
    private TextField txtTabla;

    // Este es el método que el FXML no encontraba
    @FXML
    private void handleConectar() {
        String host = txtHost.getText();
        String puerto = txtPuerto.getText();
        String db = txtBD.getText();
        String user = txtUsuario.getText();
        String pass = txtContra.getText();
        String tabla = txtTabla.getText();

        System.out.println("Intentando conectar a: " + host + ":" + puerto);
        
        // Aquí llamaremos a tu clase DB_conexion más adelante
        if (DB_conexion.conectar(host, puerto, db, user, pass) != null) {
            System.out.println("¡Conexión exitosa a la tabla " + tabla + "!");
        } else {
            System.out.println("Error al conectar. Revisa los datos.");
        }
    }
}