package pe.puyu.pukahttp.domain;

import org.jetbrains.annotations.NotNull;

public interface ServerConfigReader {
    @NotNull
    ServerConfig read() throws ServerConfigException;

    void write(ServerConfig serverConfig) throws ServerConfigException;

    boolean hasServerConfig();
}
