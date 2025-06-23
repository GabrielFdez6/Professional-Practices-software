package professionalpractice.controller.coordinator;

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
import professionalpractice.model.dao.CoordinatorDAO;
import professionalpractice.model.dao.interfaces.ICoordinatorDAO;
import professionalpractice.model.pojo.Coordinator;
import professionalpractice.utils.Utils;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class FXMLCoordinatorMainScreenController implements Initializable {

  @FXML
  private Label lbWelcome;
  @FXML
  private Label lbFullName;
  private ICoordinatorDAO coordinatorDAO;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    this.coordinatorDAO = new CoordinatorDAO();
    int userId = SesionUsuario.getInstancia().getIdUsuario();

    if (userId > 0) {
      try {
        Coordinator coordinator = coordinatorDAO.getCoordinatorByIdUser(userId);
        configureScreen(coordinator);
      } catch (SQLException e) {
        e.printStackTrace();
        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Base de Datos",
            "No se pudo cargar la información del coordinador.");
      }
    }
  }

  private void configureScreen(Coordinator coordinator) {
    if (coordinator != null) {
      lbFullName.setText(coordinator.getFirstName() + " " + coordinator.getLastNameFather());
      lbWelcome.setText("Bienvenido(a) " + coordinator.getFirstName() + " " + coordinator.getLastNameFather());
    } else {
      lbFullName.setText("Coordinador Desconocido");
    }
  }

  @FXML
  void btnScheduleDelivery(ActionEvent event) {
    try {
      Stage modalStage = new Stage();
      modalStage.initModality(Modality.APPLICATION_MODAL);
      modalStage.initOwner(Utils.getSceneComponent(lbFullName));

      FXMLLoader loader = new FXMLLoader(
          ProfessionalPractices.class.getResource("view/coordinator/FXMLScheduleDelivery.fxml"));
      System.out.println(ProfessionalPractices.class.getResource("view/coordinator/FXMLScheduleDelivery.fxml"));
      Parent view = loader.load();

      Stage stage = new Stage();
      stage.setTitle("Programar Nueva Entrega");
      stage.setScene(new Scene(view));
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.showAndWait();

    } catch (IOException ex) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar",
          "Lo sentimos, no se pudo mostrar la ventana de programación.");
      ex.printStackTrace();
    }
  }

  @FXML
  void btnAssignProjects(ActionEvent event) {
    try {
      Stage baseStage = (Stage) lbFullName.getScene().getWindow();
      FXMLLoader loader = new FXMLLoader(
          ProfessionalPractices.class.getResource("view/coordinator/FXMLInfoStudentsProject.fxml"));
      Parent view = loader.load();
      Scene mainScene = new Scene(view);
      baseStage.setScene(mainScene);
      baseStage.setTitle("Asignar estudiantes");
      baseStage.show();
    } catch (IOException ex) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos, no se pudo mostrar la ventana.");
      ex.printStackTrace();
    }
  }

  @FXML
  void btnRegisterProject(ActionEvent event) {
    try {
      Stage baseStage = (Stage) lbFullName.getScene().getWindow();
      FXMLLoader loader = new FXMLLoader(
          ProfessionalPractices.class.getResource("view/coordinator/FXMLListOVRegisterProject.fxml"));
      Parent view = loader.load();
      Scene mainScene = new Scene(view);
      baseStage.setScene(mainScene);
      baseStage.setTitle("Lista Organización Vinculada");
      baseStage.show();
    } catch (IOException ex) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos, no se pudo mostrar la ventana.");
      ex.printStackTrace();
    }
  }

  @FXML
  void btnRegisterResponsible(ActionEvent event) {
    try {
      Stage baseStage = (Stage) lbFullName.getScene().getWindow();
      FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/coordinator/FXMLListOV.fxml"));
      Parent view = loader.load();
      Scene mainScene = new Scene(view);
      baseStage.setScene(mainScene);
      baseStage.setTitle("Seleccionar Organización Vinculada");
      baseStage.show();
    } catch (IOException ex) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos, no se pudo mostrar la ventana.");
      ex.printStackTrace();
    }
  }

  @FXML
  void btnUpdateResponsible(ActionEvent event) {
    try {
      Stage baseStage = (Stage) lbFullName.getScene().getWindow();
      FXMLLoader loader = new FXMLLoader(
          ProfessionalPractices.class.getResource("view/coordinator/FXMLSelectOVForUpdateResponsible.fxml"));
      Parent view = loader.load();
      Scene mainScene = new Scene(view);
      baseStage.setScene(mainScene);
      baseStage.setTitle("Seleccionar Organización Vinculada - Actualizar Responsable");
      baseStage.show();
    } catch (IOException ex) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar",
          "Lo sentimos, no se pudo mostrar la ventana de selección de organización.");
      ex.printStackTrace();
    }
  }

  @FXML
  void btnClickLogOut(ActionEvent event) {
    try {
      Stage baseStage = (Stage) lbFullName.getScene().getWindow();
      FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/FXMLLogIn.fxml"));
      Parent viewLogIn = loader.load();
      Scene LogInScene = new Scene(viewLogIn);
      baseStage.setScene(LogInScene);
      baseStage.setTitle("Inicio de Sesión");
      baseStage.show();
    } catch (IOException ex) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar",
          "Lo sentimos, no se pudo regresar a la pantalla de inicio de sesión.");
    }
  }
}
