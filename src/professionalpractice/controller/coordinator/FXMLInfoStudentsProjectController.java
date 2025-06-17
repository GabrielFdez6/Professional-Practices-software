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
import professionalpractice.model.dao.ProjectDAO;
import professionalpractice.model.dao.StudentDAO;
import professionalpractice.model.dao.interfaces.IProjectDAO;
import professionalpractice.model.dao.interfaces.IStudentDAO;
import professionalpractice.model.pojo.Project;
import professionalpractice.model.pojo.Student;
import professionalpractice.utils.Constants;
import professionalpractice.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FXMLInfoStudentsProjectController implements Initializable {

    @FXML
    private TableView<Student> tvStudents;
    @FXML
    private TableColumn<Student, String> colMatriculation;
    @FXML
    private TableColumn<Student, String> colNameStudent;

    @FXML
    private TableView<Project> tvProjects;
    @FXML
    private TableColumn<Project, String> colNameProject;
    @FXML
    private TableColumn<Project, Integer> colAvailability;

    private IStudentDAO studentDAO;
    private IProjectDAO projectDAO;

    private ObservableList<Student> unassignedStudents;
    private ObservableList<Project> availableProjects;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        studentDAO = new StudentDAO();
        projectDAO = new ProjectDAO();
        unassignedStudents = FXCollections.observableArrayList();
        availableProjects = FXCollections.observableArrayList();
        configureTables();
        loadData();
    }

    private void configureTables() {
        colMatriculation.setCellValueFactory(new PropertyValueFactory<>("enrollment"));
        colNameStudent.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        tvStudents.setItems(unassignedStudents);

        colNameProject.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAvailability.setCellValueFactory(new PropertyValueFactory<>("availability"));
        tvProjects.setItems(availableProjects);
    }

    private void    loadData() {
        try {
            unassignedStudents.clear();
            availableProjects.clear();

            ArrayList<Student> studentsFromDB = ((StudentDAO) studentDAO).getUnassignedStudents();
            unassignedStudents.setAll(studentsFromDB);

            ArrayList<Project> projectsFromDB = ((ProjectDAO) projectDAO).getAvailableProjects();
            availableProjects.setAll(projectsFromDB);

        } catch (SQLException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión", "No se pudo cargar la información de la base de datos.");
            e.printStackTrace();
        }
    }


    @FXML
    public void btnCancel(ActionEvent actionEvent) {
        try {
            if (Utils.showConfirmationAlert("Salir de la proyectos", "¿Estás seguro que quieres cancelar?")) {
                Stage stageStudentsList = (Stage) tvStudents.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/coordinator/FXMLCoordinatorMainScreen.fxml"));
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
    public void btnAssign(ActionEvent actionEvent) {
        Student selectedStudent = tvStudents.getSelectionModel().getSelectedItem();
        Project selectedProject = tvProjects.getSelectionModel().getSelectedItem();

        if (selectedStudent == null || selectedProject == null) {
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Selección Requerida", "Debe seleccionar un estudiante y un proyecto para realizar la asignación.");
            return;
        }

        String confirmationMessage = "¿Está seguro de que desea asignar al estudiante '" + selectedStudent.getFullName() + "' al proyecto '" + selectedProject.getName() + "'?";

        if (Utils.showConfirmationAlert("Confirmar Asignación", confirmationMessage)) {
            try {

                int responseCode = projectDAO.assignProjectToStudent(
                        selectedProject.getIdProject(),
                        selectedStudent.getIdStudent(),
                        selectedProject.getName()
                );
                if (responseCode == Constants.OPERATION_SUCCESFUL) {
                    Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Asignación Exitosa", "El estudiante ha sido asignado al proyecto correctamente.");
                    loadData();
                } else {
                    Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error en la Asignación", "No se pudo completar la asignación del proyecto.");
                }
            } catch (SQLException e) {
                Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Base de Datos", "Ocurrió un error al intentar asignar el proyecto. Por favor, inténtelo de nuevo.");
                e.printStackTrace();
            }
        }
    }
}