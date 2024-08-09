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

    private Javalin app = null;
    private final AppLog log = new AppLog(JavalinPrintServer.class);

    @Override
    public void start(@NotNull ServerConfigDTO serverConfig) throws PrintServerException {
        if(app == null){
            try {
                initializeApp();
                int port = Integer.parseInt(serverConfig.port());
                app.start(serverConfig.ip(), port);
                log.getLogger().info("Sever listening on {}:{}", serverConfig.ip(), port);
            } catch (Exception e) {
                throw new PrintServerException(e.getMessage(), e.getCause());
            }
        }

    }

    @Override
    public void stop() {
        if(app != null){
            app.stop();
            app = null;
        }
    }

    private void initializeApp(){
        app = Javalin.create(this::serverConfig);
        Routes.config(app);
        app.exception(Exception.class, JavalinErrorHandling::generic);
    }

    private void serverConfig(JavalinConfig config){
        config.plugins.enableCors(cors -> cors.add(CorsPluginConfig::anyHost));
    }

}
