module com.mycompany.universalmarketcrud {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.mycompany.universalmarketcrud to javafx.fxml;
    exports com.mycompany.universalmarketcrud;
}