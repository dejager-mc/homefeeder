package nl.dejagermc.homefeeder.gathering.liquipedia.dota;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static nl.dejagermc.homefeeder.gathering.liquipedia.dota.predicates.MatchPredicates.isEenMatchDieVandaagIs;

@Service
@Slf4j
public class MatchService {

    private MatchRepository repository;

    @Autowired
    public MatchService(MatchRepository repository) {
        this.repository = repository;
    }

    public List<Match> getAllMatches() {
        return repository.getAllMatches();
    }

    public Optional<Match> getNextMatchForTeam(String team) {
        List<Match> allMatches = repository.getAllMatches();
        return allMatches.stream()
                .sorted(Comparator.comparing(Match::matchTime))
                .filter(match -> match.matchEitherTeam(team))
                .findFirst();
    }

    public List<Match> getLiveMatches() {
        return repository.getAllMatches().stream()
                .filter(Match::isLive)
                .collect(Collectors.toList());
    }

    public Optional<Match> getLiveMatchForTeam(String team) {
        return getLiveMatches().stream().filter(m -> m.matchEitherTeam(team)).findFirst();
    }

    public List<Match> getTodaysMatches() {
        return repository.getAllMatches().stream()
                .filter(isEenMatchDieVandaagIs())
                .collect(Collectors.toList());
    }

    public List<Match> getTodaysMatchesForTournament(String tournament) {
        return repository.getAllMatches().stream()
                .filter(isEenMatchDieVandaagIs())
                .filter(m -> m.tournamentName().equals(tournament))
                .collect(Collectors.toList());
    }
}
