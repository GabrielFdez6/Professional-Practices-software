package professionalpractice.controller.evaluator;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLStudentListController implements Initializable {

    @FXML
    private TableColumn colMatriculation;
    @FXML
    private TableView tvStudents;
    @FXML
    private TableColumn colStudentName;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadTableInformation();
    }

    public void loadTableInformation() {

    }

}
