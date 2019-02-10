package nl.dejagermc.homefeeder.gathering.sonarr;

import nl.dejagermc.homefeeder.domain.generated.sonarr.SonarrWebhookSchema;
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

    public void delayReportForSeries(SonarrWebhookSchema episodes) {
        sonarrRepository.addEpisodes(episodes);
    }

    public List<SonarrWebhookSchema> getDelayedReportedSeries() {
        return sonarrRepository.getEpisodes();
    }

    public void resetDelayedReportedSeries() {
        sonarrRepository.resetEpisodes();
    }
}
