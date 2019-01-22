package nl.dejagermc.homefeeder.web.dev;

import nl.dejagermc.homefeeder.gathering.liquipedia.dota.MatchService;
import nl.dejagermc.homefeeder.gathering.postnl.PostNLUtil;
import nl.dejagermc.homefeeder.reporting.telegram.Telegram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DevWeb {

    private Telegram telegram;
    private MatchService matchService;

    @Autowired
    public DevWeb(Telegram telegram, MatchService matchService) {
        Assert.notNull(telegram, "telegram must not be null");
        Assert.notNull(matchService, "matchService must not be null");

        this.telegram = telegram;
        this.matchService = matchService;
    }

    @GetMapping("/telegram")
    public String greeting(@RequestParam(value="text") String text) {
        telegram.sendMessage(text);
        return "Sending " + text;
    }

    @GetMapping("/dota")
    public String dota() {
        StringBuilder sb = new StringBuilder();

        matchService.getMatches().forEach(match -> sb.append(match.toString()).append("<br/>"));

        return sb.toString();
    }

    @GetMapping("/postnl")
    public String postnl() {
        PostNLUtil util = new PostNLUtil();
        util.test();

        return "postnl";
    }
}
