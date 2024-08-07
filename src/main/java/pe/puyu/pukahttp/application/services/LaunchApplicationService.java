package pe.puyu.pukahttp.application.services;

import javafx.application.Platform;
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

    public void startApplication() throws ServerConfigException, ValidationException {
        if (printService.existServerConfig()) {
            printService.start();
            Platform.exit();
        } else {
            viewLauncher.launchMainView();
        }
    }
}
