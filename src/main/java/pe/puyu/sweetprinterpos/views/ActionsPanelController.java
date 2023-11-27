package pe.puyu.sweetprinterpos.views;

import ch.qos.logback.classic.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import pe.puyu.sweetprinterpos.Constants;
import pe.puyu.sweetprinterpos.util.AppUtil;
import pe.puyu.sweetprinterpos.util.FxUtil;

import java.io.IOException;

public class ActionsPanelController {
	private final Logger logger = (Logger) LoggerFactory.getLogger(AppUtil.makeNamespaceLogs("ActionsPanelController"));

	@FXML
	void onRelease() {

	}

	@FXML
	void onReprint() {

	}

	@FXML
	void onTestPrint() {
		try {
			Stage stage = new Stage();
			stage.setScene(FxUtil.loadScene(Constants.TEST_PANEL_FXML));
			stage.setTitle("Ventana de pruebas de impresion");
			stage.show();
		} catch (Exception e) {
			logger.error("Exception on show test print: {}", e.getMessage(), e);
		}
	}

	@FXML
	void onHideWindow() {
		this.getStage().close();
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
}