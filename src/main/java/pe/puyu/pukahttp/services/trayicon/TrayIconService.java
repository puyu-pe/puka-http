package pe.puyu.pukahttp.services.trayicon;

import ch.qos.logback.classic.Logger;
import com.dustinredmond.fxtrayicon.FXTrayIcon;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckMenuItem;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import pe.puyu.pukahttp.Constants;
import pe.puyu.pukahttp.services.configuration.ConfigAppProperties;
import pe.puyu.pukahttp.util.AppAlerts;
import pe.puyu.pukahttp.util.AppUtil;
import pe.puyu.pukahttp.util.FileSystemLock;
import pe.puyu.pukahttp.util.FxUtil;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class TrayIconService {
	private FXTrayIcon trayIcon;
	private final ConfigAppProperties configProperties;
	private final CheckMenuItem enableNotificationMenuItem;
	private final Logger logger = (Logger) LoggerFactory.getLogger(AppUtil.makeNamespaceLogs("TrayIconService"));

	private static FileSystemLock lock;
	private Runnable onExit;

	public TrayIconService(Stage parentStage) {
		configProperties = new ConfigAppProperties();
		enableNotificationMenuItem = new CheckMenuItem(MenuItemLabel.ENABLE_NOTIFICATIONS.getValue());
		enableNotificationMenuItem.setOnAction(this::onClickEnableNotificationsMenu);
		loadTrayIcon(parentStage);
	}

	public TrayIconService show() {
		configProperties.notificationEnabled().ifPresent(enableNotificationMenuItem::setSelected);
		trayIcon.show();
		return this;
	}

	public void showInfoMessage(String title, String message) {
		var notificationEnabled = configProperties.notificationEnabled();
		if (notificationEnabled.isPresent() && notificationEnabled.get()) {
			this.trayIcon.showInfoMessage(title, message);
		}
	}

	public void showErrorMessage(String title, String message) {
		var notificationEnabled = configProperties.notificationEnabled();
		if (notificationEnabled.isPresent() && notificationEnabled.get()) {
			this.trayIcon.showErrorMessage(title, message);
		}
	}

	public void showWarningMessage(String title, String message) {
		var notificationEnabled = configProperties.notificationEnabled();
		if (notificationEnabled.isPresent() && notificationEnabled.get()) {
			this.trayIcon.showWarningMessage(title, message);
		}
	}

	public void setOnExit(Runnable onExit) {
		this.onExit = onExit;
	}

	private void loadTrayIcon(Stage parentStage) {
		var builder = new FXTrayIcon.Builder(parentStage, Objects.requireNonNull(getClass().getResource(Constants.ICON_PATH)))
			.menuItem(MenuItemLabel.ABOUT.getValue(), this::onClickAboutMenu)
			.separator()
			.checkMenuItem(enableNotificationMenuItem)
			.menuItem(MenuItemLabel.SHOW_INIT_WINDOW.getValue(), (event) -> onClickShowInitWindow(parentStage));
		// Activate close service only mac
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("mac")) {
			builder
				.separator()
				.menuItem(MenuItemLabel.CLOSE.getValue(), this::onClickCloseMenu);
		}
		this.trayIcon = builder.build();
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
		configProperties.notificationEnabled(enableNotificationMenuItem.isSelected());
	}

	private void onClickShowInitWindow(Stage parentStage) {
		Platform.runLater(() -> {
			try {
				parentStage.setScene(FxUtil.loadScene(Constants.ACTIONS_PANEL_FXML));
				parentStage.setTitle(String.format("Panel de acciones %s", Constants.APP_NAME));
				parentStage.show();
			} catch (Exception e) {
				logger.error("Error on show init window: {}", e.getMessage(), e);
				showErrorMessage("Error", "No se pudo abrir la ventana de acciones: " + e.getLocalizedMessage());
			}
		});
	}

	private void onClickCloseMenu(ActionEvent event) {
		CompletableFuture.runAsync(() -> {
			onExit.run();
			unLock();
			Platform.exit();
			System.exit(0);
		});
	}

	public static boolean isTrayIconLock() {
		var otherLock = new FileSystemLock(AppUtil.makeLockFile("lockTrayIconService"));
		var isLock = otherLock.hasLock();
		if(!isLock){
			otherLock.unLock();
		}
		return isLock;
	}

	public static void lockTrayIcon() {
		lock = new FileSystemLock(AppUtil.makeLockFile("lockTrayIconService"));
	}

	public static void unLock() {
		if (lock != null) {
			lock.unLock();
		}
	}
}