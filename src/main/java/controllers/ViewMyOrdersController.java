package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import ClientWorker.Connect;
import ClientWorker.Session;
import Enums.RequestType;
import Enums.ResponseStatus;
import check.Dialog;
import TCP.Request;
import TCP.Response;
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
import salonOrg.OrderItems;

public class ViewMyOrdersController {

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

    private ObservableList<Order> myOrdersList;

    @FXML
    void goToCartCustomer(ActionEvent event) {

    }

    @FXML
    void initialize() {
        Request request = new Request();
        request.setRequestType(RequestType.VIEW_MY_ORDERS);
        request.setUserLogin(Session.getUserLogin());
        Connect.client.sendObject(request);

        Response response = (Response) Connect.client.readObject();


        if(response.getResponseStatus()== ResponseStatus.OK){

            String products = response.getResponseData();

            Order[] myOrdersArray = new Gson().fromJson(products, Order[].class);
            myOrdersList = FXCollections.observableArrayList(Arrays.asList(myOrdersArray));

            orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
            orderDateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
            paymentCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
            totalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
            statusCol.setCellValueFactory(new PropertyValueFactory<>("orderStateName"));

            ordersTable.setItems(myOrdersList);

            ordersTable.setRowFactory (tv ->{
                TableRow<Order> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if(event.getClickCount()==2 && (!row.isEmpty())){
                        Order selectedOrder = row.getItem();
                        showOrderDetailAlert(selectedOrder);
                    }
                });
                return row;
            });


        }

    }

    @FXML
    void goToMainPageCustomer(ActionEvent event) {
        backButton.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/customerPage.fxml"));


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

    private void showOrderDetailAlert(Order order) {
        Request request = new Request();
        request.setRequestType(RequestType.VIEW_ORDER_DETAILS);
        request.setUserLogin(Session.getUserLogin());
        request.setRequestMessage(new Gson().toJson(order));
        Connect.client.sendObject(request);

        Response response = (Response) Connect.client.readObject();

        if(response.getResponseStatus()== ResponseStatus.OK){
            String responseData = response.getResponseData();
            OrderItems[]  orderItemsArray = new Gson().fromJson(responseData, OrderItems[].class);

            Double totalAmount;
            StringBuilder message = new StringBuilder();
            for(OrderItems item : orderItemsArray){
                totalAmount = item.getQuantity() * item.getProduct().getSellPrice();

                message.append(" - ").append(item.getProduct().getProductName())
                        .append(" ").append(item.getProduct().getSellPrice())
                        .append(" x ").append(item.getQuantity())
                        .append(" = ").append(totalAmount).append(" BYN\n");
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Подробности заказа");
            alert.setHeaderText("Информация о заказе № " + order.getOrderId()+ " от " + order.getOrderDate());
            alert.setContentText(message.toString());
            alert.showAndWait();
        } else{
            Dialog.showAlert("Ошибка", response.getResponseMessage());
        }
    }

}

