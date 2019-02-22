package nl.dejagermc.homefeeder.web;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.reporting.DownloadReportService;
import nl.dejagermc.homefeeder.domain.generated.sonarr.SonarrWebhookSchema;
import nl.dejagermc.homefeeder.user.UserState;
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

    private DownloadReportService downloadReportService;

    @Autowired
    public SonarrController(UserState userState, DownloadReportService downloadReportService) {
        super(userState);
        this.downloadReportService = downloadReportService;
    }

    @PostMapping("add")
    public ResponseEntity addSonarr(@RequestBody SonarrWebhookSchema schema) {
        downloadReportService.reportSonarr(schema);
        return new ResponseEntity(HttpStatus.OK);
    }
}
