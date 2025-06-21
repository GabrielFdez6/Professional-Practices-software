package professionalpractice.controller.coordinator;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import professionalpractice.ProfessionalPractices;
import professionalpractice.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLScheduleDeliveryController implements Initializable {

  @FXML private ComboBox<String> cmbDeliveryType;
  @FXML private Button btnCancel;
  @FXML private Button btnContinue;
  @FXML private Label lblLegend;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    cmbDeliveryType.setItems(FXCollections.observableArrayList("DOCUMENTOS INICIALES", "REPORTES", "DOCUMENTOS FINALES"));
    if (lblLegend != null) {
      lblLegend.setText("Selecciona el tipo de documento para esta entrega");
    }
  }

  @FXML
  void btnContinueClick(ActionEvent event) {
    String selectedType = cmbDeliveryType.getSelectionModel().getSelectedItem();
    if (selectedType == null) {
      Utils.showSimpleAlert(Alert.AlertType.WARNING, "Selección Requerida", "Por favor, selecciona un tipo de documento.");
      return;
    }

    try {
      Stage currentStage = (Stage) cmbDeliveryType.getScene().getWindow();
      currentStage.close();

      FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/coordinator/FXMLSelectDocument.fxml"));
      Parent view = loader.load();

      FXMLSelectDocumentController controller = loader.getController();
      controller.initData(selectedType);

      Stage newStage = new Stage();
      newStage.setScene(new Scene(view));
      newStage.setTitle("Seleccionar Documento (" + selectedType + ")");
      newStage.initModality(Modality.APPLICATION_MODAL);
      newStage.setResizable(false); // Fijo, como se solicitó antes
      newStage.setWidth(800);
      newStage.setHeight(580); // Altura ajustada
      newStage.showAndWait();

    } catch (IOException ex) {
      Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "No se pudo cargar la siguiente ventana.");
      ex.printStackTrace();
    }
  }

  @FXML
  void btnCancelClick(ActionEvent event) {
    if (Utils.showConfirmationAlert("Cancelar Proceso", "¿Estás seguro que quieres cancelar?")) {
      closeWindow();
    }
  }

  private void closeWindow() {
    Stage stage = (Stage) btnCancel.getScene().getWindow();
    stage.close();
  }
}