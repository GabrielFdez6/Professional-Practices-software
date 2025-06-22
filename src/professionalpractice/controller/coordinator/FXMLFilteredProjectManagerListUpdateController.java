package professionalpractice.controller.coordinator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import professionalpractice.ProfessionalPractices;
import professionalpractice.model.dao.ProjectManagerDAO;
import professionalpractice.model.dao.interfaces.IProjectManagerDAO;
import professionalpractice.model.pojo.LinkedOrganization;
import professionalpractice.model.pojo.ProjectManager;
import professionalpractice.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLFilteredProjectManagerListUpdateController implements Initializable {

  @FXML
  private TableView<ProjectManager> tvProjectManagers;
  @FXML
  private TableColumn<ProjectManager, String> colManagerName;
  @FXML
  private TableColumn<ProjectManager, String> colPosition;
  @FXML
  private TableColumn<ProjectManager, String> colEmail;
  @FXML
  private TableColumn<ProjectManager, String> colPhoneNumber;
  @FXML
  private TableColumn<ProjectManager, String> colOrganization;
  @FXML
  private Button btnUpdateResponsible;
  @FXML
  private Button btnBack;
  @FXML
  private Label lbOrganizationName;
  @FXML
  private Label lbNoResponsibles;

  private IProjectManagerDAO projectManagerDAO;
  private ObservableList<ProjectManager> projectManagers;
  private LinkedOrganization selectedOrganization;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    projectManagerDAO = new ProjectManagerDAO();
    projectManagers = FXCollections.observableArrayList();
    configureTable();
  }

  private void configureTable() {
    // Configurar las columnas con custom cell value factories para mostrar el
    // nombre completo
    colManagerName.setCellValueFactory(cellData -> {
      ProjectManager manager = cellData.getValue();
      String fullName = manager.getFirstName();
      if (manager.getLastNameFather() != null && !manager.getLastNameFather().isEmpty()) {
        fullName += " " + manager.getLastNameFather();
      }
      if (manager.getLastNameMother() != null && !manager.getLastNameMother().isEmpty()) {
        fullName += " " + manager.getLastNameMother();
      }
      return new javafx.beans.property.SimpleStringProperty(fullName);
    });

    colPosition.setCellValueFactory(new PropertyValueFactory<>("position"));
    colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phone"));
    colOrganization.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
        selectedOrganization != null ? selectedOrganization.getName() : ""));

    tvProjectManagers.setItems(projectManagers);

    // Configurar selección para habilitar/deshabilitar botón
    tvProjectManagers.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
          btnUpdateResponsible.setDisable(newValue == null);
        });

    // Inicialmente deshabilitar el botón de actualizar
    btnUpdateResponsible.setDisable(true);
  }

  public void initData(LinkedOrganization organization) {
    this.selectedOrganization = organization;
    if (organization != null) {
      lbOrganizationName.setText("Responsables de " + organization.getName());
      loadProjectManagersByOrganization(organization.getIdLinkedOrganization());
    }
  }

  private void loadProjectManagersByOrganization(int organizationId) {
    try {
      List<ProjectManager> managers = projectManagerDAO.getProjectManagersByOrganizationId(organizationId);

      if (managers.isEmpty()) {
        // Mostrar mensaje de que no hay responsables
        lbNoResponsibles.setVisible(true);
        tvProjectManagers.setVisible(false);
        btnUpdateResponsible.setDisable(true);
      } else {
        // Ocultar mensaje y mostrar tabla
        lbNoResponsibles.setVisible(false);
        tvProjectManagers.setVisible(true);
        projectManagers.setAll(managers);
      }

    } catch (SQLException e) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión",
          "No se pudieron cargar los responsables de proyecto para esta organización.");
      System.err.println("Error al cargar responsables por organización: " + e.getMessage());
      e.printStackTrace();
    }
  }

  @FXML
  private void btnUpdateResponsibleClick(ActionEvent event) {
    ProjectManager selectedManager = tvProjectManagers.getSelectionModel().getSelectedItem();
    if (selectedManager == null) {
      Utils.showSimpleAlert(Alert.AlertType.WARNING, "Selección Requerida",
          "Por favor, selecciona un responsable de proyecto de la lista.");
      return;
    }

    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(
          "/professionalpractice/view/coordinator/FXMLUpdateResponsible.fxml"));
      Parent view = loader.load();

      FXMLUpdateResponsibleController controller = loader.getController();
      controller.initData(selectedManager);

      Stage modalStage = new Stage();
      modalStage.initModality(Modality.APPLICATION_MODAL);
      modalStage.initOwner(Utils.getSceneComponent(tvProjectManagers));
      modalStage.setTitle(
          "Actualizar Responsable - " + selectedManager.getFirstName() + " " + selectedManager.getLastNameFather());
      modalStage.setScene(new Scene(view));
      modalStage.showAndWait();

      // Verificar si la actualización fue exitosa
      if (controller.isUpdateSuccessful()) {
        // Si fue exitosa, cerrar esta ventana y abrir el menú principal
        goToMainCoordinatorScreen();
      } else {
        // Si no fue exitosa o se canceló, solo recargar la lista
        loadProjectManagersByOrganization(selectedOrganization.getIdLinkedOrganization());
      }

    } catch (IOException ex) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar",
          "Lo sentimos, no se pudo mostrar la ventana de actualización del responsable.");
      System.err.println("Error al cargar ventana de actualización: " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  @FXML
  private void btnBackClick(ActionEvent event) {
    try {
      // Regresar a la selección de organizaciones
      FXMLLoader loader = new FXMLLoader(getClass().getResource(
          "/professionalpractice/view/coordinator/FXMLSelectOVForUpdateResponsible.fxml"));
      Parent view = loader.load();

      Stage stage = (Stage) tvProjectManagers.getScene().getWindow();
      stage.setScene(new Scene(view));
      stage.setTitle("Seleccionar Organización Vinculada");
      stage.show();

    } catch (IOException ex) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Navegación",
          "No se pudo regresar a la selección de organizaciones.");
      System.err.println("Error al navegar a selección de organizaciones: " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  private void goToMainCoordinatorScreen() {
    try {
      // Cerrar la ventana actual
      Stage currentStage = (Stage) tvProjectManagers.getScene().getWindow();
      currentStage.close();

      // Abrir nueva ventana principal del coordinador
      FXMLLoader loader = new FXMLLoader(getClass().getResource(
          "/professionalpractice/view/coordinator/FXMLCoordinatorMainScreen.fxml"));
      Parent view = loader.load();
      Scene scene = new Scene(view);
      Stage newStage = new Stage();
      newStage.setScene(scene);
      newStage.setTitle("Página Principal Coordinador");
      newStage.show();
    } catch (IOException ex) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Navegación",
          "No se pudo regresar al menú principal.");
      System.err.println("Error al navegar al menú principal: " + ex.getMessage());
      ex.printStackTrace();
    }
  }
}