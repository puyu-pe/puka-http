package pe.puyu.pukahttp.infrastructure.javafx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class StartConfigController {

    public StartConfigController() {
    }

    public void initialize() {
    }

    @FXML
    void onAccept() {
        //recuperar los datos  dto
        //service.save(dto)
        //controla la excepción
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
