package nl.dejagermc.homefeeder.gathering.liquipedia.dota;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.TournamentType;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static nl.dejagermc.homefeeder.gathering.liquipedia.dota.predicates.TournamentPredicates.isTournamentActive;
import static nl.dejagermc.homefeeder.gathering.liquipedia.dota.predicates.TournamentPredicates.sortTournamentsByImportance;

@Service
@Slf4j
public class TournamentService {

    private TournamentRepository tournamentRepository;

    @Autowired
    public TournamentService(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    public Set<Tournament> getAllTournaments() {
        Set<Tournament> allTournaments = new HashSet<>();

        final Set<Tournament> premiumTournaments = tournamentRepository.getAllPremierTournaments();
        final Set<Tournament> majorTournaments = tournamentRepository.getAllMajorTournaments();
        final Set<Tournament> qualifierTournaments = tournamentRepository.getAllQualifierTournaments();

        allTournaments.addAll(premiumTournaments);
        allTournaments.addAll(majorTournaments);
        allTournaments.addAll(qualifierTournaments);

        return allTournaments;
    }

    public Set<Tournament> getAllPremierTournaments() {
        return tournamentRepository.getAllPremierTournaments();
    }

    public Set<Tournament> getAllMajorTournaments() {
        return tournamentRepository.getAllMajorTournaments();
    }

    public Set<Tournament> getAllQualifierTournaments() {
        return tournamentRepository.getAllQualifierTournaments();
    }

    public Set<Tournament> getAllActiveTournamentsForType(final TournamentType tournamentType) {
        switch (tournamentType) {
            case PREMIER:
                return getAllPremierTournaments().stream().filter(isTournamentActive()).collect(Collectors.toSet());
            case MAJOR:
                return getAllMajorTournaments().stream().filter(isTournamentActive()).collect(Collectors.toSet());
            case QUALIFIER:
                return getAllQualifierTournaments().stream().filter(isTournamentActive()).collect(Collectors.toSet());
            default:
                return Collections.emptySet();
        }
    }

    public Optional<Tournament> getTournamentByName(final String name) {
        return getAllTournaments().stream()
                .filter(t -> t.name().equals(name))
                .findFirst();
    }

    public Optional<Tournament> getMostImportantActiveTournament() {
        return getAllTournaments().stream()
                .filter(isTournamentActive())
                .max(sortTournamentsByImportance());
    }
}
