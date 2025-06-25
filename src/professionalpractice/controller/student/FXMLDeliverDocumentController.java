package professionalpractice.controller.student;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import professionalpractice.model.dao.DeliveryDAO;
import professionalpractice.model.dao.interfaces.IDeliveryDAO;
import professionalpractice.model.pojo.Delivery;
import professionalpractice.model.pojo.DeliveryDefinition;
import professionalpractice.utils.SecurityValidationUtils;
import professionalpractice.utils.Utils;
import professionalpractice.utils.ValidationUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.scene.control.Button;

public class FXMLDeliverDocumentController {

  @FXML private Label lblDeliveryName;
  @FXML private Label lblStartDate;
  @FXML private Label lblEndDate;
  @FXML private Label lblFileName;
  @FXML private AnchorPane vboxDynamicFields;
  @FXML private TextField tfReportedHours;
  @FXML private TextField tfGrade;
  @FXML private TextArea taObservations;
  @FXML private Label lblStatus;
  @FXML private Button btnAttachFile;
  @FXML private Button btnDeliver;
  @FXML private Button btnReturn;

  private Delivery currentDelivery;
  private File selectedFile;
  private IDeliveryDAO deliveryDAO;

  @FXML
  public void initialize() {
    this.deliveryDAO = (IDeliveryDAO) new DeliveryDAO();
  }

  public void initData(Delivery delivery) {
    this.currentDelivery = delivery;

    if (currentDelivery.getDeliveryDefinition() == null || currentDelivery.getDeliveryDefinition().getName() == null) {
      try {
        Delivery loadedDelivery = ((DeliveryDAO) deliveryDAO).getDeliveryById(delivery.getIdDelivery());
        if (loadedDelivery != null && loadedDelivery.getDeliveryDefinition() != null) {
          this.currentDelivery = loadedDelivery;
        } else {
          Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Carga", "No se pudo cargar la definición completa de la entrega.");
          return;
        }
      } catch (SQLException e) {
        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Carga", "No se pudo cargar la información completa de la entrega debido a un error de base de datos.");
        e.printStackTrace();
        return;
      }
    }

    lblDeliveryName.setText(currentDelivery.getDeliveryDefinition().getName());
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    lblStartDate.setText("Fecha de Inicio: " + sdf.format(currentDelivery.getDeliveryDefinition().getStartDate()));
    lblEndDate.setText("Fecha de Fin: " + sdf.format(currentDelivery.getDeliveryDefinition().getEndDate()));

    lblStatus.setText("Estado: " + currentDelivery.getStatus());

    configureUIBasedOnDeliveryType(currentDelivery.getDeliveryDefinition());

    if (currentDelivery.getFilePath() != null && !currentDelivery.getFilePath().isEmpty()) {
      lblFileName.setText(new File(currentDelivery.getFilePath()).getName());
      selectedFile = new File(currentDelivery.getFilePath());
    } else {
      lblFileName.setText("Ningún archivo seleccionado");
    }
    if (currentDelivery.getReportedHours() != null) {
      tfReportedHours.setText(String.valueOf(currentDelivery.getReportedHours()));
    }
    if (currentDelivery.getGrade() != null) {
      tfGrade.setText(currentDelivery.getGrade().toPlainString());
    }
    if (currentDelivery.getObservations() != null) {
      taObservations.setText(currentDelivery.getObservations());
    }

    checkDeliveryAvailabilityAndSetUIState();
  }

  private void configureUIBasedOnDeliveryType(DeliveryDefinition definition) {
    vboxDynamicFields.setVisible(true);
    vboxDynamicFields.setManaged(true);

    hideAllDynamicFields();

    switch (definition.getDeliveryType()) {
      case "INITIAL DOCUMENT":
      case "FINAL DOCUMENT":
        tfGrade.setVisible(false);
        tfGrade.setManaged(false);
        taObservations.setVisible(true);
        taObservations.setManaged(true);
        taObservations.setPromptText("Observaciones adicionales...");
        tfReportedHours.setVisible(false);
        tfReportedHours.setManaged(false);
        break;
      case "REPORT":
        tfReportedHours.setVisible(true);
        tfReportedHours.setManaged(true);
        tfGrade.setVisible(true);
        tfGrade.setManaged(true);
        taObservations.setVisible(true);
        taObservations.setManaged(true);
        tfReportedHours.setPromptText("Horas reportadas (requerido)");
        tfGrade.setPromptText("Calificación (ej. 8.5)");
        taObservations.setPromptText("Observaciones del reporte...");
        break;
    }
  }

  private void hideAllDynamicFields() {
    tfReportedHours.setVisible(false);
    tfReportedHours.setManaged(false);
    tfGrade.setVisible(false);
    tfGrade.setManaged(false);
    taObservations.setVisible(false);
    taObservations.setManaged(false);
  }

  @FXML
  void btnAttachFileClick(ActionEvent event) {
    if (btnAttachFile.isDisable()) {
      Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Entrega No Disponible", "No puedes adjuntar archivos en el estado actual de la entrega.");
      return;
    }

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Seleccionar Documento para " + currentDelivery.getDeliveryDefinition().getName());

    configureFileChooserFilters(fileChooser);

    selectedFile = fileChooser.showOpenDialog(lblFileName.getScene().getWindow());

    if (selectedFile != null) {
      if (validateSelectedFile(selectedFile)) {
        lblFileName.setText(selectedFile.getName());
      } else {
        selectedFile = null;
        lblFileName.setText("Ningún archivo seleccionado");
      }
    }
  }

  private void configureFileChooserFilters(FileChooser fileChooser) {
    FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("Documentos PDF", "*.pdf");
    FileChooser.ExtensionFilter docFilter = new FileChooser.ExtensionFilter("Documentos Word", "*.doc", "*.docx");
    FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("Documentos de Texto", "*.txt");

    fileChooser.getExtensionFilters().addAll(pdfFilter, docFilter, txtFilter);
    fileChooser.setSelectedExtensionFilter(pdfFilter);
  }

  private boolean validateSelectedFile(File file) {
    List<String> errors = new ArrayList<>();

    String fileName = file.getName().toLowerCase();
    if (!SecurityValidationUtils.isFileExtensionAllowed(fileName)) {
      errors.add("Tipo de archivo no permitido. Solo se permiten documentos PDF, Word y texto.");
    }

    if (!SecurityValidationUtils.isFileSizeValid(file)) {
      errors.add("El archivo excede el tamaño máximo permitido. (Ajustar mensaje si ya no hay constante MAX_FILE_SIZE)");
    }

    String fileNameError = ValidationUtils.validateFileName(fileName);
    if (!fileNameError.isEmpty()) {
      errors.add(fileNameError);
    }

    if (!file.exists() || !file.canRead()) {
      errors.add("El archivo no existe o no se puede leer.");
    }

    if (!errors.isEmpty()) {
      String allErrors = String.join("\n", errors);
      Utils.showSimpleAlert(Alert.AlertType.WARNING, "Archivo Inválido", allErrors);
      return false;
    }

    return true;
  }

  @FXML
  void btnDeliverClick(ActionEvent event) {
    if (btnDeliver.isDisable()) {
      Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Entrega No Permitida", "No puedes realizar la entrega en el estado actual de la tarea o fuera de fecha.");
      return;
    }

    if (performComprehensiveValidation()) {
      try {
        File savedFile = copyFileToDeliveriesFolder(selectedFile);
        String newFilePath = savedFile.getPath();

        String newStatus = "ENTREGADO";

        int updateResult = ((DeliveryDAO) deliveryDAO).updateStudentDeliveryStatus(
                currentDelivery.getIdDelivery(),
                newFilePath,
                new Date(),
                newStatus,
                taObservations.getText(),
                tfGrade.isVisible() && tfGrade.getText() != null && !tfGrade.getText().trim().isEmpty() ? new BigDecimal(tfGrade.getText().trim()) : null,
                (tfReportedHours.getText() != null && !tfReportedHours.getText().trim().isEmpty()) ? Integer.parseInt(tfReportedHours.getText().trim()) : null
        );

        if (updateResult > 0) {
          currentDelivery.setStatus(newStatus);
          lblStatus.setText("Estado: " + newStatus);
          Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Entrega Exitosa",
                  "El documento '" + lblFileName.getText() + "' ha sido entregado correctamente.\n" +
                          "Estado: " + newStatus);

          closeWindow();

        } else {
          throw new SQLException("No se pudo actualizar la información de la entrega en la base de datos.");
        }

      } catch (IOException e) {
        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Archivo",
                "No se pudo guardar el archivo. Verifique los permisos del sistema e inténtelo de nuevo.");
        System.err.println("Error de E/O al guardar archivo: " + e.getMessage());
        e.printStackTrace();
      } catch (SQLException e) {
        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Base de Datos",
                "No fue posible conectar con la base de datos o hubo un error en la operación." + "\nDetalle: " + e.getMessage());
        System.err.println("Error de base de datos en entrega: " + e.getMessage());
        e.printStackTrace();
      } catch (NumberFormatException e) {
        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Formato",
                "Los valores numéricos ingresados no son válidos.");
        e.printStackTrace();
      } catch (Exception e) {
        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error Inesperado",
                "Ocurrió un error inesperado durante la entrega. Intente más tarde.");
        System.err.println("Error inesperado en entrega de documento: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  private boolean performComprehensiveValidation() {
    List<String> errors = new ArrayList<>();

    if (selectedFile == null) {
      errors.add("Debe seleccionar un archivo para entregar.");
    } else if (!selectedFile.exists()) {
      errors.add("El archivo seleccionado ya no existe en el sistema.");
    }

    validateFieldsByDeliveryType(errors);
    validateDeliveryTiming(errors);
    performSecurityValidationOnTextFields(errors);

    if (!errors.isEmpty()) {
      String allErrors = String.join("\n", errors);
      Utils.showSimpleAlert(Alert.AlertType.WARNING, "Errores de Validación", allErrors);
      return false;
    }

    sanitizeAllTextFields();
    return true;
  }

  private void validateFieldsByDeliveryType(List<String> errors) {
    String deliveryType = currentDelivery.getDeliveryDefinition().getDeliveryType();

    switch (deliveryType) {
      case "REPORT":
        validateReportFields(errors);
        break;
      case "INITIAL DOCUMENT":
      case "FINAL DOCUMENT":
        validateDocumentFields(errors);
        break;
      default:
        errors.add("Tipo de entrega no reconocido: " + deliveryType);
    }
  }

  private void validateReportFields(List<String> errors) {
    String hoursText = tfReportedHours.getText();
    if (hoursText == null || hoursText.trim().isEmpty()) {
      errors.add("Las horas reportadas son obligatorias para un reporte.");
    } else {
      String hoursError = ValidationUtils.validateReportedHours(hoursText);
      if (!hoursError.isEmpty()) {
        errors.add(hoursError);
      }
    }

    String gradeText = tfGrade.getText();
    if (gradeText == null || gradeText.trim().isEmpty()) {
      errors.add("La calificación es obligatoria para un reporte.");
    } else {
      String gradeError = ValidationUtils.validateGrade(gradeText);
      if (!gradeError.isEmpty()) {
        errors.add(gradeError);
      }
    }
  }

  private void validateDocumentFields(List<String> errors) {
    String gradeText = tfGrade.getText();
    if (tfGrade.isVisible() && gradeText != null && !gradeText.trim().isEmpty()) {
      String gradeError = ValidationUtils.validateGrade(gradeText);
      if (!gradeError.isEmpty()) {
        errors.add(gradeError);
      }
    }

    String observations = taObservations.getText();
    if (observations != null && !observations.trim().isEmpty()) {
      String observationsError = ValidationUtils.validateObservations(observations);
      if (!observationsError.isEmpty()) {
        errors.add(observationsError);
      }
    }
  }

  private void validateDeliveryTiming(List<String> errors) {
    Date now = new Date();
    Date startDate = currentDelivery.getDeliveryDefinition().getStartDate();
    Date endDate = currentDelivery.getDeliveryDefinition().getEndDate();

    if (now.before(startDate)) {
      errors.add("La entrega aún no está disponible. Fecha de inicio: " +
              new SimpleDateFormat("dd-MM-yyyy HH:mm").format(startDate));
    }

    if (now.after(endDate)) {
      errors.add("La fecha límite de entrega ha expirado. Fecha límite: " +
              new SimpleDateFormat("dd-MM-yyyy HH:mm").format(endDate));
    }
  }

  private void performSecurityValidationOnTextFields(List<String> errors) {
    String observations = taObservations.getText();
    if (observations != null && !observations.isEmpty()) {
      List<String> securityErrors = SecurityValidationUtils.performComprehensiveSecurityValidation(
              observations, "Observaciones");
      errors.addAll(securityErrors);
    }

    String hoursText = tfReportedHours.getText();
    if (tfReportedHours.isVisible() && hoursText != null && !hoursText.isEmpty()) {
      if (SecurityValidationUtils.containsSQLInjection(hoursText)) {
        errors.add("El campo de horas contiene caracteres no permitidos.");
      }
    }

    String gradeText = tfGrade.getText();
    if (tfGrade.isVisible() && gradeText != null && !gradeText.isEmpty()) {
      if (SecurityValidationUtils.containsSQLInjection(gradeText)) {
        errors.add("El campo de calificación contiene caracteres no permitidos.");
      }
    }
  }

  private void sanitizeAllTextFields() {
    if (taObservations.getText() != null) {
      taObservations.setText(SecurityValidationUtils.advancedSanitization(taObservations.getText()));
    }
    if (tfReportedHours.isVisible() && tfReportedHours.getText() != null) {
      tfReportedHours.setText(ValidationUtils.sanitizeInput(tfReportedHours.getText()));
    }
    if (tfGrade.isVisible() && tfGrade.getText() != null) {
      tfGrade.setText(ValidationUtils.sanitizeInput(tfGrade.getText()));
    }
  }

  private int updateDeliveryInstanceForStudent(String filePath) throws SQLException {
    Date deliveryDate = new Date();
    String status = "ENTREGADO";
    String observations = taObservations.getText();
    BigDecimal grade = null;
    if (tfGrade.getText() != null && !tfGrade.getText().trim().isEmpty()) {
      grade = new BigDecimal(tfGrade.getText().trim());
    }
    Integer reportedHours = null;
    if (tfReportedHours.getText() != null && !tfReportedHours.getText().trim().isEmpty()) {
      reportedHours = Integer.parseInt(tfReportedHours.getText().trim());
    }

    return ((DeliveryDAO) deliveryDAO).updateStudentDeliveryStatus(
            currentDelivery.getIdDelivery(),
            filePath,
            deliveryDate,
            status,
            observations,
            grade,
            reportedHours
    );
  }

  private File copyFileToDeliveriesFolder(File sourceFile) throws IOException {
    String directoryName = currentDelivery.getDeliveryDefinition().getDeliveryType().replace(" ", "_");
    String directoryPathStr = "deliveries/" + directoryName;
    Path directoryPath = Paths.get(directoryPathStr);
    if (Files.notExists(directoryPath)) {
      Files.createDirectories(directoryPath);
    }
    String uniqueFileName = System.currentTimeMillis() + "_" + sourceFile.getName();
    Path destinationPath = directoryPath.resolve(uniqueFileName);
    Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
    return destinationPath.toFile();
  }

  @FXML
  private void btnClickReturn(ActionEvent event) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/professionalpractice/view/student/FXMLDeliveryList.fxml"));
      Parent view = loader.load();

      closeWindow();
    } catch (IOException e) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo regresar a la ventana anterior.");
      e.printStackTrace();
    }
  }

  private void showSuccessAndClose() {
    Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Entrega Exitosa",
            "El documento '" + lblFileName.getText() + "' ha sido entregado correctamente.\n" +
                    "Estado: En revisión");
    closeWindow();
  }

  private void closeWindow() {
    Stage stage = (Stage) lblFileName.getScene().getWindow();
    stage.close();
  }

  private void checkDeliveryAvailabilityAndSetUIState() {
    Date currentDate = new Date();
    Date endDate = currentDelivery.getDeliveryDefinition().getEndDate();
    String currentStatus = currentDelivery.getStatus();

    boolean enableDeliveryUI = false;
    String message = "";

    tfReportedHours.setDisable(false);
    tfGrade.setDisable(false);
    taObservations.setDisable(false);
    btnAttachFile.setDisable(false);
    btnDeliver.setDisable(false);
    btnReturn.setDisable(false);

    if (currentDate.after(endDate)) {
      message = "La fecha límite de entrega ha expirado (" + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(endDate) + "). No se pueden realizar entregas.";
      enableDeliveryUI = false;
    } else {
      switch (currentStatus) {
        case "PENDIENTE":
          enableDeliveryUI = true;
          break;
        case "ENTREGADO":
          enableDeliveryUI = true;
          message = "Ya entregaste una versión, puedes subir una nueva.";
          break;
        case "RECHAZADO":
          enableDeliveryUI = true;
          message = "Tu entrega fue rechazada. Puedes subir una nueva versión.";
          break;
        case "EN_REVISION":
          message = "Tu entrega está actualmente en revisión. No se puede subir una nueva versión hasta que sea evaluada.";
          enableDeliveryUI = false;
          break;
        case "APROBADO":
          message = "Tu entrega ha sido aprobada. No se puede subir una nueva versión.";
          enableDeliveryUI = false;
          break;
        default:
          message = "Estado de entrega desconocido. Contacta a soporte.";
          enableDeliveryUI = false;
          break;
      }
    }

    if (!enableDeliveryUI) {
      tfReportedHours.setDisable(true);
      tfGrade.setDisable(true);
      taObservations.setDisable(true);
      btnAttachFile.setDisable(true);
      btnDeliver.setDisable(true);
      if (!message.isEmpty()) {
        Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Entrega No Disponible", message);
      }
    }
  }

}