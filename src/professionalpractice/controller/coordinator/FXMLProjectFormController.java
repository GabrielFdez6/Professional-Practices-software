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
import professionalpractice.utils.Utils;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class FXMLProjectFormController implements Initializable {


  @FXML
  private TextField tfAvailability;
  @FXML
  private TextArea taDescription;
  @FXML
  private Label lblFormTitle;
  @FXML
  private Label lblPMInfo;
  @FXML
  private TextField tfProjectName;
  @FXML
  private Label lblOrgInfo;
  @FXML
  private TextField tfMethodology;
  private LinkedOrganization currentOrganization;
  private ProjectManager currentProjectManager;
  private Project projectToEdit;
  private boolean isEditMode;
  private IProjectDAO projectDAO;
  @FXML
  private Label lbCharCounter;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    projectDAO = new ProjectDAO();
    setupFieldListeners();
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
      lblPMInfo.setText("Responsable: " + (manager != null ? manager.getFirstName() + " " + manager.getLastNameFather() : "N/A"));
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
      tfProjectName.clear();
      taDescription.clear();
      tfMethodology.clear();
      tfAvailability.clear();
    }
  }

  private void setupFieldListeners() {
    setupDescriptionCharCounter();
    addTextLimiter(tfProjectName, 50, "Nombre del Proyecto");
    addTextLimiter(tfMethodology, 45, "Metodología");
  }

  private void setupDescriptionCharCounter() {
    final int maxLength = 200;
    lbCharCounter.setText("0/" + maxLength);

    taDescription.textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.length() > maxLength) {
        taDescription.setText(newValue.substring(0, maxLength));
      }
      lbCharCounter.setText(taDescription.getText().length() + "/" + maxLength);
    });
  }

  private void addTextLimiter(TextField textField, int maxLength, String fieldName) {
    textField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.length() > maxLength) {
        String limitedText = newValue.substring(0, maxLength);
        textField.setText(limitedText);

        String alertTitle = "Límite de Caracteres Excedido";
        String alertMessage = "El campo '" + fieldName + "' no puede exceder los " + maxLength + " caracteres.";
        Utils.showSimpleAlert(Alert.AlertType.WARNING, alertTitle, alertMessage);
      }
    });
  }


  @FXML
  public void btnCancel(ActionEvent actionEvent) {
    if (Utils.showConfirmationAlert("Cancelar operación",
            "¿Estás seguro que quieres cancelar? Se perderán los cambios no guardados.")) {
      closeWindow(actionEvent);
    }
  }

  private Optional<Integer> validateAvailability(String availabilityStr) {
    try {
      int availability = Integer.parseInt(availabilityStr);
      if (availability > 0 && availability <= 5) {
        return Optional.of(availability);
      } else {
        Utils.showSimpleAlert(Alert.AlertType.WARNING, "Disponibilidad Inválida", "La disponibilidad debe ser un número positivo, mayor a 0 y no mayor a 5.");
        return Optional.empty();
      }
    } catch (NumberFormatException e) {
      Utils.showSimpleAlert(Alert.AlertType.WARNING, "Disponibilidad Inválida", "La disponibilidad debe ser un número válido entre 1 y 5.");
      return Optional.empty();
    }
  }

  @FXML
  public void btnRegisterProject(ActionEvent actionEvent) {
    String name = tfProjectName.getText().trim();
    String description = taDescription.getText().trim();
    String methodology = tfMethodology.getText().trim();
    String availabilityStr = tfAvailability.getText().trim();

    if (name.isEmpty() || description.isEmpty() || methodology.isEmpty() || availabilityStr.isEmpty()) {
      Utils.showSimpleAlert(Alert.AlertType.WARNING, "Campos Vacíos", "Por favor, completa todos los campos.");
      return;
    }

    Optional<Integer> availabilityOptional = validateAvailability(availabilityStr);
    if (!availabilityOptional.isPresent()) {
      return;
    }
    int availability = availabilityOptional.get();

    try {
      int response;
      if (isEditMode) {
        projectToEdit.setName(name);
        projectToEdit.setDescription(description);
        projectToEdit.setMethodology(methodology);
        projectToEdit.setAvailability(availability);

        response = projectDAO.updateProject(projectToEdit);
        if (response == Constants.OPERATION_SUCCESFUL) {
          Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Actualización Exitosa", "El proyecto ha sido actualizado correctamente.");
          closeWindow(actionEvent);
        } else {
          Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el proyecto.");
        }

      } else {
        Project newProject = new Project();
        newProject.setName(name);
        newProject.setDescription(description);
        newProject.setMethodology(methodology);
        newProject.setAvailability(availability);

        if (currentOrganization == null || currentProjectManager == null) {
          Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Contexto", "No se pudo asociar el proyecto a una organización o responsable.");
          return;
        }
        newProject.setIdLinkedOrganization(currentOrganization.getIdLinkedOrganization());
        newProject.setIdProjectManager(currentProjectManager.getIdProjectManager());

        response = projectDAO.saveProject(newProject);
        if (response == Constants.OPERATION_SUCCESFUL) {
          Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Registro Exitoso", "El proyecto ha sido registrado correctamente.");
          closeWindow(actionEvent);
        } else {
          Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error", "No se pudo registrar el proyecto.");
        }
      }
    } catch (SQLException e) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Base de Datos", "Ocurrió un error al guardar los datos del proyecto.");
      e.printStackTrace();
    }
  }

  private void closeWindow(ActionEvent event) {
    Stage stage = (Stage) ((javafx.scene.Node)event.getSource()).getScene().getWindow();
    stage.close();
  }
}