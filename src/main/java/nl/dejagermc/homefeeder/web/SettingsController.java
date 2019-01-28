package nl.dejagermc.homefeeder.web;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.user.UserState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("settings")
@Slf4j
public class SettingsController extends AbstractController {

    @Autowired
    public SettingsController(UserState userState) {
        super(userState);
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
