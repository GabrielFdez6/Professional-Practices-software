package professionalpractice.controller.coordinator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import professionalpractice.model.dao.ProjectManagerDAO;
import professionalpractice.model.dao.interfaces.IProjectManagerDAO;
import professionalpractice.model.pojo.ProjectManager;
import professionalpractice.utils.Constants;
import professionalpractice.utils.EntityValidationUtils;
import professionalpractice.utils.SecurityValidationUtils;
import professionalpractice.utils.Utils;
import professionalpractice.utils.ValidationUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FXMLUpdateResponsibleController {

  @FXML
  private TextField tfFirstName;
  @FXML
  private TextField tfLastNameFather;
  @FXML
  private TextField tfLastNameMother;
  @FXML
  private TextField tfPosition;
  @FXML
  private TextField tfEmail;
  @FXML
  private TextField tfPhone;

  private ProjectManager managerToUpdate;
  private IProjectManagerDAO projectManagerDAO;
  private boolean updateSuccessful = false;

  @FXML
  public void initialize() {
    projectManagerDAO = new ProjectManagerDAO();
    setupFieldValidations();
  }

  private void setupFieldValidations() {
    // Configurar validaciones en tiempo real para campos críticos
    tfEmail.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) { // Cuando pierde el foco
        validateEmailField();
      }
    });

    tfPhone.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        validatePhoneField();
      }
    });

    // Validar nombres en tiempo real
    setupNameFieldValidation(tfFirstName, "Nombre");
    setupNameFieldValidation(tfLastNameFather, "Apellido Paterno");
    setupNameFieldValidation(tfLastNameMother, "Apellido Materno");

    // Validar posición
    tfPosition.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        validatePositionField();
      }
    });
  }

  private void validateEmailField() {
    String email = tfEmail.getText();
    if (email != null && !email.trim().isEmpty()) {
      String emailError = ValidationUtils.validateEmail(email, true);
      if (!emailError.isEmpty()) {
        Utils.showSimpleAlert(Alert.AlertType.WARNING, "Email Inválido", emailError);
        tfEmail.requestFocus();
      }
    }
  }

  private void validatePhoneField() {
    String phone = tfPhone.getText();
    if (phone != null && !phone.trim().isEmpty()) {
      String phoneError = ValidationUtils.validatePhoneNumber(phone, true);
      if (!phoneError.isEmpty()) {
        Utils.showSimpleAlert(Alert.AlertType.WARNING, "Teléfono Inválido", phoneError);
        tfPhone.requestFocus();
      }
    }
  }

  private void setupNameFieldValidation(TextField field, String fieldName) {
    field.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        String name = field.getText();
        if (name != null && !name.trim().isEmpty()) {
          String nameError = ValidationUtils.validatePersonName(name, fieldName, true);
          if (!nameError.isEmpty()) {
            Utils.showSimpleAlert(Alert.AlertType.WARNING, fieldName + " Inválido", nameError);
            field.requestFocus();
          }
        }
      }
    });
  }

  private void validatePositionField() {
    String position = tfPosition.getText();
    if (position != null && !position.trim().isEmpty()) {
      String positionError = ValidationUtils.validatePosition(position);
      if (!positionError.isEmpty()) {
        Utils.showSimpleAlert(Alert.AlertType.WARNING, "Cargo Inválido", positionError);
        tfPosition.requestFocus();
      }
    }
  }

  public void initData(ProjectManager manager) {
    if (manager == null) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Datos",
          "No se pudo cargar la información del responsable.");
      closeWindow();
      return;
    }

    this.managerToUpdate = manager;

    // Cargar datos de forma segura
    tfFirstName.setText(manager.getFirstName() != null ? manager.getFirstName() : "");
    tfLastNameFather.setText(manager.getLastNameFather() != null ? manager.getLastNameFather() : "");
    tfLastNameMother.setText(manager.getLastNameMother() != null ? manager.getLastNameMother() : "");
    tfPosition.setText(manager.getPosition() != null ? manager.getPosition() : "");
    tfEmail.setText(manager.getEmail() != null ? manager.getEmail() : "");
    tfPhone.setText(manager.getPhone() != null ? manager.getPhone() : "");
  }

  @FXML
  void btnActualizarClick(ActionEvent event) {
    if (performComprehensiveValidation()) {
      try {
        ProjectManager updatedManager = createUpdatedManagerFromForm();

        // Validación completa de la entidad usando nuestras validaciones rigurosas
        List<String> entityErrors = EntityValidationUtils.validateProjectManager(updatedManager);
        if (EntityValidationUtils.hasValidationErrors(entityErrors)) {
          String formattedErrors = EntityValidationUtils.formatValidationErrors(entityErrors);
          Utils.showSimpleAlert(Alert.AlertType.WARNING, "Errores de Validación", formattedErrors);
          return;
        }

        int result = projectManagerDAO.updateProjectManager(updatedManager);
        if (result == Constants.OPERATION_SUCCESFUL) {
          updateSuccessful = true;
          Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Actualización Exitosa",
              Constants.SUCCESS_RECORD_UPDATED +
                  "\nLos datos del responsable han sido actualizados correctamente.");
          closeWindow();
        } else {
          Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error en la Actualización",
              "No se pudo actualizar la información del responsable. Intente nuevamente.");
        }

      } catch (SQLException e) {
        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Base de Datos",
            Constants.ERROR_DATABASE_CONNECTION);
        System.err.println("Error de base de datos al actualizar responsable: " + e.getMessage());
        e.printStackTrace();
      } catch (Exception e) {
        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error Inesperado",
            "Ocurrió un error inesperado al actualizar el responsable.");
        System.err.println("Error inesperado al actualizar responsable: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  private boolean performComprehensiveValidation() {
    List<String> errors = new ArrayList<>();

    // Validar contexto - que tengamos un responsable para actualizar
    if (managerToUpdate == null) {
      errors.add("Error de contexto: No hay responsable seleccionado para actualizar.");
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Contexto", "No hay responsable para actualizar.");
      return false;
    }

    // Validar campos obligatorios
    validateRequiredFields(errors);

    // Validar contenido de todos los campos
    validateAllFieldsContent(errors);

    // Validaciones de seguridad
    performSecurityValidations(errors);

    // Mostrar errores si los hay
    if (!errors.isEmpty()) {
      String allErrors = String.join("\n", errors);
      Utils.showSimpleAlert(Alert.AlertType.WARNING, "Errores de Validación", allErrors);
      return false;
    }

    // Sanitizar campos antes de proceder
    sanitizeAllFields();

    return true;
  }

  private void validateRequiredFields(List<String> errors) {
    if (tfFirstName.getText() == null || tfFirstName.getText().trim().isEmpty()) {
      errors.add("El nombre es un campo obligatorio.");
    }

    if (tfLastNameFather.getText() == null || tfLastNameFather.getText().trim().isEmpty()) {
      errors.add("El apellido paterno es un campo obligatorio.");
    }

    if (tfEmail.getText() == null || tfEmail.getText().trim().isEmpty()) {
      errors.add("El correo electrónico es un campo obligatorio.");
    }

    if (tfPosition.getText() == null || tfPosition.getText().trim().isEmpty()) {
      errors.add("El cargo/posición es un campo obligatorio.");
    }
  }

  private void validateAllFieldsContent(List<String> errors) {

    String firstName = tfFirstName.getText();
    if (firstName != null && !firstName.trim().isEmpty()) {
      String nameError = ValidationUtils.validateFirstName(firstName);
      if (!nameError.isEmpty()) {
        errors.add("Nombre: " + nameError);
      }
    }

    String lastNameFather = tfLastNameFather.getText();
    if (lastNameFather != null && !lastNameFather.trim().isEmpty()) {
      String lastNameError = ValidationUtils.validateLastNameFather(lastNameFather);
      if (!lastNameError.isEmpty()) {
        errors.add("Apellido Paterno: " + lastNameError);
      }
    }

    String lastNameMother = tfLastNameMother.getText();
    if (lastNameMother != null && !lastNameMother.trim().isEmpty()) {
      String lastNameMotherError = ValidationUtils.validateLastNameMother(lastNameMother);
      if (!lastNameMotherError.isEmpty()) {
        errors.add("Apellido Materno: " + lastNameMotherError);
      }
    }

    String email = tfEmail.getText();
    if (email != null && !email.trim().isEmpty()) {
      String emailError = ValidationUtils.validateEmail(email, true);
      if (!emailError.isEmpty()) {
        errors.add("Email: " + emailError);
      }
    }

    // Validar teléfono (opcional)
    String phone = tfPhone.getText();
    if (phone != null && !phone.trim().isEmpty()) {
      String phoneError = ValidationUtils.validatePhoneNumber(phone, true);
      if (!phoneError.isEmpty()) {
        errors.add("Teléfono: " + phoneError);
      }
    }

    // Validar posición/cargo
    String position = tfPosition.getText();
    if (position != null && !position.trim().isEmpty()) {
      String positionError = ValidationUtils.validatePosition(position);
      if (!positionError.isEmpty()) {
        errors.add("Cargo: " + positionError);
      }
    }
  }

  private void performSecurityValidations(List<String> errors) {
    // Validaciones de seguridad en todos los campos de texto
    String[] fieldNames = { "Nombre", "Apellido Paterno", "Apellido Materno", "Cargo", "Email", "Teléfono" };
    TextField[] fields = { tfFirstName, tfLastNameFather, tfLastNameMother, tfPosition, tfEmail, tfPhone };

    for (int i = 0; i < fields.length; i++) {
      String text = fields[i].getText();
      if (text != null && !text.isEmpty()) {
        List<String> securityErrors = SecurityValidationUtils.performComprehensiveSecurityValidation(
            text, fieldNames[i]);
        errors.addAll(securityErrors);
      }
    }
  }

  private void sanitizeAllFields() {
    // Sanitizar todos los campos de texto
    if (tfFirstName.getText() != null) {
      tfFirstName.setText(SecurityValidationUtils.advancedSanitization(tfFirstName.getText()));
    }
    if (tfLastNameFather.getText() != null) {
      tfLastNameFather.setText(SecurityValidationUtils.advancedSanitization(tfLastNameFather.getText()));
    }
    if (tfLastNameMother.getText() != null) {
      tfLastNameMother.setText(SecurityValidationUtils.advancedSanitization(tfLastNameMother.getText()));
    }
    if (tfPosition.getText() != null) {
      tfPosition.setText(SecurityValidationUtils.advancedSanitization(tfPosition.getText()));
    }
    if (tfEmail.getText() != null) {
      tfEmail.setText(ValidationUtils.sanitizeInput(tfEmail.getText()));
    }
    if (tfPhone.getText() != null) {
      tfPhone.setText(ValidationUtils.sanitizeInput(tfPhone.getText()));
    }
  }

  private ProjectManager createUpdatedManagerFromForm() {
    ProjectManager updatedManager = new ProjectManager();

    // Mantener el ID original y el ID de la organización vinculada
    updatedManager.setIdProjectManager(managerToUpdate.getIdProjectManager());
    updatedManager.setIdLinkedOrganization(managerToUpdate.getIdLinkedOrganization());

    // Asignar valores sanitizados
    updatedManager.setFirstName(tfFirstName.getText().trim());
    updatedManager.setLastNameFather(tfLastNameFather.getText().trim());

    String lastNameMother = tfLastNameMother.getText();
    updatedManager
        .setLastNameMother(lastNameMother != null && !lastNameMother.trim().isEmpty() ? lastNameMother.trim() : null);

    updatedManager.setPosition(tfPosition.getText().trim());
    updatedManager.setEmail(tfEmail.getText().trim().toLowerCase()); // Email en minúsculas

    String phone = tfPhone.getText();
    updatedManager.setPhone(phone != null && !phone.trim().isEmpty() ? phone.trim() : null);

    return updatedManager;
  }

  @FXML
  void btnCancelarClick(ActionEvent event) {
    if (Utils.showConfirmationAlert("Cancelar Actualización",
        "¿Estás seguro que quieres cancelar la actualización?\n" +
            "Se perderán los cambios realizados.",
        "Cualquier dato no guardado se perderá.")) {
      closeWindow();
    }
  }

  private void closeWindow() {
    Stage stage = (Stage) tfFirstName.getScene().getWindow();
    stage.close();
  }

  public boolean isUpdateSuccessful() {
    return updateSuccessful;
  }

  private void goToMainCoordinatorScreen() {
    try {
      // Cerrar la ventana actual
      Stage currentStage = (Stage) tfFirstName.getScene().getWindow();
      currentStage.close();

      // Abrir nueva ventana principal del coordinador
      javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(
          "/professionalpractice/view/coordinator/FXMLCoordinatorMainScreen.fxml"));
      javafx.scene.Parent view = loader.load();
      javafx.scene.Scene scene = new javafx.scene.Scene(view);
      Stage newStage = new Stage();
      newStage.setScene(scene);
      newStage.setTitle("Página Principal Coordinador");
      newStage.show();
    } catch (java.io.IOException ex) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Navegación",
          "No se pudo regresar al menú principal.");
      System.err.println("Error al navegar al menú principal: " + ex.getMessage());
      ex.printStackTrace();
    }
  }
}