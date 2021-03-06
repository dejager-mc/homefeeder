package nl.dejagermc.homefeeder.business.reporting;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.AbstractBusinessService;
import nl.dejagermc.homefeeder.business.reported.ReportedBusinessService;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethod;
import nl.dejagermc.homefeeder.input.liquipedia.dota.MatchService;
import nl.dejagermc.homefeeder.input.liquipedia.dota.TournamentService;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.TournamentType;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutputService;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutputService;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethod.GOOGLE_HOME;
import static nl.dejagermc.homefeeder.input.liquipedia.dota.model.TournamentType.*;
import static nl.dejagermc.homefeeder.input.liquipedia.dota.predicates.MatchPredicates.*;

@Service
@Slf4j
public class DotaReportBusinessService extends AbstractBusinessService {

    private static final String LIVE_DOTA_MATCH_TELEGRAM_MESSAGE = "<b>Live: %S versus %S</b>%n%s%n";
    private static final String TODAY_DOTA_MATCH_TELEGRAM_MESSAGE = "%S: %S versus %S%n";

    private static final String TOURNAMENT_CACHE = "";

    private MatchService matchService;
    private TournamentService tournamentService;
    private CacheManager cacheManager;

    @Inject
    public DotaReportBusinessService(SettingsService settingsService, ReportedBusinessService reportedBusinessService,
                                     TelegramOutputService telegramOutputService, GoogleHomeOutputService googleHomeOutputService,
                                     MatchService matchService, TournamentService tournamentService, CacheManager cacheManager) {
        super(settingsService, reportedBusinessService, telegramOutputService, googleHomeOutputService);
        this.matchService = matchService;
        this.tournamentService = tournamentService;
        this.cacheManager = cacheManager;
    }

    public void refreshTournamentInformation() {
        cacheManager.getCache(TOURNAMENT_CACHE).clear();
        tournamentService.getAllTournaments();
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
        log.info("UC100: Reporting live match: {}", match);
        if (!reportedBusinessService.hasThisBeenReportedToThat(match, GOOGLE_HOME)) {
            reportLiveMatchToGoogleHome(match);
        }

        if (!reportedBusinessService.hasThisBeenReportedToThat(match, ReportMethod.TELEGRAM)) {
            reportLiveMatchToTelegram(match);
        }

        matchService.removeMatchNotReported(match);
    }

    private void reportLiveMatchToTelegram(Match match) {
        String message = getLiveDotaMatchTelegramMessage(match);
        telegramOutputService.sendMessage(message);
        reportedBusinessService.markThisReportedToThat(match, ReportMethod.TELEGRAM);
    }

    private void reportLiveMatchToGoogleHome(Match match) {
        if (settingsService.isHomeMuted()) {
            log.info("UC103: Home muted, save match report");
            matchService.addMatchNotReported(match);
        } else {
            String message = String.format("Playing live is %S versus %S.", match.leftTeam(), match.rightTeam());
            googleHomeOutputService.broadcast(message);
            reportedBusinessService.markThisReportedToThat(match, GOOGLE_HOME);
        }
    }

    void reportSummary() {
        log.info("UC105: dota report summary");
        StringBuilder sb = new StringBuilder();
        addMostImportantActiveTournamentToSummary(sb);
        addNotYetReportedMatchesToSummary(sb);
        addMatchesLaterTodayToSummary(sb);
        if (!sb.toString().isBlank()) {
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

    private void addNotYetReportedMatchesToSummary(StringBuilder sb) {
        List<Match> matchesNotReported = matchService.getMatchesNotReported();
        if (!matchesNotReported.isEmpty()) {
            log.info("UC104: reporting {} saved match reports", matchesNotReported.size());
            sb.append("Your favorite teams have played ").append(matchesNotReported.size()).append(" games today. ");
        }
    }

    private void addMatchesLaterTodayToSummary(StringBuilder sb) {
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
