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
import professionalpractice.model.pojo.EvaluationCriterion;
import professionalpractice.model.pojo.Student;
import professionalpractice.utils.Constants;
import professionalpractice.utils.EntityValidationUtils;
import professionalpractice.utils.SecurityValidationUtils;
import professionalpractice.utils.Utils;
import professionalpractice.utils.ValidationUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import professionalpractice.model.dao.PresentationEvaluationDAO;
import professionalpractice.model.dao.StudentDAO;
import professionalpractice.model.pojo.PresentationEvaluation;
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

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    configureTable();
    loadTableInformation();
    setupValidationsAndListeners();
    calculateAndSetAverage();
  }

  private void setupValidationsAndListeners() {
    // Configurar validaciones numéricas para campos de calificación
    TextField[] scoreFields = {
        tfSecurityMasteryScore, tfRequirementsScore, tfSpellingGrammarScore,
        tfContentScore, tfISMethodsTechniquesScore
    };

    for (TextField field : scoreFields) {
      addAdvancedNumericValidation(field);
      field.textProperty().addListener((obs, oldV, newV) -> calculateAndSetAverage());
    }

    // Configurar validación para el título de evaluación
    setupEvaluationTitleValidation();

    // Configurar validación para observaciones
    setupObservationsValidation();
  }

  private void addAdvancedNumericValidation(TextField textField) {
    UnaryOperator<TextFormatter.Change> filter = change -> {
      String newText = change.getControlNewText();

      // Permitir texto vacío temporalmente
      if (newText.isEmpty()) {
        return change;
      }

      // Validar que solo contenga números válidos para calificaciones (5-10)
      if (newText.matches("^([5-9]|10)$")) {
        return change;
      }

      // Permitir entrada gradual (como "1" antes de completar "10")
      if (newText.matches("^[0-9]$") && newText.length() == 1) {
        return change;
      }

      return null;
    };

    TextFormatter<String> textFormatter = new TextFormatter<>(filter);
    textField.setTextFormatter(textFormatter);

    // Validación cuando el campo pierde el foco
    textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) { // Cuando pierde el foco
        validateScoreField(textField);
      }
    });
  }

  private void validateScoreField(TextField scoreField) {
    String text = scoreField.getText().trim();

    if (text.isEmpty()) {
      return; // Permitir campos vacíos durante la edición
    }

    try {
      int score = Integer.parseInt(text);
      String validationError = ValidationUtils.validateEvaluationScore(score);

      if (!validationError.isEmpty()) {
        scoreField.setText("");
        Utils.showSimpleAlert(Alert.AlertType.WARNING, "Calificación Inválida", validationError);
        scoreField.requestFocus();
      }
    } catch (NumberFormatException e) {
      scoreField.setText("");
      Utils.showSimpleAlert(Alert.AlertType.WARNING, "Formato Inválido",
          "La calificación debe ser un número entero entre 5 y 10.");
      scoreField.requestFocus();
    }
  }

  private void setupEvaluationTitleValidation() {
    tfEvaluationTitle.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        String title = tfEvaluationTitle.getText();
        if (title != null && !title.trim().isEmpty()) {
          String titleError = ValidationUtils.validateEvaluationTitle(title);
          if (!titleError.isEmpty()) {
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Título Inválido", titleError);
            tfEvaluationTitle.requestFocus();
          }
        }
      }
    });
  }

  private void setupObservationsValidation() {
    taObservationsAndComments.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        String observations = taObservationsAndComments.getText();
        if (observations != null && !observations.trim().isEmpty()) {
          String observationsError = ValidationUtils.validateObservations(observations);
          if (!observationsError.isEmpty()) {
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Observaciones Inválidas", observationsError);
            taObservationsAndComments.requestFocus();
          }
        }
      }
    });
  }

  public void initializeData(Student student) {
    this.studentToEvaluate = student;
    if (studentToEvaluate != null) {
      lbStudentName.setText("Estudiante: " + studentToEvaluate.getFullName());

      // Generar título por defecto si no existe
      if (tfEvaluationTitle.getText() == null || tfEvaluationTitle.getText().trim().isEmpty()) {
        String defaultTitle = "Evaluación de Presentación - " + student.getFirstName() + " " +
            student.getLastNameFather() + " - " + LocalDate.now().toString();
        tfEvaluationTitle.setText(defaultTitle);
      }
    }
  }

  private void calculateAndSetAverage() {
    TextField[] scoreFields = {
        tfISMethodsTechniquesScore, tfRequirementsScore, tfSecurityMasteryScore,
        tfContentScore, tfSpellingGrammarScore
    };

    double totalScore = 0;
    int validFields = 0;

    for (TextField field : scoreFields) {
      String text = field.getText().trim();
      if (text != null && !text.isEmpty()) {
        try {
          int score = Integer.parseInt(text);
          if (score >= 5 && score <= 10) {
            totalScore += score;
            validFields++;
          }
        } catch (NumberFormatException e) {
          // Ignorar campos con formato inválido
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
    if (Utils.showConfirmationAlert("Cancelar Evaluación",
        "¿Estás seguro que quieres cancelar esta evaluación?\n" +
            "Se perderán todos los datos ingresados.")) {
      goMainMenu();
    }
  }

  @FXML
  public void btnSaveGrade(ActionEvent actionEvent) {
    if (performComprehensiveEvaluationValidation()) {
      try {
        PresentationEvaluation evaluation = createEvaluationFromForm();

        // Validar la entidad completa
        List<String> entityErrors = EntityValidationUtils.validatePresentationEvaluation(evaluation);
        if (EntityValidationUtils.hasValidationErrors(entityErrors)) {
          String formattedErrors = EntityValidationUtils.formatValidationErrors(entityErrors);
          Utils.showSimpleAlert(Alert.AlertType.WARNING, "Errores de Validación", formattedErrors);
          return;
        }

        // Guardar en la base de datos
        PresentationEvaluationDAO evaluationDAO = new PresentationEvaluationDAO();
        int result = evaluationDAO.saveEvaluation(evaluation);

        if (result == Constants.OPERATION_SUCCESFUL) {
          Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Evaluación Guardada",
              Constants.SUCCESS_RECORD_CREATED +
                  "\nLa evaluación ha sido registrada exitosamente.");
          goMainMenu();
        } else {
          Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al Guardar",
              "No se pudo guardar la evaluación. Intente nuevamente.");
        }

      } catch (SQLException e) {
        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Base de Datos",
            Constants.ERROR_DATABASE_CONNECTION);
        System.err.println("Error de base de datos al guardar evaluación: " + e.getMessage());
        e.printStackTrace();
      } catch (Exception e) {
        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error Inesperado",
            "Ocurrió un error inesperado al guardar la evaluación.");
        System.err.println("Error inesperado al guardar evaluación: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  private boolean performComprehensiveEvaluationValidation() {
    List<String> errors = new ArrayList<>();

    // Validar que hay un estudiante seleccionado
    if (studentToEvaluate == null) {
      errors.add("No se ha seleccionado un estudiante para evaluar.");
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Contexto", "No hay estudiante seleccionado.");
      return false;
    }

    // Validar título de evaluación
    validateEvaluationTitle(errors);

    // Validar calificaciones individuales
    validateAllScoreFields(errors);

    // Validar que hay al menos una calificación
    validateMinimumScores(errors);

    // Validar observaciones
    validateObservationsContent(errors);

    // Validaciones de seguridad
    performSecurityValidationsOnEvaluation(errors);

    // Mostrar errores si los hay
    if (!errors.isEmpty()) {
      String allErrors = String.join("\n", errors);
      Utils.showSimpleAlert(Alert.AlertType.WARNING, "Errores de Validación", allErrors);
      return false;
    }

    // Sanitizar campos antes de proceder
    sanitizeEvaluationFields();

    return true;
  }

  private void validateEvaluationTitle(List<String> errors) {
    String title = tfEvaluationTitle.getText();
    if (title == null || title.trim().isEmpty()) {
      errors.add("El título de la evaluación es obligatorio.");
    } else {
      String titleError = ValidationUtils.validateEvaluationTitle(title);
      if (!titleError.isEmpty()) {
        errors.add(titleError);
      }
    }
  }

  private void validateAllScoreFields(List<String> errors) {
    TextField[] scoreFields = {
        tfISMethodsTechniquesScore, tfRequirementsScore, tfSecurityMasteryScore,
        tfContentScore, tfSpellingGrammarScore
    };

    String[] fieldNames = {
        "Métodos y Técnicas IS", "Requisitos", "Seguridad y Dominio",
        "Contenido", "Ortografía y Gramática"
    };

    for (int i = 0; i < scoreFields.length; i++) {
      String text = scoreFields[i].getText();
      if (text != null && !text.trim().isEmpty()) {
        try {
          int score = Integer.parseInt(text.trim());
          String scoreError = ValidationUtils.validateEvaluationScore(score);
          if (!scoreError.isEmpty()) {
            errors.add(fieldNames[i] + ": " + scoreError);
          }
        } catch (NumberFormatException e) {
          errors.add(fieldNames[i] + ": Debe ser un número válido.");
        }
      }
    }
  }

  private void validateMinimumScores(List<String> errors) {
    TextField[] scoreFields = {
        tfISMethodsTechniquesScore, tfRequirementsScore, tfSecurityMasteryScore,
        tfContentScore, tfSpellingGrammarScore
    };

    long validScores = 0;
    for (TextField field : scoreFields) {
      String text = field.getText();
      if (text != null && !text.trim().isEmpty()) {
        try {
          int score = Integer.parseInt(text.trim());
          if (score >= 5 && score <= 10) {
            validScores++;
          }
        } catch (NumberFormatException e) {
          // Ya validado arriba
        }
      }
    }

    if (validScores < 3) {
      errors.add("Se requieren al menos 3 calificaciones válidas para completar la evaluación.");
    }
  }

  private void validateObservationsContent(List<String> errors) {
    String observations = taObservationsAndComments.getText();
    if (observations != null && !observations.trim().isEmpty()) {
      String observationsError = ValidationUtils.validateObservations(observations);
      if (!observationsError.isEmpty()) {
        errors.add(observationsError);
      }
    }
  }

  private void performSecurityValidationsOnEvaluation(List<String> errors) {
    // Validar título
    String title = tfEvaluationTitle.getText();
    if (title != null && !title.isEmpty()) {
      List<String> securityErrors = SecurityValidationUtils.performComprehensiveSecurityValidation(
          title, "Título de evaluación");
      errors.addAll(securityErrors);
    }

    // Validar observaciones
    String observations = taObservationsAndComments.getText();
    if (observations != null && !observations.isEmpty()) {
      List<String> securityErrors = SecurityValidationUtils.performComprehensiveSecurityValidation(
          observations, "Observaciones");
      errors.addAll(securityErrors);
    }

    // Validar campos de calificación contra inyección
    TextField[] scoreFields = {
        tfISMethodsTechniquesScore, tfRequirementsScore, tfSecurityMasteryScore,
        tfContentScore, tfSpellingGrammarScore
    };

    for (TextField field : scoreFields) {
      String text = field.getText();
      if (text != null && !text.isEmpty()) {
        if (SecurityValidationUtils.containsSQLInjection(text)) {
          errors.add("Se detectaron caracteres sospechosos en las calificaciones.");
          break;
        }
      }
    }
  }

  private void sanitizeEvaluationFields() {
    // Sanitizar título
    if (tfEvaluationTitle.getText() != null) {
      tfEvaluationTitle.setText(SecurityValidationUtils.advancedSanitization(tfEvaluationTitle.getText()));
    }

    // Sanitizar observaciones
    if (taObservationsAndComments.getText() != null) {
      taObservationsAndComments
          .setText(SecurityValidationUtils.advancedSanitization(taObservationsAndComments.getText()));
    }

    // Sanitizar campos de calificación
    TextField[] scoreFields = {
        tfISMethodsTechniquesScore, tfRequirementsScore, tfSecurityMasteryScore,
        tfContentScore, tfSpellingGrammarScore
    };

    for (TextField field : scoreFields) {
      if (field.getText() != null) {
        field.setText(ValidationUtils.sanitizeInput(field.getText()));
      }
    }
  }

  private PresentationEvaluation createEvaluationFromForm() {
    PresentationEvaluation evaluation = new PresentationEvaluation();

    evaluation.setTitle(tfEvaluationTitle.getText().trim());
    evaluation.setEvaluationDate(Date.valueOf(LocalDate.now()));
    evaluation.setStudentId(studentToEvaluate.getIdStudent());

    // Asignar calificaciones individuales
    setScoreIfValid(evaluation::setMethodsTechniquesScore, tfISMethodsTechniquesScore);
    setScoreIfValid(evaluation::setRequirementsScore, tfRequirementsScore);
    setScoreIfValid(evaluation::setSecurityMasteryScore, tfSecurityMasteryScore);
    setScoreIfValid(evaluation::setContentScore, tfContentScore);
    setScoreIfValid(evaluation::setSpellingGrammarScore, tfSpellingGrammarScore);

    // Calcular y asignar promedio
    String averageText = lbScoreAverage.getText();
    if (averageText != null && !averageText.equals("0.0")) {
      try {
        evaluation.setAverageScore(new BigDecimal(averageText));
      } catch (NumberFormatException e) {
        evaluation.setAverageScore(BigDecimal.ZERO);
      }
    }

    // Asignar observaciones
    String observations = taObservationsAndComments.getText();
    if (observations != null && !observations.trim().isEmpty()) {
      evaluation.setObservations(observations.trim());
    }

    return evaluation;
  }

  private void setScoreIfValid(java.util.function.Consumer<BigDecimal> setter, TextField field) {
    String text = field.getText();
    if (text != null && !text.trim().isEmpty()) {
      try {
        int score = Integer.parseInt(text.trim());
        if (score >= 5 && score <= 10) {
          setter.accept(new BigDecimal(score));
        }
      } catch (NumberFormatException e) {
        // Ignorar calificaciones inválidas
      }
    }
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