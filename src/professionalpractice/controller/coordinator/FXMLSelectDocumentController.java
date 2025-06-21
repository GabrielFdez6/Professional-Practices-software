package professionalpractice.controller.coordinator;

import javafx.application.Platform; // Se mantiene la importación, aunque Platform.runLater ya no se use aquí
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import professionalpractice.ProfessionalPractices;
import professionalpractice.model.dao.DocumentDAO;
import professionalpractice.model.dao.interfaces.IDocumentDAO;
import professionalpractice.model.pojo.FinalDocument;
import professionalpractice.model.pojo.InitialDocument;
import professionalpractice.model.pojo.ReportDocument;
import professionalpractice.utils.Constants;
import professionalpractice.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLSelectDocumentController implements Initializable {

    @FXML private Label lblLegend;
    @FXML private TableView<Object> tvDocuments;
    @FXML private TableColumn<Object, String> colDocumentName;
    @FXML private TableColumn<Object, String> colDocumentStatus;

    @FXML private Button btnContinue;
    @FXML private Button btnBack;

    private String selectedDeliveryType;
    private IDocumentDAO documentDAO;
    private ObservableList<Object> documents;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        documentDAO = new DocumentDAO();
        documents = FXCollections.observableArrayList();
        configureTable();
        tvDocuments.setItems(documents);

        tvDocuments.setPlaceholder(new Label("No hay documentos para mostrar en este momento."));
        tvDocuments.setFixedCellSize(30.0);
    }

    public void initData(String deliveryType) {
        this.selectedDeliveryType = deliveryType;
        lblLegend.setText("Selecciona el documento solicitado para esta entrega (" + deliveryType + ")");
        loadDocuments();
    }

    private void configureTable() {
        colDocumentName.setCellValueFactory(cellData -> {
            Object item = cellData.getValue();
            String value = "N/A";
            if (item instanceof InitialDocument) {
                value = ((InitialDocument) item).getName();
            } else if (item instanceof ReportDocument) {
                value = ((ReportDocument) item).getName();
            } else if (item instanceof FinalDocument) {
                value = ((FinalDocument) item).getName();
            }
            System.out.println("setCellValueFactory (Nombre): Item -> " + item + ", Valor -> " + value);
            return new javafx.beans.property.SimpleStringProperty(value);
        });

        colDocumentName.setCellFactory(column -> new TableCell<Object, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle(null);
                } else {
                    setGraphic(null);
                    setText(item);
                    setTextFill(Color.BLACK);
                    setStyle(null);
                    System.out.println("CellFactory (Nombre) dibujando para fila: " + getIndex() + ", Contenido: " + item);
                }
            }
        });


        colDocumentStatus.setCellValueFactory(cellData -> {
            Object item = cellData.getValue();
            String value = "N/A";
            if (item instanceof InitialDocument) {
                value = ((InitialDocument) item).getStatus();
            } else if (item instanceof ReportDocument) {
                value = ((ReportDocument) item).getStatus(); // Error: should be getStatus()
            } else if (item instanceof FinalDocument) {
                value = ((FinalDocument) item).getStatus();
            }
            System.out.println("setCellValueFactory (Estado): Item -> " + item + ", Valor -> " + value);
            return new javafx.beans.property.SimpleStringProperty(value);
        });

        colDocumentStatus.setCellFactory(column -> new TableCell<Object, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle(null);
                } else {
                    setGraphic(null);
                    setText(item);
                    setTextFill(Color.BLACK);
                    setStyle(null);
                    System.out.println("CellFactory (Estado) dibujando para fila: " + getIndex() + ", Contenido: " + item);
                }
            }
        });
    }

    private void loadDocuments() {
        List<?> fetchedDocuments = null;
        SQLException sqlException = null;

        try {
            System.out.println("Intentando cargar documentos de tipo: " + selectedDeliveryType);
            switch (selectedDeliveryType) {
                case "DOCUMENTOS INICIALES":
                    fetchedDocuments = documentDAO.getAllInitialDocuments();
                    break;
                case "REPORTES":
                    fetchedDocuments = documentDAO.getAllReportDocuments();
                    break;
                case "DOCUMENTOS FINALES":
                    fetchedDocuments = documentDAO.getAllFinalDocuments();
                    break;
                default:
                    System.out.println("Tipo de documento no reconocido: " + selectedDeliveryType);
                    break;
            }
        } catch (SQLException e) {
            sqlException = e;
            System.err.println("SQLException al cargar documentos: " + e.getMessage());
        }

        if (sqlException != null) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión", "No se pudieron cargar los documentos desde la base de datos.");
            sqlException.printStackTrace();
        } else if (selectedDeliveryType != null) {
            documents.clear();
            if (fetchedDocuments != null) {
                documents.setAll(fetchedDocuments);
                System.out.println("Documentos cargados en ObservableList (tamaño): " + documents.size());
            } else {
                System.out.println("fetchedDocuments es NULL.");
            }

            tvDocuments.setItems(documents);
            tvDocuments.refresh();

            if (documents.isEmpty()) {
                Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Sin Documentos", "No hay documentos disponibles para el tipo seleccionado.");
            } else {
                System.out.println("Tabla de documentos actualizada. Filas en tabla: " + tvDocuments.getItems().size());
            }
        } else {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Tipo de Documento Inválido", "El tipo de documento seleccionado no es reconocido.");
        }
    }

    @FXML
    private void btnContinueClick(ActionEvent event) {
        Object selectedDocument = tvDocuments.getSelectionModel().getSelectedItem();
        if (selectedDocument == null) {
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Selección Requerida", "Por favor, selecciona un documento para continuar.");
            return;
        }

        String documentName = "";
        if (selectedDocument instanceof InitialDocument) {
            documentName = ((InitialDocument) selectedDocument).getName();
        } else if (selectedDocument instanceof ReportDocument) {
            documentName = ((ReportDocument) selectedDocument).getName();
        } else if (selectedDocument instanceof FinalDocument) {
            documentName = ((FinalDocument) selectedDocument).getName();
        }

        try {
            Stage currentStage = (Stage) btnContinue.getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/coordinator/FXMLScheduleDeliveryDetails.fxml"));
            Parent view = loader.load();

            FXMLScheduleDeliveryDetailsController controller = loader.getController();
            controller.initData(documentName, selectedDeliveryType);

            Stage newStage = new Stage();
            newStage.setScene(new Scene(view));
            newStage.setTitle("Programar entrega");
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setResizable(true);
            newStage.setWidth(600);
            newStage.setHeight(450);
            newStage.showAndWait();

        } catch (IOException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "No se pudo cargar la ventana de programación de entrega.");
            ex.printStackTrace();
        }
    }

    @FXML
    private void btnBackClick(ActionEvent event) {
        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.close();
    }
}