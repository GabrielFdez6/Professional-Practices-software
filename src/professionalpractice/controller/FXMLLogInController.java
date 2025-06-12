package professionalpractice.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import professionalpractice.ProfessionalPractices;
import professionalpractice.model.ConectionBD;
import professionalpractice.utils.Utils;

public class FXMLLogInController implements Initializable {

    @FXML
    private TextField tfUsername;
    @FXML
    private TextField tfPassword;
    @FXML
    private Label lbErrorUsername;
    @FXML
    private Label lbErrorPassword;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Conexion
    }        

    @FXML
    private void btnClickLogIn(ActionEvent event) {
        String username = tfUsername.getText();
        String password = tfPassword.getText();
        
        if (verifyFields(username, password))
            verifyCredentials(username, password);
    }
    
    private boolean verifyFields(String username, String password) {
        boolean validFields = true;
        lbErrorUsername.setText("");
        lbErrorPassword.setText("");
        
        if (username.isEmpty()) {
            lbErrorUsername.setText("Usuario obligatorio");
            validFields = false;
        }
        
        if (password.isEmpty()) {
            lbErrorPassword.setText("Contraseña obligatoria");
            validFields = false;
        }
        
        return validFields;
    }
    
    private void verifyCredentials(String username, String password) {
        /*try {
            
        } catch() {
            
        } */
    }

    private void goHomeScreen() {
        try {
            Stage baseScenario = (Stage) tfUsername.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(ProfessionalPractices.class.getResource("view/FXMLHomeScreen.fxml"));
            Parent view = loader.load();

            FXMLHomeScreenController controller = loader.getController();
            controller.initializeInformation();

            Scene mainScene = new Scene(view);
            baseScenario.setScene(mainScene);
            baseScenario.setTitle("Pagina Principal");
            baseScenario.showAndWait();
        } catch(IOException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos por el momento no se pudo mostrar la ventana");
        }
    }
    
}
