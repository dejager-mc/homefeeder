package nl.dejagermc.homefeeder.web;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.TestSetup;
import nl.dejagermc.homefeeder.business.reported.ReportedBusinessService;
import nl.dejagermc.homefeeder.domain.generated.radarr.RadarrWebhookSchema;
import nl.dejagermc.homefeeder.input.radarr.RadarrService;
import nl.dejagermc.homefeeder.output.google.home.GoogleHomeOutputService;
import nl.dejagermc.homefeeder.output.telegram.TelegramOutputService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Set;

import static nl.dejagermc.homefeeder.input.radarr.RadarrBuilder.getDefaultRadarrSchema;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Slf4j
public class RadarrControllerTest extends TestSetup {

    @Autowired
    private ReportedBusinessService reportedBusinessService;
    @Autowired
    private RadarrController radarrController;
    @Autowired
    private RadarrService radarrService;

    @MockBean
    private TelegramOutputService telegramOutputService;
    @MockBean
    private GoogleHomeOutputService googleHomeOutputService;

    @Captor
    private ArgumentCaptor<String> telegramCaptor;
    @Captor
    private ArgumentCaptor<String> googleHomeCaptor;

    @Before
    public void resetTestSetup() {
        log.info("Loading specific getAllDeliveries setup for {}...", this.getClass().getSimpleName());
        reportedBusinessService.resetAll();
    }

    @Test
    public void testLiveReportRadarrMessage() {
        RadarrWebhookSchema schema = getDefaultRadarrSchema();
        radarrController.addRadarr(schema);

        validateMockitoUsage();

        verify(telegramOutputService, times(1)).sendMessage(anyString());
        verify(googleHomeOutputService, times(1)).broadcast(anyString());
    }

    @Test
    public void testSaveRadarrMessageNotListening() {
        settingsService.getOpenHabSettings().setHomeMuted(true);
        RadarrWebhookSchema schema = getDefaultRadarrSchema();

        radarrController.addRadarr(schema);

        validateMockitoUsage();

        verify(telegramOutputService, times(1)).sendMessage(anyString());
        verify(googleHomeOutputService, times(0)).broadcast(anyString());

        Set<RadarrWebhookSchema> schemas = radarrService.getNotYetReported();
        assertEquals(schemas.size(), 1);
        assertTrue(schemas.contains(schema));
    }

    @Test
    public void testSaveRadarrMessageMuted() {
        settingsService.getOpenHabSettings().setHomeMuted(true);
        RadarrWebhookSchema schema = getDefaultRadarrSchema();

        radarrController.addRadarr(schema);

        validateMockitoUsage();

        verify(telegramOutputService, times(1)).sendMessage(anyString());
        verify(googleHomeOutputService, times(0)).broadcast(anyString());

        Set<RadarrWebhookSchema> schemas = radarrService.getNotYetReported();
        assertEquals(schemas.size(), 1);
        assertTrue(schemas.contains(schema));
    }
}
