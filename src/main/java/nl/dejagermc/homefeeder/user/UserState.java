package nl.dejagermc.homefeeder.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import nl.dejagermc.homefeeder.domain.generated.Episode;
import nl.dejagermc.homefeeder.domain.generated.Movie;
import nl.dejagermc.homefeeder.domain.generated.RadarrWebhookSchema;
import nl.dejagermc.homefeeder.domain.generated.SonarrWebhookSchema;
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

    // sonarr
    private List<Episode> sonarrDownloads;
    // radarr
    private List<Movie> radarrDownloads;

    // settings
    private boolean useGoogleHome;
    private boolean useTelegram;
    private List<String> dotaTeamsNotify;
}
