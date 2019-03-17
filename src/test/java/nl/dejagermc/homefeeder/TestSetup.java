package nl.dejagermc.homefeeder;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.appconfig.HomeFeederConfig;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethods;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = HomeFeederConfig.class)
@EnableConfigurationProperties
@Slf4j
public class TestSetup {

    @Autowired
    public SettingsService settingsService;

    @Autowired
    private CacheManager cacheManager;

    @Before
    public void basicResetTestSetup() {
        log.info("Loading default test setup...");

        settingsService.getDotaSettings().setFavoriteTeams(Arrays.asList("OG"));
        settingsService.getOpenHabSettings().setHome(true);
        settingsService.getOpenHabSettings().setMute(false);
        settingsService.getOpenHabSettings().setSleeping(false);

        cacheManager.getCacheNames().forEach(name -> cacheManager.getCache(name).clear());
    }

    @Test
    public void testSettings() {
        assertFalse(settingsService.surpressMessage());
        assertTrue(settingsService.userIsAvailable());
        assertThat(settingsService.getReportMethods(), containsInAnyOrder(ReportMethods.values()));
    }
}
