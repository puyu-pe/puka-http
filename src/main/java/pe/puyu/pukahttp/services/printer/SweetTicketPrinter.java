package pe.puyu.pukahttp.services.printer;

import ch.qos.logback.classic.Logger;
import com.google.gson.JsonObject;
import org.slf4j.LoggerFactory;
import pe.puyu.jticketdesing.core.SweetTicketDesign;
import pe.puyu.pukahttp.services.printer.interfaces.Cancelable;
import pe.puyu.pukahttp.util.AppUtil;

import java.io.OutputStream;
import java.util.function.Consumer;

public class SweetTicketPrinter {
	private final JsonObject printerInfo;
	private final JsonObject ticket;
	private Consumer<String> onUncaughtException;
	private static final Logger logger = (Logger) LoggerFactory.getLogger(AppUtil.makeNamespaceLogs("SweetTicketPrinter"));

	public SweetTicketPrinter(JsonObject ticket) {
		this.ticket = ticket;
		this.printerInfo = ticket.getAsJsonObject("printer");
		this.onUncaughtException = logger::error;
	}

	public void printTicket() throws Exception {
		try (OutputStream outputStream = getOutputStreamByPrinterType()) {
			loadMetadata();
			outputStream.write(new SweetTicketDesign(ticket).getBytes());
		}
	}

	public void setOnUncaughtException(Consumer<String> onUncaughtException) {
		this.onUncaughtException = onUncaughtException;
	}

	private void loadMetadata() throws Exception {
		var metadata = new JsonObject();
		if (ticket.has("metadata") && !ticket.get("metadata").isJsonNull()) {
			metadata = ticket.getAsJsonObject("metadata");
		}
		if (!metadata.has("logoPath") || metadata.get("logoPath").isJsonNull()) {
			metadata.addProperty("logoPath", AppUtil.getLogoFileDir());
			ticket.add("metadata", metadata);
		}
	}

	private OutputStream getOutputStreamByPrinterType() throws Exception {
		if (this.printerInfo.get("name_system").isJsonNull())
			throw new Exception("name_system esta vacio");
		var name_system = this.printerInfo.get("name_system").getAsString();
		var port = this.printerInfo.get("port").getAsInt();
		var outputStream = Printer.getOutputStreamFor(name_system, port, this.printerInfo.get("type").getAsString());
		Printer.setOnUncaughtExceptionFor(outputStream, makeUncaughtException(outputStream));
		return outputStream;
	}

	private Thread.UncaughtExceptionHandler makeUncaughtException(OutputStream outputStream) {
		return (t, e) -> {
			logger.error("UncaughtException SweetTicketPrinter: {}, thread_name: {}, thread_state: {}",
				e.getMessage(), t.getName(), t.getState().name(), e);
			onUncaughtException.accept(makeErrorMessage(e.getMessage()));
			if (outputStream instanceof Cancelable) {
				((Cancelable) outputStream).cancel();
			}
		};
	}

	private String makeErrorMessage(String message) {
		return String.format("UncaughtException en name_system: %s, type: %s, port: %d, message: %s",
			printerInfo.get("name_system").getAsString(), printerInfo.get("type").getAsString(),
			printerInfo.get("port").getAsInt(), message);
	}

}
