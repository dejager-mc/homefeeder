package nl.dejagermc.homefeeder.business;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.domain.generated.radarr.Movie;
import nl.dejagermc.homefeeder.domain.generated.sonarr.Episode;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.MatchService;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.TournamentService;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.gathering.radarr.RadarrService;
import nl.dejagermc.homefeeder.gathering.sonarr.SonarrService;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeReporter;
import nl.dejagermc.homefeeder.user.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static nl.dejagermc.homefeeder.gathering.liquipedia.dota.predicates.MatchPredicates.isEenMatchDieLaterVandaagIs;

@Service
@Slf4j
public class StatusReportService {

    private UserState userState;

    private SonarrService sonarrService;
    private RadarrService radarrService;
    private TournamentService tournamentService;
    private MatchService matchService;

    private GoogleHomeReporter googleHomeReporter;

    @Autowired
    public StatusReportService(UserState userState, SonarrService sonarrService, RadarrService radarrService, TournamentService tournamentService, MatchService matchService, GoogleHomeReporter googleHomeReporter) {
        this.userState = userState;
        this.sonarrService = sonarrService;
        this.radarrService = radarrService;
        this.tournamentService = tournamentService;
        this.matchService = matchService;
        this.googleHomeReporter = googleHomeReporter;
    }

    public void statusUpdate() {
        if (userState.isMute() || !userState.isHome() || userState.isSleeping()) {
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

        googleHomeReporter.broadcast(sb.toString());
        markEverythingAsReported();
    }

    private void addSonarrToUpdate(StringBuilder sb) {
        List<Episode> episodes = sonarrService.getEpisodes();

        if (episodes.isEmpty()) {
            return;
        }

        sb.append(episodes.size() + " series were downloaded: ");
        episodes.stream().forEach(serie -> sb.append(serie.getTitle() + " "));
        sb.append("%n");
    }

    private void addRadarrToUpdate(StringBuilder sb) {
        List<Movie> movies = radarrService.getMovies();

        if (movies.isEmpty()) {
            return;
        }

        sb.append(movies.size() + " movies were downloaded: ");
        movies.stream().forEach(serie -> sb.append(serie.getTitle() + " "));
        sb.append("%n");
    }

    private void addTournamentToUpdate(StringBuilder sb) {
        Optional<Tournament> optionalTournament = tournamentService.getMostImportantActiveTournament();

        if (!optionalTournament.isPresent()) {
            return;
        }

        sb.append("Dota tournament ").append(optionalTournament.get().name()).append(" is active.%n");
    }

    private void addMatchesToUpdate(StringBuilder sb) {
        // matches earlier
        List<Match> favTeamMissedMatches = matchService.getMatchesNotReported();
        if (!favTeamMissedMatches.isEmpty()) {
            sb.append("You've missed the following games:%n");
            addAllMatchesToReport(sb, favTeamMissedMatches);
        }
        // matches live
        List<Match> liveMatches = matchService.getLiveMatches();
        if (!liveMatches.isEmpty()) {
            sb.append("Playing live are:%n");
            addAllMatchesToReport(sb, liveMatches);
        }
        // matchers later today
        List<Match> futureMatches = matchService.getTodaysMatches().stream().filter(isEenMatchDieLaterVandaagIs()).collect(Collectors.toList());
        if (!futureMatches.isEmpty()) {
            sb.append("Games played later today are:%n");
            addAllMatchesToReport(sb, futureMatches);
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

    private void markEverythingAsReported() {
        sonarrService.resetEpisodes();
        radarrService.resetMovies();
        matchService.resetMatchesNotReported();
    }
}
