package nl.dejagermc.homefeeder.gathering.liquipedia.dota;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.repository.MatchRepository;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MatchTournamentService {

    private MatchRepository matchRepository;
    private TournamentRepository tournamentRepository;

    @Autowired
    public MatchTournamentService(MatchRepository matchRepository, TournamentRepository tournamentRepository) {
        this.matchRepository = matchRepository;
        this.tournamentRepository = tournamentRepository;
    }

    public Optional<Tournament> getTournamentForMatch(Match match) {
        Optional<Tournament> gevonden = tournamentRepository.getAllPremierEvents().stream().filter(t -> t.name().equals(match.eventName())).findAny();
        if (gevonden.isPresent()) {
            return gevonden;
        }
        gevonden = tournamentRepository.getAllMajorEvents().stream().filter(t -> t.name().equals(match.eventName())).findAny();
        if (gevonden.isPresent()) {
            return gevonden;
        }
        gevonden = tournamentRepository.getAllQualifierEvents().stream().filter(t -> t.name().equals(match.eventName())).findAny();
        if (gevonden.isPresent()) {
            return gevonden;
        }
        return Optional.empty();
    }
}
