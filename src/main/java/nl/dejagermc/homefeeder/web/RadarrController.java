package nl.dejagermc.homefeeder.web;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.domain.generated.RadarrWebhookSchema;
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
@RequestMapping("radarr")
@Slf4j
public class RadarrController extends AbstractController {

    @Autowired
    public RadarrController(UserState userState) {
        super(userState);
    }

    @PostMapping("add")
    public ResponseEntity addRadarr(@RequestBody RadarrWebhookSchema schema) {
        userState.radarrDownloads().add(schema.getMovie());
        return new ResponseEntity(HttpStatus.OK);
    }
}
