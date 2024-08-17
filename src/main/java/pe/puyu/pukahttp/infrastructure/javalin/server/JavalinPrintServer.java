package pe.puyu.pukahttp.infrastructure.javalin.server;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.redoc.ReDocPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import io.javalin.plugin.bundled.CorsPluginConfig;
import org.jetbrains.annotations.NotNull;
import pe.puyu.pukahttp.infrastructure.config.AppConfig;
import pe.puyu.pukahttp.infrastructure.loggin.AppLog;
import pe.puyu.pukahttp.application.services.printjob.PrintJobException;
import pe.puyu.pukahttp.application.services.printjob.PrintServiceNotFoundException;
import pe.puyu.pukahttp.domain.PrintServer;
import pe.puyu.pukahttp.domain.PrintServerException;
import pe.puyu.pukahttp.domain.ServerConfigDTO;
import pe.puyu.pukahttp.domain.DataValidationException;

public class JavalinPrintServer implements PrintServer {
    private Javalin app = null;
    private final AppLog log = new AppLog(JavalinPrintServer.class);

    @Override
    public void start(@NotNull ServerConfigDTO serverConfig) throws PrintServerException {
        if (app == null) {
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
        if (app != null) {
            app.stop();
            app = null;
            log.getLogger().info("Print server was stopped.");
        }
    }

    @Override
    public boolean isStarted() {
        return app != null;
    }

    private void initializeApp() {
        app = Javalin.create(this::serverConfig);
        app.exception(Exception.class, JavalinErrorHandling::generic);
        app.exception(PrintServiceNotFoundException.class, JavalinErrorHandling::printServiceNotFoundExceptionHandler);
        app.exception(PrintJobException.class, JavalinErrorHandling::printJobExceptionHandler);
        app.exception(DataValidationException.class, JavalinErrorHandling::validationExceptionHandler);
    }

    private void serverConfig(JavalinConfig config) {
        config.http.asyncTimeout = 20000;
        config.registerPlugin(new OpenApiPlugin(pluginConfig -> {
            pluginConfig.withDefinitionConfiguration((version, definition) -> {
                definition.withInfo(info -> {
                    info.title("Puka HTTP OpenAPI");
                    info.version(AppConfig.getAppVersion());
                    info.contact("PUYU");
                    info.description("Api for print service");
                    info.license("Apache 2.0", "https://www.apache.org/licenses/", "Apache-2.0");
                });
            });
        }));
        config.registerPlugin(new SwaggerPlugin());
        config.registerPlugin(new ReDocPlugin());
        config.bundledPlugins.enableCors(cors -> cors.addRule(CorsPluginConfig.CorsRule::anyHost));
        config.router.apiBuilder(Routes::group);
    }

}
