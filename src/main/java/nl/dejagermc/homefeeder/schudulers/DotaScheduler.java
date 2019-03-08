package nl.dejagermc.homefeeder.schudulers;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.reporting.DotaReportBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DotaScheduler {

    private DotaReportBusinessService dotaReportBusinessService;

    @Autowired
    public DotaScheduler(DotaReportBusinessService dotaReportBusinessService) {
        this.dotaReportBusinessService = dotaReportBusinessService;
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 60000)
    public void reportLiveMatches() {
        log.info("Report live matches");
        dotaReportBusinessService.reportLiveMatchesFavoriteTeams();
    }

    @Scheduled(cron = "0 5 6 * * *")
    public void reportTodaysMatches() {
        dotaReportBusinessService.reportTodaysMatches();
    }
}
