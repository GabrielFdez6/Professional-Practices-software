package professionalpractice.controller.coordinator;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import professionalpractice.model.dao.ScheduleDeliveryDAO;
import professionalpractice.model.dao.TermDAO;
import professionalpractice.model.pojo.OperationResult;
import professionalpractice.model.pojo.Term;
import professionalpractice.utils.Utils;

public class FXMLScheduleDeliveryDetailsController implements Initializable {

    @FXML
    private TextField tfName;
    @FXML
    private TextArea taDescription;
    @FXML
    private DatePicker dpStartDate;
    @FXML
    private DatePicker dpEndDate;
    private String deliveryType;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void initializeInformation(String deliveryType, String documentName){
        this.deliveryType = deliveryType;
        this.tfName.setText(documentName);
    }

    private boolean validateFields(){
        if(tfName.getText().isEmpty() || dpStartDate.getValue() == null || dpEndDate.getValue() == null){
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Campos Vacíos", "Existen campos vacíos, por favor llena los campos marcados con *");
            return false;
        }

        LocalDate startDate = dpStartDate.getValue();
        LocalDate endDate = dpEndDate.getValue();
        LocalDate currentDate = LocalDate.now();

        if(startDate.isBefore(currentDate)){
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Fecha de Inicio Incorrecta", "La fecha de inicio no puede ser anterior a la fecha actual.");
            return false;
        }

        if(endDate.isBefore(startDate)){
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Fechas Incorrectas", "La fecha de fin no puede ser anterior a la fecha de inicio.");
            return false;
        }

        try {
            Connection tempCon = professionalpractice.model.ConectionBD.getConnection();

            if (tempCon == null) {
                Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Período", "No se encontró un período escolar activo en la base de datos.");
                return false;
            }

            Term currentPeriod = TermDAO.getCurrentPeriod(tempCon);

            try {
                tempCon.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            LocalDate periodStartDate = LocalDate.parse(currentPeriod.getStartDate());
            LocalDate periodEndDate = LocalDate.parse(currentPeriod.getEndDate());

            if (startDate.isBefore(periodStartDate)) {
                Utils.showSimpleAlert(Alert.AlertType.WARNING, "Fechas Fuera de Periodo",
                        "La fecha de la entrega está fuera del periodo escolar actual (" + periodStartDate.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy - ")) + periodEndDate.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".\nPor favor selecciona una fecha dentro del periodo escolar");
                return false;
            }

            if (endDate.isAfter(periodEndDate)) {
                Utils.showSimpleAlert(Alert.AlertType.WARNING, "Fechas Fuera de Periodo",
                        "La fecha de la entrega está fuera del periodo escolar actual (" + periodStartDate.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy - ")) + periodEndDate.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ").\n\nPor favor selecciona una fecha dentro del periodo escolar.");
                return false;
            }

        } catch (SQLException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión", "No se pudo verificar el período actual: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @FXML
    private void btnClickCancel(ActionEvent event) {
        boolean confirmed = Utils.showConfirmationAlert("Cancelar Operación",
                "¿Estás seguro de que quieres cancelar?", "Se cerrarán todas las ventanas abiertas.");
        if (confirmed) {
            closeWindow();
        }
    }

    private void closeWindow(){
        Utils.getSceneComponent(tfName).close();
    }

    @FXML
    private void btnClickSchedule(ActionEvent event) {
        if(validateFields()){
            String definitionName = tfName.getText();
            String definitionDescription = taDescription.getText();
            Timestamp definitionStartDate = Timestamp.valueOf(dpStartDate.getValue().atStartOfDay());
            Timestamp definitionEndDate = Timestamp.valueOf(dpEndDate.getValue().atStartOfDay());

            try{
                OperationResult result = ScheduleDeliveryDAO.scheduleDeliveryCurrentPeriod(
                        definitionName,
                        definitionDescription,
                        definitionStartDate,
                        definitionEndDate,
                        deliveryType
                );

                if(!result.isError()){
                    Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Operación Exitosa", result.getMessage());
                    closeWindow();
                } else {
                    Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error", result.getMessage());
                }

            } catch(SQLException e){
                Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión", "No fue posible conectar con la base de datos.");
                e.printStackTrace();
            }
        }
    }
}