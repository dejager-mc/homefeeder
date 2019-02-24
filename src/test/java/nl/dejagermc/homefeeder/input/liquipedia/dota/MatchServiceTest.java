package nl.dejagermc.homefeeder.input.liquipedia.dota;

import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.input.liquipedia.dota.repository.MatchRepository;
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

import static nl.dejagermc.homefeeder.input.liquipedia.dota.builders.MatchBuilders.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MatchService.class)
@EnableConfigurationProperties
public class MatchServiceTest {

    private Set<Match> allMatches;

    private Match liveMatchFavTeamTourn1 = defaultMatch("OG", "EG", "tournament1", true);
    private Match liveMatchNotFavTeamTourn1 = defaultMatch("VP", "NIP", "tournament1", true);
    private Match liveMatchNotFavTeamTourn2 = defaultMatch("ESP", "VICI", "tournament2", true);

    private Match notLiveMatchFavTeamTourn1 = defaultMatch("OG", "VP", "tournament1", false);
    private Match notLiveMatchFavTeamTourn2 = defaultMatch("OG", "Liquid", "tournament2", false);
    private Match notLiveMatchNotFavTeamTourn1 = defaultMatch("Secret", "LGD", "tournament1", false);
    private Match notLiveMatchNotFavTeamTourn2 = defaultMatch("Fnatic", "Razors", "tournament2", false);

    private Match tomorrowMatchFavTeamTourn1 = tomorrowMatch("OG", "EG", "tournament1");
    private Match tomorrowMatchNotFavTeamTourn1 = tomorrowMatch("VP", "NIP", "tournament1");
    private Match tomorrowMatchNotFavTeamTourn2 = tomorrowMatch("Liquid", "VG", "tournament2");

    private Match notActiveTournamentMatchFavTeam = notActiveTournamentMatch("OG", "Liquid", "tournament3");
    private Match notActiveTournamentMatchNotFavTeam = notActiveTournamentMatch("Secret", "VP", "tournament3");
    private Match notActiveTournamentMatchNotFavTeam2 = notActiveTournamentMatch("Warriors", "Others", "tournament4");

    @MockBean
    private MatchRepository matchRepository;

    @Autowired
    private MatchService matchService;

    @Before
    public void setup() {
        allMatches = Set.of(
                liveMatchFavTeamTourn1,
                liveMatchNotFavTeamTourn1,
                liveMatchNotFavTeamTourn2,
                notLiveMatchFavTeamTourn1,
                notLiveMatchNotFavTeamTourn1,
                notLiveMatchNotFavTeamTourn2,
                notLiveMatchFavTeamTourn2,
                tomorrowMatchFavTeamTourn1,
                tomorrowMatchNotFavTeamTourn1,
                tomorrowMatchNotFavTeamTourn2,
                notActiveTournamentMatchFavTeam,
                notActiveTournamentMatchNotFavTeam,
                notActiveTournamentMatchNotFavTeam2
        );
    }

    @Test
    public void testGetTodaysMatches() {
        when(matchRepository.getAllMatches()).thenReturn(allMatches);
        List<Match> results = matchService.getTodaysMatches();
        validateMockitoUsage();

        assertEquals(7, results.size());
        assertThat(results, containsInAnyOrder(liveMatchFavTeamTourn1, liveMatchNotFavTeamTourn1,
                liveMatchNotFavTeamTourn2,notLiveMatchFavTeamTourn1,notLiveMatchNotFavTeamTourn1,
                notLiveMatchNotFavTeamTourn2,notLiveMatchFavTeamTourn2));
    }

    @Test
    public void testGetTodaysMatchesForTournament1() {
        when(matchRepository.getAllMatches()).thenReturn(allMatches);
        List<Match> results = matchService.getTodaysMatchesForTournament("tournament1");
        validateMockitoUsage();

        assertEquals(4, results.size());
        assertThat(results, containsInAnyOrder(liveMatchFavTeamTourn1, liveMatchNotFavTeamTourn1,
                notLiveMatchFavTeamTourn1,notLiveMatchNotFavTeamTourn1));
    }

    @Test
    public void testGetTodaysMatchesForTournament2() {
        when(matchRepository.getAllMatches()).thenReturn(allMatches);
        List<Match> results = matchService.getTodaysMatchesForTournament("tournament2");
        validateMockitoUsage();

        assertEquals(3, results.size());
        assertThat(results, containsInAnyOrder(liveMatchNotFavTeamTourn2, notLiveMatchNotFavTeamTourn2,
                notLiveMatchFavTeamTourn2));
    }

    @Test
    public void testGetTodaysMatchesForTournament3() {
        when(matchRepository.getAllMatches()).thenReturn(allMatches);
        List<Match> results = matchService.getTodaysMatchesForTournament("tournament3");
        validateMockitoUsage();

        assertTrue(results.isEmpty());
    }

    @Test
    public void testGetLiveMatches() {
        when(matchRepository.getAllMatches()).thenReturn(allMatches);
        Set<Match> results = matchService.getLiveMatches();
        validateMockitoUsage();

        assertEquals(3, results.size());
        assertThat(results, containsInAnyOrder(liveMatchFavTeamTourn1, liveMatchNotFavTeamTourn2,
                liveMatchNotFavTeamTourn1));
    }

    @Test
    public void testGetLiveMatchForTeamThatIsPlayingLive() {
        when(matchRepository.getAllMatches()).thenReturn(allMatches);
        Optional<Match> result = matchService.getLiveMatchForTeam("OG");
        validateMockitoUsage();

        assertTrue(result.isPresent());
        assertEquals(result.get(), liveMatchFavTeamTourn1);
    }

    @Test
    public void testGetLiveMatchForTeamThatIsNotPlayingLive() {
        when(matchRepository.getAllMatches()).thenReturn(allMatches);
        Optional<Match> result = matchService.getLiveMatchForTeam("Liquid");
        validateMockitoUsage();

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetNextMatchForTeamThatIsPlayingLive() {
        when(matchRepository.getAllMatches()).thenReturn(allMatches);
        Optional<Match> result = matchService.getNextMatchForTeam("OG");
        validateMockitoUsage();

        assertTrue(result.isPresent());
        assertEquals(result.get(), liveMatchFavTeamTourn1);
    }

    @Test
    public void testGetNextMatchForTeamThatIsPlayingLaterToday() {
        when(matchRepository.getAllMatches()).thenReturn(allMatches);
        Optional<Match> result = matchService.getNextMatchForTeam("Liquid");
        validateMockitoUsage();

        assertTrue(result.isPresent());
        assertEquals(result.get(), notLiveMatchFavTeamTourn2);
    }

    @Test
    public void testGetNextMatchForTeamThatIsPlayingTomorrow() {
        when(matchRepository.getAllMatches()).thenReturn(allMatches);
        Optional<Match> result = matchService.getNextMatchForTeam("VG");
        validateMockitoUsage();

        assertTrue(result.isPresent());
        assertEquals(result.get(), tomorrowMatchNotFavTeamTourn2);
    }
}
