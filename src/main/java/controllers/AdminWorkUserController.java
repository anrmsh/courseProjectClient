package controllers;

import java.io.IOException;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import ClientWorker.Connect;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import salonOrg.User;
import TCP.Request;
import TCP.Response;
import check.Dialog;
import Enums.RequestType;
import Enums.ResponseStatus;
import com.google.gson.Gson;

public class AdminWorkUserController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableColumn<User, Integer> accessCol;

    @FXML
    private Button backButton;

    @FXML
    private TableColumn<User, String> emailCol;

    @FXML
    private TableColumn<User, String> firstNameCol;

    @FXML
    private TableColumn<User, String> lastNameCol;

    @FXML
    private TableColumn<User, String> loginCol;

    @FXML
    private TableColumn<User, String> passwordCol;

    @FXML
    private TableColumn<User, String> roleCol;

    @FXML
    private TableView<User> tableusers;

    @FXML
    private Button workUserBut;

    @FXML
    private Button saveChangeBut;
    private List<User> modifiedUsers = new ArrayList<>();

    @FXML
    private Label labelMessage;

    @FXML
    private ComboBox<String> filterBox;

    private ObservableList<User> userList;

    @FXML
    void exitApplication(ActionEvent event) {

    }

    @FXML
    void goToAuthorization(ActionEvent event) {

    }


    private void filterUsers() {
        String selectedFilter = filterBox.getSelectionModel().getSelectedItem();
        ObservableList<User> filteredList = FXCollections.observableArrayList();

        // Фильтруем пользователей в зависимости от выбранного фильтра
        for (User user : userList) {
            if ("Все".equals(selectedFilter)) {
                filteredList.add(user);  // Отображаем всех пользователей
            } else if ("Сотрудники".equals(selectedFilter) && ("Администратор".equals(user.getRoleName())
                    ||"Бухгалтер".equals(user.getRoleName())
                    ||"Менеджер".equals(user.getRoleName()) )) {
                filteredList.add(user);  // Отображаем только сотрудников
            } else if ("Покупатели".equals(selectedFilter) && "Покупатель".equals(user.getRoleName())) {
                filteredList.add(user);  // Отображаем только покупателей
            }
        }

        tableusers.setItems(filteredList);  // Обновляем таблицу
    }


    @FXML
    void initialize() throws IOException {
        labelMessage.setVisible(false);
        filterBox.setItems(FXCollections.observableArrayList("Все", "Сотрудники", "Покупатели"));
        filterBox.getSelectionModel().select(0);
        filterBox.setOnAction(event -> filterUsers());

        Request request = new Request();
        request.setRequestType(RequestType.GET_ALL_USERS);
        Connect.client.sendObject(request);

        Response responseModel = (Response) Connect.client.readObject();

        if (responseModel.getResponseStatus() == ResponseStatus.OK) {
            labelMessage.setVisible(false);
            String jsonUsers = responseModel.getResponseData();

            User[] usersArray = new Gson().fromJson(jsonUsers, User[].class);

            userList = FXCollections.observableArrayList(Arrays.asList(usersArray));
            loginCol.setCellValueFactory(new PropertyValueFactory<>("login"));
            passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
            roleCol.setCellValueFactory(new PropertyValueFactory<>("roleName"));
            accessCol.setCellValueFactory(new PropertyValueFactory<>("access"));
            firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
            //accessCol.setCellValueFactory(new PropertyValueFactory<>("access"));

            accessCol.setCellFactory(col -> {
                TableCell<User, Integer> cell = new TableCell<User, Integer>() {
                    private ComboBox<String> comboBox;

                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);

                        // Если ячейка пуста, скрываем ComboBox
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            if (comboBox == null) {
                                // Создаем ComboBox один раз для каждой ячейки
                                comboBox = new ComboBox<>();
                                comboBox.setItems(FXCollections.observableArrayList("Доступ есть", "Доступа нет"));
                                comboBox.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

                                // Обработчик изменения значения в ComboBox
                                comboBox.setOnAction(event -> {
                                    User user = getTableView().getItems().get(getIndex());
                                    // Обновляем поле доступа в объекте User
                                    user.setAccess(comboBox.getSelectionModel().getSelectedItem().equals("Доступ есть") ? 1 : 0);
                                    modifiedUsers.add(user);
                                });
                            }

                            // Устанавливаем текущее значение в ComboBox
                            comboBox.getSelectionModel().select(item == 1 ? "Доступ есть" : "Доступа нет");

                            // Отображаем ComboBox в ячейке
                            setGraphic(comboBox);
                        }
                    }
                };
                return cell;
            });

            tableusers.setItems(userList);
        } else {
            labelMessage.setVisible(true);
        }

    }

    @FXML
    void deleteUser() throws ClassNotFoundException {
        User selectedUser = tableusers.getSelectionModel().getSelectedItem();
        boolean confirmationResult = Dialog.showConfirmationDialog("Подтверждение", "Отмена", "Вы действительно хотите удалить эту запись?");
        if (confirmationResult) {
            if (selectedUser != null) {

                Request request = new Request();
                request.setRequestType(RequestType.DELETE_USER);
                request.setRequestMessage(new Gson().toJson(selectedUser));
                Connect.client.sendObject(request);

                Response responseModel = (Response) Connect.client.readObject();
                User obj = new Gson().fromJson(responseModel.getResponseData(), User.class);

                tableusers.getItems().remove(selectedUser);
                Dialog.showAlertInfo("Пользователь с id " + obj.getLogin() + " удалён");

            } else {
                Dialog.showAlertInfo("Пожалуйста, выберите пользователя для удаления.");
            }
        }
    }


    @FXML
    public void saveUserAccessChanged(ActionEvent event) {

if(!modifiedUsers.isEmpty()) {
    Request request = new Request();
    request.setRequestType(RequestType.UPDATE_USER_ACCESS);
    request.setRequestMessage(new Gson().toJson(modifiedUsers));
    Connect.client.sendObject(request);

    Response response = (Response) Connect.client.readObject();
    if (response.getResponseStatus() == ResponseStatus.OK) {
        labelMessage.setText("Данные успешно обновлены.");
        labelMessage.setVisible(true);
    } else{
        labelMessage.setText("Ошибка при обновлении данных.");
        labelMessage.setVisible(true);
    }
}

    }

}
