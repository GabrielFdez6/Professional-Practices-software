package professionalpractice.controller.coordinator;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import professionalpractice.model.dao.LinkedOrganizationDAO;
import professionalpractice.model.dao.ProjectManagerDAO;
import professionalpractice.model.dao.interfaces.IProjectManagerDAO;
import professionalpractice.model.pojo.LinkedOrganization;
import professionalpractice.model.pojo.ProjectManager;
import professionalpractice.utils.Constants;
import professionalpractice.utils.EntityValidationUtils;
import professionalpractice.utils.Utils;
import professionalpractice.utils.ValidationUtils;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLUpdateResponsibleNewController implements Initializable {

  @FXML
  private ComboBox<LinkedOrganization> cbOrganizations;
  @FXML
  private ComboBox<ProjectManager> cbResponsibles;
  @FXML
  private Label lbSelectedInfo;
  @FXML
  private GridPane gridEditFields;
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
  @FXML
  private Button btnActualizar;
  @FXML
  private Label lbMessage;

  private LinkedOrganizationDAO organizationDAO;
  private IProjectManagerDAO projectManagerDAO;
  private ProjectManager selectedManager;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    organizationDAO = new LinkedOrganizationDAO();
    projectManagerDAO = new ProjectManagerDAO();

    setupComboBoxes();
    loadOrganizations();
    setupEventHandlers();
  }

  private void setupComboBoxes() {
    // Configurar ComboBox de organizaciones
    cbOrganizations.setConverter(new StringConverter<LinkedOrganization>() {
      @Override
      public String toString(LinkedOrganization organization) {
        return organization != null ? organization.getName() : "";
      }

      @Override
      public LinkedOrganization fromString(String string) {
        return null; // No necesario para este caso
      }
    });

    // Configurar ComboBox de responsables
    cbResponsibles.setConverter(new StringConverter<ProjectManager>() {
      @Override
      public String toString(ProjectManager manager) {
        return manager != null ? manager.getFullName() + " - " + manager.getPosition() : "";
      }

      @Override
      public ProjectManager fromString(String string) {
        return null; // No necesario para este caso
      }
    });
  }

  private void setupEventHandlers() {
    // Manejador de eventos para cuando se selecciona una organización
    cbOrganizations.setOnAction(event -> {
      LinkedOrganization selectedOrg = cbOrganizations.getSelectionModel().getSelectedItem();
      if (selectedOrg != null) {
        loadResponsiblesByOrganization(selectedOrg.getIdLinkedOrganization());
        cbResponsibles.setDisable(false);
        cbResponsibles.setPromptText("Selecciona un responsable...");
        hideEditFields();
        lbMessage.setText("");
      } else {
        clearResponsibles();
      }
    });

    // Manejador de eventos para cuando se selecciona un responsable
    cbResponsibles.setOnAction(event -> {
      ProjectManager selectedResponsible = cbResponsibles.getSelectionModel().getSelectedItem();
      if (selectedResponsible != null) {
        this.selectedManager = selectedResponsible;
        showResponsibleInfo(selectedResponsible);
        populateEditFields(selectedResponsible);
        showEditFields();
        btnActualizar.setDisable(false);
      } else {
        hideEditFields();
        btnActualizar.setDisable(true);
        lbSelectedInfo.setText("");
      }
    });
  }

  private void loadOrganizations() {
    try {
      List<LinkedOrganization> organizations = organizationDAO.getAllOrganizations();
      cbOrganizations.setItems(FXCollections.observableArrayList(organizations));

      if (organizations.isEmpty()) {
        lbMessage.setText("No se encontraron organizaciones vinculadas activas.");
      }
    } catch (SQLException e) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión",
          "No se pudieron cargar las organizaciones vinculadas.");
      e.printStackTrace();
    }
  }

  private void loadResponsiblesByOrganization(int organizationId) {
    try {
      List<ProjectManager> managers = projectManagerDAO.getProjectManagersByOrganizationId(organizationId);
      cbResponsibles.setItems(FXCollections.observableArrayList(managers));

      if (managers.isEmpty()) {
        lbMessage.setText("No hay responsables de proyecto registrados para esta organización.");
        cbResponsibles.setPromptText("No hay responsables disponibles");
      } else {
        lbMessage.setText("");
        cbResponsibles.setPromptText("Selecciona un responsable...");
      }
    } catch (SQLException e) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión",
          "No se pudieron cargar los responsables de proyecto.");
      e.printStackTrace();
    }
  }

  private void clearResponsibles() {
    cbResponsibles.getItems().clear();
    cbResponsibles.setDisable(true);
    cbResponsibles.setPromptText("Primero selecciona una organización...");
    hideEditFields();
    btnActualizar.setDisable(true);
    lbSelectedInfo.setText("");
  }

  private void showResponsibleInfo(ProjectManager manager) {
    String info = String.format("Responsable seleccionado: %s - %s | Email: %s | Teléfono: %s",
        manager.getFullName(),
        manager.getPosition() != null ? manager.getPosition() : "Sin cargo especificado",
        manager.getEmail() != null ? manager.getEmail() : "Sin email",
        manager.getPhone() != null ? manager.getPhone() : "Sin teléfono");
    lbSelectedInfo.setText(info);
  }

  private void populateEditFields(ProjectManager manager) {
    tfFirstName.setText(manager.getFirstName() != null ? manager.getFirstName() : "");
    tfLastNameFather.setText(manager.getLastNameFather() != null ? manager.getLastNameFather() : "");
    tfLastNameMother.setText(manager.getLastNameMother() != null ? manager.getLastNameMother() : "");
    tfPosition.setText(manager.getPosition() != null ? manager.getPosition() : "");
    tfEmail.setText(manager.getEmail() != null ? manager.getEmail() : "");
    tfPhone.setText(manager.getPhone() != null ? manager.getPhone() : "");
  }

  private void showEditFields() {
    gridEditFields.setVisible(true);
  }

  private void hideEditFields() {
    gridEditFields.setVisible(false);
  }

  @FXML
  void btnActualizarClick(ActionEvent event) {
    if (selectedManager == null) {
      Utils.showSimpleAlert(Alert.AlertType.WARNING, "Selección Requerida",
          "Debe seleccionar un responsable para actualizar.");
      return;
    }

    if (validateFields()) {
      try {
        ProjectManager updatedManager = createUpdatedManagerFromForm();

        // Validación completa de la entidad
        List<String> entityErrors = EntityValidationUtils.validateProjectManager(updatedManager);
        if (EntityValidationUtils.hasValidationErrors(entityErrors)) {
          String formattedErrors = EntityValidationUtils.formatValidationErrors(entityErrors);
          Utils.showSimpleAlert(Alert.AlertType.WARNING, "Errores de Validación", formattedErrors);
          return;
        }

        int result = projectManagerDAO.updateProjectManager(updatedManager);
        if (result == Constants.OPERATION_SUCCESFUL) {
          Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Actualización Exitosa",
              "Los datos del responsable han sido actualizados correctamente.");

          // Recargar la lista de responsables para reflejar los cambios
          LinkedOrganization selectedOrg = cbOrganizations.getSelectionModel().getSelectedItem();
          if (selectedOrg != null) {
            loadResponsiblesByOrganization(selectedOrg.getIdLinkedOrganization());
          }
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

  private boolean validateFields() {
    List<String> errors = new ArrayList<>();

    // Validar campos obligatorios
    String firstNameError = ValidationUtils.validateFirstName(tfFirstName.getText());
    if (!firstNameError.isEmpty())
      errors.add(firstNameError);

    String lastNameFatherError = ValidationUtils.validateLastNameFather(tfLastNameFather.getText());
    if (!lastNameFatherError.isEmpty())
      errors.add(lastNameFatherError);

    // Campos opcionales con validación
    String lastNameMotherError = ValidationUtils.validateLastNameMother(tfLastNameMother.getText());
    if (!lastNameMotherError.isEmpty())
      errors.add(lastNameMotherError);

    String positionError = ValidationUtils.validatePosition(tfPosition.getText());
    if (!positionError.isEmpty())
      errors.add(positionError);

    String emailError = ValidationUtils.validateEmail(tfEmail.getText(), true);
    if (!emailError.isEmpty())
      errors.add(emailError);

    String phoneError = ValidationUtils.validatePhoneNumber(tfPhone.getText(), false);
    if (!phoneError.isEmpty())
      errors.add(phoneError);

    if (!errors.isEmpty()) {
      String errorMessage = String.join("\n", errors);
      Utils.showSimpleAlert(Alert.AlertType.WARNING, "Errores de Validación", errorMessage);
      return false;
    }

    return true;
  }

  private ProjectManager createUpdatedManagerFromForm() {
    ProjectManager updatedManager = new ProjectManager();

    // Mantener el ID original
    updatedManager.setIdProjectManager(selectedManager.getIdProjectManager());
    updatedManager.setIdLinkedOrganization(selectedManager.getIdLinkedOrganization());

    // Asignar valores sanitizados
    updatedManager.setFirstName(tfFirstName.getText().trim());
    updatedManager.setLastNameFather(tfLastNameFather.getText().trim());

    String lastNameMother = tfLastNameMother.getText();
    updatedManager.setLastNameMother(lastNameMother != null && !lastNameMother.trim().isEmpty()
        ? lastNameMother.trim()
        : null);

    updatedManager.setPosition(tfPosition.getText().trim());
    updatedManager.setEmail(tfEmail.getText().trim().toLowerCase());

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
    Stage stage = (Stage) cbOrganizations.getScene().getWindow();
    stage.close();
  }
}