package pe.puyu.sweetprinterpos.app;

import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import pe.puyu.sweetprinterpos.services.api.PrintServer;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class App extends Application {
	// Level error : TRACE DEBUG INFO WARN ERROR
	private static final Logger rootLogger = (Logger) LoggerFactory.getLogger("pe.puyu.sweetprinterpos");

	@Override
	public void init() {
		rootLogger.setLevel(Level.TRACE);
	}

	@Override
	public void start(Stage stage) {
		PrintServer server = new PrintServer();
		var ip = "127.0.0.1";
		var port = 7070;
		rootLogger.info("Start service on {}:{}", ip, port);
		server.listen(ip, port);
	}

	@Override
	public void stop() {
	}

	public static void main(String[] args) {
		launch(args);
	}

}