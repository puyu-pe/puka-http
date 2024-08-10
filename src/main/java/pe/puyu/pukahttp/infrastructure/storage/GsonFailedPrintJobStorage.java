package pe.puyu.pukahttp.infrastructure.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import pe.puyu.pukahttp.application.loggin.AppLog;
import pe.puyu.pukahttp.domain.FailedPrintJobsStorage;
import pe.puyu.pukahttp.domain.QueueObservable;
import pe.puyu.pukahttp.domain.models.PrintInfo;
import pe.puyu.pukahttp.domain.models.PrintJob;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class GsonFailedPrintJobStorage extends QueueObservable implements FailedPrintJobsStorage {

    private final Path storagePath;
    private final AppLog log = new AppLog(GsonFailedPrintJobStorage.class);
    private final GsonBuilder gsonBuilder;

    public GsonFailedPrintJobStorage(Path storagePath) {
        this.storagePath = storagePath;
        gsonBuilder = new GsonBuilder()
            .registerTypeAdapter(PrintJob.class, new PrintJobSerializer())
            .setPrettyPrinting();
    }

    @Override
    public void save(PrintJob printJob) {
        try {
            String fileName = ConfigGsonStorage.makeFileNameTo(LocalDateTime.now(), printJob.id());
            Path filePath = this.storagePath.resolve(fileName);
            Gson gson = gsonBuilder.create();
            try (FileWriter writer = new FileWriter(filePath.toString())) {
                writer.write(gson.toJson(printJob));
            }
            notifyQueueSize();
        } catch (Exception e) {
            log.getLogger().warn(e.getMessage(), e);
        }
    }

    @Override
    public List<PrintJob> getAllPrintJobs() {
        try (Stream<Path> files = Files.list(this.storagePath)) {
            List<PrintJob> printJobs = new LinkedList<>();
            files.forEach(filePath -> {
                try {
                    String data = new String(Files.readAllBytes(Paths.get(filePath.toString())));
                    Gson gson = gsonBuilder.create();
                    PrintJob printJob = gson.fromJson(data, PrintJob.class);
                    printJobs.add(printJob);
                } catch (IOException e) {
                    log.getLogger().warn(e.getMessage(), e);
                }
            });
            notifyQueueSize();
            return printJobs;
        } catch (Exception e) {
            log.getLogger().warn(e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public void deleteAll() {
        try (Stream<Path> files = Files.list(storagePath)) {
            files.forEach(filePath -> {
                try {
                    Files.deleteIfExists(filePath);
                    notifyQueueSize();
                } catch (IOException e) {
                    log.getLogger().warn(e.getMessage(), e);
                }
            });
        } catch (Exception e) {
            log.getLogger().warn(e.getMessage(), e);
        }
    }

    @Override
    public void delete(PrintJob printJob) {
        try (Stream<Path> files = Files.list(this.storagePath)) {
            List<Path> filesList = files.toList();
            for (Path filePath : filesList) {
                String fileName = filePath.getFileName().toString().replace(".json", "");
                String[] split = fileName.split("-");
                String id = split[0];
                if (id.equals(printJob.id())) {
                    Files.deleteIfExists(filePath);
                    break;
                }
            }
            notifyQueueSize();
        } catch (Exception e) {
            log.getLogger().warn(e.getMessage(), e);
        }
    }

    @Override
    public void deleteBefore(LocalDateTime beforeTime) {
        try (Stream<Path> files = Files.list(this.storagePath)) {
            List<Path> filesList = files.toList();
            for (Path filePath : filesList) {
                String fileName = filePath.getFileName().toString().replace(".json", "");
                String[] split = fileName.split("-");
                String date = split[1];
                LocalDateTime createdAt = LocalDateTime.parse(date);
                if (createdAt.isBefore(beforeTime)) {
                    Files.deleteIfExists(filePath);
                }
            }
        } catch (Exception e) {
            log.getLogger().warn(e.getMessage(), e);
        }
    }

    @Override
    public long getQueueSize() {
        try (Stream<Path> files = Files.list(storagePath)) {
            return files.count();
        } catch (Exception e) {
            return 0;
        }
    }
}
