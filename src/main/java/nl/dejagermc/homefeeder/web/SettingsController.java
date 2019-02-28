package nl.dejagermc.homefeeder.web;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.homefeeder.model.HomeFeederState;
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
    public SettingsController(HomeFeederState homeFeederState) {
        super(homeFeederState);
    }

    @GetMapping("/useTelegram/{value}")
    public ResponseEntity useTelegram(@PathVariable final boolean value) {
        homeFeederState.useTelegram(value);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/useGoogleHome/{value}")
    public ResponseEntity useGoogleHome(@PathVariable final boolean value) {
        homeFeederState.useGoogleHome(value);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/favoriteTeams/{value}")
    public ResponseEntity favoriteTeams(@PathVariable final List<String> value) {
        homeFeederState.favoriteTeams(value);
        return new ResponseEntity(HttpStatus.OK);
    }
}
