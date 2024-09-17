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
import pe.puyu.pukahttp.application.services.printjob.designer.SweetTicketDesigner;
import pe.puyu.pukahttp.application.services.printjob.output.MyPrinter;
import pe.puyu.pukahttp.application.services.printjob.output.SystemPrinter;
import pe.puyu.pukahttp.domain.DataValidationException;
import pe.puyu.pukahttp.domain.models.PrintDocument;
import pe.puyu.pukahttp.domain.models.PrinterInfo;
import pe.puyu.pukahttp.domain.models.PrinterType;
import pe.puyu.pukahttp.infrastructure.clipboard.MyClipboard;
import pe.puyu.pukahttp.infrastructure.config.AppConfig;
import pe.puyu.pukahttp.infrastructure.javafx.views.FxToast;
import pe.puyu.pukahttp.infrastructure.smeargle.Smg;
import pe.puyu.pukahttp.infrastructure.smeargle.SmgPrintObject;
import pe.puyu.pukahttp.infrastructure.smeargle.block.*;
import pe.puyu.pukahttp.infrastructure.smeargle.properties.SmgProperties;
import pe.puyu.pukahttp.infrastructure.smeargle.styles.SmgJustify;
import pe.puyu.pukahttp.infrastructure.smeargle.styles.SmgMapStyles;
import pe.puyu.pukahttp.infrastructure.smeargle.styles.SmgScale;
import pe.puyu.pukahttp.infrastructure.smeargle.styles.SmgStyle;

import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class PrintTestController {

    private final FxToast toast;

    public PrintTestController() {
        this.toast = new FxToast(3);
    }

    public void initialize() {
        initPrinterType();
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
        styles.addGlobalStyle(Smg.normalize(checkBoxNormalize.isSelected()))
            .set("title", Smg.title())
            .set("center", Smg.center(0))
            .set("left_20", Smg.left(20))
            .set("line*", Smg.pad('*').bold())
            .set("line-", Smg.pad('-').bold());
        SmgTextBlock test = SmgTextBlock.builder(" ")
            .addCell(SmgCell.build("-- PUYU - PUKA --", "title"))
            .addCell(SmgCell.build("This is a print test.", "center"))
            .addCell(SmgCell.build("", "line*"))
            .addRow(SmgRow.build().addCell("Printer name:", "left_20").addText(txtPrintServiceName.getText()))
            .addRow(SmgRow.build().addCell("Printer type:", "left_20").addText(cmbPrinterType.getSelectionModel().getSelectedItem()))
            .addRow(SmgRow.build().addCell("Block Width (cx):", "left_20").addText(txtBlockWidth.getText()))
            .addCell(SmgCell.build("", "line-"))
            .addCell(SmgCell.build("Thank you, have a nice day.", "center"));
        print(List.of(test), styles);
    }

    @FXML
    void onClickBtnPrint() {
        SmgMapStyles styles = new SmgMapStyles();
        List<SmgBlock> blocks = new LinkedList<>();
        if (!textBlock.isDisable()) {
            SmgStyle textStyle = Smg.fontWidth(spnFontWidth.getValue())
                .fontHeight(spnFontHeight.getValue())
                .bgInverted(checkBoxBgInverted.isSelected())
                .bold(checkBoxBold.isSelected())
                .pad('*')
                .align(SmgJustify.from(cmbTextAlign.getSelectionModel().getSelectedItem()));
            styles.set("text-test-print", textStyle);
            blocks.add(buildTextBlock());
        }
        if (!imageBlock.isDisable()) {
            try {
                try {
                    int width = Integer.parseInt(txtImageWidth.getText());
                    int height = Integer.parseInt(txtImageHeight.getText());
                    SmgStyle imgTestPrintStyle = SmgStyle.builder()
                        .align(SmgJustify.from(cmbImageAlign.getSelectionModel().getSelectedItem()))
                        .scale(SmgScale.from(cmbImageScale.getSelectionModel().getSelectedItem()))
                        .width(width)
                        .height(height);
                    styles.set("img-test-print", imgTestPrintStyle);
                    blocks.add(buildImageBlock());
                } catch (NumberFormatException e) {
                    throw new DataValidationException("Image height and width must be a positive integers.");
                }
            } catch (Exception e) {
                addOutputMessage(e.getMessage());
            }
        }
        if (!qrBlock.isDisable()) {
            try {
                try {
                    int size = Integer.parseInt(txtQrSize.getText());
                    SmgStyle qrStyle = SmgStyle.builder()
                        .align(SmgJustify.from(cmbQrAlign.getSelectionModel().getSelectedItem()))
                        .scale(SmgScale.from(cmbQrScale.getSelectionModel().getSelectedItem()))
                        .width(size);
                    styles.set("qr-test-print", qrStyle);
                    blocks.add(buildQrBlock());
                } catch (NumberFormatException e) {
                    throw new DataValidationException("Qr size must be a positive integer.");
                }
            } catch (Exception e) {
                addOutputMessage(e.getMessage());
            }
        }
        print(blocks, styles);
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
        return SmgTextBlock.builder(" ")
            .addCell(SmgCell.build("Test print of text: áéíóú-ñ@-//", "text-test-print"));
    }

    private SmgImageBlock buildImageBlock() {
        String imagePath = AppConfig.getLogoFilePath().toString();
        File file = new File(imagePath);
        if (!file.exists()) {
            imagePath = Optional.ofNullable(getClass().getResource("/pe/puyu/pukahttp/assets/icon.png"))
                .map(URL::getPath).orElse("");
        }
        return SmgImageBlock.builder()
            .setClass("img-test-print")
            .setPath(imagePath);
    }

    private SmgQrBlock buildQrBlock() {
        String data = "20450523381|01|F001|00000006|0|9.00|30/09/2019|6|sdfsdfsdf|";
        return SmgQrBlock.builder()
            .setClass("qr-test-print")
            .setData(data)
            .setQrType(SmgQrType.from(cmbQrType.getSelectionModel().getSelectedItem()))
            .setCorrectionLevel(SmgQrErrorLevel.from(cmbErrorLevel.getSelectionModel().getSelectedItem()));
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

    private void print(List<SmgBlock> blocks, SmgMapStyles styles) {
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
                SmgProperties properties = SmgProperties.builder().setBlockWidth(blockWidth);
                SmgPrintObject printObject = SmgPrintObject.build()
                    .setStyles(styles)
                    .setProperties(properties);
                for (SmgBlock block : blocks) {
                    printObject.addBlock(block);
                }
                String data = printObject.toJson();
                String printerName = txtPrintServiceName.getText();
                PrinterType type = PrinterType.from(cmbPrinterType.getSelectionModel().getSelectedItem());
                PrinterInfo printerInfo = new PrinterInfo(printerName, type);
                PrintDocument document = new PrintDocument(printerInfo, data, 1);
                MyPrinter printer = MyPrinter.from(printerInfo);
                SweetTicketDesigner designer = new SweetTicketDesigner();
                printer.print(designer.design(document.jsonData(), document.times()));
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
