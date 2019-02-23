package nl.dejagermc.homefeeder.business.reporting;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.domain.generated.radarr.RadarrWebhookSchema;
import nl.dejagermc.homefeeder.domain.generated.sonarr.SonarrWebhookSchema;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.MatchService;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.TournamentService;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.gathering.postnl.PostNLService;
import nl.dejagermc.homefeeder.gathering.postnl.model.Delivery;
import nl.dejagermc.homefeeder.gathering.radarr.RadarrService;
import nl.dejagermc.homefeeder.gathering.sonarr.SonarrService;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutput;
import nl.dejagermc.homefeeder.business.reported.ReportedService;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutput;
import nl.dejagermc.homefeeder.user.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static nl.dejagermc.homefeeder.gathering.liquipedia.dota.predicates.MatchPredicates.isMatchThatWillTakePlaceLaterToday;
import static nl.dejagermc.homefeeder.gathering.liquipedia.dota.predicates.MatchPredicates.isMatchWithOneOfTheseTeams;

@Service
@Slf4j
public class StatusReportService extends AbstractReportService {

    private SonarrService sonarrService;
    private RadarrService radarrService;
    private TournamentService tournamentService;
    private MatchService matchService;
    private PostNLService postNLService;

    @Autowired
    public StatusReportService(UserState userState, ReportedService reportedService, TelegramOutput telegramOutput,
                               GoogleHomeOutput googleHomeOutput, SonarrService sonarrService,
                               RadarrService radarrService, TournamentService tournamentService, MatchService matchService, PostNLService postNLService) {
        super(userState, reportedService, telegramOutput, googleHomeOutput);
        this.sonarrService = sonarrService;
        this.radarrService = radarrService;
        this.tournamentService = tournamentService;
        this.matchService = matchService;
        this.postNLService = postNLService;
    }

    public void statusUpdate() {
        if (!userState.reportNow()) {
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

        googleHomeOutput.broadcast(sb.toString());
        markEverythingAsReported();
    }

    private void addSonarrToUpdate(StringBuilder sb) {
        Set<SonarrWebhookSchema> schemas = sonarrService.getNotYetReported();

        if (schemas.isEmpty()) {
            return;
        }

        sb.append(schemas.size()).append(" series were downloaded: ");
        schemas.forEach(schema -> addEachSonarrEpisodeToUpdate(schema, sb));
        sb.append("%n");
    }

    private void addEachSonarrEpisodeToUpdate(SonarrWebhookSchema schema, StringBuilder sb) {
        schema.getEpisodes().forEach(episode -> sb.append(episode.getTitle()).append(" "));
    }

    private void addRadarrToUpdate(StringBuilder sb) {
        Set<RadarrWebhookSchema> schemas = radarrService.getNotYetReported();

        if (schemas.isEmpty()) {
            return;
        }

        sb.append(schemas.size()).append(" movies were downloaded: ");
        schemas.forEach(schema -> sb.append(schema.getMovie().getTitle()).append(" "));
        sb.append("%n");
    }

    private void addTournamentToUpdate(StringBuilder sb) {
        Optional<Tournament> optionalTournament = tournamentService.getMostImportantActiveTournament();

        if (optionalTournament.isEmpty()) {
            return;
        }

        sb.append("Dota tournament ").append(optionalTournament.get().name()).append(" is active.%n");
    }

    private void addMatchesToUpdate(StringBuilder sb) {
        // matches earlier
        Set<Match> favTeamMissedMatches = matchService.getMatchesNotReported();
        if (!favTeamMissedMatches.isEmpty()) {
            sb.append("You've missed the following games:%n");
            addAllMatchesToReport(sb, favTeamMissedMatches);
        }
        // matches live
        Set<Match> liveMatchesOfFavoriteTeams = matchService.getLiveMatchForTeams(userState.favoriteTeams());
        if (!liveMatchesOfFavoriteTeams.isEmpty()) {
            sb.append("Playing live are:%n");
            addAllMatchesToReport(sb, liveMatchesOfFavoriteTeams);
        }
        // matchers later today
        Set<Match> futureMatchesOfFavoriteTeams =
                matchService.getTodaysMatches().stream()
                        .filter(isMatchWithOneOfTheseTeams(userState.favoriteTeams()))
                        .filter(isMatchThatWillTakePlaceLaterToday())
                        .collect(Collectors.toSet());
        if (!futureMatchesOfFavoriteTeams.isEmpty()) {
            sb.append("Games played later today are:%n");
            addAllMatchesToReport(sb, futureMatchesOfFavoriteTeams);
        }
    }

    private void addAllMatchesToReport(StringBuilder sb, Set<Match> matches) {
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

    private void markEverythingAsReported() {
        sonarrService.resetNotYetReported();
        radarrService.resetNotYetReported();
        matchService.resetMatchesNotReported();
    }
}
