package nl.dejagermc.homefeeder.input.homefeeder.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Accessors
@Getter
@Setter
@ToString
public class HomeFeederSettings {
    @Value("${homefeeder.use.googlehome}")
    private boolean useGoogleHome;
    @Value("${homefeeder.use.telegram}")
    private boolean useTelegram;
}