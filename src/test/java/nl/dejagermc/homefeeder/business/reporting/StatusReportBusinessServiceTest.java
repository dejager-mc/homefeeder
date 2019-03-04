package nl.dejagermc.homefeeder.business.reporting;

import com.netflix.discovery.converters.Auto;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import nl.dejagermc.homefeeder.TestSetup;
import nl.dejagermc.homefeeder.business.reported.ReportedBusinessService;
import nl.dejagermc.homefeeder.domain.generated.radarr.RadarrWebhookSchema;
import nl.dejagermc.homefeeder.domain.generated.sonarr.SonarrWebhookSchema;
import nl.dejagermc.homefeeder.input.liquipedia.dota.MatchService;
import nl.dejagermc.homefeeder.input.liquipedia.dota.TournamentService;
import nl.dejagermc.homefeeder.input.radarr.RadarrService;
import nl.dejagermc.homefeeder.input.sonarr.SonarrService;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutput;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutput;
import nl.dejagermc.homefeeder.web.RadarrController;
import nl.dejagermc.homefeeder.web.SonarrController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Set;

import static nl.dejagermc.homefeeder.input.radarr.RadarrBuilder.getDefaultRadarrSchema;
import static nl.dejagermc.homefeeder.input.radarr.SonarrBuilder.getDefaultSonarrSchema;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Slf4j
public class StatusReportBusinessServiceTest extends TestSetup {

    @MockBean
    private TelegramOutput telegramOutput;
    @MockBean
    private GoogleHomeOutput googleHomeOutput;

    @Autowired
    private ReportedBusinessService reportedBusinessService;
    @Autowired
    private StatusReportBusinessService statusReportBusinessService;
    @Autowired
    private RadarrController radarrController;
    @Autowired
    private SonarrController sonarrController;
    @Autowired
    private RadarrService radarrService;
    @Autowired
    private SonarrService sonarrService;

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
    public void testReportSavedSonarrMessagesToGoogleHomeBecauseSleeping() {
        /*
        When user is sleeping
        When Sonarr message is received
        Then Telegram message is send
        Then no google home message is send
        When reporting saved messages
        Then google home message is send
         */

        SonarrWebhookSchema schema = getDefaultSonarrSchema();

        // When user is sleeping and sonarr message is received
        settingsService.getOpenHabSettings().setSleeping(true);
        sonarrController.addSonarr(schema);

        // Then telegram is send but not google home
        validateMockitoUsage();
        verify(telegramOutput, times(1)).sendMessage(anyString());
        verify(googleHomeOutput, times(0)).broadcast(anyString());
        clearInvocations(telegramOutput, googleHomeOutput);

        Set<SonarrWebhookSchema> schemas = sonarrService.getNotYetReported();
        assertEquals(schemas.size(), 1);
        assertTrue(schemas.contains(schema));

        // mock google home output
        when(googleHomeOutput.broadcast(anyString())).thenReturn(true);
        validateMockitoUsage();

        // when reporting saved messages
        statusReportBusinessService.reportSavedMessagesToGoogleHome();

        // then google home message is send
        verify(telegramOutput, times(0)).sendMessage(anyString());
        verify(googleHomeOutput, times(1)).broadcast(anyString());
        clearInvocations(telegramOutput, googleHomeOutput);

        schemas = sonarrService.getNotYetReported();
        assertEquals(schemas.size(), 0);
    }

    @Test
    public void testReportSavedRadarrMessagesToGoogleHomeBecauseSleeping() {
        /*
        When user is sleeping
        When Radarr message is received
        Then Telegram message is send
        Then no google home message is send
        When reporting saved messages
        Then google home message is send
         */

        RadarrWebhookSchema schema = getDefaultRadarrSchema();

        // When user is sleeping and radarr message is received
        settingsService.getOpenHabSettings().setSleeping(true);
        radarrController.addRadarr(schema);

        // Then telegram is send but not google home
        validateMockitoUsage();
        verify(telegramOutput, times(1)).sendMessage(anyString());
        verify(googleHomeOutput, times(0)).broadcast(anyString());
        clearInvocations(telegramOutput, googleHomeOutput);

        Set<RadarrWebhookSchema> schemas = radarrService.getNotYetReported();
        assertEquals(schemas.size(), 1);
        assertTrue(schemas.contains(schema));

        // mock google home output
        when(googleHomeOutput.broadcast(anyString())).thenReturn(true);
        validateMockitoUsage();

        // when reporting saved messages
        statusReportBusinessService.reportSavedMessagesToGoogleHome();

        // then google home message is send
        verify(telegramOutput, times(0)).sendMessage(anyString());
        verify(googleHomeOutput, times(1)).broadcast(anyString());
        clearInvocations(telegramOutput, googleHomeOutput);

        schemas = radarrService.getNotYetReported();
        assertEquals(schemas.size(), 0);
    }
}
