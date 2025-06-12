package professionalpractice.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import professionalpractice.model.ConectionBD;

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
        try {
            
        } catch() {
            
        }
    }
    
}
