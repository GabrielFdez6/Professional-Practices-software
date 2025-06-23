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
    calculateAndSetAverage();
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
    try {
      dbCriteriaList = criteriaDAO.getAllCriteria();

      if (dbCriteriaList == null || dbCriteriaList.isEmpty()) {
        Utils.showSimpleAlert(Alert.AlertType.WARNING, "Sin Criterios", "No se encontraron criterios de evaluación en la base de datos.");
        return;
      }


      for (Criteria dbCriterion : dbCriteriaList) {
        EvaluationCriterion tableCriterion = new EvaluationCriterion(
                dbCriterion.getCriteriaName(),
                dbCriterion.getCompetent(),
                dbCriterion.getIndependent(),
                dbCriterion.getAdvancedBasic(),
                dbCriterion.getThresholdBasic(),
                dbCriterion.getNotCompetent()
        );
        criteria.add(tableCriterion);
      }

      tvEvaluationRubric.setItems(criteria);

    } catch (SQLException e) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión", "No se pudieron cargar los criterios de evaluación desde la base de datos.");
      e.printStackTrace();
    }
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

  private void calculateAndSetAverage() {
    TextField[] scoreFields = {
            tfISMethodsTechniquesScore,
            tfRequirementsScore,
            tfSecurityMasteryScore,
            tfContentScore,
            tfSpellingGrammarScore
    };
    double totalScore = 0;
    int validFields = 0;

    for (TextField field : scoreFields) {
      String text = field.getText();
      if (text != null && !text.isEmpty() && !"1".equals(text)) {
        try {
          totalScore += Double.parseDouble(text);
          validFields++;
        } catch (NumberFormatException e) {
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

  private void addNumericValidation(TextField textField) {
    final String regex = "^($|1(0(\\.(0{1,2})?)?)?|[5-9](\\.\\d{0,2})?|[5-9]\\.)$";
    UnaryOperator<TextFormatter.Change> filter = change -> {
      String newText = change.getControlNewText();
      if (newText.matches(regex)) {
        return change;
      }
      return null;
    };

    TextFormatter<String> textFormatter = new TextFormatter<>(filter);
    textField.setTextFormatter(textFormatter);

    textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        String text = textField.getText();

        if (text.endsWith(".")) {
          textField.setText(text.substring(0, text.length() - 1));
        }

        text = textField.getText();

        if (!text.isEmpty()) {
          try {
            double value = Double.parseDouble(text);
            if (value < 5.0 || value > 10.0) {
              Utils.showSimpleAlert(Alert.AlertType.ERROR, "Valor fuera de rango",
                      "La calificación debe estar entre 5.0 y 10.0.");
              textField.setText("");
            }
          } catch (NumberFormatException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Formato incorrecto",
                    "El valor ingresado no es un número válido.");
            textField.setText("");
          }
        }
      }
    });
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
    if (studentToEvaluate == null|| lbScoreAverage.getText().equals("0.0")
            || lbScoreAverage.getText().isEmpty()
            || tfISMethodsTechniquesScore.getText().isEmpty()
            || tfRequirementsScore.getText().isEmpty()
            || tfSecurityMasteryScore.getText().isEmpty()
            || tfContentScore.getText().isEmpty()
            || tfSpellingGrammarScore.getText().isEmpty()
    ) {
      Utils.showSimpleAlert(Alert.AlertType.WARNING, "Campos incompletos",
              "Debe calificar todos los rubros antes de guardar.");
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