package nl.dejagermc.homefeeder.gathering.liquipedia.dota;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static nl.dejagermc.homefeeder.gathering.liquipedia.dota.predicates.MatchPredicates.isEenMatchDieVandaagIs;

@Service
@Slf4j
public class MatchService {

    private MatchRepository repository;

    private Set<Match> matchesNotReported = new HashSet<>();

    @Autowired
    public MatchService(MatchRepository repository) {
        this.repository = repository;
    }

    public Set<Match> getAllMatches() {
        return repository.getAllMatches();
    }

    public Optional<Match> getNextMatchForTeam(String team) {
        return repository.getAllMatches().stream()
                .sorted(Comparator.comparing(Match::matchTime))
                .filter(match -> match.matchEitherTeam(team))
                .findFirst();
    }

    public void addMatchNotReported(Match match) {
        matchesNotReported.add(match);
    }

    public Set<Match> getMatchesNotReported() {
        return matchesNotReported;
    }

    public void resetMatchesNotReported() {
        matchesNotReported = new HashSet<>();
    }

    public Set<Match> getLiveMatches() {
        return repository.getAllMatches().stream()
                .filter(Match::isLive)
                .collect(Collectors.toSet());
    }

    public Optional<Match> getLiveMatchForTeam(String team) {
        return getLiveMatches().stream()
                .filter(m -> m.matchEitherTeam(team))
                .findFirst();
    }

    public Set<Match> getTodaysMatches() {
        return repository.getAllMatches().stream()
                .filter(isEenMatchDieVandaagIs())
                .collect(Collectors.toSet());
    }

    public Set<Match> getTodaysMatchesForTournament(String tournament) {
        return repository.getAllMatches().stream()
                .filter(isEenMatchDieVandaagIs())
                .filter(m -> m.tournamentName().equals(tournament))
                .collect(Collectors.toSet());
    }
}
