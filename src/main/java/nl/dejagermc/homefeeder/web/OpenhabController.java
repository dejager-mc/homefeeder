package nl.dejagermc.homefeeder.web;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.reporting.StatusReportService;
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

    private StatusReportService statusReportService;

    @Autowired
    public OpenhabController(SettingsService settingsService, StatusReportService statusReportService) {
        super(settingsService);
        this.statusReportService = statusReportService;
    }

    @GetMapping("/whatHappenedWhileIWasGoneReport")
    public ResponseEntity whatHappenedWhileIWasGoneReport() {
        statusReportService.whatHappenedWhileIWasGoneReport();
        return new ResponseEntity(HttpStatus.OK);
    }
}
