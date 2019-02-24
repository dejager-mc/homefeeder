package nl.dejagermc.homefeeder.web.dev;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.postnl.PostNLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class DevWeb {

    private PostNLService postNLService;

    @Autowired
    public DevWeb(PostNLService postNLService) {
        this.postNLService = postNLService;
    }

    @GetMapping("/postnl")
    public String postnl() {
        postNLService.test();

        return "postnl";
    }
}
