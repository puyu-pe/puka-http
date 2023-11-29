package pe.puyu.sweetprinterpos.views;

import ch.qos.logback.classic.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import pe.puyu.sweetprinterpos.Constants;
import pe.puyu.sweetprinterpos.model.PosConfig;
import pe.puyu.sweetprinterpos.util.AppUtil;
import pe.puyu.sweetprinterpos.util.FxUtil;
import pe.puyu.sweetprinterpos.util.JsonUtil;
import pe.puyu.sweetprinterpos.validations.PosConfigValidator;

public class PasswordController {

	@FXML
	private PasswordField lblPassword;

	private final Logger logger = (Logger) LoggerFactory.getLogger(AppUtil.makeNamespaceLogs("PasswordController"));

	@FXML
	void onKeyPressed(KeyEvent event) {
		if(event.getCode() != KeyCode.ENTER)
				return ;
		event.consume();
		try {
			var password = lblPassword.getText();
			var posConfig = JsonUtil.convertFromJson(AppUtil.getPosConfigFileDir(), PosConfig.class);
			var errors = PosConfigValidator.validatePassword(password);
			if (posConfig.isPresent()) {
				var originPassword = posConfig.get().getPassword();
				if (errors.isEmpty() && (password.equals(originPassword))) {
					FxUtil.newStage(Constants.ADMIN_PANEL_FXML, "Panel de administrador").show();
					getStage().close();
				}
			}
		} catch (Exception e) {
			logger.error("Exception when entering password :) {}", e.getMessage(), e);
		}
	}

	private Stage getStage() {
		return (Stage) root.getScene().getWindow();
	}

	@FXML
	private HBox root;
}
