package nl.dejagermc.homefeeder;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.user.UserState;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

import java.util.Arrays;

@Slf4j
public class TestSetup {

    @Autowired
    private UserState userState;

    @Autowired
    private CacheManager cacheManager;

    @Before
    public void basicResetTestSetup() {
        log.info("Loading default test setup...");
        userState.useTelegram(true);
        userState.useGoogleHome(true);
        userState.favoriteTeams(Arrays.asList("OG"));
        userState.isHome(true);
        userState.isMute(false);
        userState.isSleeping(false);
        cacheManager.getCache("getAllMatches").clear();
        cacheManager.getCache("getAllPremierTournaments").clear();
        cacheManager.getCache("getAllMajorTournaments").clear();
        cacheManager.getCache("getAllQualifierTournaments").clear();
    }
}
