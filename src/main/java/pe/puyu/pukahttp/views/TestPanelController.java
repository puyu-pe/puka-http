package pe.puyu.pukahttp.views;

import ch.qos.logback.classic.Logger;
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPos.CharacterCodeTable;
import com.github.anastaciocintra.escpos.EscPos.CutMode;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import pe.puyu.jticketdesing.util.escpos.EscPosWrapper;
import pe.puyu.jticketdesing.util.escpos.JustifyAlign;
import pe.puyu.jticketdesing.util.escpos.StyleText;
import pe.puyu.pukahttp.model.PrinterConnection;
import pe.puyu.pukahttp.model.TicketInfo;
import pe.puyu.pukahttp.services.printer.Printer;
import pe.puyu.pukahttp.services.printer.SweetTicketPrinter;
import pe.puyu.pukahttp.services.printingtest.PrintTestService;
import pe.puyu.pukahttp.util.JsonUtil;
import pe.puyu.pukahttp.util.AppUtil;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class TestPanelController implements Initializable {
	private final Logger logger = (Logger) LoggerFactory.getLogger(AppUtil.makeNamespaceLogs("TestPanelController"));
	private final TicketInfo ticketInfo = new TicketInfo();
	private final PrinterConnection printerConnection = new PrinterConnection();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ticketInfo.widthProperty().bind(Bindings.createIntegerBinding(() -> {
			try {
				return Integer.parseInt(txtCharacterPerLine.getText());
			} catch (Exception e) {
				return 42;
			}
		}, txtCharacterPerLine.textProperty()));

		ticketInfo.fontSizeCommandProperty().bind(Bindings.createIntegerBinding(() -> {
			try {
				return Integer.parseInt(txtFontSizeCommand.getText());
			} catch (Exception e) {
				return 2;
			}
		}, txtFontSizeCommand.textProperty()));

		printerConnection.portProperty().bind(Bindings.createIntegerBinding(() -> {
			try {
				return Integer.parseInt(txtPort.getText());
			} catch (Exception e) {
				return 9100;
			}
		}, txtPort.textProperty()));

		ticketInfo.charCodeTableProperty().bind(Bindings.createStringBinding(() -> {
			if (checkBoxCharCodeTable.isSelected())
				return cmbCharCodeTable.getValue();
			return null;
		}, cmbCharCodeTable.valueProperty(), checkBoxCharCodeTable.selectedProperty()));

		ticketInfo.nativeQRProperty().bind(checkBoxNativeQR.selectedProperty());
		ticketInfo.backgroundInvertedProperty().bind(checkBoxInvertedText.selectedProperty());
		ticketInfo.textNormalizeProperty().bind(checkBoxNormalize.selectedProperty());
		printerConnection.name_systemProperty().bind(txtNameSystem.textProperty());
		printerConnection.typeProperty().bind(cmbTypeConnection.valueProperty());
		reloadPrintServices();
		initTypesConnectionList();
		initCharCodeTableList();
		initTypeDocumentList();
		initDefaultValues();
	}

	@FXML
	void onClickBtnTest() {
		btnTest.setDisable(true);
		btnPrint.setDisable(true);
		CompletableFuture.runAsync(() -> {
			var name_system = printerConnection.getName_system();
			var port = printerConnection.getPort();
			var type = printerConnection.getType();
			try (var outputStream = Printer.getOutputStreamFor(name_system, port, type)) {
				Printer.setOnUncaughtExceptionFor(outputStream, (t, e) -> showMessageAreaError(e.getMessage(), "error"));
				var buffer = new ByteArrayOutputStream();
				var escpos = new EscPos(buffer);
				var width = ticketInfo.getWidth();
				escpos.setCharacterCodeTable(CharacterCodeTable.WPC1252);
				var escposWrapper = new EscPosWrapper(escpos);
				StyleText titleStyle = StyleText.builder()
					.align(JustifyAlign.CENTER)
					.bold(true)
					.fontSize(2)
					.build();
				StyleText subtitleStyle = StyleText.builder()
					.bold(true)
					.align(JustifyAlign.CENTER)
					.build();
				StyleText sayingStyle = StyleText.builder().align(JustifyAlign.CENTER).build();
				escposWrapper.printText("-- PUYU - PUKA --", width, titleStyle);
				escposWrapper.printText("Esta es una prueba de impresión", width, subtitleStyle);
				escposWrapper.printLine('-', width);
				escposWrapper.printText(String.format("name_system: %s", name_system), width);
				escposWrapper.printText(String.format("port:        %s", port), width);
				escposWrapper.printText(String.format("type:        %s", type), width);
				escposWrapper.printText(String.format("width:       %s", width), width);
				escposWrapper.printLine('-', width);
				escposWrapper.printText("Gracias, que tenga un buen dia.", width, sayingStyle);
				escpos.feed(4);
				escpos.cut(CutMode.PART);
				outputStream.write(buffer.toByteArray());
				Platform.runLater(() -> showMessageAreaError("La prueba no lanzo ninguna excepcion.", "info"));
			} catch (Exception e) {
				Platform.runLater(() -> showMessageAreaError(String.format("Fallo la prueba: %s", e.getMessage()), "error"));
				logger.error("Ocurrio una excepcion al realizar el test basico: {}", e.getMessage(), e);
			} finally {
				Platform.runLater(() -> {
					btnPrint.setDisable(false);
					btnTest.setDisable(false);
				});
			}
		});
	}

	@FXML
	void onClickCheckBoxCharCodeTable() {
		cmbCharCodeTable.setDisable(!checkBoxCharCodeTable.isSelected());
	}

	@FXML
	void onClickBtnPrint() {
		btnTest.setDisable(true);
		btnPrint.setDisable(true);
		CompletableFuture.runAsync(() -> {
			try {
				var ticket = PrintTestService.getTicketByTypeDocument(cmbTypeDocument.getValue());
				var printer = JsonUtil.toJSONObject(printerConnection);
				var properties = JsonUtil.toJSONObject(ticketInfo);
				ticket.add("printer", printer);
				ticket.getAsJsonObject("printer").add("properties", properties);
				if (checkBoxLogo.isSelected()) {
					PrintTestService.addLogoToTicket(ticket);
				}
				if (!checkBoxQrCode.isSelected()) {
					PrintTestService.removeQRToTicket(ticket);
				}
				var sweetTicketPrinter = new SweetTicketPrinter(ticket);
				sweetTicketPrinter.setOnUncaughtException(error -> showMessageAreaError(error, "error"));
				sweetTicketPrinter.printTicket();
				Platform.runLater(() -> showMessageAreaError("La prueba no lanzo una excepcion.", "info"));
			} catch (Exception e) {
				Platform.runLater(() -> showMessageAreaError(e.getMessage(), "error"));
				logger.error("Excepcion al imprimir, pruebas avanzadas: {}", e.getMessage(), e);
			} finally {
				Platform.runLater(() -> {
					btnTest.setDisable(false);
					btnPrint.setDisable(false);
				});
			}
		});
	}

	@FXML
	void onClickListViewServices(MouseEvent event) {
		if (event.getClickCount() == 1) {
			String selectedItem = listViewServices.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {
				AppUtil.toClipboard(selectedItem);
				AppUtil.toast(getStage(), String.format("Se copio %s", selectedItem));
				txtNameSystem.setText(selectedItem);
			}
		}
	}

	@FXML
	void onReloadPrintServices() {
		reloadPrintServices();
	}

	private void reloadPrintServices() {
		listViewServices.getItems().clear();
		listViewServices.getItems().addAll(PrintTestService.getPrintServices());
	}

	private void initTypesConnectionList() {
		var typesConnection = PrintTestService.getTypesConnection();
		cmbTypeConnection.getItems().addAll(typesConnection);
		cmbTypeConnection.setValue(typesConnection.get(0));
	}

	private void initCharCodeTableList() {
		var charCodeTableList = PrintTestService.getCharCodeTableList();
		cmbCharCodeTable.getItems().addAll(charCodeTableList);
		cmbCharCodeTable.setValue(charCodeTableList.get(0));
	}

	private void initTypeDocumentList() {
		var typeDocumentList = PrintTestService.getTypeDocumentsMap().keySet();
		cmbTypeDocument.getItems().addAll(typeDocumentList);
		var firstItem = typeDocumentList.stream().findFirst();
		firstItem.ifPresent(s -> cmbTypeDocument.setValue(s));
	}

	private void initDefaultValues() {
		txtPort.setText("9100");
		txtCharacterPerLine.setText("42");
		checkBoxInvertedText.setSelected(true);
		checkBoxNativeQR.setSelected(false);
		checkBoxCharCodeTable.setSelected(false);
		checkBoxNormalize.setSelected(false);
	}

	private void showMessageAreaError(String message, String type) {
		switch (type) {
			case "info":
				txtErrorArea.setStyle("-fx-text-fill: #6afc65;");
				break;
			case "error":
				txtErrorArea.setStyle("-fx-text-fill: #fc8865;");
				break;
		}
		txtErrorArea.setText(message);
	}

	private Stage getStage() {
		return (Stage) root.getScene().getWindow();
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
