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
import professionalpractice.model.pojo.DeliveryDefinition;
import professionalpractice.utils.Utils;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date; // Para la fecha actual
import java.util.List;
import java.util.stream.Collectors; // Para filtrar la lista

public class FXMLDeliveryListController {

    @FXML private TableView<Delivery> tvDeliveries;
    @FXML private TableColumn<Delivery, String> colName;
    @FXML private TableColumn<Delivery, String> colType;
    @FXML private TableColumn<Delivery, String> colEndDate;
    @FXML private TableColumn<Delivery, String> colStatus; // Nueva columna para el estado

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
        colName.setCellValueFactory(cellData -> {
            DeliveryDefinition definition = cellData.getValue().getDeliveryDefinition();
            return new SimpleStringProperty(definition != null ? definition.getName() : "N/A");
        });
        colType.setCellValueFactory(cellData -> {
            DeliveryDefinition definition = cellData.getValue().getDeliveryDefinition();
            String englishType = (definition != null) ? definition.getDeliveryType() : "N/A";
            return new SimpleStringProperty(translateDeliveryType(englishType));
        });
        colEndDate.setCellValueFactory(cellData -> {
            DeliveryDefinition definition = cellData.getValue().getDeliveryDefinition();
            if (definition != null && definition.getEndDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                return new SimpleStringProperty(sdf.format(definition.getEndDate()));
            }
            return new SimpleStringProperty("N/A");
        });
        // --- Configuración de la nueva columna de estado ---
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        // --- Fin de nueva columna ---

        tvDeliveries.setItems(deliveries);
    }

    private String translateDeliveryType(String englishType) {
        switch (englishType) {
            case "INITIAL DOCUMENT":
                return "DOCUMENTO INICIAL";
            case "FINAL DOCUMENT":
                return "DOCUMENTO FINAL";
            case "REPORT":
                return "REPORTE";
            case "N/A": // Si no hay definición
                return "No Disponible";
            default:
                return englishType; // Devuelve el original si no se reconoce
        }
    }

    private void loadDeliveriesData(int idRecord) {
        try {
            List<Delivery> allDeliveries = deliveryDAO.getDeliveriesByRecord(idRecord);

            // --- Filtrar entregas: No mostrar las que estén en estado "APROBADO" ---
            List<Delivery> filteredDeliveries = allDeliveries.stream()
                    .filter(d -> !"APROBADO".equalsIgnoreCase(d.getStatus())) // Filtrar las "APROBADO"
                    .collect(Collectors.toList());

            this.deliveries.setAll(filteredDeliveries);
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
        if (selectedDelivery.getDeliveryDefinition() == null) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Datos", "Los detalles de la entrega seleccionada no están disponibles.");
            return;
        }

        // --- Nueva validación de estado antes de abrir la ventana de entrega ---
        Date currentDate = new Date();
        Date endDate = selectedDelivery.getDeliveryDefinition().getEndDate();
        String currentStatus = selectedDelivery.getStatus();

        boolean canDeliver = false;
        String message = "";

        switch (currentStatus) {
            case "PENDIENTE":
                // Siempre se puede entregar si está pendiente, pero validamos la fecha
                if (currentDate.after(endDate)) {
                    message = "La fecha límite de entrega para esta tarea ha expirado.";
                } else {
                    canDeliver = true;
                }
                break;
            case "ENTREGADO":
                // Se puede volver a entregar si está dentro de la fecha de fin
                if (currentDate.after(endDate)) {
                    message = "La entrega ya fue realizada y la fecha límite ha expirado. No se puede volver a entregar.";
                } else {
                    canDeliver = true;
                    message = "La entrega ya fue realizada, pero aún puedes subir una nueva versión.";
                }
                break;
            case "RECHAZADO":
                // Se puede volver a entregar si está dentro de la fecha de fin
                if (currentDate.after(endDate)) {
                    message = "La entrega fue rechazada y la fecha límite ha expirado. No se puede volver a entregar.";
                } else {
                    canDeliver = true;
                    message = "Tu entrega fue rechazada. Puedes subir una nueva versión.";
                }
                break;
            case "EN_REVISION":
                message = "Tu entrega está actualmente en revisión. No se puede subir una nueva versión hasta que sea evaluada.";
                break;
            case "APROBADO":
                message = "Tu entrega ha sido aprobada. No se puede subir una nueva versión.";
                break;
            default:
                message = "Estado de entrega desconocido o inválido.";
                break;
        }

        if (!canDeliver) {
            Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Entrega No Disponible", message);
            return; // No abrir la ventana de entrega
        }
        // --- Fin de nueva validación de estado ---


        try {
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(Utils.getSceneComponent(tvDeliveries));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/professionalpractice/view/student/FXMLDeliverDocument.fxml"));
            Parent view = loader.load();

            FXMLDeliverDocumentController controller = loader.getController();
            controller.initData(selectedDelivery);

            Scene scene = new Scene(view);
            modalStage.setTitle("Realizar Entrega: " + selectedDelivery.getDeliveryDefinition().getName());
            modalStage.setScene(scene);
            modalStage.showAndWait();

            // Después de cerrar la ventana de entrega, recargar la lista para reflejar los cambios
            loadDeliveriesData(selectedDelivery.getIdRecord());

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