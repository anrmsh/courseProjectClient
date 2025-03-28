module com.example.client {
    requires javafx.controls;
    requires javafx.fxml;



    exports main;
    opens main to javafx.fxml;
    exports controllers;
    opens controllers to javafx.fxml;
}