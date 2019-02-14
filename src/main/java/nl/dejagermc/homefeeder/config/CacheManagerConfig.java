package nl.dejagermc.homefeeder.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheManagerConfig {

    @Bean
    public CacheManager cacheManagerCaffeine(Ticker ticker) {
        CaffeineCache getAllMatches = buildCache("getAllMatches", ticker, 15, 1);
        CaffeineCache getAllPremierTournaments = buildCache("getAllPremierTournaments", ticker, 60*24, 1);
        CaffeineCache getAllMajorTournaments = buildCache("getAllMajorTournaments", ticker, 60*24, 1);
        CaffeineCache getAllQualifierTournaments = buildCache("getAllQualifierTournaments", ticker, 60*24, 1);
        CaffeineCache getFullTournamentName = buildCache("getFullTournamentName", ticker, 60*24, 100);

        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(Arrays.asList(getAllMatches, getAllPremierTournaments, getAllMajorTournaments, getAllQualifierTournaments, getFullTournamentName));
        return manager;
    }

    private CaffeineCache buildCache(String name, Ticker ticker, int minutesToExpire, int maximumSize) {
        return new CaffeineCache(name, Caffeine.newBuilder()
                .expireAfterWrite(minutesToExpire, TimeUnit.MINUTES)
                .maximumSize(maximumSize)
                .ticker(ticker)
                .expireAfterAccess(minutesToExpire, TimeUnit.MINUTES)
                .build());
    }

    @Bean
    public Ticker ticker() {
        return Ticker.systemTicker();
    }
}
