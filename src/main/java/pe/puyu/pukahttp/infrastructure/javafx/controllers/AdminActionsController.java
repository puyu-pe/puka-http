package pe.puyu.pukahttp.infrastructure.javafx.controllers;

import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import pe.puyu.pukahttp.application.services.PrintServerService;
import pe.puyu.pukahttp.domain.ServerConfigDTO;
import pe.puyu.pukahttp.infrastructure.loggin.AppLog;
import pe.puyu.pukahttp.infrastructure.loggin.LogLevel;
import pe.puyu.pukahttp.infrastructure.properties.AppPropertyKey;
import pe.puyu.pukahttp.infrastructure.properties.ApplicationProperties;

import java.util.Arrays;

public class AdminActionsController {
    private final PrintServerService printServerService;
    private final AppLog log = new AppLog(AdminActionsController.class);

    public AdminActionsController(PrintServerService printServerService) {
        this.printServerService = printServerService;
    }

    public void initialize() {
        try {
            initCmbLevelLogs();
            ServerConfigDTO serverConfig = printServerService.getServerConfig();
            txtIp.setText(serverConfig.ip());
            txtPort.setText(serverConfig.port());
        } catch (Exception e) {
            log.getLogger().error(e.getMessage(), e);
        }
    }

    public void onStop(ActionEvent actionEvent) {
    }

    public void onStart(ActionEvent actionEvent) {

    }

    public void onConfiguration(ActionEvent actionEvent) {

    }

    public void onLogs(ActionEvent actionEvent) {

    }

    private void initCmbLevelLogs() {
        cmbLevelLogs.getItems().addAll(Arrays.stream(LogLevel.values()).map(LogLevel::getValue).toList());
        String logLevel = ApplicationProperties.getString(AppPropertyKey.LOG_LEVEL, LogLevel.INFO.getValue());
        cmbLevelLogs.setValue(logLevel);
        cmbLevelLogs.valueProperty().addListener(this::changeLogLevel);
    }

    private void changeLogLevel(Observable observable) {
        String selectedLogLevel = cmbLevelLogs.getSelectionModel().getSelectedItem();
        AppLog.setErrorLevel(LogLevel.fromValue(selectedLogLevel));
        ApplicationProperties.setString(AppPropertyKey.LOG_LEVEL, selectedLogLevel);
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
