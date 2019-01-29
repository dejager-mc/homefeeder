package nl.dejagermc.homefeeder.web;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tasker")
@Slf4j
public class TaskerController {

    @GetMapping("dotaOnTv/{value}")
    public ResponseEntity dotaOnTv(@PathVariable final String value) {
        log.info(value);
        return new ResponseEntity(HttpStatus.OK);
    }
}
