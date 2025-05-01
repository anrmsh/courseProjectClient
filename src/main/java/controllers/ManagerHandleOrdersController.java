package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import ClientWorker.Connect;
import ClientWorker.Session;
import Enums.RequestType;
import Enums.ResponseStatus;
import TCP.Request;
import TCP.Response;
import check.Dialog;
import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import salonOrg.Order;
import salonOrg.OrderState;
import salonOrg.User;

public class ManagerHandleOrdersController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backButton;

    @FXML
    private TableColumn<Order, String> orderDateCol;

    @FXML
    private TableColumn<Order, Integer> orderIdCol;

    @FXML
    private TableView<Order> ordersTable;

    @FXML
    private TableColumn<Order, String> paymentCol;

    @FXML
    private TableColumn<Order, String> statusCol;

    @FXML
    private TableColumn<Order, Double> totalCol;

    @FXML
    private Button workUserBut;

    private ObservableList<Order> allOrdersList;
    private List<Order> modifiedOrders = new ArrayList<>();

    @FXML
    void goToCartCustomer(ActionEvent event) {

    }

    @FXML
    void initialize() {
        Request request = new Request();
        request.setRequestType(RequestType.MANAGER_VIEW_ORDERS);
        request.setUserLogin(Session.getUserLogin());
        Connect.client.sendObject(request);

        Response response = (Response) Connect.client.readObject();


        if(response.getResponseStatus()== ResponseStatus.OK){

            String products = response.getResponseData();

            Order[] myOrdersArray = new Gson().fromJson(products, Order[].class);
            allOrdersList = FXCollections.observableArrayList(Arrays.asList(myOrdersArray));

            orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
            orderDateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
            paymentCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
            totalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
            statusCol.setCellValueFactory(new PropertyValueFactory<>("orderStateName"));

            statusCol.setCellFactory(col -> {
                TableCell<Order, String> cell = new TableCell<Order, String>() {
                    private ComboBox<String> comboBox;


                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            if (comboBox == null) {
                                comboBox = new ComboBox<>();
                                comboBox.setItems(fetchOrderState());
                                comboBox.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

                                comboBox.setOnAction(event -> {
                                    Order order = getTableView().getItems().get(getIndex());
                                    OrderState newStatus = new OrderState();
                                    newStatus.setOrderStateName(comboBox.getSelectionModel().getSelectedItem());
                                    order.setOrderState(newStatus);
                                    modifiedOrders.add(order);
                                });
                            }


                            comboBox.getSelectionModel().select(item);

                            setGraphic(comboBox);
                        }
                    }
                };
                return cell;
            });



            ordersTable.setItems(allOrdersList);


        }

    }


    private ObservableList<String> fetchOrderState() {
        ObservableList<String> orderStatus = FXCollections.observableArrayList();
        try {
            Request request1 = new Request();
            request1.setRequestType(RequestType.GET_ALL_STATUS_ORDERS);
            Connect.client.sendObject(request1);

            Response response = (Response) Connect.client.readObject();

            if (response.getResponseStatus() == ResponseStatus.OK) {

                String jsonData = response.getResponseData();
                OrderState[] orderStates = new Gson().fromJson(jsonData, OrderState[].class);

                // Заполнение ComboBox
//                ObservableList<String> orderStatus = FXCollections.observableArrayList();
                for (OrderState status : orderStates) {
                    orderStatus.add(status.getOrderStateName()); // Обязательно изменить на соответствующее поле
                }

                return orderStatus;
            } else {
                // Обработка ошибки
                System.out.println("Ошибка: " + response.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderStatus;
    }



    @FXML
    void goToMainPageCustomer(ActionEvent event) {
        backButton.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/managerPage.fxml"));


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
    public void saveOrderStatusChanged(ActionEvent event) {

        if(!modifiedOrders.isEmpty()) {
            Request request = new Request();
            request.setRequestType(RequestType.UPDATE_ORDER_STATE);
            request.setRequestMessage(new Gson().toJson(modifiedOrders));
            Connect.client.sendObject(request);

            Response response = (Response) Connect.client.readObject();
            if (response.getResponseStatus() == ResponseStatus.OK) {
                Dialog.showAlertInfo("Данные успешно обновлены.");
            } else{
                Dialog.showAlertInfo("Ошибка при обновлении данных.");
            }
        }

    }


}


