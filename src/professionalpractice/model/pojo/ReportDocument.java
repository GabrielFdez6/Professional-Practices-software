package professionalpractice.model.pojo;

import java.math.BigDecimal;
import java.util.Date;

public class ReportDocument {
    private int idReportDocument;
    private int reportedHours;
    private Date date;
    private BigDecimal grade;
    private String name;
    private boolean delivered;
    private String status;
    private String filePath;

    public ReportDocument() {}

    public ReportDocument(int idReportDocument, int reportedHours, Date date, BigDecimal grade, String name, boolean delivered, String status, String filePath) {
        this.idReportDocument = idReportDocument;
        this.reportedHours = reportedHours;
        this.date = date;
        this.grade = grade;
        this.name = name;
        this.delivered = delivered;
        this.status = status;
        this.filePath = filePath;
    }

    public int getIdReportDocument() {
        return idReportDocument;
    }

    public void setIdReportDocument(int idReportDocument) {
        this.idReportDocument = idReportDocument;
    }

    public int getReportedHours() {
        return reportedHours;
    }

    public void setReportedHours(int reportedHours) {
        this.reportedHours = reportedHours;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getGrade() {
        return grade;
    }

    public void setGrade(BigDecimal grade) {
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
