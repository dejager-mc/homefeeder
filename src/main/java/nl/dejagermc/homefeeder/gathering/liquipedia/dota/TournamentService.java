package nl.dejagermc.homefeeder.gathering.liquipedia.dota;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.TournamentType;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static nl.dejagermc.homefeeder.gathering.liquipedia.dota.predicates.TournamentPredicates.isTournamentActief;

@Service
@Slf4j
public class TournamentService {

    private TournamentRepository tournamentRepository;

    @Autowired
    public TournamentService(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    public List<Tournament> getAllTournaments() {
        List allTournaments = tournamentRepository.getAllPremierTournaments();
        allTournaments.addAll(tournamentRepository.getAllMajorTournaments());
        allTournaments.addAll(tournamentRepository.getAllQualifierTournaments());
        return allTournaments;
    }

    public List<Tournament> getAllPremierTournaments() {
        return tournamentRepository.getAllPremierTournaments();
    }

    public List<Tournament> getAllMajorTournaments() {
        return tournamentRepository.getAllMajorTournaments();
    }

    public List<Tournament> getAllQualifierTournaments() {
        return tournamentRepository.getAllQualifierTournaments();
    }

    public List<Tournament> getAllTournamentsForType(TournamentType tournamentType) {
        return getAllTournaments().stream().filter(t -> t.tournamentType().equals(tournamentType)).collect(Collectors.toList());
    }

    public List<Tournament> getAllActiveTournamentsForType(TournamentType tournamentType) {
        switch (tournamentType) {
            case PREMIER:
                return getAllPremierTournaments().stream().filter(isTournamentActief()).collect(Collectors.toList());
            case MAJOR:
                return getAllMajorTournaments().stream().filter(isTournamentActief()).collect(Collectors.toList());
            case QUALIFIER:
                return getAllQualifierTournaments().stream().filter(isTournamentActief()).collect(Collectors.toList());
            default:
                return Collections.emptyList();
        }
    }
}
