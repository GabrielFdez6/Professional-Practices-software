package professionalpractice.controller.coordinator;

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
import professionalpractice.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLCoordinatorMainScreenController implements Initializable {

    @FXML
    private Label lbWelcome;
    @FXML
    private Label lbFullName;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void initializeInformation() {
        loadUserInformation();
    }

    public void loadUserInformation() {
        lbWelcome.setText("BIENVENIDO(A), ");
    }

    @FXML
    public void btnRegisterProject(ActionEvent actionEvent) {
        try {
            Stage baseStage = (Stage) lbFullName.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/coordinator/FXMLListOV.fxml"));
            Parent view = loader.load();

            Scene mainScene = new Scene(view);
            baseStage.setScene(mainScene);
            baseStage.setTitle("Listas de Organización Vinculadas");
            baseStage.show();
        } catch (IOException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos por el momento no se pudo mostrar la ventana");
        }
    }

    @FXML
    public void btnClickLogOut(ActionEvent actionEvent) {
        try {
            Stage baseStage = (Stage) lbFullName.getScene().getWindow();
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