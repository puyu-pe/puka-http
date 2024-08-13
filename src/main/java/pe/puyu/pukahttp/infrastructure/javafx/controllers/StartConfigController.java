package pe.puyu.pukahttp.infrastructure.javafx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pe.puyu.pukahttp.infrastructure.loggin.AppLog;
import pe.puyu.pukahttp.application.services.BusinessLogoService;
import pe.puyu.pukahttp.application.services.PrintServerService;
import pe.puyu.pukahttp.domain.PngFileChooser;
import pe.puyu.pukahttp.domain.ServerConfigDTO;
import pe.puyu.pukahttp.domain.ServerConfigException;
import pe.puyu.pukahttp.domain.DataValidationException;
import pe.puyu.pukahttp.infrastructure.javafx.views.FxAlert;
import pe.puyu.pukahttp.infrastructure.javafx.views.FxPngFileChooser;

import java.io.File;
import java.io.IOException;

public class StartConfigController {
    private final PrintServerService printServerService;
    private final BusinessLogoService businessLogoService;
    private final AppLog log = new AppLog(StartConfigController.class);

    public StartConfigController(PrintServerService printServerService, BusinessLogoService businessLogoService) {
        this.printServerService = printServerService;
        this.businessLogoService = businessLogoService;
    }

    public void initialize() {
        recoverLogo();
    }

    @FXML
    void onAccept() {
        try {
            ServerConfigDTO serverConfig = new ServerConfigDTO(txtIP.getText(), txtPort.getText());
            printServerService.saveServerConfig(serverConfig);
            printServerService.start();
            getStage().close();
        } catch (ServerConfigException e) {
            log.getLogger().warn(e.getMessage());
            FxAlert.showWarning("Failed to save configuration", e.getMessage());
        } catch (DataValidationException e) {
            log.getLogger().warn(e.getMessage());
            FxAlert.showWarning("Field values is Invalid.", e.getMessage());
        } catch (Exception e) {
            log.getLogger().error(e.getMessage());
            FxAlert.showError("Unknown error", e.getMessage());
        }
    }

    @FXML
    void onCancel() {
        boolean shouldLeave = FxAlert.showConfirmation("Confirm Cancel", "Are you sure you want to cancel the configuration?");
        if (shouldLeave) {
            getStage().close();
        }
    }

    @FXML
    void onSelectLogo() {
        try {
            PngFileChooser pngFileChooser = new FxPngFileChooser(getStage());
            File imageFile = pngFileChooser.show();
            if(imageFile != null) {
                businessLogoService.save(imageFile);
                imgViewLogo.setImage(new Image(businessLogoService.getLogoUrl().toString()));
            }
        } catch (IOException e) {
            log.getLogger().warn(e.getMessage());
            FxAlert.showWarning("Failed on save logo", e.getMessage());
        } catch (Exception e) {
            log.getLogger().error(e.getMessage());
            FxAlert.showError("Unknown error on save logo", e.getMessage());
        }
    }

    private void recoverLogo(){
        try {
            imgViewLogo.setImage(new Image(businessLogoService.getLogoUrl().toString()));
        } catch (Exception e) {
            log.getLogger().warn("error on initialize image: {}", e.getMessage(), e);
        }
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }

    @FXML
    private ImageView imgViewLogo;
    @FXML
    private VBox root;
    @FXML
    public HBox imgViewContainer;
    @FXML
    private TextField txtIP;
    @FXML
    private TextField txtPort;

}
