package professionalpractice.model.pojo;

import javafx.beans.property.SimpleStringProperty;

public class EvaluationCriterion {

    private final SimpleStringProperty criterion;
    private final SimpleStringProperty competent;
    private final SimpleStringProperty independent;
    private final SimpleStringProperty advancedBasic;
    private final SimpleStringProperty thresholdBasic;
    private final SimpleStringProperty notCompetent;

    public EvaluationCriterion(String criterion, String competent, String independent, String advancedBasic, String thresholdBasic, String notCompetent) {
        this.criterion = new SimpleStringProperty(criterion);
        this.competent = new SimpleStringProperty(competent);
        this.independent = new SimpleStringProperty(independent);
        this.advancedBasic = new SimpleStringProperty(advancedBasic);
        this.thresholdBasic = new SimpleStringProperty(thresholdBasic);
        this.notCompetent = new SimpleStringProperty(notCompetent);
    }

    public String getCriterion() {
        return criterion.get();
    }

    public String getCompetent() {
        return competent.get();
    }

    public String getIndependent() {
        return independent.get();
    }

    public String getAdvancedBasic() {
        return advancedBasic.get();
    }

    public String getThresholdBasic() {
        return thresholdBasic.get();
    }

    public String getNotCompetent() {
        return notCompetent.get();
    }
}