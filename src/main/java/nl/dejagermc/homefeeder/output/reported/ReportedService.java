package nl.dejagermc.homefeeder.output.reported;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.domain.generated.radarr.Movie;
import nl.dejagermc.homefeeder.domain.generated.sonarr.Episode;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Match;
import nl.dejagermc.homefeeder.gathering.liquipedia.dota.model.Tournament;
import nl.dejagermc.homefeeder.output.reported.model.ReportedTo;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ReportedService {
    private List<Pair<Match, ReportedTo>> matches = new ArrayList<>();
    private List<Pair<Tournament, ReportedTo>> tournaments = new ArrayList<>();

    private List<Pair<Episode, ReportedTo>> episodes = new ArrayList<>();
    private List<Pair<Movie, ReportedTo>> movies = new ArrayList<>();

    public boolean hasThisBeenReported(Object object, ReportedTo reportedTo) {
        log.info("hasThisBeenReported: checking");
        if (object instanceof Match) {
            log.info("Match to be reported: {}", object);
            log.info("All matches that have been reported: ");
            matches.stream().forEach(m -> log.info(m.toString()));
            return matches.stream().anyMatch(m -> m.getFirst().equals(object) && m.getSecond().equals(reportedTo));
        }
        else if (object instanceof Tournament) {
            return tournaments.stream().anyMatch(m -> m.getFirst().equals(object) && m.getSecond().equals(reportedTo));
        }
        else if (object instanceof Episode) {
            return episodes.stream().anyMatch(m -> m.getFirst().equals(object) && m.getSecond().equals(reportedTo));
        }
        else if (object instanceof Movie) {
            return movies.stream().anyMatch(m -> m.getFirst().equals(object) && m.getSecond().equals(reportedTo));
        }
        log.error("hasThisBeenReported error: geen match kunnen vinden voor report to {} voor {}", reportedTo, object);
        return false;
    }

    public void reportThisToThat(Object object, ReportedTo reportedTo) {
        if (object instanceof Match) {
            matches.add(Pair.of((Match)object, reportedTo));
        } else if (object instanceof Tournament) {
            tournaments.add(Pair.of((Tournament)object, reportedTo));
        }
        else if (object instanceof Episode) {
            episodes.add(Pair.of((Episode)object, reportedTo));
        }
        else if (object instanceof Movie) {
            movies.add(Pair.of((Movie)object, reportedTo));
        }
    }
}
