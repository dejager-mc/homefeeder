package nl.dejagermc.homefeeder.web;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.reporting.StatusReportBusinessService;
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

    private StatusReportBusinessService statusReportBusinessService;

    @Autowired
    public OpenhabController(SettingsService settingsService, StatusReportBusinessService statusReportBusinessService) {
        super(settingsService);
        this.statusReportBusinessService = statusReportBusinessService;
    }

    @GetMapping("/whatHappenedWhileIWasGoneReport")
    public ResponseEntity whatHappenedWhileIWasGoneReport() {
        statusReportBusinessService.reportSavedMessagesToGoogleHome();
        return new ResponseEntity(HttpStatus.OK);
    }
}
