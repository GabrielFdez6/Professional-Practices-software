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

public class FXMLProjectManagerListRegController implements Initializable {

    @FXML private TableView<ProjectManager> tvProjectManagers;
    @FXML private TableColumn<ProjectManager, String> colManagerName;
    @FXML private TableColumn<ProjectManager, String> colPosition;
    @FXML private TableColumn<ProjectManager, String> colEmail;
    @FXML private TableColumn<ProjectManager, String> colPhoneNumber;
    @FXML private Label lblTitle;

    private IProjectManagerDAO projectManagerDAO;
    private ObservableList<ProjectManager> projectManagers;

    private Project currentProject;
    private LinkedOrganization selectedLinkedOrganization;

    private int callingContextType = -1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        projectManagerDAO = new ProjectManagerDAO();
        projectManagers = FXCollections.observableArrayList();
        configureTable();
    }

    public void setProject(Project project) {
        this.currentProject = project;
        this.selectedLinkedOrganization = null;
        this.callingContextType = 0;
        loadProjectManagers();
        updateTitle();
    }

    public void setLinkedOrganization(LinkedOrganization organization) {
        this.selectedLinkedOrganization = organization;
        this.currentProject = null;
        this.callingContextType = 1;
        loadProjectManagers();
        updateTitle();
    }

    private void configureTable(){
        colManagerName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colPosition.setCellValueFactory(new PropertyValueFactory<>("position"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phone"));
    }

    private void loadProjectManagers(){
        try {
            if (selectedLinkedOrganization != null) {
                List<ProjectManager> managers = projectManagerDAO.getProjectManagersByOrganizationId(selectedLinkedOrganization.getIdLinkedOrganization());
                projectManagers.setAll(managers);
                if (managers.isEmpty()) {
                    Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Sin Responsables",
                            "No hay responsables de proyecto registrados para la organización: " + selectedLinkedOrganization.getName());
                }
            } else if (currentProject != null) {
                List<ProjectManager> managers = projectManagerDAO.getAllProjectManagersByProjectId(currentProject.getIdProject());
                projectManagers.setAll(managers);
                if (managers.isEmpty()) {
                    Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Sin Responsables",
                            "No hay responsables de proyecto registrados para el proyecto: " + currentProject.getName());
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
            if (selectedLinkedOrganization != null) {
                lblTitle.setText("RESPONSABLES PARA LA ORGANIZACIÓN: " + selectedLinkedOrganization.getName());
            } else if (currentProject != null) {
                lblTitle.setText("RESPONSABLES PARA EL PROYECTO: " + currentProject.getName());
            } else {
                lblTitle.setText("SELECCIONAR RESPONSABLE DE PROYECTO");
            }
        }
    }

    // --- Manejo de botones ---

    @FXML
    public void btnCancel(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) tvProjectManagers.getScene().getWindow();
            Parent view = null;
            String title = "";

            if (callingContextType == 1) { // Si venimos de la lista de OVs
                view = FXMLLoader.load(ProfessionalPractices.class.getResource("view/coordinator/FXMLListOVRegisterProjectController.fxml"));
                title = "Organizaciones Vinculadas";
            } else {
                view = FXMLLoader.load(ProfessionalPractices.class.getResource("view/coordinator/FXMLSelectProject.fxml"));
                title = "Seleccionar Proyecto";
            }

            Scene scene = new Scene(view);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo regresar a la pantalla anterior.");
        }
    }

    @FXML
    public void btnSelectProjectManager(ActionEvent actionEvent) {
        ProjectManager selectedManager = tvProjectManagers.getSelectionModel().getSelectedItem();
        if(selectedManager == null){
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Selección Requerida", "Por favor, selecciona un responsable de proyecto de la lista.");
            return;
        }

        if (callingContextType == 1 && selectedLinkedOrganization != null) {
            try {
                Stage baseStage = (Stage) tvProjectManagers.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/coordinator/FXMLAddEditProjectController.fxml"));
                Parent view = loader.load();

                FXMLAddEditProjectController nextController = loader.getController();

                nextController.initDataForNewProject(selectedLinkedOrganization, selectedManager);

                Scene mainScene = new Scene(view);
                baseStage.setScene(mainScene);
                baseStage.setTitle("Registrar Nuevo Proyecto");
                baseStage.show();

            } catch (IOException ex) {
                Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos, no se pudo mostrar la ventana para registrar el proyecto.");
                ex.printStackTrace();
            }
        } else if (callingContextType == 0 && currentProject != null) {
            try {
                FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/coordinator/FXMLUpdateResponsible.fxml"));
                Parent view = loader.load();

                FXMLUpdateResponsibleController controller = loader.getController();
                controller.initData(selectedManager);

                Stage modalStage = new Stage();
                modalStage.initModality(Modality.APPLICATION_MODAL);
                modalStage.initOwner(Utils.getSceneComponent(tvProjectManagers));
                modalStage.setTitle("Actualizar Responsable");
                modalStage.setScene(new Scene(view));
                modalStage.showAndWait();

                loadProjectManagers();

            } catch (IOException e) {
                Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Carga", "No se pudo mostrar el formulario de actualización.");
                e.printStackTrace();
            }
        } else {
            // Manejo si no hay contexto claro o no se selecciona un responsable
            Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Acción Inválida", "No se pudo determinar la acción para el responsable seleccionado.");
        }
    }
}