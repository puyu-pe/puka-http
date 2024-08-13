package pe.puyu.pukahttp.infrastructure.javafx.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import pe.puyu.pukahttp.application.services.LaunchApplicationService;
import pe.puyu.pukahttp.application.services.printjob.PrintJobException;
import pe.puyu.pukahttp.application.services.printjob.PrintJobService;
import pe.puyu.pukahttp.application.services.printjob.PrintServiceNotFoundException;
import pe.puyu.pukahttp.infrastructure.javafx.views.FxAlert;
import pe.puyu.pukahttp.infrastructure.loggin.AppLog;

import java.net.URL;
import java.util.ResourceBundle;

public class PrintActionsController implements Initializable {

    private final AppLog appLog = new AppLog(PrintActionsController.class);
    private final LaunchApplicationService launchApplicationService;
    private final PrintJobService printJobService;

    public PrintActionsController(
        LaunchApplicationService launchApplicationService,
        PrintJobService printJobService
    ) {
        this.launchApplicationService = launchApplicationService;
        this.printJobService = printJobService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    void onRelease() {
        try {
            printJobService.release();
        } catch (Exception e) {
            appLog.getLogger().error(e.getMessage(), e);
        }
    }

    @FXML
    void onReprint() {
        try {
            printJobService.reprint();
        } catch (PrintJobException | PrintServiceNotFoundException e) {
            appLog.getLogger().warn(e.getMessage());
        } catch (Exception e) {
            appLog.getLogger().error(e.getMessage(), e);
        }
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
