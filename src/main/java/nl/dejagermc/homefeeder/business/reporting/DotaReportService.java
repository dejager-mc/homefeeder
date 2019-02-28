package nl.dejagermc.homefeeder.business.reporting;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.liquipedia.dota.MatchService;
import nl.dejagermc.homefeeder.input.liquipedia.dota.TournamentService;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.TournamentType;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutput;
import nl.dejagermc.homefeeder.business.reported.ReportedService;
import nl.dejagermc.homefeeder.business.reported.model.ReportedTo;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutput;
import nl.dejagermc.homefeeder.input.homefeeder.model.HomeFeederState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static nl.dejagermc.homefeeder.input.liquipedia.dota.model.TournamentType.*;
import static nl.dejagermc.homefeeder.input.liquipedia.dota.predicates.MatchPredicates.isMatchWithOneOfTheseTeams;

@Service
@Slf4j
public class DotaReportService extends AbstractReportService {

    private static final String LIVE_DOTA_MATCH_TELEGRAM_MESSAGE = "<b>Live: %S versus %S</b>%n%s%n";
    private static final String TODAY_DOTA_MATCH_TELEGRAM_MESSAGE = "%S: %S versus %S%n";
    private static final String TODAY_TOURNAMENT_MATCHES_TELEGRAM_MESSAGE = "";


    private MatchService matchService;
    private TournamentService tournamentService;

    @Autowired
    public DotaReportService(HomeFeederState homeFeederState, ReportedService reportedService,
                             TelegramOutput telegramOutput, GoogleHomeOutput googleHomeOutput,
                             MatchService matchService, TournamentService tournamentService) {
        super(homeFeederState, reportedService, telegramOutput, googleHomeOutput);
        this.matchService = matchService;
        this.tournamentService = tournamentService;
    }

    public void reportLiveMatch() {
        for (String team : homeFeederState.favoriteTeams()) {
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
        reportTodaysFavoriteTeamMatchesForQualifierTournaments();
    }

    private void reportTodaysMatchsForTournamentType(TournamentType tournamentType) {
        StringBuilder sb = new StringBuilder();
        List<Tournament> tournaments = tournamentService.getAllActiveTournamentsForType(tournamentType);

        for (Tournament tournament : tournaments) {
            List<Match> matches = matchService.getTodaysMatchesForTournament(tournament.name());
            if (!matches.isEmpty()) {
                matches.forEach(match -> sb.append(getTodayDotaMatchTelegramMessage(match)));
                sb.append("\n");
            }
        }

        if (!sb.toString().isBlank()) {
            telegramOutput.sendMessage(String.format("<b>%s matches:</b>%n", tournamentType.getName()) + sb.toString());
        }
    }

    private void reportTodaysFavoriteTeamMatchesForQualifierTournaments() {
        StringBuilder sb = new StringBuilder();
        List<Tournament> tournaments = tournamentService.getAllActiveTournamentsForType(QUALIFIER);

        for (Tournament tournament : tournaments) {
            List<Match> matches = matchService.getTodaysMatchesForTournament(tournament.name()).stream()
                    .filter(isMatchWithOneOfTheseTeams(homeFeederState.favoriteTeams()))
                    .collect(Collectors.toList());
            if (!matches.isEmpty()) {
                sb.append(tournament.name()).append("\n");
                matches.forEach(match -> sb.append(getTodayDotaMatchTelegramMessage(match)));
                sb.append("\n");
            }
        }

        if (!sb.toString().isBlank()) {
            telegramOutput.sendMessage("<b>Qualifiers with favorite teams:</b>\n" + sb.toString());
        }
    }



    private String getLiveDotaMatchTelegramMessage(Match match) {
        return String.format(LIVE_DOTA_MATCH_TELEGRAM_MESSAGE,
                match.leftTeam(),
                match.rightTeam(),
                match.tournamentName());
    }

    private String getTodayDotaMatchTelegramMessage(Match match) {
        return String.format(TODAY_DOTA_MATCH_TELEGRAM_MESSAGE,
                match.matchTime().format(DateTimeFormatter.ofPattern("H:mm")),
                match.leftTeam(),
                match.rightTeam());
    }

    private void reportNewToTelegram(Match match) {
        if (!reportedService.hasThisBeenReportedToThat(match, ReportedTo.TELEGRAM)) {
            String message = getLiveDotaMatchTelegramMessage(match);
            telegramOutput.sendMessage(message);
            reportedService.markThisReportedToThat(match, ReportedTo.TELEGRAM);
        }
    }

    private void reportNewToGoogleHome(Match match) {
        if (!reportedService.hasThisBeenReportedToThat(match, ReportedTo.GOOGLE_HOME)) {
            if (!homeFeederState.reportNow()) {
                matchService.addMatchNotReported(match);
            } else {
                String message = String.format("Playing live is %S versus %S.", match.leftTeam(), match.rightTeam());
                googleHomeOutput.broadcast(message);
                reportedService.markThisReportedToThat(match, ReportedTo.GOOGLE_HOME);
            }
        }
    }
}
