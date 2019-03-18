package nl.dejagermc.homefeeder.business.reporting;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.AbstractBusinessService;
import nl.dejagermc.homefeeder.business.reported.ReportedBusinessService;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethods;
import nl.dejagermc.homefeeder.input.liquipedia.dota.MatchService;
import nl.dejagermc.homefeeder.input.liquipedia.dota.TournamentService;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.TournamentType;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutputService;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethods.GOOGLE_HOME;
import static nl.dejagermc.homefeeder.input.liquipedia.dota.model.TournamentType.*;
import static nl.dejagermc.homefeeder.input.liquipedia.dota.predicates.MatchPredicates.*;

@Service
@Slf4j
public class DotaReportBusinessService extends AbstractBusinessService {

    private static final String LIVE_DOTA_MATCH_TELEGRAM_MESSAGE = "<b>Live: %S versus %S</b>%n%s%n";
    private static final String TODAY_DOTA_MATCH_TELEGRAM_MESSAGE = "%S: %S versus %S%n";

    private MatchService matchService;
    private TournamentService tournamentService;

    @Autowired
    public DotaReportBusinessService(SettingsService settingsService, ReportedBusinessService reportedBusinessService,
                                     TelegramOutputService telegramOutputService, GoogleHomeOutputService googleHomeOutputService,
                                     MatchService matchService, TournamentService tournamentService) {
        super(settingsService, reportedBusinessService, telegramOutputService, googleHomeOutputService);
        this.matchService = matchService;
        this.tournamentService = tournamentService;
    }

    public void reportLiveMatchesFavoriteTeams() {
        settingsService.getFavoriteDotaTeams()
                .forEach(team -> matchService.getLiveMatchForTeam(team)
                        .ifPresentOrElse(this::reportLiveMatch, () -> log.info("UC100: no live match found for team {}", team)));
    }

    public void reportTodaysMatches() {
        reportTodaysMatchsForTournamentType(PREMIER);
        reportTodaysMatchsForTournamentType(MAJOR);
        reportTodaysFavoriteTeamMatchesForQualifierTournaments();
    }

    private void reportTodaysMatchsForTournamentType(TournamentType tournamentType) {
        StringBuilder sb = new StringBuilder();
        List<Tournament> tournaments = tournamentService.getAllActiveTournamentsForType(tournamentType);
        log.info("UC101: Reporting: {} active {} tournaments", tournaments.size(), tournamentType);

        for (Tournament tournament : tournaments) {
            List<Match> matches = matchService.getTodaysMatchesForTournament(tournament.name());
            log.info("UC101: Reporting: {} matches for tournament {}", matches.size(), tournament.name());
            addMatchesForTournamentToReport(matches, tournament, sb);
        }

        if (!sb.toString().isBlank()) {
            telegramOutputService.sendMessage(String.format("<b>%s matches:</b>%n", tournamentType.getName()) + sb.toString());
        }
    }

    private void reportTodaysFavoriteTeamMatchesForQualifierTournaments() {
        StringBuilder sb = new StringBuilder();
        List<Tournament> tournaments = tournamentService.getAllActiveTournamentsForType(QUALIFIER);
        log.info("UC101: Reporting: {} active {} tournaments", tournaments.size(), QUALIFIER);

        for (Tournament tournament : tournaments) {
            List<Match> matches = matchService.getTodaysMatchesForTournament(tournament.name()).stream()
                    .filter(isMatchWithOneOfTheseTeams(settingsService.getFavoriteDotaTeams()))
                    .collect(Collectors.toList());
            log.info("UC101: Reporting: {} favorite team matches for tournament {}", matches.size(), tournament.name());
            addMatchesForTournamentToReport(matches, tournament, sb);
        }

        if (!sb.toString().isBlank()) {
            telegramOutputService.sendMessage("<b>Qualifiers with favorite teams:</b>\n" + sb.toString());
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
        log.info("UC100: Reporting: reporting live match: {}", match);
        if (!reportedBusinessService.hasThisBeenReportedToThat(match, GOOGLE_HOME)) {
            reportLiveMatchToGoogleHome(match);
        }

        if (!reportedBusinessService.hasThisBeenReportedToThat(match, ReportMethods.TELEGRAM)) {
            reportLiveMatchToTelegram(match);
        }

        matchService.removeMatchNotReported(match);
    }

    private void reportLiveMatchToTelegram(Match match) {
        String message = getLiveDotaMatchTelegramMessage(match);
        telegramOutputService.sendMessage(message);
        reportedBusinessService.markThisReportedToThat(match, ReportMethods.TELEGRAM);
        log.info("UC100: Reporting: reported live match to telegram");
    }

    private void reportLiveMatchToGoogleHome(Match match) {
        if (settingsService.surpressMessage()) {
            log.info("UC100: Reporting: reporting live match is surpressed");
            return;
        }
        if (!settingsService.userIsListening()) {
            log.info("UC100: Reporting: saving live match to report later");
            matchService.addMatchNotReported(match);
        } else {
            String message = String.format("Playing live is %S versus %S.", match.leftTeam(), match.rightTeam());
            googleHomeOutputService.broadcast(message);
            reportedBusinessService.markThisReportedToThat(match, GOOGLE_HOME);
            log.info("UC100: Reporting: reported live match to google home");
        }
    }

    public void reportSummary() {
        StringBuilder sb = new StringBuilder();
        addMostImportantActiveTournamentToSummary(sb);
        addNotYetReportedAndFutureMatchesToSummary(sb);
        if (sb.length() > 0) {
            googleHomeOutputService.broadcast(sb.toString());
        }
        matchService.resetMatchesNotReported();
    }

    private void addMostImportantActiveTournamentToSummary(StringBuilder sb) {
        Optional<Tournament> optionalTournament = tournamentService.getMostImportantPremierOrMajorActiveTournament();
        if (optionalTournament.isPresent()) {
            if (!reportedBusinessService.hasThisBeenReportedToThat(optionalTournament, GOOGLE_HOME)) {
                optionalTournament.ifPresent(t -> sb.append("Active Dota tournament is: ").append(t.name()).append(". "));
                reportedBusinessService.markThisReportedToThat(optionalTournament, GOOGLE_HOME);
            }
        }
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
                        .sorted(sortMatchesOnTime())
                        .collect(Collectors.toList());
        if (!futureMatchesOfFavoriteTeams.isEmpty()) {
            for (String team : settingsService.getFavoriteDotaTeams()) {
                futureMatchesOfFavoriteTeams.stream()
                        .filter(match -> match.matchEitherTeam(team))
                        .forEach(match -> addFutureMatchToSummary(sb, match, team));
            }
        }
    }

    private void addFutureMatchToSummary(StringBuilder sb, Match match, String team) {
        String futureMatchTemplate = "%s will be playing today at %s. ";
        sb.append(String.format(futureMatchTemplate, team, match.getDateTimeFormattedTimeToday()));
    }
}
