package nl.dejagermc.homefeeder.business.streaming;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.liquipedia.dota.MatchService;
import nl.dejagermc.homefeeder.input.liquipedia.dota.TournamentService;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.input.liquipedia.dota.util.MatchUtil;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutput;
import nl.dejagermc.homefeeder.output.openhab.OpenhabOutput;
import nl.dejagermc.homefeeder.input.homefeeder.model.HomeFeederState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static nl.dejagermc.homefeeder.input.liquipedia.dota.predicates.MatchPredicates.isMatchWithStream;
import static nl.dejagermc.homefeeder.input.liquipedia.dota.predicates.TournamentPredicates.sortTournamentsByImportanceMostToLeast;

@Service
@Slf4j
public class StreamOutputService {

    private static final String NO_MATCH_FOUND_MESSAGE = "There is no match that can be streamed.";
    private static final String MATCH_FOUND_MESSAGE = "Streaming %s versus %s.";

    private OpenhabOutput openhabOutput;
    private HomeFeederState homeFeederState;
    private MatchService matchService;
    private TournamentService tournamentService;
    private GoogleHomeOutput googleHomeOutput;

    @Autowired
    public StreamOutputService(OpenhabOutput openhabOutput, HomeFeederState homeFeederState, MatchService matchService, TournamentService tournamentService, GoogleHomeOutput googleHomeOutput) {
        this.openhabOutput = openhabOutput;
        this.homeFeederState = homeFeederState;
        this.matchService = matchService;
        this.tournamentService = tournamentService;
        this.googleHomeOutput = googleHomeOutput;
    }

    public void streamLiveMatch() {
        Optional<Match> match = getMostImportantLiveMatch();

        if (match.isPresent()) {
            Match liveMatch = match.get();
            googleHomeOutput.broadcast(String.format(MATCH_FOUND_MESSAGE, liveMatch.leftTeam(), liveMatch.rightTeam()));
            openhabOutput.turnOnTv();
            openhabOutput.streamToTv(MatchUtil.getStreamUri(match.get()));
        } else {
            googleHomeOutput.broadcast(NO_MATCH_FOUND_MESSAGE);
        }
    }

    private Optional<Match> getMostImportantLiveMatch() {
        Set<Match> streamableLiveMatches = matchService.getLiveMatches().stream()
                .filter(isMatchWithStream())
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

        List<String> teams = homeFeederState.favoriteTeams();
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
