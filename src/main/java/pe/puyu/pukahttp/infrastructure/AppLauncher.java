package pe.puyu.pukahttp.infrastructure;

import pe.puyu.pukahttp.application.config.AppConfig;
import pe.puyu.pukahttp.infrastructure.javafx.app.JavaFXApplication;

public class AppLauncher {

    public static void main(String[] args) {
        System.setProperty("logs.directory", AppConfig.getLogsDirectory());
        System.setProperty("app.env", AppConfig.getEnv());
        JavaFXApplication.main(args);
    }

}
