package nl.dejagermc.homefeeder.web;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.reporting.SummaryReportBusinessService;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("openhab")
@Slf4j
public class OpenhabController extends AbstractController {

    private SummaryReportBusinessService summaryReportBusinessService;

    @Autowired
    public OpenhabController(SettingsService settingsService, SummaryReportBusinessService summaryReportBusinessService) {
        super(settingsService);
        this.summaryReportBusinessService = summaryReportBusinessService;
    }

    @GetMapping("/whatHappenedWhileIWasGoneReport")
    public ResponseEntity whatHappenedWhileIWasGoneReport() {
        summaryReportBusinessService.reportSummaryToGoogleHome();
        return new ResponseEntity(HttpStatus.OK);
    }
}
