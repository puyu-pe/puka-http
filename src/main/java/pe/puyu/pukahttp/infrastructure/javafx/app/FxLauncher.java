package pe.puyu.pukahttp.infrastructure.javafx.app;

import javafx.application.Platform;
import pe.puyu.pukahttp.application.notifier.AppNotifier;
import pe.puyu.pukahttp.domain.ViewLauncher;
import pe.puyu.pukahttp.infrastructure.javafx.views.PrintActionsView;
import pe.puyu.pukahttp.infrastructure.javafx.views.StartConfigView;
import pe.puyu.pukahttp.infrastructure.javafx.views.TrayIconView;
import pe.puyu.pukahttp.infrastructure.properties.AppPropertyKey;
import pe.puyu.pukahttp.infrastructure.properties.ApplicationProperties;

public class FxLauncher implements ViewLauncher {
    private final AppNotifier notifier;

    public FxLauncher(AppNotifier notifier) {
        this.notifier = notifier;
    }


    @Override
    public void launchStartConfig() {
        StartConfigView view = new StartConfigView();
        view.minimizeInsteadHide(true);
        view.show();
    }

    @Override
    public void launchMain() {
        boolean supportTrayIcon = ApplicationProperties.getBoolean(AppPropertyKey.TRAY_SUPPORT, false);
        if (supportTrayIcon) {
            TrayIconView trayIconView = new TrayIconView();
            notifier.addInfoSubscriber(trayIconView::info);
            notifier.addWarnSubscriber(trayIconView::warn);
            notifier.addErrorSubscriber(trayIconView::error);
            trayIconView.show();
        } else {
            PrintActionsView printActions = new PrintActionsView();
            printActions.minimizeInsteadHide(true);
            printActions.show();
        }
    }

    @Override
    public void exit() {
        Platform.exit();
        //trayIcon.close
    }

}
