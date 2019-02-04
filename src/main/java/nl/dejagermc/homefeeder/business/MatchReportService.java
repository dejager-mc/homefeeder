package nl.dejagermc.homefeeder.business;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.MatchService;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.MatchTournamentService;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.TournamentService;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.reporting.google.home.GoogleHomeReporter;
import nl.dejagermc.homefeeder.reporting.reported.ReportedService;
import nl.dejagermc.homefeeder.reporting.reported.model.ReportedTo;
import nl.dejagermc.homefeeder.reporting.telegram.TelegramReporter;
import nl.dejagermc.homefeeder.user.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MatchReportService extends AbstractReportService {

    private MatchService matchService;
    private TournamentService tournamentService;
    private MatchTournamentService matchTournamentService;

    @Autowired
    public MatchReportService(TournamentService tournamentService, MatchTournamentService matchTournamentService, UserState userState, ReportedService reportedService, TelegramReporter telegramReporter, GoogleHomeReporter googleHomeReporter, MatchService matchService) {
        super(userState, reportedService, telegramReporter, googleHomeReporter);
        this.matchService = matchService;
        this.matchTournamentService = matchTournamentService;
        this.tournamentService = tournamentService;
    }

    public void reportLiveMatch() {
        for (String team : userState.dotaTeamsNotify()) {
            Optional<Match> optionalMatch = matchService.getLiveMatchForTeam(team);
            if (optionalMatch.isPresent()) {
                Match match = optionalMatch.get();
                reportNewToTelegram(match);
                reportNewToGoogleHome(match);
            }
        }
    }

    public void reportTodaysMatches() {
        StringBuilder sb = new StringBuilder();

        List<Tournament> allTournaments = tournamentService.getAllTournaments();
        sb.append("Premier tournament matches:\n");
        for (Tournament tournament : allTournaments.stream().filter(Tournament::isPremier).collect(Collectors.toList())) {
            addMatch(tournament, sb);
        }
        sb.append("\n");
        sb.append("Major tournament matches:\n");
        for (Tournament tournament : allTournaments.stream().filter(Tournament::isMajor).collect(Collectors.toList())) {
            addMatch(tournament, sb);
        }
        sb.append("\n");
        sb.append("Qualifier tournament matches:\n");
        for (Tournament tournament : allTournaments.stream().filter(Tournament::isQualifier).collect(Collectors.toList())) {
            log.info("qualifier: {}", tournament.name());
            addMatch(tournament, sb);
        }

        for (Match match : matchService.getTodaysMatches()) {
            log.info("match: {}", match.eventName());
        }

        telegramReporter.sendMessage(sb.toString());
    }

    private void addMatch(Tournament tournament, StringBuilder sb) {
        for (Match match : matchService.getTodaysMatches().stream().filter(m -> m.eventName().toLowerCase().matches(".*" + tournament.name().toLowerCase() + ".*")).collect(Collectors.toList())) {
            sb.append(String.format("%S: %S versus %S - %S.\n", match.matchTime().format(DateTimeFormatter.ofPattern("H:mm")), match.leftTeam(), match.rightTeam(), match.eventName()));
        }
    }

    private void reportNewToTelegram(Match match) {
        if (!reportedService.hasThisBeenReported(match, ReportedTo.TELEGRAM)) {
            String message = String.format("Playing live is %S versus %S.\nTournament: %S", match.leftTeam(), match.rightTeam(), match.eventName());
            telegramReporter.sendMessage(message);
            reportedService.reportThisToThat(match, ReportedTo.TELEGRAM);
        }
    }

    private void reportNewToGoogleHome(Match match) {
        if (!reportedService.hasThisBeenReported(match, ReportedTo.GOOGLE_HOME)) {
            String message = String.format("Playing live is %S versus %S.", match.leftTeam(), match.rightTeam());
            googleHomeReporter.broadcast(message);
            reportedService.reportThisToThat(match, ReportedTo.GOOGLE_HOME);
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

        googleHomeReporter.broadcast(sb.toString());
    }

    public void reportWhenWakingUp() {

    }

    public void reportImportantDotaTeamPlayingNow() {

    }
}
