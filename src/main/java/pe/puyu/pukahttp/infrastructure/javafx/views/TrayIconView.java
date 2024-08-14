package pe.puyu.pukahttp.infrastructure.javafx.views;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import pe.puyu.pukahttp.infrastructure.javafx.controllers.TrayIconController;
import pe.puyu.pukahttp.infrastructure.javafx.injection.TrayIconDependencyInjection;
import pe.puyu.pukahttp.infrastructure.properties.AppPropertyKey;
import pe.puyu.pukahttp.infrastructure.properties.ApplicationProperties;

import java.util.Objects;

public class TrayIconView extends View {

    private FXTrayIcon.Builder builder;
    private FXTrayIcon trayIcon = null;

    private final CheckMenuItem enableNotificationsMenuItem = new CheckMenuItem("Notifications");
    private final MenuItem aboutMenuItem = new MenuItem("About");
    private final MenuItem closeMenuItem = new MenuItem("Close");

    public TrayIconView() {
        super("print-actions.fxml");
    }

    @Override
    public void show() {
        loadTrayIcon();
        trayIcon.show();
    }

    @Override
    protected void config(Stage stage) {
        stage.setTitle("Print Actions");
        String iconResourcePath = "/pe/puyu/pukahttp/infrastructure/javafx/images/icon.png";
        builder = new FXTrayIcon.Builder(stage, Objects.requireNonNull(getClass().getResource(iconResourcePath)));
    }

    public void info(String message) {
        if (isNotificationsEnabled() && this.trayIcon != null) {
            this.trayIcon.showInfoMessage("INFO NOTIFICATION", message);
        }
    }

    public void error(String message) {
        if (isNotificationsEnabled() && this.trayIcon != null) {
            this.trayIcon.showErrorMessage("ERROR NOTIFICATION", message);
        }
    }

    public void warn(String message) {
        if (isNotificationsEnabled() && this.trayIcon != null) {
            this.trayIcon.showWarningMessage("WARNING NOTIFICATION", message);
        }
    }

    private boolean isNotificationsEnabled() {
        return ApplicationProperties.getBoolean(AppPropertyKey.TRAY_NOTIFICATIONS, true);
    }

    private void loadTrayIcon() {
        if (trayIcon == null) {
            TrayIconController controller = TrayIconDependencyInjection.loadController(TrayIconController.class);
            aboutMenuItem.setOnAction(controller::onAbout);
            closeMenuItem.setOnAction(controller::onClose);
            enableNotificationsMenuItem.setOnAction(e -> controller.onEnabledNotifications(e, enableNotificationsMenuItem));
            trayIcon = builder
                .menuItems(aboutMenuItem, enableNotificationsMenuItem)
                .separator()
                .menuItem(closeMenuItem)
                .build();
            trayIcon.setOnAction(e -> super.show());
        }
    }

}
