package professionalpractice.controller.coordinator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import professionalpractice.ProfessionalPractices;
import professionalpractice.model.dao.ProjectManagerDAO;
import professionalpractice.model.dao.interfaces.IProjectManagerDAO;
import professionalpractice.model.pojo.LinkedOrganization;
import professionalpractice.model.pojo.ProjectManager;
import professionalpractice.utils.Constants;
import professionalpractice.utils.Utils;

import java.io.IOException;
import java.sql.SQLException;

public class FXMLRegisterResponsibleController {

    @FXML private Label lblOrganizationName;
    @FXML private TextField tfFirstName;
    @FXML private TextField tfLastNameFather;
    @FXML private TextField tfLastNameMother;
    @FXML private TextField tfPosition;
    @FXML private TextField tfEmail;
    @FXML private TextField tfPhone;

    private boolean isEdit;
    private ProjectManager projectManager;

    private LinkedOrganization selectedOrganization;
    private IProjectManagerDAO projectManagerDAO;

    @FXML
    public void initialize() {
        projectManagerDAO = new ProjectManagerDAO();
    }

    private void configureTable() {
        if (isEdit) {
            tfFirstName.setText(projectManager.getFirstName());
            tfLastNameFather.setText(projectManager.getLastNameFather());
            tfLastNameMother.setText(projectManager.getLastNameMother());
            tfPosition.setText(projectManager.getPosition());
            tfEmail.setText(projectManager.getEmail());
            tfPhone.setText(projectManager.getPhone());
        }

    }

    public void initData(LinkedOrganization organization) {
        this.selectedOrganization = organization;
        lblOrganizationName.setText("Organización: " + organization.getName());
        this.isEdit = false;
        configureTable();

    }

    public void initData(ProjectManager projectManager) {
        this.projectManager = projectManager;
        lblOrganizationName.setText("");
        this.isEdit = true;
        configureTable();

    }

    @FXML
    void btnGuardarClick(ActionEvent event) {
        if (validateFields()) {

            ProjectManager newManager = new ProjectManager();
            newManager.setFirstName(tfFirstName.getText().trim());
            newManager.setLastNameFather(tfLastNameFather.getText().trim());
            newManager.setLastNameMother(tfLastNameMother.getText().trim());
            newManager.setPosition(tfPosition.getText().trim());
            newManager.setEmail(tfEmail.getText().trim());
            newManager.setPhone(tfPhone.getText().trim());

            if(isEdit) {
                try {
                    newManager.setIdProjectManager(projectManager.getIdProjectManager());
                    if (projectManagerDAO.updateProjectManager(newManager) == Constants.OPERATION_SUCCESFUL) {
                        Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Actualizacion Exitoso", "El responsable ha sido actualizado.");
                        goMainMenu();
                    } else {
                        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error en la actualizacion", "No se pudo actualizar al responsable.");
                    }
                } catch (SQLException e) {
                    Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión", "No se pudo conectar con la base de datos.");
                }
            }else {
                newManager.setIdLinkedOrganization(selectedOrganization.getIdLinkedOrganization());

                try {
                    if (projectManagerDAO.registerProjectManager(newManager) == Constants.OPERATION_SUCCESFUL) {
                        Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Registro Exitoso", "El responsable ha sido guardado.");
                        goMainMenu();
                    } else {
                        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error en el Registro", "No se pudo registrar al responsable.");
                    }
                } catch (SQLException e) {
                    Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión", "No se pudo conectar con la base de datos.");
                }
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
        goMainMenu();
    }

    private void goMainMenu() {
        try {
            Stage stage = (Stage) tfLastNameFather.getScene().getWindow();
            Parent view = FXMLLoader.load(ProfessionalPractices.class.getResource("view/coordinator/FXMLCoordinatorMainScreen.fxml"));
            Scene scene = new Scene(view);
            stage.setScene(scene);
            stage.setTitle("Página Principal Coordinador");
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}