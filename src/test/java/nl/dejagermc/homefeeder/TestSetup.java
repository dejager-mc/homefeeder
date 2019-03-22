package nl.dejagermc.homefeeder;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.appconfig.HomeFeederConfig;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.input.homefeeder.enums.ReportMethod;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;

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
        log.info("Loading default getAllDeliveries setup...");

        settingsService.getDotaSettings().setFavoriteTeams(List.of("OG"));
        settingsService.getOpenHabSettings().setHomeMuted(false);

        cacheManager.getCacheNames().forEach(name -> cacheManager.getCache(name).clear());
    }

    @Test
    public void testSettings() {
        assertFalse(settingsService.isHomeMuted());
        assertThat(settingsService.getReportMethods(), containsInAnyOrder(ReportMethod.values()));
    }
}
