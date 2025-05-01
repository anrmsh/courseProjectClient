package controllers;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import ClientWorker.Connect;
import Enums.RequestType;
import TCP.Request;
import TCP.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ManagerSalesController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backButton;

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private Button createReportBut;


    @FXML
    private Button handleOrdersBut;

    @FXML
    private Label labelSort;

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private StackPane planeForGrafics;

    @FXML
    private ComboBox<String> typeChartBox;

    @FXML
    private Button workUserBut;

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
    void removeParamButt(ActionEvent event) {

    }

    @FXML
    void initialize() {

        barChart.setVisible(true);
        lineChart.setVisible(true);
        //typeChartBox.getItems().addAll("Количество заказов (столбчатый)", "Сумма заказов (линейный)");
        //typeChartBox.setOnAction(event -> updateChart());
        loadBarChartData();
        loadLineChartData();

    }


    private void updateChart() {
        String selectedChart = typeChartBox.getSelectionModel().getSelectedItem();
        if (selectedChart != null) {
            if (selectedChart.contains("Количество")) {
                barChart.setVisible(true);
                lineChart.setVisible(false);
                loadBarChartData(); // Вызов для загрузки данных для столбчатого графика
            } else {
                lineChart.setVisible(true);
                barChart.setVisible(false);
                loadLineChartData(); // Вызов для загрузки данных для линейного графика
            }
        }
    }


    @FXML
    public void loadBarChartData() {

        barChart.getData().clear();

        // Настройка существующих осей
        ((CategoryAxis) barChart.getXAxis()).setLabel("Месяц");
        NumberAxis yAxis = (NumberAxis) barChart.getYAxis();
        yAxis.setLabel("Количество заказов");
        yAxis.setAutoRanging(true);
        yAxis.setForceZeroInRange(false); // отключает обязательное наличие 0 на оси

        barChart.setTitle("Количество заказов по месяцам");
        barChart.setAnimated(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Количество заказов");

        Map<String, Integer> orderCount = orderCountFromServer();

        for (Map.Entry<String, Integer> entry : orderCount.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().add(series);

    }

    @FXML
    public void loadLineChartData() {

        lineChart.getData().clear();

        ((CategoryAxis) lineChart.getXAxis()).setLabel("Месяц");
        NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();
        yAxis.setLabel("Сумма заказов");
        yAxis.setAutoRanging(true);
        yAxis.setForceZeroInRange(false);

        lineChart.setTitle("Сумма заказов по месяцам");
        lineChart.setAnimated(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Сумма заказов");

        Map<String, Double> revenue = revenueFromServer();

        for (Map.Entry<String, Double> entry : revenue.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        lineChart.getData().add(series);

    }


    public Map<String,Double> revenueFromServer(){

        Request request = new Request();
        request.setRequestType(RequestType.SALES_LINE_CHART);
        Connect.client.sendObject(request);

        Response response = (Response) Connect.client.readObject();

        Type type = new TypeToken<Map<String, Double>>() {}.getType();
        Map<String,Double> orderRevenue = new Gson().fromJson(response.getResponseData(), type);
        System.out.println(orderRevenue);
        System.out.println("Данные для линейного графика:");
        orderRevenue.forEach((k, v) -> System.out.println(k + ": " + v));
        return orderRevenue;
    }

    public Map<String,Integer> orderCountFromServer(){

        Request request = new Request();
        request.setRequestType(RequestType.SALES_BAR_CHART);
        Connect.client.sendObject(request);

        Response response = (Response) Connect.client.readObject();
        Type type = new TypeToken<Map<String, Integer>>() {}.getType();
        Map<String,Integer> orderCount = new Gson().fromJson(response.getResponseData(), type);
        System.out.println(orderCount);
        System.out.println("Данные для столбчатого графика:");
        orderCount.forEach((k, v) -> System.out.println(k + ": " + v));
        return orderCount;
    }

    @FXML
    void goToMakeReport(ActionEvent event) {
        createReportBut.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/managerMakeReport.fxml"));


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

    @FXML
    void goToHandleOrder(ActionEvent event) {
        handleOrdersBut.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/managerHandleOrders.fxml"));


        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root,710,600));
        stage.setTitle("Shop");
        stage.show();
    }


}

