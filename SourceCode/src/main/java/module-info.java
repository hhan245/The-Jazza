module com.example.myshop {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.prefs;

    opens com.example.myshop to javafx.fxml;
    exports com.example.myshop;
    exports com.example.myshop.controllers;
    opens com.example.myshop.controllers to javafx.fxml;
}