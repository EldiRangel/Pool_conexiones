module com.visual.pool_conexiones {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;      // JDBC 
    requires com.zaxxer.hikari; 
    requires java.base;

    opens com.visual.pool_conexiones to javafx.fxml;
    exports com.visual.pool_conexiones;
    exports com.simulador.logic;
}