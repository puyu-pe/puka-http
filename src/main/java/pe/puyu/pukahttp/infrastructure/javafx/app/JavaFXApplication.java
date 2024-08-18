package pe.puyu.pukahttp.infrastructure.javafx.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import pe.puyu.pukahttp.application.notifier.AppNotifier;
import pe.puyu.pukahttp.domain.ViewLauncher;
import pe.puyu.pukahttp.infrastructure.config.AppConfig;
import pe.puyu.pukahttp.infrastructure.javafx.controllers.*;
import pe.puyu.pukahttp.infrastructure.javafx.injection.TrayIconDependencyInjection;
import pe.puyu.pukahttp.infrastructure.loggin.AppLog;
import pe.puyu.pukahttp.application.services.LaunchApplicationService;
import pe.puyu.pukahttp.application.services.PrintServerService;
import pe.puyu.pukahttp.application.services.printjob.PrintJobService;
import pe.puyu.pukahttp.infrastructure.javafx.injection.FxDependencyInjection;
import pe.puyu.pukahttp.infrastructure.javalin.controllers.PrintJobController;
import pe.puyu.pukahttp.infrastructure.javalin.injection.JavalinDependencyInjection;
import pe.puyu.pukahttp.infrastructure.javalin.server.JavalinPrintServer;
import pe.puyu.pukahttp.infrastructure.loggin.LogLevel;
import pe.puyu.pukahttp.infrastructure.properties.AppPropertyKey;
import pe.puyu.pukahttp.infrastructure.properties.ApplicationProperties;
import pe.puyu.pukahttp.infrastructure.properties.ServerPropertiesReader;
import pe.puyu.pukahttp.infrastructure.storage.GsonFailedPrintJobStorage;

public class JavaFXApplication extends Application {

    private final AppLog appLog = new AppLog(JavaFXApplication.class);
    private final AppNotifier notifier = new AppNotifier();
    private final PrintServerService printServerService;
    private final LaunchApplicationService launchApplicationService;
    private final ViewLauncher viewLauncher;

    public JavaFXApplication() {
        this.viewLauncher = new FxLauncher(notifier);
        printServerService = new PrintServerService(new JavalinPrintServer(), new ServerPropertiesReader(), notifier);
        launchApplicationService = new LaunchApplicationService(printServerService, viewLauncher);
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
            FxDependencyInjection.addControllerFactory(StartConfigController.class, () -> new StartConfigController(printServerService, viewLauncher));
            FxDependencyInjection.addControllerFactory(PrintActionsController.class, () -> {
                PrintJobService printJobService = new PrintJobService(storage, notifier);
                return new PrintActionsController(launchApplicationService, printJobService, storage);
            });
            FxDependencyInjection.addControllerFactory(PrintTestController.class, () -> {
                PrintJobService printJobService = new PrintJobService(storage, notifier);
                return new PrintTestController(printJobService);
            });
            FxDependencyInjection.addControllerFactory(AdminActionsController.class, () -> new AdminActionsController(printServerService));
            JavalinDependencyInjection.addControllerFactory(PrintJobController.class, () -> {
                PrintJobService printJobService = new PrintJobService(storage, notifier);
                return new PrintJobController(printJobService, storage);
            });
            TrayIconDependencyInjection.registerController(TrayIconController.class, () -> new TrayIconController(launchApplicationService));
            appLog.getLogger().info("build injected controller dependencies  success!!!");
        } catch (Exception e) {
            appLog.getLogger().error("error on injected controller dependencies: {}", e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        _config_global_properties_(); // Important config first global properties
        _config_app_properties_();
        launch(args);
    }

    private static void _config_global_properties_() {
        System.setProperty("logs.directory", AppConfig.getLogsDirectory());
        System.setProperty("app.env", AppConfig.getEnv());
    }

    private static void _config_app_properties_() {
        // config App Log
        AppLog.setErrorLevel(LogLevel.fromValue(ApplicationProperties.getString(AppPropertyKey.LOG_LEVEL, LogLevel.INFO.getValue())));
        // config support TrayIcon
        if (!ApplicationProperties.has(AppPropertyKey.TRAY_SUPPORT)) {
            String os = System.getProperty("os.name").toLowerCase();
            ApplicationProperties.setBoolean(AppPropertyKey.TRAY_SUPPORT, os.contains("win") || os.contains("mac"));
        }
        if (!ApplicationProperties.has(AppPropertyKey.TRAY_NOTIFICATIONS)) {
            ApplicationProperties.setBoolean(AppPropertyKey.TRAY_NOTIFICATIONS, true);
        }
        //...
    }
}
