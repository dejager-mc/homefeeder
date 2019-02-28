package nl.dejagermc.homefeeder.business.reporting;


import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.TestSetup;
import nl.dejagermc.homefeeder.config.CacheManagerConfig;
import nl.dejagermc.homefeeder.input.liquipedia.dota.MatchService;
import nl.dejagermc.homefeeder.input.liquipedia.dota.TournamentService;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.TournamentType;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutput;
import nl.dejagermc.homefeeder.business.reported.ReportedService;
import nl.dejagermc.homefeeder.business.reported.model.ReportedTo;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutput;
import nl.dejagermc.homefeeder.input.homefeeder.model.HomeFeederState;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static nl.dejagermc.homefeeder.input.liquipedia.dota.builders.MatchBuilders.defaultMatch;
import static nl.dejagermc.homefeeder.input.liquipedia.dota.builders.TournamentBuilders.defaultTournament;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DotaReportService.class, ReportedService.class, HomeFeederState.class, CacheManagerConfig.class})
@EnableConfigurationProperties
@Slf4j
public class DotaReportServiceTest extends TestSetup {

    @Autowired
    private ReportedService reportedService;
    @MockBean
    private TelegramOutput telegramOutput;
    @MockBean
    private GoogleHomeOutput googleHomeOutput;
    @MockBean
    private MatchService matchService;
    @MockBean
    private TournamentService tournamentService;

    @Autowired
    private DotaReportService dotaReportService;

    @Captor
    private ArgumentCaptor<String> telegramCaptor;

    @Before
    public void resetTestSetup() {
        log.info("Loading specific test setup for {}...", this.getClass().getSimpleName());
        reportedService.resetAll();
    }

    @Test
    public void testReportLiveMatchNoLiveMatches() {
        // given a favorite team
        // given a live match with different teams
        // expect: no matches reported
        String favTeam = "OG";
        when(matchService.getLiveMatchForTeam(favTeam)).thenReturn(Optional.empty());
        validateMockitoUsage();

        dotaReportService.reportLiveMatch();
        assertTrue(!reportedService.hasThisBeenReportedToThat(null, ReportedTo.GOOGLE_HOME));
        assertTrue(!reportedService.hasThisBeenReportedToThat(null, ReportedTo.TELEGRAM));
    }

    @Test
    public void testReportLiveMatch1LiveMatch() {
        String favTeam = "OG";
        Match match = defaultMatch(favTeam, "VP", "DREAMLEAGUE", true);

        assertTrue(!reportedService.hasThisBeenReportedToThat(match, ReportedTo.GOOGLE_HOME));
        assertTrue(!reportedService.hasThisBeenReportedToThat(match, ReportedTo.TELEGRAM));

        when(matchService.getLiveMatchForTeam(favTeam)).thenReturn(Optional.of(match));
        validateMockitoUsage();

        dotaReportService.reportLiveMatch();
        verify(telegramOutput, times(1)).sendMessage(anyString());
        verify(googleHomeOutput, times(1)).broadcast(anyString());

        assertTrue(reportedService.hasThisBeenReportedToThat(match, ReportedTo.GOOGLE_HOME));
        assertTrue(reportedService.hasThisBeenReportedToThat(match, ReportedTo.TELEGRAM));
    }

    @Test
    public void testReportLiveMatch1LiveMatchCalledTwice() {
        // first time it should report the match
        // second time it should not report the match

        // setup
        String favTeam = "OG";
        Match match = defaultMatch(favTeam, "VP", "DREAMLEAGUE", true);

        assertTrue(!reportedService.hasThisBeenReportedToThat(match, ReportedTo.GOOGLE_HOME));
        assertTrue(!reportedService.hasThisBeenReportedToThat(match, ReportedTo.TELEGRAM));

        // run 1
        when(matchService.getLiveMatchForTeam(favTeam)).thenReturn(Optional.of(match));
        validateMockitoUsage();

        dotaReportService.reportLiveMatch();
        verify(telegramOutput, times(1)).sendMessage(anyString());
        verify(googleHomeOutput, times(1)).broadcast(anyString());

        assertTrue(reportedService.hasThisBeenReportedToThat(match, ReportedTo.GOOGLE_HOME));
        assertTrue(reportedService.hasThisBeenReportedToThat(match, ReportedTo.TELEGRAM));


        // run 2
        clearInvocations(telegramOutput, googleHomeOutput);
        when(matchService.getLiveMatchForTeam(favTeam)).thenReturn(Optional.of(match));
        validateMockitoUsage();

        dotaReportService.reportLiveMatch();
        verify(telegramOutput, times(0)).sendMessage(anyString());
        verify(googleHomeOutput, times(0)).broadcast(anyString());
    }

    @Test
    public void testReportLiveMatch2DifferentLiveMatch() {
        // first time it should report the match
        // second time it should report the other match

        // setup
        String favTeam = "OG";
        Match match = defaultMatch(favTeam, "VP", "DREAMLEAGUE", true);
        Match match2 = defaultMatch(favTeam, "EG", "DREAMLEAGUE", true);

        assertTrue(!reportedService.hasThisBeenReportedToThat(match, ReportedTo.GOOGLE_HOME));
        assertTrue(!reportedService.hasThisBeenReportedToThat(match, ReportedTo.TELEGRAM));
        assertTrue(!reportedService.hasThisBeenReportedToThat(match2, ReportedTo.GOOGLE_HOME));
        assertTrue(!reportedService.hasThisBeenReportedToThat(match2, ReportedTo.TELEGRAM));

        // run 1
        when(matchService.getLiveMatchForTeam(favTeam)).thenReturn(Optional.of(match));
        validateMockitoUsage();

        dotaReportService.reportLiveMatch();
        verify(telegramOutput, times(1)).sendMessage(anyString());
        verify(googleHomeOutput, times(1)).broadcast(anyString());

        assertTrue(reportedService.hasThisBeenReportedToThat(match, ReportedTo.GOOGLE_HOME));
        assertTrue(reportedService.hasThisBeenReportedToThat(match, ReportedTo.TELEGRAM));


        // run 2
        clearInvocations(telegramOutput, googleHomeOutput);
        when(matchService.getLiveMatchForTeam(favTeam)).thenReturn(Optional.of(match2));
        validateMockitoUsage();

        dotaReportService.reportLiveMatch();
        verify(telegramOutput, times(1)).sendMessage(anyString());
        verify(googleHomeOutput, times(1)).broadcast(anyString());

        assertTrue(reportedService.hasThisBeenReportedToThat(match2, ReportedTo.GOOGLE_HOME));
        assertTrue(reportedService.hasThisBeenReportedToThat(match2, ReportedTo.TELEGRAM));
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

        dotaReportService.reportTodaysMatches();
        verify(telegramOutput, times(1)).sendMessage(telegramCaptor.capture());

        List<String> telegramLines = Arrays.asList(telegramCaptor.getValue().split("\n"));
        assertEquals(telegramLines.size(), 2);
    }
}
