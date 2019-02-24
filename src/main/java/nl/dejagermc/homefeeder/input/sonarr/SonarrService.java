package nl.dejagermc.homefeeder.input.sonarr;

import nl.dejagermc.homefeeder.domain.generated.sonarr.SonarrWebhookSchema;
import nl.dejagermc.homefeeder.input.sonarr.repository.SonarrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SonarrService {
    private SonarrRepository sonarrRepository;

    @Autowired
    public SonarrService(SonarrRepository sonarrRepository) {
        this.sonarrRepository = sonarrRepository;
    }

    public void addNotYetReported(SonarrWebhookSchema episodes) {
        sonarrRepository.addEpisodes(episodes);
    }

    public Set<SonarrWebhookSchema> getNotYetReported() {
        return sonarrRepository.getEpisodes();
    }

    public void resetNotYetReported() {
        sonarrRepository.resetEpisodes();
    }
}
