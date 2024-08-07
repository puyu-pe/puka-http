package pe.puyu.pukahttp.infrastructure.javafx.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import pe.puyu.pukahttp.application.loggin.AppLog;
import pe.puyu.pukahttp.application.services.BusinessLogoService;
import pe.puyu.pukahttp.application.services.LaunchApplicationService;
import pe.puyu.pukahttp.application.services.PrintService;
import pe.puyu.pukahttp.domain.ServerConfigReader;
import pe.puyu.pukahttp.infrastructure.javafx.controllers.StartConfigController;
import pe.puyu.pukahttp.infrastructure.javafx.injection.FxDependencyInjection;
import pe.puyu.pukahttp.infrastructure.javalin.JavalinServer;
import pe.puyu.pukahttp.infrastructure.reader.ServerPropertiesReader;
import pe.puyu.pukahttp.infrastructure.config.AppConfig;

import java.nio.file.Path;

public class JavaFXApplication extends Application {

    private final AppLog appLog = new AppLog(JavaFXApplication.class);
    private final PrintService printService;
    private final LaunchApplicationService launchApplicationService;

    public JavaFXApplication() {
        ServerConfigReader propertiesReader = new ServerPropertiesReader(AppConfig.getPropertiesFilePath("server.ini"));
        printService = new PrintService(new JavalinServer(), propertiesReader);
        launchApplicationService = new LaunchApplicationService(printService, new FxLauncher());
    }

    @Override
    public void init() {
        configControllerDependencies();
        Platform.setImplicitExit(true);
    }

    @Override
    public void start(Stage stage) {
        try {
            launchApplicationService.startApplication();
        } catch (Exception startException) {
            appLog.getLogger().error("start application failed: {}", startException.getMessage(), startException);
            Platform.exit();
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
        FxDependencyInjection.addControllerFactory(StartConfigController.class, () -> {
            Path logoFilePath = AppConfig.getLogoFilePath();
            return new StartConfigController(printService, new BusinessLogoService(logoFilePath));
        });
        appLog.getLogger().info("build injected controller dependencies  success!!!");
    }

}
