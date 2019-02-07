package nl.dejagermc.homefeeder.web;


import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.StreamOutputService;
import org.springframework.beans.factory.annotation.Autowired;
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

    private StreamOutputService streamOutputService;

    @Autowired
    public TaskerController(StreamOutputService streamOutputService) {
        this.streamOutputService = streamOutputService;
    }

    @GetMapping("dotaOnTv")
    public ResponseEntity dotaOnTv() {
        log.info("start dota stream on tv");
        streamOutputService.watchStream();
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("dotaOnCinema")
    public ResponseEntity dotaOnCinema() {
        return new ResponseEntity(HttpStatus.OK);
    }
}
