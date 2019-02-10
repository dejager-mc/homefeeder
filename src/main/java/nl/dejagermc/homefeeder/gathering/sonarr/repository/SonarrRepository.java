package nl.dejagermc.homefeeder.gathering.sonarr.repository;

import nl.dejagermc.homefeeder.domain.generated.sonarr.SonarrWebhookSchema;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SonarrRepository {
    private List<SonarrWebhookSchema> episodes = new ArrayList<>();

    public void resetEpisodes() {
        episodes = new ArrayList<>();
    }

    public void addEpisodes(SonarrWebhookSchema episodeList) {
        this.episodes.add(episodeList);
    }

    public List<SonarrWebhookSchema> getEpisodes() {
        return episodes;
    }
}
