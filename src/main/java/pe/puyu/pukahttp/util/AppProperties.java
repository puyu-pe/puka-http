package pe.puyu.pukahttp.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class AppProperties {
	private final String pathIniConfigFile;

	public AppProperties(String pathIniConfigFile){
		this.pathIniConfigFile = pathIniConfigFile;
	}

	public void set(String property, String value) throws Exception {
		var properties = loadProperties();
		properties.put(property, value);
		try (FileOutputStream out = new FileOutputStream(pathIniConfigFile)){
			properties.store(out, "/* properties updated */");
		}
	}

	public String get(String property) throws Exception{
		return loadProperties().getProperty(property);
	}

	private Properties loadProperties() throws  Exception{
		var properties = new Properties();
		try (var inputStream = new FileInputStream(pathIniConfigFile)) {
			properties.load(inputStream);
		}
		return properties;
	}
}
