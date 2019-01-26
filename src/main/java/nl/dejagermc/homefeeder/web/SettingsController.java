package nl.dejagermc.homefeeder.web;

import nl.dejagermc.homefeeder.reporting.google.home.HomeBroadcaster;
import nl.dejagermc.homefeeder.user.UserState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("settings")
public class SettingsController {

    private static final Logger LOG = LoggerFactory.getLogger(SettingsController.class);

    private UserState userState;

    @Autowired
    public SettingsController(UserState userState) {
        this.userState = userState;
    }

    @GetMapping("/useTelegram/{value}")
    public ResponseEntity useTelegram(@PathVariable final boolean value) {
        userState.useTelegram(value);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/useGoogleHome/{value}")
    public ResponseEntity useGoogleHome(@PathVariable final boolean value) {
        userState.useGoogleHome(value);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/dotaTeamNotify/{value}")
    public ResponseEntity dotaTeamNotify(@PathVariable final List<String> value) {
        userState.dotaTeamsNotify(value);
        return new ResponseEntity(HttpStatus.OK);
    }
}
