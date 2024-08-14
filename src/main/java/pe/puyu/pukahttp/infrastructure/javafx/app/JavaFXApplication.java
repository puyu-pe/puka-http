package pe.puyu.pukahttp.infrastructure.javafx.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import pe.puyu.pukahttp.application.notifier.AppNotifier;
import pe.puyu.pukahttp.infrastructure.loggin.AppLog;
import pe.puyu.pukahttp.application.services.LaunchApplicationService;
import pe.puyu.pukahttp.application.services.PrintServerService;
import pe.puyu.pukahttp.application.services.printjob.PrintJobService;
import pe.puyu.pukahttp.infrastructure.javafx.controllers.PrintActionsController;
import pe.puyu.pukahttp.infrastructure.javafx.controllers.StartConfigController;
import pe.puyu.pukahttp.infrastructure.javafx.injection.FxDependencyInjection;
import pe.puyu.pukahttp.infrastructure.javalin.controllers.PrintJobController;
import pe.puyu.pukahttp.infrastructure.javalin.injection.JavalinDependencyInjection;
import pe.puyu.pukahttp.infrastructure.javalin.server.JavalinPrintServer;
import pe.puyu.pukahttp.infrastructure.properties.ServerPropertiesReader;
import pe.puyu.pukahttp.infrastructure.storage.GsonFailedPrintJobStorage;

public class JavaFXApplication extends Application {

    private final AppLog appLog = new AppLog(JavaFXApplication.class);
    private final AppNotifier notifier = new AppNotifier();
    private final PrintServerService printServerService;
    private final LaunchApplicationService launchApplicationService;

    public JavaFXApplication() {
        printServerService = new PrintServerService(new JavalinPrintServer(), new ServerPropertiesReader(), notifier);
        launchApplicationService = new LaunchApplicationService(printServerService, new FxLauncher(notifier));
    }

    @Override
    public void init() {
        injectDependenciesIntoControllers();
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

    private void injectDependenciesIntoControllers() {
        try {
            GsonFailedPrintJobStorage storage = new GsonFailedPrintJobStorage();
            FxDependencyInjection.addControllerFactory(StartConfigController.class, () -> new StartConfigController(printServerService));
            FxDependencyInjection.addControllerFactory(PrintActionsController.class, () -> {
                PrintJobService printJobService = new PrintJobService(storage, notifier);
                return new PrintActionsController(launchApplicationService, printJobService, storage);
            });
            JavalinDependencyInjection.addControllerFactory(PrintJobController.class, () -> {
                PrintJobService printJobService = new PrintJobService(storage, notifier);
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
