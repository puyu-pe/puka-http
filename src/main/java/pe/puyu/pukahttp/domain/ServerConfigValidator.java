package pe.puyu.pukahttp.domain;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class ServerConfigValidator {

    private final Serverconfig serverConfig;

    public ServerConfigValidator(@NotNull Serverconfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public ServerConfigValidator(String service) {
        String ip = service;
        String port = "9100";
        if (service.contains(":")) {
            String[] split = service.split(":");
            if(split.length > 1) {
                ip = split[0];
                port = split[1];
            }
        }
        this.serverConfig = new Serverconfig(ip, port);
    }

    public void validateIp() throws DataValidationException {
        var ipPattern = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$|^localhost$", Pattern.MULTILINE);
        var ipMatcher = ipPattern.matcher(serverConfig.ip());
        if (!ipMatcher.matches()) {
            throw new DataValidationException(String.format("Invalid IP address: %s", serverConfig.ip()));
        }
    }

    public void validatePort() throws DataValidationException {
        var portPattern = Pattern.compile("^([0-9]|[1-9][0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$", Pattern.MULTILINE);
        var portMatcher = portPattern.matcher(serverConfig.port());
        if (!portMatcher.matches()) {
            throw new DataValidationException(String.format("Invalid port number: %s", serverConfig.port()));
        }
    }

}
