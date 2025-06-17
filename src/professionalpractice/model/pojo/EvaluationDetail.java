package professionalpractice.model.pojo;

public class EvaluationDetail {
    private int idEvaluationDetail;
    private int idEvaluation;
    private int idCriteria;
    private float grade;

    public int getIdEvaluationDetail() {
        return idEvaluationDetail;
    }

    public void setIdEvaluationDetail(int idEvaluationDetail) {
        this.idEvaluationDetail = idEvaluationDetail;
    }

    public int getIdEvaluation() {
        return idEvaluation;
    }

    public void setIdEvaluation(int idEvaluation) {
        this.idEvaluation = idEvaluation;
    }

    public int getIdCriteria() {
        return idCriteria;
    }

    public void setIdCriteria(int idCriteria) {
        this.idCriteria = idCriteria;
    }

    public float getGrade() {
        return grade;
    }

    public void setGrade(float grade) {
        this.grade = grade;
    }
}