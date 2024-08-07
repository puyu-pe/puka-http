package pe.puyu.pukahttp.infrastructure.javafx.views;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pe.puyu.pukahttp.domain.PngFileChooser;

import java.io.File;

public class FxPngFileChooser implements PngFileChooser {
    private final Stage parentStage;

    public FxPngFileChooser(Stage parent) {
        this.parentStage = parent;
    }


    @Override
    public File show() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(pngFilter);
        return fileChooser.showOpenDialog(parentStage);
    }
}
