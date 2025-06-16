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
import professionalpractice.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FXMLDeliverDocumentController {

    @FXML private Label lblDeliveryName;
    @FXML private Label lblStartDate;
    @FXML private Label lblEndDate;
    @FXML private Label lblFileName;
    @FXML private VBox vboxDynamicFields;
    @FXML private TextField tfReportedHours;
    @FXML private TextField tfGrade;
    @FXML private TextArea taObservations;

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

        // Show dynamic fields based on delivery type
        vboxDynamicFields.setVisible(true);
        vboxDynamicFields.setManaged(true);

        switch(delivery.getDeliveryType()) {
            case "INITIAL DOCUMENT":
            case "FINAL DOCUMENT":
                tfGrade.setVisible(true);
                tfGrade.setManaged(true);
                taObservations.setVisible(true);
                taObservations.setManaged(true);
                break;
            case "REPORT":
                tfReportedHours.setVisible(true);
                tfReportedHours.setManaged(true);
                tfGrade.setVisible(true);
                tfGrade.setManaged(true);
                tfGrade.setPromptText("Calificación (ej. 8.5)"); // No es opcional
                break;
        }
    }

    @FXML
    void btnAttachFileClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Documento para " + currentDelivery.getName());
        selectedFile = fileChooser.showOpenDialog(lblFileName.getScene().getWindow());

        if (selectedFile != null) {
            lblFileName.setText(selectedFile.getName());
        }
    }

    @FXML
    void btnDeliverClick(ActionEvent event) {
        if (selectedFile == null || !validateDynamicFields()) {
            return;
        }

        try {
            File savedFile = copyFileToDeliveriesFolder(selectedFile);
            String newFilePath = savedFile.getPath();
            int newDocumentId = -1;

            switch (currentDelivery.getDeliveryType()) {
                case "INITIAL DOCUMENT":
                    newDocumentId = saveInitialDocument(newFilePath);
                    break;
                case "REPORT":
                    newDocumentId = saveReportDocument(newFilePath);
                    break;
                case "FINAL DOCUMENT":
                    newDocumentId = saveFinalDocument(newFilePath);
                    break;
                default:
                    Utils.showSimpleAlert(Alert.AlertType.ERROR, "Tipo Inválido", "El tipo de entrega no es reconocido.");
                    return;
            }

            if (newDocumentId == -1) {
                throw new SQLException("No se pudo guardar la información del documento en la BD.");
            }

            int result = ((DeliveryDAO) deliveryDAO).linkDocumentToDelivery(currentDelivery.getIdDelivery(), newDocumentId, currentDelivery.getDeliveryType());

            if (result == Constants.OPERATION_SUCCESFUL) {
                Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Operación Exitosa", "Documento '" + selectedFile.getName() + "' entregado correctamente.");
                closeWindow();
            } else {
                throw new SQLException("No se pudo enlazar el documento a la entrega.");
            }

        } catch (IOException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Archivo", "No se pudo guardar el archivo. Verifique los permisos e inténtelo de nuevo.");
            e.printStackTrace();
        } catch (SQLException | NumberFormatException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Datos", "Ocurrió un error al guardar la información: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int saveInitialDocument(String filePath) throws SQLException {
        InitialDocument doc = new InitialDocument();
        doc.setName(currentDelivery.getName());
        doc.setFilePath(filePath);
        doc.setDate(new Date());
        doc.setDelivered(true);
        doc.setStatus("EN_REVISION");
        doc.setObservations(taObservations.getText());
        if (!tfGrade.getText().trim().isEmpty()) {
            doc.setGrade(new BigDecimal(tfGrade.getText()));
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
        doc.setReportedHours(Integer.parseInt(tfReportedHours.getText()));
        doc.setGrade(new BigDecimal(tfGrade.getText()));
        return documentDAO.saveReportDocument(doc);
    }

    private int saveFinalDocument(String filePath) throws SQLException {
        FinalDocument doc = new FinalDocument();
        doc.setName(currentDelivery.getName());
        doc.setFilePath(filePath);
        doc.setDate(new Date());
        doc.setDelivered(true);
        doc.setStatus("EN_REVISION");
        doc.setObservations(taObservations.getText());
        if (!tfGrade.getText().trim().isEmpty()) {
            doc.setGrade(new BigDecimal(tfGrade.getText()));
        }
        return documentDAO.saveFinalDocument(doc);
    }

    private boolean validateDynamicFields() {
        if (selectedFile == null) {
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Archivo no seleccionado", "Debes adjuntar un archivo antes de entregar.");
            return false;
        }

        if ("REPORT".equals(currentDelivery.getDeliveryType())) {
            if (tfReportedHours.getText().trim().isEmpty() || tfGrade.getText().trim().isEmpty()) {
                Utils.showSimpleAlert(Alert.AlertType.WARNING, "Campos Obligatorios", "Para un reporte, las horas y la calificación son obligatorias.");
                return false;
            }
            try {
                Integer.parseInt(tfReportedHours.getText().trim());
                new BigDecimal(tfGrade.getText().trim());
            } catch (NumberFormatException e) {
                Utils.showSimpleAlert(Alert.AlertType.WARNING, "Datos Inválidos", "Las horas y la calificación deben ser números válidos.");
                return false;
            }
        }
        return true;
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
