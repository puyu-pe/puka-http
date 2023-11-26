package pe.puyu.sweetprinterpos.views;

import ch.qos.logback.classic.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import pe.puyu.sweetprinterpos.model.UserConfig;

import pe.puyu.sweetprinterpos.util.JsonUtil;
import pe.puyu.sweetprinterpos.util.PukaAlerts;
import pe.puyu.sweetprinterpos.util.PukaUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PosConfigController implements Initializable {
	private final Logger logger = (Logger) LoggerFactory.getLogger("pe.puyu.puka.views.userConfig");
	private final UserConfig userConfig = new UserConfig();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		imgViewLogo.fitWidthProperty().bind(imgViewContainer.widthProperty());
		imgViewLogo.fitHeightProperty().bind(imgViewContainer.heightProperty());
	}

	@FXML
	void onAccept(ActionEvent event) {
		try {
			List<String> errors = new LinkedList<>();
			if (errors.isEmpty()) {
				getStage().close();
			} else {
				PukaAlerts.showWarning("Configuración invalida detectada.", String.join("\n", errors));
			}
		} catch (Exception e) {
			logger.error("Excepción fatal al aceptar los cambios formulario de configuración: {}", e.getMessage(), e);
		}
	}

	@FXML
	void onCancel(ActionEvent event) {
		boolean result = PukaAlerts.showConfirmation("¿Seguro que deseas cancelar la configuración?",
			"No se guardara la configuración y no se iniciara el servicio de bifrost");
		if (result)
			System.exit(0);
	}

	@FXML
	void onSelectLogo(ActionEvent event) {
		try {
			Optional<File> selectFile = PukaUtil.showPngFileChooser(getStage());
			if (selectFile.isPresent()) {
				String imgUrl = selectFile.get().toURI().toURL().toString();
				persistUserLogoPath(selectFile.get());
				imgViewLogo.setImage(new Image(imgUrl));
			}
		} catch (Exception e) {
			logger.error("Excepción al selecionar el logo: {}", e.getMessage(), e);
		}
	}
	@FXML
	void onMouseEnteredWindow(MouseEvent event) {
		recoverUserConfig();
	}

	private void persistUserLogoPath(File logoFile) {
		try {
			Path sourcePath = Path.of(logoFile.toString());
			Path destinationPath = Path.of(PukaUtil.getUserDataDir(), "logo.png");
			Files.copy(sourcePath, destinationPath);
			userConfig.setLogoPath(destinationPath.toString());
			JsonUtil.saveJson(PukaUtil.getUserConfigFileDir(), userConfig);
		} catch (IOException e) {
			logger.error("Excepción al persistir la información en el archivo de configuración del usuario: {}",
				e.getMessage(),
				e);
		}
	}

	private void recoverUserConfig() {
		try {
			var userConfigOpt = JsonUtil.convertFromJson(PukaUtil.getUserConfigFileDir(), UserConfig.class);
			if (userConfigOpt.isPresent()) {
				userConfig.copyFrom(userConfigOpt.get());
				File logoFile = new File(userConfig.getLogoPath());
				if (logoFile.exists()) {
					String imgUrl = logoFile.toURI().toURL().toString();
					imgViewLogo.setImage(new Image(imgUrl));
				}
			}
		} catch (IOException e) {
			logger.error("Excepción al recuperar la configuración del ususario: {}", e.getMessage(), e);
		}
	}

	private Stage getStage() {
		return (Stage) root.getScene().getWindow();
	}
	@FXML
	private ImageView imgViewLogo;
	@FXML
	private VBox root;
	@FXML
	public HBox imgViewContainer;
}
