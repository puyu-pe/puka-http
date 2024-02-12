package pe.puyu.pukahttp.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
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
import java.util.concurrent.CompletableFuture;

public class App extends Application {
	// Level error : TRACE DEBUG INFO WARN ERROR
	private Logger rootLogger;

	public App() {
		// important set properties into constructor!, first execution
		var logsDirectoryProperty = LogsDirectoryProperty.get();
		System.setProperty(logsDirectoryProperty.key(), logsDirectoryProperty.value());
	}


	@Override
	public void init() {
		this.rootLogger = (Logger) LoggerFactory.getLogger(Constants.PACKAGE_BASE_PATH);
		rootLogger.setLevel(Level.INFO);
		configTrayIconEnabled();
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
					AppUtil.releaseExpiredTickets(ip, port);
					showActionsPanel(stage);
				} else {
					server.listen(ip, port);
					AppUtil.releaseExpiredTickets(ip, port);
					initTrayIcon(stage, ip, port);
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

	private void showActionsPanel(Stage stage) throws Exception {
		stage.setScene(FxUtil.loadScene(Constants.ACTIONS_PANEL_FXML));
		stage.setTitle(String.format("Panel de acciones %s", Constants.APP_NAME));
		stage.show();
	}

	private void showPosConfigPanel(Stage stage) throws Exception {
		stage.setScene(FxUtil.loadScene(Constants.POS_CONFIG_FXML));
		stage.setTitle(String.format("Configuración %s", Constants.APP_NAME));
		stage.show();
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

	private void configTrayIconEnabled() {
		ConfigAppProperties config = new ConfigAppProperties();
		if (config.trayIconEnabled().isPresent()) {
			return;
		}
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win") || os.contains("mac")) {
			config.trayIconEnabled(true);
		} else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
			config.trayIconEnabled(false);
		} else {
			rootLogger.warn("Unidentified Operating System, trayIconEnabled not set.");
		}
	}

	private void initTrayIcon(Stage stage, String ip, int port) {
		var config = new ConfigAppProperties();
		var trayIconEnabled = config.trayIconEnabled();
		if (trayIconEnabled.isEmpty() || !trayIconEnabled.get()) {
			return;
		}
		var trayIcon = new TrayIconService(stage);
		trayIcon.setOnExit(() -> {
			try {
				String baseUrl = String.format("http://%s:%d", ip, port);
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
		trayIcon.show();
		trayIcon.showInfoMessage("Servicio online.", "Servicio de impresión ejecutandose en segundo plano.");
	}

}