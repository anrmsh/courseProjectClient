package decorator;

public class RecommendationDecorator extends ReportDecorator {

    private String userRecommendations;

    public RecommendationDecorator(FinancialReport report, String userRecommendations) {
        super(report);
        this.userRecommendations = userRecommendations;
    }

    @Override
    public String generateReport() {
        String report = super.generateReport();
        return report + "\n\nРекомендации:\n" + generateRecommendations();
    }

    private String generateRecommendations() {
        return userRecommendations != null && !userRecommendations.isEmpty() ? userRecommendations : "Нет рекомендаций.";
    }

}
