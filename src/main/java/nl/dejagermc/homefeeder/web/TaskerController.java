package nl.dejagermc.homefeeder.web;


import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.streaming.StreamOutputBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tasker")
@Slf4j
public class TaskerController {

    private StreamOutputBusinessService streamOutputBusinessService;

    @Autowired
    public TaskerController(StreamOutputBusinessService streamOutputBusinessService) {
        this.streamOutputBusinessService = streamOutputBusinessService;
    }

    @GetMapping("dotaOnTv")
    public ResponseEntity dotaOnTv() {
        log.info("start dota stream on tv");
        streamOutputBusinessService.streamLiveMatch();
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("dotaOnCinema")
    public ResponseEntity dotaOnCinema() {
        return new ResponseEntity(HttpStatus.OK);
    }
}
