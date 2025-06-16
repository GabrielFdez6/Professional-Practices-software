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
import professionalpractice.model.dao.StudentDAO;
import professionalpractice.model.pojo.StudentProject;
import professionalpractice.utils.Constants;
import professionalpractice.utils.Utils;

public class FXMLAcademicProgressStudentsController implements Initializable {

    @FXML
    private TableView<StudentProject> tvStudents;
    @FXML
    private TableColumn<StudentProject, String> tcEnrollment;
    @FXML
    private TableColumn<StudentProject, String> tcName;
    @FXML
    private TableColumn<StudentProject, String> tcSemester;
    @FXML
    private TableColumn<StudentProject, String> tcProject;
    @FXML
    private Button btnConsultAdvance;
    @FXML
    private Button btnExit;

    private ObservableList<StudentProject> students;
    private StudentDAO studentDAO = new StudentDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        students = FXCollections.observableArrayList();
        configureTableColumns();
        loadStudentsData();
    }

    private void configureTableColumns() {
        tcEnrollment.setCellValueFactory(new PropertyValueFactory<>("enrollment"));
        tcName.setCellValueFactory(new PropertyValueFactory<>("studentFullName"));
        tcSemester.setCellValueFactory(new PropertyValueFactory<>("semester"));
        tcProject.setCellValueFactory(new PropertyValueFactory<>("projectName"));
    }

    private void loadStudentsData() {
        // IDs de ejemplo. Debes obtenerlos del usuario logueado (profesor) y del periodo actual.
        int idAcademic = 2; // ID del profesor Juan Pérez Ramírez del DUMP
        int idTerm = 1;     // ID del periodo Febrero 2025 - Julio 2025 del DUMP

        HashMap<String, Object> response = studentDAO.getStudentsWithProjectByProfessor(idAcademic, idTerm);

        if ((int) response.get("responseCode") == Constants.CONNECTION_FAILED) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión", "No hay conexión con la base de datos.");
        } else {
            ArrayList<StudentProject> studentList = (ArrayList<StudentProject>) response.get("students");
            if (studentList.isEmpty()) {
                Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Información", "No hay estudiantes con proyectos asignados para consultar.");
            } else {
                students.addAll(studentList);
                tvStudents.setItems(students);
            }
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/practicasprofesionales/view/AcademicProgressDetail.fxml")); // <- Debes crear este FXML
            Parent root = loader.load();

            // Suponiendo que tienes un AcademicProgressDetailController con un método initData
            // AcademicProgressDetailController controller = loader.getController();
            // controller.initData(selectedStudent.getIdStudent());

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Avance Académico por Estudiante");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Opcional: Recargar datos por si algo cambió
            // loadStudentsData();

        } catch (IOException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR,"Error de UI", "No se pudo cargar la ventana de detalle.");
            e.printStackTrace();
        }
    }

    @FXML
    private void btnExitClick(ActionEvent event) {
        boolean confirm = Utils.showConfirmationAlert("Confirmar salida", "¿Estás seguro que quieres salir?");
        if (confirm) {
            Stage stage = (Stage) tvStudents.getScene().getWindow();
            stage.close();
        }
    }
}
