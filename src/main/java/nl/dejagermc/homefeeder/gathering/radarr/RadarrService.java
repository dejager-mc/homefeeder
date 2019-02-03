package nl.dejagermc.homefeeder.gathering.radarr;

import nl.dejagermc.homefeeder.domain.generated.Movie;
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

    public void addMovie(Movie movies) {
        radarrRepository.addMovie(movies);
    }

    public List<Movie> getMovies() {
        return radarrRepository.getMovies();
    }

    public void resetMovies() {
        radarrRepository.resetMovies();
    }
}
