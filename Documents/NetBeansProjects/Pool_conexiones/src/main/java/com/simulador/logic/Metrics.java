package com.simulador.logic;

import java.util.concurrent.atomic.AtomicInteger;

public class Metrics {
    public AtomicInteger exitosas = new AtomicInteger(0);
    public AtomicInteger fallidas = new AtomicInteger(0);
    public AtomicInteger totalRetries = new AtomicInteger(0);
    public long tiempoTotal;

    public void imprimirReporte(String tipo, int total) {
        System.out.println("\n--- REPORTE FINAL: " + tipo + " ---");
        System.out.println("Tiempo total: " + tiempoTotal + " ms");
        System.out.println("Muestras: " + total);
        
        double pctExito = (total > 0) ? (exitosas.get() * 100.0 / total) : 0;
        double pctFallo = (total > 0) ? (fallidas.get() * 100.0 / total) : 0;
        double promRetries = (total > 0) ? ((double) totalRetries.get() / total) : 0;

        System.out.printf("Exitosas: %d (%.2f%%)\n", exitosas.get(), pctExito);
        System.out.printf("Fallidas: %d (%.2f%%)\n", fallidas.get(), pctFallo);
        System.out.printf("Promedio de reintentos: %.2f\n", promRetries);
    }
}