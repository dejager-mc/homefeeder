package nl.dejagermc.homefeeder.input.liquipedia.dota;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.TournamentType;
import nl.dejagermc.homefeeder.input.liquipedia.dota.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static nl.dejagermc.homefeeder.input.liquipedia.dota.predicates.TournamentPredicates.*;

@Service
@Slf4j
public class TournamentService {

    private TournamentRepository tournamentRepository;

    @Autowired
    public TournamentService(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    Set<Tournament> getAllTournaments() {
        Set<Tournament> allTournaments = new HashSet<>();

        final Set<Tournament> premiumTournaments = tournamentRepository.getAllPremierTournaments();
        final Set<Tournament> majorTournaments = tournamentRepository.getAllMajorTournaments();
        final Set<Tournament> qualifierTournaments = tournamentRepository.getAllQualifierTournaments();

        allTournaments.addAll(premiumTournaments);
        allTournaments.addAll(majorTournaments);
        allTournaments.addAll(qualifierTournaments);

        return allTournaments;
    }

    private Set<Tournament> getAllPremierTournaments() {
        return tournamentRepository.getAllPremierTournaments();
    }

    private Set<Tournament> getAllMajorTournaments() {
        return tournamentRepository.getAllMajorTournaments();
    }

    private Set<Tournament> getAllQualifierTournaments() {
        return tournamentRepository.getAllQualifierTournaments();
    }

    public List<Tournament> getAllActiveTournamentsForType(final TournamentType tournamentType) {
        switch (tournamentType) {
            case PREMIER:
                return getAllPremierTournaments().stream().filter(isTournamentActive()).collect(Collectors.toList());
            case MAJOR:
                return getAllMajorTournaments().stream().filter(isTournamentActive()).collect(Collectors.toList());
            case QUALIFIER:
                return getAllQualifierTournaments().stream().filter(isTournamentActive()).collect(Collectors.toList());
            default:
                return Collections.emptyList();
        }
    }

    public Optional<Tournament> getTournamentByName(final String name) {
        return getAllTournaments().stream()
                .filter(t -> t.name().equals(name))
                .findFirst();
    }

    public Optional<Tournament> getMostImportantPremierOrMajorActiveTournament() {
        return getAllTournaments().stream()
                .filter(isTournamentActive())
                .filter(isPremierOrMajorTournament())
                .min(sortTournamentsByImportanceMostToLeast());
    }
}
