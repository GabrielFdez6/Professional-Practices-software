package professionalpractice.controller.coordinator;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import professionalpractice.model.dao.DeliveryDAO;
import professionalpractice.model.dao.interfaces.IDeliveryDAO;
import professionalpractice.model.pojo.Delivery;
import professionalpractice.utils.Constants;
import professionalpractice.utils.SecurityValidationUtils;
import professionalpractice.utils.Utils;
import professionalpractice.utils.ValidationUtils;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLScheduleDeliveryController implements Initializable {

  @FXML
  private TextField tfDeliveryName;
  @FXML
  private ComboBox<String> cmbDeliveryType;
  @FXML
  private DatePicker dpStartDate;
  @FXML
  private DatePicker dpEndDate;
  @FXML
  private TextField tfDescription;
  @FXML
  private Button btnAccept;
  @FXML
  private Button btnCancel;

  private IDeliveryDAO deliveryDAO;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    deliveryDAO = new DeliveryDAO();
    setupDeliveryTypeComboBox();
    setupDefaultDates();
  }

  private void setupDeliveryTypeComboBox() {
    cmbDeliveryType.setItems(FXCollections.observableArrayList(
        "INITIAL DOCUMENT",
        "REPORT",
        "FINAL DOCUMENT"));
    cmbDeliveryType.setValue("INITIAL DOCUMENT"); // Valor por defecto
  }

  private void setupDefaultDates() {
    // Establecer fecha de inicio como mañana por defecto
    LocalDate tomorrow = LocalDate.now().plusDays(1);
    dpStartDate.setValue(tomorrow);

    // Establecer fecha de fin como una semana después por defecto
    LocalDate weekLater = tomorrow.plusWeeks(1);
    dpEndDate.setValue(weekLater);
  }

  @FXML
  void btnAcceptClick(ActionEvent event) {
    if (performComprehensiveValidation()) {
      try {
        Delivery delivery = createDeliveryFromForm();

        int result = deliveryDAO.scheduleDeliveryForAllRecords(delivery);
        if (result == Constants.OPERATION_SUCCESFUL) {
          Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Operación Exitosa",
              Constants.SUCCESS_RECORD_CREATED +
                  "\nLa entrega ha sido programada para todos los estudiantes activos.");
          closeWindow();
        } else {
          Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error en la Operación",
              "No se pudo programar la entrega. Verifique los datos e intente nuevamente.");
        }

      } catch (SQLException e) {
        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Base de Datos",
            Constants.ERROR_DATABASE_CONNECTION);
        System.err.println("Error de base de datos al programar entrega: " + e.getMessage());
        e.printStackTrace();
      } catch (Exception e) {
        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error Inesperado",
            "Ocurrió un error inesperado al programar la entrega.");
        System.err.println("Error inesperado al programar entrega: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  @FXML
  void btnCancelClick(ActionEvent event) {
    if (Utils.showConfirmationAlert("Cancelar Programación",
        "¿Estás seguro que quieres cancelar la programación de esta entrega?\n" +
            "Se perderán todos los datos ingresados.")) {
      closeWindow();
    }
  }

  private boolean performComprehensiveValidation() {
    List<String> errors = new ArrayList<>();

    // Validar todos los campos obligatorios
    validateRequiredFields(errors);

    // Validar contenido de los campos
    validateFieldContent(errors);

    // Validar fechas y lógica temporal
    validateDateLogic(errors);

    // Validaciones de seguridad
    performSecurityValidations(errors);

    // Mostrar errores si los hay
    if (!errors.isEmpty()) {
      String allErrors = String.join("\n", errors);
      Utils.showSimpleAlert(Alert.AlertType.WARNING, "Errores de Validación", allErrors);
      return false;
    }

    // Sanitizar campos antes de proceder
    sanitizeInputFields();

    return true;
  }

  private void validateRequiredFields(List<String> errors) {
    if (tfDeliveryName.getText() == null || tfDeliveryName.getText().trim().isEmpty()) {
      errors.add("El nombre de la entrega es obligatorio.");
    }

    if (cmbDeliveryType.getValue() == null) {
      errors.add("Debe seleccionar un tipo de entrega.");
    }

    if (dpStartDate.getValue() == null) {
      errors.add("La fecha de inicio es obligatoria.");
    }

    if (dpEndDate.getValue() == null) {
      errors.add("La fecha de fin es obligatoria.");
    }
  }

  private void validateFieldContent(List<String> errors) {
    // Validar nombre de la entrega
    String deliveryName = tfDeliveryName.getText();
    if (deliveryName != null && !deliveryName.trim().isEmpty()) {
      String nameError = ValidationUtils.validateDeliveryName(deliveryName);
      if (!nameError.isEmpty()) {
        errors.add(nameError);
      }
    }

    // Validar descripción (campo opcional)
    String description = tfDescription.getText();
    if (description != null && !description.trim().isEmpty()) {
      String descriptionError = ValidationUtils.validateDescription(description);
      if (!descriptionError.isEmpty()) {
        errors.add(descriptionError);
      }
    }

    // Validar tipo de entrega
    String deliveryType = cmbDeliveryType.getValue();
    if (deliveryType != null) {
      String typeError = ValidationUtils.validateDeliveryType(deliveryType);
      if (!typeError.isEmpty()) {
        errors.add(typeError);
      }
    }
  }

  private void validateDateLogic(List<String> errors) {
    LocalDate startDate = dpStartDate.getValue();
    LocalDate endDate = dpEndDate.getValue();
    LocalDate today = LocalDate.now();

    if (startDate != null && endDate != null) {
      // Validar que la fecha de fin no sea anterior a la de inicio
      if (endDate.isBefore(startDate)) {
        errors.add("La fecha de fin no puede ser anterior a la fecha de inicio.");
      }

      // Validar que las fechas no sean en el pasado (con un día de gracia)
      if (startDate.isBefore(today)) {
        errors.add("La fecha de inicio no puede ser en el pasado.");
      }

      // Validar que haya tiempo suficiente para la entrega (al menos 1 día)
      if (startDate.equals(endDate)) {
        errors.add("Debe haber al menos un día de diferencia entre la fecha de inicio y fin.");
      }

      // Validar que el periodo no sea excesivamente largo
      long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
      if (daysBetween > Constants.MAX_DELIVERY_PERIOD_DAYS) {
        errors.add("El periodo de entrega no puede exceder " + Constants.MAX_DELIVERY_PERIOD_DAYS + " días.");
      }

      // Validar fechas razonables (no más de un año en el futuro)
      if (startDate.isAfter(today.plusYears(1))) {
        errors.add("La fecha de inicio no puede ser más de un año en el futuro.");
      }
    }
  }

  private void performSecurityValidations(List<String> errors) {
    // Validaciones de seguridad en el nombre de la entrega
    String deliveryName = tfDeliveryName.getText();
    if (deliveryName != null && !deliveryName.isEmpty()) {
      List<String> securityErrors = SecurityValidationUtils.performComprehensiveSecurityValidation(
          deliveryName, "Nombre de la entrega");
      errors.addAll(securityErrors);
    }

    // Validaciones de seguridad en la descripción
    String description = tfDescription.getText();
    if (description != null && !description.isEmpty()) {
      List<String> securityErrors = SecurityValidationUtils.performComprehensiveSecurityValidation(
          description, "Descripción");
      errors.addAll(securityErrors);
    }

    // Validar que el tipo de entrega no haya sido manipulado
    String deliveryType = cmbDeliveryType.getValue();
    if (deliveryType != null && !isValidDeliveryType(deliveryType)) {
      errors.add("Tipo de entrega no válido detectado. Posible manipulación de datos.");
    }
  }

  private boolean isValidDeliveryType(String type) {
    List<String> validTypes = new ArrayList<>();
    validTypes.add("INITIAL DOCUMENT");
    validTypes.add("REPORT");
    validTypes.add("FINAL DOCUMENT");
    return validTypes.contains(type);
  }

  private void sanitizeInputFields() {
    // Sanitizar nombre de la entrega
    if (tfDeliveryName.getText() != null) {
      tfDeliveryName.setText(SecurityValidationUtils.advancedSanitization(tfDeliveryName.getText()));
    }

    // Sanitizar descripción
    if (tfDescription.getText() != null) {
      tfDescription.setText(SecurityValidationUtils.advancedSanitization(tfDescription.getText()));
    }
  }

  private Delivery createDeliveryFromForm() {
    Delivery delivery = new Delivery();

    delivery.setName(tfDeliveryName.getText().trim());
    delivery.setDeliveryType(cmbDeliveryType.getValue());

    LocalDate startDate = dpStartDate.getValue();
    LocalDate endDate = dpEndDate.getValue();

    // Establecer horas específicas para las fechas
    delivery.setStartDate(Timestamp.valueOf(startDate.atStartOfDay()));
    delivery.setEndDate(Timestamp.valueOf(endDate.atTime(23, 59, 59)));

    String description = tfDescription.getText();
    if (description != null && !description.trim().isEmpty()) {
      delivery.setDescription(description.trim());
    } else {
      delivery.setDescription("Entrega programada automáticamente - " + delivery.getDeliveryType());
    }

    return delivery;
  }

  private void closeWindow() {
    Stage stage = (Stage) btnCancel.getScene().getWindow();
    stage.close();
  }
}