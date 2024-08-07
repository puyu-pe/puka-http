package pe.puyu.pukahttp.infrastructure.javafx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pe.puyu.pukahttp.application.loggin.AppLog;
import pe.puyu.pukahttp.application.services.PrintService;
import pe.puyu.pukahttp.domain.ServerConfigDTO;
import pe.puyu.pukahttp.domain.ServerConfigException;
import pe.puyu.pukahttp.domain.ValidationException;
import pe.puyu.pukahttp.infrastructure.javafx.views.AlertsView;

public class StartConfigController {
    private final PrintService printService;
    private final AppLog log = new AppLog(StartConfigController.class);

    public StartConfigController(PrintService printService) {
        this.printService = printService;
    }

    public void initialize() {
    }

    @FXML
    void onAccept() {
        try {
            ServerConfigDTO serverConfig = new ServerConfigDTO(txtIP.getText(), txtPort.getText());
            printService.saveServerConfig(serverConfig);
            getStage().close();
        } catch (ServerConfigException e) {
            log.getLogger().warn(e.getMessage());
            AlertsView.showWarning("Failed to save configuration", e.getMessage());
        } catch (ValidationException e) {
            log.getLogger().warn(e.getMessage());
            AlertsView.showWarning("Field values is Invalid.", e.getMessage());
        } catch (Exception e) {
            log.getLogger().error(e.getMessage());
            AlertsView.showError("Unknown error", e.getMessage());
        }
    }

    @FXML
    void onCancel() {

    }

    @FXML
    void onSelectLogo() {

    }

    @FXML
    void onMouseEnteredWindow() {

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
    private PasswordField txtPassword;

    @FXML
    private TextField txtPort;

}
