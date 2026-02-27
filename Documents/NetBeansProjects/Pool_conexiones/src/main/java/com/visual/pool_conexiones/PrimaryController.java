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
        
        try (InputStream is = App.class.getClassLoader().getResourceAsStream("config.properties")) {
            config.load(is);
        } catch (Exception e) {
            updateConsole("Error al cargar config.properties");
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
        int minIdle = Integer.parseInt(config.getProperty("db.pool.minIdle"));
        int poolSize = Integer.parseInt(config.getProperty("db.pool.maxSize"));
        
        String tipoQuery = cbQuery.getValue().toLowerCase();
        String currentQuery = config.getProperty("query." + tipoQuery);
        
        int baseSamples = Integer.parseInt(config.getProperty("simulation.samples"));
        int[] iterativeSamples = chkIterative.isSelected() ? new int[]{100, 500, 1000} : new int[]{baseSamples};

        Engine engine = new Engine();

     
        new Thread(() -> {
            for (int samples : iterativeSamples) {
                if (App.stopRequested) break;

                updateConsole("\n>>> INICIANDO ESCALA: " + samples + " MUESTRAS (" + tipoQuery.toUpperCase() + ") <<<");
                
                updateConsole("[RAW] Procesando...");
                Metrics rawM = engine.iniciar(false, url, user, pass, currentQuery, samples, retries, minIdle, poolSize);

                if (App.stopRequested) break;
                try { Thread.sleep(1500); } catch (InterruptedException e) {}

                updateConsole("[POOLED] Procesando...");
                Metrics pooledM = engine.iniciar(true, url, user, pass, currentQuery, samples, retries, minIdle, poolSize);

                mostrarComparativa(samples, rawM, pooledM);
            }
            
            updateConsole("\n>>> SIMULACIÓN FINALIZADA. Revisa simulacion.log <<<");
            Platform.runLater(() -> btnStart.setDisable(false));
            
        }).start();
    }

    private void mostrarComparativa(int s, Metrics r, Metrics p) {
        updateConsole("\n--- RESULTADO DE " + s + " MUESTRAS ---");
        updateConsole("Tiempo RAW: " + r.tiempoTotal + "ms | Tiempo POOLED: " + p.tiempoTotal + "ms");
        if (p.tiempoTotal < r.tiempoTotal) {
            updateConsole("🏆 POOLED ganó por " + (r.tiempoTotal - p.tiempoTotal) + "ms.");
        } else {
            updateConsole("🏆 RAW fue más rápido.");
        }
    }

    @FXML
    private void stopSimulation() {
        App.stopRequested = true;
        updateConsole("\n[!] FRENO MANUAL ACTIVADO DESDE LA INTERFAZ [!]");
    }

    private void updateConsole(String text) {
        Platform.runLater(() -> {
            txtConsole.appendText(text + "\n");
        });
    }
}