package pe.puyu.pukahttp.application.services;

import pe.puyu.pukahttp.domain.PrintServerException;
import pe.puyu.pukahttp.domain.ServerConfigException;
import pe.puyu.pukahttp.domain.ValidationException;
import pe.puyu.pukahttp.domain.ViewLauncher;

public class LaunchApplicationService {
    private final PrintService printService;
    private final ViewLauncher viewLauncher;

    public LaunchApplicationService(PrintService printService, ViewLauncher viewLauncher){
        this.printService = printService;
        this.viewLauncher = viewLauncher;
    }

    public void startApplication() throws ServerConfigException, ValidationException, PrintServerException {
        if (printService.existServerConfig()) {
            printService.start();
        } else {
            viewLauncher.launchMainView();
        }
    }
}
