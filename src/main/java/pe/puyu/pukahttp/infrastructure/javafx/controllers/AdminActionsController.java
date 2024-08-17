package pe.puyu.pukahttp.infrastructure.javafx.controllers;

import javafx.application.Platform;
import javafx.beans.Observable;
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
import pe.puyu.pukahttp.infrastructure.javafx.views.FxAlert;
import pe.puyu.pukahttp.infrastructure.javafx.views.FxToast;
import pe.puyu.pukahttp.infrastructure.loggin.AppLog;
import pe.puyu.pukahttp.infrastructure.loggin.LogLevel;
import pe.puyu.pukahttp.infrastructure.properties.AppPropertyKey;
import pe.puyu.pukahttp.infrastructure.properties.ApplicationProperties;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

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
            txtIp.setDisable(printServerService.isRunning());
            txtPort.setDisable(printServerService.isRunning());
        } catch (Exception e) {
            log.getLogger().error(e.getMessage(), e);
        }
    }

    public void onStop() {
        CompletableFuture.runAsync(() -> {
            try {
                printServerService.stop();
                Platform.runLater(() -> {
                    txtIp.setDisable(false);
                    txtPort.setDisable(false);
                    btnStart.setDisable(false);
                    btnStop.setDisable(true);
                });
            } catch (Exception e) {
                log.getLogger().error(e.getMessage(), e);
                lblError.setText(e.getLocalizedMessage());
            }
        });
    }

    public void onStart() {
        ServerConfigDTO serverConfig = new ServerConfigDTO(txtIp.getText(), txtPort.getText());
        try {
            printServerService.saveServerConfig(serverConfig);
        } catch (Exception e) {
            lblError.setText(e.getLocalizedMessage());
            log.getLogger().error(e.getMessage(), e);
        }

        CompletableFuture.runAsync(() -> {
            try {
                printServerService.start();
                Platform.runLater(() -> {
                    txtIp.setDisable(true);
                    txtPort.setDisable(true);
                    btnStart.setDisable(true);
                    btnStop.setDisable(false);
                });
            } catch (Exception e) {
                log.getLogger().error(e.getMessage(), e);
                lblError.setText(e.getLocalizedMessage());
            }
        });
    }

    public void onConfiguration() {
        openInNativeFileExplorer(AppConfig.getUserDataDir());
    }

    public void onLogs() {
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
        log.getLogger().info(String.format("Log level set to %s", selectedLogLevel));
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
        } finally {
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
