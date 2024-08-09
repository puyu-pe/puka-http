package pe.puyu.pukahttp.infrastructure.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import pe.puyu.pukahttp.application.loggin.AppLog;
import pe.puyu.pukahttp.domain.FailedPrintJobsStorage;
import pe.puyu.pukahttp.domain.models.PrintJob;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class GsonFailedPrintJobStorage implements FailedPrintJobsStorage {
    private final Path storagePath;
    private final AppLog log = new AppLog(GsonFailedPrintJobStorage.class);
    private final DateTimeFormatter createdAtFormatter = DateTimeFormatter.ofPattern("HH:mm_dd/MM");

    public GsonFailedPrintJobStorage(Path storagePath) {
        this.storagePath = storagePath;
    }

    @Override
    public void save(PrintJob printJob) {
        try {
            String fileName = String.format("%d-%s", printJob.id(), printJob.createdAt().format(createdAtFormatter));
            String filePath = this.storagePath.resolve(fileName).toString();
            var ignored = storagePath.getParent().toFile().mkdirs();
            try (FileWriter writer = new FileWriter(filePath)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                writer.write(gson.toJson(printJob.data()));
            }
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
                    String fileName = filePath.getFileName().toString();
                    String[] partFileName = fileName.split("-");
                    Long id = Long.parseLong(partFileName[0]);
                    LocalDateTime createdAt = LocalDateTime.parse(partFileName[1], createdAtFormatter);
                    String data = new String(Files.readAllBytes(Paths.get(filePath.toString())));
                    PrintJob printJob = new PrintJob(id, data, createdAt);
                    printJobs.add(printJob);
                } catch (IOException e) {
                    log.getLogger().warn(e.getMessage(), e);
                }
            });
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
        try{
            String fileName = String.format("%d-%s", printJob.id(), printJob.createdAt().format(createdAtFormatter));
            Path filePath = this.storagePath.resolve(fileName);
            Files.deleteIfExists(filePath);
        }catch (Exception e){
            log.getLogger().warn(e.getMessage(), e);
        }
    }

    @Override
    public void deleteBefore(LocalDateTime beforeTime) {
        List<PrintJob> printJobs = getAllPrintJobs();
        printJobs.stream()
            .filter(printJob -> printJob.createdAt().isBefore(beforeTime))
            .forEach(this::delete);
    }

    @Override
    public long countAll() {
        try (Stream<Path> files = Files.list(storagePath)) {
            return files.count();
        }catch (Exception e){
            return 0;
        }
    }

}
