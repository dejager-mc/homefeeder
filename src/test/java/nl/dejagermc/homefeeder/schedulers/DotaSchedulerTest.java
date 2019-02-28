package nl.dejagermc.homefeeder.schedulers;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.TestSetup;
import nl.dejagermc.homefeeder.business.reporting.DotaReportService;
import nl.dejagermc.homefeeder.config.CacheManagerConfig;
import nl.dejagermc.homefeeder.input.liquipedia.dota.MatchService;
import nl.dejagermc.homefeeder.input.liquipedia.dota.TournamentService;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.input.liquipedia.dota.repository.MatchRepository;
import nl.dejagermc.homefeeder.input.liquipedia.dota.repository.TournamentRepository;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutput;
import nl.dejagermc.homefeeder.business.reported.ReportedService;
import nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethods;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutput;
import nl.dejagermc.homefeeder.schudulers.DotaScheduler;
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

import java.util.Set;

import static nl.dejagermc.homefeeder.input.liquipedia.dota.builders.MatchBuilders.defaultMatch;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DotaScheduler.class, DotaReportService.class, HomeFeederState.class, ReportedService.class,
        MatchService.class, TournamentService.class, CacheManagerConfig.class})
@EnableConfigurationProperties
@Slf4j
public class DotaSchedulerTest extends TestSetup {

    @MockBean
    private TournamentRepository tournamentRepository;
    @MockBean
    private MatchRepository matchRepository;
    @MockBean
    private TelegramOutput telegramOutput;
    @MockBean
    private GoogleHomeOutput googleHomeOutput;

    @Autowired
    private DotaScheduler dotaScheduler;
    @Autowired
    private DotaReportService dotaReportService;
    @Autowired
    private HomeFeederState homeFeederState;
    @Autowired
    private ReportedService reportedService;
    @Autowired
    private MatchService matchService;
    @Autowired
    private TournamentService tournamentService;

    @Captor
    private ArgumentCaptor<String> telegramCaptor;

    @Before
    public void resetTestSetup() {
        log.info("resetting test setup");
        reportedService.resetAll();
    }

    @Test
    public void testLiveMatchesNotHappeningTwiceForSameMatch() {
        // laden van matches, rapporteer 1 match
        // laden van matches opnieuw, check dat dezelfde match niet nog een keer wordt gerapporteerd
        String tournamentName = "ESL ONE";
        String teamLeft = "OG";
        String teamRight = "VP";
//        Tournament tournament1 = getTournament(tournamentName, TournamentType.PREMIER, true);
        Match match1 = defaultMatch(teamLeft, teamRight, tournamentName, true);

        // check begin state is correct
        assertTrue(!reportedService.hasThisBeenReportedToThat(match1, ReportMethods.GOOGLE_HOME));
        assertTrue(!reportedService.hasThisBeenReportedToThat(match1, ReportMethods.TELEGRAM));

        when(matchRepository.getAllMatches()).thenReturn(Set.of(match1));
        validateMockitoUsage();
        // run 1
        dotaScheduler.reportLiveMatches();

        verify(telegramOutput, times(1)).sendMessage(anyString());
        verify(googleHomeOutput, times(1)).broadcast(anyString());
        validateMockitoUsage();
        assertTrue(reportedService.hasThisBeenReportedToThat(match1, ReportMethods.GOOGLE_HOME));
        assertTrue(reportedService.hasThisBeenReportedToThat(match1, ReportMethods.TELEGRAM));


        // run 2
        clearInvocations(telegramOutput, googleHomeOutput);
        Match match2 = defaultMatch(teamLeft, teamRight, tournamentName, true);
        when(matchRepository.getAllMatches()).thenReturn(Set.of(match2));
        validateMockitoUsage();

        dotaScheduler.reportLiveMatches();

        verify(telegramOutput, times(0)).sendMessage(anyString());
        verify(googleHomeOutput, times(0)).broadcast(anyString());
        validateMockitoUsage();
    }
}
