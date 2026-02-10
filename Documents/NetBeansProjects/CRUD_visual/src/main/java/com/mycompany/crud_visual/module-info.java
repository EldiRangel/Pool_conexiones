module com.mycompany.crud_visual {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    opens com.mycompany.crud_visual to javafx.fxml;
    exports com.mycompany.crud_visual;
}