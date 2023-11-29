package pe.puyu.sweetprinterpos.views;

import ch.qos.logback.classic.Logger;
import javafx.beans.Observable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import pe.puyu.sweetprinterpos.model.PosConfig;
import pe.puyu.sweetprinterpos.services.api.PrintServer;
import pe.puyu.sweetprinterpos.services.api.ResponseApi;
import pe.puyu.sweetprinterpos.util.AppUtil;
import pe.puyu.sweetprinterpos.util.HttpUtil;
import pe.puyu.sweetprinterpos.util.JsonUtil;

import java.net.URL;
import java.util.ResourceBundle;

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
			} catch (NumberFormatException e) {
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
		PrintServer server = new PrintServer();
		try {
			if (server.isRunningInOtherProcess()) {
				throw new Exception("Server is already running in another process");
			}
			server.listen(posConfig.getIp(), posConfig.getPort());
			JsonUtil.saveJson(AppUtil.getPosConfigFileDir(), posConfig);
			getStage().close();
		} catch (Exception e) {
			server.closeService();
			logger.error("Exception on start server {}", e.getLocalizedMessage(), e);
		}
	}

	@FXML
	void onStop() {
		try {
			var url = baseUrl + "/stop-service";
			var response = HttpUtil.get(url);
			if (response.getStatus().equals("success")) {
				txtIp.setDisable(false);
				txtPort.setDisable(false);
				btnStop.setDisable(true);
				btnStart.setDisable(false);
				cmbLevelLogs.setDisable(true);
			} else {
				logger.warn("{}: {}", response.getMessage(), response.getError());
			}
		} catch (Exception e) {
			logger.error("Exception on stop service: {}", e.getLocalizedMessage());
		}
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

}
