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
import javafx.stage.Stage;
import professionalpractice.ProfessionalPractices;
import professionalpractice.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLInfoStudentsProjectController implements Initializable {

    @FXML
    private TableColumn colMatriculation;
    @FXML
    private TableView tvStudents;
    @FXML
    private TableColumn colNameProject;
    @FXML
    private TableColumn colNameStudent;
    @FXML
    private TableView tvProjects;
    @FXML
    private TableColumn colAvailability;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }


    @FXML
    public void btnCancel(ActionEvent actionEvent) {
        try {
            if (Utils.showConfirmationAlert("Salir de la proyectos", "¿Estás seguro que quieres cancelar?")) {
                Stage stageStudentsList = (Stage) tvStudents.getScene().getWindow();
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
    public void btnAssign(ActionEvent actionEvent) {
    }
}