package pe.puyu.pukahttp.views;

import ch.qos.logback.classic.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import pe.puyu.pukahttp.Constants;
import pe.puyu.pukahttp.model.PosConfig;
import pe.puyu.pukahttp.model.UserConfig;
import pe.puyu.pukahttp.services.api.ResponseApi;
import pe.puyu.pukahttp.util.*;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class ActionsPanelController implements Initializable {
	private final Logger logger = (Logger) LoggerFactory.getLogger(AppUtil.makeNamespaceLogs("ActionsPanelController"));
	private final String baseUrl;
	private final PosConfig posConfig;
	private WebSocketContainer container;

	public ActionsPanelController() {
		posConfig = new PosConfig();
		posConfig.copyFrom(AppUtil.recoverPosConfigDefaultValues());
		baseUrl = String.format("http://%s:%d", posConfig.getIp(), posConfig.getPort());
		container = null;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		try {
			AppUtil.releaseExpiredTickets(posConfig.getIp(), posConfig.getPort());
		} catch (Exception e) {
			logger.error("Error al liberar tickets: {}", e.getMessage(),e);
		}
		lblVersion.setText(AppUtil.getAppVersion());
		lblQueueSize.setText(requestQueueSize());
		recoverLogo();
	}

	@FXML
	void onRelease() {
		btnRelease.setDisable(true);
		btnReprint.setDisable(true);
		CompletableFuture.runAsync(() -> {
			try {
				var url = baseUrl + "/printer/ticket";
				ResponseApi<Double> response = HttpUtil.delete(url);
				Platform.runLater(() -> lblQueueSize.setText(String.valueOf(Math.round(response.getData()))));
			} catch (Exception e) {
				logger.error("Exception on release queue: {}", e.getMessage());
			} finally {
				Platform.runLater(() -> {
					btnRelease.setDisable(false);
					btnReprint.setDisable(false);
				});
			}
		});
	}

	@FXML
	void onReprint() {
		btnRelease.setDisable(true);
		btnReprint.setDisable(true);
		CompletableFuture.runAsync(() -> {
			try {
				var url = baseUrl + "/printer/ticket/reprint";
				ResponseApi<Double> response = HttpUtil.get(url);
				Platform.runLater(() -> lblQueueSize.setText(String.valueOf(Math.round(response.getData()))));
			} catch (Exception e) {
				logger.error("Exception on reprint queue: {}", e.getMessage());
			} finally {
				Platform.runLater(() -> {
					btnRelease.setDisable(false);
					btnReprint.setDisable(false);
				});
			}
		});
	}

	@FXML
	void onClickLabelVersion() {
		Platform.runLater(() -> {
			try {
				FxUtil.newStage(Constants.PASSWORD_FXML, "password").show();
				getStage().close();
			} catch (Exception e) {
				logger.error("Exception on click label version. {}", e.getMessage());
			}
		});
	}

	@FXML
	void onTestPrint() {
		Platform.runLater(() -> {
			try {
				FxUtil.newStage(Constants.TEST_PANEL_FXML, "Pruebas de impresión").show();
				getStage().close();
			} catch (Exception e) {
				logger.error("Exception on show test print: {}", e.getMessage(), e);
			}
		});
	}

	@FXML
	void onHideWindow() {
		this.getStage().close();
	}

	@FXML
	void onMouseEnteredWindow() {
		if (container != null)
			return;
		try {
			container = ContainerProvider.getWebSocketContainer();
			String uri = String.format("ws://%s:%d/printer/ticket/queue/events", posConfig.getIp(), posConfig.getPort());
			WebSocketClient client = new WebSocketClient("queue-events");
			Session session = container.connectToServer(client, URI.create(uri));
			client.setOnMessage(message -> Platform.runLater(() -> lblQueueSize.setText(message)));
			Runnable closeSession = () -> {
				try {
					session.close();
				} catch (IOException ignored) {
				}
			};
			getStage().setOnCloseRequest((event) -> closeSession.run());
			Runtime.getRuntime().addShutdownHook(new Thread(closeSession));
		} catch (Exception e) {
			logger.error("Exception on connectQueueEvents: {}", e.getMessage());
		}
	}

	private String requestQueueSize() {
		try {
			var url = baseUrl + "/printer/ticket/queue";
			ResponseApi<Double> response = HttpUtil.get(url);
			return String.valueOf(Math.round(response.getData()));
		} catch (Exception e) {
			logger.error("Exception at request queue size: {}", e.getLocalizedMessage());
			return "0";
		}
	}

	private void recoverLogo() {
		try {
			var userConfig = JsonUtil.convertFromJson(AppUtil.getUserConfigFileDir(), UserConfig.class);
			if (userConfig.isPresent()) {
				File logoFile = new File(userConfig.get().getLogoPath());
				if (logoFile.exists()) {
					String imgUrl = logoFile.toURI().toURL().toString();
					imgViewLogo.setImage(new Image(imgUrl));
				}
			}
		} catch (Exception e) {
			logger.error("Exception on recover logo: {}", e.getMessage(), e);
		}
	}

	private Stage getStage() {
		return (Stage) root.getScene().getWindow();
	}

	@FXML
	private ImageView imgViewLogo;
	@FXML
	private Label lblQueueSize;
	@FXML
	private Label lblVersion;
	@FXML
	private GridPane root;
	@FXML
	private Button btnRelease;
	@FXML
	private Button btnReprint;
}