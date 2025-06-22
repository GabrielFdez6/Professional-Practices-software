package professionalpractice.model.pojo;

public class Criteria {
    private int idCriteria;
    private String criteriaName;
    private String competent;
    private String independent;
    private String advancedBasic;
    private String thresholdBasic;
    private String notCompetent;

    public Criteria() {}

    public int getIdCriteria() {
        return idCriteria;
    }

    public void setIdCriteria(int idCriteria) {
        this.idCriteria = idCriteria;
    }

    public String getCriteriaName() {
        return criteriaName;
    }

    public void setCriteriaName(String criteriaName) {
        this.criteriaName = criteriaName;
    }

    public String getCompetent() {
        return competent;
    }

    public void setCompetent(String competent) {
        this.competent = competent;
    }

    public String getIndependent() {
        return independent;
    }

    public void setIndependent(String independent) {
        this.independent = independent;
    }

    public String getAdvancedBasic() {
        return advancedBasic;
    }

    public void setAdvancedBasic(String advancedBasic) {
        this.advancedBasic = advancedBasic;
    }

    public String getThresholdBasic() {
        return thresholdBasic;
    }

    public void setThresholdBasic(String thresholdBasic) {
        this.thresholdBasic = thresholdBasic;
    }

    public String getNotCompetent() {
        return notCompetent;
    }

    public void setNotCompetent(String notCompetent) {
        this.notCompetent = notCompetent;
    }
}