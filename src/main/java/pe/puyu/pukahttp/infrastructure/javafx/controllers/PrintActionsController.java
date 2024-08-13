package pe.puyu.pukahttp.infrastructure.javafx.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import pe.puyu.pukahttp.application.services.LaunchApplicationService;
import pe.puyu.pukahttp.application.services.UuidGeneratorService;
import pe.puyu.pukahttp.application.services.printjob.PrintJobException;
import pe.puyu.pukahttp.application.services.printjob.PrintJobService;
import pe.puyu.pukahttp.application.services.printjob.PrintServiceNotFoundException;
import pe.puyu.pukahttp.domain.PrintQueueObservable;
import pe.puyu.pukahttp.infrastructure.javafx.views.FxAlert;
import pe.puyu.pukahttp.infrastructure.loggin.AppLog;

import java.util.concurrent.CompletableFuture;


public class PrintActionsController {

    private final AppLog log = new AppLog(PrintActionsController.class);
    private final LaunchApplicationService launchApplicationService;
    private final PrintJobService printJobService;
    private final PrintQueueObservable printQueueObservable;

    public PrintActionsController(
        LaunchApplicationService launchApplicationService,
        PrintJobService printJobService,
        PrintQueueObservable printQueueObservable
    ) {
        this.launchApplicationService = launchApplicationService;
        this.printJobService = printJobService;
        this.printQueueObservable = printQueueObservable;
    }

    public void initialize() {
        this.lblQueueSize.setText(String.valueOf(printQueueObservable.getQueueSize()));
        printQueueObservable.addObserver(UuidGeneratorService.random(), (queueSize) -> {
            try {
                Platform.runLater(() -> this.lblQueueSize.setText(queueSize.toString()));
            } catch (Exception e) {
                log.getLogger().error(e.getMessage(), e);
            }
        });
    }

    @FXML
    void onRelease() {
        btnRelease.setDisable(true);
        btnReprint.setDisable(true);
        CompletableFuture.runAsync(() -> {
            try {
                printJobService.release();
            } catch (Exception e) {
                log.getLogger().error(e.getMessage(), e);
            } finally {
                btnRelease.setDisable(false);
                btnReprint.setDisable(false);
            }
        });
    }

    @FXML
    void onReprint() {
        btnRelease.setDisable(true);
        btnReprint.setDisable(true);
        CompletableFuture.runAsync(() -> {
            try {
                printJobService.reprint();
            } catch (PrintJobException | PrintServiceNotFoundException e) {
                log.getLogger().warn(e.getMessage());
            } catch (Exception e) {
                log.getLogger().error(e.getMessage(), e);
            } finally {
                btnRelease.setDisable(false);
                btnReprint.setDisable(false);
            }
        });

    }

    @FXML
    void onClickLabelVersion() {

    }

    @FXML
    void onTestPrint() {

    }

    @FXML
    void onCloseService() {
        boolean response = FxAlert.showConfirmation("Are you sure you want to leave?", "The print service will stop !!!");
        if (response) {
            launchApplicationService.stopApplication();
        }
    }

    @FXML
    void onMouseEnteredWindow() {

    }

    @FXML
    private ImageView imgViewLogo;

    @FXML
    private Label lblQueueSize;

    @FXML
    private Label lblVersion;

    @FXML
    private GridPane root;

    @FXML
    private Button btnRelease;

    @FXML
    private Button btnReprint;

}
