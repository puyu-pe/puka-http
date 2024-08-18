
package pe.puyu.pukahttp.infrastructure.javalin.injection;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class JavalinDependencyInjection {

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

    public static <T> void addControllerFactory(Class<T> clazz, Supplier<T> factory) {
        controllerFactories.put(clazz, factory);
    }

}
