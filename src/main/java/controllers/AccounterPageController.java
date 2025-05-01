package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import check.Dialog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AccounterPageController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button exitButton;

    @FXML
    private Button mainButt;

    @FXML
    private Button planBut;

    @FXML
    private Button reportBut;



    @FXML
    void initialize() {

    }

    @FXML
    public void goToExit(ActionEvent event) {
        Dialog.showConfirmationDialog("Выход","Выход", "Вы действительно хотите выйти?");
        exitButton.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/startPage.fxml"));


        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root,700,600));
        stage.setTitle("Shop");
        stage.show();

    }

    @FXML
    public void goToAccounterReportPage(ActionEvent event) {
        reportBut.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/accounterReport.fxml"));


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
    public void goToFinPlanPage(ActionEvent event) {
        planBut.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/accounterPlan.fxml"));


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




}

