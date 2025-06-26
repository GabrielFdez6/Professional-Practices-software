package professionalpractice.controller.coordinator;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List; // Importar List
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView; // ¡Importar ListView!
import javafx.scene.control.SelectionMode; // Para SelectionMode
import javafx.stage.Stage;
import professionalpractice.model.ConectionBD;
import professionalpractice.model.dao.GroupDAO;
import professionalpractice.model.dao.ScheduleDeliveryDAO;
import professionalpractice.model.dao.TermDAO;
import professionalpractice.model.pojo.Group;
import professionalpractice.model.pojo.OperationResult;
import professionalpractice.model.pojo.Term;
import professionalpractice.utils.Utils;

public class FXMLScheduleDeliveryDetailsController implements Initializable {

    @FXML private TextField tfName;
    @FXML private TextArea taDescription;
    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;
    @FXML private ListView<Group> lvGroups;

    private String deliveryType;
    private String selectedDocumentName;
    private Term currentAcademicPeriod;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (selectedDocumentName != null) {
            tfName.setText(selectedDocumentName);
        }
        lvGroups.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        loadGroupsIntoListView();
    }

    public void initializeInformation(String deliveryType, String documentName){
        this.deliveryType = deliveryType;
        this.selectedDocumentName = documentName;
        if (tfName != null) {
            tfName.setText(this.selectedDocumentName);
        }
    }

    private void loadGroupsIntoListView() {
        try {
            Connection tempCon = ConectionBD.getConnection();
            if (tempCon == null) {
                Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión", "No se pudo establecer conexión para cargar grupos.");
                return;
            }

            currentAcademicPeriod = TermDAO.getCurrentPeriod(tempCon);
            if (currentAcademicPeriod == null) {
                Utils.showSimpleAlert(Alert.AlertType.WARNING, "Periodo No Encontrado", "No se encontró un periodo escolar activo. No se pueden cargar grupos.");
                return;
            }

            ArrayList<Group> groups = GroupDAO.getGroupsByTerm(currentAcademicPeriod.getIdTerm(), tempCon);

            try {
                tempCon.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            if (groups.isEmpty()) {
                Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "No se Encontraron Grupos", "No se encontraron grupos activos para el periodo actual.");
                return;
            }

            ObservableList<Group> observableGroups = FXCollections.observableArrayList(groups);
            lvGroups.setItems(observableGroups);

        } catch (SQLException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Carga", "Error al cargar los grupos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateFields(){
        // Validar que se haya seleccionado al menos un grupo
        if(tfName.getText().isEmpty() || dpStartDate.getValue() == null || dpEndDate.getValue() == null || lvGroups.getSelectionModel().isEmpty()){
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Campos Vacíos", "Existen campos vacíos. Por favor, llene todos los campos marcados con * y seleccione al menos un grupo.");
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

        if (currentAcademicPeriod == null) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Periodo", "No se encontró un periodo escolar activo para validación.");
            return false;
        }

        LocalDate periodStartDate = LocalDate.parse(currentAcademicPeriod.getStartDate());
        LocalDate periodEndDate = LocalDate.parse(currentAcademicPeriod.getEndDate());

        if (startDate.isBefore(periodStartDate)) {
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Fechas Fuera de Periodo",
                    "La fecha de entrega (" + startDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + " - " + endDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) +
                            ") está fuera del periodo escolar actual (" + periodStartDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ").\n\nPor favor, seleccione una fecha dentro del periodo actual.");
            return false;
        }

        if (endDate.isAfter(periodEndDate)) {
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Fechas Fuera de Periodo",
                    "La fecha de entrega (" + startDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + " - " + endDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) +
                            ") está fuera del periodo escolar actual (" + periodStartDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + " - " + periodEndDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ").\n\nPor favor, seleccione una fecha dentro del periodo actual.");
            return false;
        }

        return true;
    }

    @FXML
    private void btnClickCancel(ActionEvent event) {
        boolean confirmed = Utils.showConfirmationAlert("Cancelar Operación",
                "¿Está seguro de que desea cancelar?", "Cualquier dato no guardado se perderá. Volverá a la ventana anterior.");
        if (confirmed) {
            closeWindow();
        }
    }


    private void closeWindow(){
        Stage stage = (Stage) tfName.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void btnClickSchedule(ActionEvent event) {
        if(validateFields()){
            String definitionName = tfName.getText();
            String definitionDescription = taDescription.getText();
            Timestamp definitionStartDate = Timestamp.valueOf(dpStartDate.getValue().atStartOfDay());
            Timestamp definitionEndDate = Timestamp.valueOf(dpEndDate.getValue().atStartOfDay());

            ObservableList<Group> selectedGroups = lvGroups.getSelectionModel().getSelectedItems();
            List<Integer> idSelectedGroups = new ArrayList<>();
            for (Group group : selectedGroups) {
                idSelectedGroups.add(group.getIdGroup());
            }

            try{
                OperationResult result = ScheduleDeliveryDAO.scheduleDeliveryForMultipleGroups(
                        definitionName,
                        definitionDescription,
                        definitionStartDate,
                        definitionEndDate,
                        deliveryType,
                        idSelectedGroups
                );

                if(!result.isError()){
                    Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Operación Exitosa", result.getMessage());
                    closeWindow();
                } else {
                    Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error", result.getMessage());
                }

            } catch(SQLException e){
                Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión", "No se pudo conectar con la base de datos." + "\nDetalle: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}