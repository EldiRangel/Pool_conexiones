package com.visual.pool_conexiones;

import com.simulador.logic.Engine;
import com.simulador.logic.Metrics;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.InputStream;
import java.util.Properties;

public class PrimaryController {

    @FXML private TextArea txtConsole;
    @FXML private ComboBox<String> cbQuery;
    @FXML private CheckBox chkIterative;
    @FXML private Button btnStart;

    private Properties config = new Properties();

    @FXML
    public void initialize() {
        cbQuery.getItems().addAll("SELECT", "INSERT", "UPDATE");
        cbQuery.getSelectionModel().selectFirst();
        
        try (InputStream is = getClass().getResourceAsStream("/config.properties")) {
            if (is != null) {
                config.load(is);
            } else {
                updateConsole("Error: No se encontro config.properties en resources");
            }
        } catch (Exception e) {
            updateConsole("Error al cargar configuracion");
        }
    }

    @FXML
    private void runSimulation() {
        App.stopRequested = false;
        txtConsole.clear();
        btnStart.setDisable(true); 
        
        String url = config.getProperty("db.url");
        String user = config.getProperty("db.user");
        String pass = config.getProperty("db.password");
        int retries = Integer.parseInt(config.getProperty("simulation.maxRetries"));
        int poolSize = Integer.parseInt(config.getProperty("db.pool.maxSize"));
        
        String tipoQuery = cbQuery.getValue().toLowerCase();
        String currentQuery = config.getProperty("query." + tipoQuery);
        
        int baseSamples = Integer.parseInt(config.getProperty("simulation.samples"));
        int[] iterativeSamples = chkIterative.isSelected() ? new int[]{100, 500, 1000} : new int[]{baseSamples};

        Engine engine = new Engine();

        new Thread(() -> {
            for (int samples : iterativeSamples) {
                if (App.stopRequested) break;

                updateConsole("\n>>> INICIANDO: " + samples + " MUESTRAS (" + tipoQuery.toUpperCase() + ") <<<");
                
                updateConsole("[RAW] Procesando...");
                Metrics rawM = engine.iniciar(false, url, user, pass, currentQuery, samples, retries, poolSize);

                if (App.stopRequested) break;
                
                try { Thread.sleep(1000); } catch (InterruptedException e) {}

                updateConsole("[POOLED] Procesando...");
                Metrics pooledM = engine.iniciar(true, url, user, pass, currentQuery, samples, retries, poolSize);

                mostrarComparativa(samples, rawM, pooledM);
            }
            
            updateConsole("\n>>> PROCESO TERMINADO. Revisa simulacion.log <<<");
            Platform.runLater(() -> btnStart.setDisable(false));
            
        }).start();
    }

    private void mostrarComparativa(int s, Metrics r, Metrics p) {
        updateConsole("\nRESULTADOS " + s + " MUESTRAS ");
        updateConsole("Tiempo RAW: " + r.tiempoTotal + "ms");
        updateConsole("Tiempo POOLED: " + p.tiempoTotal + "ms");
        
        if (p.exitosas.get() > 0 || r.exitosas.get() > 0) {
            if (p.tiempoTotal < r.tiempoTotal) {
                updateConsole("RESULTADO: POOLED fue mas rapido por " + (r.tiempoTotal - p.tiempoTotal) + "ms");
            } else {
                updateConsole("RESULTADO: RAW fue mas rapido");
            }
        } else {
            updateConsole("AVISO: Ambas pruebas fallaron. Revisa la conexion a la base de datos.");
        }
    }

    @FXML
    private void stopSimulation() {
        App.stopRequested = true;
        updateConsole("\n[!] DETENCION MANUAL SOLICITADA [!]");
    }

    private void updateConsole(String text) {
        Platform.runLater(() -> {
            txtConsole.appendText(text + "\n");
        });
    }
}