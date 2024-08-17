package pe.puyu.pukahttp.infrastructure;

import pe.puyu.pukahttp.infrastructure.config.AppConfig;
import pe.puyu.pukahttp.infrastructure.javafx.app.JavaFXApplication;
import pe.puyu.pukahttp.infrastructure.loggin.AppLog;
import pe.puyu.pukahttp.infrastructure.loggin.LogLevel;
import pe.puyu.pukahttp.infrastructure.properties.AppPropertyKey;
import pe.puyu.pukahttp.infrastructure.properties.ApplicationProperties;

public class AppLauncher {

    public static void main(String[] args) {
        _config_global_properties_();// IMPORTANT !!, first line execution
        _config_app_properties_();
        JavaFXApplication.main(args);
    }

    private static void _config_global_properties_() {
        System.setProperty("logs.directory", AppConfig.getLogsDirectory());
        System.setProperty("app.env", AppConfig.getEnv());
    }

    private static void _config_app_properties_() {
        // config App Log
        AppLog.setErrorLevel(LogLevel.fromValue(ApplicationProperties.getString(AppPropertyKey.LOG_LEVEL, LogLevel.INFO.getValue())));
        // config support TrayIcon
        if (!ApplicationProperties.has(AppPropertyKey.TRAY_SUPPORT)) {
            String os = System.getProperty("os.name").toLowerCase();
            ApplicationProperties.setBoolean(AppPropertyKey.TRAY_SUPPORT, os.contains("win") || os.contains("mac"));
        }
        if(!ApplicationProperties.has(AppPropertyKey.TRAY_NOTIFICATIONS)) {
            ApplicationProperties.setBoolean(AppPropertyKey.TRAY_NOTIFICATIONS, true);
        }
        //...
    }

}
