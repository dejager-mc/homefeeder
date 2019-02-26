package nl.dejagermc.homefeeder.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("apiai")
@Slf4j
public class ApiAiController {

    @PostMapping("test")
    public ResponseEntity webhook(@RequestBody Object object) {
        log.info("API.AI received: {}", object );
        return new ResponseEntity(HttpStatus.OK);
    }
}
