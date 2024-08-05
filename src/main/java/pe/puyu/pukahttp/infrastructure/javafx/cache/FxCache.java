package pe.puyu.pukahttp.infrastructure.javafx.cache;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import pe.puyu.pukahttp.infrastructure.javafx.loader.FxLoader;

import java.util.HashMap;
import java.util.Map;

public class FxCache {

    private final static Map<String, Parent> cache = new HashMap<>();

    public static Parent get(String fxmlFileName) {
        try {
            if (!cache.containsKey(fxmlFileName)) {
                cache.put(fxmlFileName, FxLoader.load(fxmlFileName));
            }
            return cache.get(fxmlFileName);
        } catch (Exception e) {
            //TODO: call logger
            return new VBox(new Label(e.getMessage()));
        }
    }

}
