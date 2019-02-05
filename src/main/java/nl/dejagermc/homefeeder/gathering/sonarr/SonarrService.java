package nl.dejagermc.homefeeder.gathering.sonarr;

import nl.dejagermc.homefeeder.domain.generated.sonarr.Episode;
import nl.dejagermc.homefeeder.gathering.sonarr.repository.SonarrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SonarrService {
    private SonarrRepository sonarrRepository;

    @Autowired
    public SonarrService(SonarrRepository sonarrRepository) {
        this.sonarrRepository = sonarrRepository;
    }

    public void addEpisodes(List<Episode> episodes) {
        sonarrRepository.addEpisodes(episodes);
    }

    public List<Episode> getEpisodes() {
        return sonarrRepository.getEpisodes();
    }

    public void resetEpisodes() {
        sonarrRepository.resetEpisodes();
    }
}
