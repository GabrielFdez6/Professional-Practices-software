package professionalpractice.controller.student;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import professionalpractice.model.dao.interfaces.IStudentDAO;
import professionalpractice.model.dao.StudentDAO;
import professionalpractice.model.pojo.Student;
import professionalpractice.model.pojo.StudentProgress;
import professionalpractice.model.pojo.DeliveryInfo;
import professionalpractice.utils.Utils;

public class FXMLStudentProgressController implements Initializable {

  @FXML
  private Label lbFullName;
  @FXML
  private Label lbStudentNumber;
  @FXML
  private Label lbSemester;
  @FXML
  private Label lbProjectName;
  @FXML
  private Label lbAccumulatedHours;
  @FXML
  private Label lbHoursToCover;
  @FXML
  private Label lbFinalGrade;
  @FXML
  private ProgressBar pbHoursProgress;
  @FXML
  private Label lbProgressPercentage;
  @FXML
  private TableView<DeliveryInfo> tvDeliveries;
  @FXML
  private TableColumn<DeliveryInfo, String> colDeliveryName;
  @FXML
  private TableColumn<DeliveryInfo, String> colDeliveryType;
  @FXML
  private TableColumn<DeliveryInfo, String> colDeliveryStatus;
  @FXML
  private TableColumn<DeliveryInfo, String> colDeliveryDate;
  @FXML
  private TableColumn<DeliveryInfo, String> colDeliveryGrade;
  @FXML
  private TableColumn<DeliveryInfo, String> colDeliveryDueDate;

  private IStudentDAO studentDAO;
  private final int TOTAL_HOURS = 400;
  private ObservableList<DeliveryInfo> deliveryList;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    studentDAO = new StudentDAO();
    deliveryList = FXCollections.observableArrayList();
    configureDeliveryTable();
  }

  private void configureDeliveryTable() {
    colDeliveryName.setCellValueFactory(new PropertyValueFactory<>("deliveryName"));
    colDeliveryType.setCellValueFactory(
        cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDeliveryTypeInSpanish()));
    colDeliveryStatus.setCellValueFactory(
        cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatusText()));
    colDeliveryDate.setCellValueFactory(
        cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFormattedDeliveryDate()));
    colDeliveryGrade.setCellValueFactory(
        cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFormattedGrade()));
    colDeliveryDueDate.setCellValueFactory(
        cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFormattedEndDate()));

    tvDeliveries.setItems(deliveryList);
  }

  public void configureView(Student student) {
    if (student == null) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Sesión",
          "No se ha iniciado sesión correctamente. Por favor, inicia sesión de nuevo.");
      return;
    }
    try {
      StudentProgress progress = studentDAO.getStudentProgress(student.getIdStudent());

      if (progress != null) {
        populateData(progress);
        loadStudentDeliveries(student.getIdStudent());
      } else {
        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Consulta",
            "No se pudo recuperar tu información de progreso. Intenta más tarde.");
      }
    } catch (SQLException e) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Base de Datos",
          "Ocurrió un error al consultar tu progreso. Por favor, intenta más tarde.");
      System.err.println("Error al obtener el progreso del estudiante: " + e.getMessage());
      e.printStackTrace();

    } catch (Exception e) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error Inesperado",
          "Ocurrió un error inesperado. Por favor, intenta más tarde.");
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

    updateHoursProgress(accumulated, TOTAL_HOURS);
  }

  @FXML
  private void btnClickExit(ActionEvent event) {
    Node source = (Node) event.getSource();
    Stage currentStage = (Stage) source.getScene().getWindow();
    currentStage.close();
  }

  private void updateHoursProgress(int currentHours, int totalHours) {
    double progress = (double) currentHours / totalHours;
    pbHoursProgress.setProgress(progress);
    lbProgressPercentage.setText(String.format("%.0f%%", progress * 100));
  }

  private void loadStudentDeliveries(int studentId) {
    try {
      ArrayList<DeliveryInfo> deliveries = studentDAO.getStudentDeliveries(studentId);
      deliveryList.clear();
      deliveryList.addAll(deliveries);
    } catch (SQLException e) {
      System.err.println("Error al cargar las entregas del estudiante: " + e.getMessage());
      Utils.showSimpleAlert(Alert.AlertType.WARNING, "Advertencia",
          "No se pudieron cargar las entregas. Algunas funciones pueden no estar disponibles.");
    }
  }
}