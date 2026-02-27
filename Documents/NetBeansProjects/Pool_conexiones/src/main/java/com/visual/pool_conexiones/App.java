package com.visual.pool_conexiones;

import com.simulador.logic.Engine;
import com.simulador.logic.Metrics;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

public class App {

    public static volatile boolean stopRequested = false;

    public static void main(String[] args) {
        Properties config = new Properties();
        
        try (InputStream is = App.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is == null) {
                System.out.println("Error: No se encontro el archivo config.properties");
                return;
            }
            config.load(is);
        } catch (Exception e) {
            System.err.println("Error al cargar la configuracion: " + e.getMessage());
            return;
        }

        String url = config.getProperty("db.url");
        String user = config.getProperty("db.user");
        String pass = config.getProperty("db.password");
        String query = config.getProperty("db.query");
        int samples = Integer.parseInt(config.getProperty("simulation.samples"));
        int retries = Integer.parseInt(config.getProperty("simulation.maxRetries"));
        
        int minIdle = Integer.parseInt(config.getProperty("db.pool.minIdle"));
        int poolSize = Integer.parseInt(config.getProperty("db.pool.maxSize"));

        Thread freno = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            System.out.println("\n[!] INFO: PRESIONA 'q' Y ENTER EN ESTA CONSOLA PARA ACTIVAR EL FRENO MANUAL [!]");
            while (scanner.hasNextLine()) {
                if (scanner.nextLine().equalsIgnoreCase("q")) {
                    stopRequested = true;
                    System.out.println("\n>>> FRENO MANUAL ACTIVADO. CANCELANDO SIMULACION... <<<");
                    break;
                }
            }
        });
        freno.setDaemon(true); // Para que se cierre solo cuando el programa acabe
        freno.start();

        Engine engine = new Engine();

        // Simulación RAW 
        System.out.println("\n--- INICIANDO SIMULACION RAW ---");
        Metrics rawMetrics = engine.iniciar(false, url, user, pass, query, samples, retries, minIdle, poolSize);

        if (!stopRequested) {
            // Pausa entre simulaciones
            try { Thread.sleep(2000); } catch (InterruptedException e) {}

        //  Simulación POOLED 
            System.out.println("\n--- INICIANDO SIMULACION POOLED ---");
            Metrics pooledMetrics = engine.iniciar(true, url, user, pass, query, samples, retries, minIdle, poolSize);
            
            System.out.println("\n=======================================================");
            System.out.println("             ANALISIS COMPARATIVO FINAL                ");
            System.out.println("=======================================================");
            if (rawMetrics.tiempoTotal > pooledMetrics.tiempoTotal) {
                System.out.println("🏆 MEJOR RENDIMIENTO: POOLED");
                System.out.println("El metodo POOLED fue " + (rawMetrics.tiempoTotal - pooledMetrics.tiempoTotal) + " ms mas rapido.");
                System.out.println("Motivo: El Pool (HikariCP) reciclo conexiones activas dinamicamente (" + minIdle + " a " + poolSize + "),");
            } else {
                System.out.println("🏆 MEJOR DESEMPEÑO: RAW");
                System.out.println("El método RAW fue " + (pooledMetrics.tiempoTotal - rawMetrics.tiempoTotal) + " ms más rapido.");
            }
            System.out.println("=======================================================\n");
        }
        
        System.out.println("Proceso finalizado. Verifique el archivo 'simulacion.log'");
        System.exit(0);
    }
}