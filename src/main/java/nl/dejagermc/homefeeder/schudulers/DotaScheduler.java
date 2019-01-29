package nl.dejagermc.homefeeder.schudulers;

import org.springframework.scheduling.annotation.Scheduled;

public class DotaScheduler {

    @Scheduled(fixedDelay = 60000, initialDelay = 60000)
    public void liveMatches() {
        // elke minuut de report live match functionaliteit aftrappen
    }
}
