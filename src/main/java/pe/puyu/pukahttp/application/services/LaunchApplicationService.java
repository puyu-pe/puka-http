package pe.puyu.pukahttp.application.services;

import pe.puyu.pukahttp.domain.PrintServerException;
import pe.puyu.pukahttp.domain.ServerConfigException;
import pe.puyu.pukahttp.domain.DataValidationException;
import pe.puyu.pukahttp.domain.ViewLauncher;

public class LaunchApplicationService {
    private final PrintServerService printServerService;
    private final ViewLauncher viewLauncher;

    public LaunchApplicationService(PrintServerService printServerService, ViewLauncher viewLauncher){
        this.printServerService = printServerService;
        this.viewLauncher = viewLauncher;
    }

    public void startApplication() throws ServerConfigException, DataValidationException, PrintServerException {
        if (printServerService.existServerConfig()) {
            printServerService.start();
        } else {
            viewLauncher.launchMainView();
        }
    }
}
