package professionalpractice.controller.coordinator;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import professionalpractice.model.dao.ScheduleDeliveryDAO;
import professionalpractice.model.pojo.Delivery;
import professionalpractice.model.pojo.OperationResult;
import professionalpractice.utils.Utils;

public class FXMLScheduleDeliveryDetailsController implements Initializable {

    @FXML
    private TextField tfNombre;
    @FXML
    private TextArea taDescripcion;
    @FXML
    private DatePicker dpFechaInicio;
    @FXML
    private DatePicker dpFechaFin;
    private String tipoEntrega;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void inicializarInformacion(String tipoEntrega, String nombreDocumento){
        this.tipoEntrega = tipoEntrega;
        this.tfNombre.setText(nombreDocumento);
    }

    private boolean validarCampos(){
        if(tfNombre.getText().isEmpty() || taDescripcion.getText().isEmpty() || dpFechaInicio.getValue() == null || dpFechaFin.getValue() == null){
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Campos Vacíos", "Existen campos vacíos, por favor llena todos los campos");
            return false;
        }
        if(dpFechaFin.getValue().isBefore(dpFechaInicio.getValue())){
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Fechas Incorrectas", "La fecha de fin no puede ser anterior a la fecha de inicio");
            return false;
        }
        return true;
    }

    @FXML
    private void btnClicCancelar(ActionEvent event) {
        boolean confirmado = Utils.showConfirmationAlert("Cancelar Operación",
                "¿Estás seguro de que quieres cancelar?", "Cualquier dato no guardado se perderá.");
        if (confirmado) {
            cerrarVentana();
        }
    }

    private void cerrarVentana(){
        Utils.getSceneComponent(tfNombre).close();
    }

    @FXML
    private void btnClicAceptar(ActionEvent event) {
        if(validarCampos()){
            String tablaDestino = "";
            switch(tipoEntrega){
                case "DOCUMENTOS INICIALES": tablaDestino = "entregadocumentoinicio"; break;
                case "REPORTES": tablaDestino = "entregareporte"; break;
                case "DOCUMENTOS FINALES": tablaDestino = "entregadocumentofinal"; break;
            }

            Delivery nuevaEntrega = new Delivery();
            nuevaEntrega.setName(tfNombre.getText());
            nuevaEntrega.setDescription(taDescripcion.getText());
            nuevaEntrega.setStartDate(Timestamp.valueOf(dpFechaInicio.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
            nuevaEntrega.setEndDate(Timestamp.valueOf(dpFechaFin.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

            try{
                // Se llama al nuevo método transaccional
                OperationResult resultado = ScheduleDeliveryDAO.programarEntregaPeriodoActual(nuevaEntrega, tablaDestino);

                if(!resultado.isError()){
                    Utils.showSimpleAlert(Alert.AlertType.INFORMATION, "Operación Exitosa", resultado.getMensaje());
                    cerrarVentana();
                } else {
                    Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error", resultado.getMensaje());
                }

            } catch(SQLException e){
                Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error de Conexión", "No fue posible conectar con la base de datos.");
                e.printStackTrace();
            }
        }
    }

}
