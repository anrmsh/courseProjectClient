package decorator;

public class BasicReport implements FinancialReport {
    protected double income;
    protected double expenses;
    protected double taxRate;
    protected double otherExpenses;

    public BasicReport(double income, double expenses, double taxRate, double otherExpenses) {
        this.income = income;
        this.expenses = expenses;
        this.taxRate = taxRate;
        this.otherExpenses = otherExpenses;
    }


    @Override
    public String generateReport() {
        double profit = income - expenses - otherExpenses;
        double tax = profit * taxRate / 100;
        double netProfit = profit - tax;
        double profitability = expenses != 0 ? (netProfit / expenses) * 100 : 0;

        return String.format(
                "Доход: %.2f\n" +
                "Расход: %.2f\n" +
                "Прочие расходы: %.2f\n" +
                "Налог: %.2f\n" +
                "Чистая прибыль: %.2f\n" +
                "Рентабельность: %.2f%%\n",
                income, expenses, otherExpenses, tax, netProfit, profitability
        );
    }
}
