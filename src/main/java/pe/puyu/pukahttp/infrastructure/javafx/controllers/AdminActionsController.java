package pe.puyu.pukahttp.infrastructure.javafx.controllers;

import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pe.puyu.pukahttp.application.services.PrintServerService;
import pe.puyu.pukahttp.domain.ServerConfigDTO;
import pe.puyu.pukahttp.infrastructure.clipboard.MyClipboard;
import pe.puyu.pukahttp.infrastructure.config.AppConfig;
import pe.puyu.pukahttp.infrastructure.javafx.views.FxToast;
import pe.puyu.pukahttp.infrastructure.loggin.AppLog;
import pe.puyu.pukahttp.infrastructure.loggin.LogLevel;
import pe.puyu.pukahttp.infrastructure.properties.AppPropertyKey;
import pe.puyu.pukahttp.infrastructure.properties.ApplicationProperties;

import java.awt.*;
import java.io.File;
import java.util.Arrays;

public class AdminActionsController {
    private final PrintServerService printServerService;
    private final AppLog log = new AppLog(AdminActionsController.class);
    private final FxToast toast;

    public AdminActionsController(PrintServerService printServerService) {
        this.printServerService = printServerService;
        this.toast = new FxToast(5);
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
        openInNativeFileExplorer(AppConfig.getUserDataDir());
    }

    public void onLogs(ActionEvent actionEvent) {
        openInNativeFileExplorer(AppConfig.getLogsDirectory());
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

    public void openInNativeFileExplorer(String directory) {
        try {
            File dirToOpen = new File(directory);
            String os = System.getProperty("os.name").toLowerCase();
            Runtime runtime = Runtime.getRuntime();
            if (os.contains("win")) {
                runtime.exec("explorer.exe " + dirToOpen.getAbsolutePath());
            } else if (os.contains("nix") || os.contains("nux")) {
                runtime.exec("xdg-open " + dirToOpen.getAbsolutePath());
            } else if (os.contains("mac")) {
                runtime.exec("open " + dirToOpen.getAbsolutePath());
            }
        } catch (Exception e) {
            log.getLogger().error(e.getMessage());
        }finally {
            MyClipboard.copy(directory);
            this.toast.show(getStage(), String.format("Copy \"%s\" to clipboard", directory));
        }
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
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
