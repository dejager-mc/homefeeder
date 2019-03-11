package nl.dejagermc.homefeeder.web.dev;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.reporting.BinPickupReportBusinessService;
import nl.dejagermc.homefeeder.input.postnl.PostNLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("dev")
@Slf4j
public class DevWeb {

    private PostNLService postNLService;
    private BinPickupReportBusinessService binPickupReportBusinessService;

    @Autowired
    public DevWeb(PostNLService postNLService, BinPickupReportBusinessService binPickupReportBusinessService) {
        this.postNLService = postNLService;
        this.binPickupReportBusinessService = binPickupReportBusinessService;
    }

    @GetMapping("/rubbish")
    public String rubbish() {
        binPickupReportBusinessService.reportNextBinPickup();

        return "rubbish";
    }

    @GetMapping("/postnl")
    public String postnl() {
        postNLService.test();

        return "postnl";
    }
}
