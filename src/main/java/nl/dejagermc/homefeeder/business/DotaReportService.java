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
    public DotaReportService(TournamentService tournamentService, UserState userState, ReportedService reportedService, TelegramReporter telegramReporter, GoogleHomeReporter googleHomeReporter, MatchService matchService) {
        super(userState, reportedService, telegramReporter, googleHomeReporter);
        this.matchService = matchService;
        this.tournamentService = tournamentService;
    }

    public void reportLiveMatch() {
        for (String team : userState.favoriteTeams()) {
            Optional<Match> optionalMatch = matchService.getLiveMatchForTeam(team);
            if (optionalMatch.isPresent()) {
                Match match = optionalMatch.get();
                reportNewToTelegram(match);
                reportNewToGoogleHome(match);
            }
        }
    }

    public void reportTodaysMatches() {
        reportTodaysMatchsForTournaments(PREMIER);
        reportTodaysMatchsForTournaments(MAJOR);
        reportTodaysMatchsForTournaments(QUALIFIER);
    }

    private void reportTodaysMatchsForTournaments(TournamentType tournamentType) {
        StringBuilder sb = new StringBuilder();
        List<Tournament> tournaments = tournamentService.getAllActiveTournamentsForType(tournamentType);

        for (Tournament tournament : tournaments) {
            matchService.getTodaysMatchesForTournament(tournament.name()).stream()
                    .forEach(match -> sb.append(formatMatchForTelegram(match)));
        }

        if (!sb.toString().isBlank()) {
            telegramReporter.sendMessage(tournamentType.getName() + " matches:\n" + sb.toString());
        }
    }

    private String formatMatchForTelegram(Match match) {
        return String.format("%6.6S: %S versus %S%n%47.33s%n",
                match.matchTime().format(DateTimeFormatter.ofPattern("H:mm")),
                match.leftTeam(),
                match.rightTeam(),
                match.tournamentName());
    }

    private void reportNewToTelegram(Match match) {
        if (!reportedService.hasThisBeenReported(match, ReportedTo.TELEGRAM)) {
            String message = formatMatchForTelegram(match);
            telegramReporter.sendMessage(message);
            reportedService.reportThisToThat(match, ReportedTo.TELEGRAM);
        }
    }

    private void reportNewToGoogleHome(Match match) {
        if (!reportedService.hasThisBeenReported(match, ReportedTo.GOOGLE_HOME)) {
            if (userState.isSleeping() || !userState.isHome() || userState.isMute()) {
                matchService.addMatchNotReported(match);
            } else {
                String message = String.format("Playing live is %S versus %S.", match.leftTeam(), match.rightTeam());
                googleHomeReporter.broadcast(message);
                reportedService.reportThisToThat(match, ReportedTo.GOOGLE_HOME);
            }
        }
    }
}
