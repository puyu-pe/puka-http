package pe.puyu.pukahttp.util;

import javafx.animation.PauseTransition;
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
import pe.puyu.pukahttp.Constants;
import pe.puyu.pukahttp.app.properties.LogsDirectoryProperty;
import pe.puyu.pukahttp.model.PosConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

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

	public static String getUserConfigFileDir() throws IOException {
		return getConfigFileDir("user.json");
	}

	public static String getConfigFileDir(String jsonFileName) throws IOException {
		File file = new File(Path.of(getUserDataDir(), jsonFileName).toString());
		if (!file.exists()) {
			var ignored = file.createNewFile();
		}
		return file.getAbsolutePath();
	}

	public static String getPosConfigFileDir() throws Exception {
		return getConfigFileDir("pos.json");
	}

	public static String getDatabaseDirectory() {
		return getLookDirectory();
	}

	public static String getLogsDirectory() {
		return LogsDirectoryProperty.get().value();
	}

	public static String getAppVersion() {
		try {
			var resourceUrl = AppUtil.class.getResource("/VERSION");
			BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceUrl).openStream()));
			String version = reader.readLine();
			reader.close();
			return version;
		} catch (Exception e) {
			return "0.1.0";
		}
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

	public static PosConfig recoverPosConfig(){
		PosConfig config = new PosConfig();
		config.setIp(AppUtil.getHostIp());
		config.setPort(8175);
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

	public static SimpleDateFormat getDateTimeFormatter(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

}
