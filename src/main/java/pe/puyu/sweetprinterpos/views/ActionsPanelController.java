package pe.puyu.sweetprinterpos.views;

import ch.qos.logback.classic.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import pe.puyu.sweetprinterpos.Constants;
import pe.puyu.sweetprinterpos.model.UserConfig;
import pe.puyu.sweetprinterpos.util.AppUtil;
import pe.puyu.sweetprinterpos.util.FxUtil;
import pe.puyu.sweetprinterpos.util.JsonUtil;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ActionsPanelController implements Initializable {
	private final Logger logger = (Logger) LoggerFactory.getLogger(AppUtil.makeNamespaceLogs("ActionsPanelController"));

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		recoverLogo();
	}

	@FXML
	void onRelease() {

	}

	@FXML
	void onReprint() {

	}

	@FXML
	void onClickLabelVersion() {
		try {
			FxUtil.newStage(Constants.PASSWORD_FXML, "password").show();
			getStage().close();
		} catch (Exception e) {
			logger.error("Exception on click label version. {}", e.getMessage());
		}
	}

	@FXML
	void onTestPrint() {
		try {
			FxUtil.newStage(Constants.TEST_PANEL_FXML, "Pruebas de impresión").show();
			getStage().close();
		} catch (Exception e) {
			logger.error("Exception on show test print: {}", e.getMessage(), e);
		}
	}

	@FXML
	void onHideWindow() {
		this.getStage().close();
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

}