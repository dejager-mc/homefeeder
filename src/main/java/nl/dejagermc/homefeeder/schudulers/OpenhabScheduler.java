package nl.dejagermc.homefeeder.schudulers;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.openhab.OpenhabBusinessService;
import nl.dejagermc.homefeeder.output.openhab.OpenhabOutputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OpenhabScheduler {

    private OpenhabOutputService openhabOutputService;
    private OpenhabBusinessService openhabBusinessService;

    @Autowired
    public OpenhabScheduler(OpenhabOutputService openhabOutputService, OpenhabBusinessService openhabBusinessService) {
        this.openhabOutputService = openhabOutputService;
        this.openhabBusinessService = openhabBusinessService;
    }

    @Scheduled(fixedDelay = 300000, initialDelay = 5000)
    public void homefeederIsOnline() {
        log.info("UC500: scheduler: report home feeder is online to openhab.");
        openhabOutputService.homefeederIsOnline();
    }

    @Scheduled(cron = "0 4 6 * * *")
    public void refreshItems() {
        log.info("UC502: scheduler: refresh items.");
        openhabBusinessService.refreshItems();
    }

    public void giveReportWhenUserStartsListening() {

    }
}
