package com.simulador.logic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CustomPool {
    private final BlockingQueue<Connection> pool;
    private final String url, user, pass;

    public CustomPool(String url, String user, String pass, int size) throws SQLException {
        this.url = url;
        this.user = user;
        this.pass = pass;
        this.pool = new LinkedBlockingQueue<>(size);
        
        for (int i = 0; i < size; i++) {
            pool.add(crearNuevaConexion());
        }
    }

    private Connection crearNuevaConexion() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }

    public Connection getConnection() throws InterruptedException {
        return pool.take(); 
    }

    public void releaseConnection(Connection conn) {
        if (conn != null) {
            pool.offer(conn); 
        }
    }

    public void closeAll() {
        for (Connection conn : pool) {
            try {
                if (conn != null && !conn.isClosed()) conn.close();
            } catch (SQLException e) { }
        }
    }
}