package pe.puyu.pukahttp.infrastructure.javafx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class PrintTestController {

    public void onClickBtnTest(ActionEvent actionEvent) {

    }

    public void onClickCheckBoxCharCodeTable(ActionEvent actionEvent) {

    }

    public void onClickBtnPrint(ActionEvent actionEvent) {
    }

    public void onClickListViewServices(MouseEvent mouseEvent) {

    }

    public void onReloadPrintServices(ActionEvent actionEvent) {

    }

    @FXML
    private CheckBox checkBoxCharCodeTable;

    @FXML
    private CheckBox checkBoxInvertedText;

    @FXML
    private CheckBox checkBoxLogo;

    @FXML
    private CheckBox checkBoxNativeQR;

    @FXML
    private CheckBox checkBoxQrCode;

    @FXML
    private ComboBox<String> cmbCharCodeTable;

    @FXML
    private TextField txtNameSystem;

    @FXML
    private ComboBox<String> cmbTypeConnection;

    @FXML
    private ComboBox<String> cmbTypeDocument;

    @FXML
    private TextField txtCharacterPerLine;

    @FXML
    private TextArea txtErrorArea;

    @FXML
    private TextField txtPort;

    @FXML
    private ListView<String> listViewServices;

    @FXML
    private TabPane root;

    @FXML
    private CheckBox checkBoxNormalize;

    @FXML
    private TextField txtFontSizeCommand;

    @FXML
    private Button btnPrint;

    @FXML
    private Button btnTest;

}
