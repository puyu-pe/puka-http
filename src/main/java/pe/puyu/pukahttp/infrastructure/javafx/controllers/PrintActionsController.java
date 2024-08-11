package pe.puyu.pukahttp.infrastructure.javafx.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class PrintActionsController implements Initializable {
    private String data = "";

    public void _init(String data) {
        this.data = data;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    void onRelease() {

    }

    @FXML
    void onReprint() {

    }

    @FXML
    void onClickLabelVersion() {

    }

    @FXML
    void onTestPrint() {

    }

    @FXML
    void onCloseService() {

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
