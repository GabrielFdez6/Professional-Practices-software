package professionalpractice.controller.coordinator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import professionalpractice.model.dao.ProjectDAO;
import professionalpractice.model.dao.interfaces.IProjectDAO;
import professionalpractice.model.pojo.LinkedOrganization;
import professionalpractice.model.pojo.Project;
import professionalpractice.model.pojo.ProjectManager;
import professionalpractice.utils.Constants;
import professionalpractice.utils.EntityValidationUtils;
import professionalpractice.utils.SecurityValidationUtils;
import professionalpractice.utils.Utils;
import professionalpractice.utils.ValidationUtils;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLProjectFormController implements Initializable {

  @FXML
  private TextField tfAvailability;
  @FXML
  private TextArea taDescription;
  @FXML
  private TextField tfProjectName;
  @FXML
  private TextField tfMethodology;

  @FXML
  private Label lblFormTitle;
  @FXML
  private Label lblOrgInfo;
  @FXML
  private Label lblPMInfo;

  private LinkedOrganization currentOrganization;
  private ProjectManager currentProjectManager;
  private Project projectToEdit;
  private boolean isEditMode;
  private IProjectDAO projectDAO;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    projectDAO = new ProjectDAO();
  }

  public void initData(boolean isEdit, LinkedOrganization organization, ProjectManager manager, Project project) {
    this.isEditMode = isEdit;
    this.currentOrganization = organization;
    this.currentProjectManager = manager;
    this.projectToEdit = project;

    if (lblOrgInfo != null) {
      lblOrgInfo.setText("Organización: " + (organization != null ? organization.getName() : "N/A"));
    }
    if (lblPMInfo != null) {
      lblPMInfo.setText(
          "Responsable: " + (manager != null ? manager.getFirstName() + " " + manager.getLastNameFather() : "N/A"));
    }

    if (isEdit) {
      lblFormTitle.setText("MODIFICAR PROYECTO");
      if (projectToEdit != null) {
        tfProjectName.setText(projectToEdit.getName());
        taDescription.setText(projectToEdit.getDescription());
        tfMethodology.setText(projectToEdit.getMethodology());
        tfAvailability.setText(String.valueOf(projectToEdit.getAvailability()));
      }
    } else {
      lblFormTitle.setText("REGISTRAR NUEVO PROYECTO");
      clearFields();
    }
  }

  private void clearFields() {
    tfProjectName.clear();
    taDescription.clear();
    tfMethodology.clear();
    tfAvailability.clear();
  }

  @FXML
  public void btnCancel(ActionEvent actionEvent) {
    if (Utils.showConfirmationAlert("Cancelar operación",
        "¿Estás seguro que quieres cancelar? Se perderán los cambios no guardados.")) {
      Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
      stage.close();
    }
  }

  @FXML
  public void btnRegisterProject(ActionEvent actionEvent) {
    if (validateProjectFields()) {
      try {
        Project project = createProjectFromForm();

        // Validación completa de la entidad usando nuestras validaciones rigurosas
        List<String> entityErrors = EntityValidationUtils.validateProject(project);
        if (EntityValidationUtils.hasValidationErrors(entityErrors)) {
          String formattedErrors = EntityValidationUtils.formatValidationErrors(entityErrors);
          Utils.showSimpleAlert(Alert.AlertType.WARNING, "Errores de Validación", formattedErrors);
          return;
        }

        int response;
        if (isEditMode) {
          response = updateProject(project);
        } else {
          response = saveNewProject(project);
        }

        if (response == Constants.OPERATION_SUCCESFUL) {
          String message = isEditMode ? Constants.SUCCESS_RECORD_UPDATED : Constants.SUCCESS_RECORD_CREATED;
          Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Operación Exitosa", message);
          closeWindow(actionEvent);
        } else {
          Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error en la Operación",
              isEditMode ? "No se pudo actualizar el proyecto." : "No se pudo registrar el proyecto.");
        }

      } catch (SQLException e) {
        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Base de Datos",
            Constants.ERROR_DATABASE_CONNECTION);
        System.err.println("Error de base de datos en formulario de proyecto: " + e.getMessage());
        e.printStackTrace();
      } catch (NumberFormatException e) {
        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Formato",
            "La disponibilidad debe ser un número válido.");
      } catch (Exception e) {
        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error Inesperado",
            "Ocurrió un error inesperado. Por favor, intente más tarde.");
        System.err.println("Error inesperado en formulario de proyecto: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  private boolean validateProjectFields() {
    List<String> errors = new ArrayList<>();

    // Obtener valores de los campos
    String name = tfProjectName.getText();
    String description = taDescription.getText();
    String methodology = tfMethodology.getText();
    String availabilityText = tfAvailability.getText();

    // Validar nombre del proyecto
    String nameError = ValidationUtils.validateProjectName(name);
    if (!nameError.isEmpty())
      errors.add(nameError);

    // Validar descripción del proyecto
    String descriptionError = ValidationUtils.validateProjectDescription(description);
    if (!descriptionError.isEmpty())
      errors.add(descriptionError);

    // Validar metodología
    String methodologyError = ValidationUtils.validateMethodology(methodology);
    if (!methodologyError.isEmpty())
      errors.add(methodologyError);

    // Validar disponibilidad
    if (availabilityText == null || availabilityText.trim().isEmpty()) {
      errors.add("La disponibilidad es un campo obligatorio.");
    } else {
      try {
        int availability = Integer.parseInt(availabilityText.trim());
        String availabilityError = ValidationUtils.validateAvailability(availability);
        if (!availabilityError.isEmpty())
          errors.add(availabilityError);
      } catch (NumberFormatException e) {
        errors.add("La disponibilidad debe ser un número entero válido.");
      }
    }

    // Validaciones de seguridad avanzadas
    List<String> securityErrors = new ArrayList<>();
    securityErrors.addAll(SecurityValidationUtils.performComprehensiveSecurityValidation(name, "Nombre del proyecto"));
    securityErrors.addAll(SecurityValidationUtils.performComprehensiveSecurityValidation(description, "Descripción"));
    securityErrors.addAll(SecurityValidationUtils.performComprehensiveSecurityValidation(methodology, "Metodología"));

    if (!securityErrors.isEmpty()) {
      errors.addAll(securityErrors);
    }

    // Validaciones de contexto (verificar que tenemos organización y responsable)
    if (currentOrganization == null || currentProjectManager == null) {
      errors.add("Error de contexto: No se pudo asociar el proyecto a una organización o responsable.");
    }

    // Mostrar errores si los hay
    if (!errors.isEmpty()) {
      String allErrors = String.join("\n", errors);
      Utils.showSimpleAlert(Alert.AlertType.WARNING, "Errores de Validación", allErrors);
      return false;
    }

    // Sanitizar los campos antes de proceder
    sanitizeInputFields();

    return true;
  }

  private void sanitizeInputFields() {
    tfProjectName.setText(SecurityValidationUtils.advancedSanitization(tfProjectName.getText()));
    taDescription.setText(SecurityValidationUtils.advancedSanitization(taDescription.getText()));
    tfMethodology.setText(SecurityValidationUtils.advancedSanitization(tfMethodology.getText()));
    tfAvailability.setText(ValidationUtils.sanitizeInput(tfAvailability.getText()));
  }

  private Project createProjectFromForm() {
    Project project = isEditMode ? projectToEdit : new Project();

    project.setName(tfProjectName.getText().trim());
    project.setDescription(taDescription.getText().trim());
    project.setMethodology(tfMethodology.getText().trim());
    project.setAvailability(Integer.parseInt(tfAvailability.getText().trim()));

    if (!isEditMode) {
      project.setIdLinkedOrganization(currentOrganization.getIdLinkedOrganization());
      project.setIdProjectManager(currentProjectManager.getIdProjectManager());
    }

    return project;
  }

  private int updateProject(Project project) throws SQLException {
    // Validar que tenemos permisos para actualizar
    if (projectToEdit == null) {
      throw new IllegalStateException("No se puede actualizar un proyecto que no existe.");
    }

    return projectDAO.updateProject(project);
  }

  private int saveNewProject(Project project) throws SQLException {
    // Verificar que no se exceda la capacidad del sistema
    // En un sistema real, aquí verificarías límites por organización, etc.

    return projectDAO.saveProject(project);
  }

  private void closeWindow(ActionEvent event) {
    Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
    stage.close();
  }
}