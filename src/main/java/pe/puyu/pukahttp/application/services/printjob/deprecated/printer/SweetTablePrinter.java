package pe.puyu.pukahttp.application.services.printjob.deprecated.printer;

import com.google.gson.JsonObject;
import pe.puyu.jticketdesing.core.table.SweetTableDesign;

import java.io.OutputStream;

public class SweetTablePrinter extends SweetPrinter {
	private final JsonObject data;

	public SweetTablePrinter(JsonObject data) {
		super(data.getAsJsonObject("printer"));
		this.data = data;
	}

	@Override
	public void print() throws Exception {
		try (OutputStream outputStream = getOutputStreamByPrinterType()) {
			outputStream.write(new SweetTableDesign(data).getBytes());
		}
	}

}
