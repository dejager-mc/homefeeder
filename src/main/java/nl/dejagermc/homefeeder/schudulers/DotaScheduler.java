package nl.dejagermc.homefeeder.schudulers;

import nl.dejagermc.homefeeder.business.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class DotaScheduler {

    private ReportService reportService;

    @Autowired
    public DotaScheduler(ReportService reportService) {
        this.reportService = reportService;
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 60000)
    public void liveMatches() {
        // elke minuut de report live match functionaliteit aftrappen
        reportService.reportLiveMatchToTelegram();
    }
}
