package professionalpractice.controller.coordinator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import professionalpractice.ProfessionalPractices;
import professionalpractice.model.dao.ProjectManagerDAO;
import professionalpractice.model.dao.interfaces.IProjectManagerDAO;
import professionalpractice.model.pojo.LinkedOrganization;
import professionalpractice.model.pojo.Project;
import professionalpractice.model.pojo.ProjectManager;
import professionalpractice.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLProjectManagerListController implements Initializable {

    @FXML private TableView<ProjectManager> tvProjectManagers;
    @FXML private TableColumn<ProjectManager, String> colManagerName;
    @FXML private TableColumn<ProjectManager, String> colPosition;
    @FXML private TableColumn<ProjectManager, String> colEmail;
    @FXML private TableColumn<ProjectManager, String> colPhoneNumber;
    @FXML private Label lblTitle;

    private IProjectManagerDAO projectManagerDAO;
    private ObservableList<ProjectManager> projectManagers;

    private Project currentProject;
    private LinkedOrganization currentLinkedOrganization;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        projectManagerDAO = new ProjectManagerDAO();
        projectManagers = FXCollections.observableArrayList();
        configureTable();
    }

    public void setProject(Project project) {
        this.currentProject = project;
        this.currentLinkedOrganization = null;
        loadProjectManagers();
        updateTitle();
    }

    public void setLinkedOrganization(LinkedOrganization organization) {
        this.currentLinkedOrganization = organization;
        this.currentProject = null;
        loadProjectManagers();
        updateTitle();
    }

    private void configureTable(){
        colManagerName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colPosition.setCellValueFactory(new PropertyValueFactory<>("position"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phone"));
    }

    private void loadProjectManagers(){
        try {
            if (currentLinkedOrganization != null) {
                List<ProjectManager> managers = projectManagerDAO.getProjectManagersByOrganizationId(currentLinkedOrganization.getIdLinkedOrganization());
                projectManagers.setAll(managers);
                if (managers.isEmpty()) {
                    Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Sin Responsables",
                            "No hay responsables de proyecto registrados para la organización: " + currentLinkedOrganization.getName() + ".");
                }
            } else if (currentProject != null) {
                List<ProjectManager> managers = projectManagerDAO.getAllProjectManagersByProjectId(currentProject.getIdProject());
                projectManagers.setAll(managers);
                if (managers.isEmpty()) {
                    Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Sin Responsables",
                            "No hay responsables de proyecto registrados para el proyecto: " + currentProject.getName() + ".");
                }
            } else {
                projectManagers.clear();
                Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Información", "No se ha especificado un proyecto o una organización para cargar responsables.");
            }
            tvProjectManagers.setItems(projectManagers);
        } catch (SQLException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión", "No se pudieron cargar los responsables.");
            e.printStackTrace();
        }
    }


    private void updateTitle() {
        if (lblTitle != null) {
            if (currentLinkedOrganization != null) {
                lblTitle.setText("RESPONSABLES DE LA ORGANIZACIÓN: " + currentLinkedOrganization.getName());
            } else if (currentProject != null) {
                lblTitle.setText("RESPONSABLES PARA EL PROYECTO: " + currentProject.getName());
            } else {
                lblTitle.setText("SELECCIONAR RESPONSABLE DE PROYECTO");
            }
        }
    }

    @FXML
    public void btnRegresar(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) tvProjectManagers.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/coordinator/FXMLListOVRegisterProject.fxml"));
            Parent view = loader.load();

            Scene scene = new Scene(view);
            stage.setScene(scene);
            stage.setTitle("Organizaciones Vinculadas");
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo regresar a la pantalla de organizaciones vinculadas.");
        }
    }

    @FXML
    public void btnSelectProjectManager(ActionEvent actionEvent) {
        ProjectManager selectedManager = tvProjectManagers.getSelectionModel().getSelectedItem();
        if(selectedManager == null){
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Selección Requerida", "Por favor, selecciona un responsable de proyecto de la lista.");
            return;
        }

        if (currentLinkedOrganization != null) {
            try {
                Stage baseStage = (Stage) tvProjectManagers.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/coordinator/FXMLAddEditProject.fxml"));
                Parent view = loader.load();

                FXMLAddEditProjectController nextController = loader.getController();
                nextController.initDataForNewProject(currentLinkedOrganization, selectedManager);

                Scene mainScene = new Scene(view);
                baseStage.setScene(mainScene);
                baseStage.setTitle("Gestión de Proyectos del Responsable");
                baseStage.show();

            } catch (IOException ex) {
                Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos, no se pudo mostrar la ventana de gestión de proyectos del responsable.");
                ex.printStackTrace();
            }
        } else {
            Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Acción Inválida", "Por favor, selecciona una organización vinculada primero para gestionar proyectos.");
        }
    }
}