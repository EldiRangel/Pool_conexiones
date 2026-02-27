package com.simulador.logic;

import com.zaxxer.hikari.*;
import java.util.concurrent.*;

public class Engine {
    private ExecutorService executor;

    public Metrics iniciar(boolean pooled, String url, String user, String pass, String query, int samples, int retries, int minIdle, int poolSize) {
        Metrics m = new Metrics();
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(samples);
        executor = Executors.newFixedThreadPool(samples);

        HikariDataSource ds = null;
        if (pooled) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url); 
            config.setUsername(user); 
            config.setPassword(pass);
            
            config.setMinimumIdle(minIdle);
            config.setMaximumPoolSize(poolSize);
            ds = new HikariDataSource(config);
        }

        for (int i = 0; i < samples; i++) {
            executor.submit(new SimulationTask(i, url, user, pass, query, retries, start, done, m, ds));
        }

        long inicio = System.currentTimeMillis();
        start.countDown();  

        try {
            if (!done.await(60, TimeUnit.SECONDS)) {
                System.out.println("[!] La simulacion tardo demasiado. Aplicando freno por timeout.");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        m.tiempoTotal = System.currentTimeMillis() - inicio;
        m.imprimirReporte(pooled ? "POOLED" : "RAW", samples);
        
        if (ds != null) ds.close();
        executor.shutdown();
        
        return m; 
    }
}