package professionalpractice.controller.evaluator;


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
import professionalpractice.model.dao.StudentDAO;
import professionalpractice.model.pojo.Student;
import professionalpractice.utils.Utils;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FXMLStudentListController implements Initializable {

    @FXML
    private TableColumn<Student, String> colMatriculation;
    @FXML
    private TableView<Student> tvStudents;
    @FXML
    private TableColumn<Student, String> colStudentName;
    private ObservableList<Student> students;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        students = FXCollections.observableArrayList();
        configureTable();
        loadTableInformation();
    }

    public void configureTable() {
        colMatriculation.setCellValueFactory(new PropertyValueFactory("enrollment"));
        colStudentName.setCellValueFactory(new PropertyValueFactory("fullName"));
    }

    public void loadTableInformation() {
        try {
            ArrayList<Student> studentsFromDB = StudentDAO.getStudentsWithoutEvaluation();
            students.addAll(studentsFromDB);
            tvStudents.setItems(students);
        } catch (SQLException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error en la Base de datos", "No hay conexión con la base de datos.");
        }
    }


    @FXML
    public void btnCancel(ActionEvent actionEvent) {
        try {
            if (Utils.showConfirmationAlert("Salir de la evaluacion", "¿Estás seguro que quieres cancelar?")) {
                Stage stageStudentsList = (Stage) tvStudents.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/evaluator/FXMLEvaluatorMainScreen.fxml"));
                Parent viewLogIn = loader.load();
                Scene mainScene = new Scene(viewLogIn);
                stageStudentsList.setScene(mainScene);
                stageStudentsList.setTitle("Pagina Principal");
                stageStudentsList.show();
            }
        } catch (IOException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos por el momento no se pudo mostrar la ventana");
        }
    }

    @FXML
    public void btnSelectStudent(ActionEvent actionEvent) {
        Student selectedStudent = tvStudents.getSelectionModel().getSelectedItem();

        if (selectedStudent == null) {
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Selección requerida",
                    "Debes seleccionar un estudiante de la lista para continuar.");
            return;
        }

        try {
            Stage EvaluationRubric = (Stage) tvStudents.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/evaluator/FXMLEvaluationRubric.fxml"));
            Parent view = loader.load();

            FXMLEvaluationRubricController controller = loader.getController();
            controller.initializeData(selectedStudent);

            Scene mainScene = new Scene(view);
            EvaluationRubric.setScene(mainScene);
            EvaluationRubric.setTitle("Rúbrica de Evaluación");
            EvaluationRubric.show();
        } catch (IOException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos, por el momento no se pudo mostrar la ventana.");
            ex.printStackTrace();
        }
    }
}