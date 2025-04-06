
module com.example.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires javafx.base;
    requires java.desktop;


    exports main;
    opens main to javafx.fxml;
    exports controllers;
    opens controllers to javafx.fxml;
    opens salonOrg to com.google.gson, javafx.base;
}