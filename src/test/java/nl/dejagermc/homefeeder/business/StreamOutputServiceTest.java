package nl.dejagermc.homefeeder.business;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.TestSetup;
import nl.dejagermc.homefeeder.business.streaming.StreamOutputService;
import nl.dejagermc.homefeeder.config.CacheManagerConfig;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.input.homefeeder.model.DotaSettings;
import nl.dejagermc.homefeeder.input.homefeeder.model.HomeFeederSettings;
import nl.dejagermc.homefeeder.input.homefeeder.model.OpenHabSettings;
import nl.dejagermc.homefeeder.input.liquipedia.dota.MatchService;
import nl.dejagermc.homefeeder.input.liquipedia.dota.TournamentService;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.TournamentType;
import nl.dejagermc.homefeeder.input.liquipedia.dota.repository.MatchRepository;
import nl.dejagermc.homefeeder.input.liquipedia.dota.repository.TournamentRepository;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutput;
import nl.dejagermc.homefeeder.output.openhab.OpenhabOutput;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static nl.dejagermc.homefeeder.input.liquipedia.dota.builders.MatchBuilders.defaultMatch;
import static nl.dejagermc.homefeeder.input.liquipedia.dota.builders.TournamentBuilders.defaultTournament;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@Slf4j
public class StreamOutputServiceTest extends TestSetup {

    private Set<Match> allMatches;

    private Match liveMatchFavTeamTourn1 = defaultMatch("OG", "EG", "tournament1", true);
    private Match liveMatchNotFavTeamTourn1 = defaultMatch("VP", "NIP", "tournament1", true);
    private Match liveMatchFavTeamTourn2 = defaultMatch("OG", "VICI", "tournament2", true);
    private Match liveMatchNotFavTeamTourn5 = defaultMatch("EG", "NAVI", "tournament5", true);

    private Match notLiveMatchFavTeamTourn1 = defaultMatch("OG", "VP", "tournament1", false);
    private Match notLiveMatchFavTeamTourn2 = defaultMatch("OG", "Liquid", "tournament2", false);
    private Match notLiveMatchNotFavTeamTourn1 = defaultMatch("Secret", "LGD", "tournament1", false);
    private Match notLiveMatchNotFavTeamTourn2 = defaultMatch("Fnatic", "Razors", "tournament2", false);


    private Set<Tournament> premierTournaments;
    private Set<Tournament> majorTournaments;
    private Set<Tournament> qualifierTournaments;

    private Tournament tournament1Active = defaultTournament("tournament1", TournamentType.PREMIER, true, false);
    private Tournament tournament2Active = defaultTournament("tournament2", TournamentType.MAJOR, true, false);
    private Tournament tournament5Active = defaultTournament("tournament5", TournamentType.QUALIFIER, true, true);
    private Tournament tournament6NotActive = defaultTournament("tournament6", TournamentType.QUALIFIER, false, false);
    private Tournament tournament3NotActive = defaultTournament("tournament3", TournamentType.PREMIER, false, false);
    private Tournament tournament4NotActive = defaultTournament("tournament4", TournamentType.MAJOR, false, false);
    private Tournament mostImportantActiveTournament = defaultTournament("tournament7", TournamentType.PREMIER, true,
            true);

    @MockBean
    private OpenhabOutput openhabOutput;
    @MockBean
    private GoogleHomeOutput googleHomeOutput;
    @MockBean
    private MatchRepository matchRepository;
    @MockBean
    private TournamentRepository tournamentRepository;

    @Autowired
    private StreamOutputService streamOutputService;

    @Captor
    private ArgumentCaptor<String> googleBroadcastCaptor;
    @Captor
    private ArgumentCaptor<String> twitchStreamUri;

    @Before
    public void resetTestSetup() {
        log.info("Loading specific test setup for {}...", this.getClass().getSimpleName());

        allMatches = Set.of(
                liveMatchFavTeamTourn1,
                liveMatchNotFavTeamTourn1,
                liveMatchFavTeamTourn2,
                notLiveMatchFavTeamTourn1,
                notLiveMatchNotFavTeamTourn1,
                notLiveMatchNotFavTeamTourn2,
                notLiveMatchFavTeamTourn2,
                liveMatchNotFavTeamTourn5
        );

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
    public void testWatchStreamNoResults() {
        when(matchRepository.getAllMatches()).thenReturn(Set.of(notLiveMatchFavTeamTourn1));
        when(tournamentRepository.getAllPremierTournaments()).thenReturn(premierTournaments);
        when(tournamentRepository.getAllMajorTournaments()).thenReturn(majorTournaments);
        when(tournamentRepository.getAllQualifierTournaments()).thenReturn(qualifierTournaments);
        validateMockitoUsage();

        streamOutputService.streamLiveMatch();

        verify(googleHomeOutput, times(1)).broadcast(googleBroadcastCaptor.capture());
        validateMockitoUsage();
        assertEquals("There is no match that can be streamed.", googleBroadcastCaptor.getValue());
    }

    @Test
    public void testWatchStreamNoFavoriteTeamResults() {
        // 2 matches
        // second is qualifier but by valve
        // first is major
        // second match should be streamed on tv
        when(matchRepository.getAllMatches()).thenReturn(Set.of(liveMatchNotFavTeamTourn1, liveMatchNotFavTeamTourn5));
        when(tournamentRepository.getAllPremierTournaments()).thenReturn(premierTournaments);
        when(tournamentRepository.getAllMajorTournaments()).thenReturn(majorTournaments);
        when(tournamentRepository.getAllQualifierTournaments()).thenReturn(qualifierTournaments);
        validateMockitoUsage();

        streamOutputService.streamLiveMatch();

        verify(googleHomeOutput, times(1)).broadcast(googleBroadcastCaptor.capture());
        verify(openhabOutput, times(1)).turnOnTv();
        verify(openhabOutput, times(1)).streamToTv(twitchStreamUri.capture());
        validateMockitoUsage();

        assertEquals("https://www.twitch.tv/tournament5", twitchStreamUri.getValue());
        assertEquals("Streaming EG versus NAVI.", googleBroadcastCaptor.getValue());
    }

    @Test
    public void testWatchStreamMutlipleResults() {
        when(matchRepository.getAllMatches()).thenReturn(allMatches);
        when(tournamentRepository.getAllPremierTournaments()).thenReturn(premierTournaments);
        when(tournamentRepository.getAllMajorTournaments()).thenReturn(majorTournaments);
        when(tournamentRepository.getAllQualifierTournaments()).thenReturn(qualifierTournaments);
        validateMockitoUsage();

        streamOutputService.streamLiveMatch();

        verify(googleHomeOutput, times(1)).broadcast(googleBroadcastCaptor.capture());
        verify(openhabOutput, times(1)).turnOnTv();
        verify(openhabOutput, times(1)).streamToTv(twitchStreamUri.capture());
        validateMockitoUsage();

        assertEquals("https://www.twitch.tv/tournament1", twitchStreamUri.getValue());
        assertEquals("Streaming OG versus EG.", googleBroadcastCaptor.getValue());
    }
}
