package nl.dejagermc.homefeeder.web;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.openhab.OpenhabBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("openhab")
@Slf4j
public class OpenhabController {

    private OpenhabBusinessService openhabBusinessService;

    @Autowired
    public OpenhabController(OpenhabBusinessService openhabBusinessService) {
        this.openhabBusinessService = openhabBusinessService;
    }

    @GetMapping("/refreshItems")
    @ResponseStatus(HttpStatus.OK)
    public void refreshItems() {
        log.info("UC502: refresh openhab items list.");
        openhabBusinessService.refreshItems();
    }
}
