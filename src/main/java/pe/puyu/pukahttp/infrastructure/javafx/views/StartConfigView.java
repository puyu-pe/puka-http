package pe.puyu.pukahttp.infrastructure.javafx.views;


import javafx.stage.Stage;

public class StartConfigView extends View {

    public StartConfigView() {
        super("start-config.fxml");
    }

    @Override
    protected void config(Stage stage) {
        stage.setTitle("Puka HTTP Start Config");
        stage.setResizable(false);
    }
}
