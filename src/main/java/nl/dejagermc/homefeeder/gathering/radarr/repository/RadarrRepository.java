package nl.dejagermc.homefeeder.gathering.radarr.repository;

import nl.dejagermc.homefeeder.domain.generated.radarr.RadarrWebhookSchema;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RadarrRepository {
    private List<RadarrWebhookSchema> movies = new ArrayList<>();

    public void resetMovies() {
        movies = new ArrayList<>();
    }

    public void addMovie(RadarrWebhookSchema movie) {
        this.movies.add(movie);
    }

    public List<RadarrWebhookSchema> getMovies() {
        return movies;
    }
}
