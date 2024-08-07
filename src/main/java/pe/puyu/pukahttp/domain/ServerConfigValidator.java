package pe.puyu.pukahttp.domain;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class ServerConfigValidator {

    private final ServerConfigDTO serverConfig;

    public ServerConfigValidator(@NotNull ServerConfigDTO serverConfig) {
        this.serverConfig = serverConfig;
    }

    public void validateIp() throws ValidationException {
        var ipPattern = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$|^localhost$", Pattern.MULTILINE);
        var ipMatcher = ipPattern.matcher(serverConfig.ip());
        if (!ipMatcher.matches()) {
            throw new ValidationException(String.format("Invalid IP address: %s", serverConfig.ip()));
        }
    }

    public void validatePort() throws ValidationException {
        var portPattern = Pattern.compile("^([0-9]|[1-9][0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$", Pattern.MULTILINE);
        var portMatcher = portPattern.matcher(serverConfig.port());
        if (!portMatcher.matches()) {
            throw new ValidationException(String.format("Invalid port number: %s", serverConfig.port()));
        }
    }

}
