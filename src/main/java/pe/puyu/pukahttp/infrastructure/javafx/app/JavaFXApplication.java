package pe.puyu.pukahttp.infrastructure.javafx.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import pe.puyu.pukahttp.application.loggin.AppLog;
import pe.puyu.pukahttp.application.services.BusinessLogoService;
import pe.puyu.pukahttp.application.services.LaunchApplicationService;
import pe.puyu.pukahttp.application.services.PrintServerService;
import pe.puyu.pukahttp.application.services.printjob.PrintJobService;
import pe.puyu.pukahttp.domain.FailedPrintJobsStorage;
import pe.puyu.pukahttp.domain.ServerConfigReader;
import pe.puyu.pukahttp.infrastructure.javafx.controllers.StartConfigController;
import pe.puyu.pukahttp.infrastructure.javafx.injection.FxDependencyInjection;
import pe.puyu.pukahttp.infrastructure.javalin.controllers.PrintJobController;
import pe.puyu.pukahttp.infrastructure.javalin.injection.JavalinDependencyInjection;
import pe.puyu.pukahttp.infrastructure.javalin.server.JavalinPrintServer;
import pe.puyu.pukahttp.infrastructure.properties.ServerPropertiesReader;
import pe.puyu.pukahttp.infrastructure.config.AppConfig;
import pe.puyu.pukahttp.infrastructure.storage.GsonFailedPrintJobStorage;

import java.nio.file.Path;

public class JavaFXApplication extends Application {

    private final AppLog appLog = new AppLog(JavaFXApplication.class);
    private final PrintServerService printServerService;

    public JavaFXApplication() {
        ServerConfigReader propertiesReader = new ServerPropertiesReader(AppConfig.getPropertiesFilePath("server.ini"));
        printServerService = new PrintServerService(new JavalinPrintServer(), propertiesReader);
    }

    @Override
    public void init() {
        injectDependenciesIntoControllers();
        Platform.setImplicitExit(true);
    }

    @Override
    public void start(Stage stage) {
        try {
            LaunchApplicationService launchApplicationService = new LaunchApplicationService(printServerService, new FxLauncher());
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

    private void injectDependenciesIntoControllers() {
        GsonFailedPrintJobStorage storage = new GsonFailedPrintJobStorage(AppConfig.getStoragePath());
        try {
            FxDependencyInjection.addControllerFactory(StartConfigController.class, () -> {
                Path logoFilePath = AppConfig.getLogoFilePath();
                return new StartConfigController(printServerService, new BusinessLogoService(logoFilePath));
            });
            JavalinDependencyInjection.addControllerFactory(PrintJobController.class, () -> {
                PrintJobService printJobService = new PrintJobService(storage);
                return new PrintJobController(printJobService, storage);
            });
            appLog.getLogger().info("build injected controller dependencies  success!!!");
        } catch (Exception e) {
            appLog.getLogger().error("error on injected controller dependencies: {}", e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
