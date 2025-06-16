package professionalpractice.controller.student;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
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
        if (selectedFile == null) {
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Archivo no seleccionado", "Debes adjuntar un archivo antes de entregar.");
            return;
        }

        try {
            // 1. Physically copy the file to a server directory.
            File savedFile = copyFileToDeliveriesFolder(selectedFile);
            String newFilePath = savedFile.getPath();
            int newDocumentId = -1;

            // 2. Connect to a DAO to save the file path and update document status.
            switch (currentDelivery.getDeliveryType()) {
                case "INITIAL DOCUMENT":
                    InitialDocument initialDoc = new InitialDocument();
                    initialDoc.setName(currentDelivery.getName());
                    initialDoc.setFilePath(newFilePath);
                    newDocumentId = documentDAO.saveInitialDocument(initialDoc);
                    break;
                case "REPORT":
                    ReportDocument reportDoc = new ReportDocument();
                    reportDoc.setName(currentDelivery.getName());
                    reportDoc.setFilePath(newFilePath);
                    reportDoc.setDate(new Date());
                    reportDoc.setDelivered(true);
                    reportDoc.setStatus("EN_REVISION");
                    // These would be filled from a form in a more complex scenario
                    reportDoc.setReportedHours(0);
                    reportDoc.setGrade(null);
                    newDocumentId = documentDAO.saveReportDocument(reportDoc);
                    break;
                case "FINAL DOCUMENT":
                    FinalDocument finalDoc = new FinalDocument();
                    finalDoc.setName(currentDelivery.getName());
                    finalDoc.setFilePath(newFilePath);
                    newDocumentId = documentDAO.saveFinalDocument(finalDoc);
                    break;
                default:
                    Utils.showSimpleAlert(Alert.AlertType.ERROR, "Tipo Inválido", "El tipo de entrega no es reconocido.");
                    return;
            }

            if (newDocumentId == -1) {
                throw new SQLException("No se pudo guardar la información del documento en la base de datos.");
            }

            // 3. Update the 'delivery' table to link the new document ID.
            int result = ((DeliveryDAO) deliveryDAO).linkDocumentToDelivery(currentDelivery.getIdDelivery(), newDocumentId, currentDelivery.getDeliveryType());

            if (result == Constants.OPERATION_SUCCESFUL) {
                Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Operación Exitosa", "Documento '" + selectedFile.getName() + "' entregado correctamente.");
                closeWindow();
            } else {
                throw new SQLException("No se pudo enlazar el documento a la entrega.");
            }

        } catch (IOException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Archivo", "No se pudo guardar el archivo en el servidor. Inténtalo de nuevo.");
            e.printStackTrace();
        } catch (SQLException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Base de Datos", "Ocurrió un error al guardar la información: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private File copyFileToDeliveriesFolder(File sourceFile) throws IOException {
        // Creates a directory structure like "deliveries/INITIAL DOCUMENT/"
        String directoryPathStr = "deliveries/" + currentDelivery.getDeliveryType().replace(" ", "_");
        Path directoryPath = Paths.get(directoryPathStr);
        if (Files.notExists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        // Creates a unique filename to avoid conflicts, e.g., "162381238_Reporte.pdf"
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
