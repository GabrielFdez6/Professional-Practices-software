package professionalpractice.model.pojo;

import java.sql.Timestamp;

public class DeliveryDefinition {
    private int idDeliveryDefinition;
    private String name;
    private String description;
    private Timestamp startDate;
    private Timestamp endDate;
    private String deliveryType;
    private Integer idInitialDocumentTemplate;
    private Integer idFinalDocumentTemplate;
    private Integer idReportDocumentTemplate;
    private Integer idTerm;
    private Integer idSubjectGroup;

    public DeliveryDefinition() {
    }

    public int getIdDeliveryDefinition() {
        return idDeliveryDefinition;
    }

    public void setIdDeliveryDefinition(int idDeliveryDefinition) {
        this.idDeliveryDefinition = idDeliveryDefinition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public Integer getIdInitialDocumentTemplate() {
        return idInitialDocumentTemplate;
    }

    public void setIdInitialDocumentTemplate(Integer idInitialDocumentTemplate) {
        this.idInitialDocumentTemplate = idInitialDocumentTemplate;
    }

    public Integer getIdFinalDocumentTemplate() {
        return idFinalDocumentTemplate;
    }

    public void setIdFinalDocumentTemplate(Integer idFinalDocumentTemplate) {
        this.idFinalDocumentTemplate = idFinalDocumentTemplate;
    }

    public Integer getIdReportDocumentTemplate() {
        return idReportDocumentTemplate;
    }

    public void setIdReportDocumentTemplate(Integer idReportDocumentTemplate) {
        this.idReportDocumentTemplate = idReportDocumentTemplate;
    }

    public Integer getIdTerm() {
        return idTerm;
    }

    public void setIdTerm(Integer idTerm) {
        this.idTerm = idTerm;
    }

}