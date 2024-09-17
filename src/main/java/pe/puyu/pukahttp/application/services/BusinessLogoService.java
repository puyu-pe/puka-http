package pe.puyu.pukahttp.application.services;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

public class BusinessLogoService {

    private final Path logoFilePath;

    public BusinessLogoService(Path logoFilePath) {
        this.logoFilePath = logoFilePath;
    }

    public void save(File fileToSave) throws IOException {
        if (!fileToSave.exists()) {
            throw new IOException(fileToSave.getAbsolutePath() + "doesn't exists");
        }
        Path newLogoFilePath = Path.of(fileToSave.toString());
        Files.move(newLogoFilePath, logoFilePath, StandardCopyOption.REPLACE_EXISTING);
    }

    public void save(byte[] bytes) throws IOException {
        Path tempFile = Files.createTempFile("logo_temp", ".png");
        try (OutputStream out = Files.newOutputStream(tempFile, StandardOpenOption.WRITE)) {
            out.write(bytes);
            Files.move(tempFile, logoFilePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public boolean existLogo() {
        return Files.exists(logoFilePath);
    }


    public URL getLogoUrl() throws MalformedURLException {
        return logoFilePath.toUri().toURL();
    }

}
