package controllers;

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
import decorator.BasicReport;
import decorator.FinancialReport;
import decorator.RecommendationDecorator;
import decorator.ReportDecorator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import salonOrg.FinacialReport;
import salonOrg.ManagerReport;

public class AccounterReportController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backButton;

    @FXML
    private TextField efficiencyField;

    @FXML
    private TextField expenseField;

    @FXML
    private Label labelSort1;

    @FXML
    private TextField otherExpensesField;

    @FXML
    private Button printReportBut;

    @FXML
    private TextField profitField;

    @FXML
    private TextArea recomendationArea;

    @FXML
    private TextField revenueField;

    @FXML
    private TextField taxField;

    @FXML
    private TextField taxRateField;

    @FXML
    private TextField textCurrentDate;

    @FXML
    private TextField textNameAccounter;

    @FXML
    private Button workUserBut;

    @FXML
    void goToMainPageAccounter(ActionEvent event) {

    }

    @FXML
    void removeParamButt(ActionEvent event) {

    }

    @FXML
    void initialize() {
        loadDataforFinReport();
    }



    @FXML
    public void loadDataforFinReport() {
        Request request = new Request();
        request.setRequestType(RequestType.MAKE_ACCOUNTIONG_REPORT);

        Connect.client.sendObject(request);

        Response response = (Response) Connect.client.readObject();

        if(response.getResponseStatus()== ResponseStatus.OK){
            FinacialReport reportData = new Gson().fromJson(response.getResponseData(), FinacialReport.class);
            revenueField.setText(String.format("%.2f",reportData.getIncome() ));
            expenseField.setText(String.format("%.2f", reportData.getExpenses() ));
            double other = parseFinancial(otherExpensesField.getText());
            double rateTax = parseFinancial(taxRateField.getText());

//decorator from gpt officuial
//            Report report = new BaseReport(revenue, expense, other, rate);
//            report = new RecommendationDecorator(report); // декоратор
//
//            recommendationArea.setText(report.generate());

//            double revenue = Double.parseDouble(revenueField.getText());
//            double expense = Double.parseDouble(expenseField.getText());

            double revenue = parseFinancial(revenueField.getText());
            double expense = parseFinancial(expenseField.getText());

            double profit = revenue - expense - other;
            double tax = profit * rateTax / 100;
            double netProfit = profit - tax;
            double profitability = expense != 0 ? (netProfit / expense) * 100 : 0;


            taxField.setText(String.format("%.2f", tax));
            profitField.setText(String.format("%.2f", netProfit));
            efficiencyField.setText(String.format("%.2f", profitability));



            textCurrentDate.setText(LocalDate.now().toString());
//
//            printReportBut.setOnAction(event -> {
//                if(textNameAccounter.getText().equals("")){
//                    Dialog.showAlertInfo("Заполните ФИО работника, выполнившего отчёт");
//
//                } else{
//                    //generateExelReport(reportData, LocalDate.now(),textNameManeger.getText(),startDate,endDate);
//                }
//
//            });


        }
        else {
            Dialog.showAlertInfo(response.getResponseMessage());
        }
    }



    @FXML
    public void printReport(ActionEvent event) {
        if (textNameAccounter.getText().isEmpty()) {
            Dialog.showAlertInfo("Заполните ФИО работника, выполнившего отчёт");
        } else {
            double income = parseFinancial(revenueField.getText());
            double expenses = parseFinancial(expenseField.getText());

            //double other = Double.parseDouble(otherExpensesField.getText());
            double other = parseFinancial(otherExpensesField.getText());
            //double taxRate = Double.parseDouble(taxRateField.getText());
            double taxRate = parseFinancial(taxRateField.getText());
            String recommendations = recomendationArea.getText();

            FinancialReport report = new BasicReport(income, expenses, taxRate, other);
            if(!recommendations.isEmpty()){
                report = new RecommendationDecorator(report, recommendations);
            }
            generateExcelReport(textNameAccounter.getText(),LocalDate.now(),report);
        }

    }

    private void generateExcelReport(String accounterName, LocalDate reportDate, FinancialReport report) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Финансовый отчёт");

        int rowNum = 0;

        // Заголовок
        Row titleRow = sheet.createRow(rowNum++);
        titleRow.createCell(0).setCellValue("Финансовый отчёт от " + reportDate);

        // Имя бухгалтера
        Row accounterRow = sheet.createRow(rowNum++);
        accounterRow.createCell(0).setCellValue("Составил: " + accounterName);

        sheet.createRow(rowNum++);

        String[] lines = report.generateReport().split("\n");
        for (String line : lines) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(line);
        }

        // Сохранение файла
        try (FileOutputStream fileOut = new FileOutputStream("financial_report_" + reportDate + ".xlsx")) {
            workbook.write(fileOut);
            Dialog.showAlertInfo("Excel-отчёт успешно создан!");
        } catch (IOException e) {
            Dialog.showAlertInfo("Ошибка при создании отчёта: ");
        }
    }


    public double parseFinancial(String finString) {
        try {
            if (finString == null || finString.trim().isEmpty()) {
                return 0.0;
            }
            finString = finString.replace(",", ".");
            return Double.parseDouble(finString);
        } catch (NumberFormatException e) {
            System.err.println("Ошибка при парсинге значения: " + finString);
            e.printStackTrace();
            return 0.0;
        }
    }

}

