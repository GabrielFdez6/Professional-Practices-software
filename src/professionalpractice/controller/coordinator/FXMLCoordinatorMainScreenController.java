package professionalpractice.controller.coordinator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import professionalpractice.ProfessionalPractices;
import professionalpractice.utils.Utils;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLCoordinatorMainScreenController implements Initializable {

    @FXML private Label lbWelcome;
    @FXML private Label lbFullName;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void configureScreen(String fullName) {
        if (fullName != null && !fullName.isEmpty()) {
            lbFullName.setText(fullName);
        } else {
            lbFullName.setText("Coordinador Desconocido");
        }
    }

    @FXML
    void btnAssignProjects(ActionEvent event) {
        try {
            Stage baseStage = (Stage) lbFullName.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/coordinator/FXMLInfoStudentsProject.fxml"));
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

    @FXML
    void btnRegisterProject(ActionEvent event) {
        try {
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(Utils.getSceneComponent(lbFullName));

            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/coordinator/FXMLListOV.fxml"));
            Parent view = loader.load();
            Scene scene = new Scene(view);

            modalStage.setTitle("Programar Entrega de Documentos");
            modalStage.setScene(scene);
            modalStage.showAndWait();
        } catch (IOException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos, no se pudo mostrar la ventana de programación.");
            ex.printStackTrace();
        }
    }

    @FXML
    void btnRegisterResponsible(ActionEvent event) {
        try {
            Stage baseStage = (Stage) lbFullName.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/coordinator/FXMLListOV.fxml"));
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

    @FXML
    void btnUpdateResponsible(ActionEvent event) {
        try {
            Stage baseStage = (Stage) lbFullName.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/coordinator/FXMLSelectProject.fxml"));
            Parent view = loader.load();
            Scene mainScene = new Scene(view);
            baseStage.setScene(mainScene);
            baseStage.setTitle("Seleccionar Proyecto para Actualizar Responsable");
            baseStage.show();
        } catch (IOException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos, no se pudo mostrar la ventana.");
            ex.printStackTrace();
        }
    }

    @FXML
    void btnClickLogOut(ActionEvent event) {
        try {
            Stage baseStage = (Stage) lbFullName.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/FXMLLogIn.fxml"));
            Parent viewLogIn = loader.load();
            Scene LogInScene = new Scene(viewLogIn);
            baseStage.setScene(LogInScene);
            baseStage.setTitle("Inicio de Sesión");
            baseStage.show();
        } catch (IOException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos, no se pudo regresar a la pantalla de inicio de sesión.");
        }
    }
}