package pe.puyu.pukahttp.services.printer.outputstream;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import pe.puyu.pukahttp.services.printer.interfaces.Cancelable;
import pe.puyu.pukahttp.services.printer.interfaces.Caughtable;
import pe.puyu.pukahttp.util.AppUtil;

import java.awt.print.PrinterJob;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;

public class ServiceOutputStream extends PipedOutputStream implements Cancelable, Caughtable {

	protected final PipedInputStream pipedInputStream;
	protected final Thread threadPrint;

	protected final Logger logger = (Logger) LoggerFactory.getLogger(AppUtil.makeNamespaceLogs("ServiceOutputStream"));

	public ServiceOutputStream(PrintService printService) throws IOException {

		Thread.UncaughtExceptionHandler uncaughtException = (t, e) ->
			logger.error("Excepcion no controlada en ServiceOutputStream: {}", e.getMessage(), e);

		pipedInputStream = new PipedInputStream();
		super.connect(pipedInputStream);

		Runnable runnablePrint = () -> {
			try {
				DocFlavor df = DocFlavor.INPUT_STREAM.AUTOSENSE;
				Doc d = new SimpleDoc(pipedInputStream, df, null);

				DocPrintJob job = printService.createPrintJob();
				job.print(d, null);
			} catch (PrintException ex) {
				throw new RuntimeException(ex);
			}
		};

		threadPrint = new Thread(runnablePrint);
		threadPrint.setUncaughtExceptionHandler(uncaughtException);
		threadPrint.start();
	}

	public void setUncaughtException(UncaughtExceptionHandler uncaughtException) {
		threadPrint.setUncaughtExceptionHandler(uncaughtException);
	}

	public static String[] getListPrintServicesNames() {
		PrintService[] printServices = PrinterJob.lookupPrintServices();
		String[] printServicesNames = new String[printServices.length];
		for (int i = 0; i < printServices.length; i++) {
			printServicesNames[i] = printServices[i].getName();
		}
		return printServicesNames;
	}

	public static PrintService getPrintServiceByName(String printServiceName) {
		PrintService[] printServices = PrinterJob.lookupPrintServices();
		PrintService foundService = null;

		for (PrintService service : printServices) {
			if (service.getName().compareTo(printServiceName) == 0) {
				foundService = service;
				break;
			}
		}
		if (foundService != null) {
			return foundService;
		}

		for (PrintService service : printServices) {
			if (service.getName().compareToIgnoreCase(printServiceName) == 0) {
				foundService = service;
				break;
			}
		}
		if (foundService != null) {
			return foundService;
		}

		for (PrintService service : printServices) {
			if (service.getName().toLowerCase().contains(printServiceName.toLowerCase())) {
				foundService = service;
				break;
			}
		}
		if (foundService == null) {
			throw new IllegalArgumentException("printServiceName is not found");
		}
		return foundService;
	}

	@Override
	public void cancel() {
		try {
			this.close();
		} catch (IOException ignored) {
		}
	}
}
