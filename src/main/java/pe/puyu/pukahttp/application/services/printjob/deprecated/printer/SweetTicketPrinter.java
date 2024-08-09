package pe.puyu.pukahttp.application.services.printjob.deprecated.printer;

import com.google.gson.JsonObject;
import pe.puyu.jticketdesing.core.SweetTicketDesign;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import pe.puyu.pukahttp.infrastructure.config.AppConfig;

public class SweetTicketPrinter extends SweetPrinter {
	private final JsonObject ticket;

	public SweetTicketPrinter(JsonObject ticket) {
		super(ticket.getAsJsonObject("printer"));
		this.ticket = ticket;
	}

	@Override
	public void print() throws Exception {
		try (OutputStream outputStream = getOutputStreamByPrinterType()) {
			loadMetadata();
			outputStream.write(new SweetTicketDesign(ticket).getBytes());
		}
	}

	private void loadMetadata() {
		var metadata = new JsonObject();
		if (ticket.has("metadata") && !ticket.get("metadata").isJsonNull()) {
			metadata = ticket.getAsJsonObject("metadata");
		}
		Path logo = AppConfig.getLogoFilePath();
		if ((!metadata.has("logoPath") || metadata.get("logoPath").isJsonNull()) && Files.exists(logo)) {
			metadata.addProperty("logoPath", logo.toString());
			ticket.add("metadata", metadata);
		}
	}

}
