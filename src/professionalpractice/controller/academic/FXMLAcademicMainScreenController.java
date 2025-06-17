package professionalpractice.controller.academic;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import professionalpractice.ProfessionalPractices;
import professionalpractice.model.SesionUsuario;
import professionalpractice.model.pojo.Academic;
import professionalpractice.utils.Utils;

public class FXMLAcademicMainScreenController implements Initializable {

    @FXML
    private Label lbWelcome;
    @FXML
    private Label lbFullName;

    private Academic loggedInAcademic;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void configureScreen(Academic academic) {
        this.loggedInAcademic = academic;
        if (academic != null) {
            lbFullName.setText(academic.getFirstName() + " " + academic.getLastNameFather());
        }
    }

    @FXML
    private void btnClickConsultStudentProgress(ActionEvent event) {
        try {
            Stage stage = (Stage) lbFullName.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/academic/FXMLAcademicProgressStudents.fxml"));
            Parent view = loader.load();

            FXMLAcademicProgressStudentsController controller = loader.getController();
            controller.initData(loggedInAcademic.getIdAcademic(), 1);
            Scene scene = new Scene(view);
            stage.setScene(scene);
            stage.setTitle("Avance Académico de Estudiantes");
            stage.show();
        } catch (IOException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de UI", "No se pudo cargar la ventana de avance de estudiantes.");
            e.printStackTrace();
        }
    }

    @FXML
    private void btnClickLogOut(ActionEvent event) {
        try {
            SesionUsuario.getInstancia().cerrarSesion();
            Stage baseStage = (Stage) lbFullName.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/FXMLLogIn.fxml"));
            Parent viewLogIn = loader.load();
            Scene LogInScene = new Scene(viewLogIn);
            baseStage.setScene(LogInScene);
            baseStage.setTitle("Inicio de Sesión");
            baseStage.show();
        } catch (IOException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos, no se pudo mostrar la ventana.");
        }
    }
}