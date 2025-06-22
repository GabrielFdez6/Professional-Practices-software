package professionalpractice.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import professionalpractice.controller.student.FXMLStudentMainScreenController;
import professionalpractice.model.SesionUsuario;
import professionalpractice.model.dao.AcademicDAO;
import professionalpractice.model.dao.CoordinatorDAO;
import professionalpractice.model.dao.StudentDAO;
import professionalpractice.model.dao.UserAccountDAO;
import professionalpractice.model.dao.interfaces.IAcademicDAO;
import professionalpractice.model.dao.interfaces.ICoordinatorDAO;
import professionalpractice.model.dao.interfaces.IStudentDAO;
import professionalpractice.model.dao.interfaces.IUserAccountDAO;
import professionalpractice.model.pojo.Academic;
import professionalpractice.model.pojo.Coordinator;
import professionalpractice.model.pojo.Student;
import professionalpractice.model.pojo.UserAccount;
import professionalpractice.utils.Constants;
import professionalpractice.utils.Utils;
import professionalpractice.utils.ValidationUtils;

public class FXMLLogInController implements Initializable {

  @FXML
  private TextField tfUsername;
  @FXML
  private PasswordField tfPassword;
  @FXML
  private Label lbErrorUsername;
  @FXML
  private Label lbErrorPassword;

  private IUserAccountDAO userAccountDAO;
  private IStudentDAO studentDAO;
  private IAcademicDAO academicDAO;
  private ICoordinatorDAO coordinatorDAO;

  private static final Map<String, Integer> loginAttempts = new HashMap<>();
  private static final Map<String, LocalDateTime> blockedUsers = new HashMap<>();

  private static final int BLOCK_DURATION_MINUTES = 15;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    userAccountDAO = new UserAccountDAO();
    studentDAO = new StudentDAO();
    academicDAO = new AcademicDAO();
    coordinatorDAO = new CoordinatorDAO();

    clearErrorMessages();
  }

  @FXML
  private void btnClickLogIn(ActionEvent event) {
    // Limpiar mensajes de error previos
    clearErrorMessages();

    String username = tfUsername.getText();
    String password = tfPassword.getText();

    // Verificar si el usuario está bloqueado temporalmente
    if (isUserBlocked(username)) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Acceso Bloqueado",
          "Tu cuenta está temporalmente bloqueada por múltiples intentos fallidos. " +
              "Intenta nuevamente en " + BLOCK_DURATION_MINUTES + " minutos.");
      return;
    }

    // Validar campos con validaciones rigurosas
    if (validateLoginFields(username, password)) {
      verifyCredentials(username, password);
    }
  }

  private void clearErrorMessages() {
    lbErrorUsername.setText("");
    lbErrorPassword.setText("");
  }

  private boolean isUserBlocked(String username) {
    if (username == null || username.trim().isEmpty()) {
      return false;
    }

    LocalDateTime blockTime = blockedUsers.get(username.toLowerCase());
    if (blockTime != null) {
      if (LocalDateTime.now().isBefore(blockTime.plusMinutes(BLOCK_DURATION_MINUTES))) {
        return true;
      } else {
        // El tiempo de bloqueo ha expirado, remover del mapa
        blockedUsers.remove(username.toLowerCase());
        loginAttempts.remove(username.toLowerCase());
      }
    }
    return false;
  }

  private void recordFailedAttempt(String username) {
    if (username == null || username.trim().isEmpty()) {
      return;
    }

    username = username.toLowerCase();
    int attempts = loginAttempts.getOrDefault(username, 0) + 1;
    loginAttempts.put(username, attempts);

    if (attempts >= Constants.MAX_LOGIN_ATTEMPTS) {
      blockedUsers.put(username, LocalDateTime.now());
      Utils.showSimpleAlert(Alert.AlertType.WARNING, "Cuenta Bloqueada",
          "Tu cuenta ha sido bloqueada temporalmente por múltiples intentos fallidos. " +
              "Podrás intentar nuevamente en " + BLOCK_DURATION_MINUTES + " minutos.");
    }
  }

  private void clearFailedAttempts(String username) {
    if (username != null && !username.trim().isEmpty()) {
      loginAttempts.remove(username.toLowerCase());
      blockedUsers.remove(username.toLowerCase());
    }
  }

  private boolean validateLoginFields(String username, String password) {
    List<String> errors = new ArrayList<>();
    boolean isValid = true;

    String usernameError = ValidationUtils.validateUsername(username);
    if (!usernameError.isEmpty()) {
      lbErrorUsername.setText(usernameError);
      isValid = false;
    } else {
      tfUsername.setText(ValidationUtils.sanitizeInput(username));
    }

    // Validar password básico (no mostrar detalles específicos por seguridad)
    if (password == null || password.isEmpty()) {
      lbErrorPassword.setText("Contraseña es obligatoria");
      isValid = false;
    } else if (password.length() < Constants.MIN_PASSWORD_LENGTH) {
      lbErrorPassword.setText("Contraseña debe tener al menos " + Constants.MIN_PASSWORD_LENGTH + " caracteres");
      isValid = false;
    } else if (password.length() > Constants.MAX_PASSWORD_LENGTH) {
      lbErrorPassword.setText("Contraseña demasiado larga");
      isValid = false;
    }

    // Verificar caracteres peligrosos en cualquier campo
    String maliciousCheck = ValidationUtils.checkForMaliciousContent(username, "Usuario");
    if (!maliciousCheck.isEmpty()) {
      lbErrorUsername.setText("Usuario contiene caracteres no válidos");
      isValid = false;
    }

    // No verificar contenido malicioso en password para no revelar información
    // sobre la política de contraseñas

    // Verificar longitudes máximas
    if (username != null && username.length() > Constants.MAX_USERNAME_LENGTH) {
      lbErrorUsername.setText("Usuario demasiado largo");
      isValid = false;
    }

    return isValid;
  }

  private void verifyCredentials(String username, String password) {
    try {
      // Sanitizar entrada
      username = ValidationUtils.sanitizeInput(username);

      if (username == null || username.trim().isEmpty()) {
        lbErrorUsername.setText("Usuario no válido");
        return;
      }

      UserAccount user = userAccountDAO.getUserByUsername(username.trim());

      if (user != null && BCrypt.checkpw(password, user.getPassword())) {
        // Login exitoso - limpiar intentos fallidos
        clearFailedAttempts(username);

        // Validar el rol antes de proceder
        String roleValidation = ValidationUtils.validateRole(user.getRole());
        if (!roleValidation.isEmpty()) {
          Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Sistema",
              "El rol de usuario no es válido. Contacte al administrador.");
          return;
        }

        // Establecer sesión
        SesionUsuario.getInstancia().setRolUsuario(user.getRole());
        SesionUsuario.getInstancia().setIdUsuario(user.getUserId());
        SesionUsuario.getInstancia().setUsername(user.getUsername());

        // Validar ID de usuario
        String idValidation = ValidationUtils.validateId(user.getUserId(), "ID de usuario");
        if (!idValidation.isEmpty()) {
          Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Sistema",
              "ID de usuario no válido. Contacte al administrador.");
          return;
        }

        // Redirigir según el rol usando constantes
        redirectByRole(user.getRole(), user.getUserId(), user.getUsername());

      } else {
        // Login fallido - registrar intento
        recordFailedAttempt(username);

        int remainingAttempts = Constants.MAX_LOGIN_ATTEMPTS -
            loginAttempts.getOrDefault(username.toLowerCase(), 0);

        if (remainingAttempts > 0) {
          Utils.showSimpleAlert(Alert.AlertType.ERROR, "Credenciales Incorrectas",
              "Usuario o contraseña incorrectos. Intentos restantes: " + remainingAttempts);
        }

        // Limpiar campos por seguridad
        tfPassword.clear();
      }
    } catch (SQLException e) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión",
          Constants.ERROR_DATABASE_CONNECTION);
      System.err.println("Error de base de datos en login: " + e.getMessage());
    } catch (Exception e) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error del Sistema",
          "Ocurrió un error inesperado. Por favor, intente más tarde.");
      System.err.println("Error inesperado en login: " + e.getMessage());
    }
  }

  private void redirectByRole(String role, int userId, String username) {
    try {
      switch (role) {
        case Constants.ROLE_STUDENT:
          goStudentHomeScreen(userId);
          break;
        case Constants.ROLE_COORDINATOR:
          goCoordinatorHomeScreen(userId);
          break;
        case "TEACHER": // Mantener compatibilidad con datos existentes
        case Constants.ROLE_ACADEMIC:
          goTeacherHomeScreen(userId);
          break;
        case Constants.ROLE_EVALUATOR:
          goEvaluatorHomeScreen(username);
          break;
        default:
          Utils.showSimpleAlert(Alert.AlertType.ERROR, "Rol No Reconocido",
              "El rol de usuario '" + role + "' no es válido.");
      }
    } catch (Exception e) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Navegación",
          "No se pudo acceder a la pantalla principal. Intente nuevamente.");
      System.err.println("Error en redirección por rol: " + e.getMessage());
    }
  }

  private void goTeacherHomeScreen(int userId) {
    try {
      Academic academic = academicDAO.getAcademicByUserId(userId);
      if (academic == null) {
        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Datos",
            "No se pudo encontrar la información del académico asociada a esta cuenta.");
        return;
      }

      Stage baseStage = (Stage) tfUsername.getScene().getWindow();
      FXMLLoader loader = new FXMLLoader(
          ProfessionalPractices.class.getResource("view/academic/FXMLAcademicMainScreen.fxml"));
      Parent view = loader.load();

      FXMLAcademicMainScreenController controller = loader.getController();
      controller.configureScreen(academic);

      Scene mainScene = new Scene(view);
      baseStage.setScene(mainScene);
      baseStage.setTitle("Página Principal del Académico");
      baseStage.show();

    } catch (Exception e) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error Inesperado",
          "Ocurrió un error inesperado. Por favor, intenta más tarde.");
      e.printStackTrace();
    }
  }

  private void goStudentHomeScreen(int userId) {
    try {
      Student student = studentDAO.getStudentByUserId(userId);
      if (student == null) {
        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Datos",
            "No se pudo encontrar la información del estudiante asociada a esta cuenta.");
        return;
      }

      Stage baseStage = (Stage) tfUsername.getScene().getWindow();
      FXMLLoader loader = new FXMLLoader(
          ProfessionalPractices.class.getResource("view/student/FXMLStudentMainScreen.fxml"));
      Parent view = loader.load();

      FXMLStudentMainScreenController controller = loader.getController();
      controller.configureScreen(student);

      Scene mainScene = new Scene(view);
      baseStage.setScene(mainScene);
      baseStage.setTitle("Página Principal del Estudiante");
      baseStage.show();

    } catch (SQLException e) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Base de Datos",
          "Ocurrió un error al consultar tu progreso. Por favor, intenta más tarde.");
      System.err.println("Error al obtener el progreso del estudiante: " + e.getMessage());
      e.printStackTrace();
      closeWindow();
    } catch (Exception e) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error Inesperado",
          "Ocurrió un error inesperado. Por favor, intenta más tarde.");
      System.err.println("Error inesperado: " + e.getMessage());
      e.printStackTrace();
      closeWindow();
    }
  }

  private void goEvaluatorHomeScreen(String username) {
    try {
      Stage baseStage = (Stage) tfUsername.getScene().getWindow();
      FXMLLoader loader = new FXMLLoader(
          ProfessionalPractices.class.getResource("view/evaluator/FXMLEvaluatorMainScreen.fxml"));
      Parent view = loader.load();

      Scene mainScene = new Scene(view);
      baseStage.setScene(mainScene);
      baseStage.setTitle("Página Principal Evaluador");
      baseStage.show();
    } catch (IOException ex) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos, no se pudo mostrar la ventana.");
    }
  }

  private void goCoordinatorHomeScreen(int userId) {

    try {
      Coordinator coordinator = coordinatorDAO.getCoordinatorByIdUser(userId);

      Stage baseStage = (Stage) tfUsername.getScene().getWindow();
      FXMLLoader loader = new FXMLLoader(
          ProfessionalPractices.class.getResource("view/coordinator/FXMLCoordinatorMainScreen.fxml"));
      Parent view = loader.load();

      Scene mainScene = new Scene(view);
      baseStage.setScene(mainScene);
      baseStage.setTitle("Página Principal Coordinador");
      baseStage.show();
    } catch (IOException ex) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos, no se pudo mostrar la ventana.");
    } catch (SQLException e) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar el usuario",
          "Lo sentimos, no se pudo cargar la información del usuario.");

    }
  }

  private void closeWindow() {
    Stage stage = (Stage) lbErrorPassword.getScene().getWindow();
    stage.close();
  }
}