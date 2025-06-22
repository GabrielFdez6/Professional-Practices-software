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
import javafx.stage.Modality;
import javafx.stage.Stage;
import professionalpractice.controller.coordinator.FXMLSelectDocumentController;
import professionalpractice.utils.Utils;

public class FXMLScheduleDeliveryController implements Initializable {

  @FXML
  private ListView<String> lvTipoEntrega;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    cargarTiposDeEntrega();
  }

  private void cargarTiposDeEntrega(){
    ObservableList<String> tipos = FXCollections.observableArrayList();
    tipos.addAll("DOCUMENTOS INICIALES", "REPORTES", "DOCUMENTOS FINALES");
    lvTipoEntrega.setItems(tipos);
  }

  @FXML
  private void btnClicContinuar(ActionEvent event) {
    String tipoSeleccionado = lvTipoEntrega.getSelectionModel().getSelectedItem();
    if(tipoSeleccionado != null){
      try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/professionalpractice/view/coordinator/FXMLSelectDocument.fxml"));
        Parent vista = loader.load();
        FXMLSelectDocumentController controller = loader.getController();
        controller.inicializarInformacion(tipoSeleccionado);

        Stage escenario = new Stage();
        escenario.setTitle("Seleccionar Tipo de Documento");
        escenario.setScene(new Scene(vista));
        escenario.show();
        cerrarVentana();
      } catch (IOException e) {
        Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo encontrar la ventana anterior para regresar.");
      }
    } else {
      Utils.showSimpleAlert(Alert.AlertType.WARNING, "Selección Requerida", "Por favor, selecciona un tipo de documento para continuar.");
    }
  }

  @FXML
  private void btnClicRegresar(ActionEvent event) {
    cerrarVentana();
  }

  private void cerrarVentana(){
    Utils.getSceneComponent(lvTipoEntrega).close();
  }

}