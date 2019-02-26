package nl.dejagermc.homefeeder.web;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.reporting.DotaReportService;
import nl.dejagermc.homefeeder.business.reporting.StatusReportService;
import nl.dejagermc.homefeeder.user.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("openhab")
@Slf4j
public class OpenhabController extends AbstractController {

    private DotaReportService dotaReportService;
    private StatusReportService statusReportService;

    @Autowired
    public OpenhabController(UserState userState, DotaReportService dotaReportService, StatusReportService statusReportService) {
        super(userState);
        this.dotaReportService = dotaReportService;
        this.statusReportService = statusReportService;
    }

    @GetMapping("/userIsHome/{value}")
    public ResponseEntity userIsHome(@PathVariable boolean value) {
        userState.isHome(value);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/userIsSleeping/{value}")
    public ResponseEntity userIsSleeping(@PathVariable boolean value) {
        userState.isSleeping(value);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/mute/{value}")
    public ResponseEntity mute(@PathVariable boolean value) {
        userState.isMute(value);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/statusUpdate")
    public ResponseEntity statusUpdate() {
        statusReportService.statusUpdate();
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/giveDailyDotaReport")
    public ResponseEntity telegramDailyDota() {
        dotaReportService.reportTodaysMatches();
        return new ResponseEntity(HttpStatus.OK);
    }
}
