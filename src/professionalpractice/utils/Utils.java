package professionalpractice.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.stage.Stage;

public class Utils {

    public static void showSimpleAlert(Alert.AlertType tipo, String title, String message) {
        Alert alert = new Alert(tipo);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean showConfirmationAlert(String title, String message, String s) {
        Alert alertConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
        alertConfirmation.setTitle(title);
        alertConfirmation.setHeaderText(null);
        alertConfirmation.setContentText(message);

        ButtonType acceptButton = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Regresar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alertConfirmation.getButtonTypes().setAll(acceptButton, cancelButton);

        return alertConfirmation.showAndWait().orElse(cancelButton) == acceptButton;
    }

    public static Stage getSceneComponent(Control component) {
        return (Stage)  component.getScene().getWindow();
    }
}
