package nl.dejagermc.homefeeder.web;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.domain.generated.radarr.RadarrWebhookSchema;
import nl.dejagermc.homefeeder.gathering.radarr.RadarrService;
import nl.dejagermc.homefeeder.user.UserState;
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

    private RadarrService radarrService;

    @Autowired
    public RadarrController(UserState userState, RadarrService radarrService) {
        super(userState);
        this.radarrService = radarrService;
    }

    @PostMapping("add")
    public ResponseEntity addRadarr(@RequestBody RadarrWebhookSchema schema) {
        radarrService.addMovie(schema.getMovie());
        return new ResponseEntity(HttpStatus.OK);
    }
}
