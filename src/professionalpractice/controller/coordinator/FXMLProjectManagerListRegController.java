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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import professionalpractice.ProfessionalPractices;
import professionalpractice.model.dao.ProjectManagerDAO;
import professionalpractice.model.dao.interfaces.IProjectManagerDAO;
import professionalpractice.model.pojo.Project;
import professionalpractice.model.pojo.ProjectManager;
import professionalpractice.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class FXMLProjectManagerListRegController implements Initializable {

    @FXML private TableView<ProjectManager> tvProjectManagers;
    @FXML private TableColumn<ProjectManager, String> colManagerName;
    @FXML private TableColumn<ProjectManager, String> colPosition;
    @FXML private TableColumn<ProjectManager, String> colEmail;
    @FXML private TableColumn<ProjectManager, String> colPhoneNumber;

    private IProjectManagerDAO projectManagerDAO;
    private ObservableList<ProjectManager> projectManagers;
    private Project selectedProject;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        projectManagerDAO = new ProjectManagerDAO();
        projectManagers = FXCollections.observableArrayList();
        configureTable();
    }

    public void setProject(Project project) {
        this.selectedProject = project;
        loadProjectManagers();
    }

    private void configureTable(){
        colManagerName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colPosition.setCellValueFactory(new PropertyValueFactory<>("position"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phone"));
    }

    private void loadProjectManagers(){
        if (selectedProject != null) {
            try {
                projectManagers.setAll(projectManagerDAO.getAllProjectManagersByProjectId(selectedProject.getIdProject()));
                tvProjectManagers.setItems(projectManagers);
            } catch (SQLException e) {
                Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión", "No se pudieron cargar los responsables.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void btnCancel(ActionEvent actionEvent) {
        goSelectProjectScreen();
    }

    @FXML
    public void btnSelectProjectManager(ActionEvent actionEvent) {
        ProjectManager selectedManager = tvProjectManagers.getSelectionModel().getSelectedItem();
        if(selectedManager == null){
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Selección Requerida", "Debe seleccionar un responsable para actualizar.");
            return;
        }

        try {
            Stage baseStage = (Stage) tvProjectManagers.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/coordinator/FXMLAddEditProjectController.fxml"));
            Parent view = loader.load();
            Scene mainScene = new Scene(view);
            baseStage.setScene(mainScene);
            baseStage.setTitle("Seleccionar Organización Vinculada");
            baseStage.show();
        } catch (IOException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos, no se pudo mostrar la ventana.");
            ex.printStackTrace();
        }
    }

    private void goSelectProjectScreen() {
        try {
            Stage stage = (Stage) tvProjectManagers.getScene().getWindow();
            Parent view = FXMLLoader.load(ProfessionalPractices.class.getResource("view/coordinator/FXMLSelectProject.fxml"));
            Scene scene = new Scene(view);
            stage.setScene(scene);
            stage.setTitle("Seleccionar Proyecto");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}