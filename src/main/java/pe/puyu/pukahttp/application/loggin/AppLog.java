package pe.puyu.pukahttp.application.loggin;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

public class AppLog {
    private final Logger _logger;

    public AppLog(Class<?> clazz) {
        _logger = (Logger) LoggerFactory.getLogger(clazz);
        _logger.setLevel(Level.TRACE);
    }

    public Logger getLogger() {
        return _logger;
    }
}
