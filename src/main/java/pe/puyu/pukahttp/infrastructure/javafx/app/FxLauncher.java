package pe.puyu.pukahttp.infrastructure.javafx.app;

import pe.puyu.pukahttp.domain.ViewLauncher;
import pe.puyu.pukahttp.infrastructure.javafx.views.PrintActionsView;
import pe.puyu.pukahttp.infrastructure.javafx.views.StartConfigView;

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
        printActions.minimizeInsteadHide(true);
        printActions.show();
    }

}
