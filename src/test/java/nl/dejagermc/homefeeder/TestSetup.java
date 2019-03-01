package nl.dejagermc.homefeeder;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.config.HomeFeederConfig;
import nl.dejagermc.homefeeder.input.homefeeder.SettingsService;
import nl.dejagermc.homefeeder.input.homefeeder.model.HomeFeederSettings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = HomeFeederConfig.class)
@EnableConfigurationProperties
@Slf4j
public class TestSetup {

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private CacheManager cacheManager;

    @Before
    public void basicResetTestSetup() {
        log.info("Loading default test setup...");

        settingsService.getHomeFeederSettings().setUseTelegram(true);
        settingsService.getHomeFeederSettings().setUseGoogleHome(true);
        settingsService.getDotaSettings().setFavoriteTeams(Arrays.asList("OG"));
        settingsService.getOpenHabSettings().setHome(true);
        settingsService.getOpenHabSettings().setMute(false);
        settingsService.getOpenHabSettings().setSleeping(false);

        cacheManager.getCache("getAllMatches").clear();
        cacheManager.getCache("getAllPremierTournaments").clear();
        cacheManager.getCache("getAllMajorTournaments").clear();
        cacheManager.getCache("getAllQualifierTournaments").clear();
    }

    @Test
    public void testStartup() {
        assertEquals(settingsService.isUserAbleToGetReport(), true);
    }
}
