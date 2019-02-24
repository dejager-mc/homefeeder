package nl.dejagermc.homefeeder.input.sonarr.repository;

import nl.dejagermc.homefeeder.domain.generated.sonarr.SonarrWebhookSchema;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class SonarrRepository {
    private Set<SonarrWebhookSchema> episodes = new HashSet<>();

    public void resetEpisodes() {
        episodes = new HashSet<>();
    }

    public void addEpisodes(SonarrWebhookSchema episodeList) {
        this.episodes.add(episodeList);
    }

    public Set<SonarrWebhookSchema> getEpisodes() {
        return episodes;
    }
}
