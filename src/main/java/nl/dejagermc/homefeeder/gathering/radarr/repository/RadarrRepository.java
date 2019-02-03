package nl.dejagermc.homefeeder.gathering.radarr.repository;

import nl.dejagermc.homefeeder.domain.generated.Movie;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RadarrRepository {
    private List<Movie> movies = new ArrayList<>();

    public void resetMovies() {
        movies = new ArrayList<>();
    }

    public void addMovie(Movie movieList) {
        this.movies.add(movieList);
    }

    public List<Movie> getMovies() {
        return movies;
    }
}
