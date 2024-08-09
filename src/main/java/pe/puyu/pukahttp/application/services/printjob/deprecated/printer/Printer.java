package pe.puyu.pukahttp.application.services.printjob.deprecated.printer;

import pe.puyu.pukahttp.application.services.printjob.deprecated.printer.interfaces.Caughtable;
import pe.puyu.pukahttp.application.services.printjob.deprecated.printer.outputstream.ImmediateFileStream;
import pe.puyu.pukahttp.application.services.printjob.deprecated.printer.outputstream.ImmediateSocketOutputStream;
import pe.puyu.pukahttp.application.services.printjob.deprecated.printer.outputstream.SerialStream;
import pe.puyu.pukahttp.application.services.printjob.deprecated.printer.outputstream.ServiceOutputStream;

import java.io.OutputStream;
import java.lang.Thread.UncaughtExceptionHandler;

public class Printer {

  public enum Type {
    WINDOWS_USB("windows-usb"),
    LINUX_USB("linux-usb"),
    SAMBA("samba"),
    SERIAL("serial"),
    CUPS("cups"),
    SMBFILE("smbfile"),
    ETHERNET("ethernet");

    private final String value;

    Type(String value) {
      this.value = value;
    }

    public String getValue() {
      return this.value.trim();
    }

    public static Type fromValue(String value) {
      for (Type type : Type.values()) {
        if (type.value.equals(value.trim())) {
          return type;
        }
      }
      throw new IllegalArgumentException(String.format("Tipo de conexión: %s, no soportado", value));
    }
  }

  public static OutputStream getOutputStreamFor(String name_system, int port, String typeConnection) throws Exception {
    var type = Type.fromValue(typeConnection);
	  return switch (type) {
		  case SMBFILE -> new ImmediateFileStream(name_system);
		  case SERIAL -> new SerialStream(name_system);
		  case WINDOWS_USB, LINUX_USB, SAMBA, CUPS -> {
			  var printService = ServiceOutputStream.getPrintServiceByName(name_system);
			  yield new ServiceOutputStream(printService);
		  }
		  case ETHERNET -> new ImmediateSocketOutputStream(name_system, port);
	  };
  }

  public static void setOnUncaughtExceptionFor(OutputStream outputStream, UncaughtExceptionHandler uncaughtException) {
		if(outputStream instanceof Caughtable){
			((Caughtable) outputStream).setUncaughtException(uncaughtException);
		}
  }

}
