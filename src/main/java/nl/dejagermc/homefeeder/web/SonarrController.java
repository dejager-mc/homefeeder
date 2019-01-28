package nl.dejagermc.homefeeder.web;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.domain.generated.SonarrWebhookSchema;
import nl.dejagermc.homefeeder.user.UserState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Autowired
    public SonarrController(UserState userState) {
        super(userState);
    }

    @PostMapping("add")
    public ResponseEntity addSonarr(@RequestBody SonarrWebhookSchema schema) {
        userState.sonarrDownloads().addAll(schema.getEpisodes());
        return new ResponseEntity(HttpStatus.OK);
    }
}
