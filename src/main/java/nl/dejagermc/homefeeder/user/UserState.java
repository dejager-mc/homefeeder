package nl.dejagermc.homefeeder.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;

import java.util.List;

@Accessors(fluent = true)
@Getter
@Setter
@Component
@NoArgsConstructor
public class UserState {
    // openhab
    private boolean isHome;

    // settings
    private boolean useGoogleHome;
    private boolean useTelegram;
    private List<String> dotaTeamsNotify;
}
