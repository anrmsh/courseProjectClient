package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.*;

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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import salonOrg.Category;
import salonOrg.Product;
import salonOrg.Warehouse;

public class ManagerRestockController {


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
    private Label labelCategory;

    @FXML
    private Label labelMessage;

    private ObservableList<Product> productsList;

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
    private TableColumn<Product, Integer> amountWarehouseCol;

    @FXML
    private TableColumn<Product, Double> sellPriceCol;

    @FXML
    private TableColumn<Product, String> categoryCol;

    @FXML
    private Button removeParamButt;

    @FXML
    private Button searchButt;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> sortBox;

    @FXML
    private TableView<Product> tableProducts;

    @FXML
    private Button workUserBut;



    @FXML
    void initialize() {
        labelMessage.setVisible(false);
        sortBox.getItems().addAll("По кол-ву остатков ↑", "По кол-ву остатков ↓", "По алфавиту");

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
            sellPriceCol.setCellValueFactory(new PropertyValueFactory<>("cost"));
            categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
            amountWarehouseCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

            /**/
//            amountWarehouseCol.setCellFactory(new Callback<TableColumn<Product, Integer>, TableCell<Product, Integer>>() {
//                @Override
//                public TableCell<Product, Integer> call(TableColumn<Product, Integer> param) {
//                    return new TableCell<Product, Integer>() {
//                        @Override
//                        protected void updateItem(Integer item, boolean empty) {
//                            super.updateItem(item, empty);
//                            if (empty || item == null) {
//                                setText(null);
//                                setStyle(""); // сбрасываем стиль
//                            } else {
//                                setText(item.toString());
//                                if (item < 3) {
//                                    setStyle("-fx-background-color: rgba(255, 0, 0, 0.5);"); // Полупрозрачный красный
//                                } else {
//                                    setStyle(""); // сбрасываем стиль
//                                }
//                            }
//                        }
//                    };
//                }
//            });

            amountWarehouseCol.setCellFactory(new Callback<TableColumn<Product, Integer>, TableCell<Product, Integer>>() {
                @Override
                public TableCell<Product, Integer> call(TableColumn<Product, Integer> param) {
                    return new TableCell<Product, Integer>() {
                        @Override
                        protected void updateItem(Integer item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) {
                                setText(null);
                                setStyle("");
                            } else {
                                setText(item.toString());
                                TableRow<Product> currentRow = getTableRow();
                                if (item < 3) {
                                    currentRow.setStyle("-fx-background-color: rgba(255, 0, 0, 0.5);"); // Полупрозрачный красный
                                } else {
                                    currentRow.setStyle("");
                                }
                            }
                        }
                    };
                }
            });

            /**/


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

            }

            tableProducts.setItems(productsList);

            /**/

            tableProducts.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    Product selectedProduct = tableProducts.getSelectionModel().getSelectedItem();
                    if (selectedProduct != null) {
                        showPurchaseDialog(selectedProduct);
                    }
                }
            });

            /**/

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
        System.out.println("Выбранные категории: " + selectedCategoryIDs);
        Map<String, Object> filterData = new HashMap<>();
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
            sellPriceCol.setCellValueFactory(new PropertyValueFactory<>("cost"));
            categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
            amountWarehouseCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

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
            sellPriceCol.setCellValueFactory(new PropertyValueFactory<>("cost"));
            categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
            amountWarehouseCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));


            tableProducts.setItems(productsList);
        }
    }


    private void sortListProduct() {
        String selectedSort = sortBox.getSelectionModel().getSelectedItem();
        ObservableList<Product> sortedList = FXCollections.observableArrayList();

        if(selectedSort!=null){
            switch (selectedSort) {
                case "По кол-ву остатков ↑":
                    productsList.sort(Comparator.comparingDouble(Product::getQuantity));

                    break;

                case "По кол-ву остатков ↓":
                    productsList.sort(Comparator.comparingDouble(Product::getQuantity).reversed());
                    break;

                case "По алфавиту":
                    productsList.sort(Comparator.comparing(Product::getProductName));
                    break;

                default:
                    break;
            }
        }


        tableProducts.setItems(productsList);
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


    private void showPurchaseDialog(Product product) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Ввод количества");
        dialog.setHeaderText("Введите количество для закупки товара " + product.getProductName() + " на склад\nЦена закупки, BYN: "+ product.getCost());
        dialog.setContentText("Количество:");

        Label totalPriceLabel = new Label();
        totalPriceLabel.setText("Общая стоимость: 0.0");

        dialog.getDialogPane().setContent(new VBox(8, dialog.getEditor(), totalPriceLabel));

        dialog.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int qty = Integer.parseInt(newValue);
                double totalPrice = qty * product.getCost();
                totalPriceLabel.setText("Общая цена: " + totalPrice);
            } catch (NumberFormatException e) {
                totalPriceLabel.setText("Общая цена: 0.0");
            }
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(quantity -> {
            try {
                int qty = Integer.parseInt(quantity);
                sendRestockRequest(product, qty);
            } catch (NumberFormatException e) {
                showError("Пожалуйста, введите корректное количество.");
            }
        });
    }


    private void sendRestockRequest(Product product, int quantity) {
        Request request = new Request();
        request.setRequestType(RequestType.RESTOCK_WAREHOUSE);
        Product productToAdd = new Product();

        productToAdd.setProductName(product.getProductName());
        productToAdd.setSellPrice(product.getSellPrice());
        productToAdd.setCategory(product.getCategory());
        productToAdd.setCost(product.getCost());

        productToAdd.setQuantity(quantity);
        System.out.println(Session.getUserLogin());

        request.setRequestMessage(new Gson().toJson(productToAdd));
        request.setUserLogin(Session.getUserLogin());
        Connect.client.sendObject(request);


        Response response = (Response) Connect.client.readObject();
        Dialog.showAlertInfo(response.getResponseMessage());

        updateProductList();


    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


//    private void showInfo(String message) {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Информация");
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }


    private void updateProductList() {
        Request request = new Request();
        request.setRequestType(RequestType.ADMIN_VIEW_CATALOG);
        Connect.client.sendObject(request);
        Response response = (Response) Connect.client.readObject();

        if(response.getResponseStatus() == ResponseStatus.OK) {
            String products = response.getResponseData();
            Product[] productsArray = new Gson().fromJson(products, Product[].class);
            productsList = FXCollections.observableArrayList(Arrays.asList(productsArray));
            tableProducts.setItems(productsList);
        } else {
            labelMessage.setText("Не удалось обновить список продуктов.");
            labelMessage.setVisible(true);
        }
    }



}
