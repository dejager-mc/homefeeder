package nl.dejagermc.homefeeder.input.liquipedia.dota;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.input.liquipedia.dota.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static nl.dejagermc.homefeeder.input.liquipedia.dota.predicates.MatchPredicates.*;

@Service
@Slf4j
public class MatchService {

    private MatchRepository repository;

    private Set<Match> matchesNotReported = new HashSet<>();

    @Autowired
    public MatchService(MatchRepository repository) {
        this.repository = repository;
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

    public List<Match> getMatchesNotReported() {
        return new ArrayList<>(matchesNotReported);
    }

    public void removeMatchNotReported(Match match) {
        if (matchesNotReported.contains(match)) {
            matchesNotReported.remove(match);
        }
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

    public List<Match> getLiveMatchForTeams(List<String> teams) {
        return getLiveMatches().stream()
                .filter(isMatchWithOneOfTheseTeams(teams))
                .collect(Collectors.toList());
    }

    public List<Match> getTodaysMatches() {
        return repository.getAllMatches().stream()
                .filter(isMatchThatHappensToday())
                .collect(Collectors.toList());
    }

    public List<Match> getTodaysMatchesForTournament(String tournament) {
        return repository.getAllMatches().stream()
                .filter(isMatchThatHappensToday())
                .filter(m -> m.tournamentName().equals(tournament))
                .sorted(sortMatchesOnTime())
                .collect(Collectors.toList());
    }
}
