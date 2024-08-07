package pe.puyu.pukahttp.application.services;

import javafx.application.Platform;
import pe.puyu.pukahttp.domain.*;

public class PrintService {
    private final PrintServer printServer;
    private final ServerConfigReader configReader;

    public PrintService(PrintServer printServer, ServerConfigReader configReader) {
        this.printServer = printServer;
        this.configReader = configReader;
    }

    public void start() throws ServerConfigException, ValidationException {
        ServerConfigDTO serverConfig = getServerConfig();
        printServer.start(serverConfig);
    }

    public void stop() {
        this.printServer.stop();
    }

    public boolean existServerConfig() {
        return configReader.hasServerConfig();
    }

    public ServerConfigDTO getServerConfig() throws ServerConfigException, ValidationException {
        ServerConfigDTO serverConfig = configReader.read();
        ServerConfigValidator validator = new ServerConfigValidator(serverConfig);
        validator.validateIp();
        validator.validatePort();
        return serverConfig;
    }

    public void saveServerConfig(ServerConfigDTO serverConfig) throws ServerConfigException, ValidationException {
        ServerConfigValidator validator = new ServerConfigValidator(serverConfig);
        validator.validateIp();
        validator.validatePort();
        configReader.write(serverConfig);
    }

}
