module com.mycompany.supermercado_crud {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.mycompany.supermercado_crud to javafx.fxml;
    exports com.mycompany.supermercado_crud;
}