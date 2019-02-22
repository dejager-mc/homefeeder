package nl.dejagermc.homefeeder.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Accessors(fluent = true)
@Getter
@Setter
@Component
@NoArgsConstructor
public class UserState {
    // openhab
    private boolean isHome = true;
    private boolean isSleeping = false;
    private boolean isMute = false;

    // settings
    @Value("${homefeeder.use.googlehome}")
    private boolean useGoogleHome;
    @Value("${homefeeder.use.telegram}")
    private boolean useTelegram;
    @Value("${homefeeder.dota.favorite.teams}")
    private List<String> favoriteTeams;

    public boolean reportNow() {
        return !isSleeping && !isMute && isHome;
    }
}
