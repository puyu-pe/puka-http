package pe.puyu.sweetprinterpos.services.api;

import ch.qos.logback.classic.Logger;
import com.github.anastaciocintra.output.PrinterOutputStream;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import io.javalin.http.Context;
import org.slf4j.LoggerFactory;
import pe.puyu.sweetprinterpos.services.printer.SweetTicketPrinter;
import pe.puyu.sweetprinterpos.util.AppUtil;

import java.util.LinkedList;

public class PrinterController {
	private static final Logger logger = (Logger) LoggerFactory.getLogger(AppUtil.makeNamespaceLogs("PrinterController"));

	public static void getAllPrinterSystem(Context ctx) {
		var response = new ResponseApi<String[]>();
		response.setMessage("Datos recuperados exitosamente");
		response.setStatus("success");
		response.setData(PrinterOutputStream.getListPrintServicesNames());
		ctx.json(response);
	}

	public static void printTickets(Context ctx) {
		var response = new ResponseApi<>();
		ctx.async(
			10000,
			() -> {
				response.setStatus("error");
				response.setMessage("Hubo un problema al imprimir ticktes.");
				response.setError("Trabajo de impresion excedio el tiempo de espera.");
				ctx.json(response);
			},
			() -> {
				JsonArray tickets = JsonParser.parseString(ctx.body()).getAsJsonArray();
				printJob(tickets);
				response.setStatus("success");
				response.setMessage("Trabajo de impresión no lanzo ningun error.");
				ctx.json(response);
			}
		);
	}

	public static void genericErrorHandling(Exception e, Context ctx) {
		logger.error("Error PrinterController, \nMESSAGE: {}.\nCAUSE: {}.\nLOCALIZED_MESSAGE: {}."
			, e.getMessage()
			, e.getCause()
			, e.getLocalizedMessage());
		var response = new ResponseApi<>();
		response.setMessage("Ocurrio un error inesperado");
		response.setError(e.getMessage());
		response.setStatus("error");
		ctx.json(response);
	}

	public static void printJob(JsonArray tickets) {
		var errors = new LinkedList<>();
		for (var ticket : tickets) {
			try {
				logger.trace("Ticket a imprimir: {}", ticket);
				var sweetTicketPrinter = new SweetTicketPrinter(ticket.getAsJsonObject());
				sweetTicketPrinter.setOnUncaughtException(e -> {
					errors.add(e);
					logger.warn("Error al imprimir un ticket: {}", e);
				});
				sweetTicketPrinter.printTicket();
			} catch (Exception e) {
				errors.add(e.getMessage());
				logger.error("Error al imprimir un ticket:  {}", e.getMessage());
			}
		}
		if (!errors.isEmpty())
			throw new RuntimeException(errors.toString());
	}

}

