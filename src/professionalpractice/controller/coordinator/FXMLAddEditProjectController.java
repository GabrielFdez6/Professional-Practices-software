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
import professionalpractice.model.dao.ProjectDAO;
import professionalpractice.model.dao.interfaces.IProjectDAO;
import professionalpractice.model.pojo.LinkedOrganization;
import professionalpractice.model.pojo.Project;
import professionalpractice.model.pojo.ProjectManager;
import professionalpractice.utils.Utils;
import professionalpractice.utils.Constants;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLAddEditProjectController implements Initializable {

    @FXML private Label lblTitle;
    @FXML private Label lblPMInfo;
    @FXML private TableView<Project> tvProjects;
    @FXML private TableColumn<Project, String> colProjectName;
    @FXML private TableColumn<Project, String> colDescription;
    @FXML private TableColumn<Project, String> colMethodology;
    @FXML private TableColumn<Project, Integer> colAvailability;

    private LinkedOrganization currentOrganization;
    private ProjectManager currentProjectManager;
    private IProjectDAO projectDAO;
    private ObservableList<Project> projects;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        projectDAO = new ProjectDAO();
        projects = FXCollections.observableArrayList();
        configureTable();
    }

    public void initDataForNewProject(LinkedOrganization organization, ProjectManager manager) {
        this.currentOrganization = organization;
        this.currentProjectManager = manager;
        updateUIInfo();
        loadProjects();
    }

    private void configureTable() {
        colProjectName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colMethodology.setCellValueFactory(new PropertyValueFactory<>("methodology"));
        colAvailability.setCellValueFactory(new PropertyValueFactory<>("availability"));
    }

    private void updateUIInfo() {
        if (lblTitle != null) {
            lblTitle.setText("PROYECTOS DE: " + currentProjectManager.getFirstName() + " " + currentProjectManager.getLastNameFather());
        }
        if (lblPMInfo != null) {
            lblPMInfo.setText("Responsable: " + currentProjectManager.getFirstName() + " " + currentProjectManager.getLastNameFather() +
                    " | Organización: " + currentOrganization.getName());
        }
    }

    private void loadProjects() {
        if (currentProjectManager != null) {
            try {
                List<Project> projectList = projectDAO.getProjectsByProjectManagerId(currentProjectManager.getIdProjectManager());
                projects.setAll(projectList);
                tvProjects.setItems(projects);
                if (projectList.isEmpty()) {
                    Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Sin Proyectos",
                            "No hay proyectos asignados a este responsable de proyecto.");
                }
            } catch (SQLException e) {
                Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión", "No se pudieron cargar los proyectos.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void btnAddProject(ActionEvent actionEvent) {
        openProjectForm(false, null);
    }

    @FXML
    public void btnEditProject(ActionEvent actionEvent) {
        Project selectedProject = tvProjects.getSelectionModel().getSelectedItem();
        if (selectedProject == null) {
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Selección Requerida", "Por favor, selecciona un proyecto para modificar.");
            return;
        }
        openProjectForm(true, selectedProject);
    }

    private void openProjectForm(boolean isEdit, Project projectToEdit) {
        try {
            Stage modalStage = new Stage();
            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/coordinator/FXMLProjectForm.fxml"));
            Parent view = loader.load();

            FXMLProjectFormController controller = loader.getController();
            controller.initData(isEdit, currentOrganization, currentProjectManager, projectToEdit);

            Scene scene = new Scene(view);
            modalStage.setScene(scene);
            modalStage.setTitle(isEdit ? "Modificar Proyecto" : "Registrar Nuevo Proyecto");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(Utils.getSceneComponent(tvProjects));
            modalStage.showAndWait();

            loadProjects();

        } catch (IOException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos, no se pudo mostrar el formulario del proyecto.");
            ex.printStackTrace();
        }
    }

    @FXML
    public void btnRegresar(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) tvProjects.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/coordinator/FXMLProjectManagerList.fxml"));
            Parent view = loader.load();

            FXMLProjectManagerListController prevController = loader.getController();
            prevController.setLinkedOrganization(currentOrganization);

            Scene scene = new Scene(view);
            stage.setScene(scene);
            stage.setTitle("Seleccionar Responsable de Proyecto");
            stage.show();
        } catch (IOException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos, no se pudo regresar a la pantalla anterior.");
            ex.printStackTrace();
        }
    }

    @FXML
    public void btnCancel(ActionEvent actionEvent) {
        try {
            if (Utils.showConfirmationAlert("Salir de la gestión de proyectos", "¿Estás seguro que quieres cancelar y volver a la página principal?")) {
                Stage stage = (Stage) tvProjects.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/coordinator/FXMLCoordinatorMainScreen.fxml"));
                Parent view = loader.load();
                Scene mainScene = new Scene(view);
                stage.setScene(mainScene);
                stage.setTitle("Página Principal Coordinador");
                stage.show();
            }
        } catch (IOException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos, no se pudo mostrar la ventana principal.");
            ex.printStackTrace();
        }
    }
}