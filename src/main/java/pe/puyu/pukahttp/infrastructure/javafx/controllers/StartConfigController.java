package pe.puyu.pukahttp.infrastructure.javafx.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class StartConfigController implements Initializable {
    String message = "";

    public StartConfigController(String message) {
        this.message = message;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtIP.setText(message);
    }

    @FXML
    void onAccept() {

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
