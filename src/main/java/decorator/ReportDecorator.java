package decorator;

public abstract class ReportDecorator implements FinancialReport {
    protected FinancialReport report;

    public ReportDecorator(FinancialReport report) {
        this.report = report;
    }

    @Override
    public String generateReport() {
        return report.generateReport();
    }

}