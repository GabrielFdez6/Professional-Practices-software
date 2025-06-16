// Ubicación: src/professionalpractice/controller/FXMLLogInController.java
package professionalpractice.controller;

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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import professionalpractice.ProfessionalPractices;
import professionalpractice.controller.academic.FXMLAcademicMainScreenController;
import professionalpractice.controller.coordinator.FXMLCoordinatorMainScreenController;
import professionalpractice.controller.evaluator.FXMLEvaluatorMainScreenController;
import professionalpractice.controller.student.FXMLStudentMainScreenController;
import professionalpractice.model.dao.AcademicDAO;
import professionalpractice.model.dao.StudentDAO;
import professionalpractice.model.dao.UserAccountDAO;
import professionalpractice.model.dao.interfaces.IAcademicDAO;
import professionalpractice.model.dao.interfaces.IStudentDAO;
import professionalpractice.model.dao.interfaces.IUserAccountDAO;
import professionalpractice.model.pojo.Academic;
import professionalpractice.model.pojo.Coordinator;
import professionalpractice.model.pojo.Student;
import professionalpractice.model.pojo.UserAccount;
import professionalpractice.utils.Utils;

public class FXMLLogInController implements Initializable {

    @FXML private TextField tfUsername;
    @FXML private PasswordField tfPassword;
    @FXML private Label lbErrorUsername;
    @FXML private Label lbErrorPassword;

    private IUserAccountDAO userAccountDAO;
    private IStudentDAO studentDAO;
    private IAcademicDAO academicDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userAccountDAO = new UserAccountDAO();
        studentDAO = new StudentDAO();
        academicDAO = new AcademicDAO();
    }

    @FXML
    private void btnClickLogIn(ActionEvent event) {
        String username = tfUsername.getText();
        String password = tfPassword.getText();

        if (verifyFields(username, password)) {
            verifyCredentials(username, password);
        }
    }

    private boolean verifyFields(String username, String password) {
        lbErrorUsername.setText("");
        lbErrorPassword.setText("");
        boolean valid = true;
        if (username.isEmpty()) {
            lbErrorUsername.setText("Usuario obligatorio");
            valid = false;
        }
        if (password.isEmpty()) {
            lbErrorPassword.setText("Contraseña obligatoria");
            valid = false;
        }
        return valid;
    }

    private void verifyCredentials(String username, String password) {
        try {
            UserAccount user = userAccountDAO.getUserByUsername(username);

            // La librería BCrypt debe estar en tu classpath para que esto funcione.
            if (user != null && BCrypt.checkpw(password, user.getPassword())) {
                System.out.println(user.getPassword());
                System.out.println(password);
                System.out.println(user.getUsername());

                String role = user.getRole();
                System.out.println(role);
                switch (role) {
                    case "STUDENT":
                        goStudentHomeScreen(user.getUserId());
                        break;
                    case "COORDINATOR":
                        goCoordinatorHomeScreen();
                        break;
                    case "TEACHER":
                        goTeacherHomeScreen(user.getUserId());
                        break;
                    case "EVALUATOR":
                        goEvaluatorHomeScreen(user.getUserId());
                        break;
                    default:
                        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Rol no reconocido", "El rol de usuario no es válido.");
                }
            } else {
                Utils.showSimpleAlert(Alert.AlertType.ERROR, "Credenciales incorrectas", "Usuario o contraseña no válidos. Por favor, inténtelo de nuevo.");
            }
        } catch (SQLException e) {
            // ¡Aquí manejamos el error de la base de datos!
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión", "No se pudo conectar a la base de datos. Por favor, inténtelo más tarde.");
        }
    }

    private void goTeacherHomeScreen(int userId) {
        try {
            Academic academic = academicDAO.getAcademicByUserId(userId); // Necesitarás crear este método en AcademicDAO
            if (academic == null) {
                Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Datos", "No se pudo encontrar la información del académico asociada a esta cuenta.");
                return;
            }

            Stage baseStage = (Stage) tfUsername.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/academic/FXMLAcademicMainScreen.fxml"));
            Parent view = loader.load();

            FXMLAcademicMainScreenController controller = loader.getController();
            controller.configureScreen(academic);

            Scene mainScene = new Scene(view);
            baseStage.setScene(mainScene);
            baseStage.setTitle("Página Principal del Académico");
            baseStage.show();

        } catch (Exception e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error Inesperado", "Ocurrió un error inesperado. Por favor, intenta más tarde.");
            e.printStackTrace();
        }
    }

    private void goStudentHomeScreen(int userId) {
        try {
            Student student = studentDAO.getStudentByUserId(userId);
            if (student == null) {
                Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Datos", "No se pudo encontrar la información del estudiante asociada a esta cuenta.");
                return;
            }

            Stage baseStage = (Stage) tfUsername.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/student/FXMLStudentMainScreen.fxml"));
            Parent view = loader.load();

            FXMLStudentMainScreenController controller = loader.getController();
            controller.configureScreen(student);

            Scene mainScene = new Scene(view);
            baseStage.setScene(mainScene);
            baseStage.setTitle("Página Principal del Estudiante");
            baseStage.show();

        } catch (SQLException e){
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Base de Datos", "Ocurrió un error al consultar tu progreso. Por favor, intenta más tarde.");
            System.err.println("Error al obtener el progreso del estudiante: " + e.getMessage());
            e.printStackTrace();
            closeWindow();
        } catch (Exception e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error Inesperado", "Ocurrió un error inesperado. Por favor, intenta más tarde.");
            System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
            closeWindow();
        }
    }

    private void goEvaluatorHomeScreen(int userid) {
        try {
            Student student = studentDAO.getStudentByUserId(userid);
            if (student == null) {
                Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Datos", "No se pudo encontrar la información del evaluador asociada a esta cuenta.");
                return;
            }

            Stage baseStage = (Stage) tfUsername.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/evaluator/FXMLEvaluatorMainScreen.fxml"));
            Parent view = loader.load();

            FXMLEvaluatorMainScreenController controller = loader.getController();
            controller.loadUserInformation(student);

            Scene mainScene = new Scene(view);
            baseStage.setScene(mainScene);
            baseStage.setTitle("Página Principal Evaluador");
            baseStage.show();
        } catch (SQLException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos, no se pudo mostrar la ventana.");
        } catch (IOException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos, no se pudo mostrar la ventana.");
        }
    }

    private void goCoordinatorHomeScreen() {
        try {
//            Coordinator coordinator = coordinatorDAO.getAcademicByUserId(userId);
//            if (coordinator == null) {
//                Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Datos", "No se pudo encontrar la información del coordinador asociada a esta cuenta.");
//                return;
//            }

            Stage baseStage = (Stage) tfUsername.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/coordinator/FXMLCoordinatorMainScreen.fxml"));
            Parent view = loader.load();
            FXMLCoordinatorMainScreenController controller = loader.getController();
            controller.configureScreen("Coordinador");
            Scene mainScene = new Scene(view);
            baseStage.setScene(mainScene);
            baseStage.setTitle("Página Principal Coordinador");
            baseStage.show();
        } catch (IOException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos, no se pudo mostrar la ventana.");
        }
    }
    private void closeWindow() {
        Stage stage = (Stage) lbErrorPassword.getScene().getWindow();
        stage.close();
    }
}