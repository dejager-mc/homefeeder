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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static nl.dejagermc.homefeeder.gathering.liquipedia.dota.predicates.MatchPredicates.isMatchMetStream;

@Service
@Slf4j
public class StreamOutputService {

    private static final String NO_MATCH_FOUND_MESSAGE = "There is no match that can be streamed.";

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
        log.info("watchStream");
        Optional<Match> match = getMostImportantLiveMatch();

        if (match.isPresent()) {
            log.info("match found");
            openhabOutput.turnOnTv();
            openhabOutput.streamToTv(MatchUtil.getStreamUri(match.get()));
        } else {
            googleHomeReporter.broadcast(NO_MATCH_FOUND_MESSAGE);
        }
    }

    private Optional<Match> getMostImportantLiveMatch() {
        List<Match> liveMatches = matchService.getLiveMatches().stream().filter(isMatchMetStream()).collect(Collectors.toList());
        if (liveMatches.isEmpty()) {
            log.info("No live match with stream found.");
            return Optional.empty();
        }
        liveMatches.forEach(m -> log.info("Live match: {}", m));

        List<String> teams = userState.dotaTeamsNotify();
        teams.forEach(m -> log.info("team: {}", m));

        List<Tournament> matchTournaments = liveMatches.stream().map(match -> tournamentService.getTournamentByName(match.tournamentName())).filter(t -> t.isPresent()).map(t -> t.get()).collect(Collectors.toList());
        Optional<Match> possibleMatch;

        matchTournaments
                .sort(Comparator
                        .comparing(Tournament::isByValve)
                        .thenComparing(Tournament::isPremier)
                        .thenComparing(Tournament::isMajor)
                        .thenComparing(Tournament::isQualifier)
                );

        matchTournaments.forEach(m -> log.info("tournament: {}", m));

        possibleMatch = getFirstMatchForTournamentAndFavTeam(matchTournaments, teams, liveMatches);
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
