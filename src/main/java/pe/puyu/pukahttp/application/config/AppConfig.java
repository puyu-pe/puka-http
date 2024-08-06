package pe.puyu.pukahttp.application.config;

import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;
import pe.puyu.pukahttp.Constants;
import pe.puyu.pukahttp.util.AppUtil;

import java.io.File;
import java.nio.file.Path;

public class AppConfig {

    public static String getUserDataDir() {
        AppDirs appDirs = AppDirsFactory.getInstance();
        String userDataDir = appDirs.getUserDataDir(Constants.APP_NAME, null, "puyu");
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

    public static boolean isProductionEnvironment() {
        return AppConfig.class.getResource("/PRODUCTION") != null;
    }

    public static String getEnv() {
        return AppUtil.isProductionEnvironment() ? "prod" : "beta";
    }

}
