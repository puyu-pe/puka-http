package pe.puyu.pukahttp.infrastructure.javafx.injection;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class FxDependencyInjection {
    private static final Map<Class<?>, Supplier<Object>> controllerFactories = new HashMap<>();

    public static Parent load(String fxmlFileName) {
        try {
            FXMLLoader loader = getLoader(fxmlFileName);
            return loader.load();
        } catch (Exception e) {
            return new VBox(new Label(String.format("Error on load %s -> %s", fxmlFileName, e.getMessage())));
        }
    }

    public static FXMLLoader getLoader(String fxmlFileName) {
        String resourceLocation = "/pe/puyu/pukahttp/infrastructure/javafx/fxml/" + fxmlFileName;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FxDependencyInjection.class.getResource(resourceLocation));
        loader.setControllerFactory(FxDependencyInjection::loadController);
        return loader;
    }

    public static void addControllerFactory(Class<?> controllerClass, Supplier<Object> controllerFactory) {
        controllerFactories.put(controllerClass, controllerFactory);
    }

    public static void removeControllerFactory(Class<?> controllerClass) {
        controllerFactories.remove(controllerClass);
    }

    private static Object loadController(Class<?> controller) {
        try {
            if (controllerFactories.containsKey(controller)) {
                return controllerFactories.get(controller).get();
            } else {
                return controller.getConstructor().newInstance();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
