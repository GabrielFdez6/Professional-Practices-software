// Ubicación: src/professionalpractice/controller/student/FXMLStudentProgressController.java
package professionalpractice.controller.student;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import professionalpractice.model.dao.interfaces.IStudentDAO;
import professionalpractice.model.dao.StudentDAO;
import professionalpractice.model.pojo.Student;
import professionalpractice.model.pojo.StudentProgress;
import professionalpractice.utils.Utils;

public class FXMLStudentProgressController implements Initializable {

    @FXML private Label lbFullName;
    @FXML private Label lbStudentNumber;
    @FXML private Label lbSemester;
    @FXML private Label lbProjectName;
    @FXML private Label lbAccumulatedHours;
    @FXML private Label lbHoursToCover;
    @FXML private Label lbFinalGrade;

    private IStudentDAO studentDAO;
    private final int TOTAL_HOURS = 300;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        studentDAO = new StudentDAO();
    }

    public void configureView(Student student) {
        if (student == null) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Sesión", "No se ha iniciado sesión correctamente. Por favor, inicia sesión de nuevo.");
            return;
        }
        try{
            StudentProgress progress = studentDAO.getStudentProgress(student.getIdStudent());

            if (progress != null) {
                populateData(progress);
            } else {
                Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Consulta", "No se pudo recuperar tu información de progreso. Intenta más tarde.");
            }
        } catch (SQLException e){
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Base de Datos", "Ocurrió un error al consultar tu progreso. Por favor, intenta más tarde.");
            System.err.println("Error al obtener el progreso del estudiante: " + e.getMessage());
            e.printStackTrace();

        } catch (Exception e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error Inesperado", "Ocurrió un error inesperado. Por favor, intenta más tarde.");
            System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();

        }

    }

    private void populateData(StudentProgress progress) {
        Student studentInfo = progress.getStudent();

        lbFullName.setText(studentInfo.getFullName());
        lbStudentNumber.setText(studentInfo.getEnrollment());
        lbSemester.setText(studentInfo.getSemester());
        lbFinalGrade.setText(String.format("%.2f", studentInfo.getGrade()));
        lbProjectName.setText(progress.getProjectName());

        int accumulated = progress.getAccumulatedHours();
        int toCover = TOTAL_HOURS - accumulated;

        lbAccumulatedHours.setText(String.format("%d/%d", accumulated, TOTAL_HOURS));
        lbHoursToCover.setText(String.valueOf(toCover));
    }

    @FXML
    private void btnClickExit(ActionEvent event) {
        // Obtenemos el Stage (la ventana) desde el origen del evento (el botón que se presionó)
        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
    }
}