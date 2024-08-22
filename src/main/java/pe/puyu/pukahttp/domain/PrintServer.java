package pe.puyu.pukahttp.domain;

import org.jetbrains.annotations.NotNull;

public interface PrintServer {

    void start(@NotNull ServerConfig serverConfig) throws PrintServerException;

    void stop();

    boolean isStarted();
}
