package pe.puyu.pukahttp.infrastructure.javafx.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import pe.puyu.pukahttp.application.notifier.AppNotifier;
import pe.puyu.pukahttp.application.services.BusinessLogoService;
import pe.puyu.pukahttp.application.services.CleanPrintQueueService;
import pe.puyu.pukahttp.application.sweetticketdesign.PrintComponentsProviderService;
import pe.puyu.pukahttp.domain.PrintServerException;
import pe.puyu.pukahttp.domain.ViewLauncher;
import pe.puyu.pukahttp.infrastructure.config.AppConfig;
import pe.puyu.pukahttp.infrastructure.javafx.controllers.*;
import pe.puyu.pukahttp.infrastructure.javafx.injection.TrayIconDependencyInjection;
import pe.puyu.pukahttp.infrastructure.javafx.views.AdminActionsView;
import pe.puyu.pukahttp.infrastructure.javafx.views.FxAlert;
import pe.puyu.pukahttp.infrastructure.javalin.controllers.PukaController;
import pe.puyu.pukahttp.infrastructure.lock.AppInstance;
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
    private final GsonFailedPrintJobStorage storage;
    private final AdminActionsView adminActionsView;

    public JavaFXApplication() {
        storage = new GsonFailedPrintJobStorage();
        this.viewLauncher = new FxLauncher(notifier);
        printServerService = new PrintServerService(new JavalinPrintServer(), new ServerPropertiesReader(), notifier);
        launchApplicationService = new LaunchApplicationService(printServerService, viewLauncher, new CleanPrintQueueService(storage));
        this.adminActionsView = new AdminActionsView();
    }

    @Override
    public void init() {
        injectDependenciesIntoControllers();
        Platform.setImplicitExit(true);
    }

    @Override
    public void start(Stage stage) {
        try {
            try {
                launchApplicationService.startApplication();
            } catch (PrintServerException serverException) {
                appLog.getLogger().error("Print server exception on start app: {}", serverException.getMessage());
                boolean response = FxAlert.showConfirmation("Error on run server", "You want to configure server parameters?");
                if (response) {
                    adminActionsView.show();
                } else {
                    throw serverException;
                }
            }
        } catch (Exception startException) {
            appLog.getLogger().error("start application failed: {}", startException.getMessage(), startException);
            AppInstance.unlock();
            launchApplicationService.stopApplication();
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
            BusinessLogoService businessLogoService = new BusinessLogoService(AppConfig.getLogoFilePath());
            FxDependencyInjection.addControllerFactory(StartConfigController.class, () -> new StartConfigController(printServerService, viewLauncher, businessLogoService));
            FxDependencyInjection.addControllerFactory(PrintActionsController.class, () -> {
                PrintJobService printJobService = new PrintJobService(storage, notifier, new PrintComponentsProviderService(businessLogoService));
                return new PrintActionsController(launchApplicationService, printJobService, storage, businessLogoService, adminActionsView);
            });
            FxDependencyInjection.addControllerFactory(AdminActionsController.class, () -> new AdminActionsController(printServerService));
            JavalinDependencyInjection.addControllerFactory(PrintJobController.class, () -> {
                PrintJobService printJobService = new PrintJobService(storage, notifier, new PrintComponentsProviderService(businessLogoService));
                return new PrintJobController(printJobService, storage);
            });
            JavalinDependencyInjection.addControllerFactory(PukaController.class, () -> new PukaController(businessLogoService));
            TrayIconDependencyInjection.registerController(TrayIconController.class, () -> new TrayIconController(launchApplicationService));
            appLog.getLogger().info("build injected controller dependencies  success!!!");
        } catch (Exception e) {
            appLog.getLogger().error("error on injected controller dependencies: {}", e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        AppInstance.requestLock();
        if (AppInstance.gotLock()) {
            _config_global_properties_(); // Important first call config global properties
            _config_app_properties_();
            launch(args);
        } else {
            Platform.exit();
        }
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
