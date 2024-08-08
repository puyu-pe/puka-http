package pe.puyu.pukahttp.infrastructure.javalin;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.plugin.bundled.CorsPluginConfig;
import org.jetbrains.annotations.NotNull;
import pe.puyu.pukahttp.domain.PrintServer;
import pe.puyu.pukahttp.domain.ServerConfigDTO;
import pe.puyu.pukahttp.infrastructure.javalin.injection.JavalinDependencyInjection;

import static io.javalin.apibuilder.ApiBuilder.path;

public class JavalinServer implements PrintServer {

    private final Javalin app;

    public JavalinServer() {
        JavalinConfig config = new JavalinConfig();
        config.plugins.enableCors(cors -> cors.add(CorsPluginConfig::anyHost));
        app = Javalin.create();
    }

    @Override
    public void start(@NotNull ServerConfigDTO serverConfig) {
        var printController = JavalinDependencyInjection.loadController(PrintController.class);
        app.routes(() -> {
            path("/", () -> {
            });
        });
    }

    @Override
    public void stop() {

    }

}
