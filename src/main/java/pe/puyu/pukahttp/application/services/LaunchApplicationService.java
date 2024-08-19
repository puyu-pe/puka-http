package pe.puyu.pukahttp.application.services;

import pe.puyu.pukahttp.domain.*;

public class LaunchApplicationService {
    private final PrintServerService printServerService;
    private final ViewLauncher viewLauncher;
    private final CleanPrintQueueService cleanPrintQueueService;

    public LaunchApplicationService(
        PrintServerService printServerService,
        ViewLauncher viewLauncher,
        CleanPrintQueueService cleanPrintQueueService
    ) {
        this.printServerService = printServerService;
        this.viewLauncher = viewLauncher;
        this.cleanPrintQueueService = cleanPrintQueueService;
    }

    public void startApplication() throws ServerConfigException, DataValidationException, PrintServerException {
        if (printServerService.existServerConfig()) {
            //Primero se lanza la vista (launchMain) por que se necesita inicializar el Notifier(TrayIcon)
            cleanPrintQueueService.cleanPrintQueue();
            viewLauncher.launchMain();
            printServerService.start();
        } else {
            viewLauncher.launchStartConfig();
        }
    }

    public void stopApplication() {
        printServerService.stop();
        viewLauncher.exit();
        System.exit(0);
    }
}
