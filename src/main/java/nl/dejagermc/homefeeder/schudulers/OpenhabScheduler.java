package nl.dejagermc.homefeeder.schudulers;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.openhab.OpenhabInputService;
import nl.dejagermc.homefeeder.output.openhab.OpenhabOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OpenhabScheduler {

    private OpenhabOutput openhabOutput;
    private OpenhabInputService openhabInputService;
    private CacheManager cacheManager;

    @Autowired
    public OpenhabScheduler(OpenhabOutput openhabOutput, OpenhabInputService openhabInputService, CacheManager cacheManager) {
        this.openhabOutput = openhabOutput;
        this.openhabInputService = openhabInputService;
        this.cacheManager = cacheManager;
    }

    @Scheduled(fixedDelay = 300000, initialDelay = 5000)
    public void homefeederIsOnline() {
        openhabOutput.homefeederIsOnline();
    }

    @Scheduled(cron = "0 4 6 * * *")
    public void refreshThings() {
        cacheManager.getCache("getAllOpenhabThings").clear();
        openhabInputService.getAllOpenhabThings();
    }
}
