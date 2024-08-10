package pe.puyu.pukahttp.application.services;

import pe.puyu.pukahttp.domain.*;

public class PrintServerService {
    private final PrintServer printServer;
    private final ServerConfigReader configReader;

    public PrintServerService(PrintServer printServer, ServerConfigReader configReader) {
        this.printServer = printServer;
        this.configReader = configReader;
    }

    public void start() throws ServerConfigException, DataValidationException, PrintServerException {
        ServerConfigDTO serverConfig = getServerConfig();
        printServer.start(serverConfig);
    }

    public void stop() {
        this.printServer.stop();
    }

    public boolean existServerConfig() {
        return configReader.hasServerConfig();
    }

    public ServerConfigDTO getServerConfig() throws ServerConfigException, DataValidationException {
        ServerConfigDTO serverConfig = configReader.read();
        ServerConfigValidator validator = new ServerConfigValidator(serverConfig);
        validator.validateIp();
        validator.validatePort();
        return serverConfig;
    }

    public void saveServerConfig(ServerConfigDTO serverConfig) throws ServerConfigException, DataValidationException {
        ServerConfigValidator validator = new ServerConfigValidator(serverConfig);
        validator.validateIp();
        validator.validatePort();
        configReader.write(serverConfig);
    }

}
