package pe.puyu.pukahttp.infrastructure.javafx.injection;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class TrayIconDependencyInjection {

    public static final Map<Class<?>, Supplier<?>> controllerFactories = new HashMap<>();

    public static <T> T loadController(Class<T> clazz) {
        try {
            if (controllerFactories.containsKey(clazz)) {
                return clazz.cast(controllerFactories.get(clazz).get());
            } else {
                return clazz.getConstructor().newInstance();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> void registerController(Class<T> clazz, Supplier<T> factory) {
        controllerFactories.put(clazz, factory);
    }

}
