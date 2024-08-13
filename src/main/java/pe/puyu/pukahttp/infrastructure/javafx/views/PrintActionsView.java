package pe.puyu.pukahttp.infrastructure.javafx.views;

import javafx.stage.Stage;

public class PrintActionsView extends View {

    public PrintActionsView() {
        super("print-actions.fxml");
    }

    @Override
    protected void config(Stage stage) {
        stage.setTitle("Print Actions");
        stage.setResizable(false);
    }
}
