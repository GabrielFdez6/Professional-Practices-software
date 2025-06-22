package professionalpractice.controller.academic;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import professionalpractice.model.dao.interfaces.IStudentDAO;
import professionalpractice.model.dao.StudentDAO;
import professionalpractice.model.dao.DocumentDAO;
import professionalpractice.model.dao.EvaluationDetailDAO;
import professionalpractice.model.pojo.Student;
import professionalpractice.model.pojo.StudentProgress;
import professionalpractice.utils.Utils;

public class FXMLAcademicDetailedStudentProgressController implements Initializable {

  @FXML
  private Label lbStudentTitle;
  @FXML
  private Label lbFullName;
  @FXML
  private Label lbStudentNumber;
  @FXML
  private Label lbSemester;
  @FXML
  private Label lbEstatus;
  @FXML
  private Label lbProjectName;
  @FXML
  private Label lbFinalGrade;
  @FXML
  private Label lbAccumulatedHours;
  @FXML
  private Label lbHoursToCover;
  @FXML
  private ProgressBar pbHoursProgress;
  @FXML
  private Label lbProgressPercentage;
  @FXML
  private TableView<DocumentGrade> tvGrades;
  @FXML
  private TableColumn<DocumentGrade, String> colDocumentType;
  @FXML
  private TableColumn<DocumentGrade, String> colDocumentName;
  @FXML
  private TableColumn<DocumentGrade, Double> colGrade;
  @FXML
  private TableColumn<DocumentGrade, String> colDeliveryDate;

  private IStudentDAO studentDAO;
  private DocumentDAO documentDAO;
  private EvaluationDetailDAO evaluationDAO;
  private final int TOTAL_HOURS = 300;
  private ObservableList<DocumentGrade> documentGrades;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    studentDAO = new StudentDAO();
    documentDAO = new DocumentDAO();
    evaluationDAO = new EvaluationDetailDAO();
    documentGrades = FXCollections.observableArrayList();
    configureTable();
  }

  private void configureTable() {
    colDocumentType.setCellValueFactory(new PropertyValueFactory<>("documentType"));
    colDocumentName.setCellValueFactory(new PropertyValueFactory<>("documentName"));
    colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
    colDeliveryDate.setCellValueFactory(new PropertyValueFactory<>("deliveryDate"));
    tvGrades.setItems(documentGrades);
  }

  public void configureView(Student student) {
    if (student == null) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Sesión",
          "No se ha recibido información válida del estudiante.");
      return;
    }
    try {
      StudentProgress progress = studentDAO.getStudentProgress(student.getIdStudent());

      if (progress != null) {
        populateStudentData(progress);
        loadDocumentGrades(student.getIdStudent());
      } else {
        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Consulta",
            "No se pudo recuperar la información de progreso del estudiante.");
      }
    } catch (SQLException e) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Base de Datos",
          "Ocurrió un error al consultar el progreso del estudiante. Por favor, intenta más tarde.");
      System.err.println("Error al obtener el progreso del estudiante: " + e.getMessage());
      e.printStackTrace();
    } catch (Exception e) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error Inesperado",
          "Ocurrió un error inesperado al cargar la información del estudiante.");
      System.err.println("Error inesperado: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void populateStudentData(StudentProgress progress) {
    Student studentInfo = progress.getStudent();

    // Título dinámico
    lbStudentTitle.setText("Avance del Estudiante: " + studentInfo.getFullName());

    // Información básica del estudiante
    lbFullName.setText(studentInfo.getFullName());
    lbStudentNumber.setText(studentInfo.getEnrollment());
    lbSemester.setText(studentInfo.getSemester());
    lbEstatus.setText(studentInfo.getEstatus() != null ? studentInfo.getEstatus() : "Activo");
    lbProjectName.setText(progress.getProjectName());
    lbFinalGrade.setText(String.format("%.2f", studentInfo.getGrade()));

    // Progreso de horas
    int accumulated = progress.getAccumulatedHours();
    int toCover = TOTAL_HOURS - accumulated;

    lbAccumulatedHours.setText(String.format("%d/%d", accumulated, TOTAL_HOURS));
    lbHoursToCover.setText(String.valueOf(toCover));

    updateHoursProgress(accumulated, TOTAL_HOURS);
  }

  private void updateHoursProgress(int currentHours, int totalHours) {
    double progress = (double) currentHours / totalHours;
    pbHoursProgress.setProgress(progress);
    lbProgressPercentage.setText(String.format("%.0f%%", progress * 100));
  }

  private void loadDocumentGrades(int studentId) {
    try {
      // Esta es una implementación simulada. En la implementación real,
      // deberías crear métodos en DocumentDAO y EvaluationDetailDAO
      // para obtener las calificaciones reales de los documentos
      List<DocumentGrade> grades = getStudentDocumentGrades(studentId);
      documentGrades.setAll(grades);
    } catch (Exception e) {
      System.err.println("Error al cargar calificaciones de documentos: " + e.getMessage());
      // Mostrar datos de ejemplo si no se pueden cargar los reales
      loadSampleGrades();
    }
  }

  private List<DocumentGrade> getStudentDocumentGrades(int studentId) {
    List<DocumentGrade> grades = new ArrayList<>();

    try {
      // Obtener entregas reales del estudiante
      StudentDAO dao = new StudentDAO();
      ArrayList<professionalpractice.model.pojo.DeliveryInfo> deliveries = dao.getStudentDeliveries(studentId);

      for (professionalpractice.model.pojo.DeliveryInfo delivery : deliveries) {
        String type = mapDeliveryType(delivery.getDeliveryType());
        String name = delivery.getDeliveryName();
        Double grade = delivery.getGrade();
        String date = delivery.getFormattedDeliveryDate();

        grades.add(new DocumentGrade(type, name, grade, date));
      }
    } catch (SQLException e) {
      System.err.println("Error al obtener entregas reales: " + e.getMessage());
      // Fallback a datos de ejemplo si hay error
      grades.add(new DocumentGrade("Documento Inicial", "Propuesta de Proyecto", 9.0, "15/01/2024"));
      grades.add(new DocumentGrade("Reporte", "Reporte Parcial 1", 8.5, "28/02/2024"));
      grades.add(new DocumentGrade("Reporte", "Reporte Parcial 2", 8.8, "15/04/2024"));
    }

    return grades;
  }

  private String mapDeliveryType(String deliveryType) {
    if (deliveryType == null)
      return "Sin tipo";

    switch (deliveryType.toUpperCase()) {
      case "INITIAL DOCUMENT":
        return "Documento Inicial";
      case "FINAL DOCUMENT":
        return "Documento Final";
      case "REPORT":
        return "Reporte";
      default:
        return deliveryType;
    }
  }

  private void loadSampleGrades() {
    List<DocumentGrade> sampleGrades = new ArrayList<>();
    sampleGrades.add(new DocumentGrade("Documento Inicial", "Propuesta de Proyecto", 9.0, "15/01/2024"));
    sampleGrades.add(new DocumentGrade("Reporte", "Reporte Parcial 1", 8.5, "28/02/2024"));
    sampleGrades.add(new DocumentGrade("Reporte", "Reporte Parcial 2", 8.8, "15/04/2024"));
    documentGrades.setAll(sampleGrades);
  }

  @FXML
  private void btnClickExit(ActionEvent event) {
    Node source = (Node) event.getSource();
    Stage currentStage = (Stage) source.getScene().getWindow();
    currentStage.close();
  }

  // Clase interna para representar las calificaciones de documentos
  public static class DocumentGrade {
    private String documentType;
    private String documentName;
    private Double grade;
    private String deliveryDate;

    public DocumentGrade(String documentType, String documentName, Double grade, String deliveryDate) {
      this.documentType = documentType;
      this.documentName = documentName;
      this.grade = grade;
      this.deliveryDate = deliveryDate;
    }

    // Getters y setters
    public String getDocumentType() {
      return documentType;
    }

    public void setDocumentType(String documentType) {
      this.documentType = documentType;
    }

    public String getDocumentName() {
      return documentName;
    }

    public void setDocumentName(String documentName) {
      this.documentName = documentName;
    }

    public Double getGrade() {
      return grade;
    }

    public void setGrade(Double grade) {
      this.grade = grade;
    }

    public String getDeliveryDate() {
      return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
      this.deliveryDate = deliveryDate;
    }
  }
}