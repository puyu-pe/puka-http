package pe.puyu.pukahttp.services.printer;

import com.google.gson.JsonObject;
import pe.puyu.jticketdesing.core.SweetTicketDesign;
import pe.puyu.pukahttp.util.AppUtil;

import java.io.OutputStream;
import java.nio.file.Files;

public class SweetTicketPrinter extends SweetPrinter{
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
		var logo = AppUtil.getLogoFileDir();
		if ((!metadata.has("logoPath") || metadata.get("logoPath").isJsonNull()) && Files.exists(logo)) {
			metadata.addProperty("logoPath", logo.toString());
			ticket.add("metadata", metadata);
		}
	}

}
