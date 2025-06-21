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
  private Label lblFormTitle;
  @FXML
  private Label lblPMInfo;
  @FXML
  private TextField tfProjectName;
  @FXML
  private Label lblOrgInfo;
  @FXML
  private TextField tfMethodology;

  @Override
  public void initialize(URL url, ResourceBundle rb) {

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
  }
}