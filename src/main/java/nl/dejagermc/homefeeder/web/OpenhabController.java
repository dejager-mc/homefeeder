package nl.dejagermc.homefeeder.web;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.reporting.StatusReportService;
import nl.dejagermc.homefeeder.input.homefeeder.model.HomeFeederState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("openhab")
@Slf4j
public class OpenhabController extends AbstractController {

    private StatusReportService statusReportService;

    @Autowired
    public OpenhabController(HomeFeederState homeFeederState, StatusReportService statusReportService) {
        super(homeFeederState);
        this.statusReportService = statusReportService;
    }

    @GetMapping("/userIsHome/{value}")
    public ResponseEntity userIsHome(@PathVariable boolean value) {
        homeFeederState.isHome(value);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/userIsSleeping/{value}")
    @ResponseStatus(HttpStatus.OK)
    public void userIsSleeping(@PathVariable boolean value) {
        homeFeederState.isSleeping(value);
    }

    @GetMapping("/mute/{value}")
    @ResponseStatus(HttpStatus.OK)
    public void mute(@PathVariable boolean value) {
        homeFeederState.isMute(value);
    }

    @GetMapping("/whatHappenedWhileIWasGoneReport")
    public ResponseEntity whatHappenedWhileIWasGoneReport() {
        statusReportService.whatHappenedWhileIWasGoneReport();
        return new ResponseEntity(HttpStatus.OK);
    }
}
