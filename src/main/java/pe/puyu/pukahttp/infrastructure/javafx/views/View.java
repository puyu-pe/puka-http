package pe.puyu.pukahttp.infrastructure.javafx.views;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pe.puyu.pukahttp.infrastructure.javafx.injection.FxDependencyInjection;

public abstract class View {

    private final Stage _stage;
    private final String _fxmlFileName;

    public View(String fxmlFileName) {
        _stage = new Stage();
        config(_stage);
        _fxmlFileName = fxmlFileName;
    }

    public void show() {
        if (_stage.getScene() == null) {
            Parent root = FxDependencyInjection.load(_fxmlFileName);
            _stage.setScene(new Scene(root));
        }
        _stage.show();
        _stage.setIconified(false);
    }

    public void minimizeInsteadHide(boolean is) {
        if (is) {
            _stage.setOnCloseRequest(event -> {
                _stage.setIconified(true);
                event.consume();
            });
        } else {
            _stage.setOnCloseRequest(event -> {
                _stage.hide();
                event.consume();
            });
        }
    }

    public void minimize() {
        _stage.setIconified(true);
    }

    public void close() {
        _stage.close();
    }

    abstract protected void config(Stage stage);

}
