package professionalpractice.controller.coordinator;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import professionalpractice.model.dao.ScheduleDeliveryDAO;
// No necesitamos importar professionalpractice.model.pojo.Delivery aquí para la programación de la definición
// import professionalpractice.model.pojo.Delivery; // Ya no se usa directamente para construir la entrega aquí
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
    private String tipoEntrega; // Este es el "DOCUMENTOS INICIALES", "REPORTES", etc.

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización
    }

    public void inicializarInformacion(String tipoEntrega, String nombreDocumento){
        this.tipoEntrega = tipoEntrega;
        this.tfNombre.setText(nombreDocumento);
    }

    private boolean validarCampos(){
        if(tfNombre.getText().isEmpty() || taDescripcion.getText().isEmpty() || dpFechaInicio.getValue() == null || dpFechaFin.getValue() == null){ // Asegúrate de validar taDescripcion también
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Campos Vacíos", "Existen campos vacíos, por favor llena todos los campos.");
            return false;
        }

        LocalDate fechaInicio = dpFechaInicio.getValue();
        LocalDate fechaFin = dpFechaFin.getValue();
        LocalDate fechaActual = LocalDate.now();

        if(fechaInicio.isBefore(fechaActual)){
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Fecha de Inicio Incorrecta", "La fecha de inicio no puede ser anterior a la fecha actual.");
            return false;
        }

        if(fechaFin.isBefore(fechaInicio)){
            Utils.showSimpleAlert(Alert.AlertType.WARNING, "Fechas Incorrectas", "La fecha de fin no puede ser anterior a la fecha de inicio.");
            return false;
        }

        return true;
    }

    @FXML
    private void btnClicCancelar(ActionEvent event) {
        boolean confirmado = Utils.showConfirmationAlert("Cancelar Operación",
                "¿Estás seguro de que quieres cancelar?");
        if (confirmado) {
            cerrarVentana();
        }
    }

    private void cerrarVentana(){
        Utils.getSceneComponent(tfNombre).close();
    }

    @FXML
    private void btnClicProgramar(ActionEvent event) {
        if(validarCampos()){
            // Obtener los datos directamente de los componentes de la UI para la DEFINICIÓN de la entrega
            String nombreDefinicion = tfNombre.getText();
            String descripcionDefinicion = taDescripcion.getText();
            Timestamp fechaInicioDefinicion = Timestamp.valueOf(dpFechaInicio.getValue().atStartOfDay());
            Timestamp fechaFinDefinicion = Timestamp.valueOf(dpFechaFin.getValue().atStartOfDay());

            try{
                // Llama al DAO pasando los datos individuales para crear la definición y las instancias
                // Ya no pasamos el POJO Delivery aquí, sino los campos de la definición.
                OperationResult resultado = ScheduleDeliveryDAO.programarEntregaPeriodoActual(
                        nombreDefinicion,
                        descripcionDefinicion,
                        fechaInicioDefinicion,
                        fechaFinDefinicion,
                        tipoEntrega // Este es el String "DOCUMENTOS INICIALES", "REPORTES", etc.
                );

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