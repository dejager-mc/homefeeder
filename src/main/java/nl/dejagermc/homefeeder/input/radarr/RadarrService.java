package nl.dejagermc.homefeeder.input.radarr;

import nl.dejagermc.homefeeder.domain.generated.radarr.RadarrWebhookSchema;
import nl.dejagermc.homefeeder.input.radarr.repository.RadarrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Service for movies downloaded by Radarr
 * Movies getAllOpenhabThings reported directly when downloaded
 * However if the user settings do not allow this
 * the movie will be stored by this service for
 * reporting at a different moment
 */
@Service
public class RadarrService {

    private RadarrRepository radarrRepository;

    @Autowired
    public RadarrService(RadarrRepository radarrRepository) {
        this.radarrRepository = radarrRepository;
    }

    public void addNotYetReported(RadarrWebhookSchema movie) {
        radarrRepository.addMovie(movie);
    }

    public Set<RadarrWebhookSchema> getNotYetReported() {
        return radarrRepository.getMovies();
    }

    public void resetNotYetReported() {
        radarrRepository.resetMovies();
    }
}
