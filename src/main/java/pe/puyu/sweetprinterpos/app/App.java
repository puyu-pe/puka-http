package pe.puyu.sweetprinterpos.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import pe.puyu.sweetprinterpos.Constants;
import pe.puyu.sweetprinterpos.services.api.PrintServer;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {
	// Level error : TRACE DEBUG INFO WARN ERROR
	private static final Logger rootLogger = (Logger) LoggerFactory.getLogger("pe.puyu.sweetprinterpos");

	@Override
	public void init() {
		rootLogger.setLevel(Level.TRACE);
	}

	@Override
	public void start(Stage stage) {
		try {
			Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(Constants.POS_CONFIG_FXML)));
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("Configuración Servicio de Impresión");
			stage.show();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
//		PrintServer server = new PrintServer();
//		var ip = "127.0.0.1";
//		var port = 7070;
//		rootLogger.info("Start service on {}:{}", ip, port);
//		server.listen(ip, port);
	}

	@Override
	public void stop() {
	}

	public static void main(String[] args) {
		launch(args);
	}

}