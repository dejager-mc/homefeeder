package nl.dejagermc.homefeeder.business.reporting;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.reported.ReportedBusinessService;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethods;
import nl.dejagermc.homefeeder.input.liquipedia.dota.MatchService;
import nl.dejagermc.homefeeder.input.liquipedia.dota.TournamentService;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.TournamentType;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutput;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static nl.dejagermc.homefeeder.input.liquipedia.dota.model.TournamentType.*;
import static nl.dejagermc.homefeeder.input.liquipedia.dota.predicates.MatchPredicates.isMatchWithOneOfTheseTeams;

@Service
@Slf4j
public class DotaReportBusinessService extends AbstractReportBusinessService {

    private static final String LIVE_DOTA_MATCH_TELEGRAM_MESSAGE = "<b>Live: %S versus %S</b>%n%s%n";
    private static final String TODAY_DOTA_MATCH_TELEGRAM_MESSAGE = "%S: %S versus %S%n";

    private MatchService matchService;
    private TournamentService tournamentService;

    @Autowired
    public DotaReportBusinessService(SettingsService settingsService, ReportedBusinessService reportedBusinessService,
                                     TelegramOutput telegramOutput, GoogleHomeOutput googleHomeOutput,
                                     MatchService matchService, TournamentService tournamentService) {
        super(settingsService, reportedBusinessService, telegramOutput, googleHomeOutput);
        this.matchService = matchService;
        this.tournamentService = tournamentService;
    }

    public void reportLiveMatch() {
        for (String team : settingsService.getFavoriteDotaTeams()) {
            matchService
                    .getLiveMatchForTeam(team)
                    .ifPresent(this::reportLiveMatchToReportMethods);
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
                    .filter(isMatchWithOneOfTheseTeams(settingsService.getFavoriteDotaTeams()))
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

    private void reportLiveMatchToReportMethods(Match match) {
        Set<ReportMethods> reportMethods = settingsService.getReportMethods();

        if (reportMethods.contains(ReportMethods.GOOGLE_HOME)) {
            if (!reportedBusinessService.hasThisBeenReportedToThat(match, ReportMethods.GOOGLE_HOME)) {
                reportLiveMatchToGoogleHome(match);
            }
        }

        if (reportMethods.contains(ReportMethods.TELEGRAM)) {
            if (!reportedBusinessService.hasThisBeenReportedToThat(match, ReportMethods.TELEGRAM)) {
                reportLiveMatchToTelegram(match);
            }
        }
    }

    private void reportLiveMatchToTelegram(Match match) {
        String message = getLiveDotaMatchTelegramMessage(match);
        telegramOutput.sendMessage(message);
        reportedBusinessService.markThisReportedToThat(match, ReportMethods.TELEGRAM);
    }

    private void reportLiveMatchToGoogleHome(Match match) {
        if (settingsService.surpressMessage()) {
            // do nothing
            return;
        }
        if (settingsService.saveOutputForLater()) {
            matchService.addMatchNotReported(match);
        } else {
            String message = String.format("Playing live is %S versus %S.", match.leftTeam(), match.rightTeam());
            googleHomeOutput.broadcast(message);
            reportedBusinessService.markThisReportedToThat(match, ReportMethods.GOOGLE_HOME);
        }
    }
}
