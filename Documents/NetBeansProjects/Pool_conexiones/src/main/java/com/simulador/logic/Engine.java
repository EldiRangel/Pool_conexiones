package com.simulador.logic;

import java.util.concurrent.*;

public class Engine {
    private ExecutorService executor;

    public Metrics iniciar(boolean pooled, String url, String user, String pass, String[] queries, 
                           int samples, int retries, int minIdle, int poolSize) {
        Metrics m = new Metrics();
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(samples);
        
        executor = Executors.newFixedThreadPool(samples); 

        CustomPool myPool = null;
        try {
            if (pooled) {
                myPool = new CustomPool(url, user, pass, minIdle, poolSize);
            }

            for (int i = 0; i < samples; i++) {
                executor.submit(new SimulationTask(i, url, user, pass, queries, retries, start, done, m, myPool));
            }

            long inicio = System.currentTimeMillis();
            
            start.countDown();  

            done.await(10, TimeUnit.MINUTES);
            m.tiempoTotal = System.currentTimeMillis() - inicio;

        } catch (Exception e) {
            System.err.println("Error en motor: " + e.getMessage());
        } finally {
            if (myPool != null) myPool.closeAll();
            executor.shutdownNow();
        }
        
        return m; 
    }
}