package pe.puyu.pukahttp.infrastructure.javafx.views;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import javafx.stage.Stage;
import pe.puyu.pukahttp.infrastructure.properties.AppPropertyKey;
import pe.puyu.pukahttp.infrastructure.properties.ApplicationProperties;

import java.util.Objects;

public class TrayIconView extends View {

    private FXTrayIcon.Builder builder;
    private FXTrayIcon trayIcon;

    public TrayIconView() {
        super("print-actions.fxml");
    }

    @Override
    public void show() {
        if (trayIcon == null) {
            trayIcon = builder.build();
            trayIcon.setOnAction(e -> super.show());
        }
        trayIcon.show();
    }

    @Override
    protected void config(Stage stage) {
        stage.setTitle("Print Actions");
        String iconResourcePath = "/pe/puyu/pukahttp/infrastructure/javafx/images/icon.png";
        builder = new FXTrayIcon.Builder(stage, Objects.requireNonNull(getClass().getResource(iconResourcePath)));
    }

    public void info(String message) {
        if (isNotificationsEnabled()) {
            this.trayIcon.showInfoMessage("INFO NOTIFICATION", message);
        }
    }

    public void error(String message) {
        if (isNotificationsEnabled()) {
            this.trayIcon.showErrorMessage("ERROR NOTIFICATION", message);
        }
    }

    public void warn(String message) {
        if (isNotificationsEnabled()) {
            this.trayIcon.showWarningMessage("WARNING NOTIFICATION", message);
        }
    }

    private boolean isNotificationsEnabled() {
        return ApplicationProperties.getBoolean(AppPropertyKey.TRAY_NOTIFICATIONS, true);
    }


}
