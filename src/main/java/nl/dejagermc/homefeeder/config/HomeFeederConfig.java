package nl.dejagermc.homefeeder.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("nl.dejagermc.homefeeder")
@EnableCaching
public class HomeFeederConfig {
}
