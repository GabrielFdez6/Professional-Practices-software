package professionalpractice.controller.coordinator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import professionalpractice.utils.Utils;

import java.net.URL;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    public void btnCancel(ActionEvent actionEvent) {
        Utils.getSceneComponent(tfAvailability).close();
    }

    @FXML
    public void btnRegisterProject(ActionEvent actionEvent) {
    }
}