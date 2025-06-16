package professionalpractice.controller.coordinator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import professionalpractice.model.dao.ProjectManagerDAO;
import professionalpractice.model.dao.interfaces.IProjectManagerDAO;
import professionalpractice.model.pojo.LinkedOrganization;
import professionalpractice.model.pojo.ProjectManager;
import professionalpractice.utils.Constants;
import professionalpractice.utils.Utils;

import java.sql.SQLException;

public class FXMLRegisterResponsibleController {

    @FXML private Label lblOrganizationName;
    @FXML private TextField tfFirstName;
    @FXML private TextField tfLastNameFather;
    @FXML private TextField tfLastNameMother;
    @FXML private TextField tfPosition;
    @FXML private TextField tfEmail;
    @FXML private TextField tfPhone;

    private LinkedOrganization selectedOrganization;
    private IProjectManagerDAO projectManagerDAO;

    @FXML
    public void initialize() {
        projectManagerDAO = new ProjectManagerDAO();
    }

    public void initData(LinkedOrganization organization) {
        this.selectedOrganization = organization;
        lblOrganizationName.setText("Organización: " + organization.getName());
    }

    @FXML
    void btnGuardarClick(ActionEvent event) {
        if (validateFields()) {
            ProjectManager newManager = new ProjectManager();
            newManager.setIdLinkedOrganization(selectedOrganization.getIdLinkedOrganization());
            newManager.setFirstName(tfFirstName.getText().trim());
            newManager.setLastNameFather(tfLastNameFather.getText().trim());
            newManager.setLastNameMother(tfLastNameMother.getText().trim());
            newManager.setPosition(tfPosition.getText().trim());
            newManager.setEmail(tfEmail.getText().trim());
            newManager.setPhone(tfPhone.getText().trim());

            try {
                if (projectManagerDAO.registerProjectManager(newManager) == Constants.OPERATION_SUCCESFUL) {
                    Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Registro Exitoso", "El responsable ha sido guardado.");
                    closeWindow();
                } else {
                    Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error en el Registro", "No se pudo registrar al responsable.");
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
        // Añadir más validaciones si es necesario (ej. formato de email)
        return true;
    }

    @FXML
    void btnCancelarClick(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) lblOrganizationName.getScene().getWindow();
        stage.close();
    }
}