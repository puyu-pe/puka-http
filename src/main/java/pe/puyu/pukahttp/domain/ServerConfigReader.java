package pe.puyu.pukahttp.domain;

import org.jetbrains.annotations.NotNull;

public interface ServerConfigReader {
    @NotNull
    Serverconfig read() throws ServerConfigException;

    void write(Serverconfig serverConfig) throws ServerConfigException;

    boolean hasServerConfig();
}
