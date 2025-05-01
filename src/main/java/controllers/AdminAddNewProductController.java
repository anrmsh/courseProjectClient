package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import ClientWorker.Connect;
import Enums.RequestType;
import Enums.ResponseStatus;
import TCP.Request;
import TCP.Response;
import check.Check;
import check.Dialog;
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
import javafx.stage.Stage;
import salonOrg.Category;
import salonOrg.Product;
import salonOrg.User;

public class AdminAddNewProductController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addBut;

    @FXML
    private Button backButton;

    @FXML
    private Button costBut;

    @FXML
    private ComboBox<String> categoryBox;

    @FXML
    private Button editBut;

    @FXML
    private Label labelMessage;

    @FXML
    private Button viewCatalog;

    @FXML
    private Button workUserBut;


    @FXML
    private TextField costField;

    @FXML
    private ListView<String> infoListBox;

    @FXML
    private TextField nameProductField;

    @FXML
    private TextField sellPriceField;

    @FXML
    void exitApplication(ActionEvent event) {

    }

    @FXML
    void goToAuthorization(ActionEvent event) {

    }

    @FXML
    void initialize() {
        labelMessage.setVisible(false);

        Request request = new Request();
        request.setRequestType(RequestType.GET_ALL_CATEGORIES);
        Connect.client.sendObject(request);

        Response response = (Response) Connect.client.readObject();


        if(response.getResponseStatus()== ResponseStatus.OK){
            labelMessage.setVisible(false);

            String categories = response.getResponseData();
            /**/
            System.out.println(categories);
            Category[] categoriesArray = new Gson().fromJson(categories, Category[].class);
            ObservableList<String> options = FXCollections.observableArrayList();

            for (Category category : categoriesArray) {
                options.add(category.getCategoryName());
            }
            categoryBox.setItems(options);



        }

    }

    @FXML
    void addNewProduct(ActionEvent event) {

        labelMessage.setVisible(false);
        Product product = new Product();
        product.setProductName(nameProductField.getText());
        product.setCategory(categoryBox.getSelectionModel().getSelectedItem());

        String name = nameProductField.getText();
        Double sellPrice = null;
        Double cost = null;
        String category = categoryBox.getSelectionModel().getSelectedItem();



        try {
            sellPrice = Double.parseDouble(sellPriceField.getText());
            cost = Double.parseDouble(costField.getText());
            product.setSellPrice(sellPrice);
            product.setCost(cost);
        } catch (NumberFormatException e) {
            Dialog.showAlert("Error","Неверный ввод! Заполните цену/стоимость цифрой");
            //labelMessage.setText("Неверный ввод! Заполните цену/стоимость цифрой");
            //labelMessage.setVisible(true);
            return;
        }

        if (name.isEmpty() || sellPrice==null || cost==null || category.isEmpty()) {
            Dialog.showAlertInfo("Пожалйста заполните все поля");
            return;
        }

        Request request = new Request();
        request.setRequestType(RequestType.ADD_NEW_PRODUCT);
        request.setRequestMessage(new Gson().toJson(product));
        Connect.client.sendObject(request);

        System.out.println("Запись товара отправлена на сервер");

        String mes = "";
        Response response = (Response) Connect.client.readObject();
        mes = response.getResponseMessage();

        if (response.getResponseStatus()== ResponseStatus.OK) {
            Dialog.showAlertInfo(mes);
            nameProductField.clear();
            sellPriceField.clear();
            costField.clear();
            categoryBox.getSelectionModel().clearSelection();
            String productInfo = String.format("Название: %s, Цена: %.2f, Стоимость: %.2f, Категория: %s",
                    product.getProductName(), product.getSellPrice(), product.getCost(), product.getCategory());
            infoListBox.getItems().add(productInfo);
        } else {
            Dialog.showAlertInfo(mes);
        }
    }


    @FXML
    void goToMainPageAdmin(ActionEvent event) {
        backButton.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/adminWorkProducts.fxml"));


        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root,830,600));
        stage.setTitle("Shop");
        stage.show();

    }

}

