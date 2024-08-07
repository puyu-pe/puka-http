package pe.puyu.pukahttp.domain;

import org.jetbrains.annotations.NotNull;

public interface ServerConfigReader {
    @NotNull
    ServerConfigDTO read() throws ServerConfigException;

    void write(ServerConfigDTO serverConfig) throws ServerConfigException;

    boolean hasServerConfig();
}
