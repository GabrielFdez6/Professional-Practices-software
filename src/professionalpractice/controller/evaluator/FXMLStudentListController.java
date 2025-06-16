package professionalpractice.controller.evaluator;


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

public class FXMLStudentListController implements Initializable {

    @FXML
    private TableColumn colMatriculation;
    @FXML
    private TableView tvStudents;
    @FXML
    private TableColumn colStudentName;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadTableInformation();
    }


    public void loadTableInformation() {
    }


    @FXML
    public void btnCancel(ActionEvent actionEvent) {
        try {
            if (Utils.showConfirmationAlert("Salir de la evaluacion", "¿Estás seguro que quieres cancelar?")) {
                Stage stageStudentsList = (Stage) tvStudents.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/evaluator/FXMLEvaluatorMainScreen.fxml"));
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
    public void btnSelectStudent(ActionEvent actionEvent) {
        try {
            Stage EvaluationRubric = (Stage) tvStudents.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/evaluator/FXMLEvaluationRubric.fxml"));
            Parent view = loader.load();
            Scene mainScene = new Scene(view);
            EvaluationRubric.setScene(mainScene);
            EvaluationRubric.setTitle("Rúbrica de Evaluación");
            EvaluationRubric.show();
        } catch (IOException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos por el momento no se pudo mostrar la ventana");
        }
    }
}