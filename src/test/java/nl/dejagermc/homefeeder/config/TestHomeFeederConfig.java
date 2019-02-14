package nl.dejagermc.homefeeder.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ComponentScan("nl.dejagermc.homefeeder")
@Profile("unit-test")
public class TestHomeFeederConfig {
}
