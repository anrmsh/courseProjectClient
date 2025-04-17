package controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import ClientWorker.Connect;
import Enums.RequestType;
import Enums.ResponseStatus;
import TCP.Request;
import TCP.Response;
import check.Dialog;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import salonOrg.ManagerReport;
import salonOrg.OperationAccounter;

public class ManagerMakeReportController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backButton;

    @FXML
    private DatePicker dateFirst;

    @FXML
    private DatePicker dateLast;

    @FXML
    private Label labelSort1;

    @FXML
    private Button printReportBut;

    @FXML
    private TextField textAllOrders;

    @FXML
    private TextField textAmountSold;

    @FXML
    private TextField textAverageCheque;

    @FXML
    private TextField textCurrentDate;

    @FXML
    private TextField textNameManeger;

    @FXML
    private TextField textPopularPr;

    @FXML
    private TextField textReadyOrders;

    @FXML
    private TextField textTotalAmount;

    @FXML
    private Button workUserBut;

    @FXML
    private Button workUserBut1;

    @FXML
    void goToMainPageCustomer(ActionEvent event) {

    }

    @FXML
    void removeParamButt(ActionEvent event) {

    }

    @FXML
    void initialize() {

    }

    @FXML
    public void loadDataforReport() {

        LocalDate startDate = dateFirst.getValue();
        LocalDate endDate = dateLast.getValue();
        if (startDate == null || endDate == null) {
            Dialog.showAlertInfo("Выберите обе даты для формирования отчёта.");
            return;
        }
//        if(textNameManeger.getText().isEmpty()) {
//            Dialog.showAlertInfo("Введите свою ФИО в поле Выполнил!");
//            return;
//        }

        String dateRange = startDate.toString() + "," + endDate.toString();

        Request request = new Request();
        request.setRequestType(RequestType.MAKE_SALES_REPORT);
        request.setRequestMessage(dateRange);

        Connect.client.sendObject(request);

        Response response = (Response) Connect.client.readObject();

        if(response.getResponseStatus()== ResponseStatus.OK){
            ManagerReport reportData = new Gson().fromJson(response.getResponseData(), ManagerReport.class);
            textAllOrders.setText(String.valueOf(reportData.getTotalOrders()));
            textReadyOrders.setText(String.valueOf(reportData.getCompletedOrders()));
            textAmountSold.setText(String.valueOf(reportData.getTotalProductsSold()));
            textTotalAmount.setText(String.format("%.2f", reportData.getTotalRevenue()));
            textAverageCheque.setText(String.format("%.2f", reportData.getAverageCheque()));
            textPopularPr.setText(reportData.getTopProduct()!=null? reportData.getTopProduct(): " - ");

            textCurrentDate.setText(LocalDate.now().toString());

            printReportBut.setOnAction(event -> {
                if(textNameManeger.getText().equals("")){
                    Dialog.showAlertInfo("Заполните ФИО работника, выполнившего отчёт");
                    return;
                } else{
                    generateExelReport(reportData, LocalDate.now(),textNameManeger.getText(),startDate,endDate);
                }

            });


        }
        else {
            Dialog.showAlertInfo(response.getResponseMessage());
        }

    }




    private void generateExelReport(ManagerReport report, LocalDate currentDate, String nameManager,LocalDate startDate, LocalDate endDate) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Отчёт о продажах");

        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Отчёт по продажам");

        // 2. Период выборки (строка 2)
        Row periodRow = sheet.createRow(2);
        periodRow.createCell(0).setCellValue("Период:");
        periodRow.createCell(1).setCellValue("с " + startDate.toString() + " по " + endDate.toString());

        // 3. Таблица с данными — начинается с 4-й строки (index = 4)
        Row headerRow = sheet.createRow(4);
        headerRow.createCell(0).setCellValue("Показатель");
        headerRow.createCell(1).setCellValue("Значение");

        String[][] data = {
                {"Всего заказов, шт", String.valueOf(report.getTotalOrders())},
                {"Выполнено заказов, шт", String.valueOf(report.getCompletedOrders())},
                {"Продано товаров, шт", String.valueOf(report.getTotalProductsSold())},
                {"Сумма заказов, BYN", String.format("%.2f", report.getTotalRevenue())},
                {"Средний чек, BYN", String.format("%.2f", report.getAverageCheque())},
                {"Самый продаваемый товар", report.getTopProduct() != null ? report.getTopProduct() : "—"}
        };

        for (int i = 0; i < data.length; i++) {
            Row row = sheet.createRow(i + 5); // начинаем с 5 строки
            row.createCell(0).setCellValue(data[i][0]);
            row.createCell(1).setCellValue(data[i][1]);
        }

        // 4. Пустая строка + строка с текущей датой и менеджером
        Row emptyRow = sheet.createRow(data.length + 6); // например, строка 11
        Row dateRow = sheet.createRow(data.length + 7);  // строка 12
        dateRow.createCell(0).setCellValue("Дата:");
        dateRow.createCell(1).setCellValue(currentDate.toString());

        Row authorRow = sheet.createRow(data.length + 8); // строка 13
        authorRow.createCell(0).setCellValue("Выполнил:");
        authorRow.createCell(1).setCellValue(nameManager);

        // Автоматическое расширение столбцов
        for (int i = 0; i < 2; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fileOut = new FileOutputStream("sales_report_" + LocalDate.now() + ".xlsx")) {
            workbook.write(fileOut);
            Dialog.showAlertInfo("Excel-отчёт успешно создан!");
        } catch (IOException e) {
            Dialog.showAlert("ERROR", "Ошибка при сохранении отчёта");
        }

//        try {
//            FileChooser fileChooser = new FileChooser();
//            fileChooser.setTitle("Сохранить Excel отчёт");
//            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx"));
//            File file = fileChooser.showSaveDialog(null);
//            if (file != null) {
//                try (FileOutputStream fileOut = new FileOutputStream(file)) {
//                    workbook.write(fileOut);
//                    Dialog.showAlertInfo("Excel-отчёт успешно сохранён!");
//                }
//            }
//            workbook.close();
//        } catch (IOException e) {
//            Dialog.showAlert("ERROR", "Ошибка при сохранении отчёта");
//        }

    }

}

