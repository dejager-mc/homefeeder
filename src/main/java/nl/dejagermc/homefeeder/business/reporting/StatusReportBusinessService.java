package nl.dejagermc.homefeeder.business.reporting;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.domain.generated.radarr.RadarrWebhookSchema;
import nl.dejagermc.homefeeder.domain.generated.sonarr.SonarrWebhookSchema;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethods;
import nl.dejagermc.homefeeder.input.liquipedia.dota.MatchService;
import nl.dejagermc.homefeeder.input.liquipedia.dota.TournamentService;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.input.postnl.PostNLService;
import nl.dejagermc.homefeeder.input.postnl.model.Delivery;
import nl.dejagermc.homefeeder.input.radarr.RadarrService;
import nl.dejagermc.homefeeder.input.sonarr.SonarrService;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutput;
import nl.dejagermc.homefeeder.business.reported.ReportedBusinessService;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static nl.dejagermc.homefeeder.input.liquipedia.dota.predicates.MatchPredicates.isMatchThatWillTakePlaceLaterToday;
import static nl.dejagermc.homefeeder.input.liquipedia.dota.predicates.MatchPredicates.isMatchWithOneOfTheseTeams;

@Service
@Slf4j
public class StatusReportBusinessService extends AbstractReportBusinessService {

    private SonarrService sonarrService;
    private RadarrService radarrService;
    private TournamentService tournamentService;
    private MatchService matchService;
    private PostNLService postNLService;

    @Autowired
    public StatusReportBusinessService(SettingsService settingsService, ReportedBusinessService reportedBusinessService, TelegramOutput telegramOutput,
                                       GoogleHomeOutput googleHomeOutput, SonarrService sonarrService,
                                       RadarrService radarrService, TournamentService tournamentService, MatchService matchService, PostNLService postNLService) {
        super(settingsService, reportedBusinessService, telegramOutput, googleHomeOutput);
        this.sonarrService = sonarrService;
        this.radarrService = radarrService;
        this.tournamentService = tournamentService;
        this.matchService = matchService;
        this.postNLService = postNLService;
    }

    public void reportSavedMessagesToGoogleHome() {
        if (settingsService.surpressMessage()) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        // series
        addSonarrToUpdate(sb);
        // movies
        addRadarrToUpdate(sb);
        // tournament
        addTournamentToUpdate(sb);
        // games
        addMatchesToUpdate(sb);
        // postnl
        addPostNLDeliveriesToReport(sb);

        if (!sb.toString().isBlank()) {
            googleHomeOutput.broadcast(sb.toString());
            resetAllNotYetReportedItems();
        }
    }

    private void addSonarrToUpdate(StringBuilder sb) {
        Set<SonarrWebhookSchema> schemas = sonarrService.getNotYetReported();

        if (schemas.isEmpty()) {
            return;
        }

        if (schemas.size() == 1) {
            sb.append("The following series has a new episode: ").append(schemas.iterator().next().getSeries().getTitle());
        } else {
            sb.append("The following series have new episode: ");
            schemas.forEach(schema -> sb.append(schema.getSeries().getTitle()).append(", "));
        }
        sb.append("\n");
    }

    private void addRadarrToUpdate(StringBuilder sb) {
        Set<RadarrWebhookSchema> schemas = radarrService.getNotYetReported();

        if (schemas.isEmpty()) {
            return;
        }

        if (schemas.size() == 1) {
            sb.append(schemas.size()).append(" movie was downloaded: ");
        } else {
            sb.append(schemas.size()).append(" movies were downloaded: ");
        }
        schemas.forEach(schema -> sb.append(schema.getRemoteMovie().getTitle()).append(", "));
        sb.append("\n");
    }

    private void addTournamentToUpdate(StringBuilder sb) {
        Optional<Tournament> optionalTournament = tournamentService.getMostImportantPremierOrMajorActiveTournament();
        optionalTournament.ifPresent(t -> sb.append("Dota tournament ").append(t.name()).append(" is active.\n"));
    }

    private void addMatchesToUpdate(StringBuilder sb) {
        // matches earlier
        List<Match> favTeamMissedMatches = matchService.getMatchesNotReported();
        if (!favTeamMissedMatches.isEmpty()) {
            sb.append("You've missed the following games:\n");
            addAllMatchesToReport(sb, favTeamMissedMatches);
        }
        // matches live
        List<Match> liveMatchesOfFavoriteTeams = matchService.getLiveMatchForTeams(settingsService.getFavoriteDotaTeams());
        if (!liveMatchesOfFavoriteTeams.isEmpty()) {
            sb.append("Playing live are:\n");
            addAllMatchesToReport(sb, liveMatchesOfFavoriteTeams);
        }
        // matchers later today
        List<Match> futureMatchesOfFavoriteTeams =
                matchService.getTodaysMatches().stream()
                        .filter(isMatchWithOneOfTheseTeams(settingsService.getFavoriteDotaTeams()))
                        .filter(isMatchThatWillTakePlaceLaterToday())
                        .collect(Collectors.toList());
        if (!futureMatchesOfFavoriteTeams.isEmpty()) {
            sb.append("Dota games later today: ");
            addAllMatchesToReport(sb, futureMatchesOfFavoriteTeams);
        }
    }

    private void addAllMatchesToReport(StringBuilder sb, List<Match> matches) {
        for (Match match : matches) {
            String report = String.format("At %s: %s versus %s%n",
                    match.matchTime().format(DateTimeFormatter.ofPattern("H:mm")),
                    match.leftTeam(),
                    match.rightTeam());
            sb.append(report);
        }
    }

    private void addPostNLDeliveriesToReport(StringBuilder sb) {
        Set<Delivery> todaysDeliveries = postNLService.getTodaysDeliveries();
        if (!todaysDeliveries.isEmpty()) {
            if (todaysDeliveries.size() == 1) {
                sb.append("Expect 1 delivery today.");
            } else {
                sb.append(String.format("Expect %s deliveries today.", todaysDeliveries.size()));
            }
        }
    }

    private void resetAllNotYetReportedItems() {
        log.info("Reset not yet reported");
        sonarrService.resetNotYetReported();
        radarrService.resetNotYetReported();
        matchService.resetMatchesNotReported();
    }
}
