package controllers;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
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
import javafx.stage.Stage;
import salonOrg.*;

public class MakeOrderController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backButton;

    @FXML
    private ListView<String> listToOrder;

    @FXML
    private Button makeOrderButt;

    @FXML
    private ComboBox<String> paymentMethodBox;

    @FXML
    private RadioButton radioButPickUp;

    @FXML
    private RadioButton radioButDelivery;

    @FXML
    private Label totalAmountLabel;

    @FXML
    private Button workUserBut;

    private double baseTotal = 0.0;

    @FXML
    void addToOrderPreview(ActionEvent event) {

    }

    @FXML
    void goToCartCustomer(ActionEvent event) {
        backButton.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/customerCart.fxml"));


        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root,916,608));
        stage.setTitle("Shop");
        stage.show();
    }

    @FXML
    void initialize() {

        listToOrder.getItems().clear();
        ToggleGroup deliveryGroup = new ToggleGroup();
        radioButPickUp.setToggleGroup(deliveryGroup);
        radioButDelivery.setToggleGroup(deliveryGroup);
        radioButPickUp.setSelected(true);

        deliveryGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {updateTotal();});
        fetchPaymentMethods();

        ObservableList<CartItems> previewOrderList = CustomerCartController.getPreviewOrderList();

        if (previewOrderList.isEmpty()) {
            Dialog.showAlertInfo("Список заказа пуст!");

        } else {

            baseTotal = 0;
            listToOrder.getItems().clear();

            for(CartItems item : previewOrderList) {
                double itemTotal = item.getTotalCost();
                baseTotal += itemTotal;
                String entry = item.getProduct().getProductName() + " x " + item.getQuantity() + " = " + itemTotal + "BYN";
                listToOrder.getItems().add(entry);
            }
            updateTotal();

        }

    }

    private void fetchPaymentMethods() {
        try {
            Request request1 = new Request();
            request1.setRequestType(RequestType.GET_ALL_PAYMENT_METHODS);
            Connect.client.sendObject(request1);

            Response response = (Response) Connect.client.readObject();

            if (response.getResponseStatus() == ResponseStatus.OK) {
                // Десериализация данных
                String jsonData = response.getResponseData();
                Payment[] payments = new Gson().fromJson(jsonData, Payment[].class);

                // Заполнение ComboBox
                ObservableList<String> paymentMethods = FXCollections.observableArrayList();
                for (Payment payment : payments) {
                    paymentMethods.add(payment.getPaymentMethod()); // Обязательно изменить на соответствующее поле
                }

                paymentMethodBox.setItems(paymentMethods);
            } else {
                // Обработка ошибки
                System.out.println("Ошибка: " + response.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTotal() {
        double total = baseTotal;
        if(radioButDelivery.isSelected()) {
            total+=100;
        }
        totalAmountLabel.setText("Итого к оплате: "+total + " BYN");
    }

    @FXML
    public void handleConfirmOrder(ActionEvent event) {
        ObservableList<CartItems> previewOrderList = CustomerCartController.getPreviewOrderList();


      String selectedPaymentMethod = paymentMethodBox.getSelectionModel().getSelectedItem();
      if(selectedPaymentMethod == null) {
          Dialog.showAlertInfo("Выберите способ оплаты!");
          return;
      }
      Payment payment = new Payment();
      payment.setPaymentMethod(selectedPaymentMethod);

      Order order = new Order();
      order.setUser(Session.getCurrentUser());
      order.setPayment(payment);

      double totalAmount = baseTotal;
      if(radioButDelivery.isSelected()) {
          totalAmount+=100;
      }
      OrderState orderState = new OrderState();
      orderState.setOrderStateId(1);

      order.setTotalAmount(totalAmount);
      order.setOrderState(orderState);

      List<OrderItems> orderItemsList = new ArrayList<>();
      for(CartItems item : previewOrderList) {
          OrderItems orderItem = new OrderItems();
          orderItem.setProduct(item.getProduct());
          orderItem.setQuantity(item.getQuantity());
          orderItemsList.add(orderItem);
      }
      order.setOrderItems(orderItemsList);

      try{
          Request request = new Request();
          request.setUserLogin(Session.getUserLogin());
          request.setRequestType(RequestType.PLACE_ORDER);
          request.setRequestMessage(new Gson().toJson(order));

          Connect.client.sendObject(request);

          Response response1 = (Response) Connect.client.readObject();
          int orderID = new Gson().fromJson(response1.getResponseData(), int.class);
          if (response1.getResponseStatus() == ResponseStatus.OK) {
              //Dialog.showAlertInfo("Заказ успешно оформлен!");

              generateOrderCheque(order,orderID);

              Alert alert = new Alert(Alert.AlertType.INFORMATION);
              alert.setTitle("Успех");
              alert.setHeaderText("Заказ оформлен!");
              alert.setContentText("Ваш заказ успешно оформлен!\n Чек отправлен на устройство.");
              Stage currentStage = (Stage) alert.getDialogPane().getScene().getWindow();

              alert.showAndWait().ifPresent(response -> {
                  if (response == ButtonType.OK) {
                      currentStage.close();
                      Stage curentStage = (Stage) listToOrder.getScene().getWindow();
                      curentStage.close();
                      FXMLLoader loader = new FXMLLoader();
                      loader.setLocation(getClass().getResource("/customerPage.fxml"));


                      try {
                          loader.load();
                      } catch (IOException e) {
                          throw new RuntimeException(e);
                      }

                      Parent root = loader.getRoot();
                      Stage stage = new Stage();
                      stage.setScene(new Scene(root,702,608));
                      stage.setTitle("Shop");
                      stage.show();

                  }
              });

          } else {
              Dialog.showAlertInfo("Ошибка оформления заказа");
          }
      } catch (Exception e) {
          e.printStackTrace();
      }





    }


    private void generateOrderCheque(Order order, int orderID) {
        String fileName = "order_" + orderID + ".txt";

        try(PrintWriter writer = new PrintWriter(new FileWriter(fileName))){
            writer.println("=== Чек заказа ===");
            writer.println("Номер заказа: " + orderID);
            writer.println("Дата заказа: " + LocalDate.now());
            writer.println("Способ оплаты: " + order.getPaymentMethod());
            if(radioButDelivery.isSelected()) {
                writer.println("Доставка: 100 BYN" );
            } else {
                writer.println("Доставка: Самовывоз" );
            }

            writer.println("-------------------------------");
            writer.println("        Товары:");

            for (OrderItems item : order.getOrderItems()) {
                String productName = item.getProduct().getProductName();
                int quantity = item.getQuantity();
                double price = item.getProduct().getSellPrice();
                double total = price * quantity;
                writer.printf("%s x %d = %.2f BYN\n", productName, quantity, total);
            }

            writer.println("-------------------------------");
            writer.printf("Итого к оплате: %.2f BYN\n", order.getTotalAmount());
            writer.println("\n\tСпасибо за покупку!");

            System.out.println("Чек успешно сохранён в файл: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

