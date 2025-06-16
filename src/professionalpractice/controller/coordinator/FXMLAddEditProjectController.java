package professionalpractice.controller.coordinator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import professionalpractice.ProfessionalPractices;
import professionalpractice.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLAddEditProjectController implements Initializable {
    @FXML
    private TableColumn colPhoneNumber;
    @FXML
    private TableView tvProjects;
    @FXML
    private TableColumn colDescription;
    @FXML
    private TableColumn colProjectName;
    @FXML
    private TableColumn colMethodology;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    public void btnEditProject(ActionEvent actionEvent) {

    }

    @FXML
    public void btnCancel(ActionEvent actionEvent) {
        try {
            if (Utils.showConfirmationAlert("Salir de la proyectos", "¿Estás seguro que quieres cancelar?")) {
                Stage stageStudentsList = (Stage) tvProjects.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/coordinator/FXMLCoordinatorMainScreen.fxml"));
                Parent viewLogIn = loader.load();
                Scene mainScene = new Scene(viewLogIn);
                stageStudentsList.setScene(mainScene);
                stageStudentsList.setTitle("Pagina Principal");
                stageStudentsList.show();
            }
        } catch (IOException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos por el momento no se pudo mostrar la ventana");
        }
    }

    @FXML
    public void btnAddProject(ActionEvent actionEvent) {
        goProjectForm(false, null);
    }

    public void goProjectForm(boolean edit, ActionEvent actionEvent) {
        try {
            Stage baseStage = new Stage();
            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/coordinator/FXMLProjectForm.fxml"));
            Parent viewLogIn = loader.load();

            Scene LogInScene = new Scene(viewLogIn);
            baseStage.setScene(LogInScene);
            baseStage.setTitle("Formulario de Proyecto");
            baseStage.initModality(Modality.APPLICATION_MODAL);
            baseStage.showAndWait();
        } catch (IOException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos por el momento no se pudo mostrar la ventana");
        }
    }
}