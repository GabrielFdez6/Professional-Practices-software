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
import professionalpractice.utils.Utils;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

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
        this.deliveryDAO = new DeliveryDAO();
        configureTable();
    }

    public void initData(int idRecord) {
        loadDeliveriesData(idRecord);
    }

    private void configureTable() {
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colType.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDeliveryType()));
        colEndDate.setCellValueFactory(cellData -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            return new SimpleStringProperty(sdf.format(cellData.getValue().getEndDate()));
        });
        tvDeliveries.setItems(deliveries);
    }

    private void loadDeliveriesData(int idRecord) {
        try {
            this.deliveries.setAll(deliveryDAO.getDeliveriesByRecord(idRecord));
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

        try {
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(Utils.getSceneComponent(tvDeliveries));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/professionalpractice/view/student/FXMLDeliverDocument.fxml"));
            Parent view = loader.load();

            FXMLDeliverDocumentController controller = loader.getController();
            controller.initData(selectedDelivery);

            Scene scene = new Scene(view);
            modalStage.setTitle("Realizar Entrega: " + selectedDelivery.getName());
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
