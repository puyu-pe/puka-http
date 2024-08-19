package pe.puyu.pukahttp.application.services;

import pe.puyu.pukahttp.domain.FailedPrintJobsStorage;
import pe.puyu.pukahttp.infrastructure.loggin.AppLog;

import java.time.LocalDateTime;

public class CleanPrintQueueService {
    private final AppLog log = new AppLog(CleanPrintQueueService.class);

    private final FailedPrintJobsStorage storage;

    public CleanPrintQueueService(FailedPrintJobsStorage storage) {
        this.storage = storage;
    }

    public void cleanPrintQueue() {
        LocalDateTime now = LocalDateTime.now();
        int CLEAN_TICKETS_TIME_HOURS = 5;
        LocalDateTime beforeTime = now.minusHours(CLEAN_TICKETS_TIME_HOURS);
        storage.deleteBefore(beforeTime);
        log.getLogger().info("Clean Print Queue Job release tickets from {} hours ago", CLEAN_TICKETS_TIME_HOURS);
    }
}
