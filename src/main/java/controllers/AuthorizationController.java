package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import ClientWorker.Connect;
import animations.Shake;
import check.Dialog;
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
//
//        authSignInButton.setOnAction(event -> {
////            String loginText = loginField.getText().trim();
////            String loginPasswordText = passwordField.getText().trim();
////            if (loginText.equals("") || loginPasswordText.equals("")) {
////                loginUser(loginText,loginPasswordText);
////            }
////            else{
////                System.out.println("login and password is empty Error");
////            }
//
//            User user = new User();
//            String inputLogOrEmail = loginField.getText().trim();
//            String password = passwordField.getText().trim();
//
//            if(inputLogOrEmail.equals("") || password.equals("")){
//                Dialog.showAlertNoDataInAuthForm();
//            } else {
//                user.setPassword(password);
//
//                String authType;
//                if(inputLogOrEmail.contains("@")){
//                    user.setEmail(inputLogOrEmail);
//                    authType = "email";
//                } else {
//                    user.setLogin(inputLogOrEmail);
//                    authType = "login";
//                }
//
//                Connect.client.sendMessage("autorizationUser");
//                Connect.client.sendObject(user);
//
//
//
//                try {
//                    String response = Connect.client.readMessage();
//                    if ("Success".equals(response)) {
//                        System.out.println("Вход выполнен");
//                        FXMLLoader loader = new FXMLLoader();
//                        loader.setLocation(getClass().getResource("/successRegistration.fxml"));
//                        Parent root = loader.getRoot();
//                        Stage stage = new Stage();
//                        stage.setScene(new Scene((root)));
//                        stage.show();
//                    } else {
//                        Dialog.showAlertErrorAuth();
//                        System.out.println("Неверные данные");
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//
//
//           }
//
//
//
//
//        });


//loginSignUpButton.setOnAction(event -> {
//    loginSignUpButton.getScene().getWindow().hide();
//
//    FXMLLoader loader = new FXMLLoader();
//    loader.setLocation(getClass().getResource("/registration.fxml"));
//
//    try {
//        loader.load();
//    } catch (IOException e) {
//        throw new RuntimeException(e);
//    }
//
//    Parent root = loader.getRoot();
//    Stage stage=new Stage();
//    stage.setScene(new Scene(root));
//    stage.show();
//
//
//});

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
        Connect.client.sendMessage("autorizationUser");
        Connect.client.sendObject(user);

        try {
            String response = Connect.client.readMessage();

            if ("Access Denied".equals(response)) {
                Dialog.showAlertErrorAuth1("Доступ закрыт!");
            } else if ("Incorrect Data".equals(response)) {
                //Dialog.showAlertErrorAuth1("Неверные данные!");
                Shake userLoginAnim = new Shake(loginField);
                Shake userPasswordAnim = new Shake(passwordField);
                userLoginAnim.playAnimation();
                userPasswordAnim.playAnimation();

            } else {
                // Переход на соответствующее окно
                authSignInButton.getScene().getWindow().hide();
                FXMLLoader loader = new FXMLLoader();

                switch (response) {
                    case "Администратор" -> loader.setLocation(getClass().getResource("/adminPage.fxml"));
                    case "Менеджер" -> loader.setLocation(getClass().getResource("/successRegistration.fxml"));
                    case "Бухгалтер" -> loader.setLocation(getClass().getResource("/successRegistration.fxml"));
                    case "Покупатель" -> loader.setLocation(getClass().getResource("/successRegistration.fxml"));
                    default -> loader.setLocation(getClass().getResource("/successRegistration.fxml"));
                }

                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
