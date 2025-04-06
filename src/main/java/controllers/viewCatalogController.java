package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import ClientWorker.Connect;
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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import salonOrg.Category;
import salonOrg.Product;
import salonOrg.User;

public class viewCatalogController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private VBox allCategoriesBox;

    @FXML
    private Button applyButt;

    @FXML
    private Button backButton;

    @FXML
    private TableColumn<Product, String> categoryCol;

    @FXML
    private TableColumn<Product, Double> costPurchaceCol;

    @FXML
    private Label labelCategory;

    @FXML
    private Label labelMessage;

    @FXML
    private Label labelMessage2;

    @FXML
    private Label labelMessage21;

    @FXML
    private Label labelSort;

    @FXML
    private Label lableFilter;

    @FXML
    private Label lableSearch;

    @FXML
    private TextField maxFilterValue;

    @FXML
    private TextField minFilterValue;

    @FXML
    private TableColumn<Product, String> nameProductCol;

    @FXML
    private TableColumn<Product, Double> priceSellCol;

    @FXML
    private Button searchButt;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> sortBox;

    @FXML
    private TableView<Product> tableProducts;

    private ObservableList<Product> productsList;

    @FXML
    private Button workUserBut;


    @FXML
    private Button removeParamButt;

    @FXML
    void exitApplication(ActionEvent event) {

    }

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
        stage.setScene(new Scene(root,700,494));
        stage.setTitle("Shop");
        stage.show();

    }

    @FXML
    void saveUserAccessChanged(ActionEvent event) {

    }

    @FXML
    void initialize() {
        labelMessage.setVisible(false);
        sortBox.getItems().addAll("По цене продажи ↑", "По цене продажи ↓", "По алфавиту");

        sortBox.setOnAction(event -> {sortListProduct();});
        Request request = new Request();
        request.setRequestType(RequestType.ADMIN_VIEW_CATALOG);
        Connect.client.sendObject(request);

        Response response = (Response) Connect.client.readObject();

        Request request2 = new Request();
        request2.setRequestType(RequestType.GET_ALL_CATEGORIES);
        Connect.client.sendObject(request2);

        Response response2 = (Response) Connect.client.readObject();



        if(response.getResponseStatus()== ResponseStatus.OK){
            labelMessage.setVisible(false);
            String products = response.getResponseData();

            Product[] productsArray = new Gson().fromJson(products, Product[].class);
            productsList = FXCollections.observableArrayList(Arrays.asList(productsArray));
            nameProductCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
            priceSellCol.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
            categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
            costPurchaceCol.setCellValueFactory(new PropertyValueFactory<>("cost"));

            if(response2.getResponseStatus()== ResponseStatus.OK){
                labelMessage.setVisible(false);

                String categories = response2.getResponseData();
                /**/
                System.out.println(categories);
                Category[] categoriesArray = new Gson().fromJson(categories, Category[].class);



                for (Category category : categoriesArray) {
                    CheckBox cb=new CheckBox(category.getCategoryName());
                    cb.setUserData(category.getCategoryId());
                    allCategoriesBox.getChildren().add(cb);
                }
                CheckBox checkAll=new CheckBox("Все");
                allCategoriesBox.getChildren().add(checkAll);



                checkAll.setOnAction(event -> {
                    boolean selectAll=checkAll.isSelected();
                    for(Node node : allCategoriesBox.getChildren()){
                        if(node instanceof CheckBox cb && !cb.getText().equals("Все")){
                            cb.setSelected(selectAll);
                        }
                    }
                });

                for (Node node : allCategoriesBox.getChildren()) {
                    if (node instanceof CheckBox cb && !cb.getText().equals("Все")) {
                        cb.setOnAction(e -> {
                            boolean allSelected = allCategoriesBox.getChildren().stream()
                                    .filter(n -> n instanceof CheckBox c && !c.getText().equals("Все"))
                                    .allMatch(n -> ((CheckBox) n).isSelected());
                            checkAll.setSelected(allSelected);
                        });
                    }
                }

               /* List<Integer> selectedCategoryIDs = new ArrayList<>();
                for (Node node: allCategoriesBox.getChildren()) {
                    if(node instanceof CheckBox cb && cb.isSelected()){
                        selectedCategoryIDs.add((Integer) cb.getUserData());
                    }
                }*/
            }

            tableProducts.setItems(productsList);
        }
    }

    @FXML void filterListProduct(ActionEvent event) {
        labelMessage.setVisible(false);
        List<Integer> selectedCategoryIDs = new ArrayList<>();
        for (Node node: allCategoriesBox.getChildren()) {
            if(node instanceof CheckBox cb && cb.isSelected()){
                if (!cb.getText().equals("Все")) {
                    selectedCategoryIDs.add((Integer) cb.getUserData());
                }
            }
        }
        System.out.println("Выбранные категории: " + selectedCategoryIDs);        Map<String, Object> filterData = new HashMap<>();
        filterData.put("categoryIds", selectedCategoryIDs);

        Double minPrice = null;
        Double maxPrice = null;
        try {
            if (!minFilterValue.getText().isEmpty()) {
                minPrice = Double.parseDouble(minFilterValue.getText());
                filterData.put("minPrice", minPrice);
            }
            if (!maxFilterValue.getText().isEmpty()) {
                maxPrice = Double.parseDouble(maxFilterValue.getText());
                filterData.put("maxPrice", maxPrice);
            }
        } catch (NumberFormatException e) {
            labelMessage.setText("Ошибка: введите корректные числа для фильтра цен.");
            labelMessage.setVisible(true);
            return;
        }

        Request request = new Request();
        request.setRequestType(RequestType.FILTER_PRODUCTS);
        request.setRequestMessage(new Gson().toJson(filterData));
        Connect.client.sendObject(request);

        Response response = (Response) Connect.client.readObject();
        if(response.getResponseStatus()== ResponseStatus.OK){
            String filteredProducts = response.getResponseData();
            Product[] productsArray = new Gson().fromJson(filteredProducts, Product[].class);
            productsList = FXCollections.observableArrayList(Arrays.asList(productsArray));
            tableProducts.setItems(productsList);
        } else {
            labelMessage.setText("Не удалось получить отфильтрованные товары.");
            labelMessage.setVisible(true);
        }

    }


    @FXML void searchProduct(ActionEvent event) {
        labelMessage.setVisible(false);

        Request request = new Request();
        request.setRequestType(RequestType.SEARCH_PRODUCTS);
        request.setRequestMessage(new Gson().toJson(searchField.getText()));
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

        } else {
            Dialog.showAlertInfo(response.getResponseMessage());
        }
    }

    @FXML
    public void removeParamButt(ActionEvent event) {
        labelMessage.setVisible(false);
        searchField.clear();
        minFilterValue.clear();
        maxFilterValue.clear();
        Request request = new Request();
        request.setRequestType(RequestType.ADMIN_VIEW_CATALOG);
        Connect.client.sendObject(request);

        Response response = (Response) Connect.client.readObject();


        if(response.getResponseStatus()== ResponseStatus.OK){
            labelMessage.setVisible(false);
            String products = response.getResponseData();

            Product[] productsArray = new Gson().fromJson(products, Product[].class);
            productsList = FXCollections.observableArrayList(Arrays.asList(productsArray));
            nameProductCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
            priceSellCol.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
            categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
            costPurchaceCol.setCellValueFactory(new PropertyValueFactory<>("cost"));


            tableProducts.setItems(productsList);
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


}
