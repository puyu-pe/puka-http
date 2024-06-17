package pe.puyu.pukahttp.app.properties;

import pe.puyu.pukahttp.util.AppUtil;

import java.nio.file.Path;

public class LogsDirectoryProperty {

	private final String key, value;

	private LogsDirectoryProperty(String key, String value){
		this.key = key;
		this.value = value;
	}

	public static LogsDirectoryProperty get() {
		Path logsDirectory = Path.of(AppUtil.getUserDataDir(),"logs");
		return new LogsDirectoryProperty("logs.directory", logsDirectory.toString());
	}

	public String key(){
		return this.key;
	}

	public String value(){
		return this.value;
	}
}
