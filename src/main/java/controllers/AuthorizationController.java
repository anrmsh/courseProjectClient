package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import ClientWorker.Connect;
import ClientWorker.Session;
import Enums.RequestType;
import TCP.Request;
import TCP.Response;
import animations.Shake;
import check.Dialog;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import salonOrg.Authorization;
import salonOrg.Role;
import salonOrg.User;
import animations.Shake;

public class AuthorizationController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button authSignInButton;

    @FXML
    private Button backButton;

    @FXML
    private TextField loginField;

    @FXML
    private Button loginSignUpButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    void initialize() {

    }

    @FXML
    void goToRegistration(ActionEvent event) {
        loginSignUpButton.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/registration.fxml"));

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Parent root = loader.getRoot();
        Stage stage=new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }


    @FXML
    void backToStartPage(ActionEvent event){
        backButton.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/startPage.fxml"));

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
        Session.clear();

    }

    @FXML
    void authorization(ActionEvent event) throws IOException {

        User user = new User();
        String inputLogOrEmail = loginField.getText().trim();
        String password = passwordField.getText().trim();
        if (inputLogOrEmail.contains("@")) {
            user.setEmail(inputLogOrEmail);
        } else {
            user.setLogin(inputLogOrEmail);
        }

        user.setPassword(password);

        // Отправляем запрос на сервер
        Request request = new Request();
        request.setRequestType(RequestType.AUTHORIZATION);
        request.setRequestMessage(new Gson().toJson(user));
        Connect.client.sendObject(request);


        try {

            String mes = Connect.client.readMessage();
            if ("Access Denied".equals(mes)) {
                Dialog.showAlertErrorAuth1("Доступ закрыт!");
            } else if ("Incorrect Data".equals(mes)) {
                Dialog.showAlertErrorAuth1("Неверные данные!");


            } else {
                // Переход на соответствующее окно
                authSignInButton.getScene().getWindow().hide();
                FXMLLoader loader = new FXMLLoader();
                Session.setCurrentUser(user);

                switch (mes) {
                    case "Администратор" -> loader.setLocation(getClass().getResource("/adminMakeDiscount.fxml")); //adminPage
                    case "Менеджер" -> loader.setLocation(getClass().getResource("/managerPage.fxml"));
                    case "Бухгалтер" -> loader.setLocation(getClass().getResource("/accounterReport.fxml"));
                    case "Покупатель" -> loader.setLocation(getClass().getResource("/customerPage.fxml"));
                    default -> loader.setLocation(getClass().getResource("/successRegistration.fxml"));
                }

                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Parent root = loader.getRoot();
                //Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
