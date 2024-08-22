package pe.puyu.pukahttp.infrastructure.javafx.app;

import javafx.application.Platform;
import pe.puyu.pukahttp.application.notifier.AppNotifier;
import pe.puyu.pukahttp.domain.ViewLauncher;
import pe.puyu.pukahttp.infrastructure.javafx.views.PrintActionsView;
import pe.puyu.pukahttp.infrastructure.javafx.views.StartConfigView;
import pe.puyu.pukahttp.infrastructure.javafx.views.TrayIconView;
import pe.puyu.pukahttp.infrastructure.lock.AppInstance;
import pe.puyu.pukahttp.infrastructure.loggin.AppLog;
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
            AppLog log = new AppLog(PrintActionsView.class);
            notifier.addInfoSubscriber(log.getLogger()::info);
            notifier.addWarnSubscriber(log.getLogger()::warn);
            notifier.addErrorSubscriber(log.getLogger()::error);
            PrintActionsView printActions = new PrintActionsView();
            printActions.minimizeInsteadHide(true);
            printActions.show();
            printActions.minimize();
        }
    }

    @Override
    public void exit() {
        AppInstance.unlock();
        Platform.exit();
        //trayIcon.close
    }

}
