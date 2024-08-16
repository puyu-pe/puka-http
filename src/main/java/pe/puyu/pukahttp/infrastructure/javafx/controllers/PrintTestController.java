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
import javafx.scene.layout.GridPane;
import pe.puyu.pukahttp.application.services.printjob.PrintJobService;
import pe.puyu.pukahttp.application.services.printjob.output.SystemPrinter;
import pe.puyu.pukahttp.domain.models.PrinterType;
import pe.puyu.pukahttp.infrastructure.smeargle.block.SmgJustify;
import pe.puyu.pukahttp.infrastructure.smeargle.block.SmgQrErrorLevel;
import pe.puyu.pukahttp.infrastructure.smeargle.block.SmgQrType;
import pe.puyu.pukahttp.infrastructure.smeargle.block.SmgScale;

public class PrintTestController {

    private final PrintJobService printJobService;

    public void initialize() {
        initPrinterType();
        txtPort.setText("9100");
        initPrintService();
        txtBlockWidth.setText("42");
        initAlign();
        checkBoxBgInverted.setSelected(true);
        initScale();
        initSize();
        initQrType();
        initQrErrorLevel();
        initBlockEvents();
        txtOutput.setEditable(false);
    }

    public PrintTestController(PrintJobService printJobService) {
        this.printJobService = printJobService;
    }

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

    private void initPrinterType() {
        for (PrinterType type : PrinterType.values()) {
            cmbPrinterType.getItems().add(type.toString());
        }
        cmbPrinterType.getSelectionModel().select(0);
    }

    private void initPrintService() {
        var printServicesName = SystemPrinter.getListPrintServicesNames();
        if (printServicesName.length > 0) {
            txtPrintServiceName.setText(printServicesName[0]);
        }
    }

    private void initAlign() {
        var justifyValues = SmgJustify.values();
        for (SmgJustify justify : justifyValues) {
            cmbTextAlign.getItems().add(justify.toString());
            cmbImageAlign.getItems().add(justify.toString());
            cmbQrAlign.getItems().add(justify.toString());
        }
        if (justifyValues.length > 0) {
            cmbTextAlign.getSelectionModel().select(0);
            cmbImageAlign.getSelectionModel().select(0);
            cmbQrAlign.getSelectionModel().select(0);
        }
    }

    private void initScale() {
        var scales = SmgScale.values();
        for (SmgScale scale : scales) {
            cmbImageScale.getItems().add(scale.toString());
            cmbQrScale.getItems().add(scale.toString());
        }
        if (scales.length > 0) {
            cmbImageScale.getSelectionModel().select(0);
            cmbQrScale.getSelectionModel().select(0);
        }
    }

    private void initSize() {
        txtImageHeight.setText("290");
        txtImageWidth.setText("290");
        txtQrSize.setText("290");
    }

    private void initQrType() {
        var types = SmgQrType.values();
        for (SmgQrType type : types) {
            cmbQrType.getItems().add(type.toString());
        }
        if (types.length > 0) {
            cmbQrType.getSelectionModel().select(0);
        }
    }

    private void initQrErrorLevel() {
        var levels = SmgQrErrorLevel.values();
        for (SmgQrErrorLevel level : levels) {
            cmbErrorLevel.getItems().add(level.toString());
        }
        if (levels.length > 0) {
            cmbErrorLevel.getSelectionModel().select(0);
        }
    }

    private void initBlockEvents() {
        checkBoxTextBlock.selectedProperty().addListener(observer -> textBlock.setDisable(!checkBoxTextBlock.isSelected()));
        checkBoxImageBlock.selectedProperty().addListener(observer -> imageBlock.setDisable(!checkBoxImageBlock.isSelected()));
        checkBoxQrBlock.selectedProperty().addListener(observer -> qrBlock.setDisable(!checkBoxQrBlock.isSelected()));
        checkBoxTextBlock.setSelected(true);
        imageBlock.setDisable(true);
        qrBlock.setDisable(true);
    }


    @FXML
    private CheckBox checkBoxBold;

    @FXML
    private Button btnPrint;

    @FXML
    private Button btnTest;

    @FXML
    private CheckBox checkBoxBgInverted;

    @FXML
    private ComboBox<String> cmbErrorLevel;

    @FXML
    private ComboBox<String> cmbImageAlign;

    @FXML
    private ComboBox<String> cmbImageScale;

    @FXML
    private ComboBox<String> cmbQrAlign;

    @FXML
    private ComboBox<String> cmbQrScale;

    @FXML
    private ComboBox<String> cmbQrType;

    @FXML
    private ComboBox<String> cmbTextAlign;

    @FXML
    private ComboBox<String> cmbPrinterType;

    @FXML
    private ListView<String> listViewServices;

    @FXML
    private TabPane root;

    @FXML
    private Spinner<Integer> spnFontWidth;

    @FXML
    private Spinner<Integer> spnFontHeight;

    @FXML
    private TextField txtBlockWidth;

    @FXML
    private TextField txtImageHeight;

    @FXML
    private TextField txtImageWidth;

    @FXML
    private TextField txtPrintServiceName;

    @FXML
    private CheckBox checkBoxNormalize;

    @FXML
    private TextArea txtOutput;

    @FXML
    private TextField txtPort;

    @FXML
    private TextField txtQrSize;

    @FXML
    private CheckBox checkBoxImageBlock;

    @FXML
    private CheckBox checkBoxQrBlock;

    @FXML
    private CheckBox checkBoxTextBlock;

    @FXML
    private GridPane qrBlock;

    @FXML
    private GridPane textBlock;

    @FXML
    private GridPane imageBlock;
}
