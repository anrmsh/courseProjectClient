package controllers;
import check.Dialog;
import check.Check;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import ClientWorker.Connect;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import salonOrg.User;
import salonOrg.Role;

public class RegistrationController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button SignUpButton;

    @FXML
    private Button backButton;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField loginField;

    @FXML
    private TextField mailField;

    @FXML
    private TextField nameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> positionSelector;

    @FXML
    private PasswordField rptPasswordField;

    public ResourceBundle getResources() {
        return resources;
    }

    public TextField getLastNameField() {
        return lastNameField;
    }

    public TextField getLoginField() {
        return loginField;
    }

    public TextField getMailField() {
        return mailField;
    }

    public TextField getNameField() {
        return nameField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public ComboBox<String> getPositionSelector() {
        return positionSelector;
    }

    public PasswordField getRptPasswordField() {
        return rptPasswordField;
    }


    @FXML
    void initialize() {
        positionSelector.setItems(FXCollections.observableArrayList("Администратор","Менеджер","Бухгалтер","Покупатель"));

    }

    @FXML
    void backToAuth(ActionEvent event){
        backButton.getScene().getWindow().hide();

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
    void goToSuccessRegistr(ActionEvent event){
        SignUpButton.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/successRegistration.fxml"));

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
    void registrationUser(ActionEvent event) throws IOException {
//write check for inputs

        Check check = new Check(this);
        if(!check.validateInput()){
            return;
        }

        User user = new User();
        user.setLogin(loginField.getText());
        user.setPassword(passwordField.getText());
        user.setFirstName(nameField.getText());
        user.setLastName(lastNameField.getText());
        user.setEmail(mailField.getText());
        user.setRoleName(positionSelector.getSelectionModel().getSelectedItem());
        String role = positionSelector.getSelectionModel().getSelectedItem();

        String password = passwordField.getText();
        String rptPassword = rptPasswordField.getText();

        if (role==null){
            Dialog.roleNotSelectedRegistrAlert();
            System.out.println("Роль не была выбрана.");
            return;
        }


        if(!password.equals(rptPassword)){
            Dialog.passwordsNotMatchRegistrAlert();
            return;
        }

        Connect.client.sendMessage("registrationUser");
        Connect.client.sendObject(user);

        System.out.println("Запись отправлена");

        String mes = "";
        try {
            mes = Connect.client.readMessage();
        } catch(IOException ex){
            System.out.println("Error in reading");
        }
        if(mes.equals("This user already exists!")){
            Dialog.showAlertWithExistLoginUser();
        } else{


            SignUpButton.getScene().getWindow().hide();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/successRegistration.fxml"));

            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene((root)));
            stage.show();
        }



    }


}

