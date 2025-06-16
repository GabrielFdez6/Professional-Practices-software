package professionalpractice.model.pojo;

import java.math.BigDecimal;
import java.util.Date;

public class InitialDocument {
    private int idInitialDocument;
    private String name;
    private Date date;
    private boolean delivered;
    private String status; // ENUM('ENTREGADO','NO_ENTREGADO','EN_REVISION')
    private String filePath;
    private String observations;
    private BigDecimal grade;

    public InitialDocument() {}

    public InitialDocument(int idInitialDocument, String name, Date date, boolean delivered, String status, String filePath, String observations, BigDecimal grade) {
        this.idInitialDocument = idInitialDocument;
        this.name = name;
        this.date = date;
        this.delivered = delivered;
        this.status = status;
        this.filePath = filePath;
        this.observations = observations;
        this.grade = grade;
    }


    // --- Getters and Setters for all attributes ---

    public int getIdInitialDocument() {
        return idInitialDocument;
    }

    public void setIdInitialDocument(int idInitialDocument) {
        this.idInitialDocument = idInitialDocument;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public BigDecimal getGrade() {
        return grade;
    }

    public void setGrade(BigDecimal grade) {
        this.grade = grade;
    }
}