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
import javafx.stage.Stage;
import professionalpractice.ProfessionalPractices;
import professionalpractice.model.dao.ProjectDAO;
import professionalpractice.model.dao.interfaces.IProjectDAO;
import professionalpractice.model.pojo.Project;
import professionalpractice.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class FXMLSelectProjectController implements Initializable {

    @FXML private TableView<Project> tvProjects;
    @FXML private TableColumn<Project, String> colProjectName;
    @FXML private TableColumn<Project, String> colDepartment;

    private IProjectDAO projectDAO;
    private ObservableList<Project> projects;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        projectDAO = new ProjectDAO();
        projects = FXCollections.observableArrayList();
        configureTable();
        loadProjects();
    }

    private void configureTable() {
        colProjectName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
    }

    private void loadProjects() {
        try {
            projects.setAll(projectDAO.getAllProjects());
            tvProjects.setItems(projects);
        } catch (SQLException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión", "No se pudieron cargar los proyectos.");
        }
    }

    @FXML
    void btnSeleccionarClick(ActionEvent event) {
        Project selectedProject = tvProjects.getSelectionModel().getSelectedItem();
        if (selectedProject == null) {
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Selección Requerida", "Debe seleccionar un proyecto para continuar.");
            return;
        }

        try {
            Stage stage = (Stage) tvProjects.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/coordinator/FXMLProjectManagerList.fxml"));
            Parent view = loader.load();


            FXMLProjectManagerListController controller = loader.getController();
            controller.setProject(selectedProject);
            Scene scene = new Scene(view);
            stage.setScene(scene);
            stage.setTitle("Seleccionar Responsable a Actualizar");
            stage.show();
        } catch (IOException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Carga", "No se pudo mostrar la ventana de responsables.");
            e.printStackTrace();
        }
    }

    @FXML
    void btnCancelarClick(ActionEvent event) {
        goMainCoordinatorScreen();
    }

    private void goMainCoordinatorScreen(){
        try {
            Stage stage = (Stage) tvProjects.getScene().getWindow();
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