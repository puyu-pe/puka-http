package pe.puyu.pukahttp.application.services;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class BusinessLogoService {

    private final Path logoFilePath;

    public BusinessLogoService(Path folderPath) {
        this.logoFilePath = folderPath;
    }

    public void save(File fileToSave) throws IOException {
        if (!fileToSave.exists()) {
            throw new IOException(fileToSave.getAbsolutePath() + "doesn't exists");
        }
        Path newLogoFilePath = Path.of(fileToSave.toString());
        if (existLogo()) {
            Files.delete(logoFilePath);
        }
        Files.copy(newLogoFilePath, logoFilePath);
    }

    public boolean existLogo() {
        return Files.exists(logoFilePath);
    }


    public URL getLogoUrl() throws MalformedURLException {
        return logoFilePath.toUri().toURL();
    }

}
