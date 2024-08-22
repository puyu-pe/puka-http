package pe.puyu.pukahttp.infrastructure.javafx.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pe.puyu.pukahttp.application.services.BusinessLogoService;
import pe.puyu.pukahttp.application.services.LaunchApplicationService;
import pe.puyu.pukahttp.application.services.UuidGeneratorService;
import pe.puyu.pukahttp.application.services.printjob.PrintJobService;
import pe.puyu.pukahttp.domain.PngFileChooser;
import pe.puyu.pukahttp.domain.PrintQueueObservable;
import pe.puyu.pukahttp.infrastructure.config.AppConfig;
import pe.puyu.pukahttp.infrastructure.javafx.views.*;
import pe.puyu.pukahttp.infrastructure.loggin.AppLog;

import java.io.File;
import java.util.concurrent.CompletableFuture;


public class PrintActionsController {

    private final AppLog log = new AppLog(PrintActionsController.class);
    private final LaunchApplicationService launchApplicationService;
    private final PrintJobService printJobService;
    private final PrintQueueObservable printQueueObservable;
    private final PrintTestView printTestView = new PrintTestView();
    private final AdminActionsView adminActionsView = new AdminActionsView();

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
        this.lblVersion.setText(AppConfig.getAppVersion());
        printQueueObservable.addObserver(UuidGeneratorService.random(), (queueSize) -> {
            try {
                Platform.runLater(() -> {
                    btnRelease.setDisable(queueSize == 0);
                    btnReprint.setDisable(queueSize == 0);
                    this.lblQueueSize.setText(queueSize.toString());
                });
            } catch (Exception e) {
                log.getLogger().error(e.getMessage(), e);
            }
        });
        recoverLogo();
        btnRelease.setDisable(printQueueObservable.getQueueSize() == 0);
        btnReprint.setDisable(printQueueObservable.getQueueSize() == 0);
    }

    @FXML
    void onRelease() {
        boolean response = FxAlert.showConfirmation("Are you sure you want to  the tickets?", "It is an irreversible action.");
        if (response) {
            btnReprint.setDisable(true);
            btnRelease.setDisable(true);
            btnTestPrint.setDisable(true);
            CompletableFuture.runAsync(() -> {
                try {
                    printJobService.release();
                } catch (Exception e) {
                    log.getLogger().error(e.getMessage(), e);
                } finally {
                    btnTestPrint.setDisable(false);
                }
            });
        }
    }

    @FXML
    void onReprint() {
        boolean response = FxAlert.showConfirmation("Are you sure you want to reprint tickets?", "");
        if (response) {
            btnReprint.setDisable(true);
            btnRelease.setDisable(true);
            btnTestPrint.setDisable(true);
            CompletableFuture.runAsync(() -> {
                try {
                    printJobService.reprint();
                } catch (Exception e) {
                    log.getLogger().error(e.getMessage(), e);
                } finally {
                    btnTestPrint.setDisable(false);
                }
            });
        }
    }

    @FXML
    void onClickLabelVersion() {
        showView(adminActionsView);
    }

    @FXML
    void onTestPrint() {
        showView(printTestView);
    }

    @FXML
    void onCloseService() {
        boolean response = FxAlert.showConfirmation("Are you sure you want to leave?", "The print service will stop !!!");
        if (response) {
            launchApplicationService.stopApplication();
        }
    }

    @FXML
    public void onClickImageView(MouseEvent ignored) {
        try {
            PngFileChooser pngFileChooser = new FxPngFileChooser(getStage());
            File imageFile = pngFileChooser.show();
            if (imageFile != null) {
                BusinessLogoService businessLogoService = new BusinessLogoService(AppConfig.getLogoFilePath());
                businessLogoService.save(imageFile);
                boolean response = FxAlert.showConfirmation("Confirmation for update logo.", "Are you sure you want to do this?");
                if (response) {
                    imgViewLogo.setImage(new Image(businessLogoService.getLogoUrl().toString()));
                }
            }
        } catch (Exception e) {
            log.getLogger().error(e.getMessage());
            FxAlert.showError("Failed on update logo", e.getMessage());
        }
    }

    private void showView(View view) {
        boolean isDisableRelease = btnRelease.isDisable();
        boolean isDisableReprint = btnReprint.isDisable();
        btnReprint.setDisable(true);
        btnRelease.setDisable(true);
        btnTestPrint.setDisable(true);
        Platform.runLater(() -> {
            try {
                view.show();
            } catch (Exception e) {
                log.getLogger().error(e.getMessage(), e);
            } finally {
                btnReprint.setDisable(isDisableReprint);
                btnRelease.setDisable(isDisableRelease);
                btnTestPrint.setDisable(false);
            }
        });
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }

    private void recoverLogo() {
        try {
            BusinessLogoService businessLogoService = new BusinessLogoService(AppConfig.getLogoFilePath());
            if (businessLogoService.existLogo()) {
                imgViewLogo.setImage(new Image(businessLogoService.getLogoUrl().toString()));
            }
        } catch (Exception e) {
            log.getLogger().warn("error on initialize image: {}", e.getMessage(), e);
        }
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

    @FXML
    private Button btnTestPrint;
}
