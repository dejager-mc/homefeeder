package nl.dejagermc.homefeeder.schedulers;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.TestSetup;
import nl.dejagermc.homefeeder.business.reported.ReportedBusinessService;
import nl.dejagermc.homefeeder.business.reporting.DotaReportBusinessService;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethod;
import nl.dejagermc.homefeeder.input.liquipedia.dota.MatchService;
import nl.dejagermc.homefeeder.input.liquipedia.dota.TournamentService;
import nl.dejagermc.homefeeder.input.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.input.liquipedia.dota.repository.MatchRepository;
import nl.dejagermc.homefeeder.input.liquipedia.dota.repository.TournamentRepository;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutputService;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutputService;
import nl.dejagermc.homefeeder.schudulers.DotaScheduler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Set;

import static nl.dejagermc.homefeeder.input.liquipedia.dota.builders.MatchBuilders.defaultMatch;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Slf4j
public class DotaSchedulerTest extends TestSetup {

    @MockBean
    private TournamentRepository tournamentRepository;
    @MockBean
    private MatchRepository matchRepository;
    @MockBean
    private TelegramOutputService telegramOutputService;
    @MockBean
    private GoogleHomeOutputService googleHomeOutputService;

    @Autowired
    private DotaScheduler dotaScheduler;
    @Autowired
    private DotaReportBusinessService dotaReportBusinessService;
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private ReportedBusinessService reportedBusinessService;
    @Autowired
    private MatchService matchService;
    @Autowired
    private TournamentService tournamentService;

    @Captor
    private ArgumentCaptor<String> telegramCaptor;

    @Before
    public void resetTestSetup() {
        log.info("resetting getAllDeliveries setup");
        reportedBusinessService.resetAll();
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
        assertTrue(!reportedBusinessService.hasThisBeenReportedToThat(match1, ReportMethod.GOOGLE_HOME));
        assertTrue(!reportedBusinessService.hasThisBeenReportedToThat(match1, ReportMethod.TELEGRAM));

        when(matchRepository.getAllMatches()).thenReturn(Set.of(match1));
        validateMockitoUsage();
        // run 1
        dotaScheduler.reportLiveMatches();

        verify(telegramOutputService, times(1)).sendMessage(anyString());
        verify(googleHomeOutputService, times(1)).broadcast(anyString());
        validateMockitoUsage();
        assertTrue(reportedBusinessService.hasThisBeenReportedToThat(match1, ReportMethod.GOOGLE_HOME));
        assertTrue(reportedBusinessService.hasThisBeenReportedToThat(match1, ReportMethod.TELEGRAM));


        // run 2
        clearInvocations(telegramOutputService, googleHomeOutputService);
        Match match2 = defaultMatch(teamLeft, teamRight, tournamentName, true);
        when(matchRepository.getAllMatches()).thenReturn(Set.of(match2));
        validateMockitoUsage();

        dotaScheduler.reportLiveMatches();

        verify(telegramOutputService, times(0)).sendMessage(anyString());
        verify(googleHomeOutputService, times(0)).broadcast(anyString());
        validateMockitoUsage();
    }
}
