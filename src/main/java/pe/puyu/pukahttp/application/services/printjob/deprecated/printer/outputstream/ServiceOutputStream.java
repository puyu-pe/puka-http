package pe.puyu.pukahttp.application.services.printjob.deprecated.printer.outputstream;

import pe.puyu.pukahttp.application.services.printjob.deprecated.printer.interfaces.Cancelable;
import pe.puyu.pukahttp.application.services.printjob.deprecated.printer.interfaces.Caughtable;

import javax.print.*;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.lang.Thread.UncaughtExceptionHandler;

public class ServiceOutputStream extends PipedOutputStream implements Cancelable, Caughtable {

	protected final PipedInputStream pipedInputStream;
	protected final Thread threadPrint;


	public ServiceOutputStream(PrintService printService) throws IOException {

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
		threadPrint.start();
	}

	public void setUncaughtException(UncaughtExceptionHandler uncaughtException) {
		threadPrint.setUncaughtExceptionHandler(uncaughtException);
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
