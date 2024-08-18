package pe.puyu.pukahttp.infrastructure.javafx.controllers;

import javafx.application.Platform;
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
import javafx.stage.Stage;
import pe.puyu.pukahttp.application.services.printjob.PrintJobService;
import pe.puyu.pukahttp.application.services.printjob.output.SystemPrinter;
import pe.puyu.pukahttp.domain.DataValidationException;
import pe.puyu.pukahttp.domain.models.PrintInfo;
import pe.puyu.pukahttp.domain.models.PrinterInfo;
import pe.puyu.pukahttp.domain.models.PrinterType;
import pe.puyu.pukahttp.infrastructure.clipboard.MyClipboard;
import pe.puyu.pukahttp.infrastructure.config.AppConfig;
import pe.puyu.pukahttp.infrastructure.javafx.views.FxToast;
import pe.puyu.pukahttp.infrastructure.smeargle.SmgPrintObject;
import pe.puyu.pukahttp.infrastructure.smeargle.block.*;
import pe.puyu.pukahttp.infrastructure.smeargle.properties.SmgProperties;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PrintTestController {

    private final PrintJobService printJobService;
    private final FxToast toast;

    public PrintTestController(PrintJobService printJobService) {
        this.printJobService = printJobService;
        this.toast = new FxToast(3);
    }

    public void initialize() {
        initPrinterType();
        txtPort.setText("9100");
        initPrintService();
        txtBlockWidth.setText("42");
        initAlign();
        initScale();
        initSize();
        initQrType();
        initQrErrorLevel();
        initBlockEvents();
        initTextOutput();
        reloadPrintServices();
    }


    @FXML
    void onClickBtnTest() {
        SmgMapStyles styles = new SmgMapStyles();
        styles.set(0, SmgStyle.span(2));
        styles.set(1, SmgStyle.span(4));
        SmgTextBlock block = SmgTextBlock.builder().styles(styles).build();
        block.text("-- PUYU - PUKA --", SmgStyle.builder().bold().fill().center().size(2).build());
        block.text("Esta es una prueba de impresión", SmgStyle.builder().fill().center().build());
        block.line();
        block.row("Printer name:", txtPrintServiceName.getText());
        block.row("Port:", txtPort.getText());
        block.row("Printer type:", cmbPrinterType.getSelectionModel().getSelectedItem());
        block.row("Block width:", txtBlockWidth.getText());
        block.line();
        block.text("Gracias, que tenga  un buen día.", SmgStyle.builder().fill().center().build());
        List<SmgBlock> blocks = new LinkedList<>();
        blocks.add(block);
        print(blocks);
    }

    @FXML
    void onClickBtnPrint() {
        List<SmgBlock> blocks = new LinkedList<>();
        if (!textBlock.isDisable()) {
            blocks.add(buildTextBlock());
        }
        if(!imageBlock.isDisable()){
            try {
                blocks.add(buildImageBlock());
            } catch (Exception e) {
                addOutputMessage(e.getMessage());
            }
        }
        if(!qrBlock.isDisable()){
            try {
                blocks.add(buildQrBlock());
            } catch (Exception e) {
                addOutputMessage(e.getMessage());
            }
        }
        print(blocks);
    }

    @FXML
    void onClickListViewServices(MouseEvent event) {
        if (event.getClickCount() == 1) {
            String selectedItem = listViewServices.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                MyClipboard.copy(selectedItem);
                toast.show(getStage(), String.format("Copy \"%s\" to clipboard", selectedItem));
                txtPrintServiceName.setText(selectedItem);
            }
        }
    }

    @FXML
    void onReloadPrintServices() {
        reloadPrintServices();
    }

    private SmgTextBlock buildTextBlock() {
        SmgStyle style = SmgStyle.builder()
            .fontWidth(spnFontWidth.getValue())
            .fontHeight(spnFontHeight.getValue())
            .align(SmgJustify.from(cmbTextAlign.getSelectionModel().getSelectedItem()))
            .bgInverted(checkBoxBgInverted.isSelected())
            .bold(checkBoxBold.isSelected())
            .normalize(checkBoxNormalize.isSelected())
            .pad('*')
            .fill()
            .build();
        return SmgTextBlock.build().text("Prueba de impresión de texto áéíóú.", style);
    }

    private SmgImageBlock buildImageBlock() throws DataValidationException {
        int width;
        int height;
        try{
            width = Integer.parseInt(txtImageWidth.getText());
            height = Integer.parseInt(txtImageHeight.getText());
        }catch (Exception e){
            throw new DataValidationException("Image height and width must be a positive integers.");
        }
        SmgStyle style = SmgStyle.builder()
            .align(SmgJustify.from(cmbImageAlign.getSelectionModel().getSelectedItem()))
            .scale(SmgScale.from(cmbImageScale.getSelectionModel().getSelectedItem()))
            .width(width)
            .height(height)
            .build();
        return SmgImageBlock.builder().imgPath(AppConfig.getLogoFilePath().toString()).style(style).build();
    }

    private SmgQrBlock buildQrBlock() throws DataValidationException {
        int size;
        try{
            size = Integer.parseInt(txtQrSize.getText());
        }catch (Exception e){
            throw new DataValidationException("Qr size must be a positive integer.");
        }
        SmgStyle style = SmgStyle.builder()
            .align(SmgJustify.from(cmbQrAlign.getSelectionModel().getSelectedItem()))
            .scale(SmgScale.from(cmbQrScale.getSelectionModel().getSelectedItem()))
            .width(size)
            .build();
        String data = "20450523381|01|F001|00000006|0|9.00|30/09/2019|6|sdfsdfsdf|";
        return SmgQrBlock.builder()
            .data(data)
            .type(SmgQrType.from(cmbQrType.getSelectionModel().getSelectedItem()))
            .errorLevel(SmgQrErrorLevel.from(cmbErrorLevel.getSelectionModel().getSelectedItem()))
            .style(style)
            .build();
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
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

    private void initTextOutput() {
        txtOutput.setEditable(false);
        txtOutput.setWrapText(true);
        txtOutput.setStyle("-fx-text-fill: #fc8865;-fx-font-family: 'monospace';");
    }

    private void print(List<SmgBlock> blocks) {
        if (blocks.isEmpty()) {
            return;
        }
        btnPrint.setDisable(true);
        btnTest.setDisable(true);
        CompletableFuture.runAsync(() -> {
            try {
                int blockWidth;
                try {
                    blockWidth = Integer.parseInt(txtBlockWidth.getText());
                } catch (Exception ignored) {
                    String message = String.format("blockWidth: %s must be a positive integer.", txtBlockWidth.getText());
                    throw new DataValidationException(message);
                }
                SmgProperties properties = SmgProperties.builder()
                    .blockWidth(blockWidth)
                    .normalize(checkBoxBgInverted.isSelected())
                    .build();
                SmgPrintObject printObject = SmgPrintObject.properties(properties);
                for (SmgBlock block : blocks) {
                    printObject.block(block);
                }
                String data = printObject.toJsonString();
                String printerName = txtPrintServiceName.getText();
                PrinterType type = PrinterType.from(cmbPrinterType.getSelectionModel().getSelectedItem());
                String port = txtPort.getText();
                PrinterInfo printerInfo = new PrinterInfo(printerName, type, port);
                PrintInfo printInfo = new PrintInfo(printerInfo, "1", data);
                printJobService.print(printInfo);
            } catch (Exception e) {
                Platform.runLater(() -> addOutputMessage(e.getMessage()));
            } finally {
                Platform.runLater(() -> {
                    btnTest.setDisable(false);
                    btnPrint.setDisable(false);
                });
            }
        });
    }

    private void addOutputMessage(String message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        message = LocalDateTime.now().format(formatter) + " - " + message + "\n";
        txtOutput.setText(txtOutput.getText() + message);
    }

    private void reloadPrintServices() {
        listViewServices.getItems().clear();
        listViewServices.getItems().addAll(SystemPrinter.getListPrintServicesNames());
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
