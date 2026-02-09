package com.mycompany.proyecto_visual;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

public class PrimaryController {

    
    @FXML
    private TextField txtHost;
    @FXML
    private TextField txtPuerto;
    @FXML
    private TextField txtBD;
    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtContra; 
    @FXML
    private TextField txtTabla;

    
    @FXML
    private void handleConectar() {
        String host = txtHost.getText();
        String puerto = txtPuerto.getText();
        String db = txtBD.getText();
        String user = txtUsuario.getText();
        String pass = txtContra.getText();
        String tabla = txtTabla.getText();

        System.out.println("Intentando conectar a: " + host + ":" + puerto);
        
        
        if (DB_conexion.conectar(host, puerto, db, user, pass) != null) {
            System.out.println("¡Conexión exitosa a la tabla " + tabla + "!");
        } else {
            System.out.println("Error al conectar. Revisa los datos.");
        }
    }
}