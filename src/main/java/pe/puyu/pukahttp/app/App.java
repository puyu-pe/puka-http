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
import pe.puyu.pukahttp.util.AppUtil;
import pe.puyu.pukahttp.util.FxUtil;
import pe.puyu.pukahttp.util.JsonUtil;
import pe.puyu.pukahttp.validations.PosConfigValidator;

import java.util.LinkedList;
import java.util.Optional;

public class App extends Application {
	// Level error : TRACE DEBUG INFO WARN ERROR
	private Logger rootLogger;

	public App(){
		// important set properties into constructor!, first execution
		var logsDirectoryProperty = LogsDirectoryProperty.get();
		System.setProperty(logsDirectoryProperty.key(), logsDirectoryProperty.value());
	}


	@Override
	public void init() {
		this.rootLogger = (Logger) LoggerFactory.getLogger(Constants.PACKAGE_BASE_PATH);
		rootLogger.setLevel(Level.INFO);
	}

	@Override
	public void start(Stage stage) {
		try {
			var posConfig = recoverPosConfig();
			if (posConfig.isPresent()) {
				PrintServer server = new PrintServer();
				if (server.isRunningInOtherProcess()) {
					showActionsPanel(stage);
				} else {
					server.listen(posConfig.get().getIp(), posConfig.get().getPort());
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

}