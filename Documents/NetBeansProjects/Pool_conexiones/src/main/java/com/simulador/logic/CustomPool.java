package com.simulador.logic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CustomPool {
    private final BlockingQueue<Connection> pool;
    private final String url, user, pass;

    public CustomPool(String url, String user, String pass, int minIdle, int maxSize) throws SQLException {
        this.url = url;
        this.user = user;
        this.pass = pass;
        this.pool = new LinkedBlockingQueue<>(maxSize);
        
        for (int i = 0; i < maxSize; i++) {
            pool.add(crearNuevaConexion());
        }
    }

    private Connection crearNuevaConexion() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }

    public Connection getConnection() throws InterruptedException {
        Connection conn = pool.take(); 
        
        try {
            if (conn.isClosed()) {
                conn = crearNuevaConexion();
            }
        } catch (SQLException e) {
            try {
                conn = crearNuevaConexion(); 
            } catch (SQLException ex) {}
        }
        
        return conn;
    }

    public void releaseConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    pool.offer(conn); 
                } else {
                    pool.offer(crearNuevaConexion()); 
                }
            } catch (SQLException e) {
                try { pool.offer(crearNuevaConexion()); } catch (SQLException ex) {}
            }
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