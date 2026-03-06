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
        
        Metrics.limpiarLog();
        
        String url = config.getProperty("db.url");
        String user = config.getProperty("db.user");
        String pass = config.getProperty("db.password");
        int retries = Integer.parseInt(config.getProperty("simulation.maxRetries"));
        int poolSize = Integer.parseInt(config.getProperty("db.pool.maxSize"));
        int minIdle = Integer.parseInt(config.getProperty("db.pool.minIdle", "2")); 
        
        String tipoQuery = cbQuery.getValue().toLowerCase();
        
        // Metemos la consulta elegida en un arreglo de 1 posición para que Engine la acepte
        String[] queriesToRun = new String[]{ config.getProperty("db.query." + tipoQuery) };
        
        int baseSamples = Integer.parseInt(config.getProperty("simulation.samples"));
        int[] iterativeSamples = chkIterative.isSelected() ? new int[]{100, 500, 1000} : new int[]{baseSamples};

        Engine engine = new Engine();

        new Thread(() -> {
            for (int samples : iterativeSamples) {
                if (App.stopRequested) break;

                updateConsole("\n>>> INICIANDO: " + samples + " MUESTRAS (" + tipoQuery.toUpperCase() + ") <<<");
                
                updateConsole("[RAW] Procesando...");
                Metrics rawM = engine.iniciar(false, url, user, pass, queriesToRun, samples, retries, minIdle, poolSize);

                if (App.stopRequested) break;
                
                try { Thread.sleep(1000); } catch (InterruptedException e) {}

                updateConsole("[POOLED] Procesando...");
                Metrics pooledM = engine.iniciar(true, url, user, pass, queriesToRun, samples, retries, minIdle, poolSize);

                mostrarComparativa(samples, rawM, pooledM);
            }
            
            updateConsole("\n>>> PROCESO TERMINADO. Revisa simulacion.log <<<");
            Platform.runLater(() -> btnStart.setDisable(false));
            
        }).start();
    }

    // Modificado para mostrar conteo de queries exitosas y fallidas
    private void mostrarComparativa(int s, Metrics r, Metrics p) {
        updateConsole("\n>>> RESULTADOS " + s + " MUESTRAS <<<");
        
        updateConsole("--- MODO RAW ---");
        updateConsole("Tiempo: " + r.tiempoTotal + "ms");
        updateConsole("Exitosas: " + r.exitosas.get() + " | Fallidas: " + r.fallidas.get());
        
        updateConsole("--- MODO POOLED ---");
        updateConsole("Tiempo: " + p.tiempoTotal + "ms");
        updateConsole("Exitosas: " + p.exitosas.get() + " | Fallidas: " + p.fallidas.get());
        
        updateConsole("------------------------");
        if (p.exitosas.get() > 0 || r.exitosas.get() > 0) {
            if (p.tiempoTotal < r.tiempoTotal) {
                updateConsole("RESULTADO: POOLED fue mas rapido por " + (r.tiempoTotal - p.tiempoTotal) + "ms");
            } else {
                updateConsole("RESULTADO: RAW fue mas rapido por " + (p.tiempoTotal - r.tiempoTotal) + "ms");
            }
        } else {
            updateConsole("AVISO: Ambas pruebas fallaron. Revisa la conexion.");
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