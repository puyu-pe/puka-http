package pe.puyu.pukahttp.infrastructure.javafx.app;

import javafx.application.Application;
import javafx.stage.Stage;
import pe.puyu.pukahttp.application.loggin.AppLog;
import pe.puyu.pukahttp.infrastructure.javafx.controllers.StartConfigController;
import pe.puyu.pukahttp.infrastructure.javafx.injection.FxDependencyInjection;
import pe.puyu.pukahttp.infrastructure.javafx.views.StartConfigView;

public class JavaFXApplication extends Application {

    private final AppLog appLog = new AppLog(JavaFXApplication.class);

    @Override
    public void init() {
        configControllerDependencies();
    }

    @Override
    public void start(Stage stage) {
        try {
            StartConfigView view = new StartConfigView();
            view.minimizeInsteadHide(false);
            view.show();
        } catch (Exception startException) {
            appLog.getLogger().error("start application failed: {}", startException.getMessage(), startException);
        }

    }

    @Override
    public void stop() {
        try {
            appLog.getLogger().info("stop application success");
        } catch (Exception stopException) {
            appLog.getLogger().error("stop application failed: {}", stopException.getMessage(), stopException);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void configControllerDependencies() {
        FxDependencyInjection.addControllerFactory(StartConfigController.class, () -> new StartConfigController("hello"));
        appLog.getLogger().info("build injected controller dependencies  success!!!");
    }

}
