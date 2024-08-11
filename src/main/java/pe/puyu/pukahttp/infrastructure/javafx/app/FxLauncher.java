package pe.puyu.pukahttp.infrastructure.javafx.app;

import pe.puyu.pukahttp.domain.ViewLauncher;
import pe.puyu.pukahttp.infrastructure.javafx.views.StartConfigView;

public class FxLauncher implements ViewLauncher {

    @Override
    public void launchMainView() {
        StartConfigView view = new StartConfigView();
        view.minimizeInsteadHide(true);
        view.show();
    }
}
