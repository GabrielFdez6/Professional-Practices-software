package professionalpractice.model.pojo;

import java.time.LocalDateTime;

public class DeliveryInfo {
  private int idDelivery;
  private String deliveryName;
  private String deliveryType;
  private LocalDateTime dateDelivered;
  private boolean delivered;
  private String status;
  private String filePath;
  private String observations;
  private Double grade;
  private LocalDateTime startDate;
  private LocalDateTime endDate;

  public DeliveryInfo() {
  }

  public DeliveryInfo(int idDelivery, String deliveryName, String deliveryType,
      LocalDateTime dateDelivered, boolean delivered, String status,
      String filePath, String observations, Double grade,
      LocalDateTime startDate, LocalDateTime endDate) {
    this.idDelivery = idDelivery;
    this.deliveryName = deliveryName;
    this.deliveryType = deliveryType;
    this.dateDelivered = dateDelivered;
    this.delivered = delivered;
    this.status = status;
    this.filePath = filePath;
    this.observations = observations;
    this.grade = grade;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  // Getters y setters
  public int getIdDelivery() {
    return idDelivery;
  }

  public void setIdDelivery(int idDelivery) {
    this.idDelivery = idDelivery;
  }

  public String getDeliveryName() {
    return deliveryName;
  }

  public void setDeliveryName(String deliveryName) {
    this.deliveryName = deliveryName;
  }

  public String getDeliveryType() {
    return deliveryType;
  }

  public void setDeliveryType(String deliveryType) {
    this.deliveryType = deliveryType;
  }

  public LocalDateTime getDateDelivered() {
    return dateDelivered;
  }

  public void setDateDelivered(LocalDateTime dateDelivered) {
    this.dateDelivered = dateDelivered;
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

  public Double getGrade() {
    return grade;
  }

  public void setGrade(Double grade) {
    this.grade = grade;
  }

  public LocalDateTime getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDateTime startDate) {
    this.startDate = startDate;
  }

  public LocalDateTime getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDateTime endDate) {
    this.endDate = endDate;
  }

  public String getFormattedDeliveryDate() {
    if (dateDelivered != null) {
      return dateDelivered.toLocalDate().toString();
    }
    return "No entregado";
  }

  public String getFormattedEndDate() {
    if (endDate != null) {
      return endDate.toLocalDate().toString();
    }
    return "Sin fecha límite";
  }

  public String getStatusText() {
    switch (status != null ? status.toUpperCase() : "") {
      case "PENDIENTE":
        return "Pendiente";
      case "ENTREGADO":
        return "Entregado";
      case "EN_REVISION":
        return "En revisión";
      case "APROBADO":
        return "Aprobado";
      case "RECHAZADO":
        return "Rechazado";
      default:
        return status != null ? status : "Desconocido";
    }
  }

  public String getFormattedGrade() {
    if (grade != null) {
      return String.format("%.2f", grade);
    }
    return "Sin calificar";
  }

  /**
   * Obtiene el tipo de entrega traducido al español
   * 
   * @return Tipo de entrega en español
   */
  public String getDeliveryTypeInSpanish() {
    if (deliveryType == null) {
      return "Sin tipo";
    }

    switch (deliveryType.toUpperCase()) {
      case "INITIAL DOCUMENT":
        return "Documento Inicial";
      case "FINAL DOCUMENT":
        return "Documento Final";
      case "REPORT":
        return "Reporte";
      default:
        return deliveryType;
    }
  }

  @Override
  public String toString() {
    return "DeliveryInfo{" +
        "idDelivery=" + idDelivery +
        ", deliveryName='" + deliveryName + '\'' +
        ", deliveryType='" + deliveryType + '\'' +
        ", status='" + status + '\'' +
        ", delivered=" + delivered +
        ", grade=" + grade +
        '}';
  }
}