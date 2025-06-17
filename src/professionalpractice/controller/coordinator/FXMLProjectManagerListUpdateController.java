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

public class FXMLProjectManagerListUpdateController implements Initializable {

    @FXML private TableView<ProjectManager> tvProjectManagers;
    @FXML private TableColumn<ProjectManager, String> colManagerName;
    @FXML private TableColumn<ProjectManager, String> colPosition;
    @FXML private TableColumn<ProjectManager, String> colEmail;
    @FXML private TableColumn<ProjectManager, String> colPhoneNumber;

    private IProjectManagerDAO projectManagerDAO;
    private ObservableList<ProjectManager> projectManagers;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        projectManagerDAO = new ProjectManagerDAO();
        projectManagers = FXCollections.observableArrayList();
        configureTable();
        loadProjectManagers();
    }

    private void configureTable(){
        colManagerName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colPosition.setCellValueFactory(new PropertyValueFactory<>("position"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phone"));
    }

    private void loadProjectManagers(){
        try {
            List<ProjectManager> managers = projectManagerDAO.getAllProjectManagers();
            projectManagers.setAll(managers);

            tvProjectManagers.setItems(projectManagers);
        } catch (SQLException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión", "No se pudieron cargar los responsables.");
            e.printStackTrace();
        }
    }


    @Deprecated
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

        try {
            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/coordinator/FXMLRegisterResponsible.fxml"));
            Parent view = loader.load();

            FXMLRegisterResponsibleController nextController = loader.getController();
            nextController.initData(selectedManager);

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(Utils.getSceneComponent(tvProjectManagers));
            modalStage.setTitle("Actualizar Responsable");
            modalStage.setScene(new Scene(view));
            modalStage.showAndWait();

        } catch (IOException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos, no se pudo mostrar la ventana de gestión de proyectos del responsable.");
            ex.printStackTrace();
        }

    }

    @FXML
    public void btnCancel(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) tvProjectManagers.getScene().getWindow();
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