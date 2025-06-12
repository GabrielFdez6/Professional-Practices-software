package professionalpractice;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import professionalpractice.utils.Utils;

import java.io.IOException;

public class ProfessionalPractices extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("view/FXMLLogIn.fxml"));
            Scene sceneLogIn = new Scene(view);

            primaryStage.setScene(sceneLogIn);
            primaryStage.setTitle("Inicio de Sesión");
            primaryStage.show();

        } catch (IOException ex) {
            Utils.showSimpleAlert(Alert.AlertType.ERROR, "Error al cargar", "Lo sentimos por el momento no se pudo recuperar la ventana para iniciar sesión");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
