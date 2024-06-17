package pe.puyu.pukahttp.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import pe.puyu.pukahttp.Constants;
import pe.puyu.pukahttp.app.properties.EnvironmentProperty;
import pe.puyu.pukahttp.app.properties.LogsDirectoryProperty;
import pe.puyu.pukahttp.model.PosConfig;
import pe.puyu.pukahttp.services.api.PrintServer;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import pe.puyu.pukahttp.services.configuration.ConfigAppProperties;
import pe.puyu.pukahttp.services.trayicon.TrayIconServiceProvider;
import pe.puyu.pukahttp.util.AppAlerts;
import pe.puyu.pukahttp.util.AppUtil;
import pe.puyu.pukahttp.util.FxUtil;
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
		var environmentProperty = EnvironmentProperty.get();
		System.setProperty(logsDirectoryProperty.key(), logsDirectoryProperty.value());
		System.setProperty(environmentProperty.key(), environmentProperty.value());
		Platform.setImplicitExit(false);
	}

	@Override
	public void init() {
		this.rootLogger = (Logger) LoggerFactory.getLogger(Constants.PACKAGE_BASE_PATH);
		rootLogger.setLevel(getConfigRootLoggerLevel());
		var isUniqueProcess = configUniqueProcess();
		if (isUniqueProcess) {
			TrayIconServiceProvider.lock();
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
					if (TrayIconServiceProvider.isLock()) {
						Platform.exit();
						System.exit(0);
					} else {
						showActionsPanel(stage);
					}
				} else {
					AppUtil.initTrayIcon(server);
					try {
						server.listen(ip, port);
					} catch (Exception e) {
						server.closeService();
						throw e;
					}
					AppUtil.releaseExpiredTickets(ip, port);
				}
			} else {
				showPosConfigPanel(stage);
			}
		} catch (Exception e) {
			rootLogger.error("Exception on start App!!!, {}", e.getMessage(), e);
			var isOk = AppAlerts.showConfirmation(
				"Servicio de impresión no inicio correctamente.",
				"Por favor informar al equipo de soporte PUYU para poder solucionarlo."
			);
			if (isOk) {
				try {
					FxUtil.newStage(Constants.PASSWORD_FXML, "password").show();
				} catch (Exception ignored) {
					rootLogger.warn("fail on recover recover exception app.");
				}
			} else {
				Platform.exit();
				System.exit(0);
			}
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

	private Level getConfigRootLoggerLevel() {
		ConfigAppProperties config = new ConfigAppProperties();
		Optional<Level> rootLoggerLevel = config.rootLoggerLevel();
		Level loggerLevel;
		if(rootLoggerLevel.isEmpty()){
			boolean isProduction = AppUtil.isProductionEnvironment();
			loggerLevel = isProduction ? Level.INFO : Level.TRACE;
		}else{
			loggerLevel = rootLoggerLevel.get();
		}
		config.rootLoggerLevel(loggerLevel);
		return loggerLevel;
	}

}