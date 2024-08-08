package pe.puyu.pukahttp.infrastructure.javalin.server;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.plugin.bundled.CorsPluginConfig;
import org.jetbrains.annotations.NotNull;
import pe.puyu.pukahttp.application.loggin.AppLog;
import pe.puyu.pukahttp.domain.PrintServer;
import pe.puyu.pukahttp.domain.PrintServerException;
import pe.puyu.pukahttp.domain.ServerConfigDTO;

public class JavalinPrintServer implements PrintServer {

    private final Javalin app;
    private final AppLog log = new AppLog(JavalinPrintServer.class);

    public JavalinPrintServer() {
        JavalinConfig config = new JavalinConfig();
        config.plugins.enableCors(cors -> cors.add(CorsPluginConfig::anyHost));
        app = Javalin.create();
    }

    @Override
    public void start(@NotNull ServerConfigDTO serverConfig) throws PrintServerException {
        try {
            Routes.config(app);
            int port = Integer.parseInt(serverConfig.port());
            app.start(serverConfig.ip(), port);
            log.getLogger().info("Sever listening on {}:{}", serverConfig.ip(), port);
        } catch (Exception e) {
            throw new PrintServerException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void stop() {

    }


}
