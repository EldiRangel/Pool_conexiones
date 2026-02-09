module com.mycompany.proyecto_visual {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.mycompany.proyecto_visual to javafx.fxml;
    exports com.mycompany.proyecto_visual;
}
