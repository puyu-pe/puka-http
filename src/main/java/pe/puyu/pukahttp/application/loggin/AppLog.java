package pe.puyu.pukahttp.application.loggin;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

public class AppLog {
    private final Logger _logger;

    public AppLog(Class<?> clazz) {
        _logger = (Logger) LoggerFactory.getLogger(clazz);
    }

    public Logger getLogger() {
        return _logger;
    }

    public static void setErrorLevel(LogLevel level) {
        Logger rootLogger = (Logger) LoggerFactory.getLogger("pe.puyu.pukahttp");
        switch (level) {
            case ERROR:
                rootLogger.setLevel(Level.ERROR);
                break;
            case INFO:
                rootLogger.setLevel(Level.INFO);
                break;
            case WARN:
                rootLogger.setLevel(Level.WARN);
                break;
            case DEBUG:
                rootLogger.setLevel(Level.DEBUG);
                break;
            default:
                rootLogger.setLevel(Level.TRACE);
        }
    }

}
