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
import java.util.Optional;
import java.util.stream.Collectors;

import static nl.dejagermc.homefeeder.input.liquipedia.dota.model.TournamentType.*;
import static nl.dejagermc.homefeeder.input.liquipedia.dota.predicates.MatchPredicates.isMatchThatWillTakePlaceLaterToday;
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

    public void reportLiveMatchesFavoriteTeams() {
        settingsService.getFavoriteDotaTeams()
                .forEach(team -> matchService.getLiveMatchForTeam(team)
                        .ifPresent(this::reportLiveMatch));
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
            addMatchesForTournamentToReport(matches, tournament, sb);
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
            addMatchesForTournamentToReport(matches, tournament, sb);
        }

        if (!sb.toString().isBlank()) {
            telegramOutput.sendMessage("<b>Qualifiers with favorite teams:</b>\n" + sb.toString());
        }
    }


    private void addMatchesForTournamentToReport(List<Match> matches, Tournament tournament, StringBuilder sb) {
        if (!matches.isEmpty()) {
            sb.append("<b>").append(tournament.name()).append(":</b>\n");
            matches.forEach(match -> sb.append(getTodayDotaMatchTelegramMessage(match)));
            sb.append("\n\n");
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

    private void reportLiveMatch(Match match) {
        if (!reportedBusinessService.hasThisBeenReportedToThat(match, ReportMethods.GOOGLE_HOME)) {
            reportLiveMatchToGoogleHome(match);
        }

        if (!reportedBusinessService.hasThisBeenReportedToThat(match, ReportMethods.TELEGRAM)) {
            reportLiveMatchToTelegram(match);
        }

        matchService.removeMatchNotReported(match);
    }

    private void reportLiveMatchToTelegram(Match match) {
        String message = getLiveDotaMatchTelegramMessage(match);
        telegramOutput.sendMessage(message);
        reportedBusinessService.markThisReportedToThat(match, ReportMethods.TELEGRAM);
    }

    private void reportLiveMatchToGoogleHome(Match match) {
        if (settingsService.surpressMessage()) {
            return;
        }
        if (settingsService.saveOutputForLater()) {
            log.info("Save for later: {}", match);
            matchService.addMatchNotReported(match);
        } else {
            String message = String.format("Playing live is %S versus %S.", match.leftTeam(), match.rightTeam());
            googleHomeOutput.broadcast(message);
            reportedBusinessService.markThisReportedToThat(match, ReportMethods.GOOGLE_HOME);
        }
    }

    public void reportSummary() {
        StringBuilder sb = new StringBuilder();
        addMostImportantActiveTournamentToSummary(sb);
        addNotYetReportedAndFutureMatchesToSummary(sb);
        if (sb.length() > 0) {
            googleHomeOutput.broadcast(sb.toString());
        }
        matchService.resetMatchesNotReported();
    }

    private void addMostImportantActiveTournamentToSummary(StringBuilder sb) {
        Optional<Tournament> optionalTournament = tournamentService.getMostImportantPremierOrMajorActiveTournament();
        optionalTournament.ifPresent(t -> sb.append("Active Dota tournament is: ").append(t.name()));
    }

    private void addNotYetReportedAndFutureMatchesToSummary(StringBuilder sb) {
        // matches earlier
        List<Match> favTeamMissedMatches = matchService.getMatchesNotReported();
        if (!favTeamMissedMatches.isEmpty()) {
            sb.append("Your favorite teams have played ").append(favTeamMissedMatches.size()).append(" games today. ");
        }

        // matchers later today
        List<Match> futureMatchesOfFavoriteTeams =
                matchService.getTodaysMatches().stream()
                        .filter(isMatchWithOneOfTheseTeams(settingsService.getFavoriteDotaTeams()))
                        .filter(isMatchThatWillTakePlaceLaterToday())
                        .collect(Collectors.toList());
        if (!futureMatchesOfFavoriteTeams.isEmpty()) {
            for (String team : settingsService.getFavoriteDotaTeams()) {
                sb.append(team).append(" will be play later today. ");
            }
        }
    }
}
