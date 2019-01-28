package nl.dejagermc.homefeeder.web.dev;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.MatchService;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.TournamentService;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.gathering.postnl.PostNLUtil;
import nl.dejagermc.homefeeder.reporting.google.home.HomeBroadcaster;
import nl.dejagermc.homefeeder.reporting.telegram.Telegram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@Slf4j
public class DevWeb {

    private Telegram telegram;
    private MatchService matchService;
    private TournamentService tournamentService;

    private HomeBroadcaster homeBroadcaster;

    @Autowired
    public DevWeb(Telegram telegram, MatchService matchService, TournamentService tournamentService, HomeBroadcaster homeBroadcaster) {
        Assert.notNull(telegram, "telegram must not be null");
        Assert.notNull(matchService, "matchService must not be null");
        Assert.notNull(tournamentService, "tournamentService must not be null");
        Assert.notNull(homeBroadcaster, "homeBroadcaster must not be null");

        this.telegram = telegram;
        this.matchService = matchService;
        this.tournamentService = tournamentService;
        this.homeBroadcaster = homeBroadcaster;
    }

    @GetMapping("/telegram/{text}")
    public String greeting(@PathVariable("text") String text) {
        telegram.sendMessage(text);
        return "Sending " + text;
    }

    @GetMapping("/dota")
    public String dota() {
        StringBuilder sb = new StringBuilder();

        sb.append("Live matches: <br/>");
        matchService.getLiveMatches().forEach(match -> sb.append(match.toString()).append("<br/>"));

        sb.append("<br/><br/>Next OG match: <br/>");
        Optional<Match> nextOGMatch = matchService.getNextMatchForTeam("OG");
        if (nextOGMatch.isPresent()) {
            sb.append(nextOGMatch.toString()).append("<br/>");
        } else {
            sb.append("No matches scheduled.").append("<br/>");
        }

        sb.append("<br/><br/>Next VP match: <br/>");
        Optional<Match> nextVPMatch = matchService.getNextMatchForTeam("VP");
        if (nextVPMatch.isPresent()) {
            sb.append(nextVPMatch.toString()).append("<br/>");
        } else {
            sb.append("No matches scheduled.").append("<br/>");
        }

        sb.append("<br/><br/>All matches: <br/>");
        matchService.getAllMatches().forEach(match -> sb.append(match.toString()).append("<br/>"));

        return sb.toString();
    }

    @GetMapping("/dota/tournaments")
    public String tournaments() {
        StringBuilder sb = new StringBuilder();

        sb.append("All tournaments: <br/>");
        sb.append("Premier tournaments: <br/>");
        tournamentService.getAllTournaments().forEach(t -> sb.append(t.toString()).append("<br/>"));

        sb.append("Major tournaments: <br/>");
        tournamentService.getAllTournaments().forEach(t -> sb.append(t.toString()).append("<br/>"));

        return sb.toString();
    }

    @GetMapping("/postnl")
    public String postnl() {
        PostNLUtil util = new PostNLUtil();
        util.test();

        return "postnl";
    }

    @GetMapping("relay/{text}")
    public String googleRelay(@PathVariable("text") String text) {
        homeBroadcaster.broadcastMessage(text);
        return "Broadcasting message: " + text;
    }
}
