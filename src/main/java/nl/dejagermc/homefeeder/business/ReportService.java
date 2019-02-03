package nl.dejagermc.homefeeder.business;

import nl.dejagermc.homefeeder.gathering.liquipedia.dota.MatchService;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.TournamentService;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.reporting.google.home.HomeBroadcaster;
import nl.dejagermc.homefeeder.reporting.reported.ReportedService;
import nl.dejagermc.homefeeder.reporting.reported.model.ReportedTo;
import nl.dejagermc.homefeeder.reporting.telegram.Telegram;
import nl.dejagermc.homefeeder.user.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private HomeBroadcaster homeBroadcaster;
    private UserState userState;
    private ReportUtil reportUtil;
    private Telegram telegram;
    private MatchService matchService;
    private TournamentService tournamentService;
    private ReportedService reportedService;

    @Autowired
    public ReportService(HomeBroadcaster homeBroadcaster, UserState userState, ReportUtil reportUtil, Telegram telegram, MatchService matchService, TournamentService tournamentService, ReportedService reportedService) {
        this.homeBroadcaster = homeBroadcaster;
        this.userState = userState;
        this.reportUtil = reportUtil;
        this.telegram = telegram;
        this.matchService = matchService;
        this.tournamentService = tournamentService;
        this.reportedService = reportedService;
    }

    public void reportLiveMatchToTelegram() {
        for (String team : userState.dotaTeamsNotify()) {
            Optional<Match> optionalMatch = matchService.getLiveMatchForTeam(team);
            if (optionalMatch.isPresent()) {
                Match match = optionalMatch.get();
                String message = String.format("Team {} is playing live. {} vs {} in {}.", team, match.leftTeam(), match.rightTeam(), match.eventName());
                reportToTelegram(match, message);
            }
        }
    }

    private void reportToTelegram(Match match, String message) {
        if (!reportedService.hasThisBeenReported(match, ReportedTo.TELEGRAM)) {
            telegram.sendMessage(message);
            reportedService.reportThisToThat(match, ReportedTo.TELEGRAM);
        }
    }

    public void reportWhenArrivingAtHome() {
        // films gedownload?
        // series gedownload?
        // actieve dota tournaments die zijn begonnen?
        // actieve dota tournaments die vandaag eindigen
        // dota teams die ik volg hebben vandaag al gespeeld
        // dota teams die ik volg spelen vandaag nog
        StringBuilder sb = new StringBuilder();
//        reportUtil.addDownloadedMoviesToReport(sb);
//        reportUtil.addDownloadedSeriesToReport(sb);
//        reportUtil.addActiveTournamentToReport(sb);

        homeBroadcaster.broadcastMessage(sb.toString());
    }

    public void reportWhenWakingUp() {

    }

    public void reportImportantDotaTeamPlayingNow() {

    }
}
