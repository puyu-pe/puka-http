package pe.puyu.pukahttp.infrastructure.javafx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import pe.puyu.pukahttp.application.services.printjob.PrintJobService;
import pe.puyu.pukahttp.domain.models.PrinterType;

public class PrintTestController {

    private final PrintJobService printJobService;

    public void initialize() {
        initPrinterType();
    }

    public PrintTestController(PrintJobService printJobService) {
        this.printJobService = printJobService;
    }

    private void initPrinterType(){
        for (PrinterType type : PrinterType.values()) {
            cmbPrinterType.getItems().add(type.toString());
        }
        cmbPrinterType.getSelectionModel().select(0);
    }

    @FXML
    private CheckBox CheckBoxBold;

    @FXML
    private Button btnPrint;

    @FXML
    private Button btnTest;

    @FXML
    private CheckBox checkBoxBgInverted;

    @FXML
    private ComboBox<?> cmbCharCode;

    @FXML
    private ComboBox<?> cmbErrorLevel;

    @FXML
    private ComboBox<?> cmbImageAlign;

    @FXML
    private ComboBox<?> cmbImageScale;

    @FXML
    private ComboBox<?> cmbQrAlign;

    @FXML
    private ComboBox<?> cmbQrScale;

    @FXML
    private ComboBox<?> cmbQrType;

    @FXML
    private ComboBox<?> cmbTextAlign;

    @FXML
    private ComboBox<String> cmbPrinterType;

    @FXML
    private ListView<?> listViewServices;

    @FXML
    private TabPane root;

    @FXML
    private Spinner<?> spnFontHeight;

    @FXML
    private Spinner<?> spnFontWidth;

    @FXML
    private TextField txtBlockWidth;

    @FXML
    private TextField txtImageHeight;

    @FXML
    private TextField txtImageWidth;

    @FXML
    private TextField txtNameSystem;

    @FXML
    private TextField txtNormalize;

    @FXML
    private TextArea txtOutput;

    @FXML
    private TextField txtPort;

    @FXML
    private TextField txtQrHeight;

    @FXML
    private TextField txtQrWidth;

    @FXML
    void onClickBtnPrint(ActionEvent event) {

    }

    @FXML
    void onClickBtnTest(ActionEvent event) {

    }

    @FXML
    void onClickListViewServices(MouseEvent event) {

    }

    @FXML
    void onReloadPrintServices(ActionEvent event) {

    }

}
