package nl.dejagermc.homefeeder.web;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.MatchReportService;
import nl.dejagermc.homefeeder.business.StatusReportService;
import nl.dejagermc.homefeeder.user.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("openhab")
@Slf4j
public class OpenhabController extends AbstractController {

    private MatchReportService matchReportService;
    private StatusReportService statusReportService;

    @Autowired
    public OpenhabController(UserState userState, MatchReportService matchReportService, StatusReportService statusReportService) {
        super(userState);
        this.matchReportService = matchReportService;
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
        matchReportService.reportWhenArrivingAtHome();
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/giveDailyDotaReport")
    public ResponseEntity telegramDailyDota() {
        matchReportService.reportTodaysMatches();
        return new ResponseEntity(HttpStatus.OK);
    }
}
