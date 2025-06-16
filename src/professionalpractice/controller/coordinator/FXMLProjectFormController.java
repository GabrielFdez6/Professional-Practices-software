package professionalpractice.controller.coordinator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import professionalpractice.model.dao.ProjectDAO;
import professionalpractice.model.dao.interfaces.IProjectDAO;
import professionalpractice.model.pojo.LinkedOrganization;
import professionalpractice.model.pojo.Project;
import professionalpractice.model.pojo.ProjectManager;
import professionalpractice.utils.Constants;
import professionalpractice.utils.Utils;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class FXMLProjectFormController implements Initializable {

    @FXML private TextField tfAvailability;
    @FXML private TextArea taDescription;
    @FXML private TextField tfProjectName;
    @FXML private TextField tfMethodology;

    @FXML private Label lblFormTitle;
    @FXML private Label lblOrgInfo;
    @FXML private Label lblPMInfo;

    private LinkedOrganization currentOrganization;
    private ProjectManager currentProjectManager;
    private Project projectToEdit;
    private boolean isEditMode;
    private IProjectDAO projectDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        projectDAO = new ProjectDAO();
    }

    public void initData(boolean isEdit, LinkedOrganization organization, ProjectManager manager, Project project) {
        this.isEditMode = isEdit;
        this.currentOrganization = organization;
        this.currentProjectManager = manager;
        this.projectToEdit = project;

        if (lblOrgInfo != null) {
            lblOrgInfo.setText("Organización: " + (organization != null ? organization.getName() : "N/A"));
        }
        if (lblPMInfo != null) {
            lblPMInfo.setText("Responsable: " + (manager != null ? manager.getFirstName() + " " + manager.getLastNameFather() : "N/A"));
        }

        if (isEdit) {
            lblFormTitle.setText("MODIFICAR PROYECTO");
            if (projectToEdit != null) {
                tfProjectName.setText(projectToEdit.getName());
                taDescription.setText(projectToEdit.getDescription());
                tfMethodology.setText(projectToEdit.getMethodology());
            }
        } else {
            lblFormTitle.setText("REGISTRAR NUEVO PROYECTO");
            tfProjectName.clear();
            taDescription.clear();
            tfMethodology.clear();
            tfAvailability.clear();
        }
    }

    @FXML
    public void btnCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) ((javafx.scene.Node)actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    public void btnRegisterProject(ActionEvent actionEvent) {
        String name = tfProjectName.getText().trim();

        String description = taDescription.getText().trim();
        String methodology = tfMethodology.getText().trim();

        if (name.isEmpty() || description.isEmpty() || methodology.isEmpty()) {
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Campos Vacíos", "Por favor, completa todos los campos obligatorios.");
            return;
        }

        try {
            int response;
            if (isEditMode) {
                projectToEdit.setName(name);
                projectToEdit.setDescription(description);
                projectToEdit.setMethodology(methodology);

                response = projectDAO.updateProject(projectToEdit);
                if (response == Constants.OPERATION_SUCCESFUL) {
                    Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Actualización Exitosa", "El proyecto ha sido actualizado correctamente.");
                    closeWindow(actionEvent);
                } else {
                    Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el proyecto.");
                }

            } else {
                Project newProject = new Project();
                newProject.setName(name);
                newProject.setDescription(description);
                newProject.setMethodology(methodology);

                if (currentOrganization == null || currentProjectManager == null) {
                    Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Contexto", "No se pudo asociar el proyecto a una organización o responsable.");
                    return;
                }
                newProject.setIdLinkedOrganization(currentOrganization.getIdLinkedOrganization());
                newProject.setIdProjectManager(currentProjectManager.getIdProjectManager());

                response = projectDAO.saveProject(newProject);
                if (response == Constants.OPERATION_SUCCESFUL) {
                    Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Registro Exitoso", "El proyecto ha sido registrado correctamente.");
                    closeWindow(actionEvent);
                } else {
                    Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error", "No se pudo registrar el proyecto.");
                }
            }
        } catch (SQLException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Base de Datos", "Ocurrió un error al guardar los datos del proyecto.");
            e.printStackTrace();
        }
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
}