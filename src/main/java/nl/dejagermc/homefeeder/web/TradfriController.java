package nl.dejagermc.homefeeder.web;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.tradfri.TradfriBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tradfri")
@Slf4j
public class TradfriController {

    private TradfriBusinessService tradfriBusinessService;

    @Autowired
    public TradfriController(TradfriBusinessService tradfriBusinessServicet) {
        this.tradfriBusinessService = tradfriBusinessServicet;
    }

    @GetMapping(value = "reboot")
    @ResponseStatus(HttpStatus.OK)
    public void rebootGateway() {
        log.info("UC200: received request to reboot gateway");
        tradfriBusinessService.rebootGateway();
    }

    @GetMapping(value = "status")
    @ResponseStatus(HttpStatus.OK)
    public void reportStatus() {
        log.info("UC202: received request to report gateway status");
        tradfriBusinessService.reportGatewayStatus();
    }

    @GetMapping(value = "devices")
    public String getDevices() {
        log.info("UC201: received request to report all devices");
        return tradfriBusinessService.getAllDevices();
    }
}
