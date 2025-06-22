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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import professionalpractice.ProfessionalPractices;
import professionalpractice.model.dao.LinkedOrganizationDAO;
import professionalpractice.model.dao.interfaces.ILinkedOrganizationDAO;
import professionalpractice.model.pojo.LinkedOrganization;
import professionalpractice.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLSelectOVForUpdateResponsibleController implements Initializable {

  @FXML
  private TableView<LinkedOrganization> tvLinkedOrganizations;
  @FXML
  private TableColumn<LinkedOrganization, String> colCompanyName;
  @FXML
  private TableColumn<LinkedOrganization, String> colAddress;
  @FXML
  private TableColumn<LinkedOrganization, String> colPhone;
  @FXML
  private Button btnSelectOV;
  @FXML
  private Button btnCancel;

  private ObservableList<LinkedOrganization> organizations;
  private ILinkedOrganizationDAO organizationDAO;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    organizations = FXCollections.observableArrayList();
    organizationDAO = new LinkedOrganizationDAO();
    configureTable();
    loadOrganizationsData();
  }

  private void configureTable() {
    colCompanyName.setCellValueFactory(new PropertyValueFactory<>("name"));
    colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
    colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

    tvLinkedOrganizations.setItems(organizations);

    // Configurar selección para habilitar/deshabilitar botón
    tvLinkedOrganizations.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
          btnSelectOV.setDisable(newValue == null);
        });

    // Inicialmente deshabilitar el botón de seleccionar
    btnSelectOV.setDisable(true);
  }

  private void loadOrganizationsData() {
    try {
      List<LinkedOrganization> orgsList = organizationDAO.getAllActiveOrganizations();
      if (orgsList.isEmpty()) {
        Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Sin Organizaciones",
            "No hay organizaciones vinculadas registradas en el sistema.");
        btnSelectOV.setDisable(true);
        return;
      }
      organizations.setAll(orgsList);
    } catch (SQLException e) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Carga",
          "No se pudieron cargar las organizaciones desde la base de datos.");
      System.err.println("Error al cargar organizaciones: " + e.getMessage());
      e.printStackTrace();
    }
  }

  @FXML
  private void btnSelectOVClick(ActionEvent event) {
    LinkedOrganization selectedOV = tvLinkedOrganizations.getSelectionModel().getSelectedItem();
    if (selectedOV == null) {
      Utils.showSimpleAlert(Alert.AlertType.WARNING, "Selección Requerida",
          "Por favor, selecciona una organización de la lista.");
      return;
    }

    try {
      // Cargar la vista de lista de responsables filtrada por OV
      FXMLLoader loader = new FXMLLoader(getClass().getResource(
          "/professionalpractice/view/coordinator/FXMLFilteredProjectManagerListUpdate.fxml"));
      Parent view = loader.load();

      FXMLFilteredProjectManagerListUpdateController controller = loader.getController();
      controller.initData(selectedOV);

      Stage stage = new Stage();
      stage.setScene(new Scene(view));
      stage.setTitle("Responsables de " + selectedOV.getName());
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.show();

      // Cerrar la ventana actual
      closeWindow();

    } catch (IOException ex) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Navegación",
          "No se pudo cargar la ventana de responsables de proyecto.");
      System.err.println("Error al cargar ventana de responsables: " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  @FXML
  private void btnCancelClick(ActionEvent event) {
    if (Utils.showConfirmationAlert("Cancelar Operación",
        "¿Estás seguro de que quieres cancelar?",
        "Regresarás al menú principal.")) {
      goToMainCoordinatorScreen();
    }
  }

  private void goToMainCoordinatorScreen() {
    try {
      // Cerrar la ventana actual
      Stage currentStage = (Stage) tvLinkedOrganizations.getScene().getWindow();
      currentStage.close();

      // Abrir nueva ventana principal del coordinador
      Parent view = FXMLLoader.load(getClass().getResource(
          "/professionalpractice/view/coordinator/FXMLCoordinatorMainScreen.fxml"));
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

  private void closeWindow() {
    Stage stage = (Stage) tvLinkedOrganizations.getScene().getWindow();
    stage.close();
  }
}