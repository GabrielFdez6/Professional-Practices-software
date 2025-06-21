package professionalpractice.controller.coordinator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
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

public class FXMLScheduleDeliveryDetailsController implements Initializable {

    @FXML private TextField tfDeliveryName;
    @FXML private TextArea taDescription;
    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;
    @FXML private Button btnProgram;
    @FXML private Button btnBack;

    private String selectedDocumentName;
    private String selectedDeliveryType;
    private IDeliveryDAO deliveryDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        deliveryDAO = new DeliveryDAO();
    }

    public void initData(String documentName, String deliveryType) {
        this.selectedDocumentName = documentName;
        this.selectedDeliveryType = deliveryType;
        tfDeliveryName.setText(documentName);
    }

    @FXML
    private void btnProgramClick(ActionEvent event) {
        if (validateFields()) {
            Delivery newDelivery = new Delivery();
            newDelivery.setName(tfDeliveryName.getText().trim());
            newDelivery.setDescription(taDescription.getText().trim());
            newDelivery.setDeliveryType(selectedDeliveryType);

            LocalDate startDate = dpStartDate.getValue();
            LocalDate endDate = dpEndDate.getValue();
            newDelivery.setStartDate(Timestamp.valueOf(startDate.atStartOfDay()));
            newDelivery.setEndDate(Timestamp.valueOf(endDate.atTime(23, 59, 59)));

            try {
                int response = deliveryDAO.scheduleDeliveryForAllRecords(newDelivery);

                if (response == Constants.OPERATION_SUCCESFUL) {
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
    private void btnBackClick(ActionEvent event) {
        if (Utils.showConfirmationAlert("Cancelar Proceso", "¿Estás seguro que quieres regresar? Los cambios no se guardarán.")) {
            closeWindow();
        }
    }

    private boolean validateFields() {
        if (tfDeliveryName.getText().trim().isEmpty() ||
                dpStartDate.getValue() == null ||
                dpEndDate.getValue() == null) {
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Campos Vacíos", "El nombre de la entrega y las fechas de inicio/fin son obligatorias.");
            return false;
        }
        if (dpEndDate.getValue().isBefore(dpStartDate.getValue())) {
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Fechas Inválidas", "La fecha de fin no puede ser anterior a la fecha de inicio.");
            return false;
        }
        return true;
    }

    private void closeWindow() {
        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.close();
    }
}