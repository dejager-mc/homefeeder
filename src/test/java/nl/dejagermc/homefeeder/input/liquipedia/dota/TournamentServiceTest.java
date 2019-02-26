package nl.dejagermc.homefeeder.input.liquipedia.dota;

import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.TournamentType;
import nl.dejagermc.homefeeder.input.liquipedia.dota.repository.TournamentRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static nl.dejagermc.homefeeder.input.liquipedia.dota.builders.TournamentBuilders.defaultTournament;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = TournamentService.class)
@EnableConfigurationProperties
public class TournamentServiceTest {

    private Set<Tournament> premierTournaments;
    private Set<Tournament> majorTournaments;
    private Set<Tournament> qualifierTournaments;


    private Tournament tournament1Active = defaultTournament("tournament1", TournamentType.PREMIER, true, false);
    private Tournament tournament2Active = defaultTournament("tournament2", TournamentType.MAJOR, true, false);
    private Tournament tournament5Active = defaultTournament("tournament5", TournamentType.QUALIFIER, true, false);
    private Tournament tournament6NotActive = defaultTournament("tournament6", TournamentType.QUALIFIER, false, false);
    private Tournament tournament3NotActive = defaultTournament("tournament3", TournamentType.PREMIER, false, false);
    private Tournament tournament4NotActive = defaultTournament("tournament4", TournamentType.MAJOR, false, false);
    private Tournament mostImportantActiveTournament = defaultTournament("tournament7", TournamentType.PREMIER, true,
            true);

    @MockBean
    private TournamentRepository tournamentRepository;

    @Autowired
    private TournamentService tournamentService;

    @Before
    public void setup() {
        premierTournaments = Set.of(
                tournament1Active,
                tournament3NotActive,
                mostImportantActiveTournament
        );
        majorTournaments = Set.of(
                tournament2Active,
                tournament4NotActive
        );
        qualifierTournaments = Set.of(
                tournament5Active,
                tournament6NotActive
        );
    }

    @Test
    public void testGetAllTournaments() {
        when(tournamentRepository.getAllPremierTournaments()).thenReturn(premierTournaments);
        when(tournamentRepository.getAllMajorTournaments()).thenReturn(majorTournaments);
        when(tournamentRepository.getAllQualifierTournaments()).thenReturn(qualifierTournaments);
        Set<Tournament> results = tournamentService.getAllTournaments();
        validateMockitoUsage();

        assertEquals(7, results.size());
        assertThat(results, containsInAnyOrder(tournament1Active, tournament2Active, tournament5Active,
                tournament6NotActive, tournament3NotActive, tournament4NotActive, mostImportantActiveTournament));
    }

    @Test
    public void testGetAllActivePremierTournaments() {
        when(tournamentRepository.getAllPremierTournaments()).thenReturn(premierTournaments);
        List<Tournament> results = tournamentService.getAllActiveTournamentsForType(TournamentType.PREMIER);
        validateMockitoUsage();

        assertEquals(2, results.size());
        assertThat(results, containsInAnyOrder(tournament1Active, mostImportantActiveTournament));
    }

    @Test
    public void testGetAllActiveMajorTournaments() {
        when(tournamentRepository.getAllMajorTournaments()).thenReturn(majorTournaments);
        List<Tournament> results = tournamentService.getAllActiveTournamentsForType(TournamentType.MAJOR);
        validateMockitoUsage();

        assertEquals(1, results.size());
        assertThat(results, containsInAnyOrder(tournament2Active));
    }

    @Test
    public void testGetAllActiveQualifierTournaments() {
        when(tournamentRepository.getAllQualifierTournaments()).thenReturn(qualifierTournaments);
        List<Tournament> results = tournamentService.getAllActiveTournamentsForType(TournamentType.QUALIFIER);
        validateMockitoUsage();

        assertEquals(1, results.size());
        assertThat(results, containsInAnyOrder(tournament5Active));
    }

    @Test
    public void testGetMostImportantActiveTournament() {
        when(tournamentRepository.getAllPremierTournaments()).thenReturn(premierTournaments);
        when(tournamentRepository.getAllMajorTournaments()).thenReturn(majorTournaments);
        when(tournamentRepository.getAllQualifierTournaments()).thenReturn(qualifierTournaments);
        Optional<Tournament> result = tournamentService.getMostImportantPremierOrMajorActiveTournament();
        validateMockitoUsage();

        assertTrue(result.isPresent());
        assertEquals(mostImportantActiveTournament, result.get());
    }

    @Test
    public void testGetMostImportantActiveTournamentWithAMajorTournamentByValve() {
        Tournament mostImportantActiveTournamentMajor = defaultTournament("tournament8", TournamentType.MAJOR, true,
                true);
        premierTournaments = Set.of(tournament1Active, mostImportantActiveTournamentMajor);

        when(tournamentRepository.getAllPremierTournaments()).thenReturn(premierTournaments);
        when(tournamentRepository.getAllMajorTournaments()).thenReturn(majorTournaments);
        when(tournamentRepository.getAllQualifierTournaments()).thenReturn(qualifierTournaments);
        Optional<Tournament> result = tournamentService.getMostImportantPremierOrMajorActiveTournament();
        validateMockitoUsage();

        assertTrue(result.isPresent());
        assertEquals(mostImportantActiveTournamentMajor, result.get());
    }

}
