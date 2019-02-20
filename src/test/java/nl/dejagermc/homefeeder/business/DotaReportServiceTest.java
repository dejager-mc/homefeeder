package nl.dejagermc.homefeeder.business;


import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.MatchService;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.TournamentService;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.TournamentType;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeReporter;
import nl.dejagermc.homefeeder.output.reported.ReportedService;
import nl.dejagermc.homefeeder.output.reported.model.ReportedTo;
import nl.dejagermc.homefeeder.output.telegram.TelegramReporter;
import nl.dejagermc.homefeeder.user.UserState;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DotaReportService.class, ReportedService.class, UserState.class})
@EnableConfigurationProperties
@Slf4j
public class DotaReportServiceTest {

    @Autowired
    private UserState userState;
    @Autowired
    private ReportedService reportedService;
    @MockBean
    private TelegramReporter telegramReporter;
    @MockBean
    private GoogleHomeReporter googleHomeReporter;
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
        log.info("resetting test setup");
        userState.useTelegram(true);
        userState.useGoogleHome(true);
        userState.favoriteTeams(Arrays.asList("OG"));
        userState.isHome(true);
        userState.isMute(false);
        userState.isSleeping(false);
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
        assertTrue(!reportedService.hasThisBeenReported(null, ReportedTo.GOOGLE_HOME));
        assertTrue(!reportedService.hasThisBeenReported(null, ReportedTo.TELEGRAM));
    }

    @Test
    public void testReportLiveMatch1LiveMatch() {
        String favTeam = "OG";
        Match match = getMatchForTeam(favTeam, "VP");

        assertTrue(!reportedService.hasThisBeenReported(match, ReportedTo.GOOGLE_HOME));
        assertTrue(!reportedService.hasThisBeenReported(match, ReportedTo.TELEGRAM));

        when(matchService.getLiveMatchForTeam(favTeam)).thenReturn(Optional.of(match));
        validateMockitoUsage();

        dotaReportService.reportLiveMatch();
        verify(telegramReporter, times(1)).sendMessage(anyString());
        verify(googleHomeReporter, times(1)).broadcast(anyString());

        assertTrue(reportedService.hasThisBeenReported(match, ReportedTo.GOOGLE_HOME));
        assertTrue(reportedService.hasThisBeenReported(match, ReportedTo.TELEGRAM));
    }

    @Test
    public void testReportLiveMatch1LiveMatchCalledTwice() {
        // first time it should report the match
        // second time it should not report the match

        // setup
        String favTeam = "OG";
        Match match = getMatchForTeam(favTeam, "VP");
//        Match match2 = getMatchForTeam(favTeam, "EG");

        assertTrue(!reportedService.hasThisBeenReported(match, ReportedTo.GOOGLE_HOME));
        assertTrue(!reportedService.hasThisBeenReported(match, ReportedTo.TELEGRAM));

        // run 1
        when(matchService.getLiveMatchForTeam(favTeam)).thenReturn(Optional.of(match));
        validateMockitoUsage();

        dotaReportService.reportLiveMatch();
        verify(telegramReporter, times(1)).sendMessage(anyString());
        verify(googleHomeReporter, times(1)).broadcast(anyString());

        assertTrue(reportedService.hasThisBeenReported(match, ReportedTo.GOOGLE_HOME));
        assertTrue(reportedService.hasThisBeenReported(match, ReportedTo.TELEGRAM));


        // run 2
        clearInvocations(telegramReporter, googleHomeReporter);
        when(matchService.getLiveMatchForTeam(favTeam)).thenReturn(Optional.of(match));
        validateMockitoUsage();

        dotaReportService.reportLiveMatch();
        verify(telegramReporter, times(0)).sendMessage(anyString());
        verify(googleHomeReporter, times(0)).broadcast(anyString());
    }

    @Test
    public void testReportLiveMatch2DifferentLiveMatch() {
        // first time it should report the match
        // second time it should report the other match

        // setup
        String favTeam = "OG";
        Match match = getMatchForTeam(favTeam, "VP");
        Match match2 = getMatchForTeam(favTeam, "EG");

        assertTrue(!reportedService.hasThisBeenReported(match, ReportedTo.GOOGLE_HOME));
        assertTrue(!reportedService.hasThisBeenReported(match, ReportedTo.TELEGRAM));
        assertTrue(!reportedService.hasThisBeenReported(match2, ReportedTo.GOOGLE_HOME));
        assertTrue(!reportedService.hasThisBeenReported(match2, ReportedTo.TELEGRAM));

        // run 1
        when(matchService.getLiveMatchForTeam(favTeam)).thenReturn(Optional.of(match));
        validateMockitoUsage();

        dotaReportService.reportLiveMatch();
        verify(telegramReporter, times(1)).sendMessage(anyString());
        verify(googleHomeReporter, times(1)).broadcast(anyString());

        assertTrue(reportedService.hasThisBeenReported(match, ReportedTo.GOOGLE_HOME));
        assertTrue(reportedService.hasThisBeenReported(match, ReportedTo.TELEGRAM));


        // run 2
        clearInvocations(telegramReporter, googleHomeReporter);
        when(matchService.getLiveMatchForTeam(favTeam)).thenReturn(Optional.of(match2));
        validateMockitoUsage();

        dotaReportService.reportLiveMatch();
        verify(telegramReporter, times(1)).sendMessage(anyString());
        verify(googleHomeReporter, times(1)).broadcast(anyString());

        assertTrue(reportedService.hasThisBeenReported(match2, ReportedTo.GOOGLE_HOME));
        assertTrue(reportedService.hasThisBeenReported(match2, ReportedTo.TELEGRAM));
    }
    @Test
    public void testReportTodaysMatchesPremierOnly() {
        // 1 match is aanwezig
        // 1 premier tournament is aanwezig
        String tournamentName = "DREAMLEAGUE";
        Match match = getMatchForTeam("OG", "EG");
        Tournament tournament = getPremierTournament(tournamentName, TournamentType.PREMIER);

        when(tournamentService.getAllActiveTournamentsForType(TournamentType.PREMIER)).thenReturn(Arrays.asList(tournament));
        when(matchService.getTodaysMatchesForTournament(tournamentName)).thenReturn(Arrays.asList(match));
        validateMockitoUsage();

        dotaReportService.reportTodaysMatches();
        verify(telegramReporter, times(1)).sendMessage(telegramCaptor.capture());

        List<String> telegramLines = Arrays.asList(telegramCaptor.getValue().split("\n"));
        assertTrue(telegramLines.size()==3);
    }

    private Match getMatchForTeam(String leftTeam, String rightTeam) {
        return Match.builder()
                .matchTime(LocalDateTime.now())
                .gameType("")
                .leftTeam(leftTeam)
                .rightTeam(rightTeam)
                .tournamentName("DREAMLEAGUE")
                .twitchChannel("dreamleauge")
                .youtubeChannel("")
                .build();
    }

    private Tournament getPremierTournament(String name, TournamentType tournamentType) {
        return Tournament.builder()
                .isByValve(true)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().plusDays(5))
                .name(name)
                .teams(2)
                .location("London")
                .prize(5)
                .tournamentType(tournamentType)
                .winner("")
                .build();
    }

}
