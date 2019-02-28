package nl.dejagermc.homefeeder;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.homefeeder.model.HomeFeederState;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

import java.util.Arrays;

@Slf4j
public class TestSetup {

    @Autowired
    private HomeFeederState homeFeederState;

    @Autowired
    private CacheManager cacheManager;

    @Before
    public void basicResetTestSetup() {
        log.info("Loading default test setup...");
        homeFeederState.useTelegram(true);
        homeFeederState.useGoogleHome(true);
        homeFeederState.favoriteTeams(Arrays.asList("OG"));
        homeFeederState.isHome(true);
        homeFeederState.isMute(false);
        homeFeederState.isSleeping(false);
        cacheManager.getCache("getAllMatches").clear();
        cacheManager.getCache("getAllPremierTournaments").clear();
        cacheManager.getCache("getAllMajorTournaments").clear();
        cacheManager.getCache("getAllQualifierTournaments").clear();
    }
}
