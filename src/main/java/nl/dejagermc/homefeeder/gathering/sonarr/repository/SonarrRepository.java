package nl.dejagermc.homefeeder.gathering.sonarr.repository;

import nl.dejagermc.homefeeder.domain.generated.Episode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SonarrRepository {
    private List<Episode> episodes = new ArrayList<>();

    public void resetEpisodes() {
        episodes = new ArrayList<>();
    }

    public void addEpisodes(List<Episode> episodeList) {
        this.episodes.addAll(episodeList);
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }
}
