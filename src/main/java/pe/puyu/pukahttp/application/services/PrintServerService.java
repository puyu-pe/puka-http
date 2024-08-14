package pe.puyu.pukahttp.application.services;

import pe.puyu.pukahttp.application.notifier.AppNotifier;
import pe.puyu.pukahttp.domain.*;

public class PrintServerService {
    private final PrintServer printServer;
    private final ServerConfigReader configReader;
    private final AppNotifier notifier;

    public PrintServerService(PrintServer printServer, ServerConfigReader configReader, AppNotifier notifier) {
        this.printServer = printServer;
        this.configReader = configReader;
        this.notifier = notifier;
    }

    public void start() throws ServerConfigException, DataValidationException, PrintServerException {
        ServerConfigDTO serverConfig = getServerConfig();
        printServer.start(serverConfig);
        notifier.info(String.format("Server started on %s:%s", serverConfig.ip(), serverConfig.port()));
    }

    public void stop() {
        this.printServer.stop();
        notifier.info("The printing service has been stopped.");
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
