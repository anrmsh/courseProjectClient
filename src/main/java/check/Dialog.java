package check;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class Dialog {
    public static  void showAlertWithExistLoginUser(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка: Регистрации");
        alert.setContentText("Такой логин уже существует! \nПридумайте пожалуйста другой");
        alert.showAndWait();

    }

    public static void passwordsNotMatchRegistrAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка: Регистрация");
        alert.setContentText("Пароли не совпадают");
        alert.showAndWait();
    }

    public static void roleNotSelectedRegistrAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка: Регистрация");
        alert.setContentText("Выберите пожалуйста роль");
        alert.showAndWait();
    }

    public static void showAlertNoDataInAuthForm(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка: Входа");
        alert.setContentText("Заполните все поля!");
        alert.showAndWait();
    }

    public static void showAlertErrorAuth(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка: Входа");
        alert.setContentText("Неверный логин или пароль!");
        alert.showAndWait();
    }

    public static void showAlertErrorAuth1(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка: Входа");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showAlertInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText("Информационное сообщение");
        alert.setContentText(message);
        alert.showAndWait();
    }


    public static boolean showConfirmationDialog(String title, String header, String content) {//пробное подтверждение выхода
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);


        ButtonType yesButton = new ButtonType("Да");
        ButtonType noButton = new ButtonType("Нет");
        alert.getButtonTypes().setAll(yesButton, noButton);


        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == yesButton;
    }


}
