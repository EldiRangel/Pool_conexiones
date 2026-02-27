package com.visual.pool_conexiones;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

   
    public static volatile boolean stopRequested = false;

    @Override
    public void start(Stage stage) throws Exception {
       Parent root = FXMLLoader.load(getClass().getResource("/primary.fxml"));
        stage.setTitle("Simulador Avanzado de Conexiones DB");
        stage.setScene(new Scene(root, 650, 500));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}