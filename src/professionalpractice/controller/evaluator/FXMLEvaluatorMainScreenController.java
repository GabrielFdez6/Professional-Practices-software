
package professionalpractice.controller.evaluator;

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
import professionalpractice.utils.Utils;

public class FXMLEvaluatorMainScreenController implements Initializable {

    @FXML
    private Label lbUsername;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String username = SesionUsuario.getInstancia().getUsername();
        if (username != null && !username.isEmpty()) {
            lbUsername.setText(username);
        }
    }

    @FXML
    public void btnClickGradePresentations(ActionEvent actionEvent) {
        try {
            Stage baseStage = (Stage) lbUsername.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/evaluator/FXMLStudentList.fxml"));
            Parent view = loader.load();

            Scene mainScene = new Scene(view);
            baseStage.setScene(mainScene);
            baseStage.setTitle("Lista de Estudiantes");
            baseStage.show();
        } catch(IOException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos por el momento no se pudo mostrar la ventana");
        }
    }

    @FXML
    public void btnClickLogOut(ActionEvent actionEvent) {
        try {
            SesionUsuario.getInstancia().cerrarSesion();
            Stage baseStage = (Stage) lbUsername.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/FXMLLogIn.fxml"));
            Parent viewLogIn = loader.load();
            Scene LogInScene = new Scene(viewLogIn);
            baseStage.setScene(LogInScene);
            baseStage.setTitle("Inicio de Sesión");
            baseStage.show();
        } catch (IOException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos por el momento no se pudo mostrar la ventana");
        }
    }
}
