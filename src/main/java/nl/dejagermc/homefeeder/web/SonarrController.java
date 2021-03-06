package nl.dejagermc.homefeeder.web;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.reporting.DownloadReportBusinessService;
import nl.dejagermc.homefeeder.domain.generated.sonarr.SonarrWebhookSchema;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("sonarr")
@Slf4j
public class SonarrController extends AbstractController {

    private DownloadReportBusinessService downloadReportBusinessService;

    @Autowired
    public SonarrController(SettingsService settingsService, DownloadReportBusinessService downloadReportBusinessService) {
        super(settingsService);
        this.downloadReportBusinessService = downloadReportBusinessService;
    }

    @PostMapping("add")
    public ResponseEntity addSonarr(@RequestBody SonarrWebhookSchema schema) {
        log.info("UC301: series download added");
        downloadReportBusinessService.reportSonarr(schema);
        return new ResponseEntity(HttpStatus.OK);
    }
}
