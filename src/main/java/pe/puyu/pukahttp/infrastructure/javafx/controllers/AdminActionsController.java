package pe.puyu.pukahttp.infrastructure.javafx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import pe.puyu.pukahttp.application.services.PrintServerService;

public class AdminActionsController {
    private final PrintServerService printServerService;

    public AdminActionsController(PrintServerService printServerService){
        this.printServerService = printServerService;
    }

    public void onStop(ActionEvent actionEvent) {
    }

    public void onStart(ActionEvent actionEvent) {

    }

    public void onConfiguration(ActionEvent actionEvent) {

    }

    public void onLogs(ActionEvent actionEvent) {

    }

    @FXML
    private Button btnStart;

    @FXML
    private Button btnStop;

    @FXML
    private ComboBox<String> cmbLevelLogs;

    @FXML
    private GridPane root;

    @FXML
    private TextField txtIp;

    @FXML
    private TextField txtPort;

    @FXML
    private Label lblError;
}
