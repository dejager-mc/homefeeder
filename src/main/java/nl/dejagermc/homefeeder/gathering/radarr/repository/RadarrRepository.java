package nl.dejagermc.homefeeder.gathering.radarr.repository;

import nl.dejagermc.homefeeder.domain.generated.radarr.RadarrWebhookSchema;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;


@Component
public class RadarrRepository {
    private Set<RadarrWebhookSchema> movies = new HashSet<>();

    public void resetMovies() {
        movies = new HashSet<>();
    }

    public void addMovie(RadarrWebhookSchema movie) {
        this.movies.add(movie);
    }

    public Set<RadarrWebhookSchema> getMovies() {
        return movies;
    }
}
