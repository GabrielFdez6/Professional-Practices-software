package professionalpractice.controller.coordinator;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import professionalpractice.model.dao.DocumentDAO; // Importa tu DocumentDAO
import professionalpractice.utils.Utils;
import java.util.List;

public class FXMLSelectDocumentController implements Initializable {

    @FXML
    private ListView<String> lvTipoDocumento; // ¡Cambiado a String!
    private String tipoEntrega;

    private DocumentDAO documentDAO; // Instancia de tu DocumentDAO

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        documentDAO = new DocumentDAO(); // Inicializa el DAO
    }

    public void inicializarInformacion(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
        cargarDocumentos();
    }

    @FXML
    private void btnClicContinuar(ActionEvent event) {
        String docSeleccionadoNombre = lvTipoDocumento.getSelectionModel().getSelectedItem();
        if (docSeleccionadoNombre != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/professionalpractice/view/coordinator/FXMLScheduleDeliveryDetails.fxml"));
                Parent vista = loader.load();
                FXMLScheduleDeliveryDetailsController controller = loader.getController();
                // Pasas el tipo de entrega original y el nombre del documento seleccionado
                controller.inicializarInformacion(tipoEntrega, docSeleccionadoNombre);

                Stage escenario = new Stage();
                escenario.setTitle("Programar Entrega");
                escenario.setScene(new Scene(vista));
                escenario.initModality(Modality.APPLICATION_MODAL);
                cerrarVentana();
                escenario.showAndWait();
            } catch (IOException e) {
                Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo cargar la ventana de detalles de entrega.");
                e.printStackTrace();
            }
        } else {
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Selección Requerida", "Por favor, selecciona un documento para continuar.");
        }
    }

    @FXML
    private void BtnClicRegresar(ActionEvent event) {
        try {
            // Asegúrate de que esta ruta sea correcta para regresar
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/professionalpractice/view/coordinator/FXMLScheduleDelivery.fxml"));
            Parent vista = loader.load();
            Stage escenario = new Stage();
            escenario.setTitle("Seleccionar Tipo de Entrega");
            escenario.setScene(new Scene(vista));
            escenario.show();
            cerrarVentana();
        } catch (IOException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo regresar a la ventana anterior.");
            e.printStackTrace();
        }
    }

    private void cargarDocumentos() {
        System.out.println("Cargando documentos para categoría: " + tipoEntrega);
        List<String> nombresDocumentos = new ArrayList<>();

        try {
            switch (tipoEntrega) {
                case "DOCUMENTOS INICIALES":
                    nombresDocumentos = documentDAO.getDistinctInitialDocumentNames();
                    break;
                case "REPORTES":
                    nombresDocumentos = documentDAO.getDistinctReportDocumentNames();
                    break;
                case "DOCUMENTOS FINALES":
                    nombresDocumentos = documentDAO.getDistinctFinalDocumentNames();
                    break;
                default:
                    Utils.showSimpleAlert(Alert.AlertType.WARNING, "Categoría Desconocida", "La categoría de documentos seleccionada no es válida.");
                    break;
            }
        } catch (SQLException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Base de Datos", "No se pudieron cargar los tipos de documento: " + ex.getMessage());
            ex.printStackTrace();
        }

        lvTipoDocumento.setItems(FXCollections.observableArrayList(nombresDocumentos));
        lvTipoDocumento.requestFocus();
    }

    private void cerrarVentana() {
        Utils.getSceneComponent(lvTipoDocumento).close();
    }
}