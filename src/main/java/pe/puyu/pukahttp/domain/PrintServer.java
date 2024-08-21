package pe.puyu.pukahttp.domain;

import org.jetbrains.annotations.NotNull;

public interface PrintServer {

    void start(@NotNull Serverconfig serverConfig) throws PrintServerException;

    void stop();

    boolean isStarted();
}
