package professionalpractice.model.pojo;

import java.math.BigDecimal;
import java.sql.Date;

public class PresentationEvaluation {
  private int idEvaluation;
  private String title;
  private Date date;
  private Date evaluationDate;
  private BigDecimal grade;
  private BigDecimal averageScore;
  private BigDecimal methodsTechniquesScore;
  private BigDecimal requirementsScore;
  private BigDecimal securityMasteryScore;
  private BigDecimal contentScore;
  private BigDecimal spellingGrammarScore;
  private String observations;
  private int idRecord;
  private int studentId;

  public PresentationEvaluation() {
  }

  public int getIdEvaluation() {
    return idEvaluation;
  }

  public void setIdEvaluation(int idEvaluation) {
    this.idEvaluation = idEvaluation;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Date getEvaluationDate() {
    return evaluationDate;
  }

  public void setEvaluationDate(Date evaluationDate) {
    this.evaluationDate = evaluationDate;
  }

  public BigDecimal getGrade() {
    return grade;
  }

  public void setGrade(BigDecimal grade) {
    this.grade = grade;
  }

  public BigDecimal getAverageScore() {
    return averageScore;
  }

  public void setAverageScore(BigDecimal averageScore) {
    this.averageScore = averageScore;
  }

  public BigDecimal getMethodsTechniquesScore() {
    return methodsTechniquesScore;
  }

  public void setMethodsTechniquesScore(BigDecimal methodsTechniquesScore) {
    this.methodsTechniquesScore = methodsTechniquesScore;
  }

  public BigDecimal getRequirementsScore() {
    return requirementsScore;
  }

  public void setRequirementsScore(BigDecimal requirementsScore) {
    this.requirementsScore = requirementsScore;
  }

  public BigDecimal getSecurityMasteryScore() {
    return securityMasteryScore;
  }

  public void setSecurityMasteryScore(BigDecimal securityMasteryScore) {
    this.securityMasteryScore = securityMasteryScore;
  }

  public BigDecimal getContentScore() {
    return contentScore;
  }

  public void setContentScore(BigDecimal contentScore) {
    this.contentScore = contentScore;
  }

  public BigDecimal getSpellingGrammarScore() {
    return spellingGrammarScore;
  }

  public void setSpellingGrammarScore(BigDecimal spellingGrammarScore) {
    this.spellingGrammarScore = spellingGrammarScore;
  }

  public String getObservations() {
    return observations;
  }

  public void setObservations(String observations) {
    this.observations = observations;
  }

  public int getIdRecord() {
    return idRecord;
  }

  public void setIdRecord(int idRecord) {
    this.idRecord = idRecord;
  }

  public int getStudentId() {
    return studentId;
  }

  public void setStudentId(int studentId) {
    this.studentId = studentId;
  }
}