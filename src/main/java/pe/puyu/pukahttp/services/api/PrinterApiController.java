package pe.puyu.pukahttp.services.api;

import ch.qos.logback.classic.Logger;
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.output.PrinterOutputStream;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.http.Context;
import io.javalin.websocket.WsConfig;
import org.slf4j.LoggerFactory;
import pe.puyu.pukahttp.Constants;
import pe.puyu.pukahttp.repository.AppDatabase;
import pe.puyu.pukahttp.repository.TicketRepository;
import pe.puyu.pukahttp.services.configuration.ConfigAppProperties;
import pe.puyu.pukahttp.services.printer.Printer;
import pe.puyu.pukahttp.services.printer.SweetTablePrinter;
import pe.puyu.pukahttp.services.printer.SweetTicketPrinter;
import pe.puyu.pukahttp.util.AppUtil;
import pe.puyu.pukahttp.util.Notifier;

import java.io.OutputStream;
import java.text.ParseException;
import java.time.Duration;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
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

	public void openDrawer(Context ctx, Notifier serverNotifier) {
		var response = new ResponseApi<String>();
		ctx.async(
			10000,
			() -> {
				response.setStatus("error");
				response.setMessage("Error al abrir caja");
				response.setError("Tiempo espera agotado");
				serverNotifier.error("Error al abrir caja registradora, razon: timeout");
				ctx.json(response);
			},
			() -> {
				try {
					JsonObject printer = JsonParser.parseString(ctx.body()).getAsJsonObject();
					var name_system = printer.get("name_system").getAsString();
					var port = printer.get("port").getAsInt();
					var type = printer.get("type").getAsString();
					OutputStream outputStream = Printer.getOutputStreamFor(name_system, port, type);
					try (var escpos = new EscPos(outputStream)) {
						escpos.pulsePin(EscPos.PinConnector.Pin_2, 120, 240);
						response.setMessage("Operación de abrir caja exitosa.");
						response.setStatus("success");
						ctx.json(response);
					}
					serverNotifier.info("Solicitud exitosa para abrir caja registradora.");
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		);
	}

	public void printTickets(Context ctx, Notifier serverNotifier) {
		var response = new ResponseApi<Integer>();
		ctx.async(
			15000,
			() -> {
				response.setStatus("error");
				response.setMessage("Hubo un problema al imprimir ticktes.");
				response.setError("Trabajo de impresion excedio el tiempo de espera.");
				setResponseItemsQueue(response);
				ctx.json(response);
				serverNotifier.error("Error al imprimir ticktes, razon: timeout");
			},
			() -> {
				JsonArray tickets = JsonParser.parseString(ctx.body()).getAsJsonArray();
				printTicketJob(tickets, serverNotifier);
				response.setStatus("success");
				response.setMessage("Trabajo de impresión no lanzo ningun error.");
				setResponseItemsQueue(response);
				ctx.json(response);
			}
		);
	}

	public void printReport(Context ctx, Notifier serverNotifier) {
		var response = new ResponseApi<String>();
		ctx.async(
			20000,
			() -> {
				response.setMessage("Ocurrio un error al intentar imprimir el reporte");
				response.setStatus("error");
				response.setError("Tiempo de conexión agotado: timeout");
				ctx.json(response);
				serverNotifier.error("El reporte no se pudo imprimir, tiempo de conexión agotado.");
			},
			() -> {
				JsonElement body = JsonParser.parseString(ctx.body());
				if (!body.isJsonObject()) {
					throw new RuntimeException("bad json, expected a Json Object");
				}
				JsonObject data = body.getAsJsonObject();
				logger.trace("report json to print: {}", data);
				SweetTablePrinter sweetTablePrinter = new SweetTablePrinter(data);
				sweetTablePrinter.setOnUncaughtException(
					errorMessage -> serverNotifier.error(String.format("UncaughtException on print report: %s", errorMessage))
				);
				sweetTablePrinter.print();
				response.setStatus("success");
				response.setMessage("Reporte generado para imprimir correctamente.");
				response.setData("");
				ctx.json(response);
			}
		);
	}

	public void deleteTickets(Context ctx, Notifier serverNotifier) throws ParseException {
		var response = new ResponseApi<Integer>();
		response.setData(0);
		response.setMessage("Se libero todos tickets en memoria");
		response.setStatus("success");
		if (ticketRepository != null) {
			var countTickets = ticketRepository.countAll();
			var dateString = ctx.queryParam("date");
			if (dateString != null && !dateString.isEmpty()) {
				var date = AppUtil.getDateTimeFormatter().parse(dateString);
				var rowsDeleted = ticketRepository.deleteWithDatesBefore(date);
				if (rowsDeleted > 0) {
					response.setMessage(String.format("to deleted %d tickets", rowsDeleted));
					serverNotifier.info(String.format("Se eliminaron %d tickets, por que expiro su tiempo de vida.", rowsDeleted));
				}
			} else {
				if (countTickets > 0) {
					ticketRepository.deleteAll();
					serverNotifier.info("All tickets, They were removed.");
				}
			}
			response.setData(countTickets);
		}
		ctx.json(response);
	}

	public void reprintTickets(Context ctx, Notifier serverNotifier) {
		var response = new ResponseApi<Integer>();
		ctx.async(
			20000,
			() -> {
				response.setStatus("error");
				response.setMessage("Hubo un problema al reimprimir ticktes.");
				response.setError("Trabajo de impresion excedio el tiempo de espera.");
				setResponseItemsQueue(response);
				ctx.json(response);
				serverNotifier.error("Error al reimprimir tickets, razon: timeout");
			},
			() -> {
				if (ticketRepository != null) {
					var list = ticketRepository.getAll();
					JsonArray tickets = new JsonArray();
					for (var item : list) {
						tickets.add(JsonParser.parseString(item.getData()));
					}
					ticketRepository.deleteAll();
					printTicketJob(tickets, serverNotifier);
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

	public void genericErrorHandling(Exception e, Context ctx, Notifier serverNotifier) {
		logger.error("Error PrinterApiController, \nMESSAGE: {}.\nCAUSE: {}.\nLOCALIZED_MESSAGE: {}."
			, e.getMessage()
			, e.getCause()
			, e.getLocalizedMessage());
		var response = new ResponseApi<Integer>();
		response.setMessage("Ocurrio una excepción en el servidor");
		response.setError(e.getMessage());
		response.setStatus("error");
		setResponseItemsQueue(response);
		ctx.json(response);
		serverNotifier.warn(String.format("Exception into server: %s", e.getLocalizedMessage()));
	}

	public void printTicketJob(JsonArray tickets, Notifier serverNotifier) {
		var errors = new LinkedList<String>();
		ConfigAppProperties configurations = new ConfigAppProperties();
		Optional<Integer> printDelay = configurations.printDelay();
		for (var ticket : tickets) {
			var printer = ticket.getAsJsonObject().getAsJsonObject("printer");
			try {
				logger.trace("Ticket a imprimir: {}", ticket);
				var sweetTicketPrinter = new SweetTicketPrinter(ticket.getAsJsonObject());
				sweetTicketPrinter.setOnUncaughtException(e -> {
					errors.add(e);
					secureInsertTicket(ticket);
					serverNotifier.warn(String.format("UncaughtException printJob: %s", e));
				});
				sweetTicketPrinter.print();
				TimeUnit.MILLISECONDS.sleep(printDelay.orElse(Constants.printDelayDefault));
			} catch (InterruptedException ignored) {
			} catch (Exception e) {
				errors.add(e.getMessage());
				secureInsertTicket(ticket);
				logger.error("Error al imprimir un ticket:  {}", e.getMessage());
				serverNotifier.error(String.format(
					"Error al imprimir name_system: %s, port: %d, type: %s, error: %s",
					printer.get("name_system").getAsString(),
					printer.get("port").getAsInt(),
					printer.get("type").getAsString(),
					e.getLocalizedMessage()
				));
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
			if (ticketRepository != null) {
				Consumer<Integer> observer = (count) -> {
					if (ticketRepository != null) {
						ctx.send(count);
					}
				};
				String observerId = ctx.getSessionId();
				ticketRepository.addObserver(observerId, observer);
				logger.info("Add observer {} to queue events websocket.", observerId);
				ws.onClose(closeCtx -> {
					String idToRemove = closeCtx.getSessionId();
					ticketRepository.removeObserver(idToRemove);
					logger.info("close ws connection, then remove observer {}.", idToRemove);
				});
			}
			ws.onError(ctxError -> {
				if (ctxError.error() != null) {
					logger.warn("Exception an occurred in websockets queue-events with message: {}", ctxError.error().getMessage());
					logger.trace("more details into fillInStackTrace: ", ctxError.error().fillInStackTrace());
				}
			});
		});
	}
}

