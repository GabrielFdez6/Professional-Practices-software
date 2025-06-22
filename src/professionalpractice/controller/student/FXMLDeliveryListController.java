package professionalpractice.controller.student;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import professionalpractice.model.dao.DeliveryDAO;
import professionalpractice.model.dao.interfaces.IDeliveryDAO;
import professionalpractice.model.pojo.Delivery;
import professionalpractice.model.pojo.DeliveryDefinition; // Asegúrate de importar DeliveryDefinition
import professionalpractice.utils.Utils;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List; // Necesario para List<Delivery>

public class FXMLDeliveryListController {

    @FXML private TableView<Delivery> tvDeliveries;
    @FXML private TableColumn<Delivery, String> colName;
    @FXML private TableColumn<Delivery, String> colType;
    @FXML private TableColumn<Delivery, String> colEndDate;

    private ObservableList<Delivery> deliveries;
    private IDeliveryDAO deliveryDAO;

    @FXML
    public void initialize() {
        this.deliveries = FXCollections.observableArrayList();
        this.deliveryDAO = (IDeliveryDAO) new DeliveryDAO();
        configureTable();
    }

    public void initData(int idRecord) {
        loadDeliveriesData(idRecord);
    }

    private void configureTable() {
        // --- Cambios aquí: Acceder a DeliveryDefinition para los datos ---
        colName.setCellValueFactory(cellData -> {
            // Asegurarse de que deliveryDefinition no sea null
            DeliveryDefinition definition = cellData.getValue().getDeliveryDefinition();
            return new SimpleStringProperty(definition != null ? definition.getName() : "N/A");
        });
        colType.setCellValueFactory(cellData -> {
            DeliveryDefinition definition = cellData.getValue().getDeliveryDefinition();
            return new SimpleStringProperty(definition != null ? definition.getDeliveryType() : "N/A");
        });
        colEndDate.setCellValueFactory(cellData -> {
            DeliveryDefinition definition = cellData.getValue().getDeliveryDefinition();
            if (definition != null && definition.getEndDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                return new SimpleStringProperty(sdf.format(definition.getEndDate()));
            }
            return new SimpleStringProperty("N/A");
        });
        // --- Fin de cambios ---

        tvDeliveries.setItems(deliveries);
    }

    private void loadDeliveriesData(int idRecord) {
        try {
            // El método getDeliveriesByRecord en DeliveryDAO debe cargar DeliveryDefinition
            List<Delivery> loadedList = deliveryDAO.getDeliveriesByRecord(idRecord);
            this.deliveries.setAll(loadedList);
        } catch (SQLException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión", "No se pudieron cargar las entregas programadas.");
            e.printStackTrace();
        }
    }

    @FXML
    void btnOpenDeliveryClick(ActionEvent event) {
        Delivery selectedDelivery = tvDeliveries.getSelectionModel().getSelectedItem();
        if (selectedDelivery == null) {
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Selección Requerida", "Debes seleccionar una entrega de la lista.");
            return;
        }

        // Asegurarse de que selectedDelivery ya tiene su DeliveryDefinition cargada.
        // Si no la tiene, se podría cargar aquí, pero es mejor que getDeliveriesByRecord lo haga.
        if (selectedDelivery.getDeliveryDefinition() == null) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Datos", "Los detalles de la entrega seleccionada no están disponibles.");
            return;
        }

        try {
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(Utils.getSceneComponent(tvDeliveries));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/professionalpractice/view/student/FXMLDeliverDocument.fxml"));
            Parent view = loader.load();

            FXMLDeliverDocumentController controller = loader.getController();
            controller.initData(selectedDelivery); // selectedDelivery ahora contiene su DeliveryDefinition

            Scene scene = new Scene(view);
            // Usar el nombre de la definición para el título
            modalStage.setTitle("Realizar Entrega: " + selectedDelivery.getDeliveryDefinition().getName());
            modalStage.setScene(scene);
            modalStage.showAndWait();

        } catch (IOException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al Cargar", "No se pudo abrir la ventana de entrega.");
            e.printStackTrace();
        }
    }

    @FXML
    void btnBackClick(ActionEvent event) {
        Stage stage = (Stage) tvDeliveries.getScene().getWindow();
        stage.close();
    }
}