package nl.dejagermc.homefeeder.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan("nl.dejagermc.homefeeder")
@EnableCaching
@EnableScheduling
public class HomeFeederConfig {
}
