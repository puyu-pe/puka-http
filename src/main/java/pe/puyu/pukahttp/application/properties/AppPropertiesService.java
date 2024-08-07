package pe.puyu.pukahttp.application.properties;

import pe.puyu.pukahttp.application.config.AppConfig;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.Properties;

public class AppPropertiesService {

    private final String pathIniConfigFile;

    public AppPropertiesService(String configFileName) {
        this.pathIniConfigFile = Path.of(AppConfig.getConfigDirectory(), configFileName).toString();
    }

    public void set(String property, String value) throws Exception {
        var properties = loadProperties();
        properties.put(property, value);
        try (FileOutputStream out = new FileOutputStream(pathIniConfigFile)) {
            properties.store(out, "/* properties updated */");
        }
    }

    public String get(String property) throws Exception {
        return loadProperties().getProperty(property);
    }

    private Properties loadProperties() throws Exception {
        var properties = new Properties();
        try (var inputStream = new FileInputStream(pathIniConfigFile)) {
            properties.load(inputStream);
        }
        return properties;
    }
}
