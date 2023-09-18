package pe.puyu.service;

import java.net.URI;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import io.socket.client.IO;
import io.socket.client.Socket;

public class BifrostService {
  private final Logger logger = (Logger) LoggerFactory.getLogger("pe.puyu.service");

  Socket socket;
  URI uriBifrost;
  int attempsConnection;

  public BifrostService(URI uri) {
    IO.Options options = IO.Options.builder().build();
    uriBifrost = uri;
    attempsConnection = 0;
    socket = IO.socket(uri, options);
    startListiningEvents();
  }

  public void start() {
    socket.connect();
    logger.info("Se inicia el servicio de bifrost");
  }

  private void startListiningEvents() {
    socket.on("printer:send-printing-queue", this::onSendPrintingQueue);
    socket.on("printer:emit-item", this::onEmitItem);
    socket.on("printer:send-number-items-queue", this::onSendNumberItemsQueue);
    socket.on(Socket.EVENT_CONNECT, this::onConnected);
    socket.on(Socket.EVENT_CONNECT_ERROR, this::onConnectedError);
    socket.on(Socket.EVENT_DISCONNECT, this::onDisconnect);
  }

  private void onSendPrintingQueue(Object... args) {
    try {
      var response = new BifrostResponse((JSONObject) args[0]);
      if (response.getStatus().equalsIgnoreCase("success")) {
        logger.debug("Llego cola de impresión de bifrost con el siguiente mensaje: {}", response.getMessage());
        printItems(response.getData());
      }
    } catch (JSONException e) {
      logger.error("Excepción al obtener cola de impresión: {}", e.getMessage(), e);
    }
  }

  private void onEmitItem(Object... args) {
    try {
      var response = new BifrostResponse((JSONObject) args[0]);
      logger.debug("Llego un item de bifrost para imprimir con el siguiente mensaje: {}", response.getMessage());
      printItems(response.getData());
    } catch (JSONException e) {
      logger.error("Excepción al lanzar el evento para emitir un item emit-item: {}", e.getMessage(), e);
    }
  }

  private void onConnected(Object... args) {
    // TODO: Notify onConnectedSuccess
    attempsConnection = 0;
    logger.info("Se establecio conexión con {}", uriBifrost);
    requestToGetPrintingQueue();
  }

  private void onConnectedError(Object... args) {
    ++attempsConnection;
    logger.error("Ocurrio un error al intentar conectarse: {}, ...reintentando n° {}", args[0], attempsConnection);
  }

  private void onDisconnect(Object... args) {
    var message = "";
    if (args.length > 0)
      message = args[0].toString();
    logger.info("El servicio a sido desconectado: {}", message);
  }

  private void onSendNumberItemsQueue(Object... args) {
    int numberItemsQueue = (int) args[0];
    // TODO: Notify onChangeNumberItemsQueue
    logger.debug("Se actualizo el numero de elementos en cola: {}", numberItemsQueue);
  }

  private void emitPrintItem(String itemId) {
    // NOTE: Considerar cambiar el nombre del evento printer:emit-item por
    // printer:release-item
    try {
      JSONObject obj = new JSONObject();
      obj.put("key", itemId);
      socket.emit("printer:print-item", obj);
    } catch (JSONException e) {
      logger.error("Ocurrio una excepción en emit printer:print-item: {}", e.getMessage(), e);
    }
  }

  public void requestToGetPrintingQueue() {
    socket.emit("printer:get-printing-queue");
    logger.debug("Se solicita cola de impresión a bifrost");
  }

  public void requestToReleaseQueue() {
    socket.emit("printer:release-queue");
    logger.debug("Se solicita liberar cola de impresión a bifrost");
  }

  public void printItems(Map<String, JSONObject> data) {
    logger.debug("Se recibe {} items de bifrost", data.size());
    for (Map.Entry<String, JSONObject> entry : data.entrySet()) {
      var id = entry.getKey();
      var item = entry.getValue();
      try {
        if (!item.has("tickets")) {
          throw new Exception(String.format("La propiedad tickets no existe para el itemId %d", id));
        }
        var tickets = new JSONArray(item.getString("tickets"));
        for (int i = 0; i < tickets.length(); ++i) {
          var ticket = tickets.get(i);
          logger.trace("Se imprimira el siguiente ticket con id {}: {}", id, ticket);
          emitPrintItem(id);
        }
      } catch (Exception e) {
        logger.error("Excepción intentar imprimir un ticket con id {}: {}", id, e.getMessage(), e);
      }
    }
  }

}
