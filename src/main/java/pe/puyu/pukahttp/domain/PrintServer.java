package pe.puyu.pukahttp.domain;

import org.jetbrains.annotations.NotNull;

public interface PrintServer {

    void start(@NotNull ServerConfigDTO serverConfig);

    void stop();
}
