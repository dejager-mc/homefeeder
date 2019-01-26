package nl.dejagermc.homefeeder.web;

import nl.dejagermc.homefeeder.user.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("openhab")
public class OpenhabController {

    private UserState userState;

    @Autowired
    public OpenhabController(UserState userState) {
        this.userState = userState;
    }

    @GetMapping("/userIsHome/{value}")
    public ResponseEntity userIsHome(@PathVariable boolean value) {
        userState.isHome(value);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/voiceReport")
    public ResponseEntity voiceReport() {
        return new ResponseEntity(HttpStatus.OK);
    }
}
