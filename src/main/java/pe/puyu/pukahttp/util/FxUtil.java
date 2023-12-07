package pe.puyu.pukahttp.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class FxUtil {

	public static Scene loadScene(String fxmlViewPath) throws Exception {
		Parent root = FXMLLoader.load(Objects.requireNonNull(FxUtil.class.getResource(fxmlViewPath)));
		return new Scene(root);
	}

	public static Stage newStage(String fxmlViewPath, String title) throws Exception{
		Stage stage = new Stage();
		stage.setScene(FxUtil.loadScene(fxmlViewPath));
		stage.setTitle(title);
		return stage;
	}
}
