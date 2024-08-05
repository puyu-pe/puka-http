package pe.puyu.pukahttp.infrastructure.javafx.loader;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class FxLoader {

    public static @NotNull Parent load(@NotNull String fxmlFileName) throws IOException {
        String resourcePath = "/pe/puyu/pukahttp/infrastructure/javafx/fxml/" + fxmlFileName;
        Optional<URL> urlToFxmlResource = Optional.ofNullable(FxLoader.class.getResource(resourcePath));
        if (urlToFxmlResource.isPresent())
            return FXMLLoader.load(urlToFxmlResource.get());
        throw new RuntimeException(String.format("FXML Resource %s not found", resourcePath));
    }

}
