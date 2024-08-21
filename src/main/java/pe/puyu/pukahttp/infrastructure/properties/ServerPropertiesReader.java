package pe.puyu.pukahttp.infrastructure.properties;

import org.jetbrains.annotations.NotNull;
import pe.puyu.pukahttp.domain.ServerConfig;
import pe.puyu.pukahttp.domain.ServerConfigException;
import pe.puyu.pukahttp.domain.ServerConfigReader;

import java.io.*;
import java.util.Optional;
import java.util.Properties;

import pe.puyu.pukahttp.infrastructure.config.AppConfig;

public class ServerPropertiesReader implements ServerConfigReader {

    @Override
    public @NotNull ServerConfig read() throws ServerConfigException {
        Properties properties = loadProperties();
        String ip = Optional.ofNullable(properties.getProperty("ip")).orElse("");
        String port = Optional.ofNullable(properties.getProperty("port")).orElse("");
        return new ServerConfig(ip, port);
    }

    @Override
    public void write(ServerConfig serverConfig) throws ServerConfigException {
        String propertiesFilePath = AppConfig.getServerPropertiesPath();
        try (FileOutputStream out = new FileOutputStream(propertiesFilePath)) {
            Properties properties = loadProperties();
            properties.put("ip", serverConfig.ip());
            properties.put("port", serverConfig.port());
            properties.store(out, "/* properties updated */");
        } catch (IOException e) {
            throw new ServerConfigException("server properties file could not be written", e);
        }
    }

    @Override
    public boolean hasServerConfig() {
        String propertiesFilePath = AppConfig.getServerPropertiesPath();
        File file = new File(propertiesFilePath);
        return file.exists();
    }

    private Properties loadProperties() throws ServerConfigException {
        String propertiesFilePath = AppConfig.getServerPropertiesPath();
        var properties = new Properties();
        try (var inputStream = new FileInputStream(propertiesFilePath)) {
            properties.load(inputStream);
        } catch (IOException e) {
            String message = String.format("server properties %s not found", propertiesFilePath);
            throw new ServerConfigException(message, e);
        }
        return properties;
    }

}
