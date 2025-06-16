package professionalpractice.controller.academic;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
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
import professionalpractice.controller.student.FXMLStudentProgressController;
import professionalpractice.model.dao.StudentDAO;
import professionalpractice.model.pojo.StudentProject;
import professionalpractice.utils.Constants;
import professionalpractice.utils.Utils;

public class FXMLAcademicProgressStudentsController implements Initializable {

    @FXML private TableView<StudentProject> tvStudents;
    @FXML private TableColumn<StudentProject, String> tcEnrollment;
    @FXML private TableColumn<StudentProject, String> tcName;
    @FXML private TableColumn<StudentProject, String> tcSemester;
    @FXML private TableColumn<StudentProject, String> tcProject;
    @FXML private Button btnConsultAdvance;
    @FXML private Button btnExit;

    private ObservableList<StudentProject> students;
    private StudentDAO studentDAO;
    private int currentIdAcademic;
    private int currentIdTerm;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        studentDAO = new StudentDAO();
        students = FXCollections.observableArrayList();
        configureTableColumns();
    }

    public void initData(int idAcademic, int idTerm) {
        this.currentIdAcademic = idAcademic;
        this.currentIdTerm = idTerm;
        loadStudentsData();
    }

    private void configureTableColumns() {
        tcEnrollment.setCellValueFactory(new PropertyValueFactory<>("enrollment"));
        tcName.setCellValueFactory(new PropertyValueFactory<>("studentFullName"));
        tcSemester.setCellValueFactory(new PropertyValueFactory<>("semester"));
        tcProject.setCellValueFactory(new PropertyValueFactory<>("projectName"));
    }

    private void loadStudentsData() {
        HashMap<String, Object> response = StudentDAO.getStudentsWithProjectByProfessor(currentIdAcademic, currentIdTerm);

        if ((int) response.get("responseCode") == Constants.CONNECTION_FAILED) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión", "No hay conexión con la base de datos.");
        } else {
            ArrayList<StudentProject> studentList = (ArrayList<StudentProject>) response.get("students");
            if (studentList.isEmpty()) {
                Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Información", "No hay estudiantes con proyectos asignados para consultar en este periodo.");
            }
            students.setAll(studentList);
            tvStudents.setItems(students);
        }
    }

    @FXML
    private void btnConsultAdvanceClick(ActionEvent event) {
        StudentProject selectedStudent = tvStudents.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Selección requerida", "Debes seleccionar un estudiante para consultar su avance.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/professionalpractice/view/student/FXMLStudentProgress.fxml"));
            Parent view = loader.load();

            // Usamos el controlador de la vista de progreso del estudiante que ya existe
            FXMLStudentProgressController controller = loader.getController();
            // Creamos un objeto Student temporal solo con el ID para pasarlo
            professionalpractice.model.pojo.Student studentToConsult = new professionalpractice.model.pojo.Student();
            studentToConsult.setIdStudent(selectedStudent.getIdStudent());

            controller.configureView(studentToConsult);

            Stage stage = new Stage();
            stage.setScene(new Scene(view));
            stage.setTitle("Avance Académico de " + selectedStudent.getStudentFullName());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR,"Error de UI", "No se pudo cargar la ventana de detalle del avance.");
            e.printStackTrace();
        }
    }

    @FXML
    private void btnExitClick(ActionEvent event) {
        // Regresamos a la pantalla principal del académico
        try {
            Stage stage = (Stage) tvStudents.getScene().getWindow();
            // Aquí deberías recargar el FXMLTeacherMainScreen, pasando de nuevo los datos del académico
            // Por simplicidad, aquí solo cerramos, pero lo ideal es navegar de vuelta.
            if (Utils.showConfirmationAlert("Confirmar salida", "¿Estás seguro que quieres salir?")) {
                stage.close(); // Simplificado.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}