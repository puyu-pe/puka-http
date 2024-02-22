package pe.puyu.pukahttp.views;

import ch.qos.logback.classic.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import pe.puyu.pukahttp.Constants;
import pe.puyu.pukahttp.model.PosConfig;
import pe.puyu.pukahttp.util.AppUtil;
import pe.puyu.pukahttp.util.FxUtil;
import pe.puyu.pukahttp.util.JsonUtil;
import pe.puyu.pukahttp.validations.PosConfigValidator;

import java.util.concurrent.CompletableFuture;

public class PasswordController {
	private final Logger logger = (Logger) LoggerFactory.getLogger(AppUtil.makeNamespaceLogs("PasswordController"));

	@FXML
	void onKeyPressed(KeyEvent event) {
		if (event.getCode() != KeyCode.ENTER)
			return;
		event.consume();
		txtPassword.setDisable(true);
		lblError.setText("");
		var password = txtPassword.getText();
		CompletableFuture.runAsync(() -> Platform.runLater(() -> {
			try {
				var posConfig = JsonUtil.convertFromJson(AppUtil.getPosConfigFileDir(), PosConfig.class);
				var errors = PosConfigValidator.validatePassword(password);
				if (posConfig.isPresent()) {
					var originPassword = posConfig.get().getPassword();
					if (errors.isEmpty() && (password.equals(originPassword))) {
						FxUtil.newStage(Constants.ADMIN_PANEL_FXML, "Panel de administrador").show();
						getStage().close();
					}else{
						lblError.setText("password incorrect");
						txtPassword.setText("");
					}
				}
			} catch (Exception e) {
				logger.error("Exception when entering password :) {}", e.getMessage(), e);
			} finally {
				txtPassword.setDisable(false);
			}
		}));
	}

	private Stage getStage() {
		return (Stage) root.getScene().getWindow();
	}

	@FXML
	private VBox root;
	@FXML
	private Label lblError;
	@FXML
	private PasswordField txtPassword;
}
