package nl.dejagermc.homefeeder.schedulers;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.business.DotaReportService;
import nl.dejagermc.homefeeder.config.CacheManagerConfig;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.MatchService;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.TournamentService;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.TournamentType;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.repository.MatchRepository;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.repository.TournamentRepository;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeReporter;
import nl.dejagermc.homefeeder.output.reported.ReportedService;
import nl.dejagermc.homefeeder.output.reported.model.ReportedTo;
import nl.dejagermc.homefeeder.output.telegram.TelegramReporter;
import nl.dejagermc.homefeeder.schudulers.DotaScheduler;
import nl.dejagermc.homefeeder.user.UserState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DotaScheduler.class, DotaReportService.class, UserState.class, ReportedService.class,
        MatchService.class, TournamentService.class, CacheManagerConfig.class})
@EnableConfigurationProperties
@Slf4j
public class DotaSchedulerTest {

    @MockBean
    private TournamentRepository tournamentRepository;
    @MockBean
    private MatchRepository matchRepository;
    @MockBean
    private TelegramReporter telegramReporter;
    @MockBean
    private GoogleHomeReporter googleHomeReporter;

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private DotaScheduler dotaScheduler;
    @Autowired
    private DotaReportService dotaReportService;
    @Autowired
    private UserState userState;
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
        userState.useTelegram(true);
        userState.useGoogleHome(true);
        userState.favoriteTeams(Arrays.asList("OG"));
        userState.isHome(true);
        userState.isMute(false);
        userState.isSleeping(false);
        reportedService.resetAll();
        cacheManager.getCache("getAllMatches").clear();
    }

    @Test
    public void testLiveMatchesNotHappeningTwiceForSameMatch() {
        // laden van matches, rapporteer 1 match
        // laden van matches opnieuw, check dat dezelfde match niet nog een keer wordt gerapporteerd
        String tournamentName = "ESL ONE";
        String teamLeft = "OG";
        String teamRight = "VP";
//        Tournament tournament1 = getTournament(tournamentName, TournamentType.PREMIER, true);
        Match match1 = getMatch(teamLeft, teamRight, tournamentName, true);

        // check begin state is correct
        assertTrue(!reportedService.hasThisBeenReported(match1, ReportedTo.GOOGLE_HOME));
        assertTrue(!reportedService.hasThisBeenReported(match1, ReportedTo.TELEGRAM));

        when(matchRepository.getAllMatches()).thenReturn(Arrays.asList(match1));
//        when(tournamentRepository.getAllPremierTournaments()).thenReturn(Arrays.asList(tournament1));
        validateMockitoUsage();
        // run 1
        dotaScheduler.liveMatches();

        verify(telegramReporter, times(1)).sendMessage(anyString());
        verify(googleHomeReporter, times(1)).broadcast(anyString());
        validateMockitoUsage();
        assertTrue(reportedService.hasThisBeenReported(match1, ReportedTo.GOOGLE_HOME));
        assertTrue(reportedService.hasThisBeenReported(match1, ReportedTo.TELEGRAM));


        // run 2
        clearInvocations(telegramReporter, googleHomeReporter);
        Match match2 = getMatch(teamLeft, teamRight, tournamentName, true);
        when(matchRepository.getAllMatches()).thenReturn(Arrays.asList(match2));
        validateMockitoUsage();

        dotaScheduler.liveMatches();

        verify(telegramReporter, times(0)).sendMessage(anyString());
        verify(googleHomeReporter, times(0)).broadcast(anyString());
        validateMockitoUsage();
    }

    private Match getMatch(String leftTeam, String rightTeam, String tournamentName, boolean isLive) {
        LocalDateTime time;
        if (isLive) {
            time = LocalDateTime.now();
        } else {
            time = LocalDateTime.now().plusHours(1);
        }

        return Match.builder()
                .matchTime(time)
                .gameType("Bo3")
                .leftTeam(leftTeam)
                .rightTeam(rightTeam)
                .tournamentName(tournamentName)
                .twitchChannel(tournamentName)
                .youtubeChannel("")
                .build();
    }

    private Tournament getTournament(String name, TournamentType tournamentType, boolean active) {
        LocalDateTime time;
        if (active) {
            time = LocalDateTime.now();
        } else {
            time = LocalDateTime.now().plusMonths(2);
        }

        return Tournament.builder()
                .isByValve(true)
                .start(time.minusDays(5))
                .end(time.plusDays(5))
                .name(name)
                .teams(2)
                .location("London")
                .prize(5)
                .tournamentType(tournamentType)
                .winner("")
                .build();
    }
}
