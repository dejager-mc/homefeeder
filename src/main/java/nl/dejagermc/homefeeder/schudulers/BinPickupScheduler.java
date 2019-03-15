package nl.dejagermc.homefeeder.schudulers;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.reporting.BinPickupReportBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BinPickupScheduler {
    private BinPickupReportBusinessService binPickupReportBusinessService;

    @Autowired
    public BinPickupScheduler(BinPickupReportBusinessService binPickupReportBusinessService) {
        this.binPickupReportBusinessService = binPickupReportBusinessService;
    }

    @Scheduled(cron = "0 2 12-22 ? * *")
    public void scheduleBinPickupReporting() {
        log.info("UC001: Scheduler: reporting bin pickup next day");
        binPickupReportBusinessService.reportNextBinPickup();
    }
}
