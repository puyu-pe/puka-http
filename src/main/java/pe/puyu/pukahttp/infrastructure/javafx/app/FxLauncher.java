package pe.puyu.pukahttp.infrastructure.javafx.app;

import javafx.application.Platform;
import pe.puyu.pukahttp.domain.ViewLauncher;
import pe.puyu.pukahttp.infrastructure.javafx.views.PrintActionsView;
import pe.puyu.pukahttp.infrastructure.javafx.views.StartConfigView;
import pe.puyu.pukahttp.infrastructure.properties.AppPropertyKey;
import pe.puyu.pukahttp.infrastructure.properties.ApplicationProperties;

public class FxLauncher implements ViewLauncher {

    @Override
    public void launchStartConfig() {
        StartConfigView view = new StartConfigView();
        view.minimizeInsteadHide(true);
        view.show();
    }

    @Override
    public void launchMain() {
        PrintActionsView printActions = new PrintActionsView();
        printActions.minimizeInsteadHide(ApplicationProperties.getBoolean(AppPropertyKey.TRAY_SUPPORT, false));
        printActions.show();
    }

    @Override
    public void exit() {
        Platform.exit();
        //trayIcon.close
    }

}
