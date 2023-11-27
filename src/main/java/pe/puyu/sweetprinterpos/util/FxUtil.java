package pe.puyu.sweetprinterpos.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.util.Objects;

public class FxUtil {

	public static Scene loadScene(String fxmlViewPath) throws Exception {
		Parent root = FXMLLoader.load(Objects.requireNonNull(FxUtil.class.getResource(fxmlViewPath)));
		return new Scene(root);
	}
}
