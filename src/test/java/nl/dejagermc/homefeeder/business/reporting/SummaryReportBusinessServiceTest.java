package nl.dejagermc.homefeeder.business.reporting;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.TestSetup;
import nl.dejagermc.homefeeder.business.reported.ReportedBusinessService;
import nl.dejagermc.homefeeder.domain.generated.radarr.RadarrWebhookSchema;
import nl.dejagermc.homefeeder.domain.generated.sonarr.SonarrWebhookSchema;
import nl.dejagermc.homefeeder.input.radarr.RadarrService;
import nl.dejagermc.homefeeder.input.sonarr.SonarrService;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutputService;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutputService;
import nl.dejagermc.homefeeder.startup.StartupCheck;
import nl.dejagermc.homefeeder.util.http.HttpUtil;
import nl.dejagermc.homefeeder.web.RadarrController;
import nl.dejagermc.homefeeder.web.SonarrController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.Set;

import static nl.dejagermc.homefeeder.input.radarr.RadarrBuilder.getDefaultRadarrSchema;
import static nl.dejagermc.homefeeder.input.sonarr.SonarrBuilder.getDefaultSonarrSchema;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Slf4j
public class SummaryReportBusinessServiceTest extends TestSetup {

    @MockBean
    private TelegramOutputService telegramOutputService;
    @MockBean
    private GoogleHomeOutputService googleHomeOutputService;
    @MockBean
    private HttpUtil httpUtil;
    @MockBean
    private StartupCheck startupCheck;

    @Autowired
    private ReportedBusinessService reportedBusinessService;
    @Autowired
    private SummaryReportBusinessService summaryReportBusinessService;
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

        // When user is not listening and sonarr message is received
        settingsService.getOpenHabSettings().setListening(false);
        sonarrController.addSonarr(schema);
        doNothing().when(telegramOutputService).sendMessage(anyString());

        // Then telegram is send but not google home
        validateMockitoUsage();
        verify(telegramOutputService, times(1)).sendMessage(anyString());
        verify(googleHomeOutputService, times(0)).broadcast(anyString());
        clearInvocations(telegramOutputService, googleHomeOutputService);

        Set<SonarrWebhookSchema> schemas = sonarrService.getNotYetReported();
        assertEquals(schemas.size(), 1);
        assertTrue(schemas.contains(schema));

        // mock google home output
        when(httpUtil.getDocument(anyString())).thenReturn(Optional.empty());
        doNothing().when(googleHomeOutputService).broadcast(anyString());

        // when reporting saved messages
        summaryReportBusinessService.reportSummaryToGoogleHome();

        // then google home message is send
        verify(telegramOutputService, times(0)).sendMessage(anyString());
        verify(googleHomeOutputService, times(1)).broadcast(anyString());
        clearInvocations(telegramOutputService, googleHomeOutputService);

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

        // When user is not listening and radarr message is received
        settingsService.getOpenHabSettings().setListening(false);
        radarrController.addRadarr(schema);

        // Then telegram is send but not google home
        validateMockitoUsage();
        verify(telegramOutputService, times(1)).sendMessage(anyString());
        verify(googleHomeOutputService, times(0)).broadcast(anyString());
        clearInvocations(telegramOutputService, googleHomeOutputService);

        Set<RadarrWebhookSchema> schemas = radarrService.getNotYetReported();
        assertEquals(schemas.size(), 1);
        assertTrue(schemas.contains(schema));

        // mock google home output
        when(httpUtil.getDocument(anyString())).thenReturn(Optional.empty());
        doNothing().when(googleHomeOutputService).broadcast(anyString());

        // when reporting saved messages
        summaryReportBusinessService.reportSummaryToGoogleHome();

        // then google home message is send
        verify(telegramOutputService, times(0)).sendMessage(anyString());
        verify(googleHomeOutputService, times(1)).broadcast(anyString());
        clearInvocations(telegramOutputService, googleHomeOutputService);

        schemas = radarrService.getNotYetReported();
        assertEquals(schemas.size(), 0);
    }
}
