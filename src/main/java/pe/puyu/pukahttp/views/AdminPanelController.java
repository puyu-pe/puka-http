package pe.puyu.pukahttp.views;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import pe.puyu.pukahttp.model.PosConfig;
import pe.puyu.pukahttp.services.api.PrintServer;
import pe.puyu.pukahttp.services.api.ResponseApi;
import pe.puyu.pukahttp.services.configuration.ConfigAppProperties;
import pe.puyu.pukahttp.services.trayicon.TrayIconServiceProvider;
import pe.puyu.pukahttp.util.AppUtil;
import pe.puyu.pukahttp.util.HttpUtil;
import pe.puyu.pukahttp.util.JsonUtil;
import pe.puyu.pukahttp.validations.PosConfigValidator;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class AdminPanelController implements Initializable {
	private final Logger logger = (Logger) LoggerFactory.getLogger(AppUtil.makeNamespaceLogs("AdminPanelController"));
	private final PosConfig posConfig = new PosConfig();

	private final SimpleIntegerProperty portNumberProperty = new SimpleIntegerProperty(7172);
	private String baseUrl;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		posConfig.ipProperty().bindBidirectional(txtIp.textProperty());
		posConfig.portProperty().bindBidirectional(portNumberProperty);
		txtPort.textProperty().addListener((observable, oldValue, newValue) -> {
			try {
				portNumberProperty.set(Integer.parseInt(newValue));
			} catch (Exception e) {
				portNumberProperty.set(posConfig.getPort());
			}
		});
		portNumberProperty.addListener((observable, oldValue, newValue) -> txtPort.setText(newValue.toString()));
		posConfig.copyFrom(AppUtil.recoverPosConfigDefaultValues());
		baseUrl = String.format("http://%s:%d", posConfig.getIp(), posConfig.getPort());
		testConnectionServer();
		initCmbLevelLogs();
	}

	@FXML
	void onMouseEnteredWindow() {
		getStage().setOnCloseRequest(event -> {
			try {
				PrintServer server = new PrintServer();
				tryGetUpServer(server);
			} catch (Exception e) {
				logger.warn("Exception when trying wake up to server: {}!!!", e.getMessage());
			}
		});
	}

	@FXML
	void onConfiguration() {
		AppUtil.openInNativeFileExplorer(AppUtil.getUserDataDir());
	}

	@FXML
	void onLogs() {
		AppUtil.openInNativeFileExplorer(AppUtil.getLogsDirectory());
	}

	@FXML
	void onStart() {
		btnStart.setDisable(true);
		CompletableFuture.runAsync(() -> {
			PrintServer server = new PrintServer();
			try {
				if (!tryGetUpServer(server)) {
					throw new Exception("server is already running in other process!!");
				}
				Platform.runLater(() -> getStage().close());
			} catch (Exception e) {
				logger.error("Exception on start server {}", e.getLocalizedMessage());
				Platform.runLater(() -> {
					btnStart.setDisable(false);
					lblError.setText(e.getLocalizedMessage());
				});
			}
		});
	}

	@FXML
	void onStop() {
		btnStop.setDisable(true);
		CompletableFuture.runAsync(() -> {
			try {
				var url = baseUrl + "/stop-service";
				var response = HttpUtil.get(url);
				if (response.getStatus().equals("success")) {
					Platform.runLater(() -> {
						txtIp.setDisable(false);
						txtPort.setDisable(false);
						btnStop.setDisable(true);
						btnStart.setDisable(false);
						cmbLevelLogs.setDisable(true);
					});
				} else {
					logger.warn("{}: {}", response.getMessage(), response.getError());
					Platform.runLater(() -> {
						btnStop.setDisable(false);
						lblError.setText(response.getError());
					});
				}
			} catch (Exception e) {
				logger.error("Exception on stop service: {}", e.getLocalizedMessage());
				Platform.runLater(() -> {
					btnStop.setDisable(false);
					lblError.setText(e.getLocalizedMessage());
				});
			}
		});
	}

	private boolean tryGetUpServer(PrintServer server) throws Exception {
		try {
			if (!server.isRunningInOtherProcess()) {
				var errors = PosConfigValidator.validateIp(txtIp.getText());
				errors.addAll(PosConfigValidator.validatePort(txtPort.getText()));
				if (!errors.isEmpty())
					throw new Exception(errors.toString());
				JsonUtil.saveJson(AppUtil.getPosConfigFileDir(), posConfig);
				Platform.runLater(() -> getStage().close());
				if (TrayIconServiceProvider.isLock()) {
					var trayIcon = TrayIconServiceProvider.instance();
					server.addListenerErrorNotification(trayIcon::showErrorMessage);
					server.addListenerInfoNotification(trayIcon::showInfoMessage);
					server.addListenerWarnNotification(trayIcon::showWarningMessage);
				}
				server.listen(txtIp.getText(), Integer.parseInt(txtPort.getText()));
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			server.closeService();
			throw e;
		}
	}

	private void initCmbLevelLogs() {
		ConfigAppProperties config = new ConfigAppProperties();
		Level loggerLevel = config.rootLoggerLevel().isEmpty() ? Level.DEBUG : config.rootLoggerLevel().get();
		cmbLevelLogs.getItems().addAll("TRACE", "DEBUG", "INFO", "WARN", "ERROR");
		cmbLevelLogs.setValue(loggerLevel.toString().toUpperCase());
		try {
			var url = baseUrl + "/info/debug";
			ResponseApi<String> response = HttpUtil.get(url);
			cmbLevelLogs.setValue(response.getData());
		} catch (Exception e) {
			logger.error("Exception at recover Log Level from Server {}: {}", baseUrl, e.getLocalizedMessage(), e);
		}
		cmbLevelLogs.valueProperty().addListener(this::changeLogLevel);
	}

	private void changeLogLevel(Observable observable) {
		try {
			var url = baseUrl + "/info/debug";
			ResponseApi<String> response = HttpUtil.put(url, cmbLevelLogs.getValue());
			logger.info("Update Log Level into server to {}", response.getData());
		} catch (Exception e) {
			logger.error("Exception at change log level: {}", e.getLocalizedMessage());
		}
	}

	private void testConnectionServer() {
		var url = baseUrl + "/test-connection";
		try{
			HttpUtil.getString(url);
			btnStart.setDisable(true);
			btnStop.setDisable(false);
			cmbLevelLogs.setDisable(false);
			txtIp.setDisable(true);
			txtPort.setDisable(true);
		}catch (Exception e){
			btnStart.setDisable(false);
			btnStop.setDisable(true);
			cmbLevelLogs.setDisable(true);
			txtIp.setDisable(false);
			txtPort.setDisable(false);
		}
	}

	private Stage getStage() {
		return (Stage) root.getScene().getWindow();
	}

	@FXML
	private Button btnStart;

	@FXML
	private Button btnStop;

	@FXML
	private ComboBox<String> cmbLevelLogs;

	@FXML
	private GridPane root;

	@FXML
	private TextField txtIp;

	@FXML
	private TextField txtPort;

	@FXML
	private Label lblError;
}
