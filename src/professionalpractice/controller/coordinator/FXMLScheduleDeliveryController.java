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
import professionalpractice.utils.Utils;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class FXMLScheduleDeliveryController implements Initializable {

    @FXML private TextField tfDeliveryName;
    @FXML private ComboBox<String> cmbDeliveryType;
    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;
    @FXML private TextField tfDescription;
    @FXML private Button btnAccept;
    @FXML private Button btnCancel;

    private IDeliveryDAO deliveryDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        deliveryDAO = new DeliveryDAO();
        // As per CU-06, these are the options
        cmbDeliveryType.setItems(FXCollections.observableArrayList("INITIAL DOCUMENT", "REPORT", "FINAL DOCUMENT"));
    }

    @FXML
    void btnAcceptClick(ActionEvent event) {
        if (validateFields()) {
            Delivery delivery = new Delivery();
            delivery.setName(tfDeliveryName.getText().trim());
            delivery.setDeliveryType(cmbDeliveryType.getValue());

            LocalDate startDate = dpStartDate.getValue();
            LocalDate endDate = dpEndDate.getValue();
            delivery.setStartDate(Timestamp.valueOf(startDate.atStartOfDay()));
            delivery.setEndDate(Timestamp.valueOf(endDate.atTime(23, 59, 59)));

            delivery.setDescription(tfDescription.getText().trim());

            try {
                if (deliveryDAO.scheduleDeliveryForAllRecords(delivery) == Constants.OPERATION_SUCCESFUL) {
                    Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Operación Exitosa", "La entrega ha sido programada correctamente para todos los estudiantes.");
                    closeWindow();
                } else {
                    Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error en la Operación", "No se pudo programar la entrega.");
                }
            } catch (SQLException e) {
                Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión", "No hay conexión con la base de datos.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    void btnCancelClick(ActionEvent event) {
        if (Utils.showConfirmationAlert("Cancelar Proceso", "¿Estás seguro que quieres cancelar?")) {
            closeWindow();
        }
    }

    private boolean validateFields() {
        if (tfDeliveryName.getText().trim().isEmpty() ||
                cmbDeliveryType.getValue() == null ||
                dpStartDate.getValue() == null ||
                dpEndDate.getValue() == null) {
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Campos Vacíos", "Todos los campos excepto la descripción son obligatorios.");
            return false;
        }
        if (dpEndDate.getValue().isBefore(dpStartDate.getValue())) {
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Fechas Inválidas", "La fecha de fin no puede ser anterior a la fecha de inicio.");
            return false;
        }
        return true;
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}