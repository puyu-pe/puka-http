package pe.puyu.pukahttp.util;

import ch.qos.logback.classic.Logger;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;
import org.slf4j.LoggerFactory;
import pe.puyu.pukahttp.Constants;
import pe.puyu.pukahttp.model.PosConfig;
import pe.puyu.pukahttp.services.api.PrintServer;
import pe.puyu.pukahttp.services.configuration.ConfigAppProperties;
import pe.puyu.pukahttp.services.trayicon.TrayIconServiceProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class AppUtil {

	public static String getUserDataDir() {
		AppDirs appDirs = AppDirsFactory.getInstance();
		//nota: appVersion = null por que no es necesario la version
		String userDataDir = appDirs.getUserDataDir(Constants.APP_NAME, null, "puyu");
		File file = new File(userDataDir);
		if (!file.exists()) {
			var ignored = file.mkdirs();
		}
		return userDataDir;
	}

	public static String getConfigAppFileDir(String jsonFileName) throws IOException {
		File file = new File(Path.of(getUserDataDir(), jsonFileName).toString());
		if (!file.exists()) {
			var ignored = file.createNewFile();
		}
		return file.getAbsolutePath();
	}

	public static String getPosConfigFileDir() throws Exception {
		return getConfigAppFileDir("pos.json");
	}

	public static String getDatabaseDirectory() {
		var folderDbPath = Path.of(getUserDataDir(), ".db", Constants.DB_VERSION);
		return folderDbPath.toString();
	}

	public static String getAppVersion() {
		var isBeta = !isProductionEnvironment();
		var suffix = isBeta ? "-beta" : "";
		try {
			var resourceUrl = AppUtil.class.getResource("/VERSION");
			BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceUrl).openStream()));
			String version = reader.readLine();
			reader.close();
			return version + suffix;
		} catch (Exception e) {
			return "0.1.0" + suffix;
		}
	}

	public static boolean isProductionEnvironment() {
		return AppUtil.class.getResource("/PRODUCTION") != null;
	}

	public static Optional<File> showPngFileChooser(Stage parent) {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
		fileChooser.getExtensionFilters().add(pngFilter);
		File selectFile = fileChooser.showOpenDialog(parent);
		return Optional.ofNullable(selectFile);
	}


	public static void toast(Stage stage, String text) {
		Popup popup = new Popup();
		Label message = new Label(text);
		message.setStyle("-fx-text-fill: #2cfc03; -fx-font-weight: bold; -fx-font-size: 16px");
		message.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.0), CornerRadii.EMPTY, Insets.EMPTY)));
		StackPane pane = new StackPane(message);
		pane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-padding: 10px; -fx-background-radius: 5px;");
		popup.getContent().add(pane);
		popup.show(stage);
		PauseTransition delay = new PauseTransition(Duration.seconds(2));
		delay.setOnFinished(e -> popup.hide());
		delay.play();
	}

	public static String getHostIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			return "127.0.0.1";
		}
	}

	public static String getLookDirectory() {
		String folderLockPath = Path.of(getUserDataDir(), ".lock").toString();
		var folderLock = new File(folderLockPath);
		if (!folderLock.exists()) {
			var ignored = folderLock.mkdirs();
		}
		return folderLockPath;
	}

	public static String makeLockFile(String fileName) {
		String folderLockPath = getLookDirectory();
		if (!fileName.startsWith("."))
			fileName = "." + fileName;
		File lockFile = new File(Path.of(folderLockPath, fileName).toString());
		return lockFile.getAbsolutePath();
	}

	public static String makeNamespaceLogs(String namespace) {
		return Constants.PACKAGE_BASE_PATH + "." + namespace.toLowerCase();
	}

	public static void toClipboard(String text) {
		Clipboard clipboard = Clipboard.getSystemClipboard();
		ClipboardContent content = new ClipboardContent();
		content.putString(text);
		clipboard.setContent(content);
	}

	public static PosConfig recoverPosConfigDefaultValues() {
		PosConfig config = new PosConfig();
		config.setIp(AppUtil.getHostIp());
		config.setPort(7172);
		config.setPassword("semiotica");
		try {
			var configOpt = JsonUtil.convertFromJson(AppUtil.getPosConfigFileDir(), PosConfig.class);
			return configOpt.orElse(config);
		} catch (Exception e) {
			return config;
		}
	}

	public static void openInNativeFileExplorer(String directory) {
		try {
			String os = System.getProperty("os.name").toLowerCase();
			Runtime runtime = Runtime.getRuntime();
			File dirToOpen = new File(directory);
			if (os.contains("win")) {
				runtime.exec("explorer.exe " + dirToOpen.getAbsolutePath());
			} else if (os.contains("nix") || os.contains("nux")) {
				runtime.exec("xdg-open " + dirToOpen.getAbsolutePath());
			} else if (os.contains("mac")) {
				runtime.exec("open " + dirToOpen.getAbsolutePath());
			}
		} catch (Exception ex) {
			toClipboard(directory);
		}
	}

	public static SimpleDateFormat getDateTimeFormatter() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	public static void releaseExpiredTickets(String ip, int port) throws Exception {
		var currentDate = new Date();
		var expiredDateTime = currentDate.getTime() - Constants.TIME_EXPIRED_TICKETS_MILLISECONDS;
		String expiredDate = AppUtil.getDateTimeFormatter().format(new Date(expiredDateTime));
		String encodeExpiredDate = URLEncoder.encode(expiredDate, StandardCharsets.UTF_8);
		var baseUrl = String.format("http://%s:%d/printer/ticket?date=%s", ip, port, encodeExpiredDate);
		HttpUtil.delete(baseUrl);
	}

	public static String getConfigAppFileDir() throws Exception {
		return createFileDirFromResourceIfNotExists(
			getUserDataDir(),
			"/",
			"config.ini"
		);
	}

	public static Path getLogoFileDir() {
		return Path.of(getUserDataDir(), "logo.png");
	}

	public static String createFileDirFromResourceIfNotExists(
		String destinationDir, String pathToResource, String resourceFileName
	) throws Exception {
		//note: pathToResource es una ruta relativa al directorio de paquetes del proyecto (classpath)
		//por ejemplo /pe/puyu/main-package/anyPackage../, ver getConfigFileDir() y getLogoFileDir()
		var destinationPath = Path.of(destinationDir, resourceFileName);
		var destinationFile = destinationPath.toFile();
		if (!destinationFile.exists()) {
			if (!pathToResource.endsWith("/")) {
				pathToResource += "/";
			}
			var resourceDir = pathToResource + resourceFileName;
			var resourceStream = Objects.requireNonNull(AppUtil.class.getResourceAsStream(resourceDir));
			Files.copy(resourceStream, destinationPath);
		}
		return destinationFile.getAbsolutePath();
	}

	public static void safelyShutDownApp() {
		CompletableFuture.runAsync(() -> Platform.runLater(() -> {
			var isOk = AppAlerts.showConfirmation(
				"¿Seguro que deseas cerrar el servicio de impresión?",
				"* Se dejaran de imprimir tickets."
			);
			if (isOk) {
				final Logger logger = (Logger) LoggerFactory.getLogger(AppUtil.makeNamespaceLogs("ShutdownApp"));
				try {
					// Delegar responsablidad al servidor http para que finalice su proceso y sus dependencias
					var posConfig = recoverPosConfigDefaultValues();
					String baseUrl = String.format("http://%s:%d", posConfig.getIp(), posConfig.getPort());
					var url = baseUrl + "/stop-service";
					var response = HttpUtil.get(url);
					if (response.getStatus().equals("success")) {
						logger.info("status success on stop-service");
					} else {
						logger.error("status error on stop-service:  {}", response.getMessage());
					}
				} catch (Exception e) {
					logger.error("Exception on exit safelyShutdownApp: {}", e.getMessage(), e);
				}
				// Liberar TrayIcon
				TrayIconServiceProvider.unLock();
				// Asegurar que se cierre todo
				Platform.exit();
				System.exit(0);
			}
		}));
	}

	public static Optional<URL> recoverLogoURL() throws Exception {
		var logoPath = AppUtil.getLogoFileDir().toString();
		File logoFile = new File(logoPath);
		if (logoFile.exists()) {
			return Optional.of(logoFile.toURI().toURL());
		}
		return Optional.empty();
	}

	public static void initTrayIcon(PrintServer server) {
		var config = new ConfigAppProperties();
		var uniqueProcess = config.uniqueProcess();
		if (uniqueProcess.isEmpty() || !uniqueProcess.get()) {
			return;
		}
		var trayIcon = TrayIconServiceProvider.instance();
		server.addListenerErrorNotification(trayIcon::showErrorMessage);
		server.addListenerInfoNotification(trayIcon::showInfoMessage);
		server.addListenerWarnNotification(trayIcon::showWarningMessage);
		trayIcon.show();
	}

}
