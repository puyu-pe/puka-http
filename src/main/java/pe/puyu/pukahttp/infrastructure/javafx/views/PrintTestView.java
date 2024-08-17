package pe.puyu.pukahttp.infrastructure.javafx.views;

import javafx.stage.Stage;

public class PrintTestView extends View {

    public PrintTestView() {
        super("print-test.fxml");
    }

    @Override
    protected void config(Stage stage) {
        stage.setTitle("Print Test");
    }

}
