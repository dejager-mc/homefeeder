package nl.dejagermc.homefeeder.gathering.liquipedia.dota;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
}
