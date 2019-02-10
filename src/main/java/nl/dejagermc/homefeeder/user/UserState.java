package nl.dejagermc.homefeeder.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;

import java.util.Arrays;
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
    private boolean useGoogleHome = true;
    private boolean useTelegram = true;
    private List<String> favoriteTeams = Arrays.asList("OG", "Secret", "VP");

    public boolean reportNow() {
        if (isSleeping || isMute || !isHome) {
            return false;
        }
        return true;
    }
}
