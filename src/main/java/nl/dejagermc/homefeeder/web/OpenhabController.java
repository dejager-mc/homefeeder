package nl.dejagermc.homefeeder.web;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.ReportService;
import nl.dejagermc.homefeeder.user.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("openhab")
@Slf4j
public class OpenhabController extends AbstractController {

    private ReportService reportService;

    @Autowired
    public OpenhabController(UserState userState, ReportService reportService) {
        super(userState);
        this.reportService = reportService;
    }

    @GetMapping("/userIsHome/{value}")
    public ResponseEntity userIsHome(@PathVariable boolean value) {
        userState.isHome(value);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/userIsAwake/{value}")
    public ResponseEntity userIsAwake(@PathVariable boolean value) {
        userState.isAwake(value);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/giveReport")
    public ResponseEntity voiceReport() {
        reportService.reportWhenArrivingAtHome();
        return new ResponseEntity(HttpStatus.OK);
    }
}
