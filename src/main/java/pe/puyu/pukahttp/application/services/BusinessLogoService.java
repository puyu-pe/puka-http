package pe.puyu.pukahttp.application.services;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class BusinessLogoService {

    private final Path logoFilePath;
    private final List<Consumer<URL>> logoObservers;

    public BusinessLogoService(Path logoFilePath) {
        this.logoFilePath = logoFilePath;
        this.logoObservers = new LinkedList<>();
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
        notifyChangesToLogoObservers(getLogoUrl());
    }

    public void save(byte[] bytes) throws IOException {
        Path tempFile = Files.createTempFile("logo_temp", ".png");
        try (OutputStream out = Files.newOutputStream(tempFile, StandardOpenOption.WRITE)) {
            out.write(bytes);
            Files.move(tempFile, logoFilePath, StandardCopyOption.REPLACE_EXISTING);
        }
        notifyChangesToLogoObservers(getLogoUrl());
    }

    public void addLogoObserver(Consumer<URL> callback) {
        this.logoObservers.add(callback);
    }

    public boolean existLogo() {
        return Files.exists(logoFilePath);
    }

    public URL getLogoUrl() throws MalformedURLException {
        return logoFilePath.toUri().toURL();
    }

    private void notifyChangesToLogoObservers(URL url) {
        for (Consumer<URL> observer : this.logoObservers) {
            observer.accept(url);
        }
    }
}
