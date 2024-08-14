package pe.puyu.pukahttp.infrastructure.config;

import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Objects;

public class AppConfig {

    public static String getUserDataDir() {
        AppDirs appDirs = AppDirsFactory.getInstance();
        String userDataDir = appDirs.getUserDataDir(AppConstants.APP_NAME, null, "puyu");
        File file = new File(userDataDir);
        if (!file.exists()) {
            var ignored = file.mkdirs();
        }
        return userDataDir;
    }

    public static String getLogsDirectory() {
        Path logsDirectory = Path.of(getUserDataDir(), "logs");
        return logsDirectory.toString();
    }

    public static Path getPropertiesDirectory() {
        Path propertiesDirectory = Path.of(getUserDataDir(), "config");
        File file = new File(propertiesDirectory.toString());
        if (!file.exists()) {
            var ignored = file.mkdirs();
        }
        return propertiesDirectory;
    }

    public static String getServerPropertiesPath() {
        return getPropertiesDirectory().resolve("server.ini").toString();
    }

    public static String getAppPropertiesPath() {
        return getPropertiesDirectory().resolve("app.ini").toString();
    }

    public static Path getLogoFilePath() {
        return Path.of(getUserDataDir(), "logo.png");
    }

    public static Path getStoragePath() {
        Path storagePath = Path.of(getUserDataDir(), "storage");
        File storage = new File(storagePath.toString());
        if (!storage.exists()) {
            var ignored = storage.mkdirs();
        }
        return storagePath;
    }

    public static boolean isProductionEnvironment() {
        return AppConfig.class.getResource("/PRODUCTION") != null;
    }

    public static String getEnv() {
        return isProductionEnvironment() ? "prod" : "beta";
    }

    public static String getAppVersion() {
        var isBeta = !isProductionEnvironment();
        var suffix = isBeta ? "-beta" : "";
        try {
            var resourceUrl = AppConfig.class.getResource("/VERSION");
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceUrl).openStream()));
            String version = reader.readLine();
            reader.close();
            return version + suffix;
        } catch (Exception e) {
            return "0.1.0" + suffix;
        }
    }

}
