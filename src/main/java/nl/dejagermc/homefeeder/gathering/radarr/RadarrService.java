package nl.dejagermc.homefeeder.gathering.radarr;

import nl.dejagermc.homefeeder.domain.generated.radarr.Movie;
import nl.dejagermc.homefeeder.domain.generated.radarr.RadarrWebhookSchema;
import nl.dejagermc.homefeeder.gathering.radarr.repository.RadarrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RadarrService {

    private RadarrRepository radarrRepository;

    @Autowired
    public RadarrService(RadarrRepository radarrRepository) {
        this.radarrRepository = radarrRepository;
    }

    public void delayReportForMovie(RadarrWebhookSchema movie) {
        radarrRepository.addMovie(movie);
    }

    public List<RadarrWebhookSchema> getDelayedReportedMovies() {
        return radarrRepository.getMovies();
    }

    public void resetDelayedReportedMovies() {
        radarrRepository.resetMovies();
    }
}
