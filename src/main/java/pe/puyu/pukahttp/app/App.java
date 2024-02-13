package pe.puyu.pukahttp.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import pe.puyu.pukahttp.Constants;
import pe.puyu.pukahttp.app.properties.LogsDirectoryProperty;
import pe.puyu.pukahttp.model.PosConfig;
import pe.puyu.pukahttp.services.api.PrintServer;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import pe.puyu.pukahttp.services.configuration.ConfigAppProperties;
import pe.puyu.pukahttp.services.trayicon.TrayIconService;
import pe.puyu.pukahttp.util.AppUtil;
import pe.puyu.pukahttp.util.FxUtil;
import pe.puyu.pukahttp.util.HttpUtil;
import pe.puyu.pukahttp.util.JsonUtil;
import pe.puyu.pukahttp.validations.PosConfigValidator;

import java.util.LinkedList;
import java.util.Optional;

public class App extends Application {
	// Level error : TRACE DEBUG INFO WARN ERROR
	private Logger rootLogger;

	public App() {
		// important set properties into constructor!, first execution
		var logsDirectoryProperty = LogsDirectoryProperty.get();
		System.setProperty(logsDirectoryProperty.key(), logsDirectoryProperty.value());
		Platform.setImplicitExit(false);
	}


	@Override
	public void init() {
		this.rootLogger = (Logger) LoggerFactory.getLogger(Constants.PACKAGE_BASE_PATH);
		rootLogger.setLevel(Level.INFO);
		var isUniqueProcess = configUniqueProcess();
		if (isUniqueProcess) {
			TrayIconService.lockTrayIcon();
		}
	}

	@Override
	public void start(Stage stage) {
		try {
			var posConfig = recoverPosConfig();
			if (posConfig.isPresent()) {
				PrintServer server = new PrintServer();
				final var ip = posConfig.get().getIp();
				final var port = posConfig.get().getPort();
				if (server.isRunningInOtherProcess()) {
					// importante hacer esta validación para ya no ejecutar un segundo proceso
					// esto solo si se esta en modo uniqueProcess
					if (TrayIconService.isTrayIconLock()) {
						Platform.exit();
						System.exit(0);
					} else {
						showActionsPanel(stage);
					}
				} else {
					initTrayIcon(stage, ip, port, server);
					server.listen(ip, port);
					AppUtil.releaseExpiredTickets(ip, port);
				}
			} else {
				showPosConfigPanel(stage);
			}
		} catch (Exception e) {
			rootLogger.error("Exception on start App!!!, {}", e.getMessage(), e);
			Platform.exit();
			System.exit(0);
		}
	}

	private void showActionsPanel(Stage stage) {
		Platform.runLater(() -> {
			try {
				stage.setScene(FxUtil.loadScene(Constants.ACTIONS_PANEL_FXML));
				stage.setTitle(String.format("Panel de acciones %s", Constants.APP_NAME));
				stage.show();
			} catch (Exception e) {
				rootLogger.error("Exception on show Actions panel: {}", e.getMessage(), e);
			}
		});
	}

	private void showPosConfigPanel(Stage stage) {
		Platform.runLater(() -> {
			try {
				stage.setScene(FxUtil.loadScene(Constants.POS_CONFIG_FXML));
				stage.setTitle(String.format("Configuración %s", Constants.APP_NAME));
				stage.show();
			} catch (Exception e) {
				rootLogger.error("Exception on show PosConfig panel: {}", e.getMessage(), e);
			}
		});
	}

	@Override
	public void stop() {
	}

	public static void main(String[] args) {
		launch(args);
	}

	private Optional<PosConfig> recoverPosConfig() {
		try {
			var configOpt = JsonUtil.convertFromJson(AppUtil.getPosConfigFileDir(), PosConfig.class);
			if (configOpt.isEmpty()) {
				return Optional.empty();
			}
			var errors = new LinkedList<String>();
			var config = configOpt.get();
			errors.addAll(PosConfigValidator.validateIp(config.getIp()));
			errors.addAll(PosConfigValidator.validatePort(String.valueOf(config.getPort())));
			errors.addAll(PosConfigValidator.validatePassword(config.getPassword()));
			if (!errors.isEmpty())
				return Optional.empty();
			return configOpt;
		} catch (Exception e) {
			rootLogger.error("Exception on recover PosConfig: {}", e.getLocalizedMessage());
			return Optional.empty();
		}
	}

	private boolean configUniqueProcess() {
		ConfigAppProperties config = new ConfigAppProperties();
		if (config.uniqueProcess().isPresent()) {
			return config.uniqueProcess().get();
		}
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win") || os.contains("mac")) {
			config.uniqueProcess(true);
		} else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
			config.uniqueProcess(false);
		} else {
			rootLogger.warn("Unidentified Operating System, uniquerProcess not set.");
		}
		return config.uniqueProcess().get();
	}

	private void initTrayIcon(Stage stage, String ipSnapshot, int portSnapshot, PrintServer server) {
		var config = new ConfigAppProperties();
		var uniqueProcess = config.uniqueProcess();
		if (uniqueProcess.isEmpty() || !uniqueProcess.get()) {
			return;
		}
		var trayIcon = getTrayIconService(stage, ipSnapshot, portSnapshot);
		server.addListenerErrorNotification(trayIcon::showErrorMessage);
		server.addListenerInfoNotification(trayIcon::showInfoMessage);
		server.addListenerWarnNotification(trayIcon::showWarningMessage);
		trayIcon.show();
	}

	@NotNull
	private TrayIconService getTrayIconService(Stage stage, String ipSnapshot, int portSnapshot) {
		var trayIcon = new TrayIconService(stage);
		trayIcon.setOnExit(() -> {
			try {
				String baseUrl = String.format("http://%s:%d", ipSnapshot, portSnapshot);
				var url = baseUrl + "/stop-service";
				var response = HttpUtil.get(url);
				if (response.getStatus().equals("success")) {
					rootLogger.info("status success stop-service from trayIcon");
				} else {
					rootLogger.warn("status error stop-service from trayIcon: {}", response.getMessage());
				}
			} catch (Exception e) {
				rootLogger.error("Exception on exit trayicon: {}", e.getMessage(), e);
			}
		});
		return trayIcon;
	}

}