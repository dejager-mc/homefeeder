package nl.dejagermc.homefeeder.business.dialogflow;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.TestSetup;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.TournamentType;
import nl.dejagermc.homefeeder.input.liquipedia.dota.repository.MatchRepository;
import nl.dejagermc.homefeeder.input.liquipedia.dota.repository.TournamentRepository;
import nl.dejagermc.homefeeder.input.openhab.model.OpenhabItem;
import nl.dejagermc.homefeeder.input.openhab.repository.OpenhabItemRepository;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutputService;
import nl.dejagermc.homefeeder.output.openhab.OpenhabOutputService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Set;

import static nl.dejagermc.homefeeder.business.dialogflow.builder.DialogflowRequestBuilder.dialogflowRequest;
import static nl.dejagermc.homefeeder.input.liquipedia.dota.builders.MatchBuilders.defaultMatch;
import static nl.dejagermc.homefeeder.input.liquipedia.dota.builders.TournamentBuilders.defaultTournament;
import static nl.dejagermc.homefeeder.input.openhab.builders.OpenhabItemBuilders.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@Slf4j
public class DialogflowBusinessServiceTest extends TestSetup {

    private OpenhabItem tvItem = tvItem("1");
    private OpenhabItem tvStreamItem = tvStreamItem("1");
    private OpenhabItem tvItem2 = tvItem("2");
    private OpenhabItem tvStreamItem2 = tvStreamItem("2");
    private OpenhabItem switchItem = switchItem();
    private OpenhabItem stringItem = stringItem();
    private OpenhabItem kitchenLights = kitchenLightsItem();
    private Set<OpenhabItem> openhabItems;

    private Set<Match> allMatches;
    private Set<Tournament> premierTournaments;
    private Set<Tournament> majorTournaments;
    private Set<Tournament> qualifierTournaments;

    private Match liveMatchFavTeamTourn1 = defaultMatch("OG", "EG", "tournament1", true);
    private Tournament tournament1Active = defaultTournament("tournament1", TournamentType.PREMIER, true, false);

    @Autowired
    private DialogflowBusinessService dialogflowBusinessService;

    @MockBean
    private GoogleHomeOutputService googleHomeOutputService;
    @MockBean
    private OpenhabItemRepository openhabItemRepository;
    @MockBean
    private OpenhabOutputService openhabOutputService;
    @MockBean
    private MatchRepository matchRepository;
    @MockBean
    private TournamentRepository tournamentRepository;

    @Captor
    private ArgumentCaptor<String> googleBroadcastCaptor;
    @Captor
    private ArgumentCaptor<String> twitchStreamUri;
    @Captor
    private ArgumentCaptor<OpenhabItem> openhabItem;

    @Before
    public void resetTestSetup() {
        log.info("Loading specific test setup for {}...", this.getClass().getSimpleName());

        openhabItems = Set.of(
                tvItem,
                tvItem2,
                tvStreamItem,
                tvStreamItem2,
                switchItem,
                stringItem,
                kitchenLights
        );

        allMatches = Set.of(liveMatchFavTeamTourn1);
        premierTournaments = Set.of(tournament1Active);
        majorTournaments = Set.of();
        qualifierTournaments = Set.of();
    }

    @Test
    public void testRequestSwitchOn() {
        List<String> items = List.of("kitchen lights");

        when(openhabItemRepository.getAllOpenhabItems()).thenReturn(openhabItems);
        when(openhabOutputService.performActionOnSwitchItem("ON", kitchenLights)).thenReturn(true);
        validateMockitoUsage();

        dialogflowBusinessService.handleRequest(dialogflowRequest("ON", items));

        verify(openhabOutputService, times(1)).performActionOnSwitchItem("ON", kitchenLights);
        validateMockitoUsage();
    }

    @Test
    public void testRequestSwitchMultipleOn() {
        List<String> items = List.of("kitchen lights", "sony tv 1");

        when(openhabItemRepository.getAllOpenhabItems()).thenReturn(openhabItems);
        when(openhabOutputService.performActionOnSwitchItem("ON", kitchenLights)).thenReturn(true);
        when(openhabOutputService.performActionOnSwitchItem("ON", tvItem)).thenReturn(true);
        validateMockitoUsage();

        dialogflowBusinessService.handleRequest(dialogflowRequest("ON", items));

        verify(openhabOutputService, times(1)).performActionOnSwitchItem("ON", kitchenLights);
        verify(openhabOutputService, times(1)).performActionOnSwitchItem("ON", tvItem);
        validateMockitoUsage();
    }

    @Test
    public void testRequestSwitchOff() {
        List<String> items = List.of("kitchen lights");

        when(openhabItemRepository.getAllOpenhabItems()).thenReturn(openhabItems);
        when(openhabOutputService.performActionOnSwitchItem("OFF", kitchenLights)).thenReturn(true);
        validateMockitoUsage();

        dialogflowBusinessService.handleRequest(dialogflowRequest("OFF", items));

        verify(openhabOutputService, times(1)).performActionOnSwitchItem("OFF", kitchenLights);
        validateMockitoUsage();
    }

    @Test
    public void testRequestWatchDotaStream() {
        List<String> items = List.of("sony tv 1");

        // match mocks
        when(matchRepository.getAllMatches()).thenReturn(allMatches);
        when(tournamentRepository.getAllPremierTournaments()).thenReturn(premierTournaments);
        when(tournamentRepository.getAllMajorTournaments()).thenReturn(majorTournaments);
        when(tournamentRepository.getAllQualifierTournaments()).thenReturn(qualifierTournaments);
        // rest
        when(openhabItemRepository.getAllOpenhabItems()).thenReturn(openhabItems);
        when(openhabOutputService.performActionOnSwitchItem("ON", tvItem)).thenReturn(true);
        when(openhabOutputService.performActionOnStringItem("ON", tvStreamItem)).thenReturn(true);
        validateMockitoUsage();

        dialogflowBusinessService.handleRequest(dialogflowRequest("stream dota", items));

        verify(googleHomeOutputService, times(1)).broadcast(googleBroadcastCaptor.capture());
        verify(openhabOutputService, times(1)).performActionOnSwitchItem("ON", tvItem);
        verify(openhabOutputService, times(1)).performActionOnStringItem(twitchStreamUri.capture(), openhabItem.capture());
        validateMockitoUsage();

        assertEquals(tvStreamItem, openhabItem.getValue());
        assertEquals("https://www.twitch.tv/tournament1", twitchStreamUri.getValue());
        assertEquals("Streaming OG versus EG.", googleBroadcastCaptor.getValue());
    }
}
