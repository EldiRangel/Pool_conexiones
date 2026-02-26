package com.visual.pool_conexiones;

import com.simulador.logic.Engine;
import java.io.InputStream;
import java.util.Properties;

public class App {

    public static void main(String[] args) {
        Properties config = new Properties();
        
        
        try (InputStream is = App.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is == null) {
                System.out.println("Error: No se encontró el archivo config.properties");
                return;
            }
            config.load(is);
        } catch (Exception e) {
            System.err.println("Error al cargar la configuración: " + e.getMessage());
            return;
        }

       
        String url = config.getProperty("db.url");
        String user = config.getProperty("db.user");
        String pass = config.getProperty("db.password");
        String query = config.getProperty("db.query");
        int samples = Integer.parseInt(config.getProperty("simulation.samples"));
        int retries = Integer.parseInt(config.getProperty("simulation.maxRetries"));
        int poolSize = Integer.parseInt(config.getProperty("db.pool.maxSize"));

        Engine engine = new Engine();

        // Simulación RAW 
        System.out.println("--- INICIANDO SIMULACION RAW ---");
        engine.iniciar(false, url, user, pass, query, samples, retries, poolSize);

        // Pausa entre simulaciones
        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        //  Simulación POOLED 
        System.out.println("\n--- INICIANDO SIMULACION POOLED ---");
        engine.iniciar(true, url, user, pass, query, samples, retries, poolSize);
        
        System.out.println("\nProceso finalizado. Verifique el archivo 'simulacion.log' [cite: 7]");
    }
}