package controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.ResourceBundle;

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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import salonOrg.OperationAccounter;

public class AccounterPlanController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableColumn<OperationAccounter, Double> amountColumn;

    @FXML
    private Button backButton;

    @FXML
    private TextField balanceField;

    @FXML
    private TableColumn<OperationAccounter, LocalDate> dateColumn;

    @FXML
    private DatePicker dateFromPicker;

    @FXML
    private DatePicker dateToPicker;

    @FXML
    private CheckBox expenseCheckBox;

    @FXML
    private CheckBox incomeCheckBox;

    @FXML
    private TableView<OperationAccounter> tableOperations;

    @FXML
    private TableColumn<OperationAccounter, String> typeColumn;

    @FXML
    private Button workUserBut;

    @FXML
    private Button applyBut;

    private ObservableList<OperationAccounter> operationsList;



    @FXML
    void initialize() {

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfOperation"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("typeOfOperation"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amountOfOperation"));

        expenseCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> filterOperations());
        incomeCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> filterOperations());

    }

    @FXML
    public void loadDataforReport() {
        LocalDate startDate = dateFromPicker.getValue();
        LocalDate endDate = dateToPicker.getValue();
        if(startDate==null) {
            startDate = LocalDate.of(2023, 1, 1);;
        }
        if(endDate==null) {
            endDate = LocalDate.now();
        }

        String dateRange = startDate.toString() + "," + endDate.toString();

        Request request = new Request();
        request.setRequestType(RequestType.GET_ACCOUNTING_DATA);
        request.setRequestMessage(dateRange);

        Connect.client.sendObject(request);

        Response response = (Response) Connect.client.readObject();




        if(response.getResponseStatus()== ResponseStatus.OK){
            String operations = response.getResponseData();

            OperationAccounter[] operationsArray = new Gson().fromJson(operations, OperationAccounter[].class);
            operationsList = FXCollections.observableArrayList(Arrays.asList(operationsArray));


            dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfOperation"));
            typeColumn.setCellValueFactory(new PropertyValueFactory<>("typeOfOperation"));
            amountColumn.setCellValueFactory(new PropertyValueFactory<>("amountOfOperation"));

            tableOperations.setItems(operationsList);

            double currentBalance = calculateBalance(operationsList);
            balanceField.setText(String.valueOf(currentBalance));

        }
    }




    @FXML
    void goToMainPageAccounter(ActionEvent event) {
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

    private Double calculateBalance(ObservableList<OperationAccounter> operations){
        double totalIncome = 0.0;
        double totalExpenses = 0.0;

        for(OperationAccounter operation : operations){
            if(operation.getTypeOfOperation().equals("Доход")){
                totalIncome += operation.getAmountOfOperation();
            }
            else if (operation.getTypeOfOperation().equals("Расход")){
                totalExpenses += operation.getAmountOfOperation();
            }
        }
        return totalIncome-totalExpenses;
    }

    private void filterOperations() {
        ObservableList<OperationAccounter> filteredList = FXCollections.observableArrayList();

        boolean showIncome = incomeCheckBox.isSelected();
        boolean showExpenses = expenseCheckBox.isSelected();

        for (OperationAccounter operation : operationsList) {
            if ((showIncome && operation.getTypeOfOperation().equals("Доход")) ||
                    (showExpenses && operation.getTypeOfOperation().equals("Расход"))) {
                filteredList.add(operation);
            }
        }

        tableOperations.setItems(filteredList);

    }

}
