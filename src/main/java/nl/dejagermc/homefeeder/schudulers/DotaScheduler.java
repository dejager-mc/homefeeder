package nl.dejagermc.homefeeder.schudulers;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.MatchReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DotaScheduler {

    private MatchReportService matchReportService;

    @Autowired
    public DotaScheduler(MatchReportService matchReportService) {
        this.matchReportService = matchReportService;
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 60000)
    public void liveMatches() {
        matchReportService.reportLiveMatch();
    }
}
