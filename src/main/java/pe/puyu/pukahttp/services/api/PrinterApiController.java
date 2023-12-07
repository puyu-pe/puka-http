package pe.puyu.pukahttp.services.api;

import ch.qos.logback.classic.Logger;
import com.github.anastaciocintra.output.PrinterOutputStream;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.javalin.http.Context;
import io.javalin.websocket.WsConfig;
import org.slf4j.LoggerFactory;
import pe.puyu.pukahttp.repository.AppDatabase;
import pe.puyu.pukahttp.repository.TicketRepository;
import pe.puyu.pukahttp.services.printer.SweetTicketPrinter;
import pe.puyu.pukahttp.util.AppUtil;

import java.time.Duration;
import java.util.LinkedList;
import java.util.function.Consumer;

public class PrinterApiController {
	private final Logger logger = (Logger) LoggerFactory.getLogger(AppUtil.makeNamespaceLogs("PrinterApiController"));
	private TicketRepository ticketRepository;

	public PrinterApiController(AppDatabase db) {
		db.getConnection().ifPresent(con -> ticketRepository = new TicketRepository(con));
	}

	public void getAllPrinterSystem(Context ctx) {
		var response = new ResponseApi<String[]>();
		response.setMessage("Datos recuperados exitosamente");
		response.setStatus("success");
		response.setData(PrinterOutputStream.getListPrintServicesNames());
		ctx.json(response);
	}

	public void printTickets(Context ctx) {
		var response = new ResponseApi<Integer>();
		ctx.async(
			15000,
			() -> {
				response.setStatus("error");
				response.setMessage("Hubo un problema al imprimir ticktes.");
				response.setError("Trabajo de impresion excedio el tiempo de espera.");
				setResponseItemsQueue(response);
				ctx.json(response);
			},
			() -> {
				JsonArray tickets = JsonParser.parseString(ctx.body()).getAsJsonArray();
				printJob(tickets);
				response.setStatus("success");
				response.setMessage("Trabajo de impresión no lanzo ningun error.");
				setResponseItemsQueue(response);
				ctx.json(response);
			}
		);
	}

	public void deleteTickets(Context ctx) {
		var response = new ResponseApi<Integer>();
		if (!(ticketRepository == null)) {
			ticketRepository.deleteAll();
			response.setData(ticketRepository.countAll());
		} else {
			response.setData(0);
		}
		response.setMessage("Se libero todos los tickets en memoria");
		response.setStatus("success");
		ctx.json(response);
	}

	public void reprintTickets(Context ctx) {
		var response = new ResponseApi<Integer>();
		ctx.async(
			20000,
			() -> {
				response.setStatus("error");
				response.setMessage("Hubo un problema al reimprimir ticktes.");
				response.setError("Trabajo de impresion excedio el tiempo de espera.");
				setResponseItemsQueue(response);
				ctx.json(response);
			},
			() -> {
				if (ticketRepository != null) {
					var list = ticketRepository.getAll();
					JsonArray tickets = new JsonArray();
					for (var item : list) {
						tickets.add(JsonParser.parseString(item.getData()));
					}
					ticketRepository.deleteAll();
					printJob(tickets);
					response.setData(ticketRepository.countAll());
				} else {
					response.setData(0);
				}
				response.setMessage("La operacion se completo exitosamente");
				response.setStatus("success");
				ctx.json(response);
			}
		);
	}

	public void genericErrorHandling(Exception e, Context ctx) {
		logger.error("Error PrinterApiController, \nMESSAGE: {}.\nCAUSE: {}.\nLOCALIZED_MESSAGE: {}."
			, e.getMessage()
			, e.getCause()
			, e.getLocalizedMessage());
		var response = new ResponseApi<Integer>();
		response.setMessage("Ocurrio un error inesperado");
		response.setError(e.getMessage());
		response.setStatus("error");
		setResponseItemsQueue(response);
		ctx.json(response);
	}

	public void printJob(JsonArray tickets) {
		var errors = new LinkedList<String>();
		for (var ticket : tickets) {
			try {
				logger.trace("Ticket a imprimir: {}", ticket);
				var sweetTicketPrinter = new SweetTicketPrinter(ticket.getAsJsonObject());
				sweetTicketPrinter.setOnUncaughtException(e -> {
					errors.add(e);
					logger.warn("UncaughtException printJob: {}", e);
					secureInsertTicket(ticket);
				});
				sweetTicketPrinter.printTicket();
			} catch (Exception e) {
				errors.add(e.getMessage());
				secureInsertTicket(ticket);
				logger.error("Error al imprimir un ticket:  {}", e.getMessage());
			}
		}
		if (!errors.isEmpty()) {
			throw new RuntimeException(errors.toString());
		}
	}

	private void secureInsertTicket(JsonElement ticket) {
		if (ticketRepository != null) {
			ticketRepository.insert(ticket.getAsJsonObject().toString());
		}
	}

	private void setResponseItemsQueue(ResponseApi<Integer> response) {
		if (ticketRepository != null)
			response.setData(ticketRepository.countAll());
		else
			response.setData(0);
	}


	public void getSizeQueue(Context ctx) {
		var response = new ResponseApi<Integer>();
		setResponseItemsQueue(response);
		response.setMessage("Operacion completada exitosamente");
		response.setStatus("success");
		ctx.json(response);
	}

	public void getQueueEvents(WsConfig ws) {
		ws.onConnect(ctx -> {
			ctx.session.setIdleTimeout(Duration.ofDays(1));
			Consumer<Integer> observer = (count) -> {
				if (ticketRepository != null) {
					ctx.send(count);
				}
			};
			ticketRepository.addObserver(observer);
			ws.onClose(closeCtx -> {
				logger.info("close ws connection, then remove observer ticketrepository.");
				ticketRepository.removeObserver(observer);
			});
			ws.onError(ctxError -> logger.error("Exception on websockets connection", ctxError.error()));
		});
	}
}

