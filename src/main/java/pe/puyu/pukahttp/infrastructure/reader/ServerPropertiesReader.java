package pe.puyu.pukahttp.infrastructure.reader;

import org.jetbrains.annotations.NotNull;
import pe.puyu.pukahttp.domain.ServerConfigDTO;
import pe.puyu.pukahttp.domain.ServerConfigException;
import pe.puyu.pukahttp.domain.ServerConfigReader;

import java.io.*;
import java.util.Optional;
import java.util.Properties;

public class ServerPropertiesReader implements ServerConfigReader {
    private final String propertiesFilePath;

    public ServerPropertiesReader(String propertiesFilePath) {
        this.propertiesFilePath = propertiesFilePath;
    }

    @Override
    public @NotNull ServerConfigDTO read() throws ServerConfigException {
        Properties properties = loadProperties();
        String ip = Optional.ofNullable(properties.getProperty("ip")).orElse("");
        String port = Optional.ofNullable(properties.getProperty("port")).orElse("");
        return new ServerConfigDTO(ip, port);
    }

    @Override
    public void write(ServerConfigDTO serverConfig) throws ServerConfigException {
        Properties properties = loadProperties();
        try (FileOutputStream out = new FileOutputStream(propertiesFilePath)) {
            properties.store(out, "/* properties updated */");
        }catch (IOException e){
            throw new ServerConfigException("server properties file could not be written", e);
        }
    }

    @Override
    public boolean hasServerConfig() {
        File file = new File(propertiesFilePath);
        return file.exists();
    }

    private Properties loadProperties() throws ServerConfigException {
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
