package nl.dejagermc.homefeeder.schudulers;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.reporting.GarbageCollectionBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BinPickupScheduler {
    private GarbageCollectionBusinessService garbageCollectionBusinessService;

    @Autowired
    public BinPickupScheduler(GarbageCollectionBusinessService garbageCollectionBusinessService) {
        this.garbageCollectionBusinessService = garbageCollectionBusinessService;
    }

    @Scheduled(cron = "0 2 12-22 ? * *")
    public void scheduleBinPickupReporting() {
        log.info("UC001: Scheduler: reporting bin pickup next day");
        garbageCollectionBusinessService.reportNextBinPickup();
    }
}
