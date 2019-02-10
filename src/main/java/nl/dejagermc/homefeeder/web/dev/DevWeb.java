package nl.dejagermc.homefeeder.web.dev;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.DotaReportService;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.MatchService;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.TournamentService;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.gathering.postnl.PostNLUtil;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeReporter;
import nl.dejagermc.homefeeder.output.telegram.TelegramReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@Slf4j
public class DevWeb {

    private final static String HTML_BR = "<br/>";

    private TelegramReporter telegramReporter;
    private MatchService matchService;
    private TournamentService tournamentService;
    private DotaReportService dotaReportService;
    private GoogleHomeReporter googleHomeReporter;

    @Autowired
    public DevWeb(DotaReportService dotaReportService, TelegramReporter telegramReporter, MatchService matchService, TournamentService tournamentService, GoogleHomeReporter googleHomeReporter) {
        Assert.notNull(telegramReporter, "telegram must not be null");
        Assert.notNull(matchService, "matchService must not be null");
        Assert.notNull(tournamentService, "tournamentService must not be null");
        Assert.notNull(googleHomeReporter, "homeBroadcaster must not be null");
        Assert.notNull(dotaReportService, "reportService must not be null");

        this.telegramReporter = telegramReporter;
        this.matchService = matchService;
        this.tournamentService = tournamentService;
        this.googleHomeReporter = googleHomeReporter;
        this.dotaReportService = dotaReportService;
    }

    @GetMapping("/telegram/{text}")
    public String greeting(@PathVariable("text") String text) {
        telegramReporter.sendMessage(text);
        return "Sending " + text;
    }

//    @GetMapping("/telegram/live")
//    public String telegramLive() {
//        matchReportService.reportImportantDotaTeamPlayingNow();
//        return "Telegram live";
//    }

    @GetMapping("/dota")
    public String dota() {
        StringBuilder sb = new StringBuilder();

        sb.append("Live matches: ").append(HTML_BR);
        matchService.getLiveMatches().forEach(match -> sb.append(match.toString()).append(HTML_BR));

        sb.append(HTML_BR).append(HTML_BR).append("Next OG match:").append(HTML_BR);
        Optional<Match> nextOGMatch = matchService.getNextMatchForTeam("OG");
        if (nextOGMatch.isPresent()) {
            sb.append(nextOGMatch.toString()).append(HTML_BR);
        } else {
            sb.append("No matches scheduled.").append(HTML_BR);
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

//    @GetMapping("/dota/tournaments")
//    public String tournaments() {
//        StringBuilder sb = new StringBuilder();
//
//        sb.append("All active tournament: ");
//        TournamentUtil.getActiveTournaments(tournamentService.getAllTournaments()).stream().forEach(t -> sb.append(t.toString()).append("<br/>"));
//        sb.append(HTML_BR).append(HTML_BR);
//
//        sb.append("Current tournament: ");
//        Optional<Tournament> mostImp = TournamentUtil.getMostImportantActiveTournament(tournamentService.getAllTournaments());
//        if (mostImp.isPresent()) {
//            sb.append(sb.toString() + "<br/>");
//        } else {
//            sb.append("No tournament active.<br/>");
//        }
//        sb.append("<br/><br/>");
//
//        sb.append("All tournaments: <br/>");
//        sb.append("Premier tournaments: <br/>");
//        tournamentService.getAllTournaments().stream().filter(t -> t.isPremier()).forEach(t -> sb.append(t.toString()).append("<br/>"));
//        sb.append("<br/><br/>");
//        sb.append("Major tournaments: <br/>");
//        tournamentService.getAllTournaments().stream().filter(t -> t.isMajor()).forEach(t -> sb.append(t.toString()).append("<br/>"));
//        sb.append("<br/><br/>");
//        sb.append("Qualifiers: <br/>");
//        tournamentService.getAllTournaments().stream().filter(t -> t.isQualifier()).forEach(t -> sb.append(t.toString()).append("<br/>"));
//
//        return sb.toString();
//    }

    @GetMapping("/postnl")
    public String postnl() {
        PostNLUtil util = new PostNLUtil();
        util.test();

        return "postnl";
    }

    @GetMapping("relay/{text}")
    public String googleRelay(@PathVariable("text") String text) {
        googleHomeReporter.broadcast(text);
        return "Broadcasting message: " + text;
    }
}
