package pe.puyu.pukahttp.infrastructure;

import pe.puyu.pukahttp.application.config.AppConfig;
import pe.puyu.pukahttp.application.loggin.AppLog;
import pe.puyu.pukahttp.application.loggin.LogLevel;
import pe.puyu.pukahttp.infrastructure.javafx.app.JavaFXApplication;

public class AppLauncher {

    public static void main(String[] args) {
        _config_global_properties_();// IMPORTANT !!, first line execution
        AppLog.setErrorLevel(LogLevel.TRACE);
        JavaFXApplication.main(args);
    }

    public static void _config_global_properties_() {
        System.setProperty("logs.directory", AppConfig.getLogsDirectory());
        System.setProperty("app.env", AppConfig.getEnv());
    }

}
