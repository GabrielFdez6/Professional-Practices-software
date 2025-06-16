package professionalpractice.model.pojo;

import java.sql.Timestamp;

public class Delivery {
    private int idDelivery;
    private int idRecord;
    private String name;
    private Timestamp startDate;
    private Timestamp endDate;
    private String deliveryType; // ENUM ('INITIAL DOCUMENT','FINAL DOCUMENT','REPORT')
    private int idInitialDocument;
    private int idFinalDocument;
    private int idReportDocument;
    private String description;

    public Delivery() {}

    public Delivery(int idDelivery, int idRecord, String name, Timestamp startDate, Timestamp endDate, String deliveryType, int idInitialDocument, int idFinalDocument, int idReportDocument, String description) {
        this.idDelivery = idDelivery;
        this.idRecord = idRecord;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.deliveryType = deliveryType;
        this.idInitialDocument = idInitialDocument;
        this.idFinalDocument = idFinalDocument;
        this.idReportDocument = idReportDocument;
        this.description = description;
    }

    public int getIdDelivery() {
        return idDelivery;
    }

    public void setIdDelivery(int idDelivery) {
        this.idDelivery = idDelivery;
    }

    public int getIdRecord() {
        return idRecord;
    }

    public void setIdRecord(int idRecord) {
        this.idRecord = idRecord;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getIdInitialDocument() {
        return idInitialDocument;
    }

    public void setIdInitialDocument(int idInitialDocument) {
        this.idInitialDocument = idInitialDocument;
    }

    public int getIdFinalDocument() {
        return idFinalDocument;
    }

    public void setIdFinalDocument(int idFinalDocument) {
        this.idFinalDocument = idFinalDocument;
    }

    public int getIdReportDocument() {
        return idReportDocument;
    }

    public void setIdReportDocument(int idReportDocument) {
        this.idReportDocument = idReportDocument;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
