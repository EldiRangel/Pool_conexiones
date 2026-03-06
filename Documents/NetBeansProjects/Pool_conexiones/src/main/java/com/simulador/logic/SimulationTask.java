package com.simulador.logic;

import java.sql.*;
import java.util.concurrent.CountDownLatch;

public class SimulationTask implements Runnable {
    private String id, url, user, pass;
    private String[] queries; 
    private int maxRetries;
    private CountDownLatch startLatch, doneLatch;
    private Metrics metrics;
    private CustomPool customPool;

    public SimulationTask(int i, String url, String user, String pass, String[] queries, int retries, 
                          CountDownLatch start, CountDownLatch done, Metrics m, CustomPool pool) {
        this.id = "ID-" + i;
        this.url = url; this.user = user; this.pass = pass;
        this.queries = queries; this.maxRetries = retries;
        this.startLatch = start; this.doneLatch = done;
        this.metrics = m; this.customPool = pool;
    }

    @Override
    public void run() {
        try {
            startLatch.await();
            int intento = 0;
            boolean exito = false;

            while (intento <= maxRetries && !exito && !com.visual.pool_conexiones.App.stopRequested) {
                intento++;
                Connection conn = null;
                try {
                    if (customPool != null) {
                        conn = customPool.getConnection();
                    } else {
                        conn = DriverManager.getConnection(url, user, pass);
                    }

                    try (Statement stmt = conn.createStatement()) {
                        
                        for (String q : queries) {
                            stmt.execute(q);
                        }
                        exito = true;
                    }
                } catch (Exception e) {
                    // Fallo el intento
                } finally {
                    if (customPool != null && conn != null) {
                        customPool.releaseConnection(conn); 
                    } else if (conn != null) {
                        conn.close(); 
                    }
                }
            }

            if (exito && !com.visual.pool_conexiones.App.stopRequested) {
                metrics.exitosas.incrementAndGet();
            } else {
                metrics.fallidas.incrementAndGet();
            }
            
            metrics.totalRetries.addAndGet(Math.max(0, intento - 1));

            String estado = (exito && !com.visual.pool_conexiones.App.stopRequested) ? "EXITOSA" : "FALLIDA";
            if (com.visual.pool_conexiones.App.stopRequested) estado = "DETENIDA";
            
            metrics.guardarLog(id, queries.length > 1 ? "MULTI-QUERY" : "SINGLE-QUERY", estado + " (Intento: " + intento + ")");

        } catch (Exception e) {
        } finally {
            doneLatch.countDown();
        }
    }
}