package nl.dejagermc.homefeeder.web;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.openhab.OpenhabBusinessService;
import nl.dejagermc.homefeeder.business.reporting.SummaryReportBusinessService;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("openhab")
@Slf4j
public class OpenhabController {

    private SummaryReportBusinessService summaryReportBusinessService;
    private OpenhabBusinessService openhabBusinessService;

    @Autowired
    public OpenhabController(SummaryReportBusinessService summaryReportBusinessService, OpenhabBusinessService openhabBusinessService) {
        this.summaryReportBusinessService = summaryReportBusinessService;
        this.openhabBusinessService = openhabBusinessService;
    }

    @GetMapping("/whatHappenedWhileIWasGoneReport")
    @ResponseStatus(HttpStatus.OK)
    public void whatHappenedWhileIWasGoneReport() {
        log.info("UC004: report saved reports");
        summaryReportBusinessService.reportSummaryToGoogleHome();
    }

    @GetMapping("/refreshItems")
    @ResponseStatus(HttpStatus.OK)
    public void refreshItems() {
        log.info("UC502: manual: refresh openhab items list.");
        openhabBusinessService.refreshItems();
    }
}
