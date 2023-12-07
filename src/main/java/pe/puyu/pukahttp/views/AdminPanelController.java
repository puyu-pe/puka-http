package pe.puyu.pukahttp.views;

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
		btnStart.setDisable(true);
		txtIp.setDisable(true);
		txtPort.setDisable(true);
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
		posConfig.copyFrom(AppUtil.recoverPosConfig());
		baseUrl = String.format("http://%s:%d", posConfig.getIp(), posConfig.getPort());
		initCmbLevelLogs();
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
				if (server.isRunningInOtherProcess()) {
					throw new Exception("Server is already running in another process");
				}
				var errors = PosConfigValidator.validateIp(txtIp.getText());
				errors.addAll(PosConfigValidator.validatePort(txtPort.getText()));
				if(!errors.isEmpty())
						throw new Exception(errors.toString());
				server.listen(txtIp.getText(), Integer.parseInt(txtPort.getText()));
				JsonUtil.saveJson(AppUtil.getPosConfigFileDir(), posConfig);
				Platform.runLater(() -> getStage().close());
			} catch (Exception e) {
				server.closeService();
				logger.error("Exception on start server {}", e.getLocalizedMessage(), e);
				Platform.runLater(() ->  {
					btnStart.setDisable(false);
					lblError.setText(e.getLocalizedMessage());
				} );
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

	private void initCmbLevelLogs() {
		cmbLevelLogs.getItems().addAll("TRACE", "DEBUG", "INFO", "WARN", "ERROR");
		cmbLevelLogs.setValue("INFO");
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
