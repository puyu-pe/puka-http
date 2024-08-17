package pe.puyu.pukahttp.infrastructure.javafx.views;

import javafx.stage.Stage;

public class AdminActionsView extends View{

    public AdminActionsView() {
        super("admin-actions.fxml");
    }

    @Override
    protected void config(Stage stage) {
        stage.setTitle("Admin Actions");
        stage.setResizable(false);
    }

}
