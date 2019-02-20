package nl.dejagermc.homefeeder.business;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.MatchService;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.TournamentService;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.TournamentType;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeReporter;
import nl.dejagermc.homefeeder.output.reported.ReportedService;
import nl.dejagermc.homefeeder.output.reported.model.ReportedTo;
import nl.dejagermc.homefeeder.output.telegram.TelegramReporter;
import nl.dejagermc.homefeeder.user.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.TournamentType.*;

@Service
@Slf4j
public class DotaReportService extends AbstractReportService {

    private MatchService matchService;
    private TournamentService tournamentService;

    @Autowired
    public DotaReportService(TournamentService tournamentService, UserState userState, ReportedService reportedService,
                             TelegramReporter telegramReporter, GoogleHomeReporter googleHomeReporter,
                             MatchService matchService) {
        super(userState, reportedService, telegramReporter, googleHomeReporter);
        this.matchService = matchService;
        this.tournamentService = tournamentService;
    }

    public void reportLiveMatch() {
        log.info("reportLiveMatch");
        for (String team : userState.favoriteTeams()) {
            log.info("reportLiveMatch for team {}", team);
            Optional<Match> optionalMatch = matchService.getLiveMatchForTeam(team);
            if (optionalMatch.isPresent()) {
                log.info("Found match for team {}", team);
                Match match = optionalMatch.get();
                reportNewToTelegram(match);
                reportNewToGoogleHome(match);
            }
        }
    }

    public void reportTodaysMatches() {
        reportTodaysMatchsForTournamentType(PREMIER);
        reportTodaysMatchsForTournamentType(MAJOR);
        reportTodaysMatchsForTournamentType(QUALIFIER);
    }

    private void reportTodaysMatchsForTournamentType(TournamentType tournamentType) {
        log.info("Start report for {} tournaments", tournamentType.getName());
        StringBuilder sb = new StringBuilder();
        List<Tournament> tournaments = tournamentService.getAllActiveTournamentsForType(tournamentType);

        for (Tournament tournament : tournaments) {
            log.info("Adding matches for tournament: {}", tournament);
            matchService.getTodaysMatchesForTournament(tournament.name()).stream()
                    .forEach(match -> test(sb, match));
        }

        if (!sb.toString().isBlank()) {
            telegramReporter.sendMessage(tournamentType.getName() + " matches:\n" + sb.toString());
        }
    }

    private void test(StringBuilder sb, Match match) {
        sb.append(formatMatchForTelegram(match));
        log.info("Found match: {}", match);
    }

    private String formatMatchForTelegram(Match match) {
        return String.format("%6.6S: %S versus %S%n%47.33s%n",
                match.matchTime().format(DateTimeFormatter.ofPattern("H:mm")),
                match.leftTeam(),
                match.rightTeam(),
                match.tournamentName());
    }

    private void reportNewToTelegram(Match match) {
        log.info("Reporting match to telegram: {}", match);
        if (!reportedService.hasThisBeenReported(match, ReportedTo.TELEGRAM)) {
            log.info("Match has not been reported yet.");
            String message = formatMatchForTelegram(match);
            telegramReporter.sendMessage(message);
            reportedService.reportThisToThat(match, ReportedTo.TELEGRAM);
        }
    }

    private void reportNewToGoogleHome(Match match) {
        log.info("Report match to google: {}", match);
        if (!reportedService.hasThisBeenReported(match, ReportedTo.GOOGLE_HOME)) {
            log.info("Match has not been reported yet.");
            if (userState.isSleeping() || !userState.isHome() || userState.isMute()) {
                log.info("Silence mode enabled, surpressing message");
                matchService.addMatchNotReported(match);
            } else {
                log.info("Broadcasting.");
                String message = String.format("Playing live is %S versus %S.", match.leftTeam(), match.rightTeam());
                googleHomeReporter.broadcast(message);
                reportedService.reportThisToThat(match, ReportedTo.GOOGLE_HOME);
            }
        }
    }
}
