package nl.dejagermc.homefeeder.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import nl.dejagermc.homefeeder.domain.generated.Episode;
import nl.dejagermc.homefeeder.domain.generated.Movie;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Tournament;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Accessors(fluent = true)
@Getter
@Setter
@Component
@NoArgsConstructor
public class UserState {
    // openhab
    private boolean isHome;
    private boolean isAwake;

    // sonarr
    private List<Episode> sonarrDownloads = new ArrayList<>();
    // radarr
    private List<Movie> radarrDownloads = new ArrayList<>();

    // settings
    private boolean useGoogleHome;
    private boolean useTelegram;
    private List<String> dotaTeamsNotify = new ArrayList<>();

    // dota
    private List<Tournament> tournaments = new ArrayList<>();
    private List<Match> matches = new ArrayList<>();
}
