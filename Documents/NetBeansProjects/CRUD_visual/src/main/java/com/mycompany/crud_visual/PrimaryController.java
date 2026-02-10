package com.mycompany.crud_visual;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import java.sql.Connection;
import javax.swing.JOptionPane;

public class PrimaryController {

    
    @FXML private TextField txtHost;
    @FXML private TextField txtPuerto;
    @FXML private TextField txtBD;
    @FXML private TextField txtUsuario;
    @FXML private TextField txtContra;

    @FXML
    private void handleConectar() {
        Conexion con = new Conexion();
        
        Connection miConexion = con.getConexion(
            txtHost.getText(), 
            txtPuerto.getText(), 
            txtBD.getText(), 
            txtUsuario.getText(), 
            txtContra.getText()
        );

        if (miConexion != null) {
            JOptionPane.showMessageDialog(null, "¡Conexión exitosa a PostgreSQL!");
        }
    }
}