package nl.dejagermc.homefeeder.business.reporting;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.MatchService;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.TournamentService;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.TournamentType;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutput;
import nl.dejagermc.homefeeder.business.reported.ReportedService;
import nl.dejagermc.homefeeder.business.reported.model.ReportedTo;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutput;
import nl.dejagermc.homefeeder.user.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;

import static nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.TournamentType.*;

@Service
@Slf4j
public class DotaReportService extends AbstractReportService {

    private MatchService matchService;
    private TournamentService tournamentService;

    @Autowired
    public DotaReportService(UserState userState, ReportedService reportedService,
                             TelegramOutput telegramOutput, GoogleHomeOutput googleHomeOutput,
                             MatchService matchService, TournamentService tournamentService) {
        super(userState, reportedService, telegramOutput, googleHomeOutput);
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
        reportTodaysMatchsForTournamentType(PREMIER);
        reportTodaysMatchsForTournamentType(MAJOR);
        reportTodaysMatchsForTournamentType(QUALIFIER);
    }

    private void reportTodaysMatchsForTournamentType(TournamentType tournamentType) {
        StringBuilder sb = new StringBuilder();
        Set<Tournament> tournaments = tournamentService.getAllActiveTournamentsForType(tournamentType);

        for (Tournament tournament : tournaments) {
            matchService.getTodaysMatchesForTournament(tournament.name())
                    .forEach(match -> sb.append(formatMatchForTelegram(match)));
        }

        if (!sb.toString().isBlank()) {
            telegramOutput.sendMessage(tournamentType.getName() + " matches:\n" + sb.toString());
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
            telegramOutput.sendMessage(message);
            reportedService.reportThisToThat(match, ReportedTo.TELEGRAM);
        }
    }

    private void reportNewToGoogleHome(Match match) {
        if (!reportedService.hasThisBeenReported(match, ReportedTo.GOOGLE_HOME)) {
            if (!userState.reportNow()) {
                matchService.addMatchNotReported(match);
            } else {
                String message = String.format("Playing live is %S versus %S.", match.leftTeam(), match.rightTeam());
                googleHomeOutput.broadcast(message);
                reportedService.reportThisToThat(match, ReportedTo.GOOGLE_HOME);
            }
        }
    }
}
