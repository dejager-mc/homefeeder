package nl.dejagermc.homefeeder.business.reporting;


import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.TestSetup;
import nl.dejagermc.homefeeder.business.reported.ReportedBusinessService;
import nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethod;
import nl.dejagermc.homefeeder.input.liquipedia.dota.MatchService;
import nl.dejagermc.homefeeder.input.liquipedia.dota.TournamentService;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.TournamentType;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutputService;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutputService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static nl.dejagermc.homefeeder.input.liquipedia.dota.builders.MatchBuilders.defaultMatch;
import static nl.dejagermc.homefeeder.input.liquipedia.dota.builders.TournamentBuilders.defaultTournament;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@Slf4j
public class DotaReportBusinessServiceTest extends TestSetup {

    @Autowired
    private ReportedBusinessService reportedBusinessService;
    @MockBean
    private TelegramOutputService telegramOutputService;
    @MockBean
    private GoogleHomeOutputService googleHomeOutputService;
    @MockBean
    private MatchService matchService;
    @MockBean
    private TournamentService tournamentService;

    @Autowired
    private DotaReportBusinessService dotaReportBusinessService;

    @Captor
    private ArgumentCaptor<String> telegramCaptor;

    @Before
    public void resetTestSetup() {
        log.info("Loading specific test setup for {}...", this.getClass().getSimpleName());
        reportedBusinessService.resetAll();
    }

    @Test
    public void testReportLiveMatchNoLiveMatches() {
        // given a favorite team
        // given a live match with different teams
        // expect: no matches reported
        String favTeam = "OG";
        when(matchService.getLiveMatchForTeam(favTeam)).thenReturn(Optional.empty());
        validateMockitoUsage();

        dotaReportBusinessService.reportLiveMatchesFavoriteTeams();
        assertTrue(!reportedBusinessService.hasThisBeenReportedToThat(null, ReportMethod.GOOGLE_HOME));
        assertTrue(!reportedBusinessService.hasThisBeenReportedToThat(null, ReportMethod.TELEGRAM));
    }

    @Test
    public void testReportLiveMatch1LiveMatch() {
        String favTeam = "OG";
        Match match = defaultMatch(favTeam, "VP", "DREAMLEAGUE", true);

        assertTrue(!reportedBusinessService.hasThisBeenReportedToThat(match, ReportMethod.GOOGLE_HOME));
        assertTrue(!reportedBusinessService.hasThisBeenReportedToThat(match, ReportMethod.TELEGRAM));

        when(matchService.getLiveMatchForTeam(favTeam)).thenReturn(Optional.of(match));
        validateMockitoUsage();

        dotaReportBusinessService.reportLiveMatchesFavoriteTeams();
        verify(telegramOutputService, times(1)).sendMessage(anyString());
        verify(googleHomeOutputService, times(1)).broadcast(anyString());

        assertTrue(reportedBusinessService.hasThisBeenReportedToThat(match, ReportMethod.GOOGLE_HOME));
        assertTrue(reportedBusinessService.hasThisBeenReportedToThat(match, ReportMethod.TELEGRAM));
    }

    @Test
    public void testReportLiveMatch1LiveMatchCalledTwice() {
        // first time it should report the match
        // second time it should not report the match

        // setup
        String favTeam = "OG";
        Match match = defaultMatch(favTeam, "VP", "DREAMLEAGUE", true);

        assertTrue(!reportedBusinessService.hasThisBeenReportedToThat(match, ReportMethod.GOOGLE_HOME));
        assertTrue(!reportedBusinessService.hasThisBeenReportedToThat(match, ReportMethod.TELEGRAM));

        // run 1
        when(matchService.getLiveMatchForTeam(favTeam)).thenReturn(Optional.of(match));
        validateMockitoUsage();

        dotaReportBusinessService.reportLiveMatchesFavoriteTeams();
        verify(telegramOutputService, times(1)).sendMessage(anyString());
        verify(googleHomeOutputService, times(1)).broadcast(anyString());

        assertTrue(reportedBusinessService.hasThisBeenReportedToThat(match, ReportMethod.GOOGLE_HOME));
        assertTrue(reportedBusinessService.hasThisBeenReportedToThat(match, ReportMethod.TELEGRAM));


        // run 2
        clearInvocations(telegramOutputService, googleHomeOutputService);
        when(matchService.getLiveMatchForTeam(favTeam)).thenReturn(Optional.of(match));
        validateMockitoUsage();

        dotaReportBusinessService.reportLiveMatchesFavoriteTeams();
        verify(telegramOutputService, times(0)).sendMessage(anyString());
        verify(googleHomeOutputService, times(0)).broadcast(anyString());
    }

    @Test
    public void testReportLiveMatch2DifferentLiveMatch() {
        // first time it should report the match
        // second time it should report the other match

        // setup
        String favTeam = "OG";
        Match match = defaultMatch(favTeam, "VP", "DREAMLEAGUE", true);
        Match match2 = defaultMatch(favTeam, "EG", "DREAMLEAGUE", true);

        assertTrue(!reportedBusinessService.hasThisBeenReportedToThat(match, ReportMethod.GOOGLE_HOME));
        assertTrue(!reportedBusinessService.hasThisBeenReportedToThat(match, ReportMethod.TELEGRAM));
        assertTrue(!reportedBusinessService.hasThisBeenReportedToThat(match2, ReportMethod.GOOGLE_HOME));
        assertTrue(!reportedBusinessService.hasThisBeenReportedToThat(match2, ReportMethod.TELEGRAM));

        // run 1
        when(matchService.getLiveMatchForTeam(favTeam)).thenReturn(Optional.of(match));
        validateMockitoUsage();

        dotaReportBusinessService.reportLiveMatchesFavoriteTeams();
        verify(telegramOutputService, times(1)).sendMessage(anyString());
        verify(googleHomeOutputService, times(1)).broadcast(anyString());

        assertTrue(reportedBusinessService.hasThisBeenReportedToThat(match, ReportMethod.GOOGLE_HOME));
        assertTrue(reportedBusinessService.hasThisBeenReportedToThat(match, ReportMethod.TELEGRAM));


        // run 2
        clearInvocations(telegramOutputService, googleHomeOutputService);
        when(matchService.getLiveMatchForTeam(favTeam)).thenReturn(Optional.of(match2));
        validateMockitoUsage();

        dotaReportBusinessService.reportLiveMatchesFavoriteTeams();
        verify(telegramOutputService, times(1)).sendMessage(anyString());
        verify(googleHomeOutputService, times(1)).broadcast(anyString());

        assertTrue(reportedBusinessService.hasThisBeenReportedToThat(match2, ReportMethod.GOOGLE_HOME));
        assertTrue(reportedBusinessService.hasThisBeenReportedToThat(match2, ReportMethod.TELEGRAM));
    }

    @Test
    public void testReportTodaysMatchesPremierOnly() {
        // 1 match is aanwezig
        // 1 premier tournament is aanwezig
        String tournamentName = "DREAMLEAGUE";
        Match match = defaultMatch("OG", "EG", tournamentName, true);
        Tournament tournament = defaultTournament(tournamentName, TournamentType.PREMIER, true, true);

        when(tournamentService.getAllActiveTournamentsForType(TournamentType.PREMIER)).thenReturn(List.of(tournament));
        when(matchService.getTodaysMatchesForTournament(tournamentName)).thenReturn(List.of(match));
        validateMockitoUsage();

        dotaReportBusinessService.reportTodaysMatches();
        verify(telegramOutputService, times(1)).sendMessage(telegramCaptor.capture());

        List<String> telegramLines = Arrays.asList(telegramCaptor.getValue().split("\n"));
        assertEquals(3, telegramLines.size());
    }
}
