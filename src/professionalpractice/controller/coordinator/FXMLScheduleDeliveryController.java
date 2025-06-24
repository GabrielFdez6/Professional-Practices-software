package professionalpractice.controller.coordinator;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import professionalpractice.utils.Utils;

public class FXMLScheduleDeliveryController implements Initializable {

  @FXML
  private ListView<String> lvDeliveryType;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    loadDeliveryTypes();
  }

  private void loadDeliveryTypes(){
    ObservableList<String> types = FXCollections.observableArrayList();
    types.addAll("DOCUMENTOS INICIALES", "REPORTES", "DOCUMENTOS FINALES");
    lvDeliveryType.setItems(types);
  }

  @FXML
  private void btnClickContinue(ActionEvent event) {
    String selectedType = lvDeliveryType.getSelectionModel().getSelectedItem();
    if(selectedType != null){
      try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/professionalpractice/view/coordinator/FXMLSelectDocument.fxml"));
        Parent view = loader.load();
        FXMLSelectDocumentController controller = loader.getController();
        controller.initializeInformation(selectedType);

        Stage stage = new Stage();
        stage.setTitle("Seleccionar Documento");
        stage.setScene(new Scene(view));
        stage.show();
        closeWindow();
      } catch (IOException e) {
        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo encontrar la ventana anterior para regresar.");
      }
    } else {
      Utils.showSimpleAlert(Alert.AlertType.WARNING, "Selección Requerida", "Por favor, selecciona un tipo de documento para continuar.");
    }
  }

  @FXML
  private void btnClickBack(ActionEvent event) {
    closeWindow();
  }

  private void closeWindow(){
    Utils.getSceneComponent(lvDeliveryType).close();
  }

}