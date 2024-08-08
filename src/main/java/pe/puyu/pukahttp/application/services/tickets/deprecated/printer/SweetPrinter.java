package pe.puyu.pukahttp.application.services.tickets.deprecated.printer;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.OutputStream;
import java.util.Optional;

public abstract class SweetPrinter {
	private final JsonObject printerInfo;

	public SweetPrinter(JsonObject printerObject){
		this.printerInfo = printerObject;
	}

	public abstract void print() throws Exception;

	protected OutputStream getOutputStreamByPrinterType() throws Exception {
		if (!this.printerInfo.has("name_system")  || !this.printerInfo.get("name_system").isJsonPrimitive())
			throw new Exception("property 'name_system' in printer object is void or it's not string");
		if (!this.printerInfo.has("type")  || !this.printerInfo.get("type").isJsonPrimitive())
			throw new Exception("property 'type' in printer object is void or it's not string");
		String name_system = this.printerInfo.get("name_system").getAsString();
		int port = getEthernetPort();
		return Printer.getOutputStreamFor(name_system, port, this.printerInfo.get("type").getAsString());
	}

	private int getEthernetPort(){
		JsonPrimitive portProperty = Optional
			.ofNullable(this.printerInfo.get("port"))
			.orElse(new JsonPrimitive("9100"))
			.getAsJsonPrimitive();
		int port = 9100;
		try{
			port = Integer.parseInt(portProperty.getAsString());
		}catch (Exception ignored){
		}
		return port;
	}


}
