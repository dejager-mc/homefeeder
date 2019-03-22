package nl.dejagermc.homefeeder.schudulers;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.reporting.DotaReportBusinessService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Slf4j
public class DotaScheduler {

    private DotaReportBusinessService dotaReportBusinessService;

    @Inject
    public DotaScheduler(DotaReportBusinessService dotaReportBusinessService) {
        this.dotaReportBusinessService = dotaReportBusinessService;
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 60000)
    public void reportLiveMatches() {
        log.info("UC100: Scheduler: report live matches");
        dotaReportBusinessService.reportLiveMatchesFavoriteTeams();
    }

    @Scheduled(cron = "0 5 6 * * *")
    public void reportTodaysMatches() {
        log.info("UC101: Scheduler: report todays matches");
        dotaReportBusinessService.reportTodaysMatches();
    }

    @Scheduled(cron = "0 3 2 * * *")
    public void reloadTournamentData() {
        log.info("UC106: refresh tournament information");
        dotaReportBusinessService.refreshTournamentInformation();
    }
}
