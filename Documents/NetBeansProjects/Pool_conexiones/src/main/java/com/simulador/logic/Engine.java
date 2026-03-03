package com.simulador.logic;

import java.util.concurrent.*;

public class Engine {
    private ExecutorService executor;

    public Metrics iniciar(boolean pooled, String url, String user, String pass, String query, 
                           int samples, int retries, int poolSize) {
        Metrics m = new Metrics();
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(samples);
        executor = Executors.newFixedThreadPool(50); 

        CustomPool myPool = null;
        try {
            if (pooled) {
                String urlLimpia = url.contains("?") ? url : url + "?sslmode=disable";
                myPool = new CustomPool(urlLimpia, user, pass, poolSize);
            }

            for (int i = 0; i < samples; i++) {
                executor.submit(new SimulationTask(i, url, user, pass, query, retries, start, done, m, myPool));
            }

            long inicio = System.currentTimeMillis();
            start.countDown();  

            done.await(30, TimeUnit.SECONDS);
            m.tiempoTotal = System.currentTimeMillis() - inicio;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (myPool != null) myPool.closeAll();
            executor.shutdownNow();
        }
        
        return m; 
    }
}