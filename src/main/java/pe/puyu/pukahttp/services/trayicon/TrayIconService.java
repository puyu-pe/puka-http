package pe.puyu.pukahttp.services.trayicon;

import ch.qos.logback.classic.Logger;
import com.dustinredmond.fxtrayicon.FXTrayIcon;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckMenuItem;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import pe.puyu.pukahttp.Constants;
import pe.puyu.pukahttp.services.configuration.ConfigAppProperties;
import pe.puyu.pukahttp.util.AppAlerts;
import pe.puyu.pukahttp.util.AppUtil;
import pe.puyu.pukahttp.util.FxUtil;

import java.util.Objects;

public class TrayIconService {
	private FXTrayIcon trayIcon;
	private final ConfigAppProperties configProperties;
	private final CheckMenuItem enableNotificationMenuItem;
	private final Logger logger = (Logger) LoggerFactory.getLogger(AppUtil.makeNamespaceLogs("TrayIconService"));

	TrayIconService() {
		configProperties = new ConfigAppProperties();
		enableNotificationMenuItem = new CheckMenuItem(MenuItemLabel.ENABLE_NOTIFICATIONS.getValue());
		enableNotificationMenuItem.setOnAction(this::onClickEnableNotificationsMenu);
		loadTrayIcon(getParentStage());
	}

	public void show() {
		configProperties.notifications().ifPresent(enableNotificationMenuItem::setSelected);
		trayIcon.show();
	}

	public void showInfoMessage(String title, String message) {
		var notificationEnabled = configProperties.notifications();
		if (notificationEnabled.isPresent() && notificationEnabled.get()) {
			this.trayIcon.showInfoMessage(title, message);
		}
	}

	public void showErrorMessage(String title, String message) {
		var notificationEnabled = configProperties.notifications();
		if (notificationEnabled.isPresent() && notificationEnabled.get()) {
			this.trayIcon.showErrorMessage(title, message);
		}
	}

	public void showWarningMessage(String title, String message) {
		var notificationEnabled = configProperties.notifications();
		if (notificationEnabled.isPresent() && notificationEnabled.get()) {
			this.trayIcon.showWarningMessage(title, message);
		}
	}

	private void loadTrayIcon(Stage parentStage) {
		this.trayIcon = new FXTrayIcon.Builder(parentStage, Objects.requireNonNull(getClass().getResource(Constants.ICON_PATH)))
			.menuItem(MenuItemLabel.ABOUT.getValue(), this::onClickAboutMenu)
			.checkMenuItem(enableNotificationMenuItem)
			.separator()
			.menuItem(MenuItemLabel.CLOSE.getValue(), this::onClickCloseMenu).build();
	}

	private void onClickAboutMenu(ActionEvent event) {
		AppAlerts.showInfo(
			"PUYU S.R.L",
			"Servicio de impresión de tickets, boletas y facturas, versión: " + AppUtil.getAppVersion()
		);
	}

	private void onClickEnableNotificationsMenu(ActionEvent event) {
		var isSelected = enableNotificationMenuItem.isSelected();
		enableNotificationMenuItem.setSelected(!isSelected);
		configProperties.notifications(enableNotificationMenuItem.isSelected());
	}

	private Stage getParentStage() {
		Stage parentStage = new Stage();
		try {
			parentStage.setScene(FxUtil.loadScene(Constants.ACTIONS_PANEL_FXML));
			parentStage.setTitle(String.format("Panel de acciones %s", Constants.APP_NAME));
		} catch (Exception e) {
			logger.error("Error on show init window: {}", e.getMessage(), e);
			showErrorMessage("Error", "No se pudo abrir la ventana de acciones: " + e.getLocalizedMessage());
		}
		return parentStage;
	}

	private void onClickCloseMenu(ActionEvent event) {
		AppUtil.safelyShutDownApp();
	}

}