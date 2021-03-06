package nl.dejagermc.homefeeder.appconfig;

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
    private static final int HOURS_25 = 60 * 25;

    @Bean
    public CacheManager cacheManagerCaffeine(final Ticker ticker) {
        // daily caches
        CaffeineCache getAllPremierTournaments = buildCache("getAllPremierTournaments", ticker, HOURS_25, 1);
        CaffeineCache getAllMajorTournaments = buildCache("getAllMajorTournaments", ticker, HOURS_25, 1);
        CaffeineCache getAllQualifierTournaments = buildCache("getAllQualifierTournaments", ticker, HOURS_25, 1);
        CaffeineCache getCachedDocument = buildCache("getCachedDocument", ticker, HOURS_25, 200);
        CaffeineCache getAllBinPickups = buildCache("getAllBinPickups", ticker, HOURS_25, 1);

        // short caches
        CaffeineCache getAllMatches = buildCache("getAllMatches", ticker, 10, 1);
        CaffeineCache getAllDeliveries = buildCache("getAllDeliveries", ticker, 60, 1);

        // never expiring caches
        CaffeineCache getReportMethods = buildNotExpiringCache("getReportMethods", ticker, 1);
        CaffeineCache getAllOpenhabItems = buildNotExpiringCache("getAllOpenhabItems", ticker, 1);

        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(Arrays.asList(getAllMatches, getAllPremierTournaments, getAllMajorTournaments,
                getAllQualifierTournaments, getCachedDocument, getAllDeliveries, getReportMethods, getAllOpenhabItems, getAllBinPickups));

        return manager;
    }

    private CaffeineCache buildCache(final String name, final Ticker ticker, final int minutesToExpire, final int maximumSize) {
        return new CaffeineCache(name, Caffeine.newBuilder()
                .expireAfterWrite(minutesToExpire, TimeUnit.MINUTES)
                .maximumSize(maximumSize)
                .ticker(ticker)
                .expireAfterAccess(minutesToExpire, TimeUnit.MINUTES)
                .build());
    }

    private CaffeineCache buildNotExpiringCache(final String name, final Ticker ticker, final int maximumSize) {
        return new CaffeineCache(name, Caffeine.newBuilder()
                .expireAfterWrite(Integer.MAX_VALUE, TimeUnit.DAYS)
                .maximumSize(maximumSize)
                .ticker(ticker)
                .expireAfterAccess(Integer.MAX_VALUE, TimeUnit.DAYS)
                .build());
    }

    @Bean
    public Ticker ticker() {
        return Ticker.systemTicker();
    }
}
