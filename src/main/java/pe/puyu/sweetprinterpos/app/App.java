package pe.puyu.sweetprinterpos.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import pe.puyu.sweetprinterpos.Constants;
import pe.puyu.sweetprinterpos.model.PosConfig;
import pe.puyu.sweetprinterpos.services.api.PrintServer;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import pe.puyu.sweetprinterpos.util.AppUtil;
import pe.puyu.sweetprinterpos.util.JsonUtil;
import pe.puyu.sweetprinterpos.validations.PosConfigValidator;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;

public class App extends Application {
	// Level error : TRACE DEBUG INFO WARN ERROR
	private static final Logger rootLogger = (Logger) LoggerFactory.getLogger(Constants.PACKAGE_BASE_PATH);

	@Override
	public void init() {
		rootLogger.setLevel(Level.TRACE);
	}

	@Override
	public void start(Stage stage) {
		try {
			var posConfig = recoverPosConfig();
			if (posConfig.isPresent()) {
				PrintServer server = new PrintServer();
				if (server.isRunningInOtherProcess()) {
					System.out.println("show actions panel here !!!");
				} else {
					server.listen(posConfig.get().getIp(), posConfig.get().getPort());
				}
			} else {
				Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(Constants.POS_CONFIG_FXML)));
				Scene scene = new Scene(root);
				stage.setScene(scene);
				stage.setTitle("Configuración SweetPrinterPOS");
				stage.show();
			}
		} catch (Exception e) {
			rootLogger.error("Exception on start App!!!, {}", e.getMessage(), e);
			Platform.exit();
		}
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