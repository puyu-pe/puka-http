package pe.puyu.pukahttp.util;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.util.function.Consumer;

@ClientEndpoint
public class WebSocketClient {
	private Session session;
	private final String nameService;
	private Consumer<String> onMessage;
	private final Logger logger = (Logger) LoggerFactory.getLogger(AppUtil.makeNamespaceLogs("WebSocketClient"));

	public WebSocketClient(String serviceName) {
		this.nameService = serviceName;
	}

	@OnOpen
	public void onOpen(Session session) {
		logger.info("A new websocket session for: {}", nameService);
		this.session = session;
	}

	@OnClose
	public void onClose(Session session) {
		logger.info("close session for service: {}", nameService);
		this.session = null;
	}

	@OnMessage
	public void onMessage(String message) {
		this.onMessage.accept(message);
	}

	public void sendMessage(String message) {
		this.session.getAsyncRemote().sendText(message);
	}

	public void setOnMessage(Consumer<String> onMessage) {
		this.onMessage = onMessage;
	}

}
