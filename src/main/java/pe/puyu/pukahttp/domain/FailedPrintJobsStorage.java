package pe.puyu.pukahttp.domain;

import pe.puyu.pukahttp.domain.models.PrintJob;

import java.time.LocalDateTime;
import java.util.List;

public interface FailedPrintJobsStorage {
    void save(PrintJob printJob);
    List<PrintJob> getAllPrintJobs();
    void deleteAll();
    void delete(PrintJob printJob);
    void deleteBefore(LocalDateTime beforeTime);
    long countAll();
}
