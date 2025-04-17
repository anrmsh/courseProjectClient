package controllers;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;

import ClientWorker.Connect;
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
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import salonOrg.Category;
import salonOrg.Product;

public class AdminMakeDiscountController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backButton;

    @FXML
    private TableColumn<Product, String> categoryCol;

    @FXML
    private TableColumn<Product, Double> costPurchaceCol;

    @FXML
    private Label labelSort;

    @FXML
    private TableColumn<Product, String> nameProductCol;

    @FXML
    private TableColumn<Product, Double> priceSellCol;

    @FXML
    private Button saveChangesBut;

    @FXML
    private ComboBox<String> sortBox;

    @FXML
    private TableView<Product> tableProducts;

    private ObservableList<Product> productsList;

    @FXML
    private Button workUserBut;

    @FXML
    void goToMainPageAdmin(ActionEvent event) {
        backButton.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/adminPage.fxml"));

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
    void initialize() {

        sortBox.getItems().addAll("По цене продажи ↑", "По цене продажи ↓", "По алфавиту");

        sortBox.setOnAction(event -> {sortListProduct();});
        Request request = new Request();
        request.setRequestType(RequestType.ADMIN_VIEW_CATALOG);
        Connect.client.sendObject(request);

        Response response = (Response) Connect.client.readObject();

        if(response.getResponseStatus()== ResponseStatus.OK){

            String products = response.getResponseData();

            Product[] productsArray = new Gson().fromJson(products, Product[].class);
            productsList = FXCollections.observableArrayList(Arrays.asList(productsArray));
            nameProductCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
            priceSellCol.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
            categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
            costPurchaceCol.setCellValueFactory(new PropertyValueFactory<>("cost"));

            tableProducts.setItems(productsList);

            tableProducts.setRowFactory(tv ->{
                TableRow<Product> row = new TableRow<>();
                row.setOnMouseClicked(event ->{
                    if(!row.isEmpty() && event.getClickCount() == 1){
                        Product selectedProduct = row.getItem();
                        showDiscountDialog(selectedProduct);
                    }
                });
                return row;
            });



        }
    }



    private void sortListProduct() {
        String selectedSort = sortBox.getSelectionModel().getSelectedItem();
        ObservableList<Product> sortedList = FXCollections.observableArrayList();

        if(selectedSort!=null){
            switch (selectedSort) {
                case "По цене продажи ↑":
                    productsList.sort(Comparator.comparingDouble(Product::getSellPrice));
                    break;

                case "По цене продажи ↓":
                    productsList.sort(Comparator.comparingDouble(Product::getSellPrice).reversed());
                    break;

                case "По алфавиту":
                    productsList.sort(Comparator.comparing(Product::getProductName));
                    break;

                default:
                    break;
            }
        }


        tableProducts.setItems(productsList);  // Обновляем таблицу
    }

    private void showDiscountDialog(Product product) {
        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle("Изменение цены товара");
        Label productNameLabel = new Label("Товар: " + product.getProductName());

        Label infoLabel = new Label("Введите скидку (c -) или наценку (c +) в %:");
        TextField percentField = new TextField();
        Label resultPriceLabel1 = new Label("Старая цена: " + product.getSellPrice());
        Label resultPriceLabel = new Label("Итоговая цена: " + product.getSellPrice());

        VBox content = new VBox(10, productNameLabel, infoLabel, percentField, resultPriceLabel1, resultPriceLabel);
        content.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        percentField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                double percent = Double.parseDouble(newValue.replace(",","."));
                double updatedPrice = product.getSellPrice()*(1 + percent/100);
                resultPriceLabel.setText(String.format("Итоговая цена: %.2f", updatedPrice));
            }catch (NumberFormatException e) {
                resultPriceLabel.setText("Итоговая цена: —");
            }
        });

        dialog.setResultConverter(button ->{
            if(button == ButtonType.OK){
                try {
                    return Double.parseDouble(percentField.getText().replace(",", "."));
                    } catch (NumberFormatException e) {
                    check.Dialog.showAlertInfo("Некорректное значение процента");

                }
            }
            return null;
        });

        Optional<Double> result = dialog.showAndWait();

        result.ifPresent(percent -> {
            double updatedPrice = product.getSellPrice() * (1 + percent / 100);
            product.setSellPrice(updatedPrice);
            sendPriceUpdateRequest(product);
        });

    }

    private void sendPriceUpdateRequest(Product product) {
        Request request = new Request();
        request.setRequestType(RequestType.SET_DISCOUNT_MARKUP);
        request.setRequestMessage(new Gson().toJson(product));

        Connect.client.sendObject(request);

        Response response = (Response) Connect.client.readObject();
        if (response.getResponseStatus() == ResponseStatus.OK) {
            check.Dialog.showAlertInfo("Цена успешно обновлена");
            reloadProducts();
        } else {
            check.Dialog.showAlertInfo("Ошибка обновления цены: " + response.getResponseMessage());
        }
    }

    private void reloadProducts() {
        Request request = new Request();
        request.setRequestType(RequestType.ADMIN_VIEW_CATALOG);
        Connect.client.sendObject(request);

        Response response = (Response) Connect.client.readObject();

        if (response.getResponseStatus() == ResponseStatus.OK) {
            Product[] productsArray = new Gson().fromJson(response.getResponseData(), Product[].class);
            productsList = FXCollections.observableArrayList(Arrays.asList(productsArray));
            nameProductCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
            priceSellCol.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
            categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
            costPurchaceCol.setCellValueFactory(new PropertyValueFactory<>("cost"));

            tableProducts.setItems(productsList);
        }
    }
}
