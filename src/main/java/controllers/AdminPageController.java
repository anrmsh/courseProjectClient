package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminPageController {

    @FXML
    private Button StatisticsBut;

    @FXML
    private Button catalogButt;

    @FXML
    private Button exitButton;

    @FXML
    private Button mainButt;

    @FXML
    private Button workUserBut;

    @FXML
    void exitApplication(ActionEvent event) {

    }

    @FXML
    void goToAuthorization(ActionEvent event) {

    }


    @FXML
    void backToAuth(ActionEvent event){
        exitButton.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/authorization2.fxml"));

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

    @FXML
    void goToWorkUserPage(ActionEvent event){
        workUserBut.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/adminWorkUserPage.fxml"));

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
