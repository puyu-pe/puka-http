package pe.puyu.pukahttp.infrastructure.properties;

import org.jetbrains.annotations.NotNull;
import pe.puyu.pukahttp.infrastructure.loggin.AppLog;
import pe.puyu.pukahttp.infrastructure.config.AppConfig;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class ApplicationProperties {

    public static void setBoolean(@NotNull AppPropertyKey key, boolean value) {
        set(key, Boolean.toString(value));
    }

    public static void setString(@NotNull AppPropertyKey key, @NotNull String value) {
        set(key, value);
    }

    public static void setInt(@NotNull AppPropertyKey key, int value) {
        set(key, String.valueOf(value));
    }

    public static boolean getBoolean(@NotNull AppPropertyKey key, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(loadProperties().getProperty(key.toString()));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String getString(@NotNull AppPropertyKey key, @NotNull String defaultValue) {
        try {
            return loadProperties().getProperty(key.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static int getInt(@NotNull AppPropertyKey key, int defaultValue) {
        try {
            return Integer.parseInt(loadProperties().getProperty(key.toString()));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static boolean has(@NotNull AppPropertyKey key) {
        return loadProperties().containsKey(key.toString());
    }

    private static void set(@NotNull AppPropertyKey key, @NotNull String value) {
        String appPropertiesFilePath = AppConfig.getAppPropertiesPath();
        Properties properties = loadProperties();
        properties.put(key.toString(), value);
        try (FileOutputStream out = new FileOutputStream(appPropertiesFilePath)) {
            properties.store(out, "/* properties updated */");
        } catch (Exception e) {
            AppLog log = new AppLog(ApplicationProperties.class);
            log.getLogger().error("Error on set {} with message: {}", key.toString(), e.getMessage());
        }
    }

    private static Properties loadProperties() {
        String appPropertiesFilePath = AppConfig.getAppPropertiesPath();
        var properties = new Properties();
        try (var inputStream = new FileInputStream(appPropertiesFilePath)) {
            properties.load(inputStream);
        } catch (Exception e) {
            AppLog log = new AppLog(ApplicationProperties.class);
            log.getLogger().error("Server properties {} not found", appPropertiesFilePath);
        }
        return properties;
    }

}
