package professionalpractice.controller.student;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import professionalpractice.model.dao.DeliveryDAO;
import professionalpractice.model.dao.DocumentDAO;
import professionalpractice.model.dao.interfaces.IDeliveryDAO;
import professionalpractice.model.dao.interfaces.IDocumentDAO;
import professionalpractice.model.pojo.Delivery;
import professionalpractice.model.pojo.FinalDocument;
import professionalpractice.model.pojo.InitialDocument;
import professionalpractice.model.pojo.ReportDocument;
import professionalpractice.utils.Constants;
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

public class FXMLDeliverDocumentController {

  @FXML
  private Label lblDeliveryName;
  @FXML
  private Label lblStartDate;
  @FXML
  private Label lblEndDate;
  @FXML
  private Label lblFileName;
  @FXML
  private VBox vboxDynamicFields;
  @FXML
  private TextField tfReportedHours;
  @FXML
  private TextField tfGrade;
  @FXML
  private TextArea taObservations;

  private Delivery currentDelivery;
  private File selectedFile;
  private IDocumentDAO documentDAO;
  private IDeliveryDAO deliveryDAO;

  @FXML
  public void initialize() {
    this.documentDAO = new DocumentDAO();
    this.deliveryDAO = new DeliveryDAO();
  }

  public void initData(Delivery delivery) {
    this.currentDelivery = delivery;
    lblDeliveryName.setText(delivery.getName());
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    lblStartDate.setText("Fecha de Inicio: " + sdf.format(delivery.getStartDate()));
    lblEndDate.setText("Fecha de Fin: " + sdf.format(delivery.getEndDate()));

    configureUIBasedOnDeliveryType(delivery);
  }

  private void configureUIBasedOnDeliveryType(Delivery delivery) {
    vboxDynamicFields.setVisible(true);
    vboxDynamicFields.setManaged(true);

    // Ocultar todos los campos primero
    hideAllDynamicFields();

    switch (delivery.getDeliveryType()) {
      case "INITIAL DOCUMENT":
      case "FINAL DOCUMENT":
        tfGrade.setVisible(true);
        tfGrade.setManaged(true);
        taObservations.setVisible(true);
        taObservations.setManaged(true);
        tfGrade.setPromptText("Calificación (opcional)");
        taObservations.setPromptText("Observaciones adicionales...");
        break;
      case "REPORT":
        tfReportedHours.setVisible(true);
        tfReportedHours.setManaged(true);
        tfGrade.setVisible(true);
        tfGrade.setManaged(true);
        tfReportedHours.setPromptText("Horas reportadas (requerido)");
        tfGrade.setPromptText("Calificación (ej. 8.5)");
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
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Seleccionar Documento para " + currentDelivery.getName());

    // Configurar filtros de archivos seguros
    configureFileChooserFilters(fileChooser);

    selectedFile = fileChooser.showOpenDialog(lblFileName.getScene().getWindow());

    if (selectedFile != null) {
      // Validar archivo seleccionado antes de aceptarlo
      if (validateSelectedFile(selectedFile)) {
        lblFileName.setText(selectedFile.getName());
      } else {
        selectedFile = null;
        lblFileName.setText("Ningún archivo seleccionado");
      }
    }
  }

  private void configureFileChooserFilters(FileChooser fileChooser) {
    // Filtros para archivos seguros comúnmente usados en documentos académicos
    FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("Documentos PDF", "*.pdf");
    FileChooser.ExtensionFilter docFilter = new FileChooser.ExtensionFilter("Documentos Word", "*.doc", "*.docx");
    FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("Documentos de Texto", "*.txt");

    fileChooser.getExtensionFilters().addAll(pdfFilter, docFilter, txtFilter);
    fileChooser.setSelectedExtensionFilter(pdfFilter); // PDF por defecto
  }

  private boolean validateSelectedFile(File file) {
    List<String> errors = new ArrayList<>();

    // Validar extensión del archivo
    String fileName = file.getName().toLowerCase();
    if (!SecurityValidationUtils.isFileExtensionAllowed(fileName)) {
      errors.add("Tipo de archivo no permitido. Solo se permiten documentos PDF, Word y texto.");
    }

    // Validar tamaño del archivo
    if (!SecurityValidationUtils.isFileSizeValid(file)) {
      errors.add("El archivo excede el tamaño máximo permitido (" + Constants.MAX_FILE_SIZE + " MB).");
    }

    // Validar nombre del archivo
    String fileNameError = ValidationUtils.validateFileName(fileName);
    if (!fileNameError.isEmpty()) {
      errors.add(fileNameError);
    }

    // Validar que el archivo existe y es legible
    if (!file.exists() || !file.canRead()) {
      errors.add("El archivo no existe o no se puede leer.");
    }

    // Validaciones de seguridad específicas
    if (SecurityValidationUtils.containsSuspiciousContent(fileName)) {
      errors.add("El nombre del archivo contiene caracteres o patrones sospechosos.");
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
    if (performComprehensiveValidation()) {
      try {
        File savedFile = copyFileToDeliveriesFolder(selectedFile);
        String newFilePath = savedFile.getPath();
        int newDocumentId = saveDocumentBasedOnType(newFilePath);

        if (newDocumentId != -1) {
          linkDocumentToDelivery(newDocumentId);
          showSuccessAndClose();
        } else {
          throw new SQLException("No se pudo guardar la información del documento en la base de datos.");
        }

      } catch (IOException e) {
        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Archivo",
            "No se pudo guardar el archivo. Verifique los permisos del sistema e inténtelo de nuevo.");
        System.err.println("Error de E/O al guardar archivo: " + e.getMessage());
        e.printStackTrace();
      } catch (SQLException e) {
        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Base de Datos",
            Constants.ERROR_DATABASE_CONNECTION + "\nDetalle: " + e.getMessage());
        System.err.println("Error de base de datos en entrega: " + e.getMessage());
        e.printStackTrace();
      } catch (NumberFormatException e) {
        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Formato",
            "Los valores numéricos ingresados no son válidos.");
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

    // Validar archivo seleccionado
    if (selectedFile == null) {
      errors.add("Debe seleccionar un archivo para entregar.");
    } else if (!selectedFile.exists()) {
      errors.add("El archivo seleccionado ya no existe en el sistema.");
    }

    // Validar campos específicos según el tipo de entrega
    validateFieldsByDeliveryType(errors);

    // Validar fechas de entrega
    validateDeliveryTiming(errors);

    // Validaciones de seguridad en todos los campos de texto
    performSecurityValidationOnTextFields(errors);

    // Mostrar errores si los hay
    if (!errors.isEmpty()) {
      String allErrors = String.join("\n", errors);
      Utils.showSimpleAlert(Alert.AlertType.WARNING, "Errores de Validación", allErrors);
      return false;
    }

    // Sanitizar campos antes de proceder
    sanitizeAllTextFields();

    return true;
  }

  private void validateFieldsByDeliveryType(List<String> errors) {
    String deliveryType = currentDelivery.getDeliveryType();

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
    // Validar horas reportadas (obligatorio para reportes)
    String hoursText = tfReportedHours.getText();
    if (hoursText == null || hoursText.trim().isEmpty()) {
      errors.add("Las horas reportadas son obligatorias para un reporte.");
    } else {
      String hoursError = ValidationUtils.validateReportedHours(hoursText);
      if (!hoursError.isEmpty()) {
        errors.add(hoursError);
      }
    }

    // Validar calificación (obligatorio para reportes)
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
    // Para documentos iniciales y finales, la calificación es opcional
    String gradeText = tfGrade.getText();
    if (gradeText != null && !gradeText.trim().isEmpty()) {
      String gradeError = ValidationUtils.validateGrade(gradeText);
      if (!gradeError.isEmpty()) {
        errors.add(gradeError);
      }
    }

    // Validar observaciones si están presentes
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
    Date startDate = currentDelivery.getStartDate();
    Date endDate = currentDelivery.getEndDate();

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
    // Validar observaciones
    String observations = taObservations.getText();
    if (observations != null && !observations.isEmpty()) {
      List<String> securityErrors = SecurityValidationUtils.performComprehensiveSecurityValidation(
          observations, "Observaciones");
      errors.addAll(securityErrors);
    }

    // Validar campos numéricos contra inyección
    String hoursText = tfReportedHours.getText();
    if (hoursText != null && !hoursText.isEmpty()) {
      if (SecurityValidationUtils.containsSQLInjection(hoursText)) {
        errors.add("El campo de horas contiene caracteres no permitidos.");
      }
    }

    String gradeText = tfGrade.getText();
    if (gradeText != null && !gradeText.isEmpty()) {
      if (SecurityValidationUtils.containsSQLInjection(gradeText)) {
        errors.add("El campo de calificación contiene caracteres no permitidos.");
      }
    }
  }

  private void sanitizeAllTextFields() {
    if (taObservations.getText() != null) {
      taObservations.setText(SecurityValidationUtils.advancedSanitization(taObservations.getText()));
    }
    if (tfReportedHours.getText() != null) {
      tfReportedHours.setText(ValidationUtils.sanitizeInput(tfReportedHours.getText()));
    }
    if (tfGrade.getText() != null) {
      tfGrade.setText(ValidationUtils.sanitizeInput(tfGrade.getText()));
    }
  }

  private int saveDocumentBasedOnType(String filePath) throws SQLException {
    switch (currentDelivery.getDeliveryType()) {
      case "INITIAL DOCUMENT":
        return saveInitialDocument(filePath);
      case "REPORT":
        return saveReportDocument(filePath);
      case "FINAL DOCUMENT":
        return saveFinalDocument(filePath);
      default:
        throw new IllegalArgumentException("Tipo de entrega no válido: " + currentDelivery.getDeliveryType());
    }
  }

  private void linkDocumentToDelivery(int documentId) throws SQLException {
    int result = ((DeliveryDAO) deliveryDAO).linkDocumentToDelivery(
        currentDelivery.getIdDelivery(),
        documentId,
        currentDelivery.getDeliveryType());

    if (result != Constants.OPERATION_SUCCESFUL) {
      throw new SQLException("No se pudo establecer la relación entre el documento y la entrega.");
    }
  }

  private void showSuccessAndClose() {
    Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Entrega Exitosa",
        "El documento '" + selectedFile.getName() + "' ha sido entregado correctamente.\n" +
            "Estado: En revisión");
    closeWindow();
  }

  private int saveInitialDocument(String filePath) throws SQLException {
    InitialDocument doc = new InitialDocument();
    doc.setName(currentDelivery.getName());
    doc.setFilePath(filePath);
    doc.setDate(new Date());
    doc.setDelivered(true);
    doc.setStatus("EN_REVISION");

    String observations = taObservations.getText();
    if (observations != null && !observations.trim().isEmpty()) {
      doc.setObservations(observations.trim());
    }

    String gradeText = tfGrade.getText();
    if (gradeText != null && !gradeText.trim().isEmpty()) {
      doc.setGrade(new BigDecimal(gradeText.trim()));
    }

    return documentDAO.saveInitialDocument(doc);
  }

  private int saveReportDocument(String filePath) throws SQLException {
    ReportDocument doc = new ReportDocument();
    doc.setName(currentDelivery.getName());
    doc.setFilePath(filePath);
    doc.setDate(new Date());
    doc.setDelivered(true);
    doc.setStatus("EN_REVISION");
    doc.setReportedHours(Integer.parseInt(tfReportedHours.getText().trim()));
    doc.setGrade(new BigDecimal(tfGrade.getText().trim()));
    return documentDAO.saveReportDocument(doc);
  }

  private int saveFinalDocument(String filePath) throws SQLException {
    FinalDocument doc = new FinalDocument();
    doc.setName(currentDelivery.getName());
    doc.setFilePath(filePath);
    doc.setDate(new Date());
    doc.setDelivered(true);
    doc.setStatus("EN_REVISION");

    String observations = taObservations.getText();
    if (observations != null && !observations.trim().isEmpty()) {
      doc.setObservations(observations.trim());
    }

    String gradeText = tfGrade.getText();
    if (gradeText != null && !gradeText.trim().isEmpty()) {
      doc.setGrade(new BigDecimal(gradeText.trim()));
    }

    return documentDAO.saveFinalDocument(doc);
  }

  private File copyFileToDeliveriesFolder(File sourceFile) throws IOException {
    String directoryPathStr = "deliveries/" + currentDelivery.getDeliveryType().replace(" ", "_");
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
  void btnCancelClick(ActionEvent event) {
    if (Utils.showConfirmationAlert("Cancelar Proceso", "¿Estás seguro que quieres cancelar la entrega?")) {
      closeWindow();
    }
  }

  private void closeWindow() {
    Stage stage = (Stage) lblFileName.getScene().getWindow();
    stage.close();
  }
}
