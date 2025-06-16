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

    /**
     * Configura la pantalla principal del coordinador con el nombre completo.
     * @param fullName Nombre completo del coordinador.
     */
    public void configureScreen(String fullName) {
        if (fullName != null && !fullName.isEmpty()) {
            lbFullName.setText(fullName);
        } else {
            lbFullName.setText("Coordinador Desconocido");
        }
    }

    /**
     * Inicia el flujo para registrar un nuevo RESPONSABLE (CU-14).
     * Navega a la pantalla de selección de Organización Vinculada.
     */
    @FXML
    public void btnRegisterResponsible(ActionEvent actionEvent) {
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

    /**
     * Inicia el flujo para actualizar un RESPONSABLE (CU-15).
     * Navega a la pantalla de selección de Proyectos.
     */
    @FXML
    public void btnUpdateResponsible(ActionEvent actionEvent) {
        try {
            Stage baseStage = (Stage) lbFullName.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/coordinator/FXMLSelectProject.fxml"));
            Parent view = loader.load();
            Scene mainScene = new Scene(view);
            baseStage.setScene(mainScene);
            baseStage.setTitle("Seleccionar Proyecto");
            baseStage.show();
        } catch (IOException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos, no se pudo mostrar la ventana.");
            ex.printStackTrace();
        }
    }

    /**
     * Maneja el clic en el botón "Registrar Proyecto".
     * Esta es una funcionalidad diferente a registrar un RESPONSABLE.
     */
    @FXML
    public void btnRegisterProject(ActionEvent actionEvent) {
        // En el futuro, este botón debería abrir la interfaz para registrar un NUEVO PROYECTO.
        // Por ahora, mostramos una alerta para diferenciarlo.
        Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Función en Desarrollo", "Esta sección es para registrar los datos de un nuevo Proyecto, no de un Responsable.");
    }

    @FXML
    public void btnAssignProjects(ActionEvent actionEvent) {
        Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Función en Desarrollo", "Esta funcionalidad para asignar proyectos aún no ha sido implementada.");
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
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos, no se pudo regresar a la pantalla de inicio de sesión.");
        }
    }
}