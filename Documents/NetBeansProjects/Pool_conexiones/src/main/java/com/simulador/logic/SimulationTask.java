package com.simulador.logic;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.io.FileWriter;
import java.io.PrintWriter;

public class SimulationTask implements Runnable {
    private String id;
    private String url, user, pass, query;
    private int maxRetries;
    private CountDownLatch startLatch, doneLatch;
    private Metrics metrics;
    private com.zaxxer.hikari.HikariDataSource pool;

    public SimulationTask(int i, String url, String user, String pass, String query, int retries, 
                          CountDownLatch start, CountDownLatch done, Metrics m, 
                          com.zaxxer.hikari.HikariDataSource pool) {
        this.id = "ID-" + i;
        this.url = url; this.user = user; this.pass = pass;
        this.query = query; this.maxRetries = retries;
        this.startLatch = start; this.doneLatch = done;
        this.metrics = m; this.pool = pool;
    }

    @Override
    public void run() {
        try {
            startLatch.await();
            int intento = 0;
            boolean exito = false;

            while (intento <= maxRetries && !exito) {
                intento++;
                try (Connection conn = (pool != null) ? pool.getConnection() : DriverManager.getConnection(url, user, pass);
                     Statement stmt = conn.createStatement()) {
                    stmt.execute(query);
                    exito = true;
                } catch (Exception e) { 
                    // Intento fallido
                }
            }

            if (exito) metrics.exitosas.incrementAndGet(); else metrics.fallidas.incrementAndGet();
            metrics.totalRetries.addAndGet(intento - 1);

            // Registro en archivo Log 
            synchronized (SimulationTask.class) {
                try (PrintWriter out = new PrintWriter(new FileWriter("simulacion.log", true))) {
                    out.printf("[%s] %s - %s - Intento: %d\n", LocalDateTime.now(), id, (exito ? "EXITOSA" : "FALLIDA"), intento);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            doneLatch.countDown();
        }
    }
}