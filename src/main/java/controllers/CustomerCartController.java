package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import ClientWorker.Connect;
import check.Dialog;
import ClientWorker.Session;
import Enums.RequestType;
import Enums.ResponseStatus;
import TCP.Request;
import TCP.Response;
import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import salonOrg.*;

public class CustomerCartController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addToOrderLst;

    @FXML
    private VBox allCategoriesBox;


    @FXML
    private Button backButton;

    @FXML
    private Button delFromCartBut;

    @FXML
    private Label labelMessage;

    @FXML
    private Label labelMessage1;

    @FXML
    private ListView<String> listToOrder;

    @FXML
    private Label totalOrderLabel;

    private final List<OrderItems> orderItemsList = new ArrayList<>();
    private double totalOrderSum = 0.0;
    private static ObservableList<CartItems> previewOrderList = FXCollections.observableArrayList();

public static ObservableList<CartItems> getPreviewOrderList() {
    return previewOrderList;
}

    @FXML
    private TableColumn<CartItems, Integer> amountCartCol;

    @FXML
    private TableColumn<CartItems, Integer> nameProductCol;

    @FXML
    private TableColumn<CartItems, Double> totalCostCol;

    @FXML
    private TableColumn<CartItems, Double> sellPriceCol;

    @FXML
    private Button orderBut;
    private ObservableList<CartItems> cartItemsList;



    @FXML
    private TableView<CartItems> tableCartItems;



    @FXML
    private Button workUserBut;

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

    @FXML
    void initialize() {
        labelMessage1.setVisible(false);
        Request request = new Request();
        request.setRequestType(RequestType.VIEW_CART);
        request.setUserLogin(Session.getUserLogin());
        Connect.client.sendObject(request);

        Response response = (Response) Connect.client.readObject();


        if(response.getResponseStatus()== ResponseStatus.OK){
            labelMessage1.setVisible(false);
            String products = response.getResponseData();

            CartItems[] cartItemsArray = new Gson().fromJson(products, CartItems[].class);
            cartItemsList = FXCollections.observableArrayList(Arrays.asList(cartItemsArray));


            nameProductCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
            sellPriceCol.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
            amountCartCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            totalCostCol.setCellValueFactory(new PropertyValueFactory<>("totalCost"));


            tableCartItems.setItems(cartItemsList);



        }

    }

    @FXML
    public void removeSelectedItemFromCart(ActionEvent event) {
        labelMessage1.setVisible(false);
        CartItems selectedItem = tableCartItems.getSelectionModel().getSelectedItem();

        if(selectedItem!=null){
            try {
                Request request = new Request();
                request.setRequestType(RequestType.REMOVE_FROM_CART);
                request.setUserLogin(Session.getUserLogin());
                request.setRequestMessage(new Gson().toJson(selectedItem.getCartItemId()));

                Connect.client.sendObject(request);

                Response response = (Response) Connect.client.readObject();
                if(response.getResponseStatus()== ResponseStatus.OK){

                    Dialog.showAlertInfo(response.getResponseMessage());
                    uploadeCartItems();
                }
                else {
                    Dialog.showAlertInfo(response.getResponseMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            labelMessage1.setText("Пожалуйста выберите товар для удаления!");
            labelMessage1.setVisible(true);
        }
    }

    private void uploadeCartItems(){
        labelMessage1.setVisible(false);
        Request request = new Request();
        request.setRequestType(RequestType.VIEW_CART);
        request.setUserLogin(Session.getUserLogin());
        Connect.client.sendObject(request);

        Response response = (Response) Connect.client.readObject();


        if(response.getResponseStatus()== ResponseStatus.OK){
            labelMessage1.setVisible(false);
            String products = response.getResponseData();

            CartItems[] cartItemsArray = new Gson().fromJson(products, CartItems[].class);
            cartItemsList = FXCollections.observableArrayList(Arrays.asList(cartItemsArray));


            nameProductCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
            sellPriceCol.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
            amountCartCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            totalCostCol.setCellValueFactory(new PropertyValueFactory<>("totalCost"));


            tableCartItems.setItems(cartItemsList);

        }
    }

    @FXML
    public void addToOrderPreview() {
        CartItems selectedItem = tableCartItems.getSelectionModel().getSelectedItem();
        if(selectedItem!=null){
            Product product = selectedItem.getProduct();
            int quantity = selectedItem.getQuantity();
            double total = selectedItem.getTotalCost();


            OrderItems orderItem = new OrderItems();
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItemsList.add(orderItem);
            cartItemsList.remove(selectedItem);
            tableCartItems.setItems(cartItemsList);
            String previewLine = product.getProductName()+" "+product.getSellPrice()+" " + " x " + quantity + " = " + total + " BYN";
            listToOrder.getItems().add(previewLine);
            previewOrderList.add(selectedItem);
            recalculateTotal();



        }
    }


    @FXML
    private void removeSelectedFromPreview(ActionEvent event) {
        int selectedIndex = listToOrder.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < previewOrderList.size()) {

            CartItems itemToReturn = previewOrderList.get(selectedIndex);

            cartItemsList.add(itemToReturn);
            tableCartItems.setItems(cartItemsList);

            previewOrderList.remove(selectedIndex);
            listToOrder.getItems().remove(selectedIndex);

            recalculateTotal();
        }
    }

    @FXML
    private void clearPreviewOrderList(ActionEvent event) {
        listToOrder.getItems().clear();
        previewOrderList.clear();
        recalculateTotal();
    }


    private void recalculateTotal() {
        double total = 0;

        for (CartItems item : previewOrderList) {
            total += item.getQuantity() * item.getProductPrice();
        }

        totalOrderLabel.setText("Сумма заказа: " + total + " BYN");
    }

    @FXML
    public void placeOrder(ActionEvent event) {
        Random random = new Random();
        if(previewOrderList.isEmpty()){
            Dialog.showAlertInfo("Список заказа пуст!");

        } else{
            Order order = new Order();
            order.setUser(Session.getCurrentUser());

            double totalAmount = 0;
            for (CartItems item : previewOrderList) {
                totalAmount += item.getTotalCost();
            }
            order.setTotalAmount(totalAmount);
            OrderState orderState = new OrderState();

            List<OrderItems> orderItemsList = new ArrayList<>();
            for (CartItems cartItem : previewOrderList) {
                OrderItems orderItem = new OrderItems();
                orderItem.setProduct(cartItem.getProduct());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItemsList.add(orderItem);
            }

            for (OrderItems item : order.getOrderItems()) {
                System.out.println(item.getProduct().getProductName());
                System.out.println(item.getQuantity());
            }
//олучить название метода оплаты и статус

            order.setOrderItems(orderItemsList);

            Request request = new Request();
            request.setUserLogin(Session.getUserLogin());
            request.setRequestType(RequestType.PLACE_ORDER);
            request.setRequestMessage(new Gson().toJson(order));
            Connect.client.sendObject(request);

            Response response = (Response) Connect.client.readObject();
            if(response.getResponseStatus()== ResponseStatus.OK){
                System.out.println(response.getResponseMessage());
                goToMakeOrder();
            } else {
                System.out.println(response.getResponseMessage());
            }




        }
    }


   public void goToMakeOrder() {
       orderBut.getScene().getWindow().hide();

       FXMLLoader loader = new FXMLLoader();
       loader.setLocation(getClass().getResource("/makeOrder.fxml"));


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

}
