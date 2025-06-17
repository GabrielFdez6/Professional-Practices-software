package professionalpractice.controller.coordinator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import professionalpractice.model.dao.ProjectManagerDAO;
import professionalpractice.model.dao.interfaces.IProjectManagerDAO;
import professionalpractice.model.pojo.ProjectManager;
import professionalpractice.utils.Constants;
import professionalpractice.utils.Utils;

import java.sql.SQLException;

public class FXMLUpdateResponsibleController {

    @FXML private TextField tfFirstName;
    @FXML private TextField tfLastNameFather;
    @FXML private TextField tfLastNameMother;
    @FXML private TextField tfPosition;
    @FXML private TextField tfEmail;
    @FXML private TextField tfPhone;

    private ProjectManager managerToUpdate;
    private IProjectManagerDAO projectManagerDAO;

    @FXML
    public void initialize() {
        projectManagerDAO = new ProjectManagerDAO();
    }

    public void initData(ProjectManager manager) {
        this.managerToUpdate = manager;
        tfFirstName.setText(manager.getFirstName());
        tfLastNameFather.setText(manager.getLastNameFather());
        tfLastNameMother.setText(manager.getLastNameMother());
        tfPosition.setText(manager.getPosition());
        tfEmail.setText(manager.getEmail());
        tfPhone.setText(manager.getPhone());
    }

    @FXML
    void btnActualizarClick(ActionEvent event) {
        if (validateFields()) {
            ProjectManager updatedManager = new ProjectManager();
            updatedManager.setIdProjectManager(managerToUpdate.getIdProjectManager());
            updatedManager.setFirstName(tfFirstName.getText().trim());
            updatedManager.setLastNameFather(tfLastNameFather.getText().trim());
            updatedManager.setLastNameMother(tfLastNameMother.getText().trim());
            updatedManager.setPosition(tfPosition.getText().trim());
            updatedManager.setEmail(tfEmail.getText().trim());
            updatedManager.setPhone(tfPhone.getText().trim());

            try {
                if (projectManagerDAO.updateProjectManager(updatedManager) == Constants.OPERATION_SUCCESFUL) {
                    Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Actualización Exitosa", "Los datos del responsable han sido actualizados.");
                    closeWindow();
                } else {
                    Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error en la Actualización", "No se pudo actualizar al responsable.");
                }
            } catch (SQLException e) {
                Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión", "No se pudo conectar con la base de datos.");
            }
        }
    }

    private boolean validateFields() {
        if (tfFirstName.getText().trim().isEmpty() || tfLastNameFather.getText().trim().isEmpty() || tfEmail.getText().trim().isEmpty()) {
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Campos Vacíos", "Nombre, Apellido Paterno y Correo son obligatorios.");
            return false;
        }
        return true;
    }

    @FXML
    void btnCancelarClick(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) tfFirstName.getScene().getWindow();
        stage.close();
    }
}