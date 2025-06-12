package professionalpractice.utils;

import javafx.scene.control.Alert;

public class Utils {

    public static void showSimpleAlert(Alert.AlertType tipo, String title, String message) {
        Alert alert = new Alert(tipo);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
