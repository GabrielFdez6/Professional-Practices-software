package professionalpractice.controller.coordinator;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import professionalpractice.model.dao.DocumentDAO;
import professionalpractice.utils.Utils;
import java.util.List;

public class FXMLSelectDocumentController implements Initializable {

    @FXML
    private ListView<String> lvDocumentType;
    private String deliveryType;

    private DocumentDAO documentDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        documentDAO = new DocumentDAO();
    }

    public void initializeInformation(String deliveryType) {
        this.deliveryType = deliveryType;
        loadDocuments();
    }

    @FXML
    private void btnClickContinue(ActionEvent event) {
        String selectedDocumentName = lvDocumentType.getSelectionModel().getSelectedItem();
        if (selectedDocumentName != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/professionalpractice/view/coordinator/FXMLScheduleDeliveryDetails.fxml"));
                Parent view = loader.load();
                FXMLScheduleDeliveryDetailsController controller = loader.getController();
                controller.initializeInformation(deliveryType, selectedDocumentName);

                Stage stage = new Stage();
                stage.setTitle("Programar Entrega");
                stage.setScene(new Scene(view));
                stage.initModality(Modality.APPLICATION_MODAL);
                closeWindow();
                stage.showAndWait();
            } catch (IOException e) {
                Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo cargar la ventana de detalles de entrega.");
                e.printStackTrace();
            }
        } else {
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Selección Requerida", "Por favor, selecciona un documento para continuar.");
        }
    }

    @FXML
    private void btnClickBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/professionalpractice/view/coordinator/FXMLScheduleDelivery.fxml"));
            Parent view = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Seleccionar Tipo de Entrega");
            stage.setScene(new Scene(view));
            stage.show();
            closeWindow();
        }
        catch (IOException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo regresar a la ventana anterior.");
            e.printStackTrace();
        }
    }

    private void loadDocuments() {
        System.out.println("Cargando documentos para categoría: " + deliveryType);
        List<String> documentNames = new ArrayList<>();

        try {
            switch (deliveryType) {
                case "DOCUMENTOS INICIALES":
                    documentNames = documentDAO.getDistinctInitialDocumentNames();
                    break;
                case "REPORTES":
                    documentNames = documentDAO.getDistinctReportDocumentNames();
                    break;
                case "DOCUMENTOS FINALES":
                    documentNames = documentDAO.getDistinctFinalDocumentNames();
                    break;
                default:
                    Utils.showSimpleAlert(Alert.AlertType.WARNING, "Categoría Desconocida", "La categoría de documentos seleccionada no es válida.");
                    break;
            }
        } catch (SQLException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Base de Datos", "No se pudieron cargar los tipos de documento: " + ex.getMessage());
            ex.printStackTrace();
        }

        lvDocumentType.setItems(FXCollections.observableArrayList(documentNames));
        lvDocumentType.requestFocus();
    }

    private void closeWindow() {
        Utils.getSceneComponent(lvDocumentType).close();
    }
}