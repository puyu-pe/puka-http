package pe.puyu.pukahttp.infrastructure.javafx.app;

import javafx.application.Application;
import javafx.stage.Stage;
import pe.puyu.pukahttp.infrastructure.javafx.controllers.StartConfigController;
import pe.puyu.pukahttp.infrastructure.javafx.injection.FxDependencyInjection;
import pe.puyu.pukahttp.infrastructure.javafx.views.StartConfigView;

public class JavaFXApplication extends Application {

    @Override
    public void init() {
        System.out.println("start build dependencies !!!");
        configControllerDependencies();
    }

    @Override
    public void start(Stage stage) {
        try {
            StartConfigView view = new StartConfigView();
            view.minimizeInsteadHide(false);
            view.show();
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

    private void configControllerDependencies() {
        FxDependencyInjection.addControllerFactory(StartConfigController.class, () -> new StartConfigController("hello"));
    }

}
