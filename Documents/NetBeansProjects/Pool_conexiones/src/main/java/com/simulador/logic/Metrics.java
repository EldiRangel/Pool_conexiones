package com.simulador.logic;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class Metrics {
    public AtomicInteger exitosas = new AtomicInteger(0);
    public AtomicInteger fallidas = new AtomicInteger(0);
    public AtomicInteger totalRetries = new AtomicInteger(0);
    public long tiempoTotal;
    
    // Ruta hacia la carpeta de lógica y formato de fecha
    private final String rutaLog = "src/main/java/com/simulador/logic/simulacion.log";
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Método para escribir cada intento en el archivo
    public synchronized void guardarLog(String idTarea, String query, String estado) {
        try (PrintWriter out = new PrintWriter(new FileWriter(rutaLog, true))) {
            String fecha = dtf.format(LocalDateTime.now());
            out.printf("[%s] Tarea: %s | Query: %s | Estado: %s%n", fecha, idTarea, query, estado);
            out.flush();
        } catch (IOException e) {
            // Plan B: si la ruta de paquetes no existe, lo crea en la raiz
            try (PrintWriter out = new PrintWriter(new FileWriter("simulacion.log", true))) {
                out.println("Guardado en raiz por error de ruta: " + estado);
            } catch (IOException ex) {}
        }
    }

    public void imprimirReporte(String tipo, int total) {
        System.out.println("\n REPORTE FINAL: " + tipo + " ");
        System.out.println("Tiempo total: " + tiempoTotal + " ms");
        System.out.println("Muestras: " + total);
        
        double pctExito = (total > 0) ? (exitosas.get() * 100.0 / total) : 0;
        double pctFallo = (total > 0) ? (fallidas.get() * 100.0 / total) : 0;
        double promRetries = (total > 0) ? ((double) totalRetries.get() / total) : 0;

        System.out.printf("Exitosas: %d (%.2f%%)\n", exitosas.get(), pctExito);
        System.out.printf("Fallidas: %d (%.2f%%)\n", fallidas.get(), pctFallo);
        System.out.printf("Promedio de reintentos: %.2f\n", promRetries);
        
        // Guarda el resumen del reporte en el log
        guardarLog("RESUMEN", tipo, "Exitos: " + exitosas.get() + " | Fallos: " + fallidas.get());
    }
}