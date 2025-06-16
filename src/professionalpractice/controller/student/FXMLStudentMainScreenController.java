package professionalpractice.controller.student;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
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
import professionalpractice.model.SesionUsuario;
import professionalpractice.model.dao.RecordDAO;
import professionalpractice.model.dao.interfaces.IRecordDAO;
import professionalpractice.model.pojo.Record;
import professionalpractice.model.pojo.Student;
import professionalpractice.utils.Utils;

public class FXMLStudentMainScreenController implements Initializable {

    @FXML private Label lbFullName;
    @FXML private Label lbWelcome;

    private Student loggedInStudent;
    private IRecordDAO recordDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.recordDAO = new RecordDAO();
    }

    public void configureScreen(Student student) {
        if (student != null) {
            this.loggedInStudent = student;
            lbWelcome.setText("BIENVENIDO(A), " + student.getFirstName().toUpperCase());
            lbFullName.setText(student.getFullName());
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
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos, no se pudo regresar a la pantalla de inicio de sesión.");
        }
    }

    @FXML
    private void btnClickMyProgress(ActionEvent event) {
        if (loggedInStudent == null) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de usuario", "No se pudo identificar al usuario actual.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/professionalpractice/view/student/FXMLStudentProgress.fxml"));
            Parent view = loader.load();

            FXMLStudentProgressController controller = loader.getController();
            controller.configureView(this.loggedInStudent);

            Stage stage = new Stage();
            stage.setScene(new Scene(view));
            stage.setTitle("Mi Avance de Prácticas Profesionales");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "No se pudo mostrar la ventana de avance.");
            e.printStackTrace();
        }
    }

    @FXML
    private void btnClickManageDeliveries(ActionEvent event) {
        try {
            Record studentRecord = recordDAO.getRecordByStudentId(loggedInStudent.getIdStudent());

            if (studentRecord == null) {
                Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Sin Expediente", "Aún no tienes un expediente de prácticas activo en el sistema.");
                return;
            }

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(Utils.getSceneComponent(lbFullName));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/professionalpractice/view/student/FXMLDeliveryList.fxml"));
            Parent view = loader.load();

            FXMLDeliveryListController controller = loader.getController();
            controller.initData(studentRecord.getIdRecord());

            Scene scene = new Scene(view);
            stage.setTitle("Gestionar Mis Entregas");
            stage.setScene(scene);
            stage.showAndWait();

        } catch (SQLException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión", "No se pudo obtener tu expediente del sistema.");
            e.printStackTrace();
        } catch (IOException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al Cargar", "No se pudo mostrar la ventana de entregas.");
            e.printStackTrace();
        }
    }
}