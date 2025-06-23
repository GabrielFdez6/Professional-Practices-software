package professionalpractice.model.pojo;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Delivery {
    private int idDelivery;
    private int idRecord;
    private Integer idDeliveryDefinition;
    private DeliveryDefinition deliveryDefinition;

    private Timestamp dateDelivered;
    private Boolean delivered;
    private String status;
    private String filePath;
    private String observations;
    private BigDecimal grade;
    private Integer reportedHours;

    public Delivery() {}

    public int getIdDelivery() { return idDelivery; }
    public void setIdDelivery(int idDelivery) { this.idDelivery = idDelivery; }

    public int getIdRecord() { return idRecord; }
    public void setIdRecord(int idRecord) { this.idRecord = idRecord; }

    public Integer getIdDeliveryDefinition() { return idDeliveryDefinition; }
    public void setIdDeliveryDefinition(Integer idDeliveryDefinition) { this.idDeliveryDefinition = idDeliveryDefinition; }

    public DeliveryDefinition getDeliveryDefinition() { return deliveryDefinition; }
    public void setDeliveryDefinition(DeliveryDefinition deliveryDefinition) { this.deliveryDefinition = deliveryDefinition; }

    public Timestamp getDateDelivered() { return dateDelivered; }
    public void setDateDelivered(Timestamp dateDelivered) { this.dateDelivered = dateDelivered; }

    public Boolean getDelivered() { return delivered; }
    public void setDelivered(Boolean delivered) { this.delivered = delivered; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    public BigDecimal getGrade() { return grade; }
    public void setGrade(BigDecimal grade) { this.grade = grade; }

    public Integer getReportedHours() { return reportedHours; }
    public void setReportedHours(Integer reportedHours) { this.reportedHours = reportedHours; }
}