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
import java.util.stream.Collectors;

import static nl.dejagermc.homefeeder.gathering.liquipedia.dota.predicates.MatchPredicates.isMatchMetStream;
import static nl.dejagermc.homefeeder.gathering.liquipedia.dota.predicates.TournamentPredicates.sortTournamentsByImportance;

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

    public void watchStream() {
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
        List<Match> liveMatches = matchService.getLiveMatches().stream().filter(isMatchMetStream()).collect(Collectors.toList());
        if (liveMatches.isEmpty()) {
            return Optional.empty();
        }

        List<String> teams = userState.favoriteTeams();
        List<Tournament> matchTournaments = liveMatches.stream()
                .map(match -> tournamentService.getTournamentByName(match.tournamentName()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        matchTournaments.sort(sortTournamentsByImportance());

        Optional<Match> possibleMatch = getFirstMatchForTournamentAndFavTeam(matchTournaments, teams, liveMatches);
        if (possibleMatch.isPresent()) {
            return Optional.of(possibleMatch.get());
        }

        possibleMatch = getFirstMatchForTournament(matchTournaments, liveMatches);
        if (possibleMatch.isPresent()) {
            return Optional.of(possibleMatch.get());
        }

        return Optional.empty();
    }

    private Optional<Match> getFirstMatchForTournamentAndFavTeam(List<Tournament> tournaments, List<String> favTeams, List<Match> matches) {
        for (Tournament tournament : tournaments) {
            Optional<Match> optionalMatch = matches.stream().filter(m -> m.tournamentName().equals(tournament.name())).filter(m -> m.matchEitherTeam(favTeams)).findFirst();
            if (optionalMatch.isPresent()) {
                return optionalMatch;
            }
        }
        return Optional.empty();
    }

    private Optional<Match> getFirstMatchForTournament(List<Tournament> tournaments, List<Match> matches) {
        for (Tournament tournament : tournaments) {
            Optional<Match> optionalMatch = matches.stream().filter(m -> m.tournamentName().equals(tournament.name())).findFirst();
            if (optionalMatch.isPresent()) {
                return optionalMatch;
            }
        }
        return Optional.empty();
    }
}
