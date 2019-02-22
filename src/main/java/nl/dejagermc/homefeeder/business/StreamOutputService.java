package nl.dejagermc.homefeeder.business;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.MatchService;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.TournamentService;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.util.MatchUtil;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeReporter;
import nl.dejagermc.homefeeder.output.openhab.OpenhabOutput;
import nl.dejagermc.homefeeder.user.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static nl.dejagermc.homefeeder.gathering.liquipedia.dota.predicates.MatchPredicates.isMatchMetStream;
import static nl.dejagermc.homefeeder.gathering.liquipedia.dota.predicates.TournamentPredicates.sortTournamentsByImportanceMostToLeast;

@Service
@Slf4j
public class StreamOutputService {

    private static final String NO_MATCH_FOUND_MESSAGE = "There is no match that can be streamed.";
    private static final String MATCH_FOUND_MESSAGE = "Streaming %s versus %s.";

    private OpenhabOutput openhabOutput;
    private UserState userState;
    private MatchService matchService;
    private TournamentService tournamentService;
    private GoogleHomeReporter googleHomeReporter;

    @Autowired
    public StreamOutputService(OpenhabOutput openhabOutput, UserState userState, MatchService matchService, TournamentService tournamentService, GoogleHomeReporter googleHomeReporter) {
        this.openhabOutput = openhabOutput;
        this.userState = userState;
        this.matchService = matchService;
        this.tournamentService = tournamentService;
        this.googleHomeReporter = googleHomeReporter;
    }

    public void streamLiveMatch() {
        Optional<Match> match = getMostImportantLiveMatch();

        if (match.isPresent()) {
            Match liveMatch = match.get();
            googleHomeReporter.broadcast(String.format(MATCH_FOUND_MESSAGE, liveMatch.leftTeam(), liveMatch.rightTeam()));
            openhabOutput.turnOnTv();
            openhabOutput.streamToTv(MatchUtil.getStreamUri(match.get()));
        } else {
            googleHomeReporter.broadcast(NO_MATCH_FOUND_MESSAGE);
        }
    }

    private Optional<Match> getMostImportantLiveMatch() {
        Set<Match> streamableLiveMatches = matchService.getLiveMatches().stream()
                .filter(isMatchMetStream())
                .collect(Collectors.toSet());

        if (streamableLiveMatches.isEmpty()) {
            return Optional.empty();
        }

        List<Tournament> sortedTournamentsWithLiveMatches = streamableLiveMatches.stream()
                .map(match -> tournamentService.getTournamentByName(match.tournamentName()))
                .distinct()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(sortTournamentsByImportanceMostToLeast())
                .collect(Collectors.toList());

        List<String> teams = userState.favoriteTeams();
        Optional<Match> possibleMatch = getFirstMatchForTournamentAndFavTeam(sortedTournamentsWithLiveMatches, streamableLiveMatches, teams);

        if (possibleMatch.isPresent()) {
            return possibleMatch;
        }

        return getFirstMatchForTournament(sortedTournamentsWithLiveMatches, streamableLiveMatches);
    }

    private Optional<Match> getFirstMatchForTournamentAndFavTeam(final List<Tournament> sortedTournaments,
                                                                 final Set<Match> matches, final List<String> favTeams) {
        for (Tournament tournament : sortedTournaments) {
            Optional<Match> optionalMatch = matches.stream()
                    .filter(m -> m.isInTournament(tournament.name()))
                    .filter(m -> m.matchEitherTeam(favTeams))
                    .findFirst();
            if (optionalMatch.isPresent()) {
                return optionalMatch;
            }
        }
        return Optional.empty();
    }

    private Optional<Match> getFirstMatchForTournament(final List<Tournament> sortedTournaments,
                                                       final Set<Match> matches) {
        for (Tournament tournament : sortedTournaments) {
            Optional<Match> optionalMatch = matches.stream()
                    .filter(m -> m.isInTournament(tournament.name()))
                    .findFirst();
            if (optionalMatch.isPresent()) {
                return optionalMatch;
            }
        }
        return Optional.empty();
    }
}
