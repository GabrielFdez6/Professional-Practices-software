package professionalpractice.controller.evaluator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import professionalpractice.ProfessionalPractices;
import professionalpractice.model.dao.CriteriaDAO;
import professionalpractice.model.dao.EvaluationDetailDAO;
import professionalpractice.model.dao.interfaces.ICriteriaDAO;
import professionalpractice.model.dao.interfaces.IEvaluationDetailDAO;
import professionalpractice.model.pojo.*;
import professionalpractice.utils.Utils;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import professionalpractice.model.dao.PresentationEvaluationDAO;
import professionalpractice.model.dao.StudentDAO;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class FXMLEvaluationRubricController implements Initializable {

  @FXML
  private TableView<EvaluationCriterion> tvEvaluationRubric;
  @FXML
  private TableColumn colCriterion;
  @FXML
  private TableColumn colCompetent;
  @FXML
  private TableColumn colIndependent;
  @FXML
  private TableColumn colAdvancedBasic;
  @FXML
  private TableColumn colThresholdBasic;
  @FXML
  private TableColumn colNotCompetent;
  @FXML
  private Label lbScoreAverage;
  @FXML
  private TextField tfContentScore;
  @FXML
  private TextField tfISMethodsTechniquesScore;
  @FXML
  private Label lbStudentName;
  @FXML
  private TextArea taObservationsAndComments;
  @FXML
  private TextField tfSecurityMasteryScore;
  @FXML
  private TextField tfRequirementsScore;
  @FXML
  private TextField tfSpellingGrammarScore;
  private ObservableList<EvaluationCriterion> criteria;
  @FXML
  private TextField tfEvaluationTitle;
  private Student studentToEvaluate;
  @FXML
  private Label lblCharCounter;
  private ICriteriaDAO criteriaDAO;
  private IEvaluationDetailDAO evaluationDetailDAO;
  private List<Criteria> dbCriteriaList;
  private TextField[] scoreFields;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    criteriaDAO = new CriteriaDAO();
    evaluationDetailDAO = new EvaluationDetailDAO();
    scoreFields = new TextField[] {
        tfISMethodsTechniquesScore,
        tfRequirementsScore,
        tfSecurityMasteryScore,
        tfContentScore,
        tfSpellingGrammarScore
    };

    configureTable();
    loadTableInformation();
    addNumericValidationToScoreFields();
    addScoreCalculationListeners();
    configureCharacterCounter();
    addTitleValidation();
    calculateAndSetAverage();
  }

  private void configureCharacterCounter() {
    final int MAX_CHARS = 150;
    lblCharCounter.setText("0/" + MAX_CHARS);

    taObservationsAndComments.textProperty().addListener((obs, oldValue, newValue) -> {
      lblCharCounter.setText(newValue.length() + "/" + MAX_CHARS);
    });

    taObservationsAndComments.setTextFormatter(
        new TextFormatter<String>(change -> change.getControlNewText().length() <= MAX_CHARS ? change : null));
  }

  /**
   * Añade validación visual al campo de título de evaluación
   */
  private void addTitleValidation() {
    tfEvaluationTitle.textProperty().addListener((obs, oldValue, newValue) -> {
      if (newValue == null || newValue.trim().isEmpty()) {
        tfEvaluationTitle.setStyle("-fx-border-color: #ffcccc; -fx-border-width: 2px;");
      } else if (newValue.trim().length() < 3) {
        tfEvaluationTitle.setStyle("-fx-border-color: #ffaa00; -fx-border-width: 2px;");
      } else {
        tfEvaluationTitle.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px;");
      }
    });

    // Validación cuando pierde el foco
    tfEvaluationTitle.focusedProperty().addListener((obs, oldValue, newValue) -> {
      if (!newValue) { // Cuando pierde el foco
        String text = tfEvaluationTitle.getText();
        if (text != null && !text.trim().isEmpty() && text.trim().length() < 3) {
          Utils.showSimpleAlert(Alert.AlertType.WARNING, "Título muy corto",
              "El título debe tener al menos 3 caracteres.");
        }
      }
    });
  }

  private void addScoreCalculationListeners() {
    tfSecurityMasteryScore.textProperty().addListener((obs, oldV, newV) -> calculateAndSetAverage());
    tfRequirementsScore.textProperty().addListener((obs, oldV, newV) -> calculateAndSetAverage());
    tfSpellingGrammarScore.textProperty().addListener((obs, oldV, newV) -> calculateAndSetAverage());
    tfContentScore.textProperty().addListener((obs, oldV, newV) -> calculateAndSetAverage());
    tfISMethodsTechniquesScore.textProperty().addListener((obs, oldV, newV) -> calculateAndSetAverage());
  }

  private void addNumericValidationToScoreFields() {
    addNumericValidation(tfSecurityMasteryScore);
    addNumericValidation(tfRequirementsScore);
    addNumericValidation(tfSpellingGrammarScore);
    addNumericValidation(tfContentScore);
    addNumericValidation(tfISMethodsTechniquesScore);
  }

  public void initializeData(Student student) {
    this.studentToEvaluate = student;
    if (studentToEvaluate != null) {
      lbStudentName.setText("Estudiante: " + studentToEvaluate.getFullName());
    }
  }

  /**
   * Valida que todos los campos obligatorios estén completos antes de guardar la
   * evaluación
   * 
   * @return true si todos los campos son válidos, false en caso contrario
   */
  private boolean validateRequiredFields() {
    StringBuilder errorMessage = new StringBuilder();
    boolean isValid = true;

    // Validar que hay un estudiante seleccionado
    if (studentToEvaluate == null) {
      errorMessage.append("• No hay estudiante seleccionado para evaluar.\n");
      isValid = false;
    }

    // Validar título de evaluación
    if (tfEvaluationTitle.getText() == null || tfEvaluationTitle.getText().trim().isEmpty()) {
      errorMessage.append("• El título de la evaluación es obligatorio.\n");
      isValid = false;
    } else if (tfEvaluationTitle.getText().trim().length() < 3) {
      errorMessage.append("• El título de la evaluación debe tener al menos 3 caracteres.\n");
      isValid = false;
    }

    // Validar campos de calificación
    String[] fieldNames = {
        "Uso de Métodos y Técnicas de IS",
        "Requisitos",
        "Seguridad y Dominio",
        "Contenido",
        "Ortografía y Redacción"
    };

    for (int i = 0; i < scoreFields.length; i++) {
      TextField field = scoreFields[i];
      String fieldValue = field.getText();

      if (fieldValue == null || fieldValue.trim().isEmpty()) {
        errorMessage.append("• El campo '").append(fieldNames[i]).append("' es obligatorio.\n");
        isValid = false;
      } else {
        try {
          int score = Integer.parseInt(fieldValue.trim());
          if (score < 5 || score > 10) {
            errorMessage.append("• El campo '").append(fieldNames[i])
                .append("' debe tener una calificación entre 5 y 10.\n");
            isValid = false;
          }
        } catch (NumberFormatException e) {
          errorMessage.append("• El campo '").append(fieldNames[i])
              .append("' debe contener un número válido.\n");
          isValid = false;
        }
      }
    }

    // Validar que el promedio se haya calculado
    if (lbScoreAverage.getText().equals("0.0")) {
      errorMessage.append("• No se ha calculado el promedio de calificaciones.\n");
      isValid = false;
    }

    // Validar límite de caracteres en observaciones
    if (taObservationsAndComments.getText() != null &&
        taObservationsAndComments.getText().length() > 150) {
      errorMessage.append("• Las observaciones no pueden exceder 150 caracteres.\n");
      isValid = false;
    }

    // Mostrar errores si los hay
    if (!isValid) {
      Utils.showSimpleAlert(Alert.AlertType.WARNING, "Campos incompletos o inválidos",
          "Por favor corrija los siguientes errores:\n\n" + errorMessage.toString());
    }

    return isValid;
  }

  /**
   * Calcula y establece el promedio de calificaciones
   * Solo considera campos con valores válidos (entre 5-10)
   */
  private void calculateAndSetAverage() {
    double totalScore = 0;
    int validFields = 0;

    for (TextField field : scoreFields) {
      String text = field.getText();
      if (text != null && !text.trim().isEmpty()) {
        try {
          double score = Double.parseDouble(text.trim());
          // Solo considerar calificaciones válidas (5-10)
          if (score >= 5 && score <= 10) {
            totalScore += score;
            validFields++;
          }
        } catch (NumberFormatException e) {
          // Ignorar valores no numéricos
        }
      }
    }

    if (validFields > 0) {
      double average = totalScore / validFields;
      lbScoreAverage.setText(String.format("%.1f", average));
    } else {
      lbScoreAverage.setText("0.0");
    }
  }

  /**
   * Añade validación numérica a un campo de calificación
   * 
   * @param textField el campo de texto a validar
   */
  private void addNumericValidation(TextField textField) {
    UnaryOperator<TextFormatter.Change> filter = change -> {
      String newText = change.getControlNewText();
      if (newText.matches("($|[5-9]|1|10)")) {
        return change;
      }
      return null;
    };
    TextFormatter<String> textFormatter = new TextFormatter<>(filter);
    textField.setTextFormatter(textFormatter);

    // Validación cuando el campo pierde el foco
    textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) { // Cuando pierde el foco
        String text = textField.getText();
        if (text != null && !text.trim().isEmpty()) {
          try {
            int score = Integer.parseInt(text.trim());
            if (score < 5) {
              textField.setText("");
              Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de puntuación",
                  "La puntuación mínima es 5. El valor ingresado ha sido eliminado.");
            } else if (score > 10) {
              textField.setText("");
              Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de puntuación",
                  "La puntuación máxima es 10. El valor ingresado ha sido eliminado.");
            }
          } catch (NumberFormatException e) {
            textField.setText("");
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de formato",
                "Solo se permiten números enteros entre 5 y 10.");
          }
        }
      }
    });

    // Añadir estilo visual para campos vacíos
    textField.textProperty().addListener((obs, oldValue, newValue) -> {
      if (newValue == null || newValue.trim().isEmpty()) {
        textField.setStyle("-fx-border-color: #ffcccc; -fx-border-width: 1px;");
      } else {
        textField.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px;");
      }
    });
  }

  private void configureTable() {
    colCriterion.setCellValueFactory(new PropertyValueFactory<>("criterion"));
    colCompetent.setCellValueFactory(new PropertyValueFactory<>("competent"));
    colIndependent.setCellValueFactory(new PropertyValueFactory<>("independent"));
    colAdvancedBasic.setCellValueFactory(new PropertyValueFactory<>("advancedBasic"));
    colThresholdBasic.setCellValueFactory(new PropertyValueFactory<>("thresholdBasic"));
    colNotCompetent.setCellValueFactory(new PropertyValueFactory<>("notCompetent"));
  }

  private void loadTableInformation() {
    criteria = FXCollections.observableArrayList();
    ArrayList<EvaluationCriterion> sourceCriteria = getCriteriaFromDataSource();
    criteria.addAll(sourceCriteria);
    tvEvaluationRubric.setItems(criteria);

    try {
      dbCriteriaList = criteriaDAO.getAllCriteria();
    } catch (SQLException e) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión",
          "No se pudieron cargar los criterios de evaluación.");
      e.printStackTrace();
    }
  }

  private ArrayList<EvaluationCriterion> getCriteriaFromDataSource() {
    ArrayList<EvaluationCriterion> criteriaList = new ArrayList<>();

    criteriaList.add(new EvaluationCriterion(
        "USO DE MÉTODOS Y TÉCNICAS DE LA IS",
        "Los métodos y técnicas de la IS optimizan el aseguramiento de calidad y se han aplicado de manera correcta.",
        "Los métodos y técnicas de la IS, son adecuados y se han aplicado de manera correcta.",
        "Los métodos y técnicas de la IS, son adecuados, aunque se presentan algunas deficiencias en su aplicación.",
        "Los métodos y técnicas de la IS, no son adecuados, pero se han aplicado de manera correcta.",
        "No se han aplicado métodos y técnicas de la IS."));

    criteriaList.add(new EvaluationCriterion(
        "REQUISITOS",
        "Cumplió con todos los requisitos. Excedió las expectativas.",
        "Todos los requisitos fueron cumplidos.",
        "No cumple satisfactoriamente con un requisito.",
        "Más de un requisito no fue cumplido satisfactoriamente.",
        "Más de dos requisitos no fueron cumplidos satisfactoriamente."));

    criteriaList.add(new EvaluationCriterion(
        "SEGURIDAD Y DOMINIO",
        "El dominio del tema es excelente, la exposición es dada con seguridad.",
        "Se posee un dominio adecuado y la exposición fue fluida.",
        "Aunque con algunos fallos en el dominio, la exposición fue fluida.",
        "Se demuestra falta de dominio y una exposición deficiente.",
        "No existe dominio sobre el tema y la exposición es deficiente."));

    criteriaList.add(new EvaluationCriterion(
        "CONTENIDO",
        "Cubre los temas a profundidad con detalles y ejemplos. El conocimiento del tema es excelente.",
        "Incluye conocimiento básico sobre el tema. El contenido parece ser bueno.",
        "Incluye información esencial sobre el tema, pero tiene 1-2 errores en los hechos.",
        "El contenido es mínimo y tiene tres errores en los hechos.",
        "El contenido es mínimo y tiene varios errores en los hechos."));

    criteriaList.add(new EvaluationCriterion(
        "ORTOGRAFÍA Y REDACCIÓN",
        "No hay errores de gramática, ortografía o puntuación.",
        "Casi no hay errores de gramática, ortografía o puntuación.",
        "Algunos errores de gramática, ortografía o puntuación.",
        "Varios errores de gramática, ortografía o puntuación.",
        "Demasiados errores de gramática, ortografía o puntuación."));
    return criteriaList;
  }

  @FXML
  public void btnCancel(ActionEvent actionEvent) {
    try {
      if (Utils.showConfirmationAlert("Salir de la evaluacion", "¿Estás seguro que quieres cancelar?")) {
        Stage stageStudentsList = (Stage) lbStudentName.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(
            ProfessionalPractices.class.getResource("view/evaluator/FXMLEvaluatorMainScreen.fxml"));
        Parent viewLogIn = loader.load();
        Scene mainScene = new Scene(viewLogIn);
        stageStudentsList.setScene(mainScene);
        stageStudentsList.setTitle("Pagina Principal");
        stageStudentsList.show();
      }
    } catch (IOException ex) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar",
          "Lo sentimos por el momento no se pudo mostrar la ventana");
    }
  }

  @FXML
  public void btnSaveGrade(ActionEvent actionEvent) {
    // Validar campos obligatorios
    if (!validateRequiredFields()) {
      return;
    }

    try {
      int idRecord = StudentDAO.getRecordIdByStudentId(studentToEvaluate.getIdStudent());
      if (idRecord == -1) {
        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de datos",
            "No se pudo encontrar el expediente del estudiante.");
        return;
      }

      PresentationEvaluation evaluation = new PresentationEvaluation();
      evaluation.setTitle(tfEvaluationTitle.getText());
      evaluation.setObservations(taObservationsAndComments.getText());
      evaluation.setGrade(new BigDecimal(lbScoreAverage.getText()));
      evaluation.setDate(Date.valueOf(LocalDate.now()));
      evaluation.setIdRecord(idRecord);

      PresentationEvaluationDAO dao = new PresentationEvaluationDAO();
      int newEvaluationId = dao.saveEvaluation(evaluation);

      if (newEvaluationId != -1) {
        saveCriteriaGrades(newEvaluationId);

        Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Evaluación guardada",
            "La evaluación se ha guardado exitosamente.");

        goMainMenu();
      } else {
        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al guardar", "Ocurrió un error al guardar la evaluación.");
      }
    } catch (SQLException e) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de base de datos",
          "No se pudo conectar con la base de datos para guardar la información.");
      e.printStackTrace();
    } catch (NumberFormatException e) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error en calificación", "El promedio no es un número válido.");
      e.printStackTrace();
    }
  }

  private void saveCriteriaGrades(int evaluationId) throws SQLException {
    List<EvaluationDetail> details = new ArrayList<>();
    if (dbCriteriaList == null || dbCriteriaList.size() != scoreFields.length) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Configuración",
          "La cantidad de criterios y campos de calificación no coincide.");
      return;
    }

    for (int i = 0; i < dbCriteriaList.size(); i++) {
      EvaluationDetail detail = new EvaluationDetail();
      detail.setIdEvaluation(evaluationId);
      detail.setIdCriteria(dbCriteriaList.get(i).getIdCriteria());
      String gradeText = scoreFields[i].getText().trim();
      detail.setGrade(Float.parseFloat(gradeText.isEmpty() ? "0" : gradeText));
      details.add(detail);
    }

    evaluationDetailDAO.saveEvaluationDetails(details);
  }

  private void goMainMenu() {
    try {
      Stage stage = (Stage) tfContentScore.getScene().getWindow();
      Parent view = FXMLLoader
          .load(ProfessionalPractices.class.getResource("view/evaluator/FXMLEvaluatorMainScreen.fxml"));
      Scene scene = new Scene(view);
      stage.setScene(scene);
      stage.setTitle("Página Principal Evaluador");
      stage.show();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}