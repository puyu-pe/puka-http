package pe.puyu.pukahttp.infrastructure.javafx.app;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pe.puyu.pukahttp.infrastructure.javafx.cache.FxCache;

public class JavaFXApplication extends Application {

    @Override
    public void init() {
        System.out.println("start build dependencies !!!");
    }

    @Override
    public void start(Stage stage) {
        try {
            Parent root = FxCache.get("start-config.fxml");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {

        }
    }

    @Override
    public void stop() {
        try {
            System.out.println("stop application");
        } catch (Exception ignored) {

        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
