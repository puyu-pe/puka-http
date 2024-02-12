package pe.puyu.pukahttp.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AppAlerts {
  public static void showWarning(String headerText, String contentText) {
    Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.setTitle("Advertencia");
    alert.setHeaderText(headerText);
    alert.setContentText(contentText);
    alert.showAndWait();
  }
  public static boolean showConfirmation(String headerText, String contentText) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirmar acción");
    alert.setHeaderText(headerText);
    alert.setContentText(contentText);
    var result = alert.showAndWait();
    return result.isPresent() && result.get() == ButtonType.OK;
  }
  public static void showInfo(String headerText, String contentText){
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Ventana informativa");
    alert.setHeaderText(headerText);
    alert.setContentText(contentText);
    alert.showAndWait();
  }
}
