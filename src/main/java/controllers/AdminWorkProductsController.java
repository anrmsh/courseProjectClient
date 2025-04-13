package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AdminWorkProductsController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addBut;

    @FXML
    private Button backButton;

    @FXML
    private Button costBut;

    @FXML
    private Button editBut;

    @FXML
    private Label labelMessage;

    @FXML
    private Button viewCatalog;

    @FXML
    private Button workUserBut;

    @FXML
    void exitApplication(ActionEvent event) {

    }

    @FXML
    void goToAuthorization(ActionEvent event) {

    }

    @FXML
    void initialize() {

    }


    @FXML
    public void goToViewCatalog(ActionEvent event) {
        viewCatalog.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/viewCatalog.fxml"));


        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root,830,600));
        stage.setTitle("Shop");
        stage.show();

    }

    @FXML
    void goToAddProductPage(ActionEvent event) {
        addBut.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/adminAddNewProduct.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root,830,600));
        stage.setTitle("Shop");
        stage.show();

    }

    @FXML
    void goToMainPageAdmin(ActionEvent event){
        backButton.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/adminPage.fxml"));

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }

}

