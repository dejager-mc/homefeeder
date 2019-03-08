package nl.dejagermc.homefeeder.web;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.reporting.DotaReportBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("dota")
@Slf4j
public class DotaController {

    private DotaReportBusinessService dotaReportBusinessService;

    @Autowired
    public DotaController(DotaReportBusinessService dotaReportBusinessService) {
        this.dotaReportBusinessService = dotaReportBusinessService;
    }

    @GetMapping(value = "today")
    @ResponseStatus(HttpStatus.OK)
    public void reportTodaysMatches() {
        dotaReportBusinessService.reportTodaysMatches();
    }
}
