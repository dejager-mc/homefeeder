package nl.dejagermc.homefeeder.web;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.reporting.DownloadReportService;
import nl.dejagermc.homefeeder.domain.generated.radarr.RadarrWebhookSchema;
import nl.dejagermc.homefeeder.input.homefeeder.model.HomeFeederState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("radarr")
@Slf4j
public class RadarrController extends AbstractController {

    private DownloadReportService downloadReportService;

    @Autowired
    public RadarrController(HomeFeederState homeFeederState, DownloadReportService downloadReportService) {
        super(homeFeederState);
        this.downloadReportService = downloadReportService;
    }

    @PostMapping("add")
    public ResponseEntity addRadarr(@RequestBody RadarrWebhookSchema schema) {
        downloadReportService.reportRadarr(schema);
        return new ResponseEntity(HttpStatus.OK);
    }
}
