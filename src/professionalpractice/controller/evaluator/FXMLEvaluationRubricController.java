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
import professionalpractice.utils.Utils;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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

        addNumericValidation(tfSecurityMasteryScore);
        addNumericValidation(tfRequirementsScore);
        addNumericValidation(tfSpellingGrammarScore);
        addNumericValidation(tfContentScore);
        addNumericValidation(tfISMethodsTechniquesScore);

        tfSecurityMasteryScore.textProperty().addListener((obs, oldV, newV) -> calculateAndSetAverage());
        tfRequirementsScore.textProperty().addListener((obs, oldV, newV) -> calculateAndSetAverage());
        tfSpellingGrammarScore.textProperty().addListener((obs, oldV, newV) -> calculateAndSetAverage());
        tfContentScore.textProperty().addListener((obs, oldV, newV) -> calculateAndSetAverage());
        tfISMethodsTechniquesScore.textProperty().addListener((obs, oldV, newV) -> calculateAndSetAverage());

        calculateAndSetAverage();
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
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("($|[5-9]|1|10)")) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);

        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if ("1".equals(textField.getText())) {
                    textField.setText("");
                    Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de puntuación", "La puntuación mínima es 5.");
                }
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
    }

    private ArrayList<EvaluationCriterion> getCriteriaFromDataSource() {
        ArrayList<EvaluationCriterion> criteriaList = new ArrayList<>();

        criteriaList.add(new EvaluationCriterion(
                "USO DE MÉTODOS Y TÉCNICAS DE LA IS",
                "Los métodos y técnicas de la IS optimizan el aseguramiento de calidad y se han aplicado de manera correcta.",
                "Los métodos y técnicas de la IS, son adecuados y se han aplicado de manera correcta.",
                "Los métodos y técnicas de la IS, son adecuados, aunque se presentan algunas deficiencias en su aplicación.",
                "Los métodos y técnicas de la IS, no son adecuados, pero se han aplicado de manera correcta.",
                "No se han aplicado métodos y técnicas de la IS."
        ));

        criteriaList.add(new EvaluationCriterion(
                "REQUISITOS",
                "Cumplió con todos los requisitos. Excedió las expectativas.",
                "Todos los requisitos fueron cumplidos.",
                "No cumple satisfactoriamente con un requisito.",
                "Más de un requisito no fue cumplido satisfactoriamente.",
                "Más de dos requisitos no fueron cumplidos satisfactoriamente."
        ));

        criteriaList.add(new EvaluationCriterion(
                "SEGURIDAD Y DOMINIO",
                "El dominio del tema es excelente, la exposición es dada con seguridad.",
                "Se posee un dominio adecuado y la exposición fue fluida.",
                "Aunque con algunos fallos en el dominio, la exposición fue fluida.",
                "Se demuestra falta de dominio y una exposición deficiente.",
                "No existe dominio sobre el tema y la exposición es deficiente."
        ));

        criteriaList.add(new EvaluationCriterion(
                "CONTENIDO",
                "Cubre los temas a profundidad con detalles y ejemplos. El conocimiento del tema es excelente.",
                "Incluye conocimiento básico sobre el tema. El contenido parece ser bueno.",
                "Incluye información esencial sobre el tema, pero tiene 1-2 errores en los hechos.",
                "El contenido es mínimo y tiene tres errores en los hechos.",
                "El contenido es mínimo y tiene varios errores en los hechos."
        ));

        criteriaList.add(new EvaluationCriterion(
                "ORTOGRAFÍA Y REDACCIÓN",
                "No hay errores de gramática, ortografía o puntuación.",
                "Casi no hay errores de gramática, ortografía o puntuación.",
                "Algunos errores de gramática, ortografía o puntuación.",
                "Varios errores de gramática, ortografía o puntuación.",
                "Demasiados errores de gramática, ortografía o puntuación."
        ));
        return criteriaList;
    }

    @FXML
    public void btnCancel(ActionEvent actionEvent) {
        try {
            if (Utils.showConfirmationAlert("Salir de la evaluacion", "¿Estás seguro que quieres cancelar?")) {
                Stage stageStudentsList = (Stage) lbStudentName.getScene().getWindow();
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
    public void btnSaveGrade(ActionEvent actionEvent) {
        if (studentToEvaluate == null || tfEvaluationTitle.getText().isEmpty() || lbScoreAverage.getText().equals("0.0")) {
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Campos incompletos",
                    "Debe asignar un título y calificar todos los rubros antes de guardar.");
            return;
        }

        try {
            int idRecord = StudentDAO.getRecordIdByStudentId(studentToEvaluate.getIdStudent());
            if (idRecord == -1) {
                Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de datos", "No se pudo encontrar el expediente del estudiante.");
                return;
            }

            PresentationEvaluation evaluation = new PresentationEvaluation();
            evaluation.setTitle(tfEvaluationTitle.getText());
            evaluation.setObservations(taObservationsAndComments.getText());
            evaluation.setGrade(new BigDecimal(lbScoreAverage.getText()));
            evaluation.setDate(Date.valueOf(LocalDate.now()));
            evaluation.setIdRecord(idRecord);

            PresentationEvaluationDAO dao = new PresentationEvaluationDAO();
            int response = dao.saveEvaluation(evaluation);

            if (response == 200) {
                Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Evaluación guardada",
                        "La evaluación se ha guardado exitosamente.");

                goMainMenu();
            } else {
                Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al guardar", "Ocurrió un error al guardar la evaluación.");
            }

        } catch (SQLException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de base de datos", "No se pudo conectar con la base de datos para guardar la información.");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error en calificación", "El promedio no es un número válido.");
            e.printStackTrace();
        }
    }

    private void goMainMenu(){
        try {
            Stage stage = (Stage) tfContentScore.getScene().getWindow();
            Parent view = FXMLLoader.load(ProfessionalPractices.class.getResource("view/evaluator/FXMLEvaluatorMainScreen.fxml"));
            Scene scene = new Scene(view);
            stage.setScene(scene);
            stage.setTitle("Página Principal Evaluador");
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}