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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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

public class FXMLListOVRegisterProjectController implements Initializable {

    @FXML private TableView<LinkedOrganization> tvLinkedOrganizations;
    @FXML private TableColumn<LinkedOrganization, String> colCompanyName;
    @FXML private TableColumn<LinkedOrganization, String> colAddress;

    private ObservableList<LinkedOrganization> organizations;
    private ILinkedOrganizationDAO organizationDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        organizations = FXCollections.observableArrayList();
        organizationDAO = new LinkedOrganizationDAO();
        colCompanyName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        loadOrganizationsData();
    }

    private void loadOrganizationsData() {
        try {
            List<LinkedOrganization> orgsList = organizationDAO.getAllActiveOrganizations();
            if(orgsList.isEmpty()){
                Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Sin Organizaciones", "No hay organizaciones vinculadas registradas en el sistema.");
            }
            organizations.setAll(orgsList);
            tvLinkedOrganizations.setItems(organizations);
        } catch (SQLException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Carga", "No se pudieron cargar las organizaciones desde la base de datos.");
        }
    }

    @FXML
    public void btnCancel(ActionEvent actionEvent) {
        goMainCoordinatorScreen();
    }

    @FXML
    public void btnSelectOV(ActionEvent actionEvent) {
        LinkedOrganization selectedOV = tvLinkedOrganizations.getSelectionModel().getSelectedItem();
        if (selectedOV == null) {
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Selección Requerida", "Por favor, selecciona una organización de la lista.");
            return;
        }

        try {
            // 1. Cargar el FXML de la siguiente ventana (que ahora es FXMLProjectManagerList.fxml)
            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/coordinator/FXMLProjectManagerList.fxml")); // <-- ¡CAMBIO AQUÍ!
            Parent view = loader.load();

            // 2. Obtener el controlador de la siguiente ventana (que ahora es FXMLProjectManagerListController)
            FXMLProjectManagerListController nextController = loader.getController(); // <-- ¡CAMBIO AQUÍ!

            // 3. Pasar la OV seleccionada al siguiente controlador
            nextController.setLinkedOrganization(selectedOV); // Este método ya existe en FXMLProjectManagerListController

            // 4. Mostrar la nueva ventana
            Stage baseStage = (Stage) tvLinkedOrganizations.getScene().getWindow();
            Scene mainScene = new Scene(view);
            baseStage.setScene(mainScene);
            // El título dinámico lo maneja el FXMLProjectManagerListController
            baseStage.setTitle("Seleccionar Responsable de Proyecto");
            baseStage.show();

        } catch (IOException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos, no se pudo mostrar la ventana.");
            ex.printStackTrace();
        }
    }

    private void goMainCoordinatorScreen(){
        try {
            Stage stage = (Stage) tvLinkedOrganizations.getScene().getWindow();
            Parent view = FXMLLoader.load(ProfessionalPractices.class.getResource("view/coordinator/FXMLCoordinatorMainScreen.fxml"));
            Scene scene = new Scene(view);
            stage.setScene(scene);
            stage.setTitle("Página Principal Coordinador");
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}