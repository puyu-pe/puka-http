package pe.puyu.pukahttp.app.properties;

import pe.puyu.pukahttp.util.AppUtil;

import java.nio.file.Path;

public record LogsDirectoryProperty(String key, String value) {
	public static LogsDirectoryProperty get() {
		var logsDirectory = Path.of(AppUtil.getUserDataDir(),"logs");
		return new LogsDirectoryProperty("logs.directory", logsDirectory.toString());
	}
}
