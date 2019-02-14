package nl.dejagermc.homefeeder.schudulers;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.DotaReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DotaScheduler {

    private DotaReportService dotaReportService;

    @Autowired
    public DotaScheduler(DotaReportService dotaReportService) {
        this.dotaReportService = dotaReportService;
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 60000)
    public void liveMatches() {
        log.info("Report live matches");
        dotaReportService.reportLiveMatch();
    }

    @Scheduled(cron = "0 5 6 * * *")
    public void todaysMatches() {
        dotaReportService.reportTodaysMatches();
    }
}
